
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.List" %>
<%@ page import="org.wso2.carbon.cep.stub.admin.internal.xsd.LBOutputNodeDTO" %>
<%

    String ipadd = request.getParameter("ipadd");
    String port = request.getParameter("portadd");

        List list = (List) session.getAttribute("outputnodes");
        if (list == null) {
            list = new ArrayList();
            session.setAttribute("outputnodes", list);
        }
        LBOutputNodeDTO lbout = new LBOutputNodeDTO();
        lbout.setIp(ipadd.trim());
        lbout.setPort(port.trim());
        list.add(lbout);
%>
