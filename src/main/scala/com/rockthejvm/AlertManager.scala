
package com.rockthejvm

import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import scala.util.Try

object AlertManager {

  // Helper function to format the long ISO string into a cleaner format
  private def formatTime(timeStr: String): String = {
    Try {
      val parsed = ZonedDateTime.parse(timeStr)
      val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
      parsed.format(formatter)
    }.getOrElse(timeStr)
  }

  def detectAlert(record: EnergyRecord): Option[Alert] = {

    // 1. Check for Equipment Malfunction (0 or negative on anything EXCEPT Solar)
    val isEquipmentMalfunction = record.value <= 0.0 && record.datasetId != 248

    if (isEquipmentMalfunction) {
      return Some(Alert(
        message = s"Equipment Malfunction! Zero or negative output (${record.value}) for dataset ${record.datasetId} at ${formatTime(record.startTime)}",
        severity = "Critical"
      ))
    }

    // 2. Custom Thresholds and logic based on the energy source
    record.datasetId match {

      // Wind Power
      case 75 =>
        if (record.value < 300.0)
          Some(Alert(s"Critical wind output (${record.value}) at ${formatTime(record.startTime)} (Threshold: < 300)", "Critical"))
        else if (record.value < 550.0)
          Some(Alert(s"Low wind output (${record.value}) at ${formatTime(record.startTime)} (Threshold: < 550)", "Warning")) // FIXED string mismatch
        else None

      // Hydro Power
      case 191 =>
        if (record.value < 500.0)
          Some(Alert(s"Very low hydro output (${record.value}) at ${formatTime(record.startTime)} (Threshold: < 500)", "Critical"))
        else if (record.value < 900.0)
          Some(Alert(s"Low hydro output (${record.value}) at ${formatTime(record.startTime)} (Threshold: < 900)", "Warning"))
        else None

      // Solar Power
      case 248 =>
        // Extract the hour from startTime (e.g., "2026-04-05T23:45:00.000Z" -> "23")
        val hour = record.startTime.substring(11, 13).toIntOption.getOrElse(12)
        // Define rough night hours
        val isNight = hour >= 19 || hour <= 8

        if (isNight && record.value <= 0.0) {
          // Expected 0 output during the night, skip generating a warning
          None
        } else if (record.value < 15.0) {
          // Solar power only has a low output warning, no critical
          Some(Alert(s"Low solar output (${record.value}) at ${formatTime(record.startTime)} (Threshold: < 15)", "Warning"))
        } else {
          None
        }

      // Default fallback
      case _ => None
    }
  }

  def generateAlerts(records: List[EnergyRecord]): List[Alert] = {
    records.flatMap(detectAlert)
  }
}
