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

package org.efaps.esjp.bpm.image;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.batik.dom.svg.SAXSVGDocumentFactory;
import org.apache.batik.util.XMLResourceDescriptor;
import org.efaps.admin.program.esjp.EFapsRevision;
import org.efaps.admin.program.esjp.EFapsUUID;
import org.efaps.esjp.bpm.image.jobs.TransformationJob;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * TODO comment!
 *
 * @author The eFaps Team
 * @version $Id$
 */
@EFapsUUID("27e7ac1e-83cd-453c-8d5a-435519838381")
@EFapsRevision("$Rev$")
public abstract class Processor_Base
{

    public final static String TEXT_NODE_SUFIX = "eFapsText";


    private final Context context = new Context();
    private final List<TransformationJob> transformationJobs = new ArrayList<TransformationJob>();

    public Processor_Base(final InputStream _svg)
        throws IOException
    {
        XMLResourceDescriptor.setXMLParserClassName("org.apache.xerces.parsers.SAXParser");
        final String parser = XMLResourceDescriptor.getXMLParserClassName();
        final SAXSVGDocumentFactory f = new SAXSVGDocumentFactory(parser);

        final Document svgDocument = f.createSVGDocument("http://test", _svg);
        this.context.setSvgDocument(svgDocument);
    }

    /**
     * Adds a new transformation job to be applied. Transformation jobs are
     * applied in the same order they are added to this class.
     *
     * @param e
     * @return
     */
    public boolean addTransformationJob(final TransformationJob e)
    {
        return this.transformationJobs.add(e);
    }

    /**
     * Adds a set of transformation jobs to be applied. Transformation jobs are
     * applied in the same order they are added to this object.
     *
     * @param e
     * @return
     */
    public boolean addAllTransformationJobs(final Collection<? extends TransformationJob> c)
    {
        return this.transformationJobs.addAll(c);
    }

    /**
     * Clears any transformation job previously added to this object.
     */
    public void clearTransformationJobs()
    {
        this.transformationJobs.clear();
    }

    /**
     * Applies all the transformations added to this object. Transformation jobs
     * are applied in the same order they are added to this object. This method
     * is the same as {@link #applyTransformationJobs(boolean)
     * applyTransformationJobs(true)}
     */
    public void applyTransformationJobs()
    {
        this.applyTransformationJobs(true);
    }

    /**
     * Applies all the transformations added to this object. Transformation jobs
     * are applied in the same order they are added to this object.
     *
     * @param clearTransformationJobs clear all the transformation jobs after
     *            they are applied.
     */
    public void applyTransformationJobs(final boolean clearTransformationJobs)
    {
        transform();
        if (clearTransformationJobs) {
            clearTransformationJobs();
        }
    }

    private void transform()
    {
        final NodeList nodes = this.context.getSvgDocument().getChildNodes();
        for (int i = 0; i < nodes.getLength(); i++) {
            final Node node = nodes.item(i);
            transform(node);
        }
    }

    private void transform(final Node parent)
    {
        final NodeList nodes = parent.getChildNodes();
        if (nodes != null) {
            for (int i = 0; i < nodes.getLength(); i++) {
                final Node node = nodes.item(i);
                if (node.getAttributes() != null) {
                    final Node idNode = node.getAttributes().getNamedItem("id");
                    if (idNode != null) {
                        final String id = idNode.getFirstChild().getNodeValue();
                        for (final TransformationJob transformationJob : this.transformationJobs) {
                            transformationJob.transform(this.context, id);
                        }
                    }
                }
                transform(node);
            }
        }
    }

    /**
     * Returns an XML representation of the SVG being processed.
     *
     * @return
     */
    public String toXML()
    {
        try {
            final DOMSource domSource = new DOMSource(this.context.getSvgDocument());
            final StringWriter writer = new StringWriter();
            final StreamResult result = new StreamResult(writer);
            final TransformerFactory tf = TransformerFactory.newInstance();
            final Transformer transformer = tf.newTransformer();
            transformer.transform(domSource, result);
            return writer.toString();
        } catch (final TransformerException ex) {
            throw new RuntimeException(ex);
        }
    }
}
