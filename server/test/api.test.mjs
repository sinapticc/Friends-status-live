// End-to-end checks against a running worker.
// Start one with: npx wrangler d1 migrations apply fsl --local && npx wrangler dev --port 8787
// Then run:      node test/api.test.mjs   (BASE=https://... to point elsewhere)
import assert from "node:assert/strict";

const BASE = process.env.BASE ?? "http://127.0.0.1:8787";
let passed = 0;

async function call(method, path, token, body, extraHeaders = {}) {
  const res = await fetch(BASE + path, {
    method,
    headers: { "content-type": "application/json", ...(token ? { authorization: `Bearer ${token}` } : {}), ...extraHeaders },
    body: body === undefined ? undefined : JSON.stringify(body),
  });
  const data = await res.json();
  return { status: res.status, data };
}
const ok = async (...a) => {
  const r = await call(...a);
  assert.ok(r.status < 300, `${a[0]} ${a[1]} -> ${r.status} ${JSON.stringify(r.data)}`);
  return r.data;
};
async function test(name, fn) {
  await fn();
  passed++;
  console.log("✓", name);
}
const friend = (feed, id) => feed.friends.find((f) => f.id === id);

const A = await ok("POST", "/v1/register", null, { nick: "آرش", avatar: 1 });
const B = await ok("POST", "/v1/register", null, { nick: "مریم", avatar: 0 });
const C = await ok("POST", "/v1/register", null, { nick: "کیان", avatar: 3 });
let group;

await test("rejects missing or bad tokens", async () => {
  assert.equal((await call("GET", "/v1/feed")).status, 401);
  assert.equal((await call("GET", "/v1/feed", "nope")).status, 401);
});

await test("validates input", async () => {
  assert.equal((await call("POST", "/v1/register", null, { nick: "" })).status, 400);
  assert.equal((await call("POST", "/v1/status", A.token, { key: "nope", text: "x" })).status, 400);
  assert.equal((await call("POST", "/v1/status", A.token, { key: "gym", text: "x".repeat(33) })).status, 400);
});

await test("create group, preview and join with a Persian-digit code", async () => {
  group = await ok("POST", "/v1/groups", A.token, { name: "هم‌خونه‌ها", icon: "home" });
  assert.match(group.code, /^\d{6}$/);
  const fa = group.code.replace(/\d/g, (d) => "۰۱۲۳۴۵۶۷۸۹"[d]);
  const preview = await ok("GET", `/v1/invite/${encodeURIComponent(fa)}`, B.token);
  assert.equal(preview.name, "هم‌خونه‌ها");
  assert.deepEqual(preview.names, ["آرش"]);
  await ok("POST", "/v1/join", B.token, { code: fa });
  await ok("POST", "/v1/join", C.token, { code: group.code });
  await ok("POST", "/v1/join", C.token, { code: group.code }); // idempotent
  const feed = await ok("GET", "/v1/feed", A.token);
  assert.equal(feed.groups.length, 1);
  assert.equal(feed.groups[0].members.length, 3);
  assert.equal(feed.groups[0].role, "admin");
  assert.equal(feed.groups[0].code, group.code);
  const bFeed = await ok("GET", "/v1/feed", B.token);
  assert.equal(bFeed.groups[0].code, null, "members don't see the code");
});

await test("statuses reach friends with history", async () => {
  await ok("POST", "/v1/status", A.token, { key: "coffee", text: "قهوه‌ی سوم" });
  await ok("POST", "/v1/status", A.token, { key: "gym", text: "روز پا", hue: 60, acc: "cap" });
  const feed = await ok("GET", "/v1/feed", B.token);
  const a = friend(feed, A.id);
  assert.equal(a.status.key, "gym");
  assert.equal(a.status.hue, 60);
  assert.equal(a.status.acc, "cap");
  assert.deepEqual(a.history.map((h) => h.key), ["coffee"]);
  assert.equal((await ok("GET", "/v1/feed", A.token)).me.status.key, "gym");
});

