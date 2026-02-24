package com.lnreddy.WhatsAppClone.common.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@AllArgsConstructor
@Builder
public class ExceptionResponse {
    private Instant timestamp;
    private int status;
    private String error;
    private String message;


}
