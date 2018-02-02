/*
 * Copyright 2006-2018 The JGUIraffe Team.
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
package net.sf.jguiraffe.gui.builder.di.tags;

import java.util.NoSuchElementException;

import net.sf.jguiraffe.di.BeanStore;
import net.sf.jguiraffe.gui.builder.di.DIBuilderData;

import org.apache.commons.jelly.JellyContext;
import org.apache.commons.jelly.JellyTagException;

import junit.framework.TestCase;

/**
 * Test class for BeanStoreTag.
 *
 * @author Oliver Heger
 * @version $Id: TestBeanStoreTag.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestBeanStoreTag extends TestCase
{
    /** Constant for the name of the bean store. */
    private static final String STORE_NAME = "myBeanStore";

    /** Constant for the name of the parent bean store. */
    private static final String PARENT_NAME = "myParentStore";

    /** Stores the Jelly context used for testing. */
    private JellyContext context;

    /** Stores the builder data object, which also contains the stores. */
    private DIBuilderData builderData;

    /** The tag to be tested. */
    private BeanStoreTag tag;

    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        context = new JellyContext();
        builderData = new DIBuilderData();
        builderData.put(context);
        tag = createTag();
    }

    /**
     * Creates a bean store tag and initializes it with the context.
     *
     * @return the new tag
     * @throws JellyTagException if an error occurs
     */
    private BeanStoreTag createTag() throws JellyTagException
    {
        BeanStoreTag result = new BeanStoreTag();
        result.setContext(context);
        return result;
    }

    /**
     * Simulates a parent tag. Creates a tag with the parent name and executes
     * it.
     *
     * @return the parent tag
     * @throws JellyTagException in case of an error
     */
    private BeanStoreTag setUpParentTag() throws JellyTagException
    {
        BeanStoreTag parent = createTag();
        parent.setName(PARENT_NAME);
        parent.process();
        return parent;
    }

    /**
     * Tests processing a tag that does not have a parent.
     */
    public void testProcessNoParent() throws JellyTagException
    {
        tag.setName(STORE_NAME);
        tag.process();
        assertTrue("Store not created", builderData.hasBeanStore(STORE_NAME));
        assertSame("Wrong parent of store", builderData.getRootBeanStore(),
                builderData.getBeanStore(STORE_NAME).getParent());
    }

    /**
     * Tests processing a tag with a parentName attribute.
     */
    public void testProcessWithParentAttribute() throws JellyTagException
    {
        setUpParentTag();
        tag.setName(STORE_NAME);
        tag.setParentName(PARENT_NAME);
        tag.process();
        assertSame("Wrong parent of store", builderData
                .getBeanStore(PARENT_NAME), builderData
                .getBeanStore(STORE_NAME).getParent());
    }

    /**
     * Tests processing a tag that is nested inside a parent tag.
     */
    public void testProcessWithParentTag() throws JellyTagException
    {
        BeanStoreTag parent = setUpParentTag();
        tag.setName(STORE_NAME);
        tag.setParentStoreTag(parent);
        tag.process();
        assertSame("Wrong parent of store", builderData
                .getBeanStore(PARENT_NAME), builderData
                .getBeanStore(STORE_NAME).getParent());
    }

    /**
     * Tests whether the parentName attribute has higher priority.
     */
    public void testProcessWithParentTagAndAttribute() throws JellyTagException
    {
        final String alternateParent = "myOtherParent";
        builderData.addBeanStore(alternateParent, null);
        BeanStore parentStore = builderData.getBeanStore(alternateParent);
        BeanStoreTag parent = setUpParentTag();
        tag.setName(STORE_NAME);
        tag.setParentName(alternateParent);
        tag.setParentStoreTag(parent);
        tag.process();
        assertSame("Wrong parent of store", parentStore, builderData
                .getBeanStore(STORE_NAME).getParent());
    }

    /**
     * Tests processing of a tag that defines a non existing parent store. This
     * should cause an exception.
     */
    public void testProcessNonExistingParent() throws JellyTagException
    {
        setUpParentTag();
        tag.setName(STORE_NAME);
        tag.setParentName("nonExistingParent");
        try
        {
            tag.process();
            fail("Could process tag with unexisting parent!");
        }
        catch (NoSuchElementException nsex)
        {
            // ok
        }
    }

    /**
     * Tests a tag with no name. This should cause an exception.
     */
    public void testProcessNoName()
    {
        try
        {
            tag.process();
            fail("Could process tag with no name!");
        }
        catch (JellyTagException jtex)
        {
            // ok
        }
    }
}
