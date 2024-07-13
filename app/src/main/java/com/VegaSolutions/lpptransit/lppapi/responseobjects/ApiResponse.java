package com.VegaSolutions.lpptransit.lppapi.responseobjects;

public class ApiResponse<T> {

    private boolean success;
    private T data;
    private String message;
    private String type;

    /**
     * @return Queried API data.
     */
    public ApiResponse(boolean success, T data, String message, String type) {
        this.success = success;
        this.data = data;
        this.message = message;
        this.type = type;
    }

    public T getData() {
        return data;
    }
    public String getMessage() {
        return message;
    }
    public String getType() {
        return type;
    }
    public boolean isSuccess() {
        return success;
    }

}
