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
package com.denimgroup.threadfix.importer.impl.upload;

import com.denimgroup.threadfix.annotations.ScanImporter;
import com.denimgroup.threadfix.data.ScanCheckResultBean;
import com.denimgroup.threadfix.data.ScanImportStatus;
import com.denimgroup.threadfix.data.entities.Finding;
import com.denimgroup.threadfix.data.entities.Scan;
import com.denimgroup.threadfix.data.entities.ScannerType;
import com.denimgroup.threadfix.importer.impl.AbstractChannelImporter;
import com.denimgroup.threadfix.importer.util.DateUtils;
import com.denimgroup.threadfix.importer.util.HandlerWithBuilder;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

/**
 * Imports the results of a WebInspect scan (xml output).
 * 
 * Parses the 
 * 
 * Export -> Details -> Full
 * 
 * format, and none of the others.
 * 
 * @author mcollins
 */
@ScanImporter(ScannerType.WEBINSPECT)
class WebInspectChannelImporter extends AbstractChannelImporter {
	
	private String bestPractices = "Best Practices";
		
	public WebInspectChannelImporter() {
		super(ScannerType.WEBINSPECT);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.denimgroup.threadfix.service.channel.ChannelImporter#parseInput()
	 */
	@Override
	public Scan parseInput() {
		return parseSAXInput(new WebInspectSAXParser());
	}
	
	public class WebInspectSAXParser extends HandlerWithBuilder {

        private String currentChannelVulnName;
        private String currentUrl;
        private String currentParam;
        private String currentChannelSeverityName;
        private String currentResponseText;
        private String currentAttackHTTPRequest;
        private StringBuffer currentRawFinding = new StringBuffer();

        private Map<FindingKey, String> findingMap = new HashMap<>();

        private boolean grabUrlText       = false;
        private boolean grabVulnNameText  = false;
        private boolean grabSeverityText  = false;
        private boolean grabParameterText = false;
        private boolean grabDate          = false;
        private boolean grabTypeId        = false;
        private boolean grabAttackHTTPRequest = false;

        private boolean ignoreFinding = false;

        private boolean issues = false, issue = false;

        private final String[] paramChars = {"[", "]", "%"};

        private String cleanParam(String param) {
            if (param == null || param.isEmpty())
                return null;

            String editedParam = param;

            for (String character : paramChars)
                if (editedParam.contains(character))
                    editedParam = editedParam.substring(0, editedParam.indexOf(character));

            return editedParam;
        }

        public void add(Finding finding) {
            if (finding != null) {
                finding.setNativeId(getNativeId(finding));
                finding.setIsStatic(false);
                saxFindingList.add(finding);
            }
        }

        ////////////////////////////////////////////////////////////////////
        // Event handlers.
        ////////////////////////////////////////////////////////////////////

        public void startElement(String uri, String name,
                                 String qName, Attributes atts) {
            if ("Issues".equals(qName))
                issues = true;
            if ("Issue".equals(qName))
                issue = true;

            if (issues && issue) {
                if ("Name".equals(qName)) {
                    if (currentChannelVulnName == null)
                        grabVulnNameText = true;
                } else if ("Severity".equals(qName)) {
                    grabSeverityText = true;
                } else if ("CheckTypeID".equals(qName)) {
                    grabTypeId = true;
                } else if ("AttackHTTPRequest".equals(qName)){
                    grabAttackHTTPRequest = true;
                }

            } else {
                if ("URL".equals(qName)) {
                    grabUrlText = true;
                } else if ("AttackParamDescriptor".equals(qName)) {
                    grabParameterText = true;
                }
            }

            if (date == null && "RawResponse".equals(qName))
                grabDate = true;

            if (issue){
                currentRawFinding.append(makeTag(name, qName , atts));
            }
        }

        public void endElement(String uri, String name, String qName) {

            if (issue){
                currentRawFinding.append("</").append(qName).append(">");
            }

            if (grabUrlText) {
                currentUrl = getBuilderText();
                grabUrlText = false;

            } else if (grabVulnNameText) {
                currentChannelVulnName = getBuilderText();
                grabVulnNameText = false;

            } else if (grabSeverityText) {
                currentChannelSeverityName = getBuilderText();
                grabSeverityText = false;

            } else if (grabParameterText) {
                currentParam = getBuilderText();

                // TODO decide whether or not to clean out the various [] and %5d characters
                // that are sometimes tacked on. Right now we do.
                currentParam = cleanParam(currentParam);
                grabParameterText = false;
	    	} else if (grabDate) {
	    		if (currentResponseText == null)
	    			currentResponseText = getBuilderText();
	    		else
	    			currentResponseText = currentResponseText.concat(getBuilderText());
	    	} else if (grabTypeId) {
	    		String temp = getBuilderText().trim();
	    		ignoreFinding = temp.equals(bestPractices);
	    		grabTypeId = false;
	    	} else if (grabAttackHTTPRequest) {
                currentAttackHTTPRequest = getBuilderText();
                grabAttackHTTPRequest = false;
            }
	    	
	    	if ("Issues".equals(qName))
	    		issues = false;
	    	
	    	if ("AttackParamDescriptor".equals(qName))
	    		grabParameterText = false;
	    	
	    	if ("Issue".equals(qName)) {
	    		if (currentUrl == null)
	    			return;
	    		
	    		if (!ignoreFinding) {

                    findingMap.put(FindingKey.PATH, currentUrl);
                    findingMap.put(FindingKey.PARAMETER, currentParam);
                    findingMap.put(FindingKey.VULN_CODE, currentChannelVulnName);
                    findingMap.put(FindingKey.SEVERITY_CODE, currentChannelSeverityName);
                    findingMap.put(FindingKey.REQUEST, currentAttackHTTPRequest);
                    findingMap.put(FindingKey.RAWFINDING, currentRawFinding.toString());

	    			Finding finding = constructFinding(findingMap);

		    		add(finding);
	    		}
		
	    		currentChannelSeverityName = null;
	    		currentChannelVulnName = null;
	    		currentParam = null;
	    		currentUrl = null;
	    		ignoreFinding = false;
                issue = false;
                currentRawFinding.setLength(0);
                currentAttackHTTPRequest = null;

	    	}
	    	
	    	if (grabDate && "RawResponse".equals(qName)) {
	    		grabDate = false;
	    		date = DateUtils.attemptToParseDateFromHTTPResponse(currentResponseText);
	    		currentResponseText = "";
	    	}
	    }

	    public void characters (char ch[], int start, int length)
	    {
	    	if (grabUrlText || grabVulnNameText || grabSeverityText || grabParameterText
	    			|| grabDate || grabTypeId || grabAttackHTTPRequest) {
	    		addTextToBuilder(ch, start, length);
	    	}

            if (issue)
                currentRawFinding.append(ch,start,length);
	    }
	}

	@Nonnull
    @Override
	public ScanCheckResultBean checkFile() {
		return testSAXInput(new WebInspectSAXValidator());
	}
	
	public class WebInspectSAXValidator extends HandlerWithBuilder {
		private boolean hasFindings = false, hasDate = false, correctFormat = false;
		private boolean grabDate = false;
		private String currentResponseText = null;
		
		private int tagNumber = 0;
		
		private boolean passedTags = true;
		
		private String[] firstTags = new String[] 
				{"Sessions", "Session", "URL", "Scheme", 
				"Host","Port","AttackParamDescriptor"};
				
	    private void setTestStatus() {
	    	if (!correctFormat)
	    		testStatus = ScanImportStatus.WRONG_FORMAT_ERROR;
	    	else if (hasDate)
	    		testStatus = checkTestDate();
	    	else if (!hasFindings)
	    		testStatus = ScanImportStatus.EMPTY_SCAN_ERROR;
	    	else if (testStatus == null)
	    		testStatus = ScanImportStatus.SUCCESSFUL_SCAN;
	    }

	    ////////////////////////////////////////////////////////////////////
	    // Event handlers.
	    ////////////////////////////////////////////////////////////////////
	    
	    public void endDocument() {
	    	setTestStatus();
	    }

	    public void startElement (String uri, String name, String qName, Attributes atts) {
	    	if (tagNumber < firstTags.length) {
	    		passedTags = passedTags && firstTags[tagNumber].equals(qName);
	    		tagNumber++;
	    	} else {
	    		correctFormat = passedTags;
	    	}
	    	
	    	if ("Issue".equals(qName))
	    		hasFindings = true;
	    	
	    	if (!hasDate && "RawResponse".equals(qName))
	    		grabDate = true;
	    }
	    
	    public void endElement (String uri, String name, String qName) throws SAXException
	    {
	    	if (grabDate) {
	    		currentResponseText = getBuilderText();
	    	}
	    	
	    	if (!hasDate && grabDate && "RawResponse".equals(qName)) {
	    		grabDate = false;
	    		testDate = DateUtils.attemptToParseDateFromHTTPResponse(currentResponseText);
	    		hasDate = testDate != null;
	    		currentResponseText = "";
	    		if (hasDate && hasFindings && correctFormat) {
	    			setTestStatus();
	    			throw new SAXException(FILE_CHECK_COMPLETED);
	    		}
	    	}
	    }
	    
	    public void characters (char ch[], int start, int length)
	    {
	    	if (grabDate) {
	    		addTextToBuilder(ch, start, length);
	    	}
	    }
	}
}
