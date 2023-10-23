/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     03.01.2023 16:50:24                          */
/*==============================================================*/


/*==============================================================*/
/* Table: AA_TRANS                                              */
/*==============================================================*/
create table AA_TRANS
(
    TRANS_ID             bigint not null auto_increment,
    TRANS_NAME           varchar(200),
    NOTE                 varchar(2000),
    CREATED_DT           datetime not null,
    primary key (TRANS_ID)
);


/*==============================================================*/
/* Table: AA_GROUP                                              */
/*==============================================================*/
create table AA_GROUP
(
   GROUP_ID             bigint not null auto_increment,
   GROUP_CD             varchar(32) not null,
   GROUP_NAME           varchar(64),
   NOTE                 varchar(2000),
   primary key (GROUP_ID)
);

alter table AA_GROUP
   add unique AK_GROUP_CD (GROUP_CD);

/*==============================================================*/
/* Table: AA_GROUP_MEMBER                                       */
/*==============================================================*/
create table AA_GROUP_MEMBER
(
   GROUP_ID             bigint not null,
   USER_ID              bigint not null,
   primary key (GROUP_ID, USER_ID)
);

/*==============================================================*/
/* Table: AA_GROUP_ROLE                                         */
/*==============================================================*/
create table AA_GROUP_ROLE
(
   GROUP_ID             bigint not null,
   ROLE_ID              bigint not null,
   primary key (GROUP_ID, ROLE_ID)
);

/*==============================================================*/
/* Table: AA_IP_ADDR                                            */
/*==============================================================*/
create table AA_IP_ADDR
(
   IP_ADDR_ID           bigint not null auto_increment,
   USER_ID              bigint not null,
   ADDR_ACTION          char(1) not null,
   ADDRESS              varchar(32) not null,
   NOTE                 varchar(2000),
   primary key (IP_ADDR_ID)
);

/*==============================================================*/
/* Table: AA_PROFILE                                            */
/*==============================================================*/
create table AA_PROFILE
(
   PROFILE_ID           bigint not null auto_increment,
   PROFILE_CD           varchar(32) not null,
   PSWD_LIFE_TIME       numeric(8,0) not null default 0,
   PSWD_GRACE_TIME      numeric(8,0) not null default 0,
   PSWD_REUSE_TIME      numeric(8,0) not null default 0,
   PSWD_REUSE_MAX       numeric(8,0) not null default 0,
   LOGIN_ATTEMP_MAX     numeric(8,0) not null default 0,
   PSWD_LOCK_TIME       numeric(8,0) not null default 0,
   PSWD_LEN_MIN         numeric(8,0) not null default 0,
   PSWD_ALPHANUM        char(1) not null default 'N',
   PSWD_CASE            char(1) not null default 'N',
   primary key (PROFILE_ID)
);

alter table AA_PROFILE comment 'Password policy profile';

alter table AA_PROFILE
   add unique AK_AK_PROFILE_CD (PROFILE_CD);

/*==============================================================*/
/* Table: AA_PSWD_HIST                                          */
/*==============================================================*/
create table AA_PSWD_HIST
(
   PSWD_HIST_ID         bigint not null auto_increment,
   USER_ID              bigint not null,
   PASSWORD             varchar(32) not null,
   CREATED_DT           datetime not null,
   primary key (PSWD_HIST_ID)
);

/*==============================================================*/
/* Table: AA_ROLE                                               */
/*==============================================================*/
create table AA_ROLE
(
   ROLE_ID              bigint not null auto_increment,
   ROLE_CD              varchar(32) not null,
   ROLE_NAME            varchar(128) not null,
   ROLE_TYPE            char(1) not null,
   NOTE                 varchar(2000),
   primary key (ROLE_ID)
);

alter table AA_ROLE
   add unique AK_ROLE_CD (ROLE_CD);

/*==============================================================*/
/* Table: AA_USER                                               */
/*==============================================================*/
create table AA_USER
(
   USER_ID              bigint not null auto_increment,
   UNAME                varchar(64) not null,
   PROFILE_ID           bigint not null,
   USER_TYPE            char(1) not null,
   PASSWORD             varchar(32),
   STATE                char(1) not null,
   AUTH_TYPE            char(1) not null,
   SHORT_NAME           varchar(32) not null,
   FULL_NAME            varchar(64),
   SMS_NO               varchar(16),
   EMAIL                varchar(128),
   CREATED_DT           datetime not null,
   UPDATED_DT           datetime,
   LAST_PSWD_DT         datetime not null,
   EXPIRATION_DT        datetime,
   LOCKED_DT            datetime,
   LOGIN_ATTEMP         numeric(8,0) not null default 0,
   NOTE                 varchar(2000),
   primary key (USER_ID)
);

alter table AA_USER
      add constraint CKC_STATE_AA_USER check (STATE in ('E','D','L'));


alter table AA_USER
      add constraint CKC_USER_TYPE_AA_USER check (USER_TYPE in ('A','I','E','S'));



alter table AA_USER
   add unique AK_UNAME_KEY (UNAME);

/*==============================================================*/
/* Table: AA_USER_ROLE                                          */
/*==============================================================*/
create table AA_USER_ROLE
(
   USER_ID              bigint not null,
   ROLE_ID              bigint not null,
   primary key (USER_ID, ROLE_ID)
);


alter table AA_GROUP_MEMBER add constraint FK_AA_GROUP_REF_AA_GROUP_MEM foreign key (GROUP_ID)
      references AA_GROUP (GROUP_ID) on delete restrict on update restrict;

alter table AA_GROUP_MEMBER add constraint FK_Reference_4 foreign key (USER_ID)
      references AA_USER (USER_ID) on delete restrict on update restrict;

alter table AA_GROUP_ROLE add constraint FK_AA_GROUP_REF_AA_GROUP_ROLE foreign key (GROUP_ID)
      references AA_GROUP (GROUP_ID) on delete restrict on update restrict;

alter table AA_GROUP_ROLE add constraint FK_Reference_6 foreign key (ROLE_ID)
      references AA_ROLE (ROLE_ID) on delete restrict on update restrict;

alter table AA_IP_ADDR add constraint FK_Reference_23 foreign key (USER_ID)
      references AA_USER (USER_ID) on delete restrict on update restrict;

alter table AA_PSWD_HIST add constraint FK_Reference_11 foreign key (USER_ID)
      references AA_USER (USER_ID) on delete restrict on update restrict;

alter table AA_USER add constraint FK_Reference_12 foreign key (PROFILE_ID)
      references AA_PROFILE (PROFILE_ID) on delete restrict on update restrict;

alter table AA_USER_ROLE add constraint FK_Reference_1_ foreign key (USER_ID)
      references AA_USER (USER_ID) on delete restrict on update restrict;

alter table AA_USER_ROLE add constraint FK_Reference_2 foreign key (ROLE_ID)
      references AA_ROLE (ROLE_ID) on delete restrict on update restrict;

