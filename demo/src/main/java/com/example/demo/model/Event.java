package com.example.demo.model;

import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Index;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table (name = "events", 
    indexes = {
        @Index(name = "idx_customer_id", columnList = "customer_id"),
        @Index(name = "idx_timestamp_desc", columnList = "timestamp DESC") 
    } 
)
public class Event   {
    
    @Id
    @Column(name = "id")
    private String id;
    
    @Column(name = "customer_id")
    private String customerId;

    @Column(name = "type")
    private String type;

    @Column(name = "timestamp")
    private LocalDate timestamp;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "metadata")
    private Map<String, String> metadata;

    protected Event(){}

    public Event(String customerId, String type, LocalDate timestamp, Map<String, String> metadata) {
        this.id = UUID.randomUUID().toString();
        this.customerId = customerId;
        this.type = type;
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getCustomerId() {
        return customerId;
    }

    public LocalDate getTimestamp() {
        return timestamp;
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }
}
