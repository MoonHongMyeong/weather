package com.portfolio.weather.scheduler.exception;

import com.portfolio.weather.scheduler.data.type.ApiResponseCode;

public class ApiException extends RuntimeException {
    private final String errorCode;

    public ApiException(String message) {
        super(message);
        this.errorCode = ApiResponseCode.UNKNOWN_ERROR.getStatusCode();
    }

    public ApiException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = ApiResponseCode.UNKNOWN_ERROR.getStatusCode();
    }

    public ApiException(ApiResponseCode responseCode) {
        super(responseCode.getDescription());
        this.errorCode = responseCode.getStatusCode();
    }

    public ApiException(ApiResponseCode responseCode, Throwable cause) {
        super(responseCode.getDescription(), cause);
        this.errorCode = responseCode.getStatusCode();
    }

    public ApiException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public ApiException(String message, String errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }
}
