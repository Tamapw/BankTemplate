Инструкция
============

Запуск приложения:
------------------------
Запуск приложения осуществляется посредством командной строки.
```
$java -jar BankTemplate-1.00.jar
```

Использование приложения:
------------------------

После запуска приложения вам будет доступен ввод команд.
Список команд:
  - help - выводит информацию о доступных командах в системе.
    - Example: help
  - create name - создаёт банк с именем name.
    - Example: create sbt
  - start name - запускает банк с именем name.
    - Example: start sbt
  - print -b - выводит на экран список всех существующих банков.
    - Example: print -b
  - exit - останавливает банк, если таковой запущен и выходит из приложения.
    - exit
    
После запуска банка так же добавляются такие команды:
  - transfer from to money - переводит количество money средств с счёта from на счет to. Счета вводятся в виде "id-code", где id - уникальный счёт в банке code.
    - Example: transfer 123-2 312-2 500
  - add -a count - добавляет в банк количество count клиентских аккаунтов с разным количеством денег.
    - Example: add -a 12
  - add -p money - добавляет в банк один клиентский аккаунт с количеством денег money.
    - Example: add -p 1123
  - print -a - выводит на экран все существующие счета. Вначале выводятся счета клиентов, затем счета корреспондентов.
    - Example: print -a
  - stop - останавливает работу банка, запущенного раннее.
    - Example: stop
    

