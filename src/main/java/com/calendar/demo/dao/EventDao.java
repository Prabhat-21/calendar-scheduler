package com.calendar.demo.dao;

import com.calendar.demo.dto.EventRequest;
import com.calendar.demo.entities.Event;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.List;


public interface EventDao {
    Long createEvent(EventRequest eventRequest);

    List<Event> getAllEventsByUserId(Long userId);

    List<Event> getAllEventsByUserIdAndDate(Long userId, final Date date);
}
