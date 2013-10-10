
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.List" %>
<%

    String type = request.getParameter("ipadd");
        List list = (List) session.getAttribute("iplist");
        if (list == null) {
            list = new ArrayList();
            session.setAttribute("iplist", list);
        }
        list.add(type.trim());
%>