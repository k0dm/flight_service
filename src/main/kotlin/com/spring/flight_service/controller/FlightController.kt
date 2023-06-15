package com.spring.flight_service.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.spring.flight_service.model.Flight
import com.spring.flight_service.services.FlightService
import jakarta.validation.Valid
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/flights")
class FlightController() {

    @Autowired
    private lateinit var flightService: FlightService

    @GetMapping
    fun getAllFlights(): List<Flight>? {
        return flightService.getAllFlights()
    }

    @GetMapping("/{id}")
    fun getFlightById(@PathVariable id: Long): ResponseEntity<Flight> {
        return flightService.getFlightById(id)
    }

    @PostMapping
    fun createFlight(@RequestBody flight: Flight): ResponseEntity<Flight> {
        return flightService.createFlight(flight)
    }



    @PutMapping("/{id}")
    fun updateFlight(
        @PathVariable id: Long,
        @Valid @RequestBody updatedFlight: Flight
    ): ResponseEntity<Flight> {
        return flightService.updateFlight(updatedFlight, id)
    }



    @DeleteMapping("/{id}")
    fun deleteFlight(@PathVariable id: Long): ResponseEntity<Flight> {
      return flightService.deleteFlightById(id)
    }
}