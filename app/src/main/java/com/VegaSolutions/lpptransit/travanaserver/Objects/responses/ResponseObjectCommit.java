package com.VegaSolutions.lpptransit.travanaserver.Objects.responses;

public class ResponseObjectCommit {


    private boolean success;
    private int response_code;
    private String internal_error;

    public boolean isSuccess() {
        return success;
    }

    public int getResponse_code() {
        return response_code;
    }

    public String getInternal_error() {
        return internal_error;
    }

    @Override
    public String toString() {
        return "ResponseObjectCommit{" +
                "success=" + success +
                ", response_code=" + response_code +
                ", internal_error='" + internal_error + '\'' +
                '}';
    }
}
