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

package org.efaps.esjp.bpm.listener;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.efaps.admin.event.EventExecution;
import org.efaps.admin.event.Parameter;
import org.efaps.admin.event.Parameter.ParameterValues;
import org.efaps.admin.event.Return;
import org.efaps.admin.event.Return.ReturnValues;
import org.efaps.admin.program.esjp.EFapsRevision;
import org.efaps.admin.program.esjp.EFapsUUID;
import org.efaps.admin.user.AbstractUserObject;
import org.efaps.db.CachedMultiPrintQuery;
import org.efaps.db.QueryBuilder;
import org.efaps.esjp.bpm.util.BPM;
import org.efaps.esjp.ci.CIBPM;
import org.efaps.util.EFapsException;
import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;

/**
 * TODO comment!
 *
 * @author The eFaps Team
 * @version $Id$
 */
@EFapsUUID("b06381e7-a991-47eb-b107-9f66e58b07fb")
@EFapsRevision("$Rev$")
public abstract class PotentialOwnerListener_Base
    implements EventExecution
{

    @Override
    public Return execute(final Parameter _parameter)
        throws EFapsException
    {
        final Return ret = new Return();
        final List<?> owners = (List<?>) _parameter.get(ParameterValues.BPM_VALUES);
        if (!owners.isEmpty()) {
            final List<String> uuids = new ArrayList<String>();
            final List<AbstractUserObject> newOwners = new ArrayList<AbstractUserObject>();
            for (final Object owner : owners) {
                newOwners.add((AbstractUserObject) owner);
                uuids.add(((AbstractUserObject) owner).getUUID().toString());
            }
            final DateTime date = new DateMidnight().toDateTime();

            final QueryBuilder queryBldr = new QueryBuilder(CIBPM.DelegateAutomaticAbstract);
            queryBldr.addWhereAttrEqValue(CIBPM.DelegateAutomaticAbstract.FromUserUUID, uuids.toArray());
            queryBldr.addWhereAttrGreaterValue(CIBPM.DelegateAutomaticAbstract.ValidFrom, date.minusDays(1)
                            .minusSeconds(1));
            queryBldr.addWhereAttrLessValue(CIBPM.DelegateAutomaticAbstract.ValidUntil, date.plusDays(1).plusSeconds(1));
            final CachedMultiPrintQuery multi = queryBldr.getCachedPrint(BPM.CACHEKEY4DELEGATE).setLifespan(8)
                            .setLifespanUnit(TimeUnit.HOURS);
            multi.addAttribute(CIBPM.DelegateAutomaticAbstract.ToUserUUID,
                            CIBPM.DelegateAutomaticAbstract.ValidFrom, CIBPM.DelegateAutomaticAbstract.ValidUntil);
            multi.executeWithoutAccessCheck();
            while (multi.next()) {
                final DateTime validFrom = multi.<DateTime>getAttribute(CIBPM.DelegateAutomaticAbstract.ValidFrom);
                final DateTime validUntil = multi.<DateTime>getAttribute(CIBPM.DelegateAutomaticAbstract.ValidUntil);
                if (validFrom.toLocalDateTime().isBefore(new LocalDateTime()) &&
                                validUntil.toLocalDateTime().isAfter(new LocalDateTime())) {
                    final String tuUserUUID = multi.<String>getAttribute(CIBPM.DelegateAutomaticAbstract.ToUserUUID);
                    newOwners.add(AbstractUserObject.getUserObject(UUID.fromString(tuUserUUID)));
                }
            }
            ret.put(ReturnValues.VALUES, newOwners);
        }
        return ret;
    }
}
