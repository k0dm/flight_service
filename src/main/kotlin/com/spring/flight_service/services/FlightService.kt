package com.spring.flight_service.services

import com.spring.flight_service.model.Flight
import com.spring.flight_service.repository.FlightRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.CachePut
import org.springframework.cache.annotation.Cacheable
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.*
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import org.springframework.web.util.UriComponentsBuilder

@Service
class FlightService {

    @Autowired
    private lateinit var flightRepository: FlightRepository

    fun createFlight(flight: Flight): ResponseEntity<Flight> {

        var responseEntity: ResponseEntity<List<Long>>? = null

        try {
            val availableCrewResponse = WebClient.create()
                .method(HttpMethod.GET)
                .uri("$FLIGHT_CREW_SERVICE_URL/available-crews")
                .retrieve()
                .toEntity(object : ParameterizedTypeReference<List<Long>>() {})
                .block()
            responseEntity = availableCrewResponse
        } catch (ex: WebClientResponseException) {
            ex.printStackTrace()
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build()
        }

        return if (responseEntity?.statusCode == HttpStatus.OK) {
            val availableCrewIds = responseEntity.body
            if (availableCrewIds.isNullOrEmpty()) {
                ResponseEntity.status(HttpStatus.BAD_REQUEST).build()
            } else {
                val selectedCrewId = availableCrewIds[0]

                val flights = flightRepository.findAll().toList()

                val savedFlight = flightRepository.save(
                    flight.copy(
                        flightCrewId = selectedCrewId,
                        id =  (flights.maxByOrNull { it.id ?: 0 }?.id ?: 0) + 1
                    )
                )

                val patchUrl = UriComponentsBuilder.fromUriString(FLIGHT_CREW_SERVICE_URL)
                    .path("/{id}")
                    .queryParam("isBusy", true)
                    .build(selectedCrewId)
                    .toString()

                val requestBody = mapOf("isBusy" to true)

                try {
                    WebClient.create()
                        .patch()
                        .uri(patchUrl)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .body(BodyInserters.fromValue(requestBody))
                        .retrieve()
                        .toBodilessEntity()
                        .block()

                    ResponseEntity.ok(savedFlight)
                } catch (ex: WebClientResponseException) {
                    ex.printStackTrace()
                    deleteFlightById(savedFlight.id)
                    ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build()
                }
            }
        } else {
            println("StatusCode: ${responseEntity?.statusCode}")
            ResponseEntity.status(HttpStatus.BAD_GATEWAY).build()
        }
    }

    fun updateFlight(updatedFlight: Flight, flightId: Long): ResponseEntity<Flight> {

        val flight = flightRepository.findById(flightId)
        return if (flight.isPresent) {
            val savedFlight = flightRepository.save(updatedFlight.copy(id = flight.get().id))
            ResponseEntity.ok(savedFlight)
        } else {
            ResponseEntity.notFound().build()
        }
    }

    fun getFlightById(flightId: Long): ResponseEntity<Flight> {
        val flight = flightRepository.findById(flightId)
        return if (flight.isPresent) {
            ResponseEntity.ok(flight.get())
        } else {
            ResponseEntity.notFound().build()
        }
    }

    fun getAllFlights(): List<Flight>? {
        return flightRepository.findAll().toList()
    }

    fun deleteFlightById(flightId: Long): ResponseEntity<Flight> {

        return if (flightRepository.existsById(flightId)) {
            flightRepository.deleteById(flightId)
            ResponseEntity.noContent().build()
        } else {
            ResponseEntity.notFound().build()
        }
    }

    companion object {
        private const val FLIGHT_CREW_SERVICE_URL = "http://172.18.0.5:8081/flight-crews"
    }
}