package com.rockthejvm

import java.io.File // directory path
import java.io.PrintWriter // write into files
import scala.io.Source // read files
import scala.collection.mutable.ListBuffer // as said in the requirements, we're allowed to use this
// It's basically a mutable list

object FileHandler {
  def saveToCSV(data: List[EnergyRecord]): Unit = {
    // We will define a default file name
    val fileName = "energy_records.csv"
    val file = new File(fileName)
    val writer = new PrintWriter(file)

    try {
      // CSV header
      writer.println("datasetId,startTime,endTime,value")

      // Here is where an imperative while-loop is used
      // To write data to the csv.
      var i = 0
      while (i < data.size) {
        val record = data(i)
        writer.println(s"${record.datasetId},${record.startTime},${record.endTime},${record.value}")
        i += 1
      }
      println(s"Successfully saved ${data.size} records to $fileName")
    } catch {
      case e: Exception => println(s"Error writing to CSV: ${e.getMessage}")
    } finally {
      writer.close()
    }
  }

  def readFromCSV(fileName: String): List[EnergyRecord] = {
    val buffer = ListBuffer[EnergyRecord]()
    var source: Source = null

    try {
      source = Source.fromFile(fileName)
      val lines = source.getLines().toArray // Read all lines into an array

      var i = 1 // skip header
      while (i < lines.length) {
        val columns = lines(i).split(",")
        // Make sure we have exactly 4 columns before parsing
        if (columns.length == 4) {
          val record = EnergyRecord(
            datasetId = columns(0).toInt,
            startTime = columns(1),
            endTime = columns(2),
            value = columns(3).toDouble
          )
          buffer += record // append to mutable buffer
        }
        i += 1
      }
    } catch {
      case e: java.io.FileNotFoundException =>
        println(s"File not found: $fileName")
      case e: Exception =>
        println(s"Error reading from CSV: ${e.getMessage}")
    } finally {
      if (source != null) source.close()
    }

    // Convert the mutable buffer back to an immutable List before returning
    buffer.toList
  }


}
