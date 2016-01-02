/*
 * Copyright 2006-2016 The JGUIraffe Team.
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
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.Map;

import net.sf.jguiraffe.di.BeanContext;
import net.sf.jguiraffe.gui.builder.components.ComponentBuilderData;
import net.sf.jguiraffe.gui.builder.components.ComponentManager;
import net.sf.jguiraffe.gui.builder.components.FormBuilderException;

import org.apache.commons.jelly.JellyContext;
import org.apache.commons.jelly.JellyTagException;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for {@code FontTag}.
 *
 * @author Oliver Heger
 * @version $Id: TestFontTag.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestFontTag
{
    /** Constant for the name of a variable. */
    private static final String VAR = "fontVar";

    /** Constant for the test "font" object. */
    private static final Object FONT = "TestFont";

    /** The Jelly context. */
    private JellyContext context;

    /** The component builder data object. */
    private ComponentBuilderData builderData;

    /** The mock for the component manager. */
    private ComponentManager manager;

    /** The tag to be tested. */
    private FontTagTestImpl tag;

    @Before
    public void setUp() throws Exception
    {
        context = new JellyContext();
        builderData = new ComponentBuilderData();
        builderData.put(context);
        tag = new FontTagTestImpl();
        tag.setContext(context);
    }

    /**
     * Installs a mock for a bean context in the Jelly context.
     *
     * @return the mock for the bean context
     */
    private BeanContext setUpBeanContext()
    {
        BeanContext bctx = EasyMock.createMock(BeanContext.class);
        builderData.setBeanContext(bctx);
        return bctx;
    }

    /**
     * Installs a mock for the component manager.
     *
     * @param replay flag whether the mock is to be replayed
     * @return the mock
     */
    private ComponentManager setUpComponentManager(boolean replay)
    {
        manager = EasyMock.createMock(ComponentManager.class);
        builderData.setComponentManager(manager);
        return manager;
    }

    /**
     * Tests a tag that does not have a target specified.
     */
    @Test(expected = JellyTagException.class)
    public void testProcessBeforeBodyNoTarget() throws JellyTagException,
            FormBuilderException
    {
        tag.processBeforeBody();
    }

    /**
     * Tests whether an empty attributes map is created if none is specified.
     */
    @Test
    public void testProcessBeforeBodyNoMap() throws JellyTagException,
            FormBuilderException
    {
        tag.setVar(VAR);
        tag.processBeforeBody();
        assertTrue("Map contains entries", tag.getAttributesMap().isEmpty());
    }

    /**
     * Tests that the map with additional attributes cannot be modified.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testGetAttributesMapModify() throws JellyTagException,
            FormBuilderException
    {
        tag.setVar(VAR);
        tag.processBeforeBody();
        @SuppressWarnings("unchecked")
        Map<Object, Object> map = ((Map<Object, Object>) tag.getAttributesMap());
        map.put("test", this);
    }

    /**
     * Tests whether the reference to the map with the attributes can be
     * resolved.
     */
    @Test
    public void testFetchAttributesMapReference() throws JellyTagException
    {
        BeanContext bctx = setUpBeanContext();
        EasyMock.expect(bctx.containsBean(VAR)).andReturn(Boolean.TRUE);
        Map<Object, Object> map = new HashMap<Object, Object>();
        map.put("test", "value");
        EasyMock.expect(bctx.getBean(VAR)).andReturn(map);
        EasyMock.replay(bctx);
        tag.setAttributes(VAR);
        Map<?, ?> refMap = tag.fetchAttributesMap();
        assertEquals("Wrong map", map, refMap);
        assertNotSame("Map not copied", map, refMap);
        EasyMock.verify(bctx);
    }

    /**
     * Tests fetchAttributesMap() if the map name cannot be resolved.
     */
    @Test
    public void testFetchAttributesMapUnknown()
    {
        BeanContext bctx = setUpBeanContext();
        EasyMock.expect(bctx.containsBean(VAR)).andReturn(Boolean.FALSE);
        EasyMock.replay(bctx);
        tag.setAttributes(VAR);
        try
        {
            tag.fetchAttributesMap();
            fail("Invalid reference not detected!");
        }
        catch (JellyTagException jtex)
        {
            EasyMock.verify(bctx);
        }
    }

    /**
     * Tests fetchAttributesMap() if the bean referenced by the tag is not a
     * map.
     */
    @Test
    public void testFetchAttributesMapInvalidBean()
    {
        BeanContext bctx = setUpBeanContext();
        EasyMock.expect(bctx.containsBean(VAR)).andReturn(Boolean.TRUE);
        EasyMock.expect(bctx.getBean(VAR)).andReturn(this);
        EasyMock.replay(bctx);
        tag.setAttributes(VAR);
        try
        {
            tag.fetchAttributesMap();
            fail("Invalid bean not detected!");
        }
        catch (JellyTagException jtex)
        {
            EasyMock.verify(bctx);
        }
    }

    /**
     * Tests whether a font is correctly created.
     */
    @Test
    public void testCreateFont() throws FormBuilderException
    {
        ComponentManager cm = setUpComponentManager(false);
        EasyMock.expect(cm.createFont(tag)).andReturn(FONT);
        EasyMock.replay(cm);
        assertSame("Wrong font", FONT, tag.createFont(cm));
        EasyMock.verify(cm);
    }

    /**
     * Tests whether the newly created font can be assigned to a variable.
     */
    @Test
    public void testProcessVar() throws FormBuilderException, JellyTagException
    {
        setUpComponentManager(true);
        tag.setVar(VAR);
        tag.mockCreateFont();
        tag.process();
        assertSame("Wrong font in context", FONT, context.getVariable(VAR));
    }

    /**
     * Tests whether the newly created font can be passed to the parent tag.
     */
    @Test
    public void testProcessParent() throws FormBuilderException,
            JellyTagException
    {
        setUpComponentManager(true);
        LabelTag parent = new LabelTag();
        tag.setParent(parent);
        tag.mockCreateFont();
        tag.process();
        assertSame("Font not passed to parent", FONT, parent.getFont());
    }

    /**
     * A test implementation of FontTag providing some mock facilities.
     */
    private class FontTagTestImpl extends FontTag
    {
        /** A flag whether createFont() is to be mocked. */
        private boolean mockCreateFont;

        /**
         * Initializes this object to mock the createFont() method.
         */
        public void mockCreateFont()
        {
            mockCreateFont = true;
        }

        /**
         * Either returns the mock font or calls the super method. The passed in
         * manager reference is checked, too.
         */
        @Override
        protected Object createFont(ComponentManager compManager)
                throws FormBuilderException
        {
            assertSame("Wrong manager", manager, compManager);
            return mockCreateFont ? FONT : super.createFont(compManager);
        }
    }
}
