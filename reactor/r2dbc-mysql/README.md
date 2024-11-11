# REST модули сервиса *r2dbc-mysql* использующие Reactor

* ***r2dbc-mysql-be*** - реализация REST сервиса использующего Reactor;
* ***r2dbc-mysql-be-app*** - приложение REST сервиса *r2dbc-mysql-be*;  
* ***r2dbc-mysql-webclient*** - клиент для доступа к сервису *r2dbc-mysql*;
* ***r2dbc-mysql-webclient-app*** - приложение использующее клиент *r2dbc-mysql-webclient*.


### База данных

Используется база данных MySql 8.<br>
Она запускается как Docker контейнер в сервисе *zyme-r2dbc-mysql8* (см.
  [***r2dbc-mysql-be-app/docker-compose.yaml***](r2dbc-mysql-be-app/docker-compose.yaml)).<br>
Скрипт создания объектов БД - [***schema.sql***](r2dbc-mysql-be/src/main/resources/schema.sql).<br>
Этот скрипт нужно выполнять вручную.

### Docker
Сервис (приложение) *r2dbc-mysql-be-app* не запускается в Docker.<br>
Ошибка:<br>
Failed to instantiate [io.r2dbc.pool.ConnectionPool]: Factory method 'connectionFactory' threw exception with message:
URL jdbc:mysql://zyme-r2dbc-mysql8:3306/zyme does not start with the r2dbc scheme