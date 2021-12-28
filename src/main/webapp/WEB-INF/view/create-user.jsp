
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width,initial-scale=1">
    <title>Create New User</title>
</head>

<body class="my-login-page">
<jsp:include page="templates/header.jsp"/>
<section class="h-100">
    <div class="container h-100">
        <div class="row justify-content-md-center h-100">
            <div class="card-wrapper">
                <div class="card fat">
                    <div class="card-body">
                        <h4 class="card-title">Tạo User mới</h4>
                        <c:if test="${not empty param.error}">
                            <label id="error" class="alert alert-danger">${param.error}</label>
                        </c:if>
                        <form action="/register" method="POST">

                            <div class="form-group">
                                <label for="firstName">Họ</label>
                                <input id="firstName" type="text" class="form-control" name="firstName" required oninvalid="this.setCustomValidity('Vui lòng nhập họ!')" oninput="this.setCustomValidity('')" autofocus>
                            </div>

                            <div class="form-group">
                                <label for="lastName">Tên</label>
                                <input id="lastName" type="text" class="form-control" name="lastName" required oninvalid="this.setCustomValidity('Vui lòng nhập tên!')" oninput="this.setCustomValidity('')" >
                            </div>

                            <div class="form-group">
                                <label for="email">Địa chỉ Email</label>
                                <input id="email" type="email" class="form-control" name="email" required oninvalid="valid_email()" oninput="valid_email()">
                            </div>

                            <div class="form-group">
                                <label for="phone">Số điện thoại</label>
                                <input id="phone" type="text" class="form-control" name="phone" required oninvalid="valid_phone()" oninput="valid_phone()" >
                            </div>

                            <div class="form-group">
                                <label for="username">Tài khoản</label>
                                <input id="username" type="text" class="form-control" name="username" required oninvalid="this.setCustomValidity('Vui lòng nhập tài khoản!')" oninput="this.setCustomValidity('')" >
                            </div>
                            <div class="form-group">
                                <label for="password">Mật khẩu</label>
                                <input id="password" type="password" class="form-control" name="password" required data-eye oninvalid="valid_pass()" oninput="valid_pass()" >
                            </div>
                            <div class="form-group">
                                <label for="criteriaId">Loại tài khoản</label>
                                <select id="criteriaId" name="roleKey" class="custom-select form-control" required>
                                    <option value="admin">Quản trị viên</option>
                                    <option value="seller">Nhân viên</option>
                                    <option value>Khách hàng</option>
                                </select>
                                <select id="criteriaId2" name="roles" class="custom-select form-control" style="display: none">
                                </select>
                            </div>

                            <div class="form-group no-margin">
                                <button type="submit" class="btn btn-primary btn-block">
                                    Tạo tài khoản
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
                <jsp:include page="templates/copyright.jsp"/>
            </div>
        </div>
    </div>
</section>
<jsp:include page="templates/validateScript.jsp"/>
<jsp:include page="templates/footer.jsp"/>
</body>
</html>