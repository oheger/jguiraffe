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

import java.text.MessageFormat;

import net.sf.jguiraffe.gui.builder.components.FormBuilderRuntimeException;

/**
 * Test class for TabbedPaneTag and TabTag.
 *
 * @author Oliver Heger
 * @version $Id: TestTabbedPaneTag.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestTabbedPaneTag extends AbstractTagTest
{
    /** Constant for the test script. */
    private static final String SCRIPT = "tabbedpane";

    /** Constant for the name of the tabbed pane. */
    private static final String TABBED_NAME = "MyTab";

    /** Constant for the start of the results. */
    private static final String RES_PREFIX = "Container: ROOT { "
            + "TABBEDPANE [ NAME = " + TABBED_NAME + " PLACEMENT = ";

    /** Constant for the end of the results. */
    private static final String RES_SUFFIX = " ] }";

    /** Constant for the text representing a label component in a tab. */
    private static final String LABLE_COMP = "LABEL [ TEXT = Tab {0} ALIGN = LEFT ]";

    /** Constant for the tabs' string representation. */
    private static final String RES_TABS = " TABS { TAB [ TITLE = First Tab "
            + "TOOLTIP = Tip1 MNEMO = F COMP = " + label(1) + " ] TAB [ TITLE"
            + " = A Tab TOOLTIP = A Tip MNEMO = T COMP = " + label(2)
            + " ] TAB [ ICON = " + "ICON [ " + iconLocatorString() + " ] COMP = "
            + label(3) + " ] } ";

    /** Constant for the test standard builder. */
    private static final String BUILDER_STD = "TEST_STD";

    /** Constant for the test placement builder. */
    private static final String BUILDER_PLACEMENT = "TEST_PLACEMENT";

    /** Constant for the test auto names builder. */
    private static final String BUILDER_AUTONAMES = "TEST_AUTONAME";

    /** Constant for the error placement builder. */
    private static final String BUILDER_ERR_PLACEMENT = "ERR_PLACEMENT";

    /** Constant for the error layout builder. */
    private static final String BUILDER_ERR_LAYOUT = "ERR_LAYOUT";

    /** Constant for the error other components builder. */
    private static final String BUILDER_ERR_COMPS = "ERR_OTHERCOMP";

    /** Constant for the error nested builder. */
    private static final String BUILDER_ERR_NESTED = "ERR_NESTED";

    /** Constant for the error component count builder. */
    private static final String BUILDER_ERR_COMPCNT = "ERR_MULTICOMP";

    /** Constant for the error undefined builder. */
    private static final String BUILDER_ERR_UNDEF = "ERR_UNDEF";

    /**
     * Tests creating a normal tabbed pane with some tabs.
     */
    public void testCreateTabbedPane() throws Exception
    {
        builderData.setBuilderName(BUILDER_STD);
        checkScript(SCRIPT, RES_PREFIX + "TOP" + RES_TABS + RES_SUFFIX);
        assertNotNull("Tabbed pane is not stored", builderData
                .getComponentHandler(TABBED_NAME));
        assertNull("Tabbed pane was added to form", builderData.getForm()
                .getField(TABBED_NAME));
    }

    /**
     * Tests creating a tabbed pane with a placement declaration.
     */
    public void testCreateTabbedPanePlacement() throws Exception
    {
        builderData.setBuilderName(BUILDER_PLACEMENT);
        checkScript(SCRIPT, RES_PREFIX + "RIGHT" + RES_TABS + RES_SUFFIX);
        assertNotNull("Tabbed pane is not stored", builderData
                .getComponentHandler(TABBED_NAME));
        assertNotNull("Tabbed pane was not added to form", builderData
                .getForm().getField(TABBED_NAME));
    }

    /**
     * Tests if correct automatic names are generated for tabbed panes.
     */
    public void testCreateTabbedPaneWithAutoNames() throws Exception
    {
        builderData.setBuilderName(BUILDER_AUTONAMES);
        executeScript(SCRIPT);
        assertNotNull("Cannot find tab 1", builderData
                .getComponentHandler(TabbedPaneTag.AUTO_NAME_PREFIX + "1"));
        assertNotNull("Cannot find tab 2", builderData
                .getComponentHandler(TabbedPaneTag.AUTO_NAME_PREFIX + "2"));
    }

    /**
     * Tests creating a tabbed pane with an invalid placement declaration. This
     * should cause an exception.
     */
    public void testErrorPlacement() throws Exception
    {
        errorScript(SCRIPT, BUILDER_ERR_PLACEMENT,
                "Invalid placement was not detected!");
    }

    /**
     * Tests a tabbed pane with an embedded layout declaration. On this level
     * layouts are not supported.
     */
    public void testErrorLayout() throws Exception
    {
        errorScript(SCRIPT, BUILDER_ERR_LAYOUT,
                "Layout declaration in tabbed pane was not detected!");
    }

    /**
     * Tests a tabbed pane, which contains further components in addition to
     * tabs. This is not allowed.
     */
    public void testErrorOtherComponents() throws Exception
    {
        errorScript(SCRIPT, BUILDER_ERR_COMPS, "Other components not detected!");
    }

    /**
     * Tests a tab tag that is not nested inside a tabbed pane tag.
     */
    public void testErrorNested() throws Exception
    {
        errorScript(SCRIPT, BUILDER_ERR_NESTED,
                "Could process a tab not nested inside a tabbed pane!");
    }

    /**
     * Tests a tab with more than one component in it. This is not allowed.
     */
    public void testErrorComponentCount() throws Exception
    {
        errorScript(SCRIPT, BUILDER_ERR_COMPCNT,
                "Could process multiple components in a tab!");
    }

    /**
     * Tests a tab with either a title nor an icon. Such completely undefined
     * tabs are not allowed.
     */
    public void testErrorUndefinedTab() throws Exception
    {
        errorScript(SCRIPT, BUILDER_ERR_UNDEF,
                "Undefined tab was not detected!");
    }

    /**
     * Tries to add a component of an invalid type. This should cause an
     * exception.
     */
    public void testAddComponentInvalidType()
    {
        TabbedPaneTag tag = new TabbedPaneTag();
        try
        {
            tag.addComponent(this, null);
            fail("Could add invalid component!");
        }
        catch (FormBuilderRuntimeException frex)
        {
            // ok
        }
    }

    /**
     * Returns the string representing a label for the specified tab index.
     *
     * @param tabIdx the index of the tab
     * @return the string for the label on that tab
     */
    private static String label(int tabIdx)
    {
        Object[] args = new Object[1];
        args[0] = new Integer(tabIdx);
        return MessageFormat.format(LABLE_COMP, args);
    }
}
