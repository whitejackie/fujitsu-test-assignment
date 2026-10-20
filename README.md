# Fujitsu Test Assignment

A Spring Boot REST application that calculates the delivery fee for food couriers based on the **city**, the **vehicle type** and the **current weather conditions**

The fee is the sum of a regional base fee and weather-related extra fees. Under dangerous weather conditions, delivery by a given vehicle type is forbidden.

## Features

- Delivery fee calculation by city and vehicle type (`GET /api/delivery-fee`)
- Weather data is imported from the Estonian Environment Agency XML feed on a schedule (and once on startup) and stored in the database
- Base fees and extra fee rules are **data, not code**: they are stored in the database and managed through CRUD endpoints
- Time-aware calculation: every base fee and rule has `validFrom` / `validTo`, so the fee can be calculated for a past moment using the weather and rules that were valid then
- Default fee data is seeded on first start if the tables are empty
- Consistent JSON error responses
- OpenAPI / Swagger documentation of all endpoints

## Business rules

### Cities and weather stations

| City    | Weather station | WMO code |
| ------- | --------------- | -------- |
| Tallinn | Tallinn-Harku   | 26038    |
| Tartu   | Tartu-Tõravere  | 26242    |
| Pärnu   | Pärnu           | 41803    |

### Regional base fee (RBF), EUR

| City    | Car  | Scooter | Bike |
| ------- | ---- | ------- | ---- |
| Tallinn | 4.00 | 3.50    | 3.00 |
| Tartu   | 3.50 | 3.00    | 2.50 |
| Pärnu   | 3.00 | 2.50    | 2.00 |

### Extra fees

Cars have no extra fees and are never forbidden by the default rules.

**Air temperature (ATEF), scooter and bike**

| Air temperature            | Fee, EUR |
| -------------------------- | -------- |
| below -10 °C               | 1.00     |
| -10 °C to 0 °C (inclusive) | 0.50     |

**Wind speed (WSEF), bike only**

| Wind speed                   | Result        |
| ---------------------------- | ------------- |
| 10 m/s to 20 m/s (inclusive) | 0.50 EUR      |
| above 20 m/s                 | **forbidden** |

**Weather phenomenon (WPEF), scooter and bike**

The observed phenomenon text is searched for these fragments, case-insensitively.

| Phenomenon contains        | Result        |
| -------------------------- | ------------- |
| `snow`, `sleet`            | 1.00 EUR      |
| `rain`, `shower`           | 0.50 EUR      |
| `glaze`, `hail`, `thunder` | **forbidden** |

### Total fee

```
total = RBF(city, vehicle) + ATEF + WSEF + WPEF
```

How the calculation resolves the data:

- **Weather:** the newest observation of the city's station at or before the requested moment (now, if no `datetime` is given).
- **Base fee:** among the base fees valid at that moment, the one with the latest `validFrom`.
- **Rules:** only rules valid at that moment for the vehicle type are considered.
- **Within one category, the highest matching fee wins** (fees of the same category are not added up). For example, "Light snow shower" matches both `snow` (1.00) and `shower` (0.50), so WPEF is 1.00.
- **A matching forbidden rule stops the calculation** and the request fails with HTTP 422.
- If the observation has no value for a measurement (empty or unparsable in the feed), that category adds no extra fee.

## How rules are modelled

All extra fee rules are rows of one table, `extra_fee_rule`:

| Field                                                  | Meaning                                                                                                               |
| ------------------------------------------------------ | --------------------------------------------------------------------------------------------------------------------- |
| `category`                                             | `AIR_TEMPERATURE`, `WIND_SPEED` or `PHENOMENON`                                                                       |
| `vehicleType`                                          | the vehicle type the rule applies to                                                                                  |
| `minValue`, `minInclusive`, `maxValue`, `maxInclusive` | numeric range (a `null` bound means unbounded)                                                                        |
| `matchText`                                            | text fragment for phenomenon rules                                                                                    |
| `fee`                                                  | surcharge in EUR                                                                                                      |
| `forbidden`                                            | if `true`, the rule blocks the vehicle type; `fee` is then stored as `null`                                           |
| `validFrom`, `validTo`                                 | period during which the rule is in effect (`validTo` empty = open-ended; `validTo` itself is still inside the period) |

Examples of how the default rules are expressed:

- Temperature below -10: `min = null`, `max = -10 (exclusive)`, fee `1.00`
- Temperature -10 to 0: `min = -10 (inclusive)`, `max = 0 (inclusive)`, fee `0.50`
- Wind above 20: `min = 20 (exclusive)`, `max = null`, `forbidden = true`
- Phenomenon `hail`: `matchText = "hail"`, `forbidden = true`

Changing a fee or adding a new rule (for example a wind rule for scooters) is a data change and does not require a deployment.

## API

Examples assume the app runs on `http://localhost:8080`.

### Calculate delivery fee

`GET /api/delivery-fee`

| Parameter     | Required | Description                                                                                                          |
| ------------- | -------- | -------------------------------------------------------------------------------------------------------------------- |
| `city`        | yes      | `Tallinn`, `Tartu` or `Pärnu` (also `TALLINN`, `TARTU`, `PARNU`), case-insensitive                                   |
| `vehicleType` | yes      | `CAR`, `SCOOTER` or `BIKE`, case-insensitive                                                                         |
| `datetime`    | no       | ISO-8601 instant, e.g. `2026-10-19T10:00:00Z`. If omitted, the latest weather and the currently valid rules are used |

Example:

