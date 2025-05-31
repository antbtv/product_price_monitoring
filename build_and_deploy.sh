#!/bin/bash

# Скрипт для сборки и развертывания WAR из модульного Maven-проекта

# Конфигурация
PROJECT_ROOT=$(pwd)                  # Корень проекта
MODULE_NAME="product-catalog-app"    # Имя модуля с WAR
WAR_FILE="product-catalog.war"       # Имя WAR-файла
# НИЖЕ УКАЖИТЕ ВАШ ПУТЬ
DEPLOY_DIR="/home/anton/Work/apache-tomcat-10.1.39/webapps" # Куда развертывать (например Tomcat)

# 1. Сборка проекта
echo "Сборка проекта с помощью Maven..."
mvn clean package

# Проверка успешности сборки
if [ $? -ne 0 ]; then
    echo "Ошибка сборки Maven!"
    exit 1
fi

# 2. Поиск WAR-файла
WAR_PATH="$PROJECT_ROOT/$MODULE_NAME/target/$WAR_FILE"

if [ ! -f "$WAR_PATH" ]; then
    echo "WAR-файл не найден по пути: $WAR_PATH"
    echo "Возможные причины:"
    echo "1. Неправильное имя модуля ($MODULE_NAME)"
    echo "2. Неправильное имя WAR-файла ($WAR_FILE)"
    echo "3. Ошибка в пути"
    exit 1
fi

# 3. Развертывание
echo "Найден WAR-файл: $WAR_PATH"
echo "Копирование в $DEPLOY_DIR ..."

cp "$WAR_PATH" "$DEPLOY_DIR" || {
    echo "Ошибка копирования WAR-файла!"
    echo "Проверьте:"
    echo "1. Существует ли $DEPLOY_DIR"
    echo "2. Есть ли права на запись"
    exit 1
}

echo "Готово! Приложение развернуто в $DEPLOY_DIR"