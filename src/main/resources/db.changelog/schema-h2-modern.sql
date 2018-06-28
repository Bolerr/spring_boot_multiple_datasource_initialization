/******************************************************************
New tables
******************************************************************/
CREATE SCHEMA IF NOT EXISTS admin_records;
COMMIT;

DROP TABLE IF EXISTS admin_records.record;
CREATE TABLE admin_records.record (
  id          BIGINT       NOT NULL IDENTITY PRIMARY KEY,
  record_number INTEGER NOT NULL,
  content varchar(255)

);
CREATE INDEX record_id_idx
  ON admin_records.record (id);
