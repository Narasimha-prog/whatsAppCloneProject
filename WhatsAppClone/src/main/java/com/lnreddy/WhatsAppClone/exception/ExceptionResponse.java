package com.lnreddy.WhatsAppClone.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;

@Data
@AllArgsConstructor
public class ExceptionResponse {
    private Instant timestamp;
    private int status;
    private String error;
    private String message;


}
