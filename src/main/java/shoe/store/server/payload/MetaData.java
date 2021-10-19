package shoe.store.server.payload;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MetaData {
    private int page;
    private int pages;
    private int perpage;
    private long total;
    private String sort = "desc";
    private String field = "createdAt";

    public MetaData() {
        this.setPage(1);
        this.setPerpage(10);
    }

    public MetaData(int page, int pages, int perpage, long total) {
        this.setPage(page);
        this.setPages(pages);
        this.setPerpage(perpage);
        this.setTotal(total);
    }
}