await test("private statuses are masked unless shared 1-on-1", async () => {
  await ok("POST", "/v1/join", A.token, { code: B.pairCode });
  await ok("POST", "/v1/status", A.token, { key: "period2", text: "شکلات", visibility: "one", expiresInMin: 60 });
  const b = friend(await ok("GET", "/v1/feed", B.token), A.id);
  const c = friend(await ok("GET", "/v1/feed", C.token), A.id);
  assert.equal(b.status.key, "period2");
  assert.equal(c.status.key, "busy");
  assert.ok(!c.history.some((h) => h.key === "period2"));
  const aFeed = await ok("GET", "/v1/feed", A.token);
  const pair = aFeed.groups.find((g) => g.kind === "pair");
  assert.equal(pair.name, "مریم", "pair space is named after the other person");
});

await test("private status visible to chosen groups only", async () => {
  await ok("POST", "/v1/status", A.token, { key: "spicy", text: "داغ", visibility: "groups", visGroups: [group.id] });
  assert.equal(friend(await ok("GET", "/v1/feed", C.token), A.id).status.key, "spicy");
});

await test("ghost mode and pauses hide status and history", async () => {
  await ok("PATCH", "/v1/me", A.token, { ghost: true });
  let a = friend(await ok("GET", "/v1/feed", C.token), A.id);
  assert.equal(a.status.key, "dnd");
  assert.deepEqual(a.history, []);
  await ok("PATCH", "/v1/me", A.token, { ghost: false, pausedUntil: Date.now() + 3_600_000 });
  a = friend(await ok("GET", "/v1/feed", C.token), A.id);
  assert.equal(a.status.key, "dnd");
  await ok("PATCH", "/v1/me", A.token, { pausedUntil: null });
  assert.equal(friend(await ok("GET", "/v1/feed", C.token), A.id).status.key, "spicy");
});

await test("location payloads are rejected and feeds contain no location fields", async () => {
  const legacy = [
    ["PATCH", "/v1/me", { lat: 35.7, lng: 51.4 }],
    ["PATCH", "/v1/me", { precision: "exact" }],
    ["POST", "/v1/status", { key: "gym", text: "روز پا", lat: 35.7 }],
  ];
  for (const [method, path, body] of legacy) {
    const response = await call(method, path, A.token, body);
    assert.equal(response.status, 400, `${method} ${path}`);
    assert.equal(response.data.error, "location_disabled");
  }
  const feed = await ok("GET", "/v1/feed", C.token);
  const serialized = JSON.stringify(feed);
  for (const field of ["lat", "lng", "precision", "share", "distanceKm"]) {
    assert.ok(!serialized.includes(`"${field}"`), `${field} leaked into feed`);
  }
});

await test("reactions show up for the receiver", async () => {
  const t = Date.now() - 1000;
  await ok("POST", "/v1/react", B.token, { to: A.id, kind: "poke" });
  const feed = await ok("GET", `/v1/feed?since=${t}`, A.token);
  assert.equal(feed.reactions.length, 1);
  assert.equal(feed.reactions[0].nick, "مریم");
  const stranger = await ok("POST", "/v1/register", null, { nick: "غریبه" });
  assert.equal((await call("POST", "/v1/react", stranger.token, { to: A.id, kind: "poke" })).status, 403);
});

await test("admin actions are admin-only", async () => {
  assert.equal((await call("POST", `/v1/groups/${group.id}/code`, C.token, {})).status, 403);
  assert.equal((await call("DELETE", `/v1/groups/${group.id}/members/${B.id}`, C.token)).status, 403);
  await ok("POST", `/v1/groups/${group.id}/members/${B.id}/role`, A.token, { admin: true });
  await ok("DELETE", `/v1/groups/${group.id}/members/${C.id}`, B.token);
  const cFeed = await ok("GET", "/v1/feed", C.token);
  assert.equal(cFeed.groups.length, 0);
  assert.equal(cFeed.friends.length, 0);
});

await test("code reset and expiry settings", async () => {
  const { code } = await ok("POST", `/v1/groups/${group.id}/code`, A.token, {});
  assert.notEqual(code, group.code);
  assert.equal((await call("POST", "/v1/join", C.token, { code: group.code })).status, 404, "old code stops working");
  await ok("PATCH", `/v1/groups/${group.id}`, A.token, { codeTtl: "never", name: "خونه" });
  const g = (await ok("GET", "/v1/feed", A.token)).groups.find((x) => x.id === group.id);
  assert.equal(g.codeExpiresAt, null);
  assert.equal(g.name, "خونه");
});

