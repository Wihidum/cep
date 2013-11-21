<%@ page import="org.wso2.carbon.cep.stub.admin.internal.xsd.*" %>
<%@ page import="java.util.LinkedList" %>
<%@ page import="java.util.List" %>
<%
   String   message="initated";
   try{
    String lbip = request.getParameter("lbip");
    String typeped = request.getParameter("typeped");
    String indexs   =   request.getParameter("tableIndex");
    int index = Integer.parseInt(indexs);
    if(lbip!= null){
        lbip = lbip.trim();
    }
    if(typeped!= null){
         typeped = typeped.trim();
    }

   List esdNodeList = null;
   List rrdNodeList =null;


           esdNodeList = (List) session.getAttribute("streams");


           rrdNodeList = (List) session.getAttribute("rrdjoinnodes");

     List outputNodeList = (List) session.getAttribute("outputnodes");
     LinkedList<LoadbalancerDTO> lbs = (LinkedList<LoadbalancerDTO>) session.getAttribute("loadbalancers");

     if(lbs == null) {
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
    lb.setType(typeped);
   if(esdNodeList != null){
      StreamDTO[] arrayesd = new StreamDTO[esdNodeList.size()];
                           for(int i=0;i<esdNodeList.size();i++){
                               arrayesd [i]=(StreamDTO)esdNodeList.get(i);
                           }
                         lb.setStreamDTOs(arrayesd);
                       session.removeAttribute("streams");

    }
    if(rrdNodeList !=null){
     InnerOutputNodesDTO[] arrayrrd = new InnerOutputNodesDTO[rrdNodeList.size()];
                     for(int i=0;i<rrdNodeList.size();i++){
                         arrayrrd[i]=(InnerOutputNodesDTO)rrdNodeList.get(i);
                     }
                   lb.setInnerOutputNodesDTOs(arrayrrd);
                 session.removeAttribute("rrdjoinnodes");


    }


   LBOutputNodeDTO[] outarray = new LBOutputNodeDTO[outputNodeList.size()];
    for(int i=0;i<outputNodeList.size();i++){
        outarray[i]=(LBOutputNodeDTO)outputNodeList.get(i);
    }
    lb.setLbOutputNodeDTOs(outarray);
    session.removeAttribute("outputnodes");
     message = "lb added to seesion";
  }catch(Exception e){
         message = "Error in adding bucket :" + e.getMessage();
     }

%>  <%=message%>