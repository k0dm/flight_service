package com.spring.flight_service.repository

import com.spring.flight_service.model.Flight
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface FlightRepository : CrudRepository<Flight, Long> {
}