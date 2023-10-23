CREATE TABLE IF NOT EXISTS authority (
  username VARCHAR(45) NOT NULL,
  authority VARCHAR(45) NOT NULL,
  PRIMARY KEY (username, authority));
