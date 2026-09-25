import { sendData } from "./fcm";
import {
  HttpError, coord, distanceKm, int, inviteCode, json, now, oneOf, pairCode, secretToken, sha256, str, uuid,
} from "./util";

export interface Env {
  DB: D1Database;
  FCM_SERVICE_ACCOUNT?: string;
}

// Keep in sync with Catalog.statuses in the app.
const STATUS_KEYS = [
  "toilet", "sleeping", "eating", "gym", "studying", "work", "driving", "gaming", "partying", "showering", "coffee",
  "sick", "walking", "dnd", "bored", "free", "custom", "period", "spicy", "spicy2", "spicy3", "inlove", "heartbroken",
  "hungover", "angry", "crying", "date", "shopping", "movie", "traveling", "cooking", "meditating", "hookah",
  "cleaning", "traffic", "lowbattery", "overthinking", "maincharacter", "busy", "period2", "period3", "football",
  "barber", "beard",
] as const;
const SENSITIVE = new Set(["period", "period2", "period3", "spicy", "spicy2", "spicy3"]);
const ACCS = ["none", "cap", "bow", "crown"] as const;
const SHARE_ORDER = ["status", "approx", "exact"] as const;
const CODE_TTLS: Record<string, number | null> = { "24h": 86_400_000, "7d": 7 * 86_400_000, "30d": 30 * 86_400_000, never: null };
const DAY = 86_400_000;

interface UserRow {
  id: string; nick: string; avatar: number; look: string | null; pair_code: string; fcm_token: string | null;
  ghost: number; paused_until: number | null; precision: string; lat: number | null; lng: number | null;
}
interface GroupRow {
  id: string; name: string; icon: string; color: number; kind: string; invite_code: string | null;
  code_expires_at: number | null; code_ttl: string; created_at: number;
}
interface MemberRow { group_id: string; user_id: string; role: string; share: string; muted: number; joined_at: number }
interface StatusRow {
  id: number; user_id: string; key: string; text: string; hue: number; acc: string; sensitive: number;
  visibility: string; vis_groups: string | null; lat: number | null; lng: number | null; created_at: number; expires_at: number | null;
}

type Ctx = { env: Env; exec: ExecutionContext; me: UserRow; body: any; url: URL };

export default {
  async fetch(req: Request, env: Env, exec: ExecutionContext): Promise<Response> {
    try {
      return await route(req, env, exec);
    } catch (e) {
      if (e instanceof HttpError) return json({ error: e.code }, e.status);
      console.error(e);
      return json({ error: "server" }, 500);
    }
  },
};

async function route(req: Request, env: Env, exec: ExecutionContext): Promise<Response> {
  const url = new URL(req.url);
  const p = url.pathname.replace(/\/+$/, "").split("/").filter(Boolean);
  const m = req.method;
  if (p[0] !== "v1") return p.length === 0 ? json({ ok: true, name: "fsl-api" }) : json({ error: "not_found" }, 404);
  const body = m === "GET" || m === "DELETE" ? {} : await req.json().catch(() => { throw new HttpError(400, "bad_json"); });

  if (m === "POST" && p[1] === "register" && p.length === 2) return register(env, body);

  const me = await auth(env, req);
  const c: Ctx = { env, exec, me, body, url };
  const key = `${m} /${p.slice(1).map((s, i) => (i > 0 && p[1] === "groups" && (i === 1 || i === 3) ? ":" : s)).join("/")}`;
  switch (key) {
    case "GET /feed": return feed(c);
    case "PATCH /me": return patchMe(c);
    case "DELETE /me": return deleteMe(c);
    case "DELETE /history": return clearHistory(c);
    case "POST /status": return postStatus(c);
    case "POST /react": return react(c);
    case "POST /join": return join(c);
    case "POST /groups": return createGroup(c);
    case "PATCH /groups/:": return patchGroup(c, p[2]);
    case "PATCH /groups/:/me": return patchMembership(c, p[2]);
    case "POST /groups/:/code": return resetCode(c, p[2]);
    case "POST /groups/:/leave": return leave(c, p[2]);
    case "POST /groups/:/members/:/role": return setRole(c, p[2], p[4]);
    case "DELETE /groups/:/members/:": return removeMember(c, p[2], p[4]);
  }
  if (m === "GET" && p[1] === "invite" && p.length === 3) return invitePreview(c, p[2]);
  throw new HttpError(404, "not_found");
}

