# Product catalog

## Установка и развертывание

### 1. Настройка приложения
Создайте файл конфигурации `application.properties`:
```properties
db.driver=org.postgresql.Driver
db.url=jdbc:postgresql://localhost:5432/your_db_name
db.username=your_db_user
db.password=your_db_password
hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
hibernate.hbm2ddl.auto=update
hibernate.show_sql=true
hibernate.format_sql=true
jwt.secret.key=Senla
```

### 2. Инициализация БД
Запустите скрипт инициализации, предварительно заменив используемые переменные:
```bash
# Перед запуском откройте файл и замените используемые переменные
./init_db.sh
```

### 3. Сборка приложения
Запустите скрипт сборки приложения, предварительно заменив используемые переменные
```bash
# Перед запуском откройте файл и замените используемые переменные
./build_and_deploy.sh
```

## Структура проекта
```
product_price_monitoring/
├── product-catalog-app/            # Исходный код
├── product-catalog-common/
├── product-catalog-core/            
├── product-catalog-indrastructure/            
├── sql/                            # SQL-скрипты
├── build_and_deploy.sh             # Скрипт сборки
├── init_db.sh                      # Скрипт инициализации БД
└── README.md                       # Эта документация
└── pom.xml      
```

## Устранение неполадок
- **Ошибка подключения к БД**: Проверьте параметры в `application.properties`
- **Ошибка сборки**: Убедитесь, что Maven установлен (`mvn -v`)
- **Ошибка развертывания**: Проверьте права на запись в папку webapps

## Контакты
Для вопросов: antonbut48@gmail.com