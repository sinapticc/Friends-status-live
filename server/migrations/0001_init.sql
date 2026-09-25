-- Anonymous accounts: the app keeps a random token, the server stores only its hash.
CREATE TABLE users (
  id TEXT PRIMARY KEY,
  token_hash TEXT NOT NULL UNIQUE,
  nick TEXT NOT NULL,
  avatar INTEGER NOT NULL DEFAULT 0,
  look TEXT,
  pair_code TEXT NOT NULL UNIQUE,
  fcm_token TEXT,
  ghost INTEGER NOT NULL DEFAULT 0,
  paused_until INTEGER,
  precision TEXT NOT NULL DEFAULT 'approx',
  lat REAL,
  lng REAL,
  created_at INTEGER NOT NULL
);

-- kind 'group' is a normal group; kind 'pair' is a private 1-on-1 space.
CREATE TABLE groups (
  id TEXT PRIMARY KEY,
  name TEXT NOT NULL,
  icon TEXT NOT NULL DEFAULT 'home',
  color INTEGER NOT NULL DEFAULT 0,
  kind TEXT NOT NULL DEFAULT 'group',
  invite_code TEXT UNIQUE,
  code_expires_at INTEGER,
  code_ttl TEXT NOT NULL DEFAULT '7d',
  created_at INTEGER NOT NULL
);

CREATE TABLE members (
  group_id TEXT NOT NULL REFERENCES groups(id) ON DELETE CASCADE,
  user_id TEXT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  role TEXT NOT NULL DEFAULT 'member',
  share TEXT NOT NULL DEFAULT 'approx',
  muted INTEGER NOT NULL DEFAULT 0,
  joined_at INTEGER NOT NULL,
  PRIMARY KEY (group_id, user_id)
);
CREATE INDEX members_user ON members(user_id);

CREATE TABLE statuses (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  user_id TEXT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  key TEXT NOT NULL,
  text TEXT NOT NULL,
  hue INTEGER NOT NULL DEFAULT 0,
  acc TEXT NOT NULL DEFAULT 'none',
  sensitive INTEGER NOT NULL DEFAULT 0,
  visibility TEXT NOT NULL DEFAULT 'all',
  vis_groups TEXT,
  lat REAL,
  lng REAL,
  created_at INTEGER NOT NULL,
  expires_at INTEGER
);
CREATE INDEX statuses_user ON statuses(user_id, created_at DESC);

CREATE TABLE reactions (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  from_id TEXT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  to_id TEXT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  kind TEXT NOT NULL,
  created_at INTEGER NOT NULL
);
CREATE INDEX reactions_to ON reactions(to_id, created_at);
