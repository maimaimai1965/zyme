
/* This file is generated from Table. Please do not change this file manually !!!
Table location:
https://docs.google.com/spreadsheets/d/1wLUZ4ZV9PyWvqZZf2bICAJqQ7oBalZSn8G5B8eOnxRY/edit?usp=sharing
*/

SET SESSION sql_mode='NO_AUTO_VALUE_ON_ZERO';

/* zo_animal_type */
INSERT INTO `zo_animal_type` (`ANIMAL_TYPE_ID`, `NAME`, `DESCR`) VALUES ('1', 'бенгальский тигр', null);
INSERT INTO `zo_animal_type` (`ANIMAL_TYPE_ID`, `NAME`, `DESCR`) VALUES ('2', 'леопард', null);
INSERT INTO `zo_animal_type` (`ANIMAL_TYPE_ID`, `NAME`, `DESCR`) VALUES ('3', 'сумчатый волк', null);
INSERT INTO `zo_animal_type` (`ANIMAL_TYPE_ID`, `NAME`, `DESCR`) VALUES ('4', 'павлин', null);
INSERT INTO `zo_animal_type` (`ANIMAL_TYPE_ID`, `NAME`, `DESCR`) VALUES ('5', 'фазан', null);

/*zo_animal*/
INSERT INTO `zo_animal` (`ANIMAL_ID`, `NICKNAME`, `ANIMAL_TYPE_ID`, `GENDER`, `BIRTH_DT`, `DEATH_DT`, `DESCR`) VALUES (1, 'Витязь', 1, 'M', {d '2012-02-15'}, null, null);
INSERT INTO `zo_animal` (`ANIMAL_ID`, `NICKNAME`, `ANIMAL_TYPE_ID`, `GENDER`, `BIRTH_DT`, `DEATH_DT`, `DESCR`) VALUES (2, 'Молния', 1, 'F', {d '2014-08-19'}, null, null);
INSERT INTO `zo_animal` (`ANIMAL_ID`, `NICKNAME`, `ANIMAL_TYPE_ID`, `GENDER`, `BIRTH_DT`, `DEATH_DT`, `DESCR`) VALUES (3, 'Клея', 1, 'F', {d '2023-01-18'}, null, null);
INSERT INTO `zo_animal` (`ANIMAL_ID`, `NICKNAME`, `ANIMAL_TYPE_ID`, `GENDER`, `BIRTH_DT`, `DEATH_DT`, `DESCR`) VALUES (4, 'Ночь', 2, 'F', {d '2018-04-21'}, null, null);
INSERT INTO `zo_animal` (`ANIMAL_ID`, `NICKNAME`, `ANIMAL_TYPE_ID`, `GENDER`, `BIRTH_DT`, `DEATH_DT`, `DESCR`) VALUES (5, 'Луна', 2, 'F', {d '2019-12-14'}, null, null);
INSERT INTO `zo_animal` (`ANIMAL_ID`, `NICKNAME`, `ANIMAL_TYPE_ID`, `GENDER`, `BIRTH_DT`, `DEATH_DT`, `DESCR`) VALUES (6, 'Радуга', 4, 'F', {d '2018-03-22'}, null, null);
INSERT INTO `zo_animal` (`ANIMAL_ID`, `NICKNAME`, `ANIMAL_TYPE_ID`, `GENDER`, `BIRTH_DT`, `DEATH_DT`, `DESCR`) VALUES (7, 'Аква', 4, 'M', {d '2017-04-26'}, null, null);
INSERT INTO `zo_animal` (`ANIMAL_ID`, `NICKNAME`, `ANIMAL_TYPE_ID`, `GENDER`, `BIRTH_DT`, `DEATH_DT`, `DESCR`) VALUES (8, 'Смит', 5, 'M', {d '2018-08-16'}, null, null);

/* zo_tank */
INSERT INTO `zo_tank` (`TANK_ID`, `TANK_TYPE`, `NUMBER_CD`, `DESCR`) VALUES (1, 'C', 'CAGE_001', 'Тигры');
INSERT INTO `zo_tank` (`TANK_ID`, `TANK_TYPE`, `NUMBER_CD`, `DESCR`) VALUES (2, 'P', 'PAD_001', 'Птичник');
INSERT INTO `zo_tank` (`TANK_ID`, `TANK_TYPE`, `NUMBER_CD`, `DESCR`) VALUES (3, 'C', 'CAGE_003', 'Леопарды');

/* zo_tank_animal */
INSERT INTO `zo_tank_animal` (`TANK_ID`, `ANIMAL_ID`, `FROM_DT`) VALUES (1, 1, '2015-04-22');
INSERT INTO `zo_tank_animal` (`TANK_ID`, `ANIMAL_ID`, `FROM_DT`) VALUES (1, 2, '2015-05-21');
INSERT INTO `zo_tank_animal` (`TANK_ID`, `ANIMAL_ID`, `FROM_DT`) VALUES (1, 3, '2023-01-18');
INSERT INTO `zo_tank_animal` (`TANK_ID`, `ANIMAL_ID`, `FROM_DT`) VALUES (3, 4, '2020-09-20');
INSERT INTO `zo_tank_animal` (`TANK_ID`, `ANIMAL_ID`, `FROM_DT`) VALUES (3, 5, '2020-09-20');
INSERT INTO `zo_tank_animal` (`TANK_ID`, `ANIMAL_ID`, `FROM_DT`) VALUES (2, 6, '2019-09-24');
INSERT INTO `zo_tank_animal` (`TANK_ID`, `ANIMAL_ID`, `FROM_DT`) VALUES (2, 7, '2019-10-23');
INSERT INTO `zo_tank_animal` (`TANK_ID`, `ANIMAL_ID`, `FROM_DT`) VALUES (2, 8, '2019-10-23');
