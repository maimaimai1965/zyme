# library
Модуль, в котором реализуется репозиторий Library. 

Все данные существуют в памяти в классе *LibraryRepository* (не хранятся в БД). Они создаются при старте пограммы, и
хранятся в виде списков DTO объектов. Методы *LibraryRepository* используют в качестве параметров DTO объекты. 

Для доступа к репозиторию используется *LibraryService*. В методах *LibraryService* вызываются методы *LibraryRepository*
с конвертаций их параметров в соответствующие DTO объекты.

Для использования этого модуля в проекте необходимо добавить файл конфигурации этого модуля *LibraryConfig.class* в
секцию *@Import* этого приложения.
