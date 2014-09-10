////////////////////////////////////////////////////////////////////////
//
//     Copyright (c) 2009-2014 Denim Group, Ltd.
//
//     The contents of this file are subject to the Mozilla Public License
//     Version 2.0 (the "License"); you may not use this file except in
//     compliance with the License. You may obtain a copy of the License at
//     http://www.mozilla.org/MPL/
//
//     Software distributed under the License is distributed on an "AS IS"
//     basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
//     License for the specific language governing rights and limitations
//     under the License.
//
//     The Original Code is ThreadFix.
//
//     The Initial Developer of the Original Code is Denim Group, Ltd.
//     Portions created by Denim Group, Ltd. are Copyright (C)
//     Denim Group, Ltd. All Rights Reserved.
//
//     Contributor(s): Denim Group, Ltd.
//
////////////////////////////////////////////////////////////////////////

package com.denimgroup.threadfix.service.util;

import com.denimgroup.threadfix.remote.response.RestResponse;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;
import org.codehaus.jackson.map.SerializationConfig;

import javax.annotation.Nonnull;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public final class ControllerUtils {
	
	private static final String SUCCESS_MESSAGE = "successMessage";
	private static final String ERROR_MESSAGE = "errorMessage";
    private static final String ACTIVE_TAB = "application_page_active_tab";
    public static final String ACTIVE_VULN_TAB = "active_vuln_tab";
    public static final String SCAN_TAB = "scan_tab";
    public static final String FILE_TAB = "file_tab";
    public static final String SCAN_AGENT_TASK_TAB = "scan_agent_task_tab";
    public static final String SCHEDULED_SCAN_TAB = "scheduled_scan_tab";
    public static final String CLOSED_VULN_TAB = "closed_vuln_tab";
    public static final String FALSE_POSITIVE_TAB = "false_positive_tab";

	private ControllerUtils() {
		// Nobody can instantiate this class
	}
	
	public static Object getItem(@Nonnull HttpServletRequest request, @Nonnull String key) {
		return getAttribute(request, key);
	}

	public static Object getErrorMessage(@Nonnull HttpServletRequest request) {
		return getAttribute(request, ERROR_MESSAGE);
	}
	
	public static Object getSuccessMessage(@Nonnull HttpServletRequest request) {
		return getAttribute(request, SUCCESS_MESSAGE);
	}
	
	public static void addItem(@Nonnull HttpServletRequest request, @Nonnull String key, @Nonnull Object item) {
		addMessage(request, key, item);
	}
	
	public static void addSuccessMessage(@Nonnull HttpServletRequest request, @Nonnull String successMessage) {
		addMessage(request, SUCCESS_MESSAGE, successMessage);
	}
	
	public static void addErrorMessage(@Nonnull HttpServletRequest request, @Nonnull String successMessage) {
		addMessage(request, ERROR_MESSAGE, successMessage);
	}
	
	private static Object getAttribute(@Nonnull HttpServletRequest request, @Nonnull String attribute) {
		Object returnValue = null;
		if (request.getSession() != null) {
			returnValue = request.getSession().getAttribute(attribute);
			if (returnValue != null) {
				request.getSession().removeAttribute(attribute);
			}
		}
		
		return returnValue;
	}
	
	private static void addMessage(@Nonnull HttpServletRequest request, @Nonnull String key, @Nonnull  Object message) {
		if (request.getSession() != null) {
			request.getSession().setAttribute(key, message);
		}
	}

    public static String getActiveTab(@Nonnull HttpServletRequest request) {
        Object activeTab = getAttribute(request, ACTIVE_TAB);
        if (activeTab != null)
            return activeTab.toString();
        else return null;
    }

    public static <T> ObjectWriter getObjectWriter(@Nonnull Class<T> targetClass) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationConfig.Feature.DEFAULT_VIEW_INCLUSION, false);

        return mapper.writerWithView(targetClass);
    }

    public static String writeSuccessObjectWithView(@Nonnull Object object, @Nonnull Class view) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationConfig.Feature.DEFAULT_VIEW_INCLUSION, false);

        ObjectWriter writer = mapper.writerWithView(view);

        try {
            return writer.writeValueAsString(RestResponse.success(object));
        } catch (IOException e) {
            throw new RuntimeException("Unable to write JSON Object.", e);
        }
    }
}
