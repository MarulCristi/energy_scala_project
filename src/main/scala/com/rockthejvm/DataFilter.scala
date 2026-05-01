package com.rockthejvm

import java.time.ZonedDateTime
import scala.util.Try

// This object contains all the functions needed to filter our data
object DataFilter {

  // Sorts data by value
  def sortByValue(records: List[EnergyRecord], ascending: Boolean = true): List[EnergyRecord] = {
    if (ascending) records.sortBy(_.value)
    else records.sortBy(_.value)(Ordering[Double].reverse)
  }

  // Sorts data by time
  def sortByTime(records: List[EnergyRecord], ascending: Boolean = true): List[EnergyRecord] = {
    if (ascending) records.sortBy(_.startTime)
    else records.sortBy(_.startTime)(Ordering[String].reverse)
  }

  // Searches for exact value
  def searchByValue(records: List[EnergyRecord], targetValue: Double): Option[EnergyRecord] = {
    records.find(_.value == targetValue)
  }

  // Filters data by energy type
  def filterByEnergyType(records: List[EnergyRecord], datasetId: Int): List[EnergyRecord] = {
    records.filter(_.datasetId == datasetId)
  }

  // Filters data for a given time range
  def filterByTimeRange(records: List[EnergyRecord], startDateTime: ZonedDateTime, endDateTime: ZonedDateTime): List[EnergyRecord] = {
    records.filter { r =>
      Try(ZonedDateTime.parse(r.startTime)).toOption.exists { zdt =>
        (zdt.isEqual(startDateTime) || zdt.isAfter(startDateTime)) &&
          (zdt.isEqual(endDateTime) || zdt.isBefore(endDateTime))
      }
    }
  }
}

