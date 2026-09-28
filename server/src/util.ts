export class HttpError extends Error {
  constructor(public status: number, public code: string) {
    super(code);
  }
}

export function json(data: unknown, status = 200): Response {
  return new Response(JSON.stringify(data), {
    status,
    headers: { "content-type": "application/json; charset=utf-8" },
  });
}

export const now = (req?: Request, env?: any) => {
  if (env?.FSL_TEST_CLOCK === "1" && req) {
    const h = req.headers.get("X-Test-Now");
    if (h) return Number(h);
  }
  return Date.now();
};

export function uuid(): string {
  return crypto.randomUUID();
}

export async function sha256(s: string): Promise<string> {
  const buf = await crypto.subtle.digest("SHA-256", new TextEncoder().encode(s));
  return [...new Uint8Array(buf)].map((b) => b.toString(16).padStart(2, "0")).join("");
}

/** Random string from an alphabet, using a CSPRNG. */
export function randomFrom(alphabet: string, length: number): string {
  const bytes = crypto.getRandomValues(new Uint8Array(length));
  let out = "";
  for (const b of bytes) out += alphabet[b % alphabet.length];
  return out;
}

export const inviteCode = () => randomFrom("0123456789", 6);
/** Personal 1-on-1 code; no 0/O/1/I so it can be read out loud. */
export const pairCode = () => randomFrom("ABCDEFGHJKLMNPQRSTUVWXYZ23456789", 7);
export const secretToken = () => randomFrom("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789", 40);
/** Recovery code: 12 chars, formatted XXXX-XXXX-XXXX. */
export const recoveryCode = () => randomFrom("ABCDEFGHJKLMNPQRSTUVWXYZ23456789", 12);

export function str(v: unknown, max: number, field: string): string {
  if (typeof v !== "string") throw new HttpError(400, `bad_${field}`);
  const s = v.trim();
  if (!s || [...s].length > max) throw new HttpError(400, `bad_${field}`);
  return s;
}

export function int(v: unknown, min: number, max: number, field: string): number {
  if (typeof v !== "number" || !Number.isInteger(v) || v < min || v > max) throw new HttpError(400, `bad_${field}`);
  return v;
}

export function oneOf<T extends string>(v: unknown, options: readonly T[], field: string): T {
  if (typeof v !== "string" || !options.includes(v as T)) throw new HttpError(400, `bad_${field}`);
  return v as T;
}

export async function rateLimit(db: D1Database, key: string, limit: number, windowMs: number) {
  const windowStart = Math.floor(Date.now() / windowMs) * windowMs;
  const q = await db.prepare(`
    INSERT INTO rate_limits (k, window_start, n)
    VALUES (?, ?, 1)
    ON CONFLICT(k) DO UPDATE SET
      n = CASE WHEN window_start = excluded.window_start THEN rate_limits.n + 1 ELSE 1 END,
      window_start = excluded.window_start
    RETURNING n
  `).bind(key, windowStart).first<{ n: number }>();
  if (q && q.n > limit) throw new HttpError(429, "rate_limited");
}
