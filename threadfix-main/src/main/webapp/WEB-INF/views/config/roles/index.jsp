<%@ include file="/common/taglibs.jsp"%>
<%@ taglib prefix="cbs" uri="/WEB-INF/jscachebust.tld"%>

<head>
	<title>Manage Roles</title>
    <cbs:cachebustscript src="/scripts/roles-page-controller.js"/>
    <cbs:cachebustscript src="/scripts/modal-controller-with-config.js"/>
    <cbs:cachebustscript src="/scripts/role-edit-modal-controller.js"/>
</head>

<body ng-controller="RolesPageController">
	<h2>Manage Roles</h2>

    <%@ include file="/WEB-INF/views/config/roles/form.jsp" %>
    <%@ include file="/WEB-INF/views/config/roles/newForm.jsp" %>
    <%@ include file="/WEB-INF/views/angular-init.jspf"%>

    <div id="helpText">
		ThreadFix Roles determine functional capabilities for associated users.<br/>
	</div>

    <%@ include file="/WEB-INF/views/successMessage.jspf" %>
    <%@ include file="/WEB-INF/views/errorMessage.jsp" %>
	
	<a id="createRoleModalLink" class="btn" ng-click="openNewRoleModal()">Create New Role</a>
	
	<div id="tableDiv">
		<%@ include file="/WEB-INF/views/config/roles/rolesTable.jsp" %>
	</div>
		
</body>
