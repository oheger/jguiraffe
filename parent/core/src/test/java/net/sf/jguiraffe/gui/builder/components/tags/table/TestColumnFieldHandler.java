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
package net.sf.jguiraffe.gui.builder.components.tags.table;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import net.sf.jguiraffe.gui.forms.ComponentHandler;
import net.sf.jguiraffe.gui.forms.FieldHandler;
import net.sf.jguiraffe.gui.forms.ValidationPhase;
import net.sf.jguiraffe.transform.DefaultValidationResult;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for ColumnFieldHandler.
 *
 * @author Oliver Heger
 * @version $Id: TestColumnFieldHandler.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestColumnFieldHandler
{
    /** Constant for a test data value. */
    private static final Object TEST_DATA = "testData";

    /** Holds a reference to the instance to be tested. */
    private ColumnFieldHandler handler;

    /**
     * Creates the instance to be tested. As the wrapped field handler a mock
     * object is set.
     */
    @Before
    public void setUp() throws Exception
    {
        handler = new ColumnFieldHandler(EasyMock
                .createMock(FieldHandler.class));
    }

    /**
     * Returns the field handler mock that is wrapped by the test instance.
     *
     * @return the field handler mock
     */
    private FieldHandler getMock()
    {
        return handler.getWrappedHandler();
    }

    /**
     * Tries to create the handler with a null wrapped handler. This should
     * cause an exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testInitNull()
    {
        new ColumnFieldHandler(null);
    }

    /**
     * Tests obtaining the component handler.
     */
    @Test
    public void testGetComponentHandler()
    {
        ComponentHandler<?> mockCompHandler = EasyMock
                .createMock(ComponentHandler.class);
        getMock().getComponentHandler();
        EasyMock.expectLastCall().andReturn(mockCompHandler);
        EasyMock.replay(mockCompHandler, getMock());
        assertEquals("Wrong component handler returned", mockCompHandler,
                handler.getComponentHandler());
        EasyMock.verify(mockCompHandler, getMock());
    }

    /**
     * Tests obtaining the data.
     */
    @Test
    public void testGetData()
    {
        EasyMock.expect(getMock().getData()).andReturn(TEST_DATA);
        EasyMock.replay(getMock());
        assertEquals("Wrong data returned", TEST_DATA, handler.getData());
        EasyMock.verify(getMock());
    }

    /**
     * Tests obtaining the property name.
     */
    @Test
    public void testGetPropertyName()
    {
        final String propName = "property";
        EasyMock.expect(getMock().getPropertyName()).andReturn(propName);
        EasyMock.replay(getMock());
        assertEquals("Wrong property name", propName, handler.getPropertyName());
        EasyMock.verify(getMock());
    }

    /**
     * Tests obtaining the display name.
     */
    @Test
    public void testGetDisplayName()
    {
        final String dispName = "DisplayName";
        EasyMock.expect(getMock().getDisplayName()).andReturn(dispName);
        EasyMock.replay(getMock());
        assertEquals("Wrong display name", dispName, handler.getDisplayName());
        EasyMock.verify(getMock());
    }

    /**
     * Tests obtaining the type.
     */
    @Test
    public void testGetType()
    {
        final Class<?> type = String.class;
        getMock().getType();
        EasyMock.expectLastCall().andReturn(type);
        EasyMock.replay(getMock());
        assertEquals("Wrong type returned", type, handler.getType());
        EasyMock.verify(getMock());
    }

    /**
     * Tests validation.
     */
    @Test
    public void testValidate()
    {
        EasyMock.expect(getMock().validate(ValidationPhase.SYNTAX)).andReturn(
                DefaultValidationResult.VALID);
        EasyMock.expect(getMock().validate(ValidationPhase.LOGIC)).andReturn(
                DefaultValidationResult.VALID);
        EasyMock.replay(getMock());
        assertTrue("Wrong field validation result", handler.validate(
                ValidationPhase.SYNTAX).isValid());
        assertTrue("Wrong form validation result", handler.validate(
                ValidationPhase.LOGIC).isValid());
        EasyMock.verify(getMock());
    }

    /**
     * Tests setting a new data value.
     */
    @Test
    public void testSetData()
    {
        getMock().setData(TEST_DATA);
        EasyMock.replay(getMock());
        handler.setData(TEST_DATA);
        EasyMock.verify(getMock());
    }

    /**
     * Tests setting a data value multiple times. It should be set only once on
     * the wrapped handler.
     */
    @Test
    public void testSetDataTwice()
    {
        getMock().setData(TEST_DATA);
        EasyMock.replay(getMock());
        handler.setData(TEST_DATA);
        handler.setData(TEST_DATA);
        EasyMock.verify(getMock());
    }

    /**
     * Tests setting the data to null. This will test null comparisons.
     */
    @Test
    public void testSetDataNull()
    {
        getMock().setData(TEST_DATA);
        getMock().setData(null);
        EasyMock.replay(getMock());
        handler.setData(TEST_DATA);
        handler.setData(null);
        handler.setData(null);
        EasyMock.verify(getMock());
    }

    /**
     * Tests calling validate() after setData(). This should clear the cache.
     */
    @Test
    public void testSetDataAndValidate()
    {
        getMock().setData(TEST_DATA);
        EasyMock.expect(getMock().validate(ValidationPhase.SYNTAX)).andReturn(
                DefaultValidationResult.VALID);
        getMock().setData(TEST_DATA);
        EasyMock.replay(getMock());
        handler.setData(TEST_DATA);
        handler.validate(ValidationPhase.SYNTAX);
        handler.setData(TEST_DATA);
        EasyMock.verify(getMock());
    }

    /**
     * Tests setting a null data value on a newly initialized handler. This call
     * must be passed to the wrapped handler.
     */
    @Test
    public void testSetDataNullFirst()
    {
        getMock().setData(null);
        EasyMock.replay(getMock());
        handler.setData(null);
        EasyMock.verify(getMock());
    }
}
