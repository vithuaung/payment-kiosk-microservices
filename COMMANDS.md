# Commands Reference

## Requirements

- Java 21
- Maven 3.9+
- Docker Desktop


## Build

### Build all modules
```bash
mvn clean install -DskipTests
```

### Build with tests
```bash
mvn clean install
```

### Build one module only
```bash
mvn clean install -pl patient-service -am -DskipTests
```

### Skip tests for one module
```bash
mvn clean install -pl payment-service -am -DskipTests
```


## Run Tests

### Run all tests
```bash
mvn test
```

### Run tests for one module
```bash
mvn test -pl patient-service
```

### Run one test class
```bash
mvn test -pl payment-service -Dtest=PaymentServiceTest
```

### Run one test method
```bash
mvn test -pl payment-service -Dtest=PaymentServiceTest#initiate_success_createsPendingPayment
```


## Docker

Two compose files:
- `docker-compose.yml` — infrastructure: MSSQL, Redis, Kafka, mock-gateway-service
- `docker-compose.apps.yml` — Java apps: api-gateway, patient-service, payment-service, settlement-service, notification-service

Always start infrastructure before apps.

### Step 1 — Start infrastructure
```bash
docker-compose up -d
```

### Step 2 — Start Java apps (build first time or after code change)
```bash
docker-compose -f docker-compose.apps.yml up --build
```

### Step 2 — Start Java apps (no rebuild)
```bash
docker-compose -f docker-compose.apps.yml up
```

### Start Java apps in background
```bash
docker-compose -f docker-compose.apps.yml up -d
```

### Stop Java apps only
```bash
docker-compose -f docker-compose.apps.yml down
```

### Stop infrastructure only
```bash
docker-compose down
```

### Stop everything and wipe DB data
```bash
docker-compose -f docker-compose.apps.yml down
docker-compose down -v
```

### Rebuild one app only
```bash
docker-compose -f docker-compose.apps.yml up --build payment-service
```

### View logs for one app
```bash
docker-compose -f docker-compose.apps.yml logs -f payment-service
```

### View logs for infrastructure
```bash
docker-compose logs -f
```

### View logs for all (infra + apps)
```bash
docker-compose logs -f & docker-compose -f docker-compose.apps.yml logs -f
```

### Check running containers
```bash
docker-compose ps
docker-compose -f docker-compose.apps.yml ps
```


## Run Services Locally (without Docker)

Start infrastructure first:
```bash
docker-compose up -d
```

Then run a service from its directory:
```bash
cd patient-service
mvn spring-boot:run
```

Or run the built jar:
```bash
java -jar patient-service/target/patient-service-1.0.0.jar
```


## API Gateway

All requests go through port 8080.

### Get a JWT token
```bash
curl -X POST http://localhost:8080/api/auth/token \
  -H "Content-Type: application/json" \
  -d '{"apiKey":"pmk-dev-api-key","terminalCode":"TERM-001"}'
```

Response:
```json
{
  "success": true,
  "data": {
    "token": "eyJ...",
    "expiresAt": 1234567890000
  }
}
```

Use the token in all subsequent requests:
```bash
-H "Authorization: Bearer eyJ..."
```


## Patient Service (port 8081 direct / via gateway port 8080)

### Look up a patient
```bash
curl -X POST http://localhost:8080/api/patients/lookup \
  -H "Authorization: Bearer TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"idRef":"S1234567A"}'
```

### Register a patient
```bash
curl -X POST http://localhost:8080/api/patients/register \
  -H "Authorization: Bearer TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"idRef":"S1234567A"}'
```

### List visits for a patient
```bash
curl -X POST http://localhost:8080/api/visits/list \
  -H "Authorization: Bearer TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"idRef":"S1234567A"}'
```

### Check in (appointment or walk-in)
```bash
curl -X POST http://localhost:8080/api/checkins \
  -H "Authorization: Bearer TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "idRef": "S1234567A",
    "visitId": null,
    "checkinType": "WALKIN",
    "locationCode": "CTR-01"
  }'
```


## Payment Service (port 8082 direct / via gateway port 8080)

### Look up bills
```bash
curl -X POST http://localhost:8080/api/bills/lookup \
  -H "Authorization: Bearer TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"personRef":"S1234567A","orgCode":"ORG-CENTRAL"}'
```

### Initiate a payment
```bash
curl -X POST http://localhost:8080/api/payments \
  -H "Authorization: Bearer TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "personRef": "S1234567A",
    "terminalCode": "TERM-001",
    "payMethod": "NETS_CARD",
    "totalAmt": 150.00,
    "billItems": [
      {
        "billRef": "BILL-001",
        "billSeq": 1,
        "billedAmt": 150.00,
        "payableAmt": 150.00,
        "orgCode": "ORG-CENTRAL",
        "caseRef": "CASE-001"
      }
    ]
  }'
```

### Start processing (triggers Kafka event)
```bash
curl -X PUT http://localhost:8080/api/payments/SESSION-REF/start \
  -H "Authorization: Bearer TOKEN"
```

### Complete payment
```bash
curl -X PUT http://localhost:8080/api/payments/SESSION-REF/complete \
  -H "Authorization: Bearer TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"sessionRef":"SESSION-REF","paidAmt":150.00,"changeAmt":0.00}'
```

