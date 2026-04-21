
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

  // Mode: the value that appears most often. 
  // Returning a List[Double] because there could be multiple modes (a tie).
  def mode(records: List[EnergyRecord]): List[Double] = {
    val values = getValues(records)
    if (values.isEmpty) List.empty[Double]
    else {
      // Group by the value itself to get maps of Map[Double, List[Double]]
      // Something like this:
      // Map(
      //  2.0 -> List(2.0, 2.0, 2.0),
      //  3.0 -> List(3.0, 3.0),
      //  5.0 -> List(5.0)
      //)
      val grouped = values.groupBy(identity)
      // Find the maximum size of the grouped lists
      val maxCount = grouped.values.map(_.size).max
      // Filter out only the values that have the max count, and return them
      grouped.filter { case (_, list) => list.size == maxCount }.keys.toList
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