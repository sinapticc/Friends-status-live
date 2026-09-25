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

export const now = () => Date.now();

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

/** Great-circle distance in kilometers. */
export function distanceKm(aLat: number, aLng: number, bLat: number, bLng: number): number {
  const r = 6371;
  const toRad = (d: number) => (d * Math.PI) / 180;
  const dLat = toRad(bLat - aLat);
  const dLng = toRad(bLng - aLng);
  const h = Math.sin(dLat / 2) ** 2 + Math.cos(toRad(aLat)) * Math.cos(toRad(bLat)) * Math.sin(dLng / 2) ** 2;
  return 2 * r * Math.asin(Math.sqrt(h));
}

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

export function coord(v: unknown, limit: number, field: string): number {
  if (typeof v !== "number" || !Number.isFinite(v) || Math.abs(v) > limit) throw new HttpError(400, `bad_${field}`);
  return v;
}
