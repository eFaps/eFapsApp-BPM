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

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.drools.runtime.process.NodeInstance;
import org.efaps.admin.dbproperty.DBProperties;
import org.efaps.admin.event.Parameter;
import org.efaps.admin.event.Parameter.ParameterValues;
import org.efaps.admin.event.Return;
import org.efaps.admin.event.Return.ReturnValues;
import org.efaps.admin.program.esjp.EFapsRevision;
import org.efaps.admin.program.esjp.EFapsUUID;
import org.efaps.admin.ui.AbstractCommand;
import org.efaps.bpm.BPM;
import org.efaps.ci.CIAdminProgram;
import org.efaps.db.Checkout;
import org.efaps.db.Instance;
import org.efaps.db.InstanceQuery;
import org.efaps.db.MultiPrintQuery;
import org.efaps.db.QueryBuilder;
import org.efaps.db.SelectBuilder;
import org.efaps.esjp.bpm.image.Processor;
import org.efaps.esjp.bpm.image.jobs.TaskColorJob;
import org.efaps.esjp.ci.CIBPM;
import org.efaps.util.EFapsException;
import org.jbpm.task.query.TaskSummary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO comment!
 *
 * @author The eFaps Team
 * @version $Id$
 */
@EFapsUUID("19f2abbe-4632-4ad6-a3aa-c26c133e7f26")
@EFapsRevision("$Rev$")
public abstract class BProcess_Base
{
    /**
     * Logging instance used in this class.
     */
    private static final Logger LOG = LoggerFactory.getLogger(BProcess_Base.class);

    /**
     * @param _parameter Parameter as passed by the eFaps API
     * @param _taskName name of the task
     * @param _status list of status to be used as filter, <code>null</code>
     *            uses all
     * @return list of taskSummary
     * @throws EFapsException on error
     */
    public List<TaskSummary> getTaskSummary4Instance(final Parameter _parameter,
                                                     final String _taskName,
                                                     final List<org.jbpm.task.Status> _status)
        throws EFapsException
    {
        final List<TaskSummary> ret = new ArrayList<TaskSummary>();
        final Map<?, ?> properties = (Map<?, ?>) _parameter.get(ParameterValues.PROPERTIES);
        final String taskName = _taskName == null ? (String) properties.get("TaskName") : _taskName;
        final List<org.jbpm.task.Status> status;
        if (_status == null) {
            status = new ArrayList<org.jbpm.task.Status>();
            for (final org.jbpm.task.Status statusTmp : org.jbpm.task.Status.values()) {
                status.add(statusTmp);
            }
        } else {
            status = _status;
        }

        final QueryBuilder queryBldr = new QueryBuilder(CIBPM.GeneralInstance2ProcessId);
        queryBldr.addWhereAttrEqValue(CIBPM.GeneralInstance2ProcessId.GeneralInstanceLink, _parameter.getInstance()
                        .getGeneralId());
        final MultiPrintQuery multi = queryBldr.getPrint();
        multi.addAttribute(CIBPM.GeneralInstance2ProcessId.ProcessId);
        multi.executeWithoutAccessCheck();

        while (multi.next()) {
            final Long processInstanceId = multi.<Long>getAttribute(CIBPM.GeneralInstance2ProcessId.ProcessId);
            ret.addAll(BPM.getTasksByStatusByProcessIdByTaskName(processInstanceId,
                            status,
                            taskName));
        }
        return ret;
    }

