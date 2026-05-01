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


sealed trait EnergySource {
  def id: String
  def datasetId: Int
  def sourceName: String
  def unit: String
  def interval: String
  def validFrom: String
  def status: String
}
