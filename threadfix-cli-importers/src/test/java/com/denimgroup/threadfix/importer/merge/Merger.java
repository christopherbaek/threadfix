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
package com.denimgroup.threadfix.importer.merge;

import com.denimgroup.threadfix.data.dao.ApplicationDao;
import com.denimgroup.threadfix.data.dao.ChannelTypeDao;
import com.denimgroup.threadfix.data.entities.*;
import com.denimgroup.threadfix.importer.cli.ScanParser;
import com.denimgroup.threadfix.service.merge.ScanMerger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import java.util.List;

import static com.denimgroup.threadfix.CollectionUtils.list;
import static com.denimgroup.threadfix.CollectionUtils.listOf;
import static com.denimgroup.threadfix.importer.config.SpringConfiguration.getSpringBean;

/**
 * Created by mac on 7/28/14.
 */
@Component
public class Merger extends SpringBeanAutowiringSupport {

    @Autowired
    private ScanParser     scanParser;
    @Autowired
    private ScanMerger     scanMerger;
    @Autowired
    private ApplicationDao applicationDao;
    @Autowired
    private ChannelTypeDao channelTypeDao;

    public static Application getApplicationFromScans(ScannerType scannerName, String... filePaths) {
        return getSpringBean(Merger.class).getApplicationInternal(new Application(), scannerName, filePaths);
    }

    /**
     * This is public because @Transactional doesn't work for private methods. Don't call it yourself.
     * @param scannerName scanner name of ALL the scan files.
     * @param filePaths one file path for each scan
     * @return an application with a list of scans from the given files
     */
    public Application getApplicationInternal(Application application, ScannerType scannerName, String[] filePaths) {
        assert scanMerger != null : "No Merger found, fix your Spring context.";
        assert scanParser != null : "No Parser found, fix your Spring context.";

        List<Scan> scans = list();

        ApplicationChannel channel = new ApplicationChannel();
        ChannelType channelType = channelTypeDao.retrieveByName(scannerName.getFullName());

        assert channelType != null : "Unable to find ChannelType for name " + scannerName.getFullName();

        channel.setChannelType(channelType);
        channel.setApplication(application);
        channel.setScanList(scans);
        application.setScans(scans);
        application.setVulnerabilities(listOf(Vulnerability.class));
        application.setChannelList(list(channel));
        application.setName("application merge.");

        applicationDao.saveOrUpdate(application);

        for (String file : filePaths) {
            Scan resultScan = scanParser.getScan(file);
            scanMerger.merge(resultScan, channel);
            application.getScans().add(resultScan);
        }

        return application;
    }

    @Transactional(readOnly = true)
    public List<Scan> getScanListInternal(Application application, ScannerType scannerName, String[] filePaths) {
        Application resultingApplication = getApplicationInternal(application, scannerName, filePaths);

        return resultingApplication.getScans();
    }

    public static List<Scan> getScanListFromPaths(Application application, ScannerType scannerName, String... filePaths) {
        return getSpringBean(Merger.class).getScanListInternal(application, scannerName, filePaths);
    }

    public static List<Scan> getScanListFromPaths(ScannerType scannerName, String... filePaths) {
        return getSpringBean(Merger.class).getScanListInternal(new Application(), scannerName, filePaths);
    }

}
