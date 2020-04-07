package com.udacity.asteroidradar.utils

import com.udacity.asteroidradar.Constants
import java.text.SimpleDateFormat
import java.util.*

fun today(): String {
    return SimpleDateFormat(Constants.API_QUERY_DATE_FORMAT).format(Date())
}