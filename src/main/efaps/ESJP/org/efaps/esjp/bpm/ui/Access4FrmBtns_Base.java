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


package org.efaps.esjp.bpm.ui;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.efaps.admin.event.Parameter;
import org.efaps.admin.event.Parameter.ParameterValues;
import org.efaps.admin.event.Return;
import org.efaps.admin.event.Return.ReturnValues;
import org.efaps.admin.program.esjp.EFapsRevision;
import org.efaps.admin.program.esjp.EFapsUUID;
import org.efaps.admin.user.Role;
import org.efaps.bpm.identity.EntityMapper;
import org.efaps.db.Context;
import org.efaps.esjp.common.AbstractCommon;
import org.efaps.util.EFapsException;
import org.kie.api.task.model.Status;
import org.kie.api.task.model.TaskSummary;
import org.kie.internal.task.api.model.Operation;




/**
 * TODO comment!
 *
 * @author The eFaps Team
 * @version $Id$
 */
@EFapsUUID("0d2afb83-5e21-4d32-8b83-741bb08b4a59")
@EFapsRevision("$Rev$")
public abstract class Access4FrmBtns_Base
    extends AbstractCommon
{

    public Return execute(final Parameter _parameter)
        throws EFapsException
    {
        final Return ret = new Return();
        final Set<Operation> operations = new HashSet<Operation>();
        ret.put(ReturnValues.VALUES, operations);

        final boolean requireClaim = "true".equalsIgnoreCase(getProperty(_parameter, "RequireClaim"));
        final boolean requireAction = "true".equalsIgnoreCase(getProperty(_parameter, "RequireAction"));
        final boolean infoOnly = "true".equalsIgnoreCase(getProperty(_parameter, "InfoOnly"));
        final Map<Integer, String> role4Delegate = analyseProperty(_parameter, "Role4Delegate");

         final Collection<String> operationList = analyseProperty(_parameter, "Operation").values();
        if (!operationList.isEmpty()) {
            for (final String operation : operationList) {
                operations.add(Operation.valueOf(operation));
            }
        } else {
            final TaskSummary taskSummary = (TaskSummary) _parameter.get(ParameterValues.BPM_TASK);
            if (infoOnly) {
                operations.add(Operation.Complete);
            } else {
                // only if one of the properties is set. empty operations set means all allowed
                if (requireClaim || requireAction) {
                    // if status ready
                    if (Status.Ready.equals(taskSummary.getStatus())) {
                        if (requireClaim) {
                            operations.add(Operation.Claim);
                        }
                    } else if (Status.Reserved.equals(taskSummary.getStatus())) {
                        if (taskSummary.getActualOwner().getId()
                                        .equals(EntityMapper.getUserId(Context.getThreadContext().getPerson().getUUID()))) {
                            if (!requireAction) {
                                operations.add(Operation.Complete);
                                operations.add(Operation.Fail);
                            }
                            operations.add(Operation.Release);
                        } else {
                            operations.add(Operation.Start);
                        }
                    }
                }
            }
        }
        // always add delegate to let the default value work (show when delegates exist)
        if (role4Delegate.isEmpty() && !operations.isEmpty()) {
            operations.add(Operation.Delegate);
        } else if (!role4Delegate.isEmpty()) {
            for (final String roleStr : role4Delegate.values()) {
                Role role;
                if (isUUID(roleStr)) {
                    role = Role.get(UUID.fromString(roleStr));
                } else {
                    role = Role.get(UUID.fromString(roleStr));
                }
                if (role.isAssigned()) {
                    operations.add(Operation.Delegate);
                    break;
                }
            }
        }
        return ret;
    }
}
