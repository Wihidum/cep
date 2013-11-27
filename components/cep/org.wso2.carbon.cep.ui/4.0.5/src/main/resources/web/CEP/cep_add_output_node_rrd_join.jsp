<%@ page import="org.wso2.carbon.cep.stub.admin.internal.xsd.*" %>
<%@ page import="java.util.LinkedList" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList"%>
<%
    String outputnodeid = request.getParameter("id");
  //  String indexs   =   request.getParameter("tableIndex");
   // int index = Integer.parseInt(indexs);
    if(outputnodeid!= null){
        outputnodeid = outputnodeid.trim();
    }

    List list = (List) session.getAttribute("rrdjoinoutputnodes");
           if (list == null) {
               list = new ArrayList();
               session.setAttribute("rrdjoinoutputnodes",list);
           }
           LBOutputNodeDTO lbout = new LBOutputNodeDTO();
           lbout.setId(outputnodeid.trim());
           list.add(lbout);
%>