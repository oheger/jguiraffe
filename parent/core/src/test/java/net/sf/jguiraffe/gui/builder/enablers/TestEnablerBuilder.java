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
package net.sf.jguiraffe.gui.builder.enablers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Collection;

import org.junit.Before;
import org.junit.Test;

/**
 * Test class for {@code EnablerBuilder}.
 *
 * @author Oliver Heger
 * @version $Id: TestEnablerBuilder.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestEnablerBuilder
{
    /** Constant for an element name. */
    private static final String NAME = "testElement";

    /** The builder to be tested. */
    private EnablerBuilder builder;

    @Before
    public void setUp() throws Exception
    {
        builder = new EnablerBuilder();
    }

    /**
     * Tries to add a null specification. This should cause an exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAddSpecificationNull()
    {
        builder.addSpecification(null);
    }

    /**
     * Tries to add an empty specification. This should cause an exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAddSpecificationEmpty()
    {
        builder.addSpecification("");
    }

    /**
     * Tests build() if no specifications have been added.
     */
    @Test
    public void testBuildNullEnabler()
    {
        assertSame("Wrong enabler", NullEnabler.INSTANCE, builder.build());
    }

    /**
     * Tests whether an action enabler can be built.
     */
    @Test
    public void testBuildActionEnabler()
    {
        assertEquals("Wrong builder", builder, builder
                .addSpecification("action:" + NAME));
        ActionEnabler en = (ActionEnabler) builder.build();
        assertEquals("Wrong action name", NAME, en.getActionName());
    }

    /**
     * Tests whether an action group enabler can be built.
     */
    @Test
    public void testBuildActionGroupEnabler()
    {
        assertEquals("Wrong builder", builder, builder
                .addSpecification("actiongroup:" + NAME));
        ActionGroupEnabler en = (ActionGroupEnabler) builder.build();
        assertEquals("Wrong group name", NAME, en.getActionGroupName());
    }

    /**
     * Tests whether a component enabler can be built.
     */
    @Test
    public void testBuildComponentEnabler()
    {
        assertEquals("Wrong builder", builder, builder.addSpecification("comp:"
                + NAME));
        ComponentEnabler en = (ComponentEnabler) builder.build();
        assertEquals("Wrong component name", NAME, en.getComponentName());
    }

    /**
     * Tests whether enabler specifications are case insensitive.
     */
    @Test
    public void testBuildCaseInsensitive()
    {
        builder.addSpecification("ACTION:" + NAME);
        assertTrue("Wrong enabler", builder.build() instanceof ActionEnabler);
    }

    /**
     * Tests whether whitespace are ignored in specifications.
     */
    @Test
    public void testBuildWhitespace()
    {
        builder.addSpecification("   actiONGroup        :    " + NAME + "   ");
        ActionGroupEnabler en = (ActionGroupEnabler) builder.build();
        assertEquals("Wrong group name", NAME, en.getActionGroupName());
    }

    /**
     * Helper method for checking a chain enabler.
     */
    private void checkChainEnabler()
    {
        ChainElementEnabler en = (ChainElementEnabler) builder.build();
        Collection<ElementEnabler> children = en.getChildEnablers();
        assertEquals("Wrong number of children", 3, children.size());
        int idx = 1;
        for (ElementEnabler c : children)
        {
            ComponentEnabler ce = (ComponentEnabler) c;
            assertEquals("Wrong component name", NAME + idx, ce
                    .getComponentName());
            idx++;
        }
    }

    /**
     * Tests whether a chain enabler can be constructed from a single
     * specification.
     */
    @Test
    public void testBuildChainSingleSpec()
    {
        builder.addSpecification("comp:" + NAME + "1,comp: " + NAME
                + "2 , comp : " + NAME + "3");
        checkChainEnabler();
    }

    /**
     * Tests whether a chain enabler can be constructed from multiple
     * specifications.
     */
    @Test
    public void testBuildChainMultiSpec()
    {
        builder.addSpecification("comp:" + NAME + 1).addSpecification(
                "comp: " + NAME + 2).addSpecification("comp : " + NAME + 3);
        checkChainEnabler();
    }

    /**
     * Tests whether an invalid specification is detected.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testBuildInvalidSpec()
    {
        builder.addSpecification("comp:" + NAME).addSpecification(
                "invalid spec");
        builder.build();
    }

    /**
     * Tests whether an invalid specification is detected in a list of
     * specifications.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testBuildInvalidSpecMulti()
    {
        builder.addSpecification("action : " + NAME + ", comp:" + NAME
                + ",test!invalid");
        builder.build();
    }

    /**
     * Tests whether the builder can be reset.
     */
    @Test
    public void testReset()
    {
        builder.addSpecification("action:" + NAME).addSpecification(
                "comp:" + NAME).addSpecification("invalid");
        builder.reset();
        assertTrue("No null enabler", builder.build() instanceof NullEnabler);
    }

    /**
     * Tests whether reset() is automatically called after a build() operation.
     */
    @Test
    public void testResetAfterBuild()
    {
        builder.addSpecification("action:" + NAME).build();
        assertTrue("No null enabler", builder.build() instanceof NullEnabler);
    }
}
