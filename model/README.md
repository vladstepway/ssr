# sedd-audit

Модуль модели суперсервиса.

# Сборки
## Сборка проекта
```
mvn clean install -P build-bash -P dev -P ugdev -Dnsi.rest.url=http://172.27.5.18/mdm --settings C:\m2\cdp-settings.xml -X -f pom.xml
```
## Генерация java классов по xsd схеме
Xsd схемы должны быть указаны в pom.xml
```
mvn jaxb2:xjc
```
```
docker run --env LICENSE=accept --env MQ_QMGR_NAME=QM1 --publish 1414:1414 --publish 9443:9443 ibmcom/mq
```
