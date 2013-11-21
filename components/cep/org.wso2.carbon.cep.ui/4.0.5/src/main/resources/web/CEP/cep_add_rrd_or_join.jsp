<%@ page import="org.wso2.carbon.cep.stub.admin.internal.xsd.*" %>
<%@ page import="java.util.LinkedList" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList"%>
<%
    String lbid = request.getParameter("lbid");
    String type = request.getParameter("lbtype");
  //  String indexs   =   request.getParameter("tableIndex");
   // int index = Integer.parseInt(indexs);
    if(lbid!= null){
        lbid = lbid.trim();
    }
    if(type!= null){
            type = type.trim();
        }

    List listtwo = (List) session.getAttribute("rrdjoinnodes");
           if (listtwo == null) {
               listtwo = new ArrayList();
               session.setAttribute("rrdjoinnodes", listtwo);
           }

           List listoutput = (List)session.getAttribute("rrdjoinoutputnodes");

          InnerOutputNodesDTO outpp = new  InnerOutputNodesDTO();
          outpp.setId(lbid);
          outpp.setType(type);
         if (listoutput != null){

            LBOutputNodeDTO[] outarray = new LBOutputNodeDTO[listoutput.size()];
                for(int i=0;i<listoutput.size();i++){
                    outarray[i]=(LBOutputNodeDTO)listoutput.get(i);
                }
              outpp.setLbOutputNodeDTOs(outarray);
            session.removeAttribute("rrdjoinoutputnodes");
          }

           listtwo.add(outpp);
%>