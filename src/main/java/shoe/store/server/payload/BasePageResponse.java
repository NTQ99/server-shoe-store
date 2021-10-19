package shoe.store.server.payload;

import java.util.Date;

import org.springframework.web.context.request.WebRequest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import shoe.store.server.exceptions.GlobalException;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BasePageResponse<T> {

    private MetaData meta;
    private ErrorMessage error;
    private T data;

    public BasePageResponse(T data, String msg) {
        this.setData(data);
        this.setMeta(null);
        this.setError(
                new ErrorMessage(ErrorMessage.StatusCode.findByMessage(msg), new Date(), msg, ""));
    };

    public BasePageResponse(T data, int currentPage, int totalPages, int perPage, long totalItems) {
        MetaData meta = new MetaData(currentPage + 1, totalPages, perPage, totalItems);
        this.setData(data);
        this.setMeta(meta);
        this.setError(new ErrorMessage(ErrorMessage.StatusCode.OK.code, new Date(), "", ""));
    };

    public BasePageResponse(GlobalException ex, WebRequest request) {
        this.setData(null);
        this.setMeta(null);
        ErrorMessage message = new ErrorMessage(ErrorMessage.StatusCode.findByMessage(ex.getMessage()), new Date(),
                ex.getMessage(), request.getDescription(false));
        this.setError(message);
    };

}