### Get payment status
```bash
curl http://localhost:8080/api/payments/SESSION-REF \
  -H "Authorization: Bearer TOKEN"
```


## Settlement Service (port 8083 direct / via gateway port 8080)

### Settle synchronously
```bash
curl -X POST http://localhost:8080/api/settlements/sync \
  -H "Authorization: Bearer TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "sessionRef": "SESSION-REF",
    "paymentId": "UUID-HERE",
    "personRef": "S1234567A",
    "totalAmt": 150.00,
    "payMethod": "NETS_CARD",
    "billItems": [
      {
        "billRef": "BILL-001",
        "billSeq": 1,
        "payableAmt": 150.00,
        "orgCode": "ORG-CENTRAL"
      }
    ]
  }'
```

### Get settlement status
```bash
curl http://localhost:8080/api/settlements/SESSION-REF \
  -H "Authorization: Bearer TOKEN"
```

### Retry a failed settlement
```bash
curl -X POST http://localhost:8080/api/settlements/SESSION-REF/retry \
  -H "Authorization: Bearer TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"sessionRef":"SESSION-REF"}'
```


## Notification Service (port 8084 direct / via gateway port 8080)

### Get notifications for a payment
```bash
curl http://localhost:8080/api/notifications/payment/PAYMENT-UUID \
  -H "Authorization: Bearer TOKEN"
```


## Mock Gateway Service (port 8090, no auth needed)

### Mock SAP bill lookup
```bash
curl -X POST http://localhost:8090/mock/sap/bill-details \
  -H "Content-Type: application/json" \
  -d '{"personRef":"S1234567A","orgCode":"ORG-CENTRAL"}'
```

### Mock SAP bill post
```bash
curl -X POST http://localhost:8090/mock/sap/bill-post \
  -H "Content-Type: application/json" \
  -d '{"sessionRef":"SESSION-REF","personRef":"S1234567A","items":[],"payMethod":"NETS_CARD"}'
```

### Mock NGEMR patient lookup
```bash
curl -X POST http://localhost:8090/mock/ngemr/person \
  -H "Content-Type: application/json" \
  -d '{"idRef":"S1234567A"}'
```

### Mock bank NETS initiate
```bash
curl -X POST http://localhost:8090/mock/bank/nets/initiate \
  -H "Content-Type: application/json" \
  -d '{"sessionRef":"SESSION-REF","amount":150.00,"terminalCode":"TERM-001"}'
```

### Mock bank card charge
```bash
curl -X POST http://localhost:8090/mock/bank/card/charge \
  -H "Content-Type: application/json" \
  -d '{"sessionRef":"SESSION-REF","amount":150.00,"cardNetwork":"VISA","terminalCode":"TERM-001"}'
```

### Mock bank transaction status
```bash
curl http://localhost:8090/mock/bank/transaction/NETS-ABCD1234
```


## Swagger UI

Each service exposes its own Swagger UI. Access directly (not via gateway):

| Service            | URL                                    |
|--------------------|----------------------------------------|
| mock-gateway       | http://localhost:8090/swagger-ui.html  |
| patient-service    | http://localhost:8081/swagger-ui.html  |
| payment-service    | http://localhost:8082/swagger-ui.html  |
| settlement-service | http://localhost:8083/swagger-ui.html  |
| notification-service | http://localhost:8084/swagger-ui.html |


## Database (MSSQL in Docker)

Connect via any SQL client:
- Host: localhost
- Port: 1433
- Username: sa
- Password: Pmk@Str0ng!
- Databases: pmk_patient, pmk_payment, pmk_settlement, pmk_notification

Connect via sqlcmd inside the container:
```bash
docker exec -it pmk-mssql /opt/mssql-tools/bin/sqlcmd \
  -S localhost -U sa -P 'Pmk@Str0ng!'
```

Run a query:
```sql
USE pmk_payment;
SELECT * FROM mtxn_payment;
GO
```


## Redis (in Docker)

Connect via redis-cli inside the container:
```bash
docker exec -it pmk-redis redis-cli -a pmk_redis_pass
```

Check cached keys:
```bash
KEYS *
```

Get a payment session:
```bash
GET "payment:session:SESSION-REF"
```

Get cached bill detail:
```bash
GET "patient:bill:S1234567A"
```


## Kafka (in Docker)

List topics:
```bash
docker exec -it pmk-kafka kafka-topics \
  --bootstrap-server localhost:9092 --list
```

Watch a topic live:
```bash
docker exec -it pmk-kafka kafka-console-consumer \
  --bootstrap-server localhost:9092 \
  --topic pmk.payment.initiated --from-beginning
```

Topics in use:
- pmk.payment.initiated
- pmk.payment.completed
- pmk.payment.failed
- pmk.patient.registered


## Service Ports Summary

| Service              | Port |
|----------------------|------|
| api-gateway          | 8080 |
| patient-service      | 8081 |
| payment-service      | 8082 |
| settlement-service   | 8083 |
| notification-service | 8084 |
| mock-gateway-service | 8090 |
| MSSQL                | 1433 |
| Redis                | 6379 |
| Kafka                | 9092 |
