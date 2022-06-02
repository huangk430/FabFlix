package com.github.klefstad_teaching.cs122b.idm.util;

import com.github.klefstad_teaching.cs122b.core.result.Result;
import org.springframework.stereotype.Component;

@Component
public final class Validate
{
    private Result result;

    public Result getResult() {
        return result;
    }

    public Validate setResult(Result result) {
        this.result = result;
        return this;
    }
}
