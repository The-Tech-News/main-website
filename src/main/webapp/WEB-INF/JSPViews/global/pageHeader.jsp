<%@page import="Models.Objects.User"%>
        <nav class="py-2 bg-body-tertiary border-bottom">
            <div class="container d-flex flex-wrap">
                <ul class="nav me-auto">
                    <li class="nav-item"><a href="<%= request.getContextPath()%>/" class="nav-link link-body-emphasis px-2 active" aria-current="page">Home</a></li>
                    <li class="nav-item"><a href="<%= request.getContextPath()%>/admin/category" class="nav-link link-body-emphasis px-2">Category Editing</a></li>
                    <li class="nav-item"><a href="<%= request.getContextPath()%>/admin/posts" class="nav-link link-body-emphasis px-2">Post Editing</a></li>
                </ul>
                <ul class="nav">
                    <%
                        User u = (User) request.getSession().getAttribute("loggedUser");
                        if (u != null) {
                    %>
                    <li class="nav-item" id="signed-in-tab">
                        <a href="<%= request.getContextPath()%>/auth?action=logout" class="nav-link link-body-emphasis px-2">Sign out</a>
                    </li>
                    <%
                    } else {
                    %>
                    <li class="nav-item" id="not-signed-in-tab">
                        <a href="<%= request.getContextPath()%>/auth?action=signin" class="nav-link link-body-emphasis px-2">Login</a>
                    </li>
                    <li class="nav-item" id="not-signed-in-tab-2">
                        <a href="<%= request.getContextPath()%>/auth?action=signup" class="nav-link link-body-emphasis px-2">Sign up</a>
                    </li>
                    <%
                        }
                    %>
                </ul>
            </div>
        </nav>
        <header class="py-3 mb-4 border-bottom">
           <div class="container d-flex flex-wrap justify-content-center">
              <a href="<%= request.getContextPath()%>/" class="d-flex align-items-center mb-3 mb-lg-0 me-lg-auto link-body-emphasis text-decoration-none">
                 <span class="fs-4">The Tech News</span> 
              </a>
           </div>
        </header>