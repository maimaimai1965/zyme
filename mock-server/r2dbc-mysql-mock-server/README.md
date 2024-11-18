# Mock Service эмулирующий сервис *r2dbc-mysql*

## Запуск Mock Service

Mock Service, эмулирующий сервис *r2dbc-mysql*, запускается в Docker контейнере (см.
[***docker/mock-server-compose.yaml***](docker/mock-server-compose.yaml)).<br> 

При создании Mock Service добавляются варианты запросов/ответов (expectations) из файлов *ExpectationInitializerImpl.java*
и *initializerJson.json*, которые используются в тестах [***R2dbsMysqlMockServerForMemberControllerTests.java***](src/test/java/ua/mai/zyme/mockserver/r2dbcmysql/R2dbsMysqlMockServerForMemberControllerTests.java).


## Expectations

Запросы/ответы (expectations) добавляются на сервер тремя способами: 
1. Описываются в файле [***ExpectationInitializerImpl.java***](src/main/java/ua/mai/zyme/mockserver/r2dbcmysql/ExpectationInitializerImpl.java).
   Требует перекомпиляции модуля и пересоздания контейнера. 
2. Описываются в файле [***initializerJson.json***](src/main/resources/initializerJson.json).
   Требует перекомпиляции модуля и пересоздания контейнера.
3. Выполнение REST запроса на запущенном сервере. Пример см. в Postman коллекции
   [***initializerJson.json***](src/test/resources/postman/Zyme r2dbc-mysql-mock-server.postman_collection.json),
   запрос *'add EXP:001 insertMember(member) <- POST /api/members'*. 


## Postman
В Postman коллекции [***initializerJson.json***](src/test/resources/postman/Zyme r2dbc-mysql-mock-server.postman_collection.json)
есть запросы:
 
* *server ACTIVE_EXPECTATIONS*
  * *POST /api/members ACTIVE_EXPECTATIONS* - возвращает список активных expectations, существующих на сервере для
    POST запросов с url */api/members*;
* *server REQUEST_RESPONSES*
  * *all REQUEST_RESPONSES* - возвращает список request-response, выполнявшихся на сервере;
* *EXP: insertMember(member) <- POST /api/members* 
  * *EXP:001 (new)*
    * ***add EXP:001 insertMember(member) <- POST /api/members*** - добавление на сервер нового expectation по вставке нового
      member;
    * ***del EXP:001 insertMember(member) <- POST /api/members*** - удаление expectation. (Примечание. В запросе нужно
      переопределить поле *id*, выполнив запрос *'/api/members ACTIVE_EXPECTATIONS'* - PUT запрос с url
      *http://127.0.0.1:8090/mockserver/retrieve?type=ACTIVE_EXPECTATIONS* и с body
      *{ "path": "/api/members","method": "POST" }*. В полученном ответе нужно найти expectation соответствующее
      нашему удаляемому запросу c body '{ "memberId": null, "name": "mashTest" }';
    * ***test EXP:001*** - тестовый запрос проверяющий добавленный expectation.
  * *EXP: 002 (from ExpectationInitializer.java)*
    * ***test EXP:002*** - тестовый запрос проверяющий expectation по вставке нового member, определенный в файле
      [***ExpectationInitializerImpl.java***](src/main/java/ua/mai/zyme/mockserver/r2dbcmysql/ExpectationInitializerImpl.java).
  * *EXP: 003 (from initializerJson.json)*
    * ***test EXP:003*** - тестовый запрос проверяющий expectation по вставке нового member, определенный в файле
      [***initializerJson.json***](src/main/resources/initializerJson.json).
* *EXP: findByMemberId(id) <- GET /api/members/{id}*
  * *EXP: 040 (from initializerJson.json)*
    * ***test EXP:003*** - тестовый запрос проверяющий expectation по поиску member по его id, определенный в файле
      [***initializerJson.json***](src/main/resources/initializerJson.json). 
      