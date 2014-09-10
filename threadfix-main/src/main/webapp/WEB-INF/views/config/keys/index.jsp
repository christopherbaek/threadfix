<%@ include file="/common/taglibs.jsp"%>
<%@ taglib prefix="cbs" uri="/WEB-INF/jscachebust.tld"%>

<head>
	<title>API Keys</title>
    <cbs:cachebustscript src="/scripts/api-keys-controller.js"/>
</head>

<body>
    <%@ include file="/WEB-INF/views/angular-init.jspf"%>
    <div ng-controller="ApiKeysController">
        <h2>API Keys</h2>

        <%@ include file="/WEB-INF/views/successMessage.jspf" %>
        <%@ include file="/WEB-INF/views/errorMessage.jsp" %>
        <%@ include file="newForm.jsp" %>
        <%@ include file="editForm.jsp" %>

        <div id="helpText">
            ThreadFix API Keys are used to access the REST interface.<br/>
        </div>

        <button class="btn" ng-click="openNewModal()" id="createNewKeyModalButton">Create New Key</button>

        <div ng-show="loading" style="float:right" class="modal-loading"><div><span class="spinner dark"></span>Loading...</div></div>

        <table id="table" ng-hide="loading" class="table table-striped" style="table-layout:fixed;">
            <thead>
                <tr>
                    <th class="first">Key</th>
                    <th class="medium">Note</th>
                    <th class="centered">Edit / Delete</th>
                    <th class="short last">Restricted</th>
                </tr>
            </thead>
            <tbody>
                <tr ng-hide="keys || loading">
                    <td colspan="4" style="text-align:center;">No API Keys found.</td>
                </tr>
                <tr ng-repeat="key in keys">
                    <td id="key{{ key.note }}" style="max-width:550px;word-wrap: break-word;">{{ key.apiKey }}</td>
                    <td id="note{{ key.note }}" style="max-width:300px;word-wrap: break-word;">{{ key.note }}</td>
                    <td class="centered">
                        <button class="btn" id="editKeyModal{{ key.note }}" ng-click="openEditModal(key)">Edit / Delete</button>
                    </td>
                    <td id="restricted{{ key.note }}">{{ key.isRestrictedKey }}</td>
                </tr>
            </tbody>
        </table>

    </div>
</body>
