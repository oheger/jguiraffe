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
package net.sf.jguiraffe.gui.builder.components.tags;

import java.util.Date;

import net.sf.jguiraffe.gui.forms.ComponentHandler;
import net.sf.jguiraffe.gui.forms.DefaultFieldHandler;
import net.sf.jguiraffe.gui.forms.FieldHandler;

import org.apache.commons.jelly.JellyException;
import org.easymock.EasyMock;

/**
 * Test class for InputComponentTag.
 *
 * @author Oliver Heger
 * @version $Id: TestInputComponentTag.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestInputComponentTag extends AbstractTagTest
{
    /** Constant for the name of the test script.*/
    private static final String SCRIPT = "inputcomponent";

    /**
     * Tests creation of some components with different attributes.
     */
    public void testComponents() throws Exception
    {
        checkScript(SCRIPT, "Container: ROOT { "
                + "TEXTFIELD [ NAME = name DISP = Name ], "
                + "TEXTFIELD [ NAME = birthDate PROP = birthDay DISP = Date of birth ], "
                + "TEXTFIELD [ NAME = salary DISP = Salary ], "
                + "TEXTFIELD [ NAME = level ] }");
    }

    /**
     * Tests whether the noField attribute is correctly evaluated.
     */
    public void testNoFieldAttribute() throws Exception
    {
        executeScript(SCRIPT);
        assertNull("Field handler for name field created", builderData
                .getFieldHandler("name"));
        assertNull("Field handler for name field added to form", builderData
                .getForm().getField("name"));
    }

    /**
     * Tests whether the type name attribute is correctly evaluated.
     */
    public void testSetTypeName() throws Exception
    {
        executeScript(SCRIPT);
        DefaultFieldHandler fh = (DefaultFieldHandler) builderData
                .getFieldHandler("birthDate");
        assertNotNull("Field handler for date field not found", fh);
        assertEquals("Wrong type for date field", Date.class, fh.getType());
        assertEquals("Wrong property name for date field", "birthDay", fh
                .getPropertyName());
    }

    /**
     * Tests whether the display name is correctly set for the field handlers.
     */
    public void testSetDisplayName() throws Exception
    {
        executeScript(SCRIPT);
        FieldHandler fh = builderData.getFieldHandler("birthDate");
        assertEquals("Wrong display name for birthDay field", "Date of birth",
                fh.getDisplayName());
        fh = builderData.getFieldHandler("level");
        assertNull("Display name set for level field", fh.getDisplayName());
    }

    /**
     * Tests if a tag without a name throws an exception.
     */
    public void testErrorUndefName() throws Exception
    {
        builderData.setBuilderName(ERROR_BUILDER);
        try
        {
            executeScript(SCRIPT);
            fail("Could execute tag without name attribute!");
        }
        catch (JellyException jex)
        {
            // ok
        }
    }

    /**
     * Tests what happens if an invalid class name was specified.
     */
    public void testErrorClassName() throws Exception
    {
        builderData.setBuilderName("ERR_CLS_BUILDER");
        try
        {
            executeScript(SCRIPT);
            fail("Could execute tag with invalid type class name!");
        }
        catch (JellyException jex)
        {
            // ok
        }
    }

    /**
     * Tests querying the tag's component.
     */
    public void testGetComponent()
    {
        final ComponentHandler<?> compHandler = EasyMock
                .createMock(ComponentHandler.class);
        final Object component = "MyComponent";
        EasyMock.expect(compHandler.getComponent()).andReturn(component);
        EasyMock.replay(compHandler);
        TextFieldTag tag = new TextFieldTag()
        {
            @Override
            public ComponentHandler<?> getComponentHandler()
            {
                return compHandler;
            }
        };
        assertEquals("Wrong component", component, tag.getComponent());
        EasyMock.verify(compHandler);
    }

    /**
     * Tests querying the tag's component when the handler is undefined.
     */
    public void testGetComponentHandlerUndefined()
    {
        TextFieldTag tag = new TextFieldTag();
        assertNull("Component handler is defined", tag.getComponentHandler());
        assertNull("Got a component", tag.getComponent());
    }
}
