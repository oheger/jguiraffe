/*
 * Copyright 2006-2014 The JGUIraffe Team.
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
package net.sf.jguiraffe.gui.builder.window.tags;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import net.sf.jguiraffe.gui.builder.window.ctrl.ColorFieldMarker;
import net.sf.jguiraffe.gui.builder.window.ctrl.FormControllerFieldStatusListener;
import net.sf.jguiraffe.gui.builder.window.ctrl.FormControllerFormListener;
import net.sf.jguiraffe.gui.builder.window.ctrl.FormControllerValidationListener;

import org.apache.commons.jelly.JellyContext;
import org.apache.commons.jelly.JellyTagException;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for {@code FormControllerListenerTag}.
 *
 * @author Oliver Heger
 * @version $Id: TestFormControllerListenerTag.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestFormControllerListenerTag
{
    /** The parent tag. */
    private FormControllerTagTestImpl parent;

    /** The tag to be tested. */
    private FormControllerListenerTag tag;

    @Before
    public void setUp() throws Exception
    {
        JellyContext context = new JellyContext();
        parent = new FormControllerTagTestImpl();
        parent.setContext(context);
        tag = new FormControllerListenerTag();
        tag.setContext(context);
        tag.setParent(parent);
    }

    /**
     * Tests a successful listener registration.
     */
    @Test
    public void testPassResults() throws JellyTagException
    {
        ColorFieldMarker bean = new ColorFieldMarker();
        assertTrue("Wrong result", tag.passResults(bean));
        assertEquals("Wrong number of validation listeners", 1,
                parent.validationListeners.size());
        assertEquals("Wrong number of field status listeners", 1,
                parent.fieldStatusListeners.size());
        assertEquals("Wrong validation listener", bean,
                parent.validationListeners.get(0));
        assertEquals("Wrong field status listener", bean,
                parent.fieldStatusListeners.get(0));
    }

    /**
     * Tests whether a form listener can be registered.
     */
    @Test
    public void testPassResultsFormListener() throws JellyTagException
    {
        FormControllerFormListener l = EasyMock
                .createMock(FormControllerFormListener.class);
        EasyMock.replay(l);
        assertTrue("Wrong result", tag.passResults(l));
        assertEquals("Wrong number of form listeners", 1, parent.formListeners
                .size());
        assertEquals("Wrong form listener", l, parent.formListeners.get(0));
        EasyMock.verify(l);
    }

    /**
     * Tests whether the suppressValidationListener attribute is evaluated.
     */
    @Test
    public void testPassResultsSuppressValidationListener()
            throws JellyTagException
    {
        ColorFieldMarker bean = new ColorFieldMarker();
        tag.setAttribute("suppressValidationListener", Boolean.TRUE);
        assertTrue("Wrong result", tag.passResults(bean));
        assertTrue("Got a validation listener", parent.validationListeners
                .isEmpty());
        assertEquals("Wrong number of field status listeners", 1,
                parent.fieldStatusListeners.size());
    }

    /**
     * Tests whether the suppressFieldStatusListener attribute is evaluated.
     */
    @Test
    public void testPassResultsSuppressFieldStatusListener()
            throws JellyTagException
    {
        ColorFieldMarker bean = new ColorFieldMarker();
        tag.setAttribute("suppressFieldStatusListener", "xxx");
        assertTrue("Wrong result", tag.passResults(bean));
        assertTrue("Got a field status listener", parent.fieldStatusListeners
                .isEmpty());
        assertEquals("Wrong number of validation listeners", 1,
                parent.validationListeners.size());
    }

    /**
     * Tests whether the suppressFormListener attribute is evaluated.
     */
    @Test
    public void testPassResultsSuppressFormListener() throws JellyTagException
    {
        FormControllerFormListener l = EasyMock
                .createMock(FormControllerFormListener.class);
        EasyMock.replay(l);
        tag.setAttribute("suppressFormListener", Boolean.TRUE);
        assertTrue("Wrong result", tag.passResults(l));
        assertTrue("Got form listeners", parent.formListeners.isEmpty());
        EasyMock.verify(l);
    }

    /**
     * Tests the tag if the parent tag is no controller tag.
     */
    @Test
    public void testPassResultsInvalidParent() throws JellyTagException
    {
        tag.setParent(null);
        assertFalse("Wrong result", tag.passResults(new ColorFieldMarker()));
    }

    /**
     * A specialized form controller tag implementation for testing whether the
     * expected listeners are registered.
     */
    private static class FormControllerTagTestImpl extends FormControllerTag
    {
        /** Stores the registered validation listeners. */
        final List<FormControllerValidationListener> validationListeners;

        /** Stores the registered field status listeners. */
        final List<FormControllerFieldStatusListener> fieldStatusListeners;

        /** Stores the registered form listeners. */
        final List<FormControllerFormListener> formListeners;

        public FormControllerTagTestImpl()
        {
            validationListeners = new ArrayList<FormControllerValidationListener>();
            fieldStatusListeners = new ArrayList<FormControllerFieldStatusListener>();
            formListeners = new ArrayList<FormControllerFormListener>();
        }

        /**
         * Records this invocation.
         */
        @Override
        public void addFieldStatusListener(FormControllerFieldStatusListener l)
        {
            fieldStatusListeners.add(l);
        }

        /**
         * Records this invocation.
         */
        @Override
        public void addValidationListener(FormControllerValidationListener l)
        {
            validationListeners.add(l);
        }

        /**
         * Records this invocation.
         */
        @Override
        public void addFormListener(FormControllerFormListener l)
        {
            formListeners.add(l);
        }
    }
}
