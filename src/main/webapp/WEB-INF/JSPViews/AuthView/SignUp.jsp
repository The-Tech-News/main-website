<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <title>The Tech News - Đăng Ký</title>
<%@include file="/WEB-INF/JSPViews/global/htmlHead.jsp" %>
    </head>
    <body>
<%@include file="/WEB-INF/JSPViews/global/pageHeader.jsp" %>
        <div class="container col-xl-10 col-xxl-8 px-4 py-5">
            <div class="row align-items-center g-lg-5 py-5">
                <div class="col-lg-7 text-center text-lg-start">
                    <h1 class="display-4 fw-bold lh-1 text-body-emphasis mb-3">Đăng Ký tài khoản mới</h1>
                </div>
                <div class="col-md-10 mx-auto col-lg-5">
                    <form class="p-4 p-md-5 border rounded-3 bg-body-tertiary">
                        <div class="form-floating mb-3"> 
                            <input type="text" class="form-control" id="floatingInput" placeholder="Nguyễn Văn A"> 
                            <label for="floatingInput">Họ và Tên</label> 
                        </div>
                        <div class="form-floating mb-3"> 
                            <input type="email" class="form-control" id="floatingInput" placeholder="name@example.com"> 
                            <label for="floatingInput">Email</label> 
                        </div>
                        <div class="form-floating mb-3"> 
                            <input type="password" class="form-control" id="floatingPassword" placeholder="Password"> 
                            <label for="floatingPassword">Mật khẩu</label> 
                        </div>
                        <button class="w-100 btn btn-lg btn-primary" type="submit">Đăng Ký</button> 
                    </form>
                </div>
            </div>
        </div>
<%@include file="/WEB-INF/JSPViews/global/htmlScripts.jsp" %>
    </body>
</html>
