<%@page import="Models.Objects.Post"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.HashMap"%>
<%@page import="Models.Objects.Category"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en" data-bs-theme="auto">
    <head>
        <title>The Tech News - Home Page</title>
        <%@include file="/WEB-INF/JSPViews/global/htmlHead.jsp" %>
    </head>
    <body>
        <%@include file="/WEB-INF/JSPViews/global/pageHeader.jsp" %>

        <%
            ArrayList<Post> postList = (ArrayList<Post>) request.getAttribute("post");
            HashMap<Integer, Category> categoryList = (HashMap<Integer, Category>) request.getAttribute("category");
            HashMap<Integer, String> userList = (HashMap<Integer, String>) request.getAttribute("users");
        %>
        <br>
        <main class="container">
            <div class="row mb-2">
                <% for (Post p : postList) {%>
                <div class="col-md-6">
                    <div class="row g-0 border rounded overflow-hidden flex-md-row mb-4 shadow-sm h-md-250 position-relative">
                        <div class="col p-4 d-flex flex-column position-static">
                            <strong class="d-inline-block mb-2 text-primary-emphasis"><%= categoryList.get(p.getCategoryId()).getName()%></strong>
                            <h3 class="mb-0"><%= p.getTitle()%></h3>
                            <div class="mb-1 text-body-secondary">By <%= userList.get(p.getUserId())%></div>
                            <a href="<%= request.getContextPath()%>/post?id=<%= p.getId()%>" class="icon-link gap-1 icon-link-hover stretched-link">
                                Continue reading
                                <svg class="bi" aria-hidden="true">
                                <use xlink:href="#chevron-right"/>
                                </svg>
                            </a>
                        </div>
                    </div>
                </div>
                <%  }%>
            </div>
            <hr class="col-3 col-md-2 mb-5">
            <div class="row mb-2">
                <h1 class="text-body-emphasis">View all posts</h1>
                <p>View all posts on the website. Free and no need to register anything.</p>
                <div class="mb-5"> 
                    <a href="<%= request.getContextPath()%>/post" class="btn btn-primary btn-lg px-4">View all posts</a> 
                </div>
            </div>

            <div class="row g-5">
                <div class="col-md-6">
                    <h2 class="text-body-emphasis">For administrator</h2>
                    <ul class="list-unstyled ps-0">
                        <li>
                            <a class="icon-link mb-1" href="<%= request.getContextPath()%>/admin/category" rel="noopener" target="_blank">Category Editing</a>
                        </li>
                        <li>
                            <a class="icon-link mb-1" href="<%= request.getContextPath()%>/admin/stat" rel="noopener" target="_blank">View stat</a>
                        </li>
                    </ul>
                </div>
                <div class="col-md-6">
                    <h2 class="text-body-emphasis">For registered users</h2>
                    <ul class="list-unstyled ps-0">
                        <li>
                            <a class="icon-link mb-1" href="<%= request.getContextPath()%>/admin/post" rel="noopener" target="_blank">Post Editor</a>
                        </li>
                        <% String realmHome = System.getenv("WEB_REALM_OIDC_HOME"); %>

                        <% if (realmHome != null) {%>
                        <li>
                            <a class="icon-link mb-1" href="<%= realmHome%>" rel="noopener" target="_blank">Manage your account (OIDC)</a>
                        </li>
                        <% }%>
                    </ul>
                </div>
            </div>
        </main>
        <%@include file="/WEB-INF/JSPViews/global/footer.jsp" %>
        <%@include file="/WEB-INF/JSPViews/global/htmlScripts.jsp" %>
    </body>
</html>
