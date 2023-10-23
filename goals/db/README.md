# db  
[zyme](../README.md)


[db](db/README.md)


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
