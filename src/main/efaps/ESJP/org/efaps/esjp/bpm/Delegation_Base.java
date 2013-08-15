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

package org.efaps.esjp.bpm;

import java.util.UUID;

import org.efaps.admin.datamodel.ui.FieldValue;
import org.efaps.admin.event.Parameter;
import org.efaps.admin.event.Parameter.ParameterValues;
import org.efaps.admin.event.Return;
import org.efaps.admin.program.esjp.EFapsRevision;
import org.efaps.admin.program.esjp.EFapsUUID;
import org.efaps.admin.user.AbstractUserObject;
import org.efaps.db.Instance;
import org.efaps.db.Update;
import org.efaps.esjp.ci.CIBPM;
import org.efaps.util.EFapsException;
import org.joda.time.DateTime;

/**
 * TODO comment!
 *
 * @author The eFaps Team
 * @version $Id$
 */
@EFapsUUID("f7caf73b-75bb-4261-a565-00c56a00d270")
@EFapsRevision("$Rev$")
public abstract class Delegation_Base
{

    /**
     * Convert a UUID String to a UserObject name for easier understanding.
     * @param _parameter    Parameter as passed by the eFaps API
     * @return empty Return
     * @throws EFapsException on error
     */
    public Return translateUUID2User(final Parameter _parameter)
        throws EFapsException
    {
        final Return ret = new Return();
        final FieldValue fieldValue = (FieldValue) _parameter.get(ParameterValues.UIOBJECT);
        final String uuid = (String) fieldValue.getValue();
        if (uuid != null && !uuid.isEmpty() && uuid.split("-").length == 5) {
            final AbstractUserObject user = AbstractUserObject.getUserObject(UUID.fromString(uuid));
            if (user != null) {
                fieldValue.setValue(user.getName());
            }
        }
        return ret;
    }

    /**
     * Stop a delegation by setting the Until date to now.
     * @param _parameter    Parameter as passed by the eFaps API
     * @return empty Return
     * @throws EFapsException on error
     */
    public Return stopDelegation(final Parameter _parameter)
        throws EFapsException
    {
        final String[] oids = (String[]) _parameter.get(ParameterValues.OTHERS);
        if (oids != null && oids.length > 0) {
            final Instance instance = Instance.get(oids[0]);
            if (instance.isValid()) {
                final Update update = new Update(instance);
                update.add(CIBPM.DelegateAbstract.ValidUntil, new DateTime());
                update.execute();
            }
        }
        return new Return();
    }
}
