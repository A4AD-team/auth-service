ALTER TABLE users ADD COLUMN username VARCHAR(190);

UPDATE users
SET username = lower(split_part(email, '@', 1))
WHERE username IS NULL;

WITH duplicates AS (
    SELECT username
    FROM users
    GROUP BY username
    HAVING COUNT(*) > 1
)
UPDATE users
SET username = replace(lower(email), '@', '_')
WHERE username IN (SELECT username FROM duplicates);

ALTER TABLE users ALTER COLUMN username SET NOT NULL;
CREATE UNIQUE INDEX idx_users_username ON users(username);
