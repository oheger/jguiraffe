/*
 * Copyright 2006-2013 The JGUIraffe Team.
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
package net.sf.jguiraffe.gui.builder.action.tags;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import net.sf.jguiraffe.gui.builder.action.ActionBuilder;
import net.sf.jguiraffe.gui.builder.action.ActionManagerImpl;
import net.sf.jguiraffe.gui.builder.action.PopupMenuHandler;
import net.sf.jguiraffe.gui.builder.components.ComponentBuilderCallBack;
import net.sf.jguiraffe.gui.builder.components.ComponentBuilderData;
import net.sf.jguiraffe.gui.builder.components.Container;
import net.sf.jguiraffe.gui.builder.components.FormBuilderException;
import net.sf.jguiraffe.gui.builder.components.tags.ComponentBaseTag;
import net.sf.jguiraffe.gui.forms.TransformerContextImpl;
import net.sf.jguiraffe.gui.forms.bind.BeanBindingStrategy;

import org.apache.commons.jelly.JellyContext;
import org.apache.commons.jelly.JellyTagException;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for PopupHandlerTag.
 *
 * @author Oliver Heger
 * @version $Id: TestPopupHandlerTag.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestPopupHandlerTag
{
    /** The component builder data object. */
    private ComponentBuilderData compData;

    /** The action builder data object. */
    private ActionBuilder actData;

    /** A mock object for a menu handler. */
    private PopupMenuHandler handler;

    /** A dummy component. */
    private Container component;

    /** The parent tag. */
    private ComponentBaseTag parent;

    /** The tag to be tested. */
    private PopupHandlerTagTestImpl tag;

    @Before
    public void setUp() throws Exception
    {
        tag = new PopupHandlerTagTestImpl();
        JellyContext context = new JellyContext();
        tag.setContext(context);
        compData = new ComponentBuilderData();
        compData.initializeForm(new TransformerContextImpl(),
                new BeanBindingStrategy());
        actData = new ActionBuilder();
        actData.setActionManager(new ActionManagerImpl());
        compData.put(context);
        actData.put(context);

        component = new Container("component");
        parent = new ComponentBaseTag()
        {
            @Override
            public Object getComponent()
            {
                return component;
            }
        };
        tag.setParent(parent);
        handler = EasyMock.createNiceMock(PopupMenuHandler.class);
    }

    /**
     * Tests whether the correct base class is set.
     */
    @Test
    public void testBaseClass()
    {
        assertEquals("Wrong base class", PopupMenuHandler.class, tag
                .getBaseClass());
    }

    /**
     * Tests the passResults() implementation when the parent tag is no
     * component tag. In this case no handler association can be created.
     */
    @Test
    public void testPassResultsInvalidParent() throws JellyTagException
    {
        tag.setParent(new PopupHandlerTag());
        assertFalse("Could register handler", tag.passResults(handler));
    }

    /**
     * Tests the passResults() implementation for a successful tag execution.
     */
    @Test
    public void testPassResults() throws FormBuilderException,
            JellyTagException
    {
        ComponentBuilderCallBack callBack = EasyMock
                .createMock(ComponentBuilderCallBack.class);
        callBack.callBack(compData, null);
        EasyMock.replay(callBack);
        tag.component = component;
        tag.mockCallBack = callBack;
        assertTrue("Could not register handler", tag.passResults(handler));
        compData.invokeCallBacks();
        EasyMock.verify(callBack);
    }

    /**
     * Tests creating and executing the call back.
     */
    @Test
    public void testCreateCallBack() throws FormBuilderException
    {
        ComponentBuilderCallBack callBack = tag.createCallBack(parent, handler);
        callBack.callBack(compData, null);
        assertTrue(
                "Handler not registered: " + component,
                component.toString().indexOf(
                        ActionManagerImpl.popupHandlerText(handler, compData)) > 0);
    }

    /**
     * An easier to test implementation of PopupHandlerTag.
     */
    private static class PopupHandlerTagTestImpl extends PopupHandlerTag
    {
        /** A mock call back to be returned by createCallBack(). */
        ComponentBuilderCallBack mockCallBack;

        /** The expected component passed to createCallBack(). */
        Object component;

        /**
         * Either returns the mock call back or calls the super method.
         */
        @Override
        protected ComponentBuilderCallBack createCallBack(
                ComponentBaseTag compTag, PopupMenuHandler handler)
        {
            if (mockCallBack != null)
            {
                assertEquals("Wrong component", component, compTag
                        .getComponent());
                return mockCallBack;
            }
            return super.createCallBack(compTag, handler);
        }
    }
}
