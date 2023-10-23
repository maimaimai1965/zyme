/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     03.04.2023 14:05:20                          */
/*==============================================================*/


alter table ZO_ANIMAL
    drop foreign key FK_ZO_ANIMAL$ANIMAL_TYPE_ID;

alter table ZO_TANK_ANIMAL
    drop foreign key FK_ZO_TANK_ANIMAL$TANK_ID;

alter table ZO_TANK_ANIMAL
    drop foreign key FK_ZO_TANK_ANIMAL$ANIMAL_ID;


alter table ZO_ANIMAL
    drop foreign key FK_ZO_ANIMAL$ANIMAL_TYPE_ID;

drop table if exists ZO_ANIMAL;

drop table if exists ZO_ANIMAL_TYPE;

drop table if exists ZO_TANK;


alter table ZO_TANK_ANIMAL
    drop foreign key FK_ZO_TANK_ANIMAL$TANK_ID;

alter table ZO_TANK_ANIMAL
    drop foreign key FK_ZO_TANK_ANIMAL$ANIMAL_ID;

drop table if exists ZO_TANK_ANIMAL;
