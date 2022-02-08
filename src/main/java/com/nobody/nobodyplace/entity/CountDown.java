package com.nobody.nobodyplace.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = CountDown.TABLE_NAME)
@JsonIgnoreProperties({"handler","hibernateLazyInitializer"})

public class CountDown {
    public static final String TABLE_NAME = "info_count_down";

    @Id
    Long id;
    String eventName;
    String expirationDate;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
    }
}
