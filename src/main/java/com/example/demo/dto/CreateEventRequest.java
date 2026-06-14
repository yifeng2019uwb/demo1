package com.example.demo.dto;

import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
/*
Body: {
  "customer_id": "cust-123",
  "event_type": "login",
  "timestamp": "2026-06-12T10:00:00Z",
  "metadata": { "ip": "10.0.0.1" }   ← optional
}
Response: 201 — { "event_id": "uuid" } */

public record CreateEventRequest(

    @NotBlank
    @JsonProperty("customer_id")
    String customer_id,

    @NotBlank
    @JsonProperty("event_type")
    String event_type,

    @NotNull
    @JsonProperty("timestamp")
    LocalDate timestamp,

    @NotNull
    @JsonProperty("metadata")
    Map<String, String> metadata
) {
}