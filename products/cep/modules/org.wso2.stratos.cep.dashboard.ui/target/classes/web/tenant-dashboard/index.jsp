<!--
 ~ Copyright (c) 2005-2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 ~
 ~ WSO2 Inc. licenses this file to you under the Apache License,
 ~ Version 2.0 (the "License"); you may not use this file except
 ~ in compliance with the License.
 ~ You may obtain a copy of the License at
 ~
 ~    http://www.apache.org/licenses/LICENSE-2.0
 ~
 ~ Unless required by applicable law or agreed to in writing,
 ~ software distributed under the License is distributed on an
 ~ "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 ~ KIND, either express or implied.  See the License for the
 ~ specific language governing permissions and limitations
 ~ under the License.
 -->
<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>

<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page import="org.wso2.carbon.ui.CarbonUIUtil" %>
<%@ page import="org.wso2.carbon.utils.ServerConstants" %>
<jsp:include page="../dialog/display_messages.jsp"/>
<link href="../tenant-dashboard/css/dashboard-common.css" rel="stylesheet" type="text/css" media="all"/>
<%
        Object param = session.getAttribute("authenticated");
        String passwordExpires = (String) session.getAttribute(ServerConstants.PASSWORD_EXPIRATION);
    boolean hasCAppMgtPermission = CarbonUIUtil.isUserAuthorized(request, "/permission/admin/manage");
    boolean hasTopicMgtPermission = CarbonUIUtil.isUserAuthorized(request, "/permission/admin/manage/topic");
    boolean hasServiceMgtPermission = CarbonUIUtil.isUserAuthorized(request, "/permission/admin/manage/add/service");
    boolean hasCEPMgtPermission = CarbonUIUtil.isUserAuthorized(request, "/permission/admin/manage/cep");
    boolean hasBrokerMgtPermission = CarbonUIUtil.isUserAuthorized(request, "/permission/admin/manage/brokerManager");
    boolean hasStatsPermission = CarbonUIUtil.isUserAuthorized(request, "/permission/protected/manage");

    boolean loggedIn = false;
        if (param != null) {
            loggedIn = (Boolean) param;
        }

%>

<div id="passwordExpire">
         <%
         if (loggedIn && passwordExpires != null) {
         %>
              <div class="info-box"><p>Your password expires at <%=passwordExpires%>. Please change by visiting <a href="../user/change-passwd.jsp?isUserChange=true&returnPath=../admin/index.jsp">here</a></p></div>
         <%
             }
         %>
