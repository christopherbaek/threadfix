<%@ include file="/common/taglibs.jsp"%>
<%@ taglib prefix="cbs" uri="/WEB-INF/jscachebust.tld"%>

<head>
	<title>Defect Trackers</title>
    <cbs:cachebustscript src="/scripts/scheduled-defect-tracker-update-tab-controller.js"/>
    <cbs:cachebustscript src="/scripts/defect-trackers-tab-controller.js"/>
    <cbs:cachebustscript src="/scripts/modal-controller-with-config.js"/>
</head>

<body id="config">
    <%@ include file="/WEB-INF/views/angular-init.jspf"%>
    <tabset>
        <%@include file="/WEB-INF/views/config/defecttrackers/tabs/defectTrackersTab.jsp"%>
        <%@include file="/WEB-INF/views/config/defecttrackers/tabs/scheduledUpdateTab.jsp"%>
    </tabset>
    <%@ include file="/WEB-INF/views/applications/forms/addScheduledDefectTrackerUpdateForm.jsp"%>
</body>