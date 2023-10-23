# Zyme

Проект для сборки решений по различным технологиям

[goals](goals/README.md)

## Технологии в проекте *zyme*
* Работа с транзациями - [*DbTransactionApplication*](#dbtransactionapplication)
* JOOQ - [*DbJooqApplication*](#dbjooqapplication)
* REST - [*RestLibApplication*](#restlibapplication)
* Vaadin - [*VaadinSimpleApplication*](#vaadinsimpleapplication)
* [Vaadin 8](vaadin8/README.md)

## Организация и структура проекта *zyme*


* **goals**  
  * **db** - примеры для БД.
    * **db-app** - Spring приложения для БД.
      * **db-app-general** - Spring приложения, связанные с БД.
          * [***DbTransactionApplication***](#dbtransactionapplication).java - примеры работы с транзакциями (*TransactionManager*,
            задание транзакций анотациями, *TransactionTemplate* + JOOQ, *JdbcTemplate*).
          * [***DbJooqApplication***](#dbjooqapplication).java - приложение для примеров с JOOQ.
          * [***DbJdbcTemplateApplication***](#dbjdbctemplateapplication).java - приложение для примеров с *JdbcTemplate*.
    * **depl** - создание и наполнение БД
      * **database** - создание и наполнение MySQL БД схема zyme
      * **database02** - создание и наполнение MySQL БД схема zyme02
    * **modules**
      * **[aa-schema-gen](#module-aa-schema-gen)** - модуль для генерации схемы JOOQ для MySQL БД *zyme*
      * **[aa-schema-gen2](#module-aa-schema-gen2)** - модуль для генерации схемы JOOQ для MySQL БД *zyme02*
      * **[zo-schema-gen](#module-zo-schema-gen)** - модуль для генерации схемы JOOQ для Zoo приложения в MySQL БД *zyme*
      * **db-common** - модуль содержащий классы с общей функциональностью для БД.
      * **[db-configuration](#module-db-configuration)** - определение бинов для работы с *zyme* и *zyme02* (TransactionManaher, TransactionTemplate,
        DSLConfig).
      * **[db-jooq](#module-db-jooq)** - примеры для JOOQ.
      * **[db-transaction](#module-db-transaction)** - примеры для транзакций, описанных через аннотации или использующие *TransactionTemplate*. 
  * **framework** - модули, использующиеся в проекте.
    * **common** - 

  * **security** - примеры для Spring Security.
    * **security-app** - Spring приложения использующие Security.
      * **security-app-basic** - Spring Security приложения, использующие Basic авторизацию.
        * [***SecurityBasicOrKeyApplication***](#securitybasicorkeyapplication).java - Spring Security приложение, использующие Basic авторизацию, фильтры.
      * **security-app-login** - Spring Security приложения, использующие авторизацию с использованием формы Login.
        * [***SecurityLoginApplication***](#securityloginapplication).java - Spring Security приложение, использующее
          авторизацию с использованием формы Login.
    * **modules**
      * **[security-common](#security-common)**
      * **[security-basic](#security-basic)**
      * **[security-login](#security-login)**

  * **rest** - примеры для REST.
      * **rest-app** - Spring приложения для REST.
          * **rest-app-lib** - Spring приложения, связанные с REST Library.
              * [***RestLibApplication***](#restlibapplication).java - приложение REST Library.
    * **modules**
        * **[rest-lib](#module-rest-lib)**

  * **vaadin** - примеры для Vaadin.
      * **vaadin-app** - Spring приложения для Vaadin.
          * **vaadin-app-simple** - Spring приложения, связанные с Vaadin.
              * [***VaadinSimpleApplication***](#vaadinsimpleapplication).java - приложение для примеров с Vaadin.



## *DbTransactionApplication*

В этом приложении показаны различные варианты работы с транзакциями.<br>
Приведены примеры создания и использование бинов типа *TransactionManager*, *TransactionTemplate*, *DataSource*,
*DSLContext*, которые используются для работы с разными объектами БД в транзакциях.


### Module *db-configuration*

В модуле *db-configuration* осуществляется настройка и создание бинов для доступа к базам данных.

* [***DbConfig***](goals/db/db-modules/db-configuration/src/main/java/ua/mai/zyme/db/config/DbConfig.java)
  содержит **конфигурацию модуля** *db-configuration*.<br>
  Если модуль *db-configuration* включается в какое-то
  приложение, то в аннотации *@SpringBootApplication* в *scanBasePackageClasses* этого приложения должен
  быть указан этот класс.

#### Дефолтная спринговая конфигурация (используется для доступа к БД *zyme*).
* [***JdbcConfigurationDefaultSettings***](goals/db/db-modules/db-configuration/src/main/java/ua/mai/zyme/db/config/JdbcConfigurationDefaultSettings.java)
    содержит **названия бинов** для дефолтной конфигурации.
* [***JdbcConfigurationDefault***](goals/db/db-modules/db-configuration/src/main/java/ua/mai/zyme/db/config/JdbcConfigurationDefault.java)
    содержит **бины** для дефолтной конфигурации:
  - j

#### Своя конфигурация (используется для доступа к БД *zyme02*).
* [***JdbcConfigurationZyme02Settings***](goals/db/db-modules/db-configuration/src/main/java/ua/mai/zyme/db/config/JdbcConfigurationZyme02Settings.java)
  содержит **названия бинов** для своей (БД *zyme02) конфигурации.
* [***JdbcConfigurationZyme02***](goals/db/db-modules/db-configuration/src/main/java/ua/mai/zyme/db/config/JdbcConfigurationZyme02.java)
  содержит **бины** для своей (БД *zyme02) конфигурации.


### Module *db-transaction*

В модуле *db-transaction* реализуются различные кейсы работы с транзакциями.

* [***DbTransactionConfig***](goals/db/db-modules/db-transaction/src/main/java/ua/mai/zyme/db/transaction/config/DbTransactionConfig.java)
  содержит **конфигурацию модуля** *db-transaction*.<br>
  Если модуль *db-transaction* включается в какое-то приложение, то в аннотации *@SpringBootApplication* в
  *scanBasePackageClasses* этого приложения должен быть указан этот класс.


### Кейсы приложения (сервисы):
* [***JooqTransactionAnnotatedServiceDefault***](goals/db/db-modules/db-transaction/src/main/java/ua/mai/zyme/db/transaction/JooqTransactionAnnotatedServiceDefault.java) -
  сервис для работы с БД *zyme*, в котором:
    - используется JOOQ с **дефолтным** *dslContext* (БД *zyme*).
    - транзакционность задается аннотациями, использующими **дефолтный** *transactionManager*.
* [***JooqTransactionAnnotatedServiceZyme02***](goals/db/db-modules/db-transaction/src/main/java/ua/mai/zyme/db/transaction/JooqTransactionAnnotatedServiceZyme02.java) -
  сервис для работы с БД *zyme02*, в котором:
    - используется JOOQ со **своим глобальным** *dslContextZyme02* (БД *zyme02*);
    - транзакционность задается аннотациями, использующими **свой глобальный** *transactionManagerZyme02*.
* [***JooqTransactionTemplateServiceDefault***](goals/db/db-modules/db-transaction/src/main/java/ua/mai/zyme/db/transaction/JooqTransactionTemplateServiceDefault.java) -
  сервис для работы с БД *zyme*, в котором:
    - используется JOOQ с **дефолтным** *dslContext* (БД *zyme*);
    - используется **дефолтный** *transactionManager*;
    - транзакционность обеспечивается **дефолтным** *transactionTemplate* или **новым** *TransactionTemplate*
      (использующим <i>transactionManager</i>).
* [***JooqTransactionTemplateServiceZyme02***](goals/db/db-modules/db-transaction/src/main/java/ua/mai/zyme/db/transaction/JooqTransactionTemplateServiceZyme02.java) -
  сервис для работы с БД *zyme02*, в котором:
    - используется JOOQ со **своим** *dslContextZyme02* (БД *zyme02*);
    - используется **свой глобальный** *transactionManagerZyme02*;
    - транзакционность обеспечивается **своим глобальным** *transactionTemplateZyme02* или **новым**
      *TransactionTemplate* (использующим *transactionManagerZyme02*).
* [***JdbcTemplateAnnotatedServiceDefault***](goals/db/db-modules/db-transaction/src/main/java/ua/mai/zyme/db/transaction/JdbcTemplateAnnotatedServiceDefault.java) -
  сервис для работы с БД *zyme*, в котором:
    - используются **дефолтный** *jdbcTemplate* или **новый** *JdbcTemplate* или **дефолтный** *namedParameterJdbcTemplate*
      или **новый** *NamedParameterJdbcTemplate*;
    - транзакционность задается аннотациями, использующими **дефолтный** *transactionManager*.
* [***JdbcTemplateAnnotatedServiceZyme02***](goals/db/db-modules/db-transaction/src/main/java/ua/mai/zyme/db/transaction/JdbcTemplateAnnotatedServiceZyme02.java) -
  сервис для работы с БД *zyme02*, в котором:
    - используются **свой глобальный** *jdbcTemplateZyme02* или **новый** *JdbcTemplate*;
    - транзакционность задается аннотациями, использующими свой **глобальный** *transactionTemplateZyme02*.
* [***JdbcTemplateTransactionTemplateServiceDefault***](goals/db/db-modules/db-transaction/src/main/java/ua/mai/zyme/db/transaction/JdbcTemplateTransactionTemplateServiceDefault.java) -
  сервис для работы с БД *zyme*, в котором:
    - используются **дефолтный** *jdbcTemplate* или **новый** *JdbcTemplate* или **дефолтный** *namedParameterJdbcTemplate*
      или **новый** *NamedParameterJdbcTemplate*;
    - используется **дефолтный** *transactionManager*;
    - транзакционность обеспечивается **дефолтным** *transactionTemplate* или **новым** *TransactionTemplate*.
* [***JdbcTemplateTransactionTemplateServiceZyme02***](goals/db/db-modules/db-transaction/src/main/java/ua/mai/zyme/db/transaction/JdbcTemplateTransactionTemplateServiceZyme02.java) -
  сервис для работы с БД *zyme02*, в котором:
    - используются **свой** *jdbcTemplateZyme02* или **новый** *JdbcTemplate* или **дефолтный** *namedParameterJdbcTemplateZyme02*
      или **новый** *NamedParameterJdbcTemplate*;
    - используется **свой** *transactionManagerZyme02* ;
    - транзакционность обеспечивается **своим** *transactionTemplateZyme02* или **новым** *TransactionTemplate*.


## *DbJooqApplication*

В этом приложении показаны различные варианты работы с использованием JOOQ.<br>

### Используемые модули
*[db-configuration](#module-db-configuration)*
*[db-jooq](#module-db-jooq)*

### Module *db-jooq*

В модуле *db-jooq* реализуются различные кейсы работы с JOOQ.
* [***DbJooqConfig***](goals/db/db-modules/db-jooq/src/main/java/ua/mai/zyme/db/jooq/config/DbJooqConfig.java)
  содержит **конфигурацию модуля** *db-jooq*.<br>
  Если модуль *db-jooq* включается в какое-то приложение, то в аннотации *@SpringBootApplication* в
  *scanBasePackageClasses* этого приложения должен быть указан этот класс.

### Кейсы приложения:
* [***JooqSelectSqlService***](goals/db/db-modules/db-jooq/src/main/java/ua/mai/zyme/db/jooq/JooqSelectSqlService.java) -
  примеры SELECT операций в JOOQ.
* [***JooqSelectNativeSqlService***](goals/db/db-modules/db-jooq/src/main/java/ua/mai/zyme/db/jooq/JooqSelectNativeSqlService.java) -
  примеры SELECT операций в JOOQ, использующих нативный SQL.
* [***JooqSelectCursorSqlService***](goals/db/db-modules/db-jooq/src/main/java/ua/mai/zyme/db/jooq/JooqSelectCursorSqlService.java) -
  примеры SELECT операций в JOOQ, использующих <i>Cursor</i>.
* [***JooqInsertSqlService***](goals/db/db-modules/db-jooq/src/main/java/ua/mai/zyme/db/jooq/JooqInsertSqlService.java) -
  примеры INSERT операций в JOOQ.
* [***JooqInsertBatchSqlService***](goals/db/db-modules/db-jooq/src/main/java/ua/mai/zyme/db/jooq/JooqInsertBatchSqlService.java) -
  примеры INSERT операций в JOOQ, использующие batch().
* [***JooqInsertBulkSqlService***](goals/db/db-modules/db-jooq/src/main/java/ua/mai/zyme/db/jooq/JooqInsertBulkSqlService.java) -
  примеры INSERT операций в JOOQ, использующие bulk - один оператор INSERT.
* [***JooqUpdateSqlService***](goals/db/db-modules/db-jooq/src/main/java/ua/mai/zyme/db/jooq/JooqUpdateSqlService.java) -
  примеры UPDATE операций в JOOQ.
* [***JooqDeleteSqlService***](goals/db/db-modules/db-jooq/src/main/java/ua/mai/zyme/db/jooq/JooqDeleteSqlService.java) -
  примеры UPDATE операций в JOOQ.
* [***JooqCopyService***](goals/db/db-modules/db-jooq/src/main/java/ua/mai/zyme/db/jooq/JooqCopyService.java) -
  примеры копирования данных из одной схемы в другую.
* [***JooqConvertersService***](goals/db/db-modules/db-jooq/src/main/java/ua/mai/zyme/db/jooq/JooqConvertersService.java) -
  примеры использования конверторов в JOOQ.

### Module *aa-schema-gen*

Модуль *aa-schema-gen* используется для генерации файла *information_schema.xml* для MySQL БД *zyme*, на базе которого
генерируется схема JOOQ.

### Module *aa-schema-gen2*

Модуль *aa-schema-gen2* используется для генерации файла *information_schema.xml* для MySQL БД *zyme2*, на базе которого
генерируется схема JOOQ.


## *DbJdbcTemplateApplication*

В этом приложении показаны различные варианты работы с использованием *JdbcTemplate*.<br>

### Используемые модули
* *[db-configuration](#module-db-configuration)*
* *[db-jdbc-template](#module-db-jdbc-template)*

### Module *db-jdbc-template*

В модуле *db-jdbc-template* реализуются различные кейсы работы с *JdbcTemplate*.
* [***DbJdbcTemplateConfig***](goals/db/db-modules/db-jooq/src/main/java/ua/mai/zyme/db/jooq/config/DbJdbcTemplateConfig.java)
  содержит **конфигурацию модуля** *db-jdbc-template*.<br>
  Если модуль *db-jdbc-template* включается в какое-то приложение, то в аннотации *@SpringBootApplication* в
  *scanBasePackageClasses* этого приложения должен быть указан этот класс.

### Кейсы приложения:
* [***JdbcTemplateInsertSqlService***](goals/db/db-modules/db-jdbc-template/src/main/java/ua/mai/zyme/db/jdbc/JdbcTemplateInsertSqlService.java) -
  примеры INSERT операций для *JdbcTemplate* и *NamedParameterJdbcTemplate*.
* [***JdbcTemplateUpdateSqlService***](goals/db/db-modules/db-jdbc-template/src/main/java/ua/mai/zyme/db/jdbc/JdbcTemplateUpdateSqlService.java) -
  примеры UPDATE операций для *JdbcTemplate* и *NamedParameterJdbcTemplate*.
* [***JdbcTemplateDeleteSqlService***](goals/db/db-modules/db-jdbc-template/src/main/java/ua/mai/zyme/db/jdbc/JdbcTemplateDeleteSqlService.java) -
  примеры DELETE операций для *JdbcTemplate* и *NamedParameterJdbcTemplate*.
* [***JdbcTemplateSelectSqlService***](goals/db/db-modules/db-jdbc-template/src/main/java/ua/mai/zyme/db/jdbc/JdbcTemplateSelectSqlService.java) -
  примеры SELECT операций для *JdbcTemplate* и *NamedParameterJdbcTemplate*.
* [***JdbcTemplateInsertBatchSqlService***](goals/db/db-modules/db-jdbc-template/src/main/java/ua/mai/zyme/db/jdbc/JdbcTemplateInsertBatchSqlService.java) -
  примеры batch INSERT операций для *JdbcTemplate* и *NamedParameterJdbcTemplate*.


## *SecurityBasicOrKeyApplication*

Приложение [***SecurityBasicOrKeyApplication***](goals/security/security-app/security-app-basic/src/main/java/ua/mai/zyme/security/boot/SecurityBasicOrKeyApplication.java)
реализует аутентификацию через Basic или Static Key.


### Используемые модули
* *[security-basic](#module-security-basic)*
* *[security-common](#module-security-common)*
* *[db-configuration](#module-db-configuration)*

### Module *security-basic*

В модуле *security-basic* реализуются .
* [***SecurityConfigBasic.java***](goals/security/security-modules/security-basic/src/main/java/ua/mai/zyme/security/config/SecurityConfigBasic.java)
  содержит **конфигурацию модуля** *security-basic*.<br>
  Если модуль *security-basic* включается в какое-то приложение, то в аннотации *@SpringBootApplication* в
  *scanBasePackageClasses* этого приложения должен быть указан этот класс.

### Module *security-common*
В модуле *security-common* реализуются общие компоненты для Security приложений.


## *RestLibApplication*

### Module *rest-lib*

В модуле *rest-lib* реализуются .

* [***RestLibConfig***](goals/rest/rest-modules/rest-lib/src/main/java/ua/mai/zyme/rest/lib/config/RestLibConfig.java)
  содержит **конфигурацию модуля** *rest-lib*.<br>
  Если модуль *rest-lib* включается в какое-то приложение, то в аннотации *@SpringBootApplication* в
  *scanBasePackageClasses* этого приложения должен быть указан этот класс.


## *VaadinSimpleApplication*





### Git Pull with Submodule
For a repo with submodules, we can pull all submodules using

    git submodule update --init --recursive

for the first time. All submodules will be pulled down locally.

To update submodules, we can use

    git submodule update --recursive --remote

or simply

    git pull --recurse-submodules

### Git ADD Submodule
For a repo with submodules, we can add submodule using

    git submodule add https://bitbucket.telesens.ua/scm/rbtvfua/mysql-data-import.git

### Deployment
* **Docker** performs operating-system-level virtualization also known as containerization.


### System requirements

* **OS** - any which supports Docker (Linux is recommended)
* **RAM** - 24 Gb minimum (32 Gb recommended)
* **CPU** - 8 cores minimum (16 cores recommended)
* **Storage** - 20 Gb minimum (SSD)

### Installation steps

- clone this project:
```sh
$ git clone https://bitbucket.telesens.ua/scm/rbtvfua/rbt-vodafone-ua.git 
```
- create network for work docker stack:
```sh
$ docker network create --driver overlay rbt-net 
```

By default, the stack exposes the following ports:

* **8081**: Admin UA
    * Credential: (admin/1)
* **8080**: Core API
    * Credential: (admin/1)
* **3306**: Percona DB
    * Credential: (trt/root)

### Authors

Team Telesens
