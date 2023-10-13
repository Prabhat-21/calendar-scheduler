package com.calendar.demo.dao;

import com.calendar.demo.dto.EventRequest;
import com.calendar.demo.entities.Event;
import com.calendar.demo.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.math.BigInteger;
import java.sql.*;
import java.util.List;
import java.util.Optional;

import static com.calendar.demo.service.EventService.MILLIS_DAY;

@Repository
public class EventDaoImpl implements EventDao {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public EventDaoImpl(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Autowired
    UserDao userDao;

    @Override
    public Long createEvent(EventRequest eventRequest) {
        List<Long> userIds = eventRequest.getUserIds();
        userIds.forEach(id -> {
            Optional<User> user = userDao.getUser(id);
            if (user == null) {
                throw new NullPointerException("user does not exist");
            }
        });
        String sql = "INSERT INTO events (name, startTime, endTime) VALUES (?, ?, ?)";
        final KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(conn -> {
            PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, eventRequest.getName());
            pstmt.setObject(2, eventRequest.getStartTime());
            pstmt.setObject(3, eventRequest.getEndTime());
            return pstmt;
        }, keyHolder);

        BigInteger eventId = keyHolder.getKeyAs(BigInteger.class);
        userIds.forEach(userId -> {
            String mappingQuery = "INSERT INTO user_event_mapping (userId, eventId) VALUES(?,?)";
            jdbcTemplate.update(mappingQuery, userId, eventId.longValue());
        });
        return eventId.longValue();
    }

    @Override
    public List<Event> getAllEventsByUserId(Long userId) {
        String sql = "Select e.* from events AS e INNER JOIN user_event_mapping AS uem ON uem.eventId=e.id where uem.userId=?";
        return jdbcTemplate.query(sql, new Object[]{userId.toString()}, new EventRowMapper());

    }

    public List<Event> getAllEventsByUserIdAndDate(Long userId, final Date date) {
        Timestamp startTime = new Timestamp(date.getTime());
        Timestamp endTime = new Timestamp(startTime.getTime() + MILLIS_DAY);
        String sql = "Select e.* from events AS e INNER JOIN user_event_mapping AS uem ON uem.eventId=e.id where uem.userId=? AND e.startTime>=? AND e.endTime<=?";
        return jdbcTemplate.query(sql, new Object[]{userId.toString(), startTime.toString(), endTime.toString()}, new EventRowMapper());
    }

    private final class EventRowMapper implements RowMapper<Event> {
        @Override
        public Event mapRow(final ResultSet rs, final int rowNum) throws SQLException {
            final Event event = new Event();
            event.setId(rs.getLong("id"));
            event.setName(rs.getString("name"));
            event.setStartTime(rs.getTimestamp("startTime"));
            event.setEndTime(rs.getTimestamp("endTime"));
            return event;
        }
    }
}
