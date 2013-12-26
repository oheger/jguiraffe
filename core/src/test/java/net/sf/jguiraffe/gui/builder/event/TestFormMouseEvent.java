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
package net.sf.jguiraffe.gui.builder.event;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import net.sf.jguiraffe.JGuiraffeTestHelper;
import net.sf.jguiraffe.gui.forms.ComponentHandlerImpl;

import org.junit.Test;

/**
 * Test class for {@code FormMouseEvent}.
 *
 * @author Oliver Heger
 * @version $Id: TestFormMouseEvent.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestFormMouseEvent
{
    /** Constant for a test component handler. */
    private static final ComponentHandlerImpl HANDLER = new ComponentHandlerImpl();

    /** Constant for a component name. */
    private static final String NAME = "testMouseComponent";

    /** Constant for an X position. */
    private static final int XPOS = 320;

    /** Constant for an Y position. */
    private static final int YPOS = 200;

    /**
     * Tests the creation of a mouse event.
     */
    @Test
    public void testInit()
    {
        FormMouseEvent event = new FormMouseEvent(this, HANDLER, NAME,
                FormMouseEvent.Type.MOUSE_CLICKED, XPOS, YPOS,
                FormMouseEvent.BUTTON2, EnumSet.of(Modifiers.ALT,
                        Modifiers.SHIFT));
        assertEquals("Wrong source", this, event.getSource());
        assertEquals("Wrong handler", HANDLER, event.getHandler());
        assertEquals("Wrong name", NAME, event.getName());
        assertEquals("Wrong type", FormMouseEvent.Type.MOUSE_CLICKED, event
                .getType());
        assertEquals("Wrong xpos", XPOS, event.getX());
        assertEquals("Wrong ypos", YPOS, event.getY());
        assertEquals("Wrong button", FormMouseEvent.BUTTON2, event.getButton());
        Set<Modifiers> mods = event.getModifiers();
        assertEquals("Wrong number of modifiers", 2, mods.size());
        assertTrue("ALT not found", mods.contains(Modifiers.ALT));
        assertTrue("SHIFT not found", mods.contains(Modifiers.SHIFT));
    }

    /**
     * Tries to create an instance without a type. This should cause an
     * exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testInitNoType()
    {
        new FormMouseEvent(this, HANDLER, NAME, null, XPOS, YPOS,
                FormMouseEvent.NO_BUTTON, EnumSet.of(Modifiers.META));
    }

    /**
     * Tests whether null modifiers can be passed.
     */
    @Test
    public void testInitNullModifiers()
    {
        FormMouseEvent event = new FormMouseEvent(this, HANDLER, NAME,
                FormMouseEvent.Type.MOUSE_CLICKED, XPOS, YPOS,
                FormMouseEvent.BUTTON3, null);
        assertTrue("Non empty modifiers", event.getModifiers().isEmpty());
    }

    /**
     * Tests whether empty modifiers can be passed.
     */
    @Test
    public void testInitEmptyModifiers()
    {
        FormMouseEvent event = new FormMouseEvent(this, HANDLER, NAME,
                FormMouseEvent.Type.MOUSE_CLICKED, XPOS, YPOS,
                FormMouseEvent.BUTTON3, new HashSet<Modifiers>());
        assertTrue("Non empty modifiers", event.getModifiers().isEmpty());
    }

    /**
     * Tests whether a defensive copy of the modifiers is created.
     */
    @Test
    public void testInitModifiersModify()
    {
        Set<Modifiers> mods = EnumSet.allOf(Modifiers.class);
        FormMouseEvent event = new FormMouseEvent(this, HANDLER, NAME,
                FormMouseEvent.Type.MOUSE_CLICKED, XPOS, YPOS,
                FormMouseEvent.BUTTON3, mods);
        mods.remove(Modifiers.ALT);
        mods.remove(Modifiers.ALT_GRAPH);
        assertEquals("Modifiers were changed", Modifiers.values().length, event
                .getModifiers().size());
    }

    /**
     * Tests whether the set returned by getModifiers() is immutable.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testGetModifiersModify()
    {
        FormMouseEvent event = new FormMouseEvent(this, HANDLER, NAME,
                FormMouseEvent.Type.MOUSE_CLICKED, XPOS, YPOS,
                FormMouseEvent.BUTTON2, EnumSet.of(Modifiers.ALT,
                        Modifiers.SHIFT));
        Iterator<Modifiers> it = event.getModifiers().iterator();
        it.next();
        it.remove();
    }

    /**
     * Tests the string representation of the event.
     */
    @Test
    public void testToString()
    {
        Set<Modifiers> mods = EnumSet.allOf(Modifiers.class);
        FormMouseEvent event = new FormMouseEvent(this, HANDLER, NAME,
                FormMouseEvent.Type.MOUSE_CLICKED, XPOS, YPOS,
                FormMouseEvent.BUTTON3, mods);
        String s = event.toString();
        assertEquals("Wrong string", "FormMouseEvent [ componentName = " + NAME
                + ", TYPE = MOUSE_CLICKED, X = " + XPOS + ", Y = " + YPOS
                + ", BUTTON = " + FormMouseEvent.BUTTON3 + ", MODIFIERS = "
                + mods + " ]", s);
    }

    /**
     * Tests equals() if the expected result is true.
     */
    @Test
    public void testEqualsTrue()
    {
        FormMouseEvent event =
                new FormMouseEvent(this, HANDLER, NAME,
                        FormMouseEvent.Type.MOUSE_CLICKED, XPOS, YPOS,
                        FormMouseEvent.BUTTON2, EnumSet.of(Modifiers.ALT,
                                Modifiers.SHIFT));
        JGuiraffeTestHelper.checkEquals(event, event, true);
        FormMouseEvent e2 =
                new FormMouseEvent(this, HANDLER, NAME,
                        FormMouseEvent.Type.MOUSE_CLICKED, XPOS, YPOS,
                        FormMouseEvent.BUTTON2, EnumSet.of(Modifiers.ALT,
                                Modifiers.SHIFT));
        JGuiraffeTestHelper.checkEquals(event, e2, true);
    }

    /**
     * Tests equals() if the expected result is false.
     */
    @Test
    public void testEqualsFalse()
    {
        FormMouseEvent event =
                new FormMouseEvent(this, HANDLER, NAME,
                        FormMouseEvent.Type.MOUSE_CLICKED, XPOS, YPOS,
                        FormMouseEvent.BUTTON2, EnumSet.of(Modifiers.ALT,
                                Modifiers.SHIFT));
        FormMouseEvent e2 =
                new FormMouseEvent(new Object(), HANDLER, NAME,
                        FormMouseEvent.Type.MOUSE_CLICKED, XPOS, YPOS,
                        FormMouseEvent.BUTTON2, EnumSet.of(Modifiers.ALT,
                                Modifiers.SHIFT));
        JGuiraffeTestHelper.checkEquals(event, e2, false);
        e2 =
                new FormMouseEvent(this, null, NAME,
                        FormMouseEvent.Type.MOUSE_CLICKED, XPOS, YPOS,
                        FormMouseEvent.BUTTON2, EnumSet.of(Modifiers.ALT,
                                Modifiers.SHIFT));
        JGuiraffeTestHelper.checkEquals(event, e2, false);
        e2 =
                new FormMouseEvent(this, new ComponentHandlerImpl(), NAME,
                        FormMouseEvent.Type.MOUSE_CLICKED, XPOS, YPOS,
                        FormMouseEvent.BUTTON2, EnumSet.of(Modifiers.ALT,
                                Modifiers.SHIFT));
        JGuiraffeTestHelper.checkEquals(event, e2, false);
        e2 =
                new FormMouseEvent(this, HANDLER, null,
                        FormMouseEvent.Type.MOUSE_CLICKED, XPOS, YPOS,
                        FormMouseEvent.BUTTON2, EnumSet.of(Modifiers.ALT,
                                Modifiers.SHIFT));
        JGuiraffeTestHelper.checkEquals(event, e2, false);
        e2 =
                new FormMouseEvent(this, HANDLER, NAME + "_other",
                        FormMouseEvent.Type.MOUSE_CLICKED, XPOS, YPOS,
                        FormMouseEvent.BUTTON2, EnumSet.of(Modifiers.ALT,
                                Modifiers.SHIFT));
        JGuiraffeTestHelper.checkEquals(event, e2, false);
        e2 =
                new FormMouseEvent(this, HANDLER, NAME,
                        FormMouseEvent.Type.MOUSE_DOUBLE_CLICKED, XPOS, YPOS,
                        FormMouseEvent.BUTTON2, EnumSet.of(Modifiers.ALT,
                                Modifiers.SHIFT));
        JGuiraffeTestHelper.checkEquals(event, e2, false);
        e2 =
                new FormMouseEvent(this, HANDLER, NAME,
                        FormMouseEvent.Type.MOUSE_CLICKED, XPOS + 1, YPOS,
                        FormMouseEvent.BUTTON2, EnumSet.of(Modifiers.ALT,
                                Modifiers.SHIFT));
        JGuiraffeTestHelper.checkEquals(event, e2, false);
        e2 =
                new FormMouseEvent(this, HANDLER, NAME,
                        FormMouseEvent.Type.MOUSE_CLICKED, XPOS, YPOS - 1,
                        FormMouseEvent.BUTTON2, EnumSet.of(Modifiers.ALT,
                                Modifiers.SHIFT));
        JGuiraffeTestHelper.checkEquals(event, e2, false);
        e2 =
                new FormMouseEvent(this, HANDLER, NAME,
                        FormMouseEvent.Type.MOUSE_CLICKED, XPOS, YPOS,
                        FormMouseEvent.BUTTON1, EnumSet.of(Modifiers.ALT,
                                Modifiers.SHIFT));
        JGuiraffeTestHelper.checkEquals(event, e2, false);
        e2 =
                new FormMouseEvent(this, HANDLER, NAME,
                        FormMouseEvent.Type.MOUSE_CLICKED, XPOS, YPOS,
                        FormMouseEvent.BUTTON2, null);
        JGuiraffeTestHelper.checkEquals(event, e2, false);
        e2 =
                new FormMouseEvent(this, HANDLER, NAME,
                        FormMouseEvent.Type.MOUSE_CLICKED, XPOS + 1, YPOS,
                        FormMouseEvent.BUTTON2, EnumSet.of(Modifiers.ALT));
        JGuiraffeTestHelper.checkEquals(event, e2, false);
        JGuiraffeTestHelper.checkEquals(event, new FormEvent(this, HANDLER,
                NAME), false);
    }

    /**
     * Tests equals() if other objects are involved.
     */
    @Test
    public void testEqualsOtherObjects()
    {
        FormMouseEvent event =
                new FormMouseEvent(this, HANDLER, NAME,
                        FormMouseEvent.Type.MOUSE_CLICKED, XPOS, YPOS,
                        FormMouseEvent.BUTTON2, EnumSet.of(Modifiers.ALT,
                                Modifiers.SHIFT));
        JGuiraffeTestHelper.testTrivialEquals(event);
    }
}
