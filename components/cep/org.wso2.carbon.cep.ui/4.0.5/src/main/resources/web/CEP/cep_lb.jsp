<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ page import="org.apache.axis2.client.Options" %>
<%@ page import="org.apache.axis2.client.ServiceClient" %>
<%@ page import="org.apache.axis2.context.ConfigurationContext" %>
<%@ page import="org.wso2.carbon.CarbonConstants" %>
<%@ page import="org.wso2.carbon.cep.stub.admin.CEPAdminServiceStub" %>
<%@ page import="org.wso2.carbon.cep.stub.admin.internal.xsd.*" %>
<%@ page import="org.wso2.carbon.ui.CarbonUIUtil" %>
<%@ page import="java.util.LinkedList" %>
<fmt:bundle basename="org.wso2.carbon.cep.ui.i18n.Resources">

<link type="text/css" href="../CEP/css/buckets.css" rel="stylesheet"/>
<script type="text/javascript" src="../yui/build/yahoo-dom-event/yahoo-dom-event.js"></script>
<script type="text/javascript" src="../yui/build/connection/connection-min.js"></script>
<script type="text/javascript" src="../CEP/js/cep_buckets.js"></script>


<%--Includes for registry browser--%>
<script type="text/javascript" src="../resources/js/resource_util.js"></script>
<jsp:include page="../resources/resources-i18n-ajaxprocessor.jsp"/>
<script type="text/javascript" src="../ajax/js/prototype.js"></script>
<link rel="stylesheet" type="text/css" href="../resources/css/registry.css"/>
<%

    ConfigurationContext configContext = (ConfigurationContext) config.getServletContext()
            .getAttribute(CarbonConstants.CONFIGURATION_CONTEXT);
    //Server URL which is defined in the server.xml
    String serverURL = CarbonUIUtil.getServerURL(config.getServletContext(),
                                                 session) + "CEPAdminService.CEPAdminServiceHttpsSoap12Endpoint";
    CEPAdminServiceStub stub = new CEPAdminServiceStub(configContext, serverURL);

    String cookie = (String) session.getAttribute(org.wso2.carbon.utils.ServerConstants.ADMIN_SERVICE_COOKIE);

    ServiceClient client = stub._getServiceClient();
    Options option = client.getOptions();
    option.setManageSession(true);
    option.setProperty(org.apache.axis2.transport.http.HTTPConstants.COOKIE_STRING, cookie);


    String LBIndex = request.getParameter("index");
    int index = Integer.parseInt(LBIndex);
    LoadbalancerDTO lb = null;
    LBOutputNodeDTO[] lbout= null;;


    LinkedList<LoadbalancerDTO> loadbalancers = (LinkedList<LoadbalancerDTO>) session.getAttribute("loadbalancers");
    if(loadbalancers != null){
    lb = loadbalancers.get(index);
    lbout = lb.getLbOutputNodeDTOs();
    }

    boolean isViewingBucket = false;
    String viewingBucket = request.getParameter("view");
    if (viewingBucket != null) {
        isViewingBucket = true;
    }
%>

<div id="middle">
<h2><fmt:message key="lb"/></h2>

<div id="workArea">
<table class="styledLeft noBorders spacer-bot" style="width:100% margin-bottom:20px;">
<tbody>

<tr>
    <td class="leftCol-small"><fmt:message key="lb.ip"/><span class="required">*</span></td>
    <td><input type="text" id="lbip" value="<%= lb.getIp()%>"></td>
</tr>

<tr name="outputaddrow">
    <td colspan="2" class="middle-header">
        <fmt:message key="lb.ouput"/>
    </td>
</tr>
<tr name="outputaddrow">
    <td colspan="2">
         <div id="nooutputdefine" class="noDataDiv-plain"  style="width:100%;display:<%=lbout != null?"none":"" %>;">
                               No Output Nodes Defined
         </div>
        <table class="styledLeft" id="lboutputtable" style="width:100%;display:<%=lbout != null?"":"none" %>">
            <thead>
            <th class="leftCol-med"><fmt:message key="ip"/></th>
            <th class="leftCol-med"><fmt:message key="port"/></th>
            <th><fmt:message key="actions"/></th>
            </thead>

            <%
                if (lbout!= null) {
            %>

            <%
                for (LBOutputNodeDTO property : lbout) {
            %>
            <tr>
                <td class="leftCol-med"><%=property.getIp()%></td>
                <td class="leftCol-med"><%=property.getPort()%> </td>
                <td><a class="icon-link"
                       style="background-image:url(../admin/images/delete.gif)"
                       onclick="removeOutputNode(this,'lb')">Delete</a>
                    <script type="text/javascript">
                        addOputputNodeToSession('<%=property.getIp()%>','<%=property.getPort()%>');
                    </script>
                </td>
            </tr>

            <%
                }
            %>

            <%
                }
            %>
        </table>
        </td>
        </tr>
</tbody>
</table>

<table style="width:100%">
    <tbody>
    <tr>
        <td class="buttonRow">
            <%
                if (isViewingBucket) {
            %>
            <input type="button" onclick="goBack()" value=" &lt;Back" class="button">

            <%
            } else {
            %>
            <input type="button" onclick="goBack()" value=" &lt;Back" class="button">
            <%
                }
            %>
            <input type="button" onclick="addOldLBsToList(<%=index%>)"
                   value="<fmt:message key="save"/>" class="button">
        </td>
    </tr>
    </tbody>
</table>
</div>
</div>
</fmt:bundle>
