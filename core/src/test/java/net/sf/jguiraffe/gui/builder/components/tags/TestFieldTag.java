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
package net.sf.jguiraffe.gui.builder.components.tags;

import net.sf.jguiraffe.gui.builder.components.CompositeComponentHandler;
import net.sf.jguiraffe.gui.builder.components.FormBuilderException;
import net.sf.jguiraffe.gui.forms.ComponentHandler;
import net.sf.jguiraffe.gui.forms.ComponentHandlerImpl;

import org.apache.commons.jelly.JellyException;

/**
 * Test class for FieldTag, ComponentHandlerTag, and ReferenceTag.
 *
 * @author Oliver Heger
 * @version $Id: TestFieldTag.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestFieldTag extends AbstractTagTest
{
    /** Constant for the test script.*/
    private static final String SCRIPT = "field";

    /**
     * Tests constructing a field tag.
     */
    public void testCreateField() throws Exception
    {
        executeScript(SCRIPT);
        builderData.invokeCallBacks();

        CompositeComponentHandlerImpl cch = (CompositeComponentHandlerImpl) builderData
                .getComponentHandler("testField");
        assertNotNull(cch);
        assertNull(builderData.getComponent("testField"));
        assertEquals("text3, text1, text2", cch.getChildrenString());
    }

    /**
     * Tests executing a reference tag with undefined attributes.
     */
    public void testUndefinedReference() throws Exception
    {
        checkError("ERROR_UNDEFINED",
                "Could execute reference tag with missing attributes!");
    }

    /**
     * Tests executing a reference tag with too many attributes.
     */
    public void testBothReference() throws Exception
    {
        checkError("ERROR_BOTH",
                "Could execute reference tag with both attributes!");
    }

    /**
     * Tests executing a reference tag with an invalid group reference.
     */
    public void testInvalidGroupReference() throws Exception
    {
        checkError("ERROR_INV_GROUP",
                "Could execute reference tag with invalid group reference!");
    }

    /**
     * Tests executing a reference tag with an invalid component reference.
     */
    public void testInvalidComponentReference() throws Exception
    {
        checkError("ERROR_INV_COMP",
                "Could execute reference tag with invalid component reference!");
    }

    /**
     * Tests executing a stand alone reference.
     */
    public void testStandAloneReference() throws Exception
    {
        checkError("ERR_REF_NO_HANDLER",
                "Could execute stand alone reference tag!");
    }

    /**
     * Tests executing a field tag without a component handler definition.
     */
    public void testUndefinedField() throws Exception
    {
        checkError("ERR_UNDEF_FIELD",
                "Could execute field tag without component handler definition!");
    }

    /**
     * Tests executing a handler tag without a field definition.
     */
    public void testStandAloneHandler() throws Exception
    {
        checkError("ERR_HANDLER_NO_FIELD",
                "Could execute stand alone component handler tag!");
    }

    /**
     * Tests adding unallowed references to a simple component handler.
     */
    public void testInvalidReferences() throws Exception
    {
        checkError("ERR_INV_REF", "Could add references to simple handler!");
    }

    /**
     * Performs a test which is expected to throw an exception.
     *
     * @param builder the name of the builder
     * @param msg the fail message
     * @throws Exception if an error occurs
     */
    protected void checkError(String builder, String msg) throws Exception
    {
        builderData.setBuilderName(builder);
        try
        {
            executeScript(SCRIPT);
            builderData.invokeCallBacks();
            fail(msg);
        }
        catch (JellyException jex)
        {
            // ok
        }
        catch (FormBuilderException fex)
        {
            // ok, too
        }
    }

    /**
     * A test implementation of the CompositeComponentHandler interface.
     */
    public static class CompositeComponentHandlerImpl extends
            ComponentHandlerImpl implements CompositeComponentHandler<Object, Integer>
    {
        private StringBuffer children = new StringBuffer();

        public String getChildrenString()
        {
            return children.toString();
        }

        public void addHandler(String name, ComponentHandler<Integer> handler)
        {
            if (children.length() > 0)
            {
                children.append(", ");
            }
            children.append(name);
        }
    }
}
