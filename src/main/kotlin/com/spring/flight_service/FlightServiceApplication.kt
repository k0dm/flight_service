package com.spring.flight_service

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching

@SpringBootApplication
@EnableCaching
class FlightServiceApplication

fun main(args: Array<String>) {
	runApplication<FlightServiceApplication>(*args)
}
