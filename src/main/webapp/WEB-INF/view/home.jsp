<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>List All Users</title>
</head>

<body class="my-login-page">
<jsp:include page="templates/header.jsp"/>
<section class="h-100">
    <div class="container h-100">
        <div class="row justify-content-md-center">
            <div class="card">
                <div class="card-header">
                    <h4 class="float-left">Quản lý tài khoản</h4>
                    <ul class="float-right">
                        <li>
                            <form action="/searchBox" class="form-inline my-2 my-lg-0 ">
                                <input class="form-control mr-sm-2" name="searchTerm" type="search" placeholder="Tìm kiếm"
                                       aria-label="Tìm kiếm">
                                <input type="hidden" name="page" value="0"/>
                                <input type="hidden" name="size" value="${maxTraySize}"/>
                                <input class="btn btn-outline-primary my-2 my-sm-0" value="Tìm kiếm" type="submit">
                            </form>
                        </li>
                    </ul>
                </div>
                <div class="card card-body table-responsive">
                    <c:choose>
                        <c:when test="${allUsers.totalPages > 0}">
                            <table class="table table-hover">
                                <thead>
                                <tr>
                                    <th>Tài khoản</th>
                                    <th>Địa chỉ Email</th>
                                    <th>Số điện thoại</th>
                                    <th>Họ</th>
                                    <th>Tên</th>
                                    <th colspan="2">Hành động</th>
                                </tr>
                                </thead>
                                <tbody>
                                <c:forEach var="user" items="${allUsers.content}">
                                    <tr>
                                        <td>
                                            <label>${user.getUsername()}</label>
                                        </td>
                                        <td>
                                            <label>${user.getEmail()}</label>
                                        </td>
                                        <td>
                                            <label id="phone_${user.getId()}">
                                                    ${user.getPhone()}
                                            </label>
                                            <input required class="form-control" type="text" name="phone"
                                                   value="${user.getPhone()}"
                                                   style="display: none;"
                                                   oninvalid="valid_phone()" oninput="valid_phone()"
                                                   id="text_phone_${user.getId()}">
                                        </td>
                                        <td>
                                            <label id="fname_${user.getId()}">
                                                    ${user.getFirstName()}
                                            </label>
                                            <input required type="text" name="firstName" class="form-control"
                                                   value="${user.getFirstName()}"
                                                   style="display: none;"
                                                   id="text_fname_${user.getId()}">
                                        </td>
                                        <td>
                                            <label id="lname_${user.getId()}">
                                                    ${user.getLastName()}
                                            </label>
                                            <input required class="form-control" type="text" name="firstName"
                                                   value="${user.getLastName()}"
                                                   style="display: none;"
                                                   id="text_lname_${user.getId()}">
                                        </td>
                                        <td>
                                            <a href="/update" id="update_${user.getId()}" class="updateData"
                                               onclick="event.preventDefault();"><i class="fa fa-edit"></i></a>
                                            <a href="/save" id="save_${user.getId()}" class="saveData"
                                               onclick="event.preventDefault();saveData('${user.getId()}');"
                                               style="display: none;"><i class="fa fa-save"></i></a>
                                        </td>
                                        <td><a href="/delete/${user.getId()}" class="deleteData"><i
                                                class="fa fa-trash"></i></a>
                                        </td>
                                    </tr>
                                </c:forEach>
                                </tbody>

                            </table>
                        </c:when>
                        <c:otherwise>
                            <h5>Không thấy user nào... Vui lòng thử lại!</h5>
                        </c:otherwise>
                    </c:choose>

                </div>
            </div>
            <c:if test="${allUsers.totalPages > 0}">
                <nav aria-label="Page navigation example" style="margin:auto;">
                    <ul class="pagination">
                        <c:set var="prev" value="0"/>
                        <c:if test="${param.page > 0}">
                            <c:set var="prev" value="${param.page -1}"/>
                        </c:if>
                        <c:if test="${allUsers.totalPages > 0}">
                            <li class='page-item <c:if test="${empty param.page || param.page eq 0}">disabled</c:if>'>
                                <a class="page-link" href="/home?page=${prev}&size=${maxTraySize}">Trước</a></li>
                        </c:if>
                        <c:forEach var="i" begin="0" end="${allUsers.totalPages -1}">
                            <li class='page-item <c:if test="${param.page eq i || (empty param.page && i eq 0)}">active</c:if>'>
                                <a class="page-link" href="/home?page=${i}&size=${maxTraySize}">${i+1}</a>
                            </li>
                        </c:forEach>
                        <c:if test="${allUsers.totalPages > 0}">
                            <li class='page-item <c:if test="${allUsers.totalPages <= (param.page + 1)}">disabled</c:if>'>
                                <a class="page-link" href="/home?page=${param.page + 1}&size=${maxTraySize}">Sau</a>
                            </li>
                        </c:if>
                    </ul>
                </nav>
            </c:if>

            <%--<nav aria-label="Page navigation example" style="margin:auto;">
                <ul class="pagination">
                    <li class="page-item"><a href="/prev/${currentPage-1}" class="page-link pageClickEvent">Previous</a>
                    </li>
                    <c:choose>
                        <c:when test="${totalPages gt maxTraySize}">
                            <c:set var="upperLimit" value="${totalPages - maxTraySize}"></c:set>
                            <c:choose>
                                <c:when test="${upperLimit gt maxTraySize}">
                                    <c:forEach var="page" begin="${currentPage}" end="${currentPage + maxTraySize - 1}">
                                        <li class="page-item"><a href="/page/${page}"
                                                                 class="page-link pageClickEvent">${page}</a></li>
                                    </c:forEach>
                                    <li class="page-item"><a href="/next/${currentPage+1}"
                                                             class="page-link pageClickEvent">Next</a></li>
                                </c:when>
                                <c:otherwise>
                                    <c:forEach var="page" begin="${currentPage}" end="${currentPage + upperLimit}">
                                        <li class="page-item"><a href="/page/${page}"
                                                                 class="page-link pageClickEvent">${page}</a></li>
                                    </c:forEach>
                                    <li class="page-item"><a href="/next/${currentPage+1}"
                                                             class="page-link pageClickEvent">Next</a></li>
                                </c:otherwise>
                            </c:choose>
                            <c:if test="${upperLimit gt maxTraySize}">
                            </c:if>
                        </c:when>
                        <c:otherwise>
                            <c:forEach var="page" begin="1" end="${totalPages}">
                                <li class="page-item"><a href="/page/${page}"
                                                         class="page-link pageClickEvent">${page}</a></li>
                            </c:forEach>
                        </c:otherwise>
                    </c:choose>
                </ul>
                --%>
            <input type="hidden" name="currentPage" value="${currentPage}" id="currentPageNo">
            <%--</nav>--%>
        </div>
    </div>
</section>
<jsp:include page="templates/validateScript.jsp"/>
<jsp:include page="templates/pageScript.jsp"/>
<script type="module"src="resources/js/home.js"></script>
</body>
</html>