// ------------------------------------------------------------------ accounts

async function register(env: Env, body: any): Promise<Response> {
  const nick = str(body.nick, 14, "nick");
  const avatar = int(body.avatar ?? 0, 0, 5, "avatar");
  const look = body.look == null ? null : str(body.look, 60, "look");
  const token = secretToken();
  const id = uuid();
  for (let i = 0; ; i++) {
    try {
      await env.DB.prepare(
        "INSERT INTO users (id, token_hash, nick, avatar, look, pair_code, created_at) VALUES (?, ?, ?, ?, ?, ?, ?)",
      ).bind(id, await sha256(token), nick, avatar, look, pairCode(), now()).run();
      break;
    } catch (e) {
      if (i > 3) throw e; // pair code collision, try another
    }
  }
  const u = await env.DB.prepare("SELECT pair_code FROM users WHERE id = ?").bind(id).first<{ pair_code: string }>();
  return json({ id, token, pairCode: u!.pair_code });
}

async function auth(env: Env, req: Request): Promise<UserRow> {
  const h = req.headers.get("authorization") ?? "";
  const token = h.startsWith("Bearer ") ? h.slice(7) : "";
  if (!token) throw new HttpError(401, "unauthorized");
  const u = await env.DB.prepare("SELECT * FROM users WHERE token_hash = ?").bind(await sha256(token)).first<UserRow>();
  if (!u) throw new HttpError(401, "unauthorized");
  return u;
}

async function patchMe(c: Ctx): Promise<Response> {
  const b = c.body;
  const sets: string[] = [];
  const vals: unknown[] = [];
  const set = (col: string, v: unknown) => { sets.push(`${col} = ?`); vals.push(v); };
  if ("nick" in b) set("nick", str(b.nick, 14, "nick"));
  if ("avatar" in b) set("avatar", int(b.avatar, 0, 5, "avatar"));
  if ("look" in b) set("look", b.look == null ? null : str(b.look, 60, "look"));
  if ("fcmToken" in b) set("fcm_token", b.fcmToken == null ? null : str(b.fcmToken, 4096, "fcmToken"));
  if ("ghost" in b) set("ghost", b.ghost ? 1 : 0);
  if ("pausedUntil" in b) set("paused_until", b.pausedUntil == null ? null : int(b.pausedUntil, 0, Number.MAX_SAFE_INTEGER, "pausedUntil"));
  if ("precision" in b) set("precision", oneOf(b.precision, ["exact", "approx", "off"] as const, "precision"));
  if ("lat" in b || "lng" in b) {
    set("lat", b.lat == null ? null : coord(b.lat, 90, "lat"));
    set("lng", b.lng == null ? null : coord(b.lng, 180, "lng"));
  }
  if (sets.length) await c.env.DB.prepare(`UPDATE users SET ${sets.join(", ")} WHERE id = ?`).bind(...vals, c.me.id).run();
  // Ghost mode and pauses change what friends see, so wake their widgets.
  if ("ghost" in b || "pausedUntil" in b) notifyFriends(c, "refresh");
  return json({ ok: true });
}

async function deleteMe(c: Ctx): Promise<Response> {
  const db = c.env.DB;
  const groups = await myGroupIds(db, c.me.id);
  await db.batch([
    db.prepare("DELETE FROM reactions WHERE from_id = ?1 OR to_id = ?1").bind(c.me.id),
    db.prepare("DELETE FROM statuses WHERE user_id = ?").bind(c.me.id),
    db.prepare("DELETE FROM members WHERE user_id = ?").bind(c.me.id),
    db.prepare("DELETE FROM users WHERE id = ?").bind(c.me.id),
  ]);
  for (const g of groups) await cleanupGroup(db, g);
  return json({ ok: true });
}

async function clearHistory(c: Ctx): Promise<Response> {
  await c.env.DB.prepare(
    "DELETE FROM statuses WHERE user_id = ?1 AND id <> COALESCE((SELECT id FROM statuses WHERE user_id = ?1 ORDER BY created_at DESC LIMIT 1), -1)",
  ).bind(c.me.id).run();
  return json({ ok: true });
}

// ------------------------------------------------------------------ statuses

