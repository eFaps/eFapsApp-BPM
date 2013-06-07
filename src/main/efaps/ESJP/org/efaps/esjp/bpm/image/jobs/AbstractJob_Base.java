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

package org.efaps.esjp.bpm.image.jobs;

import java.util.ArrayList;
import java.util.List;

import org.efaps.admin.program.esjp.EFapsRevision;
import org.efaps.admin.program.esjp.EFapsUUID;

/**
 * TODO comment!
 *
 * @author The eFaps Team
 * @version $Id$
 */
@EFapsUUID("bdc0379f-b86f-44da-914a-3a164605fb35")
@EFapsRevision("$Rev$")
public abstract class AbstractJob_Base
    implements TransformationJob
{

    private final String key;

    private final List<String> taskNames = new ArrayList<String>();

    public AbstractJob_Base(final String _key,
                            final String... _taskNames)
    {
        this.key = _key;
        for (final String taskName : _taskNames) {
            final String taskNameTmp = taskName.replaceAll(" ", "");
            this.taskNames.add(taskNameTmp);
        }
    }

    /**
     * Getter method for the instance variable {@link #taskNames}.
     *
     * @return value of instance variable {@link #taskNames}
     */
    public List<String> getTaskNames()
    {
        return this.taskNames;
    }

    /**
     * Getter method for the instance variable {@link #key}.
     *
     * @return value of instance variable {@link #key}
     */
    public String getKey()
    {
        return this.key;
    }

    public boolean apply(final String _nodeId)
    {
        boolean ret = false;
        if (_nodeId.contains(this.key)) {
            for (final String taskName : getTaskNames()) {
                ret = _nodeId.contains(taskName);
                if (ret) {
                    break;
                }
            }
        }
        return ret;
    }
}
