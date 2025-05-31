#!/bin/bash

# Скрипт инициализации БД PostgreSQL

# Параметры (настройте под вашу среду)
DB_HOST="localhost"
DB_PORT="5432"
DB_NAME="product_catalog"
DB_USER="postgres"
DB_PASSWORD="postgres"
SQL_DIR="sql"  # Папка с SQL-скриптами

# 1. Проверяем наличие psql
if ! command -v psql &> /dev/null; then
    echo "PostgreSQL client (psql) не установлен!"
    exit 1
fi

# 2. Проверяем наличие SQL-файлов
if [ ! -f "$SQL_DIR/DDL.sql" ] || [ ! -f "$SQL_DIR/DML.sql" ]; then
    echo "SQL-файлы не найдены в папке $SQL_DIR!"
    exit 1
fi

# 3. Проверяем существование БД и создаем при необходимости
echo "Проверка существования БД $DB_NAME..."
if ! PGPASSWORD=$DB_PASSWORD psql -h $DB_HOST -p $DB_PORT -U $DB_USER -lqt | cut -d \| -f 1 | grep -qw $DB_NAME; then
    echo "БД $DB_NAME не существует, создаем..."
    PGPASSWORD=$DB_PASSWORD psql -h $DB_HOST -p $DB_PORT -U $DB_USER -c "CREATE DATABASE $DB_NAME;"
    if [ $? -ne 0 ]; then
        echo "Ошибка при создании БД $DB_NAME!"
        exit 1
    fi
    echo "БД $DB_NAME успешно создана."
else
    echo "БД $DB_NAME уже существует."
fi

# 4. Применяем DDL
echo "Применение DDL-скриптов..."
PGPASSWORD=$DB_PASSWORD psql -h $DB_HOST -p $DB_PORT -U $DB_USER -d $DB_NAME -a -f "$SQL_DIR/DDL.sql"
if [ $? -ne 0 ]; then
    echo "Ошибка при выполнении DDL-скриптов!"
    exit 1
fi

# 5. Применяем DML
echo "Применение DML-скриптов..."
PGPASSWORD=$DB_PASSWORD psql -h $DB_HOST -p $DB_PORT -U $DB_USER -d $DB_NAME -a -f "$SQL_DIR/DML.sql"
if [ $? -ne 0 ]; then
    echo "Ошибка при выполнении DML-скриптов!"
    exit 1
fi

echo "Инициализация БД $DB_NAME завершена успешно!"