async function postStatus(c: Ctx): Promise<Response> {
  const b = c.body;
  const key = oneOf(b.key, STATUS_KEYS, "key");
  const text = str(b.text, 32, "text");
  const hue = int(b.hue ?? 0, 0, 359, "hue");
  const acc = oneOf(b.acc ?? "none", ACCS, "acc");
  const sensitive = SENSITIVE.has(key);
  const visibility = sensitive ? oneOf(b.visibility ?? "one", ["all", "groups", "one"] as const, "visibility") : "all";
  let visGroups: string | null = null;
  if (visibility === "groups") {
    const mine = new Set(await myGroupIds(c.env.DB, c.me.id));
    const list = Array.isArray(b.visGroups) ? b.visGroups.filter((g: unknown) => typeof g === "string" && mine.has(g)) : [];
    visGroups = list.join(",");
  }
  const expiresIn = b.expiresInMin == null ? null : int(b.expiresInMin, 1, 1440, "expiresInMin");
  const lat = b.lat == null ? null : coord(b.lat, 90, "lat");
  const lng = b.lng == null ? null : coord(b.lng, 180, "lng");
  const t = now();
  await c.env.DB.batch([
    c.env.DB.prepare(
      "INSERT INTO statuses (user_id, key, text, hue, acc, sensitive, visibility, vis_groups, lat, lng, created_at, expires_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
    ).bind(c.me.id, key, text, hue, acc, sensitive ? 1 : 0, visibility, visGroups, lat, lng, t, expiresIn ? t + expiresIn * 60_000 : null),
    ...(lat != null && lng != null ? [c.env.DB.prepare("UPDATE users SET lat = ?, lng = ? WHERE id = ?").bind(lat, lng, c.me.id)] : []),
    // Keep a week of history; older statuses are not shown anywhere.
    c.env.DB.prepare("DELETE FROM statuses WHERE user_id = ? AND created_at < ?").bind(c.me.id, t - 7 * DAY),
  ]);
  notifyFriends(c, "refresh");
  return json({ ok: true, at: t });
}

async function react(c: Ctx): Promise<Response> {
  const to = str(c.body.to, 64, "to");
  const kind = oneOf(c.body.kind, ["poke", "laugh", "out"] as const, "kind");
  const shared = await c.env.DB.prepare(
    "SELECT 1 FROM members a JOIN members b ON a.group_id = b.group_id WHERE a.user_id = ? AND b.user_id = ? LIMIT 1",
  ).bind(c.me.id, to).first();
  if (!shared) throw new HttpError(403, "not_friends");
  await c.env.DB.prepare("INSERT INTO reactions (from_id, to_id, kind, created_at) VALUES (?, ?, ?, ?)").bind(c.me.id, to, kind, now()).run();
  const target = await c.env.DB.prepare("SELECT fcm_token FROM users WHERE id = ?").bind(to).first<{ fcm_token: string | null }>();
  if (target?.fcm_token) c.exec.waitUntil(push(c.env, [target.fcm_token], { type: "react", from: c.me.nick, kind }));
  return json({ ok: true });
}

/** Wakes every friend's phone (silent data message) so their widget refreshes. */
function notifyFriends(c: Ctx, type: string) {
  c.exec.waitUntil((async () => {
    const rows = await c.env.DB.prepare(
      `SELECT DISTINCT u.fcm_token AS t FROM members a JOIN members b ON a.group_id = b.group_id
       JOIN users u ON u.id = b.user_id WHERE a.user_id = ? AND b.user_id <> ? AND u.fcm_token IS NOT NULL`,
    ).bind(c.me.id, c.me.id).all<{ t: string }>();
    await push(c.env, rows.results.map((r) => r.t), { type });
  })());
}

async function push(env: Env, tokens: string[], data: Record<string, string>) {
  try {
    const gone = await sendData(env.FCM_SERVICE_ACCOUNT, tokens, data);
    for (const t of gone) await env.DB.prepare("UPDATE users SET fcm_token = NULL WHERE fcm_token = ?").bind(t).run();
  } catch (e) {
    console.error("push failed", e);
  }
}

// ------------------------------------------------------------------ feed

