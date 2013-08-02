/*
 * Copyright 2003 - 2013 The eFaps Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Revision:        $Rev$
 * Last Changed:    $Date$
 * Last Changed By: $Author$
 */

package org.efaps.esjp.bpm.util;

import java.util.UUID;

import org.efaps.admin.common.SystemConfiguration;
import org.efaps.admin.program.esjp.EFapsRevision;
import org.efaps.admin.program.esjp.EFapsUUID;
import org.efaps.util.cache.CacheReloadException;

/**
 * TODO comment!
 *
 * @author The eFaps Team
 * @version $Id$
 */
@EFapsUUID("1eafb734-12f0-44c0-87f1-c59290fbe156")
@EFapsRevision("$Rev$")
public final class BPM
{
    /**
     * Key for Caching Delegates.
     */
    public static final String CACHEKEY4DELEGATE = BPM.class.getName() + ".CacheKey4Delegate";

    /**
     * Singelton.
     */
    private BPM()
    {
    }

    /**
     * @return the SystemConfigruation for Sales
     * @throws CacheReloadException on error
     */
    public static SystemConfiguration getSysConfig()
        throws CacheReloadException
    {
        // Bpm-Configuration
        return SystemConfiguration.get(UUID.fromString("53297af7-ef5f-459e-beb6-c1e69f6442c3"));
    }
}
