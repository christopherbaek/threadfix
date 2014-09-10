<%@ include file="/common/taglibs.jsp"%>
<%@ taglib prefix="cbs" uri="/WEB-INF/jscachebust.tld"%>

<head>
	<title>Manage Filters</title>
	<cbs:cachebustscript src="/scripts/vulnerability-filters-controller.js"/>
	<cbs:cachebustscript src="/scripts/modal-controller-with-config.js"/>
</head>

<body ng-controller="VulnerabilityFiltersController">

    <%@ include file="/WEB-INF/views/angular-init.jspf"%>

    <div ng-hide="initialized" class="spinner-div"><span class="spinner dark"></span>Loading</div><br>

    <div ng-show="initialized">
        <%@ include file="/WEB-INF/views/filters/form.jsp"%>

        <ul ng-show="originalType !== 'Global'" class="breadcrumb">
            <li><a href="<spring:url value="/"/>">Applications Index</a> <span class="divider">/</span></li>

            <li ng-show="originalType === 'Application'"><a class="pointer" ng-click="goToTeam(organization)">Team: {{ application.team.name }}</a> <span class="divider">/</span></li>
            <li ng-show="originalType === 'Application'"><a class="pointer" ng-click="goToApp(organization, application)">Application: {{ application.name }}</a><span class="divider">/</span></li>

            <li ng-show="originalType === 'Organization'"><a class="pointer" ng-click="goToTeam(organization)">Team: {{ organization.name }}</a> <span class="divider">/</span></li>

            <li class="active">Vulnerability Filters</li>
        </ul>

        <h2 ng-show="type === 'Application'">Application {{ application.name }} Filters</h2>
        <h2 ng-show="type === 'Organization'">Team {{ organization.name }} Filters</h2>
        <h2 ng-show="type === 'Global'">Global Filters</h2>

        <div id="helpText">
            ThreadFix Vulnerability Filters are used to sort data.<br/>
        </div>

        <tabset ng-hide="originalType === 'Global'">
            <tab ng-click="setTab('Application')" ng-show="originalType === 'Application'" heading="Application Filters" active="type==='Application'"></tab>
            <tab ng-click="setTab('Organization')" heading="Team Filters" active="type==='Organization'"></tab>
            <tab ng-click="setTab('Global')" heading="Global Filters" active="type==='Global'"></tab>
        </tabset>

        <div id="tabsDiv">
            <h3>{{ vulnFiltersTitle }}</h3>

            <div id="vulnFiltersSuccessMessage" ng-show="successMessage" class="alert alert-success">
                <button class="close" ng-click="successMessage = undefined" type="button">&times;</button>
                {{ successMessage }}
            </div>

            <a id="createNewKeyModalButton" ng-click="showNewFilterModal()" class="btn">Create New Filter</a>

            <div id="tableDiv">
                <%@ include file="/WEB-INF/views/filters/table.jsp" %>
            </div>

            <h3>{{ severityFiltersTitle }}</h3>

            <%@ include file="/WEB-INF/views/filters/severityFilterForm.jsp" %>
        </div>
    </div>
</body>
