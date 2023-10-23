
CREATE TABLE IF NOT EXISTS h2_user (
  username VARCHAR(45) NOT NULL,
  password TEXT NULL,
  PRIMARY KEY (`username`));

# CREATE TABLE IF NOT EXISTS otp (
#   username VARCHAR(45) NOT NULL,
#   code VARCHAR(45) NOT NULL,
#   PRIMARY KEY (username, code));