async function feed(c: Ctx): Promise<Response> {
  const db = c.env.DB;
  const me = c.me;
  const t = now();
  const since = Number(c.url.searchParams.get("since") ?? 0) || 0;

  const groups = (await db.prepare(
    "SELECT g.*, m.role, m.share, m.muted FROM members m JOIN groups g ON g.id = m.group_id WHERE m.user_id = ? ORDER BY g.created_at",
  ).bind(me.id).all<GroupRow & { role: string; share: string; muted: number }>()).results;
  const groupIds = groups.map((g) => g.id);
  const kindOf = new Map(groups.map((g) => [g.id, g.kind]));

  const members = groupIds.length
    ? (await inChunks(groupIds, (ph, ids) => db.prepare(
      `SELECT m.*, u.nick, u.avatar, u.look FROM members m JOIN users u ON u.id = m.user_id WHERE m.group_id IN (${ph}) ORDER BY m.joined_at`,
    ).bind(...ids).all<MemberRow & { nick: string; avatar: number; look: string | null }>()))
    : [];

  // Everyone who shares at least one group with me.
  const friendIds = [...new Set(members.filter((m) => m.user_id !== me.id).map((m) => m.user_id))];
  const friends = friendIds.length
    ? await inChunks(friendIds, (ph, ids) => db.prepare(`SELECT * FROM users WHERE id IN (${ph})`).bind(...ids).all<UserRow>())
    : [];
  const current = friendIds.length ? await currentStatuses(db, friendIds, t) : new Map<string, StatusRow>();
  const recent = friendIds.length
    ? await inChunks(friendIds, (ph, ids) => db.prepare(
      `SELECT * FROM statuses WHERE user_id IN (${ph}) AND created_at > ? ORDER BY created_at DESC`,
    ).bind(...ids, t - DAY).all<StatusRow>())
    : [];

  const out = friends.map((f) => {
    const shared = members.filter((m) => m.user_id === f.id).map((m) => m.group_id);
    const theirShare = members.filter((m) => m.user_id === f.id).reduce((best, m) => Math.max(best, SHARE_ORDER.indexOf(m.share as any)), 0);
    const hidden = !!f.ghost || (f.paused_until != null && f.paused_until > t);
    const canSee = (s: StatusRow) => {
      if (!s.sensitive || s.visibility === "all") return true;
      if (s.visibility === "groups") return (s.vis_groups ?? "").split(",").some((g) => shared.includes(g));
      return shared.some((g) => kindOf.get(g) === "pair");
    };
    const view = (s: StatusRow) => canSee(s)
      ? { key: s.key, text: s.text, hue: s.hue, acc: s.acc, at: s.created_at, expiresAt: s.expires_at }
      : { key: "busy", text: "سرم شلوغه", hue: 0, acc: "none", at: s.created_at, expiresAt: s.expires_at };
    const cur = current.get(f.id);
    let status = cur ? view(cur) : null;
    if (hidden) status = { key: "dnd", text: "غیب شده", hue: 0, acc: "none", at: cur?.created_at ?? t, expiresAt: null };

    // Distance only when they share location with a group I'm in and both of us have a position.
    let distance: number | null = null;
    const level = SHARE_ORDER[theirShare];
    const fLat = cur?.lat ?? f.lat;
    const fLng = cur?.lng ?? f.lng;
    if (!hidden && f.precision !== "off" && level !== "status" && fLat != null && fLng != null && me.lat != null && me.lng != null) {
      const d = distanceKm(me.lat, me.lng, fLat, fLng);
      distance = level === "exact" && f.precision === "exact" ? Math.round(d * 10) / 10 : Math.max(0.5, Math.round(d * 2) / 2);
    }
    const history = hidden ? [] : recent
      .filter((s) => s.user_id === f.id && s.id !== cur?.id && canSee(s))
      .slice(0, 8)
      .map((s) => ({ key: s.key, text: s.text, at: s.created_at }));
    return { id: f.id, nick: f.nick, avatar: f.avatar, look: f.look, groups: shared, status, distanceKm: distance, history };
  });

  const byGroup = (gid: string) => members.filter((m) => m.group_id === gid);
  const myStatus = (await currentStatuses(db, [me.id], t)).get(me.id);
  const reactions = since
    ? (await db.prepare(
      "SELECT r.kind, r.created_at AS at, u.nick FROM reactions r JOIN users u ON u.id = r.from_id WHERE r.to_id = ? AND r.created_at > ? ORDER BY r.created_at",
    ).bind(me.id, since).all<{ kind: string; at: number; nick: string }>()).results
    : [];

  return json({
    serverTime: t,
    me: {
      id: me.id, nick: me.nick, avatar: me.avatar, look: me.look, pairCode: me.pair_code,
      ghost: !!me.ghost, pausedUntil: me.paused_until, precision: me.precision,
      status: myStatus ? { key: myStatus.key, text: myStatus.text, hue: myStatus.hue, acc: myStatus.acc, at: myStatus.created_at, expiresAt: myStatus.expires_at } : null,
    },
    groups: groups.map((g) => {
      const ms = byGroup(g.id);
      const other = g.kind === "pair" ? ms.find((m) => m.user_id !== me.id) : undefined;
      const admin = g.role === "admin";
      return {
        id: g.id, kind: g.kind, name: other ? other.nick : g.name, icon: g.icon, color: g.color, createdAt: g.created_at,
        role: g.role, share: g.share, muted: !!g.muted,
        code: admin && g.kind === "group" ? g.invite_code : null,
        codeTtl: g.code_ttl, codeExpiresAt: admin ? g.code_expires_at : null,
        members: ms.map((m) => ({ id: m.user_id, nick: m.nick, avatar: m.avatar, role: m.role, joinedAt: m.joined_at })),
      };
    }),
    friends: out,
    reactions,
  });
}

