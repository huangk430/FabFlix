package response;

import com.github.klefstad_teaching.cs122b.core.result.Result;
import data.Item;

import java.math.BigDecimal;
import java.util.List;

public class OrderDetailResponse {
    private Result result;
    private BigDecimal total;
    private List<Item> items;

    public Result getResult() {
        return result;
    }

    public OrderDetailResponse setResult(Result result) {
        this.result = result;
        return this;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public OrderDetailResponse setTotal(BigDecimal total) {
        this.total = total;
        return this;
    }

    public List<Item> getItems() {
        return items;
    }

    public OrderDetailResponse setItems(List<Item> items) {
        this.items = items;
        return this;
    }
}
