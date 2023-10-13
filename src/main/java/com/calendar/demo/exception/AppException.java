package com.calendar.demo.exception;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = false)
public class AppException extends RuntimeException {
    @JsonIgnore
    private HttpStatus statusCode;

    @JsonProperty("error_code")
    private String errorCode;

    private String message;

    private String header;

    private Map<String, Object> information;

    @Builder
    public AppException(final String message, final HttpStatus statusCode, final String errorCode, final String header,
                        final Map<String, Object> information) {
        super(message);
        this.statusCode = statusCode;
        this.errorCode = errorCode;
        this.message = message;
        this.header = header;
        this.information = information;
    }

    public static AppExceptionBuilder newBuilder(AppExceptionBuilder builder) {
        return AppException.builder().statusCode(builder.statusCode).errorCode(builder.errorCode).header(builder.header)
                .information(builder.information).message(builder.message);
    }

    public Map<String, Object> getParamsMap() {
        final Map<String, Object> params = new HashMap<>();
        params.put("error_code", this.errorCode);
        params.put("message", this.message);
        params.put("header", this.header);
        params.put("information", this.information);
        return params;
    }

    public static class AppExceptionBuilder {
        public AppExceptionBuilder information(Map<String, Object> info) {
            this.information = info;
            return this;
        }

        public AppExceptionBuilder information(String key, Object value) {
            if (this.information == null || this.information.isEmpty()) {
                this.information = new HashMap<>();
            }
            this.information.put(key, value);
            return this;
        }

    }
}
