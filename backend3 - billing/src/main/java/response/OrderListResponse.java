package response;

import com.github.klefstad_teaching.cs122b.core.result.Result;
import data.Sale;

import java.util.List;

public class OrderListResponse {
    public Result getResult() {
        return result;
    }

    public OrderListResponse setResult(Result result) {
        this.result = result;
        return this;
    }

    public List<Sale> getSales() {
        return sales;
    }

    public OrderListResponse setSales(List<Sale> sales) {
        this.sales = sales;
        return this;
    }

    private Result result;
    private List<Sale> sales;

}
