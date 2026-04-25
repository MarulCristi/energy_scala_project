package com.rockthejvm

object AlertManager {

  // For now, let's define anything below 5.0 as low.
  private val LowOutputThreshold = 5.0

  def detectLowOutput(record: EnergyRecord): Option[Alert] = {
    if (record.value < LowOutputThreshold) {
      Some(Alert(
        message = s"Low energy output (${record.value}) detected for dataset ${record.datasetId} starting at ${record.startTime}",
        severity = "Warning"
      ))
    } else {
      None // Pure functional way to say "no alert" without using null
    }
  }

  def generateAlerts(records: List[EnergyRecord]): List[Alert] = {
    // .flatMap is a higher-order function that applies detectLowOutput to every record
    // and automatically filters out all the None values, keeping only the Some(Alert)s!
    records.flatMap(record => detectLowOutput(record))
  }
}