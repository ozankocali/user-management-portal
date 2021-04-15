package com.ozeeesoftware.usermanagementportal.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HttpResponse {
    private int httpStatusCode;

    private HttpStatus httpStatus;

    private String reason;

    private String message;
}
