package com.VegaSolutions.lpptransit.travanaserver.Objects.responses;

public class ResponseObject<T> {

    private boolean success;
    private T data;
    private int response_code;
    private String internal_error;

    public boolean isSuccess() {
        return success;
    }

    public T getData() {
        return data;
    }

    public int getResponse_code() {
        return response_code;
    }

    public String getInternal_error() {
        return internal_error;
    }

    @Override
    public String toString() {
        return "ResponseObject{" +
                "success=" + success +
                ", data=" + data +
                ", response_code=" + response_code +
                ", internal_error='" + internal_error + '\'' +
                '}';
    }
}
