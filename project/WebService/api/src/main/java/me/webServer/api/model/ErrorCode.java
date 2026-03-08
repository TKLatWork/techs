package me.webServer.api.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ErrorCode {

    INVALID_REQUEST_BODY(400, "Bad request: The request body is not valid."),
    ORDER_ID_CONFLICT(409, "Conflict: The orderId already exists or the order is already cancelled/finished."),
    ORDER_ID_NOT_FOUND(404, "Not found: The orderId does not exist.");

    private final int code;
    private final String message;

    public <T> Response<T> toResponse() {
        return new Response<>(this.code, this.message, null);
    }

    public <T> Response<T> toResponse(T data) {
        return new Response<>(this.code, this.message, data);
    }

    public <T> Response<T> toResponse(String appendMessage, T data) {
        return new Response<>(this.code, this.message + "\n" + appendMessage, data);
    }
}
