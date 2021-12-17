package shoe.store.server.payload;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorMessage {

    public enum StatusCode {
        OK(100, "Thành công"),
        NOT_FOUND(101, "Dữ liệu không tồn tại!"),
        CREATED(102, "Tạo dữ liệu thành công!"),
        EXIST(103, "Dữ liệu đã tồn tại!"),
        MODIFIED(104, "Dữ liệu đã được cập nhật!"),
        NOT_MODIFIED(105, "Dữ liệu không được cập nhật!"),
        NO_CONTENT(106, "Trống"),

        AUTH_SUCCESS(200, "Đăng nhập thành công!"),
        USER_NOT_FOUND(201, "Tài khoản không tồn tại!"),
        USER_WRONG_PASSWORD(202, "Sai mật khẩu!"),
        USER_LOCKED(203, "Tài khoản tạm thời bị khóa. Vui lòng liên hệ quản trị viên!"),
        USER_UNLOCKED(204, "Tài khoản đã được mở khóa!"),
        USER_BANNED(205, "Tài khoản đã bị chặn vĩnh viễn!"),
        USER_CREATED(206, "Tạo tài khoản thành công!"),
        USER_EXIST(207, "Tài khoản đã tồn tại!"),
        USER_MODIFIED(208, "Tài khoản đã được cập nhật!"),
        USER_NOT_MODIFIED(209, "Tài khoản không được cập nhật!"),
        USER_NOT_ROLE(210, "Tài khoản không có quyền truy cập!"),
        USER_UNAUTHORIZED(211, "Tài khoản thiếu token xác thực!"),
        PHONE_EXIST(212, "Số điện thoại đã tồn tại!"),
        EMAIL_INVALID(213, "Email không đúng!"),
        EMAIL_EXIST(212, "Email đã tồn tại!"),

        OUT_OF_STOCK(300, "Số lượng sản phẩm đã hết!"),

        BAD_REQUEST(400, "Không thể xử lý!"),
        UNAUTHORIZED(401, "Mã xác thực không hợp lệ!"),
        REQUEST_TIMEOUT(408, "Quá thời gian xử lý!"),
        INTERNAL_SERVER_ERROR(500, "Lỗi không xác định");
    
        public final int code;
        public final String message;
        private StatusCode(int code, String message) {
            this.code = code;
            this.message = message;
        }
        public static int findByMessage(String message){
            for(StatusCode status : values()){
                if( status.message.equals(message)){
                    return status.code;
                }
            }
            return 500;
        }
    }

    private int statusCode;
    private Date timestamp;
    private String message;
    private String description;
}