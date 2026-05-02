package com.rockthejvm

import io.github.cdimascio.dotenv.Dotenv
import java.time.format.DateTimeFormatter
import java.time.{LocalDate, LocalDateTime, ZoneId, ZonedDateTime}
import scala.annotation.tailrec
import scala.util.Try

// No AI used

object PrintMenu {
  def main(args: Array[String]): Unit = {
    val dotenv = Dotenv.load()

    // Retrieve API key from .env file using Option to handle potential absence
    val apiKey: Option[String] = Option(dotenv.get("FINGRID_API_KEY"))

    apiKey match {
      case Some(key) =>
        println("Success: API key loaded from .env!")
        // Start the interactive menu loop with an empty list of records
        menuLoop(key, List.empty[EnergyRecord])
      case None =>
        println("Error: FINGRID_API_KEY not found in .env file.")
    }
  }

  // Purely functional tail-recursive menu loop (No while-loops or mutable variables)
  @tailrec
  def menuLoop(apiKey: String, currentData: List[EnergyRecord]): Unit = {
    println("\n")
    println(" Renewable Energy Plant System (REPS) ")
    println("--------------------------------------")
    println(s"Current Records in Memory: ${currentData.size}")
    println("1. Fetch Data from API")
    println("2. Save Data to CSV")
    println("3. Load Data from CSV")
    println("4. Analyze Data (Statistics)")
    println("5. Check Alerts")
    println("6. Search / Filter / Sort Data")
    println("7. Exit")
    print("\nSelect an option: ")

    scala.io.StdIn.readLine() match {
      case "1" =>
        val newRecords = fetchFromApiWithDateHandling(apiKey)
        // Add new records to existing data and recurse
        menuLoop(apiKey, currentData ++ newRecords)

      case "2" =>
        FileHandler.saveToCSV(currentData)
        menuLoop(apiKey, currentData)

      case "3" =>
        val loadedRecords = FileHandler.readFromCSV("energy_records.csv")
        println(s"Loaded ${loadedRecords.size} records from file.")
        // Combine loaded records with what we currently have
        menuLoop(apiKey, currentData ++ loadedRecords)



      case "4" =>
        if (currentData.isEmpty) println("No data available to analyze.")
        else {
          val windRecords = currentData.filter(_.datasetId == 75)
          val solarRecords = currentData.filter(_.datasetId == 248)
          val hydroRecords = currentData.filter(_.datasetId == 191)

          println("\n--- Data Analysis ---")

          // Helper function to print analytics per plant type
          def printAnalytics(name: String, records: List[EnergyRecord]): Unit = {
            if (records.nonEmpty) {
              println(s"\n[$name Plant - ${records.size} records]")
              println(f"  Mean:     ${DataAnalyzer.mean(records)}%.3f")
              println(f"  Median:   ${DataAnalyzer.median(records)}%.3f")
              println(s"  Mode:     ${DataAnalyzer.mode(records)}")
              println(f"  Range:    ${DataAnalyzer.range(records)}%.3f")
              println(f"  Midrange: ${DataAnalyzer.midrange(records)}%.3f")
            }
          }

          printAnalytics("Wind", windRecords)
          printAnalytics("Solar", solarRecords)
          printAnalytics("Hydro", hydroRecords)

          // --- Generation & Capacity View ---
          println("\n--- Plant Generation & Capacity View ---")
          val maxWindMW: Double = 8000.0
          val maxSolarMW: Double = 1500.0
          val maxHydroMW: Double = 4000.0

          // Helper function to calculate and print the yields per plant type
          def printStats(name: String, records: List[EnergyRecord], intervalHrs: Double, maxMW: Double): Unit = {
            if (records.nonEmpty) {
              val generatedMWh = records.map(_.value * intervalHrs).sum
              val totalHours = records.size * intervalHrs
              val maxPossibleMWh = maxMW * totalHours
              val utilPct = if (maxPossibleMWh > 0) (generatedMWh / maxPossibleMWh) * 100 else 0.0

              println(s"[$name Plant]")
              println(f"  Generated Energy:     $generatedMWh%,.2f MWh")
              println(f"  Max Possible Energy:   $maxPossibleMWh%,.2f MWh (over $totalHours%.2f hours)")
              println(f"  Capacity Utilization: $utilPct%.2f%%")
            }
          }

          printStats("Wind", windRecords, 0.25, maxWindMW)
          printStats("Solar", solarRecords, 0.25, maxSolarMW)
          printStats("Hydro", hydroRecords, 0.05, maxHydroMW)
        }
        menuLoop(apiKey, currentData)

      case "5" =>
        val alerts = AlertManager.generateAlerts(currentData)
        if (alerts.isEmpty) {
          println("System stable: No low energy output detected.")
        } else {
          println(s"\n--- Active Alerts (${alerts.size}) ---")
          alerts.foreach(alert => println(s"[${alert.severity}] ${alert.message}"))
        }
        menuLoop(apiKey, currentData)

      case "6" =>
        println("1. Sort by value (Ascending)")
        println("2. Sort by value (Descending)")
        println("3. Sort by time (Old to New)")
        println("4. Sort by time (New to Old)")
        println("5. Filter by Energy Source (Wind=75, Solar=248, Hydro=191)")
        println("6. Search for exact value")
        println("7. Filter by Time Range")
        print("\nSelect an option: ")
        scala.io.StdIn.readLine() match {
          case "1" =>
            val sorted = DataFilter.sortByValue(currentData) // Default is ascending, so no need to pass true
            sorted.foreach(r => println(s"Value: ${r.value}, Dataset: ${r.datasetId}, Time: ${r.startTime}"))
          case "2" =>
            val sorted = DataFilter.sortByValue(currentData, false)
            sorted.foreach(r => println(s"Value: ${r.value}, Dataset: ${r.datasetId}, Time: ${r.startTime}"))
          case "3" =>
            val sorted = DataFilter.sortByTime(currentData)
            sorted.foreach(r => println(s"Time: ${r.startTime}, Value: ${r.value}, Dataset: ${r.datasetId}"))
          case "4" =>
            val sorted = DataFilter.sortByTime(currentData, false)
            sorted.foreach(r => println(s"Time: ${r.startTime}, Value: ${r.value}, Dataset: ${r.datasetId}"))
          case "5" =>
            print("Enter Dataset ID (75, 248, 191): ")
            scala.io.StdIn.readLine().toIntOption match {
              case Some(id) if id == 75 || id == 248 || id == 191 =>
                val filtered = DataFilter.filterByEnergyType(currentData, id)
                println(s"Found ${filtered.size} records for dataset $id.")
                filtered.foreach(r => println(s"Time: ${r.startTime}, Value: ${r.value}, Dataset: ${r.datasetId}"))
              case _ =>
                println("Invalid input. Please enter a valid Dataset ID (75, 248, or 191).")
            }
          case "6" =>
            print("Enter exact value to search for: ")
            scala.io.StdIn.readLine().toDoubleOption match {
              case Some(targetValue) =>
                DataFilter.searchByValue(currentData, targetValue) match {
                  case Some(record) => println(s"Found record: $record")
                  case None => println("No record found with that exact value.")
                }
              case None =>
                println("Invalid input. Please enter a valid decimal number.")
            }

          case "7" =>
            val format = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
            print("Enter start date & time (DD/MM/YYYY HH:MM): ")
            val startInput = scala.io.StdIn.readLine()
            print("Enter end date & time (DD/MM/YYYY HH:MM): ")
            val endInput = scala.io.StdIn.readLine()

            val tryFilter = for {
              // Parse the input strings into LocalDateTime objects
              startLocal <- Try(LocalDateTime.parse(startInput, format))
              endLocal <- Try(LocalDateTime.parse(endInput, format))
              startZoned = startLocal.atZone(ZoneId.of("Europe/Helsinki"))
              endZoned = endLocal.atZone(ZoneId.of("Europe/Helsinki"))
            } yield (startZoned, endZoned)

            tryFilter match {
              case scala.util.Success((start, end)) =>
                if (start.isAfter(end)) {
                  println("Invalid time range.")
                } else {
                  val filtered = DataFilter.filterByTimeRange(currentData, start, end)
                  println(s"Found ${filtered.size} records in the given range.")
                  filtered.foreach(println)
                }
              case scala.util.Failure(_) =>
                println("Invalid format.")
            }
          case _ => println("Invalid option.")
        }
        menuLoop(apiKey, currentData)

      case "7" =>
        println("Shutting down REPS. Goodbye!")
      // Ends recursion

      case _ =>
        println("Invalid option. Please try again.")
        menuLoop(apiKey, currentData)
    }
  }


