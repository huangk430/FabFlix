package com.github.klefstad_teaching.cs122b.gateway.filter;

public class POJO {
    private MyCustomPojoResult result;

    public MyCustomPojoResult getResult() {
        return result;
    }

    public POJO setResult(MyCustomPojoResult result) {
        this.result = result;
        return this;
    }
}