```
GET /api/delivery-fee?city=Tartu&vehicleType=bike
```

Response:

```json
{
  "city": "Tartu",
  "vehicleType": "BIKE",
  "totalFee": 3.5,
  "weatherObservedAt": "2026-10-20T08:15:00Z"
}
```

`weatherObservedAt` tells which weather observation the fee was based on.

### Base fee management

| Method | Path                  | Description                          |
| ------ | --------------------- | ------------------------------------ |
| GET    | `/api/base-fees`      | List all base fees                   |
| GET    | `/api/base-fees/{id}` | Get one base fee                     |
| POST   | `/api/base-fees`      | Create a base fee (`201 Created`)    |
| PUT    | `/api/base-fees/{id}` | Replace a base fee                   |
| DELETE | `/api/base-fees/{id}` | Delete a base fee (`204 No Content`) |

Validation: `city`, `vehicleType`, `baseFee` (not negative) and `validFrom` are required; `validTo` must be after `validFrom`.

### Extra fee rule management

| Method | Path                        | Description                      |
| ------ | --------------------------- | -------------------------------- |
| GET    | `/api/extra-fee-rules`      | List all rules                   |
| GET    | `/api/extra-fee-rules/{id}` | Get one rule                     |
| POST   | `/api/extra-fee-rules`      | Create a rule (`201 Created`)    |
| PUT    | `/api/extra-fee-rules/{id}` | Replace a rule                   |
| DELETE | `/api/extra-fee-rules/{id}` | Delete a rule (`204 No Content`) |

Validation:

- `category`, `vehicleType` and `validFrom` are required; `validTo` must be after `validFrom`
- `fee` is required unless the rule is `forbidden` (a forbidden rule's fee is cleared)
- `PHENOMENON` rules need a non-blank `matchText`
- `AIR_TEMPERATURE` and `WIND_SPEED` rules need `minValue` or `maxValue`, and `minValue` must not exceed `maxValue`

Example rule body:

```json
{
  "category": "WIND_SPEED",
  "vehicleType": "SCOOTER",
  "minValue": 15.0,
  "minInclusive": false,
  "maxValue": null,
  "maxInclusive": false,
  "fee": null,
  "forbidden": true,
  "validFrom": "2026-11-01T00:00:00Z"
}
```

### Errors

Every error uses the same body:

```json
{
  "status": 422,
  "message": "Usage of selected vehicle type is forbidden"
}
```

| Situation                                                      | HTTP status |
| -------------------------------------------------------------- | ----------- |
| Unknown city or vehicle type                                   | 400         |
| Malformed parameter or body (e.g. `datetime=abc`, broken JSON) | 400         |
| Invalid fee or rule payload (missing field, `min > max`, ...)  | 400         |
| No weather data for the city at the requested time             | 404         |
| No base fee defined for the city and vehicle type              | 404         |
| Base fee or rule id does not exist                             | 404         |
| Weather forbids the selected vehicle type                      | 422         |

## Weather import

- `WeatherImportScheduler` runs the import on the cron schedule in `weather.import.cron` and once right after startup (unless disabled), so the database is not empty at the first request.
- `WeatherFeedClient` downloads the XML from `weather.import.url`.
- `WeatherXmlParser` reads the observation timestamp (epoch seconds, root attribute) and each station's name, WMO code, air temperature, wind speed and phenomenon.
- `WeatherImportService` keeps only the three stations used by the supported cities and skips observations that are already stored (unique on WMO code and observation time), so repeated imports are safe.
- A failed import is logged and does not stop the application or the schedule; the service keeps using the latest stored data.

## Configuration

| Property                 | Default         | Description                                    |
| ------------------------ | --------------- | ---------------------------------------------- |
| `weather.import.url`     | none (required) | URL of the weather XML feed                    |
| `weather.import.cron`    | none (required) | Spring cron expression for the periodic import |
| `weather.import.enabled` | `true`          | Run one import on startup                      |

Example (`application.properties`):

```properties
weather.import.url=https://www.ilmateenistus.ee/ilma_andmed/xml/observations.php
# every hour, 15 minutes past the full hour
weather.import.cron=0 15 * * * *
weather.import.enabled=true
```

## Project structure

```
com.liisabot.delivery.fees
├── config       DefaultFeeDataSeeder – seeds default base fees and rules on first start
├── controller   DeliveryFeeController, BaseFeeController, ExtraFeeRuleController
├── domain       BaseFee, ExtraFeeRule, WeatherObservation, City, VehicleType, RuleCategory
├── dto          DeliveryFeeResponse, ErrorResponse
├── exception    ApiException and subclasses, WeatherImportException, GlobalExceptionHandler
├── repository   BaseFeeRepository, ExtraFeeRuleRepository, WeatherObservationRepository
├── service      DeliveryFeeService, BaseFeeService, ExtraFeeRuleService, WeatherImportService
└── weather      WeatherFeedClient, WeatherXmlParser, ObservationsXml, WeatherImportScheduler
```

## Tech stack

- Java 17+
- Spring Boot 4 (Web, Data JPA, scheduling), Jackson 3 with XML support
- Lombok
- springdoc-openapi (Swagger UI)

## Running the application

Requirements: JDK 17 or newer.

```bash
./mvnw spring-boot:run
```

On the first start the `DefaultFeeDataSeeder` fills the `base_fee` and `extra_fee_rule` tables if they are empty. Existing data is never overwritten. Swagger UI is available at <http://localhost:8080/swagger-ui/index.html>.

## Running the tests

```bash
./mvnw test
```
