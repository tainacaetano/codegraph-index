package com.dnfeitosa.codegraph.client.http;

public enum StatusCode {

    OK(200),
    BAD_REQUEST(400),
    NOT_FOUND(404);

    private int code;

    StatusCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
