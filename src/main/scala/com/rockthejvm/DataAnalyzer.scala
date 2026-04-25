
package com.rockthejvm

object DataAnalyzer {

  // Function that just extracts the values from the records to make calculations easier
  private def getValues(records: List[EnergyRecord]): List[Double] = {
    records.map(record => record.value)
  }

  // Mean: sum of all values divided by the total number of values
  def mean(records: List[EnergyRecord]): Double = {
    val values = getValues(records)
    if (values.isEmpty) 0.0
    else values.sum / values.length // using .sum and .length
  }

  // Median: the middle number in the sorted set of values
  def median(records: List[EnergyRecord]): Double = {
    val values = getValues(records).sorted
    if (values.isEmpty) 0.0
    else {
      val middle = values.length / 2
      if (values.length % 2 != 0) {
        values(middle)
      } else {
        // If even number of elements, average the two middle ones
        (values(middle - 1) + values(middle)) / 2.0
      }
    }
  }

  // Mode: Grouped by 100s and formatted directly into the requested string format.
  def mode(records: List[EnergyRecord]): String = {
    val values = getValues(records)
    if (values.isEmpty) "None"
    else {
      // Group by the hundreds (e.g., 5341.2 becomes 5300)
      val grouped = values.groupBy(v => (v / 100.0).toInt * 100)

      // Find the maximum size of the grouped lists
      val maxCount = grouped.values.map(_.size).max

      // Filter out only the values that have the max count
      val topGroups = grouped.filter { case (_, list) => list.size == maxCount }

      // Format: "5300 3 found: (5341.2, 5327.57, 5324.74)"
      topGroups.map { case (bucket, list) =>
        val examples = list.mkString(", ")
        s"$bucket ${list.size} found: ($examples)"
      }.mkString(" | ")
    }
  }

  // Range: the difference between the highest and lowest values
  def range(records: List[EnergyRecord]): Double = {
    val values = getValues(records)
    if (values.isEmpty) 0.0
    else values.max - values.min
  }

  // Midrange: sum of the highest and lowest divided by 2
  def midrange(records: List[EnergyRecord]): Double = {
    val values = getValues(records)
    if (values.isEmpty) 0.0
    else (values.max + values.min) / 2.0
  }
}