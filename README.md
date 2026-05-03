# Energy Scala Project

A functional programming project built with Scala that analyzes energy generation data from multiple renewable energy sources (Wind, Solar, and Hydroelectric power plants). This project demonstrates core functional programming concepts including immutability, higher-order functions, pattern matching, and data transformation.

## Project Overview

**Course:** Functional Programming Course  
**Language:** Scala (100%)

This project analyzes real-world energy generation data, providing tools for:
- Data filtering and sorting by multiple criteria
- Statistical analysis (mean, median, mode, range, midrange)
- Energy capacity utilization calculations
- Alert generation for anomalous power output
- Multi-source data aggregation and comparison

## Energy Sources Supported

The system tracks three types of renewable energy generation:

| Energy Source | Dataset ID | Max Capacity | Interval |
|---|---|---|---|
| **Wind Power** | 75 | 8,000 MW | 15 minutes (0.25 hrs) |
| **Solar Power** | 248 | 1,500 MW | 15 minutes (0.25 hrs) |
| **Hydroelectric Power** | 191 | 4,000 MW | 3 minutes (0.05 hrs) |

## Project Structure
```text
src/main/scala/com/rockthejvm/
├── Models.scala         # Data model definitions (EnergyRecord, Alert)
├── DataFilter.scala     # Filtering and sorting operations
├── DataAnalyzer.scala   # Statistical analysis functions
├── AlertManager.scala   # Alert detection and generation
├── FileHandler.scala    # File I/O operations
└── PrintMenu.scala      # Interactive CLI menu system
```

## Core Features

### 1. Data Filtering

The `DataFilter` object provides comprehensive filtering capabilities:

```scala
// Filter by Energy Type (Wind, Solar, or Hydro)
val windRecords = records.filter(_.datasetId == 75)
val solarRecords = records.filter(_.datasetId == 248)
val hydroRecords = records.filter(_.datasetId == 191)

// Filter by Time Range
val filteredByTime = DataFilter.filterByTimeRange(
  records, 
  startDateTime, 
  endDateTime
)

// Search for Exact Values
val exactMatch = DataFilter.searchByValue(records, 5342.5)
```

**Available Filter Operations:**
*   Sort by Value (Ascending/Descending)
*   Sort by Time (Old to New / New to Old)
*   Filter by Energy Source (Wind=75, Solar=248, Hydro=191)
*   Search for Exact Value (Precise energy output lookup)
*   Filter by Time Range (Custom date/time range queries)

### 2. Capacity Utilization Analysis

Capacity analysis calculates how efficiently each power plant is operating relative to its maximum possible output.

**How Capacity Works:**

For each energy source, the system calculates:

1.  **Total Generated Energy (MWh):**
    `Generated MWh = sum(value × intervalHrs for all records)`
    *   *value:* Energy output in MW for each record
    *   *intervalHrs:* Data collection interval (0.25 hrs for Wind/Solar, 0.05 hrs for Hydro)

2.  **Maximum Possible Energy:**
    `Max Possible MWh = maxCapacity × total hours in dataset`
    *   *maxCapacity:* Plant's rated maximum power (8000, 1500, or 4000 MW)
    *   *total hours:* Number of records × interval duration

3.  **Capacity Utilization Percentage:**
    `Capacity Utilization % = (Generated MWh / Max Possible MWh) × 100`

**Example Output:**
```text
[Wind Plant]
  Generated Energy:      12,450.50 MWh
  Max Possible Energy:   48,000.00 MWh (over 6 hours)
  Capacity Utilization:  25.94%

[Solar Plant]
  Generated Energy:       3,120.75 MWh
  Max Possible Energy:    9,000.00 MWh (over 6 hours)
  Capacity Utilization:  34.68%

[Hydro Plant]
  Generated Energy:       1,950.25 MWh
  Max Possible Energy:   24,000.00 MWh (over 6 hours)
  Capacity Utilization:   8.13%
```

*This metric is useful for identifying underperforming plants or understanding seasonal variations in renewable energy production.*

### 3. Data Analysis Features

The `DataAnalyzer` object provides statistical functions:
*   **Mean:** Average energy output across all records
*   **Median:** Middle value in the sorted dataset
*   **Mode:** Most common output range (grouped by 100 MW buckets)
*   **Range:** Difference between highest and lowest output
*   **Midrange:** Average of highest and lowest values

### 4. Alert System

The `AlertManager` generates real-time alerts based on energy output anomalies.

**Alert Thresholds:**

| Energy Source | Critical Threshold | Warning Threshold |
|---|---|---|
| **Wind** | < 300 MW | < 550 MW |
| **Hydro** | < 500 MW | < 900 MW |
| **Solar** | N/A | < 15 MW* |

*\*Solar has special night-time handling (19:00 - 08:00) where 0 output is expected.*

**Alert Types:**
*   🔴 **Critical:** Equipment malfunction (zero/negative output for non-solar) or severe underperformance
*   🟡 **Warning:** Low output within warning thresholds

## Data Model

### EnergyRecord
```scala
case class EnergyRecord(
  datasetId: Int,      // Energy source identifier (75, 248, 191)
  startTime: String,   // ISO 8601 timestamp (e.g., "2026-04-05T23:45:00.000Z")
  endTime: String,     // End of measurement interval
  value: Double        // Energy output in MW
)
```

### Alert

```scala
case class Alert(
  message: String,     // Detailed alert description
  severity: String     // "Critical" or "Warning"
)
```
## Functional Programming Concepts

This project demonstrates key functional programming principles:

*   ✅ **Immutability:** All data structures use case classes (immutable by default)
*   ✅ **Higher-Order Functions:** `map`, `filter`, `flatMap`, `groupBy` on collections
*   ✅ **Pattern Matching:** Energy source handling in `AlertManager`
*   ✅ **Pure Functions:** `DataAnalyzer` functions are pure with no side effects
*   ✅ **Function Composition:** Pipeline approach to data transformation
*   ✅ **Option Types:** Safe handling of optional values (e.g., `searchByValue` returns `Option`)
