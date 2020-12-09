package com.example.sdcresourcemonitor.util


val  REFRESH_TIME  = 10 * 1000 * 1000 * 1000L

enum class AlertLevel(val level : String) {
    CRITICAL("critical"),
    MAJOR("major"),
    MINOR("minor")
}

val BASE_URL = "http://selfcare.sdc.bsnl.co.in/srmapp/"