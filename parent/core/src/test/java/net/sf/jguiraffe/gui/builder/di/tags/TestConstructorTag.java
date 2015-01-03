/*
 * Copyright 2006-2015 The JGUIraffe Team.
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

import net.sf.jguiraffe.di.ReflectionTestClass;
import net.sf.jguiraffe.di.impl.ClassDescription;
import net.sf.jguiraffe.di.impl.ConstructorInvocation;
import net.sf.jguiraffe.di.impl.MethodInvocation;
import net.sf.jguiraffe.di.impl.providers.ConstructorBeanProvider;

import org.apache.commons.jelly.JellyContext;
import org.apache.commons.jelly.JellyTagException;

import junit.framework.TestCase;

/**
 * Test class for ConstructorTag.
 *
 * @author Oliver Heger
 * @version $Id: TestConstructorTag.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestConstructorTag extends TestCase
{
    /** Stores the current Jelly context. */
    private JellyContext context;

    /** Stores the tag to be tested. */
    private ConstructorTag tag;

    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        context = new JellyContext();
        tag = new ConstructorTag();
        tag.setContext(context);
    }

    /**
     * Creates a bean tag and partly initializes it. The new bean tag is also
     * set as enclosing bean tag of the test tag.
     *
     * @return the new bean tag
     */
    private BeanTag setUpBeanTag()
    {
        BeanTag beanTag = new BeanTag();
        try
        {
            beanTag.setContext(context);
        }
        catch (JellyTagException e)
        {
            // should normally not happen
            fail("Could not init bean tag: " + e);
        }
        tag.setBeanTag(beanTag);
        return beanTag;
    }

    /**
     * Tests obtaining the target class description when it is only defined at
     * the bean tag.
     */
    public void testGetTargetClassFromBeanTag()
    {
        BeanTag bt = setUpBeanTag();
        bt.setBeanClassDesc(ClassDescription.getInstance(getClass()));
        ClassDescription cd = tag.getTargetClassDescription();
        assertEquals("Wrong class description", getClass().getName(), cd
                .getTargetClassName());
    }

    /**
     * Tests a successful execution of the tag.
     */
    public void testProcess() throws JellyTagException
    {
        BeanTag bt = setUpBeanTag();
        tag.setBeanTag(bt);
        tag.setTargetClass(ReflectionTestClass.class);
        tag.process();
        ConstructorBeanProvider creator = (ConstructorBeanProvider) bt
                .getBeanCreator();
        assertNotNull("No creator set", creator);
        ConstructorInvocation cinv = creator.getInvocation();
        assertEquals("Wrong target class", ReflectionTestClass.class.getName(),
                cinv.getTargetClass().getTargetClassName());
    }

    /**
     * Tries to process a constructor tag when no bean tag is available. This
     * should cause an exception.
     */
    public void testProcessNoBeanTag()
    {
        tag.setTargetClass(getClass());
        try
        {
            tag.process();
            fail("Could process tag without bean tag!");
        }
        catch (JellyTagException jtex)
        {
            // ok
        }
    }

    /**
     * Tries to add an invokable, which is no ConstructorInvocation. This is not
     * allowed.
     */
    public void testAddInvokableNoConstr()
    {
        try
        {
            tag.addInvokable(new MethodInvocation("test", null));
            fail("Could add invalid Invokable!");
        }
        catch (JellyTagException jtex)
        {
            // ok
        }
    }

    /**
     * Tests adding multiple Invokable objects. Only a single one is allowed.
     */
    public void testAddInvokableMultiple() throws JellyTagException
    {
        setUpBeanTag();
        ConstructorInvocation cinv = new ConstructorInvocation(ClassDescription
                .getInstance(getClass()), null);
        tag.addInvokable(cinv);
        cinv = new ConstructorInvocation(ClassDescription
                .getInstance(ReflectionTestClass.class), null);
        try
        {
            tag.addInvokable(cinv);
            fail("Could add a second Invokable!");
        }
        catch (JellyTagException jtex)
        {
            // ok
        }
    }
}