  // Handles the specific assignment requirements for Error Handling
  def fetchFromApiWithDateHandling(apiKey: String): List[EnergyRecord] = {
    println("\nSelect Energy Type to Fetch:")
    println("1. Wind Power (Dataset 75)")
    println("2. Solar Power (Dataset 248)")
    println("3. Hydro Power (Dataset 191)")
    print("Choice: ")

    val datasetId = scala.io.StdIn.readLine() match {
      case "1" => 75
      case "2" => 248
      case "3" => 191
      case _ =>
        println("Invalid choice. Please select 1, 2, or 3.")
        return List.empty[EnergyRecord]
    }

    print("Enter the date to fetch data (DD/MM/YYYY): ")
    val dateInput = scala.io.StdIn.readLine()

    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    // Functional Try blocks replacing try-catch logic
    Try(LocalDate.parse(dateInput, formatter)) match {

      // Guard against future dates (API doesnt catch this sometimes)
      case scala.util.Success(date) if date.isAfter(LocalDate.now()) =>
        println("\nInvalid date. You cannot fetch data for future dates.")
        List.empty[EnergyRecord]

      case scala.util.Success(date) =>
        // Convert to ISO-8601 Europe/Helsinki formats for Fingrid
        val startStr = s"${date}T00:00:00Z"
        val endStr = s"${date.plusDays(1)}T00:00:00Z"

        // Fetch using dynamic datasetId
        fetchFingridData(apiKey, datasetId, startStr, endStr)

      case scala.util.Failure(_) =>
        // Satisfies Requirement Scenario 1
        println("\nInvalid date format. Please enter the date in the format 'DD/MM/YYYY'.")
        println("For example, enter '12/04/2026' for April 12, 2026.")
        List.empty[EnergyRecord]
    }
  }


