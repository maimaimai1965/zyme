# graphql-learn

Проект по изучению GraphQl

Используются:
* библиотека [graphql-datetime-spring-boot-starter](https://github.com/tailrocks/graphql-java-datetime), которая
  определяет новые дополнительные типы данных;
* библиотека [graphql-java-extended-scalars](https://github.com/graphql-java/graphql-java-extended-scalars), которая определяет новые дополнительные типы данных;
* своя библиотека [library](../library/README.md), которая реализуется репозиторий Library;


Для приложения не используется БД.

## Организация и структура проекта *graphql-learn*

* **graphql-learn**  
  * [***GraphqlLearnApplication***](#vaadin8simpleapplication).java

## Сборка и запуск проекта

* *clean/install* модуля *graphql-learn*. 
* запускаем приложение [GraphqlLearnApplication.java.java](#vsrc/main/java/ua/mai/graphql/learn/GraphqlLearnApplication.java).
* в браузере вводим http://localhost:8203/graphiql?path=/graphql - откроется приложение *graphiql*, в котором можно
  просмотреть схему GraphQl, ввести GraphQl. Доступность приложения *graphiql* определяется параметром
  *spring.graphql.graphiql.enabled*.
* (Сейчас не работает!!!) как альтернатива предыдущему можно открыть GraphQl Playground и внем задать адрес http://127.0.0.1:8203/graphql. Там 
  так же можно просмотреть схему GraphQl, ввести GraphQl.
* Запросы можно выполнять в Postman. Запросы находятся в [902 Zyme GraphQL.postman_collection.json](src/test/resources/postman/902 Zyme GraphQL.postman_collection.json),
  environment для них в [localhost.postman_environment.json](src/test/resources/postman/localhost.postman_environment.json).


## *GraphqlLearnApplication*

В этом приложении реализован .

## *Модули*


