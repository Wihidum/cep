<%@ page import="org.wso2.carbon.cep.stub.admin.internal.xsd.LoadbalancerDTO" %>
<%@ page import="java.util.LinkedList" %>
<%@ page import="java.util.Set" %>
<%@ page import="java.util.HashSet" %>
<%
    String[] loadbalancers = request.getParameterValues("loadbalancers");
    String pageNumber = request.getParameter("pageNumber");
    String deleteAlllbs = request.getParameter("deleteAlllbs");
    String bucketName = request.getParameter("bucketName");
    int pageNumberInt = 0;
    String message = "";

    if (pageNumber != null) {
        pageNumberInt = Integer.parseInt(pageNumber);
    }

    if (deleteAlllbs != null) {
        session.setAttribute("loadbalancers", new LinkedList<LoadbalancerDTO>());
        message = "Successfully removed all loadbalancers";
    } else {
        Set<LoadbalancerDTO> lbsToRemove = new HashSet<LoadbalancerDTO>();
        if (loadbalancers != null) {
            LinkedList<LoadbalancerDTO> currentLBs = (LinkedList<LoadbalancerDTO>) session.getAttribute("loadbalancers");
            if (currentLBs != null) {
                for (LoadbalancerDTO lb : currentLBs){
                    for (String removeLB : loadbalancers){
                        if (lb.getIp().equals(removeLB)){
                            lbsToRemove .add(lb);
                        }
                    }
                }

                for (LoadbalancerDTO lbToRemove : lbsToRemove) {
                    currentLBs.remove(lbToRemove);
                }
                message = "Successfully removed specified LBs";
            }
        }
    }
%>
<%=message%>
<script type="text/javascript">
    location.href = "cep_buckets.jsp?pageNumber=<%=pageNumberInt%>";
</script>