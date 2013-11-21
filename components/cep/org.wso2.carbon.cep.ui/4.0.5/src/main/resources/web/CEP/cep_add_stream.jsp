<%@ page import="org.wso2.carbon.cep.stub.admin.internal.xsd.*" %>
<%@ page import="java.util.LinkedList" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList"%>
<%
    String stream = request.getParameter("streamid");

  //  String indexs   =   request.getParameter("tableIndex");
   // int index = Integer.parseInt(indexs);
    if(stream!= null){
        stream = stream.trim();
    }


    List list = (List) session.getAttribute("streams");
           if (list == null) {
               list = new ArrayList();
               session.setAttribute("streams", list);
           }

           List listinnerlb = (List)session.getAttribute("rrdjoinnodes");

          StreamDTO outss = new  StreamDTO();
          outss.setId(stream );

         if (listinnerlb != null) {

            InnerOutputNodesDTO[] outarray = new InnerOutputNodesDTO[listinnerlb.size()];
                for(int i=0;i<listinnerlb.size();i++){
                    outarray[i]=(InnerOutputNodesDTO)listinnerlb.get(i);
                }
              outss.setInnerOutputNodesDTOs(outarray);
            session.removeAttribute("rrdjoinnodes");
          }

           list.add(outss);
%>