    /**
     * @param _parameter Parameter as passed by the eFaps API
     * @return Return with svg snipplet
     * @throws EFapsException on error
     */
    public Return getProcessImageFieldValue(final Parameter _parameter)
        throws EFapsException
    {
        final Return ret = new Return();
        final StringBuilder html = new StringBuilder();
        final Instance instance = _parameter.getInstance();

        if (instance.getType().isKindOf(CIAdminProgram.BPM.getType())) {
            final QueryBuilder queryBldr = new QueryBuilder(CIAdminProgram.BPM2Image);
            queryBldr.addWhereAttrEqValue(CIAdminProgram.BPM2Image.FromLink, instance);
            final MultiPrintQuery multi = queryBldr.getPrint();
            final SelectBuilder sel = SelectBuilder.get().linkto(CIAdminProgram.BPM2Image.ToLink).instance();
            multi.addSelect(sel);
            multi.execute();
            while (multi.next()) {
                final Instance imageInst = multi.<Instance>getSelect(sel);
                final Checkout checkout = new Checkout(imageInst);
                final InputStream ins = checkout.execute();
                final StringWriter strb = new StringWriter();
                try {
                    IOUtils.copy(ins, strb);
                } catch (final IOException e) {
                    e.printStackTrace();
                }
                html.append(strb);
            }
        }

        if (html.length() < 1) {
            html.append(DBProperties.getProperty("org.efaps.esjp.bpm.BProcess.NoImage"));
        }
        ret.put(ReturnValues.SNIPLETT, html.toString());
        return ret;
    }

    /**
     * @param _parameter Parameter as passed by the eFaps API
     * @return Return with svg snipplet
     * @throws EFapsException on error
     */
    public Return getProcessImage4InstanceFieldValue(final Parameter _parameter)
        throws EFapsException
    {
        final Return ret = new Return();
        final StringBuilder html = new StringBuilder();
        final Instance imageInst = getProcessImageInstance(_parameter);
        if (imageInst.isValid()) {
            final QueryBuilder queryBldr = new QueryBuilder(CIBPM.GeneralInstance2ProcessId);
            queryBldr.addWhereAttrEqValue(CIBPM.GeneralInstance2ProcessId.GeneralInstanceLink, _parameter.getInstance()
                            .getGeneralId());
            final MultiPrintQuery multi = queryBldr.getPrint();
            multi.addAttribute(CIBPM.GeneralInstance2ProcessId.ProcessId);
            multi.executeWithoutAccessCheck();

            final List<NodeInstance> nodes = new ArrayList<NodeInstance>();
            while (multi.next()) {
                final Long processInstanceId = multi.<Long>getAttribute(CIBPM.GeneralInstance2ProcessId.ProcessId);
                nodes.addAll(BPM.getActiveNodes4ProcessId(processInstanceId));
            }

            final Checkout checkOut = new Checkout(imageInst);
            final InputStream svgIn = checkOut.execute();
            try {
                final Processor processor = new Processor(svgIn);
                for (final NodeInstance node : nodes) {
                    processor.addTransformationJob(new TaskColorJob("rgb(78,219,33)", node.getNodeName()));
                }
                processor.applyTransformationJobs();
                html.append(processor.toXML());
            } catch (final IOException e) {
                BProcess_Base.LOG.error("IOException", e);
            }
        }
        if (html.length() < 1) {
            html.append(DBProperties.getProperty("org.efaps.esjp.bpm.BProcess.NoImage"));
        }
        ret.put(ReturnValues.SNIPLETT, html.toString());
        return ret;
    }

    /**
     *  @param _parameter Parameter as passed by the eFaps API
     * @return istance for an BPMImage
     * @throws EFapsException on error
     */
    protected Instance getProcessImageInstance(final Parameter _parameter)
        throws EFapsException
    {
        Instance ret = Instance.get("");
        final AbstractCommand cmd = (AbstractCommand) _parameter.get(ParameterValues.CALL_CMD);
        if (cmd == null) {
            BProcess_Base.LOG.error("no command found");
        } else {
            final String uuid = cmd.getProperty("ProcessImageUUID");
            final QueryBuilder imgQueryBldr = new QueryBuilder(CIAdminProgram.BPMImage);
            imgQueryBldr.addWhereAttrEqValue(CIAdminProgram.BPMImage.UUID, uuid);
            final InstanceQuery query = imgQueryBldr.getQuery();
            query.execute();
            if (query.next()) {
                ret = query.getCurrentValue();
            }
        }
        return ret;
    }
}
