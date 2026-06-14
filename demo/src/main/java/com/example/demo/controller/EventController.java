package com.example.demo.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.CreateEventRequest;
import com.example.demo.dto.CreateEventResponse;
import com.example.demo.dto.GetSummaryReqeust;
import com.example.demo.dto.GetEventResponse;
import com.example.demo.dto.GetSummaryResponse;
import com.example.demo.dto.ListTopEventsResponse;
import com.example.demo.service.EventService;
import com.example.demo.dto.ListTopEventsRequest;

import jakarta.validation.Valid;

/*

POST /events
Body: {
  "customer_id": "cust-123",
  "event_type": "login",
  "timestamp": "2026-06-12T10:00:00Z",
  "metadata": { "ip": "10.0.0.1" }   ← optional
}
Response: 201 — { "event_id": "uuid" }


GET /events/{event_id}
Response: 200 — { event_id, customer_id, event_type, timestamp, metadata }
Response: 404 — if not found


GET /summary?customer_id=cust-123&start_time=...&end_time=...
Response: 200 — {
  "customer_id": "cust-123",
  "total_events": 150,
  "event_breakdown": { "login": 100, "purchase": 25, "logout": 25 }
}

GET /top-events?limit=10
Response: 200 — {
  "results": [
    { "event_type": "login", "count": 120 },
    { "event_type": "purchase", "count": 50 }
  ]
}

GET /health
Response: 200 — { "status": "healthy" }

 */

@RestController
@RequestMapping("/api/v1/events")
public class EventController {

  // private final EventService eventService;
  private static final Logger log = LoggerFactory.getLogger(EventController.class);


  public EventController(){

  }

  @PostMapping
  public ResponseEntity<CreateEventResponse> createEvent(@Valid @RequestBody CreateEventRequest request) {
    // Implementation for creating an event
    log.atInfo().log("Event created with ID: {}", request.customer_id());
    return ResponseEntity.status(201).body(new CreateEventResponse("Event created"));
  }

  @GetMapping("/{event_id}")
  public ResponseEntity<GetEventResponse> getEvent(@PathVariable String event_id) {
    log.atInfo().log("Event retrieved with ID: {}", event_id);
    return ResponseEntity.ok(null);
  }

  @GetMapping("/summary")
  public ResponseEntity<GetSummaryResponse> getSummary(@ModelAttribute GetSummaryReqeust request) {
    log.atInfo().log("Summary retrieved for customer: {}", request.customer_id());
    return ResponseEntity.ok(null);
  }

  @GetMapping("/top-events")
  public ResponseEntity<ListTopEventsResponse> getTopEvents(@ModelAttribute ListTopEventsRequest request) {
    log.atInfo().log("Top events retrieved with limit: {}", request.limit());
    return ResponseEntity.ok(null);
  }

  @GetMapping("/health")
  public ResponseEntity<?> getHealth() {
    return ResponseEntity.ok(null);
  }
}
