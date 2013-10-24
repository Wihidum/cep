<%@ page import="org.wso2.carbon.cep.stub.admin.internal.xsd.*" %>
<%@ page import="java.util.LinkedList" %>
<%@ page import="java.util.List" %>
<%
    String lbip = request.getParameter("lbip");
    String indexs   =   request.getParameter("tableIndex");
    int index = Integer.parseInt(indexs);
    if(lbip!= null){
        lbip = lbip.trim();
    }

    List outputNodeList = (List) session.getAttribute("outputnodes");

    LinkedList<LoadbalancerDTO> lbs = (LinkedList<LoadbalancerDTO>) session.getAttribute("loadbalancers");

    if (lbs == null) {
        lbs = new LinkedList<LoadbalancerDTO>();
        session.setAttribute("loadbalancers", lbs);
    }

    LoadbalancerDTO lb;
    if (lbs.size() > index) {
        lb = lbs.get(index);
    } else {
        lb = new LoadbalancerDTO();
        lbs.add(lb);
    }

    lb.setIp(lbip);

   LBOutputNodeDTO[] outarray = new LBOutputNodeDTO[outputNodeList.size()];
    for(int i=0;i<outputNodeList.size();i++){
        outarray[i]=(LBOutputNodeDTO)outputNodeList.get(i);
    }
    lb.setLbOutputNodeDTOs(outarray);
    session.removeAttribute("outputnodes");
%>