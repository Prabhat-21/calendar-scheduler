package com.calendar.demo.service;

import com.calendar.demo.dao.EventDao;
import com.calendar.demo.dto.EventRequest;
import com.calendar.demo.entities.Event;
import com.calendar.demo.response.EventResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;
import java.util.stream.IntStream;

@Service
public class EventService {

    @Autowired
    EventDao eventDao;

    public static Long MILLIS_DAY = 24 * 60 * 60 * 1000L;

    static Long MILLIS_15_MINUTES = 15 * 60 * 1000L;

    public Long createEvent(EventRequest eventRequest) {
        return eventDao.createEvent(eventRequest);
    }

    public EventResponse getAllEventsByUserId(Long userId) {
        List<Event> events = eventDao.getAllEventsByUserId(userId);
        EventResponse eventResponse = new EventResponse();
        eventResponse.setEvents(events);
        return eventResponse;
    }

    public EventResponse getConflictingEvents(Long userId, String date) {

        List<Event> eventsInDay = eventDao.getAllEventsByUserIdAndDate(userId, Date.valueOf(date));
        Set<Event> conflictingEvents = new HashSet<>();

        for (int i = 0; i < eventsInDay.size(); i++) {
            for (int j = i + 1; j < eventsInDay.size(); j++) {
                if ((eventsInDay.get(i).getStartTime().getTime() >= eventsInDay.get(j).getStartTime().getTime() && eventsInDay.get(i).getStartTime().getTime() < eventsInDay.get(j).getEndTime().getTime()) || (eventsInDay.get(i).getEndTime().getTime() <= eventsInDay.get(j).getEndTime().getTime() && eventsInDay.get(i).getEndTime().getTime() > eventsInDay.get(j).getStartTime().getTime())) {
                    conflictingEvents.add(eventsInDay.get(i));
                    conflictingEvents.add(eventsInDay.get(j));
                }
            }
        }
        EventResponse eventResponse = new EventResponse();
        eventResponse.setEvents(conflictingEvents.stream().toList());
        return eventResponse;
    }

    public Event getFreeSlotByUserId(String date, List<Long> userIds, int durationInMinutes) {
        HashMap<Long, Event> eventMap = new HashMap<>();
        userIds.forEach(userId -> {
            eventDao.getAllEventsByUserIdAndDate(userId, Date.valueOf(date)).forEach(event -> eventMap.put(event.getId(), event));
        });
        Timestamp startTime = new Timestamp(Date.valueOf(date).getTime());
        Timestamp endTime = Timestamp.from(Instant.ofEpochMilli(startTime.getTime() + MILLIS_DAY));
        Map<Long, Long> slotCount = new HashMap<>();

        eventMap.values().forEach(event -> {
            for (Long timestamp = event.getStartTime().getTime(); timestamp < event.getEndTime().getTime(); timestamp += MILLIS_15_MINUTES) {
                slotCount.put(timestamp, slotCount.getOrDefault(timestamp, 0L) + 1L);
            }
        });

        int requiredSlots = durationInMinutes / 15;

        for (long timestamp = startTime.getTime(); timestamp < endTime.getTime(); timestamp = timestamp + MILLIS_15_MINUTES) {
            long finalTimestamp = timestamp;
            boolean found = IntStream.range(0, requiredSlots).boxed()
                    .map(slot -> slotCount.getOrDefault(finalTimestamp + slot * MILLIS_15_MINUTES, 0L))
                    .allMatch(count -> count == 0);
            if (found)
                return Event.builder().startTime(new Timestamp(timestamp))
                        .endTime(new Timestamp(timestamp + MILLIS_15_MINUTES * requiredSlots))
                        .build();
        }
        return null;
    }

    public void createRecurringEvent(EventRequest eventRequest, int days) {
        IntStream.range(0, days).forEach(day -> {
            createEvent(eventRequest);
            eventRequest.setStartTime(new Timestamp(eventRequest.getStartTime().getTime() + MILLIS_DAY));
            eventRequest.setEndTime(new Timestamp(eventRequest.getEndTime().getTime() + MILLIS_DAY));
        });
    }
}
