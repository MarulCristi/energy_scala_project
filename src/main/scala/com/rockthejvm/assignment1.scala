package com.rockthejvm


// No AI was used in the homework
// Name: Cristian Taietu
// Student Number: 002298836


object assignment1 {
  def main(args: Array[String]) {
    task1()
    task2()
    task3()
    task4()
    task5()
    task6()
    task7()
    task8(2, 5)
  }

  def task1() {
    // Immutable variable
    val name = "Cristian"
//    name = "test" // This would cause an error. val is immutable and error shows up.

    // Mutable variable -> can be reassigned, not good in scala.
    var age = 20
    age = 21

  }

  def task2() {
    val isNoob = true
    val avgGrade = 3.67
    val years = 7
    val university = "LUT"
  }

  def task3() {
    val char_1: Char = 'T' // declaring the type so it's not treated as a char.
    val char_2: Char = 'a'
    val char_3: Char = 'i'
    val char_4: Char = 'e'
    val char_5: Char = 't'
    val char_6: Char = 'u'
    val last_name = char_1 + char_2 + char_3 + char_4 + char_5 + char_6 // string composition
  }

  def task4() {
    val age = 20
    val message = s"I am learning Scala at the age of $age years" // string interpolation
  }

  def task5() {
    /*
    definition of Expression in Scala:

    An expression is essentially a set of variables, values, operators, or any literal we used
    to compute something. It's anything that produces a value.
    Most Scala programs consists of expressions that return values.

    Examples:
    67 * 69 is an integer expression. It evaluates to an integer
    "Hello" + "World" is another expression. It evaluates to a String
    if (x>0) "Positive" else "Negative. It evaluates to a String.

     */
  }

  def task6() {
    val age_type = "Adult"
    val age = {
      if (age_type == "Kid") 5 // chained if
      else if (age_type == "Adult") 25
      else 65
    }
  }

  def task7(): String = {
   val first_name = "Cristian"
   val last_name = "Taietu"
    first_name + last_name
  }

  def task8(number1: Int, number2: Int): String = {
    val sum = number1 + number2
    "The sum of $number1 and $number2 is: $sum"
  }

}