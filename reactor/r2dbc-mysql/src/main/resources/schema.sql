
CREATE TABLE IF NOT EXISTS member (
    member_id int not null auto_increment,
    name varchar(64) not null,
    primary key (member_id)
);

CREATE TABLE IF NOT EXISTS transfer (
    transfer_id bigint not null auto_increment,
    amount bigint NOT NULL,
    to_member_id int NOT NULL,
    from_member_id int,
    created_date datetime NOT NULL,
    primary key (transfer_id)
);

CREATE TABLE IF NOT EXISTS balance (
    member_id bigint NOT NULL,
    amount bigint NOT NULL,
    created_date datetime NOT NULL,
    last_modified_date datetime NOT NULL,
    primary key (member_id)
);


/*==============================================================*/
/* Table: CN_PROVIDER                                           */
/*==============================================================*/
create table CN_PROVIDER
(
    PROVIDER_ID          mediumint not null comment 'Unique Contenet Privider ID (Code)',
    PROVIDER_NAME        varchar(32) not null,
    PROVIDER_TYPE        char(1) not null default 'N',
    STATE                char(1) not null,
    CREATED_DT           datetime not null,
    END_DT               datetime,
    primary key (PROVIDER_ID)
);

alter table CN_PROVIDER
    add constraint CKC_PROVIDER_TYPE_CN_PROVIDER check (PROVIDER_TYPE in ('B','N','Y','E','O'));

alter table CN_PROVIDER
    add constraint CKC_STATE_CN_PROVIDER check (STATE in ('E','D'));

alter table CN_PROVIDER
    add constraint CKC_PROVIDER_NO_CN_PROVIDER check (PROVIDER_ID >= 100000 and PROVIDER_ID <= 999999);
