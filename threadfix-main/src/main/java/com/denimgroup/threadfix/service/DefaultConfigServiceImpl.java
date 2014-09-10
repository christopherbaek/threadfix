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

package com.denimgroup.threadfix.service;

import com.denimgroup.threadfix.data.dao.DefaultConfigurationDao;
import com.denimgroup.threadfix.data.entities.DefaultConfiguration;
import com.denimgroup.threadfix.logging.SanitizedLogger;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.errors.EncryptionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DefaultConfigServiceImpl implements DefaultConfigService {
	
	protected final SanitizedLogger log = new SanitizedLogger(DefaultConfigServiceImpl.class);
	
	@Autowired
	private DefaultConfigurationDao defaultConfigurationDao;

    @Transactional(readOnly = false)
	@Override
	public DefaultConfiguration loadCurrentConfiguration() {
        DefaultConfiguration configuration;

		List<DefaultConfiguration> list = defaultConfigurationDao.retrieveAll();
		if (list.size() == 0) {
            configuration = DefaultConfiguration.getInitialConfig();
		} else if (list.size() > 1) {
			DefaultConfiguration config = list.get(0);
			list.remove(0);
			for (DefaultConfiguration defaultConfig : list) {
				defaultConfigurationDao.delete(defaultConfig);
			}
			configuration = config;
		} else {
		    configuration = list.get(0);
        }

        assert configuration != null;
        return configuration;
	}

	@Override
	public void saveConfiguration(DefaultConfiguration config) {
		defaultConfigurationDao.saveOrUpdate(encrypt(config));
	}

    private DefaultConfiguration encrypt(DefaultConfiguration config) {
        assert config != null;

        try {
            if (config.getProxyPassword() != null && !config.getProxyPassword().trim().equals("")) {
               config.setProxyPasswordEncrypted(ESAPI.encryptor().encrypt(config.getProxyPassword()));
            }

            if (config.getProxyUsername() != null && !config.getProxyPassword().trim().equals("")) {
                config.setProxyUsernameEncrypted(ESAPI.encryptor().encrypt(config.getProxyPassword()));
            }
        } catch (EncryptionException e) {
            log.error("Encountered encryption exception, ESAPI configuration is probably incorrect. " +
                    "Check that ESAPI.properties is on the classpath.", e);
            assert false;
        }

        return config;
    }

    @Override
    public boolean isReportCacheDirty() {
        return !loadCurrentConfiguration().getHasCachedData();
    }

}
