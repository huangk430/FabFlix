package com.github.klefstad_teaching.cs122b.gateway.filter;

public class MyCustomPojoResult {
    private Integer code;
    private String message;

    public Integer getCode() {
        return code;
    }

    public MyCustomPojoResult setCode(Integer code) {
        this.code = code;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public MyCustomPojoResult setMessage(String message) {
        this.message = message;
        return this;
    }
}
