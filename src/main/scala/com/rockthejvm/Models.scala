package com.rockthejvm

// --- Data Records and Alerts ---

// Case Class because it will hold data (for data handling)
case class EnergyRecord(
                       datasetId: Int,
                       startTime: String,
                       endTime: String,
                       value: Double
                       )

case class Alert(
                message: String,
                severity: String
                )

// --- Energy Sources ---

// Sealed trait is like abstraction in Java.
// Solar Panel, Wind Turbine and Hydro Plant all have the same variables
sealed trait EnergySource {
  def id: String
  def datasetId: Int
  def sourceName: String
  def unit: String
  def interval: String
  def validFrom: String
  def status: String
}

case class SolarPanel(
                       id: String,
                       datasetId: Int,
                       sourceName: String,
                       unit: String,
                       interval: String,
                       validFrom: String,
                       status: String,
                       energyType: String = "Solar"
                     ) extends EnergySource

case class WindTurbine(
                        id: String,
                        datasetId: Int,
                        sourceName: String,
                        unit: String,
                        interval: String,
                        validFrom: String,
                        status: String,
                        energyType: String = "Wind"
                      ) extends EnergySource

case class HydroPlant(
                       id: String,
                       datasetId: Int,
                       sourceName: String,
                       unit: String,
                       interval: String,
                       validFrom: String,
                       status: String,
                       energyType: String = "Hydro"
                     ) extends EnergySource

// --- Plant ---
case class RenewablePlant(
                           plantId: String,
                           name: String,
                           sources: List[EnergySource]
                         ) {
  // We will implement these methods later using higher-order functions
  // over the generated EnergyRecords and AlertManager.
  // def totalOutput(): Double = ???
  // def activeAlerts(): List[Alert] = ???
}