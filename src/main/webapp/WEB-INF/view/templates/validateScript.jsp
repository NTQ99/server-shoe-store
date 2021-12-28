<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<script>
    function valid_email() {
        let e = window.event;
        let email = e.target.value;
        let emailRegex = /\S+@\S+\.\S+/;
        if (email != "") {
            if (!emailRegex.test(String(email))) {
                e.target.setCustomValidity('Email không đúng định dạng!');
            } else e.target.setCustomValidity('');
        } else {
            e.target.setCustomValidity('Vui lòng nhập địa chỉ email!');
        };
    };

    function valid_phone() {
        let e = window.event;
        let phone = e.target.value;
        let phoneRegex = /(\+84|84|0)+([0-9]{9})/;
        if (phone != "") {
            if (!phoneRegex.test(String(phone))) {
                e.target.setCustomValidity('Số điện thoại không đúng định dạng!');
            } else e.target.setCustomValidity('');
        } else {
            e.target.setCustomValidity('Vui lòng nhập số điện thoại!');
        };
    };

    function valid_pass() {
        let e = window.event;
        let pass = e.target.value;
        let passRegex = /^(?=.*[A-Za-z])(?=.*\d)[A-Za-z\d]{6,}/;
        if (pass != "") {
            if (!passRegex.test(pass)) {
            e.target.setCustomValidity('Phải có ít nhất 6 ký tự, bao gồm cả số và chữ!');
            } else e.target.setCustomValidity('');
        } else {
            e.target.setCustomValidity('Vui lòng nhập mật khẩu!');
        };
    };

</script>