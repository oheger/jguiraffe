/*
 * Copyright 2006-2022 The JGUIraffe Team.
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
package net.sf.jguiraffe.gui.builder.components.tags;

import static org.junit.Assert.assertEquals;

import java.util.Locale;

import net.sf.jguiraffe.gui.builder.components.ComponentBuilderData;
import net.sf.jguiraffe.gui.builder.components.FormBuilderException;
import net.sf.jguiraffe.gui.builder.di.tags.SetPropertyTag;
import net.sf.jguiraffe.gui.forms.bind.BeanBindingStrategy;
import net.sf.jguiraffe.resources.ResourceManager;
import net.sf.jguiraffe.transform.TransformerContext;

import org.apache.commons.jelly.JellyContext;
import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.MissingAttributeException;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for LocalizedPropertyTag.
 *
 * @author Oliver Heger
 * @version $Id: TestLocalizedTag.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestLocalizedTag
{
    /** Constant for the locale. */
    private static final Locale LOCALE = Locale.GERMAN;

    /** Constant for the resource ID. */
    private static final String RES_ID = "myResID";

    /** Constant for the resource group. */
    private static final String RES_GRP = "myResourceGroup";

    /** Constant for the resource value. */
    private static final Object RES_VALUE = "ResourceValue";

    /** Stores the transformer context mock. */
    private TransformerContext transformerContext;

    /** Stores the mock for the resource manager. */
    private ResourceManager resourceManager;

    /** The parent tag. */
    private SetPropertyTag parent;

    /** Stores the tag to be tested. */
    private LocalizedTag tag;

    @Before
    public void setUp() throws Exception
    {
        tag = new LocalizedTag();
        tag.setContext(new JellyContext());
        parent = new SetPropertyTag();
        parent.setContext(tag.getContext());
        tag.setParent(parent);
    }

    /**
     * Prepares a test that accesses resources. This method will setup a
     * resource manager and store it in the context.
     */
    private void prepareResourceTest()
    {
        transformerContext = EasyMock.createMock(TransformerContext.class);
        resourceManager = EasyMock.createMock(ResourceManager.class);
        EasyMock.expect(transformerContext.getLocale()).andReturn(LOCALE);
        EasyMock.expect(transformerContext.getResourceManager()).andReturn(
                resourceManager);
        EasyMock.replay(transformerContext);
        ComponentBuilderData cd = new ComponentBuilderData();
        cd.initializeForm(transformerContext, new BeanBindingStrategy());
        cd.put(tag.getContext());
    }

    /**
     * Tests processing a valid tag.
     */
    @Test
    public void testProcess() throws JellyTagException, FormBuilderException
    {
        prepareResourceTest();
        EasyMock.expect(resourceManager.getResource(LOCALE, RES_GRP, RES_ID))
                .andReturn(RES_VALUE);
        EasyMock.replay(resourceManager);
        tag.setResid(RES_ID);
        tag.setResgrp(RES_GRP);
        tag.process();
        assertEquals("Wrong value", RES_VALUE, parent.getValueData().getValue());
        EasyMock.verify(resourceManager, transformerContext);
    }

    /**
     * Tests the process() if when no resource group was specified. This is
     * completely legal.
     */
    @Test
    public void testProcessNoResGrp() throws JellyTagException,
            FormBuilderException
    {
        prepareResourceTest();
        EasyMock.expect(resourceManager.getResource(LOCALE, null, RES_ID))
                .andReturn(RES_VALUE);
        EasyMock.replay(resourceManager);
        tag.setResid(RES_ID);
        tag.process();
        assertEquals("Wrong value", RES_VALUE, parent.getValueData().getValue());
        EasyMock.verify(resourceManager, transformerContext);
    }

    /**
     * Tests the process() method if no resource ID was specified. This should
     * cause an exception.
     */
    @Test(expected = MissingAttributeException.class)
    public void testProcessNoResID() throws JellyTagException,
            FormBuilderException
    {
        tag.setResgrp(RES_GRP);
        tag.process();
    }

    /**
     * Tests process() if the tag has an invalid parent. This should cause an
     * exception.
     */
    @Test(expected = JellyTagException.class)
    public void testProcessInvalidParent() throws JellyTagException,
            FormBuilderException
    {
        tag.setParent(new LabelTag());
        tag.setResid(RES_ID);
        tag.process();
    }
}
