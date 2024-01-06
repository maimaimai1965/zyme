# mysql8  

Контейнер MySQL 8

### Installation steps

Необходимо в начале создать network *zyme-net*:

$ docker network create --driver overlay zyme-net

Выполняем [docker-compose.yaml](docker\docker-compose.yaml) 

### By default, the stack exposes the following ports:

* **3316**: MySQL 8
    * Credential: (root/root)

### Authors

mai