async function currentStatuses(db: D1Database, ids: string[], t: number): Promise<Map<string, StatusRow>> {
  const rows = await inChunks(ids, (ph, chunk) => db.prepare(
    `SELECT s.* FROM statuses s WHERE s.user_id IN (${ph}) AND s.id = (
       SELECT s2.id FROM statuses s2 WHERE s2.user_id = s.user_id AND (s2.expires_at IS NULL OR s2.expires_at > ?)
       ORDER BY s2.created_at DESC LIMIT 1)`,
  ).bind(...chunk, t).all<StatusRow>());
  return new Map(rows.map((r) => [r.user_id, r]));
}

/** D1 limits bound parameters, so large IN lists are split. */
async function inChunks<T>(ids: string[], q: (placeholders: string, chunk: string[]) => Promise<D1Result<T>>): Promise<T[]> {
  const out: T[] = [];
  for (let i = 0; i < ids.length; i += 80) {
    const chunk = ids.slice(i, i + 80);
    out.push(...(await q(chunk.map(() => "?").join(","), chunk)).results);
  }
  return out;
}

async function myGroupIds(db: D1Database, userId: string): Promise<string[]> {
  return (await db.prepare("SELECT group_id FROM members WHERE user_id = ?").bind(userId).all<{ group_id: string }>()).results.map((r) => r.group_id);
}

// ------------------------------------------------------------------ groups

async function createGroup(c: Ctx): Promise<Response> {
  const name = str(c.body.name, 30, "name");
  const icon = oneOf(c.body.icon ?? "home", ["home", "school", "fitness", "family", "star", "work"] as const, "icon");
  const color = int(c.body.color ?? 0, 0, 4, "color");
  const id = uuid();
  const t = now();
  const code = await freeInviteCode(c.env.DB);
  await c.env.DB.batch([
    c.env.DB.prepare("INSERT INTO groups (id, name, icon, color, kind, invite_code, code_expires_at, code_ttl, created_at) VALUES (?, ?, ?, ?, 'group', ?, ?, '7d', ?)")
      .bind(id, name, icon, color, code, t + CODE_TTLS["7d"]!, t),
    c.env.DB.prepare("INSERT INTO members (group_id, user_id, role, joined_at) VALUES (?, ?, 'admin', ?)").bind(id, c.me.id, t),
  ]);
  return json({ id, code });
}

async function freeInviteCode(db: D1Database): Promise<string> {
  for (let i = 0; i < 10; i++) {
    const code = inviteCode();
    const taken = await db.prepare("SELECT 1 FROM groups WHERE invite_code = ?").bind(code).first();
    if (!taken) return code;
  }
  throw new HttpError(503, "try_again");
}

