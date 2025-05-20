# Product catalog

## Установка и развертывание

### 1. Сборка приложения
```bash
# Перед запуском откройте файл и замените используемые переменные
./build_and_deploy.sh
```

### 2. Инициализация БД
1. Создайте БД в PostgreSQL:
```sql
CREATE DATABASE your_db_name;
```

2. Запустите скрипт инициализации:
```bash
./init_db.sh
```

### 3. Настройка приложения
Создайте файл конфигурации `config/application.properties`:
```properties
datasource.url=jdbc:postgresql://localhost:5432/your_db_name
datasource.username=your_db_user
datasource.password=your_db_password
```

### 4. Развертывание
Для WAR:
- Скопируйте `target/your-app.war` в `$CATALINA_HOME/webapps/`
- Или используйте скрипт `build_and_deploy.sh`

Для самостоятельного запуска:
```bash
java -jar target/your-app.jar
```

## Структура проекта
```
project/
├── src/            # Исходный код
├── sql/            # SQL-скрипты
├── config/         # Конфигурационные файлы
├── target/         # Собранные артефакты
├── build_and_deploy.sh  # Скрипт сборки
├── init_db.sh      # Скрипт инициализации БД
└── README.md       # Эта документация
```

## Миграции БД
Для обновления схемы БД:
1. Создайте новый SQL-файл в `sql/migrations/V{версия}__{описание}.sql`
2. Примените:
```bash
psql -U your_db_user -d your_db_name -a -f sql/migrations/V{версия}__{описание}.sql
```

## Устранение неполадок
- **Ошибка подключения к БД**: Проверьте параметры в `application.properties`
- **Ошибка сборки**: Убедитесь, что Maven установлен (`mvn -v`)
- **Ошибка развертывания**: Проверьте права на запись в папку webapps

## Контакты
Для вопросов: antonbut48@gmail.com