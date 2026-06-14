package com.example.demo.service;

import java.time.LocalDate;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.example.demo.dao.EventDao;
import com.example.demo.dto.CreateEventRequest;
import com.example.demo.dto.CreateEventResponse;
import com.example.demo.dto.GetEventResponse;
import com.example.demo.dto.GetSummaryResponse;
import com.example.demo.dto.ListTopEventsResponse;
import com.example.demo.model.Event;


@Service
public class EventServiceImp implements EventService {

    private static final Logger log = LoggerFactory.getLogger(EventService.class);

    private final EventDao eventDao;

    public EventServiceImp(EventDao eventDao) {
        this.eventDao = eventDao;
    }

    @Override
    public CreateEventResponse createEvent(CreateEventRequest request) {
        // TODO Auto-generated method stub
        try {
            Event event = new Event(request.customer_id(), request.event_type(), request.timestamp(), request.metadata());
            Event savedEvent = eventDao.save(event);
            log.atInfo().log("Event created with ID: {}", savedEvent.getId());
            return new CreateEventResponse(
                savedEvent.getId()
            );
        }catch (Exception e) {
            throw new UnsupportedOperationException("Unimplemented method 'createEvent'");
        }
    }

    @Override
    public GetEventResponse getEvent(String event_id) {
        // TODO Auto-generated method stub
        try {
            // return new GetEventResponse(
            //     "event_123", "customer_456", "Event Name", LocalDate.now(), null
            // );
            log.atInfo().log("Event retrieved with ID: {}", event_id);
            return null;
        }catch (Exception e) {
            throw new UnsupportedOperationException("Unimplemented method 'getEvent'");
        }
    }

    @Override
    public GetSummaryResponse getSummary(String customer_id, LocalDate startTime, LocalDate endTime) {
        // TODO Auto-generated method stub
        try {
            // return new GetSummaryResponse(
            //     "customer_456", 0, null
            // );
            log.atInfo().log("Summary retrieved for customer: {}", customer_id);
            return null;
        }catch (Exception e) {
            throw new UnsupportedOperationException("Unimplemented method 'getSummary'");
        }
    }

    @Override
    public ListTopEventsResponse getTopEvents(int limit) {
        // TODO Auto-generated method stub
        try {
            // return new ListTopEventsResponse(
            //     new ArrayList<Integer>()
            // );
            log.atInfo().log("Top events retrieved with limit: {}", limit);
            return null;
        }catch (Exception e) {
            throw new UnsupportedOperationException("Unimplemented method 'getTopEvents'");
        }
    }
    
}
