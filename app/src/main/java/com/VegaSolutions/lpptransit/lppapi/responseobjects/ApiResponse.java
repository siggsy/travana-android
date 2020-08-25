package com.VegaSolutions.lpptransit.lppapi.responseobjects;

public class ApiResponse<T> {

    private boolean success;
    private T data;

    /**
     * @return Queried API data.
     */
    public ApiResponse(boolean success, T data) {
        this.success = success;
        this.data = data;
    }

    public T getData() {
        return data;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public void setData(T data) {
        this.data = data;
    }
    /**
     * @return Boolean if API server succeeded.
     */
    public boolean isSuccess() {
        return success;
    }

}
