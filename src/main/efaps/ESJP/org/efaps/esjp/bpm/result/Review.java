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


package org.efaps.esjp.bpm.result;

import java.io.Serializable;
import java.util.UUID;

import org.efaps.admin.program.esjp.EFapsRevision;
import org.efaps.admin.program.esjp.EFapsUUID;
import org.efaps.admin.user.Person;
import org.efaps.db.Context;
import org.efaps.util.EFapsException;


/**
 * TODO comment!
 *
 * @author The eFaps Team
 * @version $Id$
 */
@EFapsUUID("09fcf691-3773-4738-929e-3684d198aed2")
@EFapsRevision("$Rev$")
public class Review
    implements Serializable
{
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * Status of the Review.
     */
    private boolean approved;

    /**
     * Name of the user that set the Status.
     * Userfriendly value that does not guarantee uniqueness.
     */
    private String userName;

    /**
     * UUID of the user that set the Status.
     * Value that does guarantee uniqueness.
     */
    private UUID userUUID;

    /**
     * On Instantiation set the User.
     * @throws EFapsException on error
     */
    public Review()
        throws EFapsException
    {
        setUser();
    }

    /**
     * Set the User information.
     * @throws EFapsException on error
     */
    private void setUser() throws EFapsException
    {
        final Person person = Context.getThreadContext().getPerson();
        if (person != null) {
            this.userName = person.getName();
            this.userUUID = person.getUUID();
        } else {
            this.userName = "Unknown";
        }
    }

    /**
     * Getter method for the instance variable {@link #approved}.
     *
     * @return value of instance variable {@link #approved}
     */
    public boolean isApproved()
    {
        return this.approved;
    }

    /**
     * Setter method for instance variable {@link #approved}.
     *
     * @param approved value for instance variable {@link #approved}
     * @throws EFapsException on error
     */
    public void setApproved(final boolean _approved)
        throws EFapsException
    {
        this.approved = _approved;
        setUser();
    }

    /**
     * Getter method for the instance variable {@link #userName}.
     *
     * @return value of instance variable {@link #userName}
     */
    public String getUserName()
    {
        return this.userName;
    }

    /**
     * Getter method for the instance variable {@link #userUUID}.
     *
     * @return value of instance variable {@link #userUUID}
     */
    public UUID getUserUUID()
    {
        return this.userUUID;
    }

    @Override
    public String toString()
    {
        return new StringBuilder()
            .append("Review: [")
            .append("approved: ").append(this.approved)
            .append(", Person: {").append(this.userName).append(", ").append(this.userUUID).append("}]")
            .toString();
    }
}
