package com.example.demo.dao;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.Event;

@Repository
public interface EventDao extends JpaRepository<Event, String> {

    Optional<Event> findEventById(String id);

    List<Event> findEventsByCustomerId(String customerId, int limit);

    List<Event> findEventsByTimestampBetween(LocalDate startDate, LocalDate endDate);

    List<Event> findTopEventsByTimestamp(int limit);
    
}
