package com.udacity.asteroidradar.main

import java.util.*

data class AsteroidDataItem(
    val id: String = UUID.randomUUID().toString(),
    val codename: String,
    val closeApproachDate: String,
    val absoluteMagnitude: Double,
    val estimatedDiameter: Double,
    val relativeVelocity: Double,
    val distanceFromEarth: Double,
    val isPotentiallyHazardous: Boolean
)