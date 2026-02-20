<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <title>Admin Category - Edit</title>
<%@include file="/WEB-INF/JSPViews/global/htmlHead.jsp" %>
    </head>
    <body>
<%@include file="/WEB-INF/JSPViews/global/pageHeader.jsp" %>
        <div class="col-lg-8 mx-auto">
            <div class="my-3 p-3 bg-body rounded shadow-sm">
                <form id="hashForm" class="p-4 p-md-5 rounded-3" action="<%= request.getContextPath()%>/admin/category?action=edit" method="POST">
                    <h2>Sửa đổi category</h2>
                    <br>
                    <div class="form-floating mb-3"> 
                        <input name="oldName" type="text" class="form-control" value="<%= request.getParameter("oldName") %>" readonly> 
                        <label for="floatingInput">Tên cũ</label>
                    </div>
                    <div class="form-floating mb-3"> 
                        <input name="newName" type="text" class="form-control"> 
                        <label for="floatingInput">Tên mới</label>
                    </div>
                    <div class="form-floating mb-3"> 
                        <input name="description" type="text" class="form-control"> 
                        <label for="floatingInput">Phụ chú</label> 
                    </div>
                    <button class="w-100 btn btn-lg btn-primary" type="submit">Submit</button> 
                </form>
            </div>
        </div>
<%@include file="/WEB-INF/JSPViews/global/htmlScripts.jsp" %>
    </body>
</html>
