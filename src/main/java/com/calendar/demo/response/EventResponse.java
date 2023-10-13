package com.calendar.demo.response;

import com.calendar.demo.entities.Event;
import lombok.Data;

import java.util.List;

@Data
public class EventResponse {
    private List<Event> events;
}
