#!/bin/bash

# Скрипт инициализации БД PostgreSQL

# Параметры (настройте под вашу среду)
DB_HOST="localhost"
DB_PORT="5432"
DB_NAME="test_dbase"
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

# 3. Применяем DDL
echo "Применение DDL-скриптов..."
psql -h $DB_HOST -p $DB_PORT -U $DB_USER -d $DB_NAME -a -f "$SQL_DIR/DDL.sql"

# 4. Применяем DML
echo "Применение DML-скриптов..."
psql -h $DB_HOST -p $DB_PORT -U $DB_USER -d $DB_NAME -a -f "$SQL_DIR/DML.sql"

echo "Инициализация БД завершена успешно!"