async function requireMember(c: Ctx, groupId: string, admin = false): Promise<GroupRow & { role: string }> {
  const g = await c.env.DB.prepare("SELECT g.*, m.role FROM groups g JOIN members m ON m.group_id = g.id WHERE g.id = ? AND m.user_id = ?")
    .bind(groupId, c.me.id).first<GroupRow & { role: string }>();
  if (!g) throw new HttpError(404, "no_group");
  if (admin && g.role !== "admin") throw new HttpError(403, "not_admin");
  return g;
}

async function patchGroup(c: Ctx, id: string): Promise<Response> {
  const g = await requireMember(c, id, true);
  const b = c.body;
  const sets: string[] = [];
  const vals: unknown[] = [];
  if ("name" in b) { sets.push("name = ?"); vals.push(str(b.name, 30, "name")); }
  if ("icon" in b) { sets.push("icon = ?"); vals.push(oneOf(b.icon, ["home", "school", "fitness", "family", "star", "work"] as const, "icon")); }
  if ("color" in b) { sets.push("color = ?"); vals.push(int(b.color, 0, 4, "color")); }
  if ("codeTtl" in b && g.kind === "group") {
    const ttl = oneOf(b.codeTtl, Object.keys(CODE_TTLS), "codeTtl");
    const ms = CODE_TTLS[ttl];
    sets.push("code_ttl = ?", "code_expires_at = ?");
    vals.push(ttl, ms == null ? null : now() + ms);
  }
  if (sets.length) await c.env.DB.prepare(`UPDATE groups SET ${sets.join(", ")} WHERE id = ?`).bind(...vals, id).run();
  return json({ ok: true });
}

async function patchMembership(c: Ctx, id: string): Promise<Response> {
  await requireMember(c, id);
  const b = c.body;
  if ("muted" in b) await c.env.DB.prepare("UPDATE members SET muted = ? WHERE group_id = ? AND user_id = ?").bind(b.muted ? 1 : 0, id, c.me.id).run();
  if ("share" in b) {
    const share = oneOf(b.share, SHARE_ORDER, "share");
    await c.env.DB.prepare("UPDATE members SET share = ? WHERE group_id = ? AND user_id = ?").bind(share, id, c.me.id).run();
  }
  return json({ ok: true });
}

async function resetCode(c: Ctx, id: string): Promise<Response> {
  const g = await requireMember(c, id, true);
  if (g.kind !== "group") throw new HttpError(400, "pair_has_no_code");
  const code = await freeInviteCode(c.env.DB);
  const ms = CODE_TTLS[g.code_ttl] ?? null;
  await c.env.DB.prepare("UPDATE groups SET invite_code = ?, code_expires_at = ? WHERE id = ?").bind(code, ms == null ? null : now() + ms, id).run();
  return json({ code });
}

async function leave(c: Ctx, id: string): Promise<Response> {
  await requireMember(c, id);
  await c.env.DB.prepare("DELETE FROM members WHERE group_id = ? AND user_id = ?").bind(id, c.me.id).run();
  await cleanupGroup(c.env.DB, id);
  return json({ ok: true });
}

/** Deletes empty groups and makes sure a group always keeps an admin. */
async function cleanupGroup(db: D1Database, id: string) {
  const left = (await db.prepare("SELECT user_id, role FROM members WHERE group_id = ? ORDER BY joined_at").bind(id).all<{ user_id: string; role: string }>()).results;
  const g = await db.prepare("SELECT kind FROM groups WHERE id = ?").bind(id).first<{ kind: string }>();
  if (left.length === 0 || (g?.kind === "pair" && left.length < 2)) {
    await db.batch([db.prepare("DELETE FROM members WHERE group_id = ?").bind(id), db.prepare("DELETE FROM groups WHERE id = ?").bind(id)]);
  } else if (g?.kind === "group" && !left.some((m) => m.role === "admin")) {
    await db.prepare("UPDATE members SET role = 'admin' WHERE group_id = ? AND user_id = ?").bind(id, left[0].user_id).run();
  }
}

async function setRole(c: Ctx, id: string, userId: string): Promise<Response> {
  await requireMember(c, id, true);
  const role = c.body.admin ? "admin" : "member";
  const res = await c.env.DB.prepare("UPDATE members SET role = ? WHERE group_id = ? AND user_id = ?").bind(role, id, userId).run();
  if (!res.meta.changes) throw new HttpError(404, "no_member");
  await cleanupGroup(c.env.DB, id);
  return json({ ok: true });
}

