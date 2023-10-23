/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     03.04.2023 15:49:18                          */
/*==============================================================*/

/*==============================================================*/
/* Table: ZO_ANIMAL                                             */
/*==============================================================*/
create table ZO_ANIMAL
(
   ANIMAL_ID            bigint not null auto_increment  comment '',
   NICKNAME             char(30) not null  comment '',
   ANIMAL_TYPE_ID       int not null  comment '',
   GENDER               char(1) not null  comment '',
   BIRTH_DT             date  comment '',
   DEATH_DT             date  comment '',
   DESCR                char(255)  comment '',
   primary key (ANIMAL_ID)
);

/*==============================================================*/
/* Table: ZO_ANIMAL_TYPE                                        */
/*==============================================================*/
create table ZO_ANIMAL_TYPE
(
   ANIMAL_TYPE_ID       int not null auto_increment  comment '',
   NAME                 char(100) not null  comment '',
   DESCR                char(255)  comment '',
   primary key (ANIMAL_TYPE_ID)
);

/*==============================================================*/
/* Table: ZO_TANK                                               */
/*==============================================================*/
create table ZO_TANK
(
   TANK_ID              int not null auto_increment  comment '',
   TANK_TYPE            char(1) not null  comment '',
   NUMBER_CD            char(20) not null  comment '',
   DESCR                char(255)  comment '',
   primary key (TANK_ID)
);

/*==============================================================*/
/* Table: ZO_TANK_ANIMAL                                        */
/*==============================================================*/
create table ZO_TANK_ANIMAL
(
   TANK_ID              int not null  comment '',
   ANIMAL_ID            bigint not null  comment '',
   FROM_DT              datetime not null  comment '',
   TO_DT                datetime  comment '',
   primary key (TANK_ID, ANIMAL_ID)
);

alter table ZO_ANIMAL add constraint FK_ZO_ANIMAL$ANIMAL_TYPE_ID foreign key (ANIMAL_TYPE_ID)
      references ZO_ANIMAL_TYPE (ANIMAL_TYPE_ID) on delete restrict on update restrict;

alter table ZO_TANK_ANIMAL add constraint FK_ZO_TANK_ANIMAL$TANK_ID foreign key (TANK_ID)
      references ZO_TANK (TANK_ID) on delete restrict on update restrict;

alter table ZO_TANK_ANIMAL add constraint FK_ZO_TANK_ANIMAL$ANIMAL_ID foreign key (ANIMAL_ID)
      references ZO_ANIMAL (ANIMAL_ID) on delete restrict on update restrict;

