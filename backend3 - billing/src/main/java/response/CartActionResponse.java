package response;

import com.github.klefstad_teaching.cs122b.core.result.Result;

public class CartActionResponse {
    private Result result;

    public Result getResult() {
        return result;
    }

    public CartActionResponse setResult(Result result) {
        this.result = result;
        return this;
    }
}
