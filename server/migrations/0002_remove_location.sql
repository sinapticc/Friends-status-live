-- Remove all previously stored location data and location-sharing preferences.
ALTER TABLE users DROP COLUMN precision;
ALTER TABLE users DROP COLUMN lat;
ALTER TABLE users DROP COLUMN lng;
ALTER TABLE members DROP COLUMN share;
ALTER TABLE statuses DROP COLUMN lat;
ALTER TABLE statuses DROP COLUMN lng;
