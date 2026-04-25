
package com.rockthejvm

import io.github.cdimascio.dotenv.Dotenv
import java.time.format.DateTimeFormatter
import java.time.LocalDate
import scala.annotation.tailrec
import scala.util.Try

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
    println("6. Exit")
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
          println("\n--- Data Analysis ---")
          println(f"Mean:     ${DataAnalyzer.mean(currentData)}%.3f")
          println(f"Median:   ${DataAnalyzer.median(currentData)}%.3f")
          println(s"Mode:     ${DataAnalyzer.mode(currentData)}")
          println(f"Range:    ${DataAnalyzer.range(currentData)}%.3f")
          println(f"Midrange: ${DataAnalyzer.midrange(currentData)}%.3f")
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
        println("Shutting down REPS. Goodbye!")
      // Ends recursion

      case _ =>
        println("Invalid option. Please try again.")
        menuLoop(apiKey, currentData)
    }
  }

  // Handles the specific assignment requirements for Error Handling
  def fetchFromApiWithDateHandling(apiKey: String): List[EnergyRecord] = {
    print("Enter the date to fetch Wind Power data (DD/MM/YYYY): ")
    val dateInput = scala.io.StdIn.readLine()

    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    // Functional Try blocks replacing try-catch logic
    Try(LocalDate.parse(dateInput, formatter)) match {
      case scala.util.Success(date) =>
        // Convert to ISO-8601 UTC formats for Fingrid
        val startStr = s"${date}T00:00:00Z"
        val endStr = s"${date.plusDays(1)}T00:00:00Z"

        // Dataset 75 = Wind power generation
        fetchFingridData(apiKey, 75, startStr, endStr)

      case scala.util.Failure(_) =>
        // Satisfies Requirement Scenario 1
        println("\nInvalid date format. Please enter the date in the format 'DD/MM/YYYY'.")
        println("For example, enter '12/04/2024' for April 12, 2024.")
        List.empty[EnergyRecord]
    }
  }


  def fetchFingridData(apiKey: String, datasetId: Int, start: String, end: String): List[EnergyRecord] = {
    import java.net.{HttpURLConnection, URL}
    import scala.io.Source

    val urlString = s"https://data.fingrid.fi/api/datasets/$datasetId/data?startTime=$start&endTime=$end"
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
            EnergyRecord(
              datasetId = item("datasetId").num.toInt,
              startTime = item("startTime").str,
              endTime = item("endTime").str,
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