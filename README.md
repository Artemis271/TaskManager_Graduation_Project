# TaskManager
Приложение для создания и отслеживания своих задач, имея чат для обсуждения, клиентскую и серверную часть

## Архитектура
![](/github/Task-Manager-ахритектура.png)

## Используемые технологии
![](https://skillicons.dev/icons?i=java,idea,spring,postgres,mongo)

![](https://skillicons.dev/icons?i=docker,kafka,postman,maven,redis)

![](https://skillicons.dev/icons?i=vscode,nodejs,react,css,js)

## Фичи приложения
* В качестве безопасности используются JWT токены
* Возможность авторизации по протоколу OAuth2
* Микросервисная архитектура
* Реализован групповой чат между пользователями с помощью протокола WebSocket и STOMP

## Старт
### Локальный старт подразумевает, что на компьютере уже установлен PostgreSQL и NodeJS!
1) #### ***Локальный старт***
* Клонировать репозиторий `git clone https://github.com/Yuranium/TaskManager.git`
* Открыть через IDE
* Скачать необходимые сервисы с помощью команды `docker-compose up --build`
* Установить все необходимые для каждого сервиса переменные окружения
* Настроить схему БД (схема к каждому сервису есть в директории resources)
* Запустить клиентскую часть, перейдя в директорию **react_application** и 
ввести команду `npm start`
* Запустить все бэкенд сервисы на Java Spring Boot
* Перейти на **localhost:3000**

2) #### ***Старт через Docker***
* Клонировать репозиторий `git clone https://github.com/Yuranium/TaskManager.git`
* Создать файл .env и заполнить его в формате .env.example в корне проекта
* Скачать необходимые сервисы с помощью команды `docker-compose up --build`
* Перейти на **localhost:3000**

## Демо
1) Главная страница  
   ![](github/main-page.png)
2) Страница логина  
   ![](github/login-page.png)
3) Страница регистрации  
   ![](github/register-page.png)
4) Страница профиля пользователя  
   ![](github/account-page.png)
5) Страница группового веб-чата  
   ![](github/web-chat-page.png)
6) Страница проектов  
   ![](github/project-page.png)
7) Страница задач  
   ![](github/tasks-page.png)
8) Кастомное модальное окно  
   ![](github/modal-window-page.png)
9) Инфографика по задачам и проектам  
   ![](github/charts-page.png)
10) Документация эндпоинтов через openAPI  
    ![](github/open-api-1.png)  
    ![](github/open-api-2.png)
