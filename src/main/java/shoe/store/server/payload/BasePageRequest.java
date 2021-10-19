package shoe.store.server.payload;

import org.springframework.util.MultiValueMap;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BasePageRequest {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Pagination {
        private int page = 1;
        private int perpage = 10;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Query {
        private String status;
        private String generalSearch;
        private String customerId;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Sort {
        private String sort;
        private String field;
    }

    private Pagination pagination;
    private Sort sort;
    private Query query;

    public static BasePageRequest getObject(MultiValueMap<String, String> object) {
        object.add("sort[sort]", "");
        object.add("sort[field]", "");
        object.add("query[status]", "");
        object.add("query[generalSearch]", "");
        object.add("query[customerId]", "");
        Pagination pagination = new Pagination(Integer.parseInt(object.get("pagination[page]").get(0)),
                Integer.parseInt(object.get("pagination[perpage]").get(0)));
        Sort sort = new Sort(object.getFirst("sort[sort]"), object.getFirst("sort[field]"));
        Query query = new Query(object.getFirst("query[status]"), object.getFirst("query[generalSearch]"),
                object.getFirst("query[customerId]"));
        return new BasePageRequest(pagination, sort, query);
    }

}