</div>
<div id="middle">
<div id="workArea">

    <style type="text/css">
        .tip-table td.cep1 {
            background-image: url(../../carbon/tenant-dashboard/images/cep1.png);
        }
        .tip-table td.cep2 {
            background-image: url(../../carbon/tenant-dashboard/images/cep2.png);
        }
        .tip-table td.cep3 {
            background-image: url(../../carbon/tenant-dashboard/images/cep3.png);
        }
        .tip-table td.cep4 {
            background-image: url(../../carbon/tenant-dashboard/images/cep4.png);
        }



        .tip-table td.cep5 {
            background-image: url(../../carbon/tenant-dashboard/images/cep5.png);
        }
        .tip-table td.cep6 {
            background-image: url(../../carbon/tenant-dashboard/images/cep6.png);
        }
        .tip-table td.cep7 {
            background-image: url(../../carbon/tenant-dashboard/images/cep7.png);
        }
        .tip-table td.cep8 {
            background-image: url(../../carbon/tenant-dashboard/images/cep8.png);
        }
    </style>
    <%--
    Capp, Bucket, Broker, Topic
   Services.  Monitor CEP, MonotorBucket, Mon-Broker,


    --%>
    <h2 class="dashboard-title">WSO2 CEP quick start dashboard</h2>
    <table class="tip-table">
        <tr>
            <td class="tip-top cep1"></td>
            <td class="tip-empty"></td>
            <td class="tip-top cep2"></td>
            <td class="tip-empty "></td>
            <td class="tip-top cep3"></td>
            <td class="tip-empty "></td>
            <td class="tip-top cep4"></td>
        </tr>
        <tr>
            <td class="tip-content">
                <div class="tip-content-lifter">
                    <%
                        if (hasCAppMgtPermission) {
                    %>
                    <a class="tip-title" href="../carbonapps/app_upload.jsp?region=region1&item=apps_add_menu">Carbon Application</a> <br/>
                    <%
                    } else {
                    %>
                    <h3 class="tip-title">Carbon Application</h3> <br/>
                    <%
                        }
                    %>

                    <p>Carbon Application aRchives (CAR) contains Buckets and other deployable artifacts to deploy and manage them through the production lifecycle</p>

                </div>
            </td>
            <td class="tip-empty"></td>
            <td class="tip-content">
                <div class="tip-content-lifter">

                    <%
                        if (hasCEPMgtPermission) {
                    %>
                    <a class="tip-title" href="../CEP/cep_buckets.jsp?region=region1&item=cep_buckets_add_menu">Bucket</a> <br/>
                    <%
                    } else {
                    %>
                    <h3 class="tip-title">Bucket</h3> <br/>
                    <%
                        }
                    %>

                    <p>Bucket contains different queries, input & output event streams and event mappings that maps external events to event streams.</p>

                </div>
            </td>
            <td class="tip-empty"></td>
            <td class="tip-content">
                <div class="tip-content-lifter">
                    <%
                        if (hasBrokerMgtPermission) {
                    %>
                    <a class="tip-title" href="../brokermanager/create_broker.jsp?region=region1&item=brokermanager_add_menu">Broker</a> <br/>
                    <%
                    } else {
                    %>
                    <h3 class="tip-title">Broker</h3> <br/>
                    <%
                        }
                    %>


                    <p>Broker can be of different types, such as Local, WS-Event, JMS and Agent. They should be configured to receive and publish events.</p>

                </div>
            </td>
            <td class="tip-empty"></td>
            <td class="tip-content">
                <div class="tip-content-lifter">
                    <%
                        if (hasTopicMgtPermission) {
                    %>
                    <a class="tip-title" href="../topics/topic_add.jsp?region=region1&item=topics_add_menu">Topic</a> <br/>
                    <%
                    } else {
                    %>
                    <h3 class="tip-title">Topic</h3> <br/>
                    <%
                        }
                    %>

                     <p>Topic allows to distribute messages to multiple subscribers and receive from multiple publishers. Topics are mapped to input & output streams of backend runtimes.</p>

                </div>
            </td>
        </tr>
        <tr>
            <td class="tip-bottom"></td>
            <td class="tip-empty"></td>
            <td class="tip-bottom"></td>
            <td class="tip-empty"></td>
            <td class="tip-bottom"></td>
            <td class="tip-empty"></td>
            <td class="tip-bottom"></td>
        </tr>
    </table>
    <div class="tip-table-div"></div>
    <table class="tip-table">
        <tr>
            <td class="tip-top cep5"></td>
            <td class="tip-empty"></td>
            <td class="tip-top cep6"></td>
            <td class="tip-empty"></td>
            <td class="tip-top cep7"></td>
            <td class="tip-empty "></td>
            <td class="tip-top cep8"></td>
        </tr>
        <tr>
            <td class="tip-content">
                <div class="tip-content-lifter">
                    <%
                    if (hasServiceMgtPermission) {
                    %>
                    <a class="tip-title" href="../service-mgt/index.jsp?region=region1&item=services_list_menu">Local Broker Service</a> <br/>
                    <%
                    } else {
                    %>
                    <h3 class="tip-title">Local Broker Service</h3> <br/>
                    <%
                        }
                    %>


                    <p>Local broker service end points are formed according to the input topics of local broker and they are used to receive incoming events.</p>

                </div>
            </td>
            <td class="tip-empty"></td>
            <td class="tip-content">
                <div class="tip-content-lifter">
                    <%
                        if (hasStatsPermission) {
                    %>
                    <a class="tip-title" href="../cep-statistics/index.jsp?region=region4&item=cep_server_statistics_menu">Monitoring CEP</a> <br/>
                    <%
                    } else {
                    %>
                    <h3 class="tip-title">Monitoring CEP</h3> <br/>
                    <%
                        }
                    %>
                    <p>Shows real-time accumulative statistics of all CEP operations.</p>
                </div>
            </td>
            <td class="tip-empty"></td>
            <td class="tip-content">
                <div class="tip-content-lifter">
                    <%
                        if (hasStatsPermission) {
                    %>
                    <a class="tip-title" href="../cep-statistics/bucket_index.jsp?region=region4&item=cep_bucket_statistics_menu">Monitoring Buckets</a> <br/>
                    <%
                    } else {
                    %>
                    <h3 class="tip-title">Monitoring Buckets</h3> <br/>
                    <%
                        }
                    %>
                    <p>Shows real-time statistics of a particular bucket and all of its topics.</p>

                </div>
            </td>
            <td class="tip-empty"></td>
            <td class="tip-content">
                <div class="tip-content-lifter">
                    <%
                        if (hasStatsPermission) {
                    %>
                    <a class="tip-title" href="../cep-statistics/broker_index.jsp?region=region4&item=cep_broker_statistics_menu">Monitoring Brokers</a> <br/>
                    <%
                    } else {
                    %>
                    <h3 class="tip-title">Monitoring Brokers</h3> <br/>
                    <%
                        }
                    %>
                    <p>Shows real-time statistics of a particular broker and all of its topics.</p>

                </div>
            </td>
        </tr>
        <tr>
            <td class="tip-bottom"></td>
            <td class="tip-empty"></td>
            <td class="tip-bottom"></td>
            <td class="tip-empty"></td>
            <td class="tip-bottom"></td>
            <td class="tip-empty"></td>
            <td class="tip-bottom"></td>
        </tr>
    </table>
<p>
    <br/>
</p> </div>
</div>
