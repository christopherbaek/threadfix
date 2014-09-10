<%@ include file="/common/taglibs.jsp"%>
<%@ taglib prefix="cbs" uri="/WEB-INF/jscachebust.tld"%>

<head>
	<title>Applications</title>
	<cbs:cachebustscript src="/scripts/applications-index-controller.js"/>
	<cbs:cachebustscript src="/scripts/upload-scan-controller.js"/>
    <cbs:cachebustscript src="/scripts/modal-controller-with-config.js"/>
</head>

<body id="apps">
    <%@ include file="/WEB-INF/views/angular-init.jspf"%>

	<h2>Teams</h2>

    <div ng-controller="ApplicationsIndexController">

        <security:authorize ifNotGranted="ROLE_CAN_MANAGE_TEAMS">
            <div ng-show="teams && teams.length === 0" class="alert alert-error">
                You don't have permission to access any ThreadFix applications or to create one for yourself.
                Contact your administrator to get help.
            </div>
        </security:authorize>

        <div ng-hide="initialized" class="spinner-div"><span class="spinner dark"></span>Loading</div><br>

        <c:if test="${ not empty successMessage }">
            <div class="alert alert-success">
                <button class="close" data-dismiss="alert" type="button">x</button>
                <c:out value="${ successMessage }"/>
            </div>
        </c:if>
        <%@ include file="/WEB-INF/views/successMessage.jspf" %>

        <div ng-show="errorMessage" class="alert alert-success">
            <button class="close" data-dismiss="errorMessage = false" type="button">&times;</button>
            {{ errorMessage }}
        </div>

        <%@ include file="/WEB-INF/views/organizations/indexTable.jsp" %>
    </div>

</body>
