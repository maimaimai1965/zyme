
create table IF NOT EXISTS AA_TRANS
(
  TRANS_ID             bigint not null auto_increment,
  TRANS_NAME           varchar(200),
  NOTE                 varchar(2000),
  CREATED_DT           datetime not null,
  primary key (TRANS_ID)
);

CREATE TABLE IF NOT EXISTS h2_user (
  username VARCHAR(45) NOT NULL,
  password TEXT NULL,
  PRIMARY KEY (`username`));

CREATE TABLE IF NOT EXISTS otp (
  username VARCHAR(45) NOT NULL,
  code VARCHAR(45) NULL,
  PRIMARY KEY (`username`));