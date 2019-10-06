package com.VegaSolutions.lpptransit.lppapideprecated.responseclasses;

public class ApiResponse<T> {

    private boolean success;
    private T data;

    /**
     * @return Queried API data.
     */
    public T getData() {
        return data;
    }

    /**
     * @return Boolean if API server succeeded.
     */
    public boolean isSuccess() {
        return success;
    }

}
