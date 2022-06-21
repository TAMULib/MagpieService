START TRANSACTION;

ALTER TABLE document ADD COLUMN IF NOT EXISTS deleted boolean;
UPDATE document SET deleted = false;
ALTER TABLE document ALTER COLUMN deleted SET NOT NULL;

COMMIT;
