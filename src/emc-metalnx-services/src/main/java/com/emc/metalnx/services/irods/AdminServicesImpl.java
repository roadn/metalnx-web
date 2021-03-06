/*
 * Copyright (c) 2015-2017, Dell EMC
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.emc.metalnx.services.irods;

import com.emc.metalnx.core.domain.exceptions.DataGridConnectionRefusedException;
import com.emc.metalnx.services.interfaces.AdminServices;
import com.emc.metalnx.services.interfaces.ConfigService;
import org.irods.jargon.core.connection.AuthScheme;
import org.irods.jargon.core.connection.IRODSAccount;
import org.irods.jargon.core.connection.auth.AuthResponse;
import org.irods.jargon.core.exception.JargonException;
import org.irods.jargon.core.pub.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.net.ConnectException;

@Service
@Transactional
@Scope(value = WebApplicationContext.SCOPE_SESSION, proxyMode = ScopedProxyMode.INTERFACES)
public class AdminServicesImpl implements AdminServices {

    @Autowired
    IRODSAccessObjectFactory irodsAccessObjectFactory;

    @Autowired
    private ConfigService configService;

    private IRODSAccount irodsAccount;

    private static final Logger logger = LoggerFactory.getLogger(AdminServicesImpl.class);

    @Override
    public UserAO getUserAO() throws DataGridConnectionRefusedException {
        try {
            authenticateIRODSAccount();

            // Returning UserAO instance
            return irodsAccessObjectFactory.getUserAO(irodsAccount);

        }
        catch (JargonException e) {
            logger.error("Could not instantiate UserAO: ", e);

            if (e.getCause() instanceof ConnectException) {
                throw new DataGridConnectionRefusedException(e.getMessage());
            }
        }
        return null;
    }

    @Override
    public SpecificQueryAO getSpecificQueryAO() throws DataGridConnectionRefusedException {
        try {
            authenticateIRODSAccount();

            // Returning CollectionAndDataObjectListAndSearchAO instance
            return irodsAccessObjectFactory.getSpecificQueryAO(irodsAccount);

        }
        catch (JargonException e) {
            logger.error("Could not instantiate CollectionAndDataObjectListAndSearchAO: ", e);

            if (e.getCause() instanceof ConnectException) {
                throw new DataGridConnectionRefusedException(e.getMessage());
            }
        }
        return null;
    }

    @Override
    public DataObjectAO getDataObjectAO() throws DataGridConnectionRefusedException {
        try {
            authenticateIRODSAccount();

            // Returning CollectionAndDataObjectListAndSearchAO instance
            return irodsAccessObjectFactory.getDataObjectAO(irodsAccount);

        }
        catch (JargonException e) {
            logger.error("Could not instantiate CollectionAndDataObjectListAndSearchAO: ", e);

            if (e.getCause() instanceof ConnectException) {
                throw new DataGridConnectionRefusedException(e.getMessage());
            }
        }

        return null;
    }

    private void authenticateIRODSAccount() throws JargonException {

        String host = configService.getIrodsHost();
        int port = Integer.parseInt(configService.getIrodsPort());
        String zone = configService.getIrodsZone();
        String user = configService.getIrodsJobUser();
        String password = configService.getIrodsJobPassword();
        String authScheme = configService.getIrodsAuthScheme();
        String resc = "demoResc";
        String homeDir = "";

        if (irodsAccount == null) {
            IRODSAccount tempAccount = IRODSAccount.instance(host, port, user, password, homeDir, zone, resc);
            tempAccount.setAuthenticationScheme(AuthScheme.findTypeByString(authScheme));

            AuthResponse authResponse = irodsAccessObjectFactory.authenticateIRODSAccount(tempAccount);

            if (authResponse.isSuccessful()) {
                irodsAccount = authResponse.getAuthenticatedIRODSAccount();
            }
        }
    }

    @Override
    public CollectionAndDataObjectListAndSearchAO getCollectionAndDataObjectListAndSearchAO() throws DataGridConnectionRefusedException {
        try {
            authenticateIRODSAccount();

            // Returning CollectionAndDataObjectListAndSearchAO instance
            return irodsAccessObjectFactory.getCollectionAndDataObjectListAndSearchAO(irodsAccount);

        }
        catch (JargonException e) {
            logger.error("Could not instantiate CollectionAndDataObjectListAndSearchAO: ", e);

            if (e.getCause() instanceof ConnectException) {
                throw new DataGridConnectionRefusedException(e.getMessage());
            }
        }

        return null;
    }

}
