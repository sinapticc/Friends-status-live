/**
 * Firebase Cloud Messaging (HTTP v1) without the Firebase SDK.
 * Sends silent data messages that wake the app so it can refresh its widget.
 */

interface ServiceAccount {
  project_id: string;
  client_email: string;
  private_key: string;
}

let cached: { token: string; exp: number; email: string } | null = null;

function b64url(data: ArrayBuffer | string): string {
  const bytes = typeof data === "string" ? new TextEncoder().encode(data) : new Uint8Array(data);
  let s = "";
  for (const b of bytes) s += String.fromCharCode(b);
  return btoa(s).replace(/\+/g, "-").replace(/\//g, "_").replace(/=+$/, "");
}

async function accessToken(sa: ServiceAccount): Promise<string> {
  const t = Math.floor(Date.now() / 1000);
  if (cached && cached.email === sa.client_email && cached.exp - 60 > t) return cached.token;
  const header = b64url(JSON.stringify({ alg: "RS256", typ: "JWT" }));
  const claims = b64url(
    JSON.stringify({
      iss: sa.client_email,
      scope: "https://www.googleapis.com/auth/firebase.messaging",
      aud: "https://oauth2.googleapis.com/token",
      iat: t,
      exp: t + 3600,
    }),
  );
  const pem = sa.private_key.replace(/-----[^-]+-----/g, "").replace(/\s+/g, "");
  const der = Uint8Array.from(atob(pem), (c) => c.charCodeAt(0));
  const key = await crypto.subtle.importKey("pkcs8", der, { name: "RSASSA-PKCS1-v1_5", hash: "SHA-256" }, false, ["sign"]);
  const sig = await crypto.subtle.sign("RSASSA-PKCS1-v1_5", key, new TextEncoder().encode(`${header}.${claims}`));
  const jwt = `${header}.${claims}.${b64url(sig)}`;
  const res = await fetch("https://oauth2.googleapis.com/token", {
    method: "POST",
    headers: { "content-type": "application/x-www-form-urlencoded" },
    body: `grant_type=urn%3Aietf%3Aparams%3Aoauth%3Agrant-type%3Ajwt-bearer&assertion=${jwt}`,
  });
  if (!res.ok) throw new Error(`fcm auth ${res.status}`);
  const body = (await res.json()) as { access_token: string; expires_in: number };
  cached = { token: body.access_token, exp: t + body.expires_in, email: sa.client_email };
  return body.access_token;
}

/**
 * Sends a data message to each token. Returns the tokens FCM reported as gone,
 * so the caller can forget them.
 */
export async function sendData(serviceAccountJson: string | undefined, tokens: string[], data: Record<string, string>): Promise<string[]> {
  if (!serviceAccountJson || tokens.length === 0) return [];
  const sa = JSON.parse(serviceAccountJson) as ServiceAccount;
  const auth = await accessToken(sa);
  const url = `https://fcm.googleapis.com/v1/projects/${sa.project_id}/messages:send`;
  const gone: string[] = [];
  await Promise.all(
    tokens.map(async (token) => {
      const res = await fetch(url, {
        method: "POST",
        headers: { authorization: `Bearer ${auth}`, "content-type": "application/json" },
        body: JSON.stringify({ message: { token, data, android: { priority: "high", ttl: "3600s" } } }),
      });
      // 404 means the app was uninstalled or the token rotated.
      if (res.status === 404) gone.push(token);
    }),
  );
  return gone;
}
