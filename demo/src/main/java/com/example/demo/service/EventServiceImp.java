package com.example.demo.service;

import java.util.UUID;
import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

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
import com.example.demo.ValidationException;
import java.util.Optional;
import java.util.stream.Collectors;



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
            validateUUID(request.customer_id());
            validateTimestamp(request.timestamp());
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
            validateUUID(event_id);
            // return new GetEventResponse(
            //     "event_123", "customer_456", "Event Name", LocalDate.now(), null
            // );
            Optional<Event> event = eventDao.findEventById(event_id);
            log.atInfo().log("Event retrieved with ID: {} , result: {}", event_id, event.orElse(null));
            if (event.isPresent()) {
                return new GetEventResponse(
                    event.get().getId(),
                    event.get().getCustomerId(),
                    event.get().getType(),
                    event.get().getTimestamp(),
                    event.get().getMetadata()
                );
            } else {
                log.atWarn().log("Event not found with ID: {}", event_id);
                throw new ValidationException("Event not found with ID: " + event_id);
            }
        }catch (Exception e) {
            throw new UnsupportedOperationException("Unimplemented method 'getEvent'");
        }
    }

    @Override
    public GetSummaryResponse getSummary(String customer_id, LocalDate startTime, LocalDate endTime) {
        // TODO Auto-generated method stub
        try {
            validateUUID(customer_id);
            validateTimestamp(startTime);
            validateTimestamp(endTime);
            if (startTime.isAfter(endTime)) {
                throw new ValidationException("Start time cannot be after end time");
            }
            // return new GetSummaryResponse(
            //     "customer_456", 0, null
            // );
            List<Event> events = eventDao.findEventsByTimestampBetween(startTime, endTime);
            log.atInfo().log("Summary retrieved for customer: {}, start time: {}, end time: {}, event count: {}", customer_id, startTime, endTime, events.size());

            return new GetSummaryResponse(
                customer_id,
                events.size(),
                countEvents(events)
            );
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
            if (limit <= 0) {
                throw new ValidationException("Limit must be a positive integer");
            }
            if (limit > 100) {
                throw new ValidationException("Limit cannot exceed 100");
            }
            List<Event> events = eventDao.findTopEventsByTimestamp(limit);
            log.atInfo().log("Top events retrieved with limit: {}, event count: {}", limit, events.size());
            return wrapperResponse(events);
        }catch (Exception e) {
            throw new UnsupportedOperationException("Unimplemented method 'getTopEvents'");
        }
    }
    
    private void validateTimestamp(LocalDate timestamp) {
        if (timestamp == null) {
            throw new ValidationException("Timestamp cannot be null");
        }
        if (timestamp.isAfter(LocalDate.now())) {
            throw new ValidationException("Timestamp cannot be in the future");
        }
    }

    private UUID validateUUID(String uuid) {
        if (uuid == null) {
            throw new ValidationException("UUID cannot be null");
        }
        try {
            return UUID.fromString(uuid);
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Invalid UUID format");
        }
    }

    private Map<String, Integer> countEvents(List<Event> events) {
        Map<String, Integer> eventCount = new HashMap<>();
        for (Event event : events) {
            eventCount.put(event.getType(), eventCount.getOrDefault(event.getType(), 0) + 1);
        }
        return eventCount;
    }

    private ListTopEventsResponse wrapperResponse(List<Event> events) {
        List<ListTopEventsResponse.Item> items = events.stream()
            .map(event -> new ListTopEventsResponse.Item(event.getType(), 1))
            .collect(Collectors.toList());
        return new ListTopEventsResponse(items);
    }

}
