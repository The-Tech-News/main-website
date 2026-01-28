<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en" data-bs-theme="auto">
    <head>
        <title>The Tech News - Home Page</title>
<%@include file="/WEB-INF/JSPViews/_globalImports/htmlHead.jsp" %>
    </head>
    <body>
<%@include file="/WEB-INF/JSPViews/_globalImports/pageHeader.jsp" %>
        <div class="px-4 pt-5 my-5 text-center border-bottom">
            <h1 class="display-4 fw-bold text-body-emphasis">It's up and running</h1>
            <div class="col-lg-6 mx-auto">
                <p class="lead mb-4">If you are seeing this line, that means your project is built and running correctly.</p>
                <div class="d-grid gap-2 d-sm-flex justify-content-sm-center mb-5"> 
                    <button type="button" class="btn btn-primary btn-lg px-4 me-sm-3">
                        <a style="color: white; text-decoration-line: none" href="https://github.com/The-Tech-News/main-website/actions" target="_blank">View workflow</a>
                    </button> 
                </div>
            </div>
        </div>
<%@include file="/WEB-INF/JSPViews/_globalImports/htmlScripts.jsp" %>
    </body>
</html>
