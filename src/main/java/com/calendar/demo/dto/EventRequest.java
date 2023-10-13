package com.calendar.demo.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class EventRequest {
    @NotNull
    private Timestamp startTime;
    @NotNull
    private Timestamp endTime;
    @NotEmpty
    private List<Long> userIds;
    @NotNull
    @NotBlank
    private String name;
}
