<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <title>Admin Category - No Access</title>
<%@include file="/WEB-INF/JSPViews/global/htmlHead.jsp" %>
    </head>
    <body>
<%@include file="/WEB-INF/JSPViews/global/pageHeader.jsp" %>
        <div class="px-4 pt-5 my-5 text-center border-bottom">
            <h1 class="display-4 fw-bold text-body-emphasis">Access Denied</h1>
            <div class="col-lg-6 mx-auto">
                <p class="lead mb-4">You don't have permission to do that.</p>
                <div class="d-grid gap-2 d-sm-flex justify-content-sm-center mb-5"> 
                    <button type="button" class="btn btn-primary btn-lg px-4 me-sm-3">
                        <a style="color: white; text-decoration-line: none" onclick="history.back()">Go Back</a>
                    </button> 
                </div>
            </div>
        </div>
<%@include file="/WEB-INF/JSPViews/global/htmlScripts.jsp" %>
    </body>
</html>
