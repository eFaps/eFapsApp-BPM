package org.efaps.esjp.bpm.listener;

/*
 * Copyright 2003 - 2018 The eFaps Team
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
 */

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.efaps.admin.EFapsSystemConfiguration;
import org.efaps.admin.KernelSettings;
import org.efaps.admin.event.Parameter;
import org.efaps.admin.program.esjp.EFapsApplication;
import org.efaps.admin.program.esjp.EFapsUUID;
import org.efaps.bpm.BPM;
import org.efaps.db.Context;
import org.efaps.db.Insert;
import org.efaps.db.Instance;
import org.efaps.esjp.common.AbstractCommon;
import org.efaps.esjp.common.uiform.IOnCreateListener;
import org.efaps.util.EFapsException;
import org.kie.api.runtime.process.ProcessInstance;

@EFapsUUID("7bea4614-78be-447f-8d14-9b4850ec4a72")
@EFapsApplication("eFapsApp-BPM")
public class OnCreateListener
    extends AbstractCommon
    implements IOnCreateListener
{

    @Override
    public void executeProcess(final Parameter _parameter, final Instance _instance)
        throws EFapsException
    {
        if (EFapsSystemConfiguration.get().getAttributeValueAsBoolean(KernelSettings.ACTIVATE_BPM)) {
            if (getProperty(_parameter, "ProcessID") != null) {
                if ("true".equalsIgnoreCase(getProperty(_parameter, "SaveContextBeforeProcessStart"))) {
                    Context.save();
                }
                final Map<String, Object> params = new HashMap<>();
                params.put("OID", _instance.getOid());
                add2ProcessMap(_parameter, _instance, params);
                final ProcessInstance processInstance = BPM.startProcess(getProperty(_parameter, "ProcessID"), params);
                if ("true".equalsIgnoreCase(getProperty(_parameter, "RegisterProcess"))) {
                    // BPM_GeneralInstance2ProcessId -- use of UUID because
                    // installed from different module
                    final Insert insert = new Insert(UUID.fromString("f6731331-e3a7-4a98-be35-ad1bb8e88497"));
                    insert.add("ProcessId", processInstance.getId());
                    insert.add("GeneralInstanceLink", _instance.getGeneralId());
                    insert.executeWithoutTrigger();
                }
            }
        }
    }

    /**
     * Add additional values to the map passed to the process prior to
     * execution.
     *
     * @param _parameter Parameter as passed by the eFasp API
     * @param _instance Insert the values can be added to
     * @param _params Map passed to the Process
     * @throws EFapsException on error
     */
    protected void add2ProcessMap(final Parameter _parameter, final Instance _instance,
                                  final Map<String, Object> _params)
        throws EFapsException
    {

    }

    @Override
    public int getWeight()
    {
        return 0;
    }
}