await test("leaving hands admin on and pairs dissolve with their last member", async () => {
  await ok("POST", `/v1/groups/${group.id}/members/${B.id}/role`, A.token, { admin: false });
  await ok("POST", `/v1/groups/${group.id}/leave`, A.token, {});
  const bFeed = await ok("GET", "/v1/feed", B.token);
  const g = bFeed.groups.find((x) => x.id === group.id);
  assert.equal(g.role, "admin", "last member became admin");
  await ok("DELETE", "/v1/me", A.token);
  assert.equal((await call("GET", "/v1/feed", A.token)).status, 401);
  assert.equal((await ok("GET", "/v1/feed", B.token)).groups.filter((x) => x.kind === "pair").length, 0);
});

await test("clear history keeps the current status", async () => {
  await ok("POST", "/v1/status", B.token, { key: "eating", text: "ناهار" });
  await ok("POST", "/v1/status", B.token, { key: "work", text: "جلسه" });
  await ok("DELETE", "/v1/history", B.token);
  assert.equal((await ok("GET", "/v1/feed", B.token)).me.status.key, "work");
});

await test("expired status reverts to previous non-expired one", async () => {
  const t = Date.now();
  await call("POST", "/v1/status", B.token, { key: "free", text: "اولیه" }, { "X-Test-Now": `${t + 1000}` });
  await call("POST", "/v1/status", B.token, { key: "dnd", text: "کوتاه", expiresInMin: 1 }, { "X-Test-Now": `${t + 2000}` });
  
  let feed = (await call("GET", "/v1/feed", B.token, undefined, { "X-Test-Now": `${t + 3000}` })).data;
  assert.equal(feed.me.status.key, "dnd");

  feed = (await call("GET", "/v1/feed", B.token, undefined, { "X-Test-Now": `${t + 70000}` })).data;
  assert.equal(feed.me.status.key, "free");
});

await test("rate limits return 429", async () => {
  // We use a unique IP so it doesn't affect other tests.
  const ip = "1.2.3.4";
  let lastStatus = 200;
  for (let i = 0; i < 22; i++) {
    const res = await call("POST", "/v1/register", null, { nick: "spam", avatar: 1 }, { "CF-Connecting-IP": ip });
    lastStatus = res.status;
  }
  assert.equal(lastStatus, 429);
});

await test("recovery code flow: create, recover, old token revoked", async () => {
  // A was deleted earlier in the test suite, so register a fresh user.
  const D = await ok("POST", "/v1/register", null, { nick: "دارا", avatar: 2 });
  // Create a recovery code
  const rec = await ok("POST", "/v1/me/recovery", D.token, {});
  assert.equal(rec.code.length, 14, "code with dashes is 14 chars");
  assert.match(rec.code, /^[A-Z0-9]{4}-[A-Z0-9]{4}-[A-Z0-9]{4}$/);
  // Recover with the code → new token works
  const res = await call("POST", "/v1/recover", null, { code: rec.code });
  assert.equal(res.status, 200);
  const newToken = res.data.token;
  assert.equal(res.data.id, D.id);
  await ok("GET", "/v1/feed", newToken);
  // Old token is revoked
  assert.equal((await call("GET", "/v1/feed", D.token)).status, 401);
  // Wrong code → 404
  assert.equal((await call("POST", "/v1/recover", null, { code: "XXXX-XXXX-XXXX" })).status, 404);
  // 11th attempt on same IP → 429
  const ip = "5.6.7.8";
  for (let i = 0; i < 10; i++) {
    await call("POST", "/v1/recover", null, { code: "AAAA-BBBB-CCCC" }, { "CF-Connecting-IP": ip });
  }
  const r11 = await call("POST", "/v1/recover", null, { code: "AAAA-BBBB-CCCC" }, { "CF-Connecting-IP": ip });
  assert.equal(r11.status, 429);
});

console.log(`\n${passed} passed`);
