package com.rockthejvm

object AlertManager {

  // Lets define that any output below 50.0 is considered low and should trigger a warning alert
  private val LowOutputThreshold = 50.0

  // Detect both low output and equipment malfunctions in one simple function
  def detectAlert(record: EnergyRecord): Option[Alert] = {
    if (record.value <= 0.0) {
      Some(Alert(
        message = s"Equipment Malfunction! Zero or negative output (${record.value}) at ${record.startTime}",
        severity = "Critical"
      ))
    } else if (record.value < LowOutputThreshold) {
      Some(Alert(
        message = s"Low energy output (${record.value}) detected at ${record.startTime}",
        severity = "Warning"
      ))
    } else {
      None
    }
  }

  def generateAlerts(records: List[EnergyRecord]): List[Alert] = {
    // .flatMap applies detectAlert to every record and automatically removes the 'None's!
    records.flatMap(detectAlert)
  }
}