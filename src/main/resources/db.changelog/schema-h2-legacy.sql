/******************************************************************
Existing Tables that have not been migrated to the new database
******************************************************************/
CREATE SCHEMA IF NOT EXISTS dbo;
COMMIT;

DROP TABLE IF EXISTS dbo.record;
CREATE TABLE dbo.record (
  recordId          INT       NOT NULL IDENTITY PRIMARY KEY,
  recordNumber varchar(5) NOT NULL,
  content varchar(255)

);
CREATE INDEX record_id_idx
  ON dbo.record (recordId);
