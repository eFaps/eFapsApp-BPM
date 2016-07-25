/*
 * Copyright 2003 - 2014 The eFaps Team
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


package org.efaps.esjp.bpm.listener;

import java.util.Map;

import org.efaps.admin.program.esjp.EFapsApplication;
import org.efaps.admin.program.esjp.EFapsUUID;
import org.efaps.bpm.BPM;
import org.efaps.db.Insert;
import org.efaps.db.Instance;
import org.efaps.esjp.bpm.result.Review;
import org.efaps.esjp.ci.CIBPM;
import org.efaps.util.EFapsException;


/**
 * TODO comment!
 *
 * @author The eFaps Team
 * @version $Id$
 */
@EFapsUUID("1dd177d8-45ff-4a5f-b167-e85c699a2c18")
@EFapsApplication("eFapsApp-BPM")
public abstract class ReviewListener_Base
    extends AbstractAsyncListener
{
    @Override
    public void onAfterExecute(final Map<String, Object> _eFapsValues,
                               final Object _taskData)
        throws EFapsException
    {
        if (_eFapsValues.containsKey(BPM.OUTPARAMETER4TASKDECISION)) {
            final Object obj = _eFapsValues.get(BPM.OUTPARAMETER4TASKDECISION);
            if (obj instanceof Review && _taskData instanceof Map) {
                final String oid = (String) ((Map<?,?>) _taskData).get("OID");
                final Instance objInst = Instance.get(oid);
                if (objInst.isValid()) {
                    Insert insert;
                    if (((Review) obj).isApproved()) {
                        insert = new Insert(CIBPM.LogApprove);
                    } else {
                        insert = new Insert(CIBPM.LogDeny);
                    }
                    insert.add(CIBPM.LogAbstract.GeneralInstanceLink, objInst.getGeneralId());
                    insert.executeWithoutTrigger();
                }
            }
        }
    }
}
