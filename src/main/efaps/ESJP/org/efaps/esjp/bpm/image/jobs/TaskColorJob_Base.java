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

import org.efaps.admin.program.esjp.EFapsRevision;
import org.efaps.admin.program.esjp.EFapsUUID;
import org.efaps.esjp.bpm.image.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * TODO comment!
 *
 * @author The eFaps Team
 * @version $Id$
 */
@EFapsUUID("1e4e3792-afbe-494d-ab83-fa54feefdada")
@EFapsRevision("$Rev$")
public abstract class TaskColorJob_Base
    extends AbstractJob
{

    /**
     * Suffix used for tyhis job.
     */
    public final static String BACKGROUND_NODE_SUFIX = "eFapsBackground";

    /**
     * Logging instance used in this class.
     */
    private static final Logger LOG = LoggerFactory.getLogger(TaskColorJob.class);

    /**
     * Color to be set.
     */
    private final String color;

    /**
     * Creates a new transformation job to change the background color of a
     * task.
     *
     * @param _color The desired background color in hexadecimal. I.e: #AA00DD
     * @param _taskNames The name of the task.
     */
    public TaskColorJob_Base(final String _color,
                             final String... _taskNames)
    {
        super(TaskColorJob_Base.BACKGROUND_NODE_SUFIX, _taskNames);
        this.color = _color;
        TaskColorJob_Base.LOG.debug("Created new TaskColorJob for {}, {}", _color, _taskNames);
    }

    /**
     * {@inheritDoc}
     */
    public void transform(final Context _context,
                          final String _nodeID)
    {
        TaskColorJob_Base.LOG.trace("Trying to transform for Node '{}' for  {}", _nodeID, _context);
        final Document svgDocument = _context.getSvgDocument();
        if (apply(_nodeID)) {
            final Element element = svgDocument.getElementById(_nodeID);
            TaskColorJob_Base.LOG.trace("Found element '{}'", element);
            // check if style element
            final String style = element.getAttribute("style");
            if (style == null) {
                element.setAttribute("fill", this.color);
            } else {
                element.setAttribute("style", style.replaceAll("fill:[^;]*", "fill:" + this.color));
            }
        }
    }
}
