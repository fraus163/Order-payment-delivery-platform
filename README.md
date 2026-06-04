# Platform

Микросервисная платформа для обработки заказов, платежей и назначения доставки.

Проект состоит из нескольких Spring Boot сервисов, общей библиотеки контрактов и локальной инфраструктуры на Docker Compose.

## Сервисы

| Сервис | Порт | Назначение |
| --- | --- | --- |
| `order-service` | `8080` | Создание заказов, получение заказа, запуск оплаты, обработка событий доставки |
| `payment-service` | `8081` | Создание платежей и сохранение статуса оплаты |
| `delivery-service` | `8082` | Назначение курьера после успешной оплаты заказа |
| `common-libs` | - | Общие DTO, enum'ы и Kafka-события |

## Стек

- Java 21
- Spring Boot 3
- Spring Web
- Spring Data JPA
- Spring Kafka
- PostgreSQL
- Apache Kafka
- Maven
- Docker Compose
- Lombok
- MapStruct
- SpringDoc OpenAPI

## Архитектура

Основной сценарий:

1. Клиент создает заказ в `order-service`.
2. `order-service` рассчитывает стоимость заказа и сохраняет его со статусом `PENDING_PAYMENT`.
3. Клиент отправляет запрос на оплату заказа.
4. `order-service` вызывает `payment-service` по HTTP.
5. `payment-service` создает платеж и возвращает статус оплаты.
6. После успешной оплаты `order-service` публикует Kafka-событие `OrderPaidEvent`.
7. `delivery-service` получает событие, назначает курьера и публикует `DeliveryAssignedEvent`.
8. `order-service` получает событие доставки и обновляет заказ.

Kafka topics:

- `orders.events`
- `delivery.events`

## Структура проекта

```text
platform/
|-- common-libs/
|-- order-service/
|-- payment-service/
|-- delivery-service/
|-- docker-compose.yaml
|-- pom.xml
`-- README.md
```

## Требования

Для локального запуска через Docker:

- Docker
- Docker Compose

Для запуска сервисов без Docker:

- JDK 21
- Maven или Maven Wrapper
- PostgreSQL
- Kafka

## Запуск через Docker Compose

Собрать и запустить всю платформу:

```bash
docker compose up --build
```

Остановить сервисы:

```bash
docker compose down
```

Остановить сервисы и удалить volumes с данными PostgreSQL/Kafka:

```bash
docker compose down -v
```

После запуска доступны:

- `order-service`: <http://localhost:8080>
- `payment-service`: <http://localhost:8081>
- `delivery-service`: <http://localhost:8082>
- Kafka external listener: `localhost:9092`
- PostgreSQL: `localhost:5432`

## Локальные переменные окружения

В `docker-compose.yaml` используются dev-значения:

```env
DB_URL=jdbc:postgresql://postgres:5432/orders
DB_USERNAME=postgres
DB_PASSWORD=postgres
KAFKA_BOOTSTRAP_SERVERS=kafka:29092
```

Эти значения предназначены для локальной разработки. Не используйте их для production-окружения.

## Сборка

Собрать все модули:

```bash
./mvnw clean install
```

Windows:

```powershell
.\mvnw.cmd clean install
```

Запустить тесты:

```bash
./mvnw test
```

Windows:

```powershell
.\mvnw.cmd test
```

## API

### Создать заказ

```http
POST /api/orders
Host: localhost:8080
Content-Type: application/json
```

Пример запроса:

```json
{
  "customerId": 1,
  "address": "Samara, Lenina 10",
  "items": [
    {
      "itemId": 101,
      "quantity": 2,
      "name": "Pizza"
    },
    {
      "itemId": 202,
      "quantity": 1,
      "name": "Cola"
    }
  ]
}
```

Пример через `curl`:

```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": 1,
    "address": "Samara, Lenina 10",
    "items": [
      { "itemId": 101, "quantity": 2, "name": "Pizza" },
      { "itemId": 202, "quantity": 1, "name": "Cola" }
    ]
  }'
```

### Получить заказ по ID

```http
GET /api/orders/{id}
Host: localhost:8080
```

Пример:

```bash
curl http://localhost:8080/api/orders/1
```

### Оплатить заказ

```http
POST /api/orders/{id}/pay
Host: localhost:8080
Content-Type: application/json
```

Доступные методы оплаты:

- `CARD`
- `QR`
- `YANDEX_SPLIT`

Пример:

```bash
curl -X POST http://localhost:8080/api/orders/1/pay \
  -H "Content-Type: application/json" \
  -d '{ "paymentMethod": "CARD" }'
```

### Создать платеж напрямую

Обычно этот endpoint вызывается из `order-service`.

```http
POST /api/payments
Host: localhost:8081
Content-Type: application/json
```

Пример:

```bash
curl -X POST http://localhost:8081/api/payments \
  -H "Content-Type: application/json" \
  -d '{
    "orderId": 1,
    "paymentMethod": "CARD",
    "amount": 1500.00
  }'
```

## Статусы

Статусы заказа:

- `PENDING_PAYMENT`
- `PAID`
- `PAYMENT_FAILED`
- `DELIVERY_ASSIGNED`
- `DELIVERED`

Статусы платежа:

- `PAYMENT_SUCCEEDED`
- `PAYMENT_FAILED`
- `REFUNDED`

## OpenAPI

Если `order-service` запущен, Swagger UI доступен по адресу:

<http://localhost:8080/swagger-ui/index.html>

## База данных

Все сервисы используют PostgreSQL из `docker-compose.yaml`.

По умолчанию:

- database: `orders`
- username: `postgres`
- password: `postgres`
- port: `5432`

Hibernate настроен с `ddl-auto: update`, поэтому таблицы создаются и обновляются автоматически при запуске сервисов.

## Полезные команды

Посмотреть логи всех контейнеров:

```bash
docker compose logs -f
```

Посмотреть логи одного сервиса:

```bash
docker compose logs -f order-service
```

Пересобрать один сервис:

```bash
docker compose build order-service
```

Перезапустить один сервис:

```bash
docker compose up -d --build order-service
```


