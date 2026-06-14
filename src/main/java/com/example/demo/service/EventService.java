package com.example.demo.service;

import java.time.LocalDate;
import com.example.demo.dto.CreateEventRequest;
import com.example.demo.dto.CreateEventResponse;
import com.example.demo.dto.GetEventResponse;
import com.example.demo.dto.GetSummaryResponse;
import com.example.demo.dto.ListTopEventsResponse;

public interface EventService {

    public CreateEventResponse createEvent(CreateEventRequest request);

    public GetEventResponse getEvent(String event_id);

    public GetSummaryResponse getSummary(String customer_id, LocalDate startTime, LocalDate endTime);

    public ListTopEventsResponse getTopEvents(int limit);



}
