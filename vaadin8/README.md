# Vaadin 8

Проект по Vaadin 8

## Организация и структура проекта *vaadin8*

* **vaadin8**  
  * **vaadin8-app** - Spring приложения для Vaadin 8.
    * **vaadin8-app-simple** - приложение.
        * [***Vaadin8SimpleApplication***](#Vaadin8SimpleApplication).java
    * **modules**
      * **[vaadin8-ui](#vaadin8-ui)** - модуль UI 
      * **[vaadin8-theme](#vaadin8-theme)** - модуль содержащий тему для приложения
  * **[vaadin8-widgets](#vaadin8-widgets)** - модуль, содержащий widgetset использующийся в проекте.


## Сборка и запуск проекта

* *clean/install* модуля *vaadin8-widgets*.<br> 
  Если изменений в этом модуле нет, то при повторной сборке проекта выполнять этот этап не нужно (widgetset
  не изменяется).   
* *clean/install* всего проекта *vaadin8*.
* запускаем приложение [Vaadin8SimpleApplication.java](#vaadin8-app\vaadin8-app-simple\src\main\java\ua\mai\zyme\vaadin8\boot\Vaadin8SimpleApplication.java).
* в браузере вводим http://localhost:8088/


## *Vaadin8SimpleApplication*

В этом приложении реализован UI на Vaadin 8.

## *Модули*

### *vaadin8-ui*

### *vaadin8-theme*

### *vaadin8-widgets*

