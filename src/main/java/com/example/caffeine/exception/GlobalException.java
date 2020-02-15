package com.example.caffeine.exception;

import lombok.Data;

/**
 * 全局异常
 *
 * @author Alex
 */
@Data
public class GlobalException extends RuntimeException {

    private static final long serialVersionUID = -6078109373603077190L;

    private String errorCode;

    private String errorMessage;

    public GlobalException() {
        super();
    }

    public GlobalException(String errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }
}
