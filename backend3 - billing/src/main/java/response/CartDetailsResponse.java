package response;

import com.github.klefstad_teaching.cs122b.core.result.Result;
import data.Item;

import java.math.BigDecimal;
import java.util.List;

public class CartDetailsResponse {
    private Result result;
    private BigDecimal total;
    private List<Item> items;

    public Result getResult() {
        return result;
    }

    public CartDetailsResponse setResult(Result result) {
        this.result = result;
        return this;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public CartDetailsResponse setTotal(BigDecimal total) {
        this.total = total;
        return this;
    }

    public List<Item> getItems() {
        return items;
    }

    public CartDetailsResponse setItems(List<Item> items) {
        this.items = items;
        return this;
    }
}
