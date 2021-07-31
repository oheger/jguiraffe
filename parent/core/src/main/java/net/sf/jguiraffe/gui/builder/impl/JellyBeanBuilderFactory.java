/*
 * Copyright 2006-2021 The JGUIraffe Team.
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
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
 */
package net.sf.jguiraffe.gui.builder.impl;

import net.sf.jguiraffe.gui.builder.BeanBuilder;
import net.sf.jguiraffe.gui.builder.BeanBuilderFactory;
import net.sf.jguiraffe.gui.builder.BuilderException;

/**
 * <p>
 * An implementation of the <code>BeanBuilderFactory</code> interface that
 * returns <code>{@link JellyBeanBuilder}</code> instances.
 * </p>
 * <p>
 * This bean builder factory implementation can be used for obtaining builder
 * objects that are able to process Jelly scripts with bean definitions.
 * <em>Note:</em> The setter methods defined by this class are intended to be
 * used for initialization purposes only (i.e. using property injection). After
 * an instance was passed to its clients, it should not be changed any more. If
 * this criterion is fulfilled, the instance can be shared between multiple
 * threads.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: JellyBeanBuilderFactory.java 205 2012-01-29 18:29:57Z oheger $
 */
public class JellyBeanBuilderFactory implements BeanBuilderFactory
{
    /** Constant for the default name space URI for the DIBuilder tag library. */
    public static final String NSURI_DI_BUILDER = "diBuilder";

    /** Stores the name space URI to be used for the DI builder tag library. */
    private String diBuilderNameSpaceURI;

    /**
     * Returns the URI for the name space of the DI builder tag library. If no
     * URI was specified, a default URI is returned.
     *
     * @return the URI for the DI builder tag library
     */
    public String getDiBuilderNameSpaceURI()
    {
        return (diBuilderNameSpaceURI != null) ? diBuilderNameSpaceURI
                : NSURI_DI_BUILDER;
    }

    /**
     * Sets the URI for the name space of the DI builder tag library. In the
     * Jelly scripts to be processed this URI must be used for specifying a name
     * space prefix for the DI builder tags.
     *
     * @param diBuilderNameSpaceURI the URI for the DI builder tag library
     */
    public void setDiBuilderNameSpaceURI(String diBuilderNameSpaceURI)
    {
        this.diBuilderNameSpaceURI = diBuilderNameSpaceURI;
    }

    /**
     * Returns the builder instance to be used.
     *
     * @return the new builder instance
     * @throws BuilderException if an error occurs
     */
    public BeanBuilder getBeanBuilder() throws BuilderException
    {
        JellyBeanBuilder builder = new JellyBeanBuilder();
        builder.setDiBuilderNameSpaceURI(getDiBuilderNameSpaceURI());
        return builder;
    }
}
