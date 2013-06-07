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
@EFapsUUID("73f21b7e-a6e1-4fb4-9fb1-d31d7ca1f4a2")
@EFapsRevision("$Rev$")
public abstract class TaskBorderJob_Base
    extends AbstractJob
{

    /**
     * Logging instance used in this class.
     */
    private static final Logger LOG = LoggerFactory.getLogger(TaskBorderJob_Base.class);
    public final static String BORDER_NODE_SUFIX = "eFapsBorder";
    private Integer strokeWidth = null;
    private String strokeColor = null;

    /**
     * Creates a new transformation job for modify the stroke color and with of
     * the border of a set of tasks.
     *
     * @param taskNames The names of the tasks.
     * @param strokeColor the stroke color in hexadecimal. I.e: #AA00DD
     * @param strokeWidth the stroke width to use.
     */
    public TaskBorderJob_Base(final String strokeColor,
                              final Integer strokeWidth,
                              final String... taskNames)
    {
        super(TaskBorderJob_Base.BORDER_NODE_SUFIX, taskNames);
        this.strokeColor = strokeColor;
        this.strokeWidth = strokeWidth;
    }

    /**
     * Creates a new transformation job for modify the stroke color of the
     * border of a set of tasks.
     *
     * @param taskNames The names of the tasks.
     * @param strokeColor the stroke color in hexadecimal. I.e: #AA00DD
     */
    public TaskBorderJob_Base(final String strokeColor,
                              final String... taskNames)
    {
        this(strokeColor, null, taskNames);
    }


    public void transform(final Context context,
                          final String _nodeID)
    {
        final Document svgDocument = context.getSvgDocument();
        if (apply(_nodeID)) {
            final Element backgroundElement = svgDocument.getElementById(_nodeID);
            if (this.strokeWidth != null) {
                backgroundElement.setAttribute("stroke-width", String.valueOf(this.strokeWidth));
            }
            if (this.strokeColor != null) {
                backgroundElement.setAttribute("stroke", this.strokeColor);
            }
        }
    }

    public void setStrokeWidth(final Integer strokeWidth)
    {
        this.strokeWidth = strokeWidth;
    }

    public void setStrokeColor(final String strokeColor)
    {
        this.strokeColor = strokeColor;
    }
}
