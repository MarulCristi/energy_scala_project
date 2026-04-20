package com.rockthejvm

import io.github.cdimascio.dotenv.Dotenv

object PrintMenu {
  def main(args: Array[String]): Unit = {
    val dotenv = Dotenv.load()

    // Safely retrieve the key (Option gives us functional error handling if it's missing)
    val apiKey: Option[String] = Option(dotenv.get("FINGRID_API_KEY"))

    // Pattern match to check if it worked, without printing the actual key
    apiKey match {
      case Some(_) => println("Success: API key loaded from .env!")
      case None    => println("Error: FINGRID_API_KEY not found in .env file.")
    }
  }

}