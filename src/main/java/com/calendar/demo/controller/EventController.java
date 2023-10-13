package com.calendar.demo.controller;

import com.calendar.demo.dto.EventRequest;
import com.calendar.demo.entities.Event;
import com.calendar.demo.response.EventResponse;
import com.calendar.demo.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping(value = "/api/events")
public class EventController {

    @Autowired
    EventService eventService;

    @PostMapping("/create")
    public Long createEvent(@Validated @NotNull @Valid @RequestBody EventRequest eventRequest) {
        return eventService.createEvent(eventRequest);
    }

    @GetMapping("/{user_id}")
    public EventResponse getAllEventsByUserId(@PathVariable("user_id") Long userId) {
        return eventService.getAllEventsByUserId(userId);
    }

    @GetMapping("/conflictingEvents/{user_id}")
    public EventResponse getConflictingEvents(@PathVariable("user_id") Long userId, @RequestParam("date") String date) {
        return eventService.getConflictingEvents(userId, date);
    }

    @GetMapping("/freeSlot")
    public Event getFreeSlotByUserId(@RequestParam("date") String date, @RequestParam("userIds") List<Long> userIds, @RequestParam("duration") int durationInMinutes) {
        return eventService.getFreeSlotByUserId(date, userIds, durationInMinutes);
    }

    @PostMapping("/create/recurring")
    public void createRecurringEvent(@Validated @NotNull @Valid @RequestBody EventRequest eventRequest, @RequestParam("days") int days) {
        eventService.createRecurringEvent(eventRequest, days);
    }
}
