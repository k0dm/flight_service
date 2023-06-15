package com.spring.flight_service.model

import jakarta.persistence.*
import org.springframework.data.redis.core.RedisHash

@Entity
@Table(name = "flight")
@RedisHash("Flight")
data class Flight(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Long = 0,

    @Column(name = "flight_crew_id")
    var flightCrewId: Long? ,

    @Column( name = "from_location", nullable = false)
    var fromLocation: String,

    @Column(name = "to_location", nullable = false)
    var toLocation: String,

    @Column(name = "departure_time", nullable = false)
    var departureTime: String,

    @Column(name = "arrival_time", nullable = false)
    var arrivalTime: String
)