async function removeMember(c: Ctx, id: string, userId: string): Promise<Response> {
  const g = await requireMember(c, id, true);
  if (g.kind !== "group") throw new HttpError(400, "pair");
  if (userId === c.me.id) throw new HttpError(400, "use_leave");
  const res = await c.env.DB.prepare("DELETE FROM members WHERE group_id = ? AND user_id = ?").bind(id, userId).run();
  if (!res.meta.changes) throw new HttpError(404, "no_member");
  return json({ ok: true });
}

// ------------------------------------------------------------------ joining

function normalizeCode(raw: unknown): { kind: "group" | "pair"; code: string } {
  if (typeof raw !== "string") throw new HttpError(400, "bad_code");
  const persian = "۰۱۲۳۴۵۶۷۸۹";
  const s = [...raw].map((ch) => (persian.includes(ch) ? String(persian.indexOf(ch)) : ch)).join("").toUpperCase().replace(/[^A-Z0-9]/g, "");
  if (/^\d{6}$/.test(s)) return { kind: "group", code: s };
  if (/^[A-Z0-9]{7}$/.test(s)) return { kind: "pair", code: s };
  throw new HttpError(400, "bad_code");
}

async function groupByCode(db: D1Database, code: string): Promise<GroupRow> {
  const g = await db.prepare("SELECT * FROM groups WHERE invite_code = ? AND kind = 'group'").bind(code).first<GroupRow>();
  if (!g) throw new HttpError(404, "no_such_code");
  if (g.code_expires_at != null && g.code_expires_at < now()) throw new HttpError(410, "code_expired");
  return g;
}

async function invitePreview(c: Ctx, raw: string): Promise<Response> {
  const { kind, code } = normalizeCode(decodeURIComponent(raw));
  if (kind === "pair") {
    const u = await c.env.DB.prepare("SELECT nick FROM users WHERE pair_code = ?").bind(code).first<{ nick: string }>();
    if (!u) throw new HttpError(404, "no_such_code");
    return json({ kind, name: u.nick, count: 1, names: [u.nick] });
  }
  const g = await groupByCode(c.env.DB, code);
  const ms = (await c.env.DB.prepare("SELECT u.nick FROM members m JOIN users u ON u.id = m.user_id WHERE m.group_id = ? ORDER BY m.joined_at")
    .bind(g.id).all<{ nick: string }>()).results;
  return json({ kind, name: g.name, count: ms.length, names: ms.slice(0, 3).map((m) => m.nick) });
}

async function join(c: Ctx): Promise<Response> {
  const db = c.env.DB;
  const { kind, code } = normalizeCode(c.body.code);
  const t = now();
  if (kind === "group") {
    const g = await groupByCode(db, code);
    await db.prepare("INSERT OR IGNORE INTO members (group_id, user_id, role, joined_at) VALUES (?, ?, 'member', ?)").bind(g.id, c.me.id, t).run();
    notifyFriends(c, "refresh");
    return json({ id: g.id, kind, name: g.name });
  }
  const other = await db.prepare("SELECT id, nick FROM users WHERE pair_code = ?").bind(code).first<{ id: string; nick: string }>();
  if (!other) throw new HttpError(404, "no_such_code");
  if (other.id === c.me.id) throw new HttpError(400, "own_code");
  const existing = await db.prepare(
    "SELECT g.id FROM groups g JOIN members a ON a.group_id = g.id JOIN members b ON b.group_id = g.id WHERE g.kind = 'pair' AND a.user_id = ? AND b.user_id = ?",
  ).bind(c.me.id, other.id).first<{ id: string }>();
  if (existing) return json({ id: existing.id, kind, name: other.nick });
  const id = uuid();
  await db.batch([
    db.prepare("INSERT INTO groups (id, name, icon, color, kind, created_at) VALUES (?, '', 'favorite', 0, 'pair', ?)").bind(id, t),
    db.prepare("INSERT INTO members (group_id, user_id, role, joined_at) VALUES (?, ?, 'member', ?)").bind(id, c.me.id, t),
    db.prepare("INSERT INTO members (group_id, user_id, role, joined_at) VALUES (?, ?, 'member', ?)").bind(id, other.id, t),
  ]);
  notifyFriends(c, "refresh");
  return json({ id, kind, name: other.nick });
}
