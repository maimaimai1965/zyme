
create schema zyme;

CREATE TABLE IF NOT EXISTS member (
    member_id int not null auto_increment,
    name varchar(64) not null,
    primary key (member_id)
);

CREATE TABLE IF NOT EXISTS balance (
                                       member_id int NOT NULL,
                                       amount bigint NOT NULL,
                                       created_date datetime NOT NULL,
                                       last_modified_date datetime NOT NULL,
                                       primary key (member_id)
);
alter table BALANCE add constraint FK_BALANCE__MEMBER_ID foreign key (member_id)
    references MEMBER (member_id) on delete restrict on update restrict;


CREATE TABLE IF NOT EXISTS transfer (
    transfer_id bigint not null auto_increment,
    from_member_id int,
    to_member_id int NOT NULL,
    amount bigint NOT NULL,
    created_date datetime NOT NULL,
    primary key (transfer_id)
);
alter table TRANSFER add constraint FK_TRANSFER__TO_MEMBER_ID foreign key (to_member_id)
    references MEMBER (member_id) on delete restrict on update restrict;
alter table TRANSFER add constraint FK_TRANSFER__FROM_MEMBER_ID foreign key (from_member_id)
    references MEMBER (member_id) on delete restrict on update restrict;

