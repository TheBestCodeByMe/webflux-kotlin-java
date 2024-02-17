# Описание

Репозиторий содержит разделяемые ресурсы(проекты) группы сервисов **SmartSafeSchool Services (SSS Services)**.
От этого репозитория зависят все основные сервисы:

* [eureka-reg-server](https://git2.ulia.com/smartsafeschool/backend/somes/cloud_layer/eureka_reg_server) -
  сервер обнаружение, регистрации сервисов.
* [auth-server](https://git2.ulia.com/smartsafeschool/backend/somes/auth_layer/auth_server) - сервер работы с
  JWT
* [smart-safe-school](https://git2.ulia.com/smartsafeschool/backend/somes/sss_layer/smart_safe_school_server) -
  сервер ведения основных сущностей (таких как `user`, `school`, `role` и т.д.)
* [smart-safe-school-store](https://git2.ulia.com/smartsafeschool/backend/somes/store_layer/store_server) -
  сервис магазина
* `(new_service)` - (описание нового сервиса)

## Сборка проекта

Как упоминалось ранее, сборка основных сервисов невозможна без этого репозитория. Рассмотрим пример сборки на
сервисе `auth-server`.

1. Выполнить клонирование репозитория с разделяемыми
   ресурсами `git clone https://git2.ulia.com/smartsafeschool/backend/somes/somes.git`
2. Перейти в каталог репозитория `cd ./somes`
3. Выполнить клонирование репозитория `auth-server`
   командой `git clone https://git2.ulia.com/smartsafeschool/backend/somes/auth_layer/auth_server.git auth-server`
   **ВАЖНО** при клонировании указать каталог назначения имя которого соответсвует имени
   в `somes/settings.gradle`
4. Выполнить `./gradlew :auth-server:build -x test` для сборки `auth-server` либо `./gradlew :build -x test` для сборки
   всех проектов

## Работа в IDEA

1. Если были произведены изменения в разных репозиториях (например в `somes` и `auth-server`) , то при коммите
   затрагивающем несколько репозиториев
   IDEA выполнит соответсвующий коммит с указанным комментарием во все эти репозитории (нет необходимости для каждого
   репозитория повторять действия)

## Запуск сервисов

TODO

## Быстрый старт

1. `git clone https://git2.ulia.com/smartsafeschool/backend/somes/somes.git`
2. `cd ./somes`
3. `git clone https://git2.ulia.com/smartsafeschool/backend/somes/auth_layer/auth_server.git auth-server`
4. `git clone https://git2.ulia.com/smartsafeschool/backend/somes/sss_layer/smart_safe_school_server.git smart-safe-school`
5. `git clone https://git2.ulia.com/smartsafeschool/backend/somes/store_layer/store_server.git smart-safe-school-store`
6. `git clone https://git2.ulia.com/smartsafeschool/backend/somes/cloud_layer/eureka_reg_server.git eureka-reg-server`
