<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>List Users</title>
</head>

<body class="my-login-page">
<jsp:include page="templates/userHeader.jsp"/>
<section class="">
    <div class="container ">
        <div class="row justify-content-md-center">
            <div class="card">
                <div class="card-header">
                    <h4 class="text-center">Chỉ tài khoản Quản Trị Viên mới có quyền truy cập!</h4>
                </div>
                <div class="card card-body table-responsive">
                </div>
            </div>
            <jsp:include page="templates/copyright.jsp"/>
        </div>
    </div>
</section>
<jsp:include page="templates/footer.jsp"/>
</body>
</html>