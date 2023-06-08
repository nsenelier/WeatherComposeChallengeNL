package com.example.restconnection.utils

class InternetConnectionException(message: String = "No internet connection"): Exception(message)

class NoResponseException(message: String = "Response is null"): Exception(message)

class ResponseFailedException(message: String): Exception(message)