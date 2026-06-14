package com.example.demo.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/*
GET /summary?customer_id=cust-123&start_time=...&end_time=...
Response: 200 — {
  "customer_id": "cust-123",
  "total_events": 150,
  "event_breakdown": { "login": 100, "purchase": 25, "logout": 25 }
}
 */

public record GetSummaryReqeust(
    
    @NotBlank
    @JsonProperty("customer_id")
    String customer_id,

    @JsonProperty("start_time")
    LocalDate start_time,

    @JsonProperty("end_time")
    LocalDate end_time

) {
}