  def fetchFingridData(apiKey: String, datasetId: Int, start: String, end: String): List[EnergyRecord] = {
    import java.net.{HttpURLConnection, URL}
    import scala.io.Source

    // Added pageSize=10000 cuz Fingrid API defaults to a small limit
    val urlString = s"https://data.fingrid.fi/api/datasets/$datasetId/data?startTime=$start&endTime=$end&pageSize=10000"
    println(s"Fetching data... (Dataset: $datasetId)")

    Try {
      val url = new URL(urlString)
      val connection = url.openConnection().asInstanceOf[HttpURLConnection]
      connection.setRequestMethod("GET")
      connection.setRequestProperty("x-api-key", apiKey)
      connection.setConnectTimeout(5000)
      connection.setReadTimeout(5000)

      val responseCode = connection.getResponseCode
      if (responseCode == 200) {
        // Read the response text functionally using Using / Source
        val inputStream = connection.getInputStream
        val jsonString = Source.fromInputStream(inputStream).mkString
        inputStream.close()
        Right(jsonString)
      } else {
        Left(s"HTTP Error code: $responseCode")
      }
    } match {
      case scala.util.Success(Right(jsonString)) =>
        // Using ujson to parse the returned text into a JSON object
        val json = ujson.read(jsonString)
        val dataArray = json("data").arr.toList

        if (dataArray.isEmpty) {
          println("\nNo available data for the selected date. Please choose another date.")
          List.empty[EnergyRecord]
        } else {
          println(s"Successfully fetched ${dataArray.size} records.")

          dataArray.map { item =>
            val finnishZone = ZoneId.of("Europe/Helsinki")

            val parsedStart = ZonedDateTime.parse(item("startTime").str)
            val parsedEnd = ZonedDateTime.parse(item("endTime").str)

            val startFinnish = parsedStart.withZoneSameInstant(finnishZone).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
            val endFinnish = parsedEnd.withZoneSameInstant(finnishZone).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)

            EnergyRecord(
              datasetId = item("datasetId").num.toInt,
              startTime = startFinnish,
              endTime = endFinnish,
              value = item("value").num
            )
          }
        }
      case scala.util.Success(Left(errorMsg)) =>
        println(s"API connection failed: $errorMsg")
        List.empty[EnergyRecord]
      case scala.util.Failure(exception) =>
        println(s"Failed to connect to API: ${exception.getMessage}")
        List.empty[EnergyRecord]
    }
  }
}
