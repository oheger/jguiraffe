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
package net.sf.jguiraffe.gui.platform.swing.builder.components;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.awt.Color;
import java.awt.Font;
import java.util.Locale;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import net.sf.jguiraffe.gui.app.Application;
import net.sf.jguiraffe.gui.app.ApplicationBuilderData;
import net.sf.jguiraffe.gui.app.ApplicationContext;
import net.sf.jguiraffe.gui.app.ApplicationException;
import net.sf.jguiraffe.gui.builder.BuilderException;
import net.sf.jguiraffe.gui.builder.components.ComponentBuilderData;
import net.sf.jguiraffe.gui.forms.ComponentHandler;
import net.sf.jguiraffe.gui.layout.BorderLayout;
import net.sf.jguiraffe.gui.layout.ButtonLayout;
import net.sf.jguiraffe.gui.layout.NumberWithUnit;
import net.sf.jguiraffe.gui.layout.PercentLayout;
import net.sf.jguiraffe.gui.layout.Unit;
import net.sf.jguiraffe.gui.platform.swing.layout.SwingPercentLayoutAdapter;
import net.sf.jguiraffe.locators.ClassPathLocator;

import org.junit.Before;
import org.junit.Test;

/**
 * Tests creating Swing components from a jelly script.
 *
 * @author Oliver Heger
 * @version $Id: TestSwingComponentManagerScript.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestSwingComponentManagerScript
{
    /** Constant for the name of the test script. */
    private static final String SCRIPT = "jelly_scripts/manager.jelly";

    /** The root container for the builder. */
    private JPanel root;

    @Before
    public void setUp() throws Exception
    {
        root = new JPanel();
    }

    /**
     * Executes the test builder script.
     *
     * @return the builder data object
     * @throws ApplicationException if the application cannot be started
     * @throws BuilderException if an error occurs
     */
    private ApplicationBuilderData execute() throws ApplicationException,
            BuilderException
    {
        Application app = new Application();
        app.setConfigResourceName("testappconfigfactorymin.xml");
        Application.startup(app, new String[0]);
        ApplicationContext appCtx = app.getApplicationContext();
        appCtx.setLocale(Locale.ENGLISH);
        ApplicationBuilderData builderData = appCtx.initBuilderData();
        builderData.setDefaultResourceGroup("testformbuilderresources");
        Font font = new Font("Monospaced", Font.ITALIC, 12);
        builderData.addProperty("testFont", font);
        appCtx.newBuilder().buildContainer(
                ClassPathLocator.getInstance(SCRIPT), builderData, root);
        return builderData;
    }

    /**
     * Tests processing the script.
     */
    @Test
    public void testCreateComponents() throws Exception
    {
        ApplicationBuilderData appData = execute();
        assertEquals("Wrong number of components", 2, root.getComponentCount());
        assertTrue("Wrong component 0", root.getComponent(0) instanceof JPanel);

        JPanel pnl = (JPanel) root.getComponent(0);
        assertEquals("Wrong number of components in panel 0", 2,
                pnl.getComponentCount());
        assertTrue("Wrong component type 1",
                pnl.getComponent(0) instanceof JLabel);
        JLabel label = (JLabel) pnl.getComponent(0);
        assertEquals("Wrong font name", "Monospaced", label.getFont()
                .getFamily());
        assertEquals("Wrong font size", 12, label.getFont().getSize());
        assertTrue("Not italic", label.getFont().isItalic());
        assertEquals("Wrong color", Color.BLUE, label.getForeground());
        assertEquals("Wrong label name", "helloLabel", label.getName());
        assertEquals("Wrong label tool tip", "test label tooltip",
                label.getToolTipText());

        ComponentBuilderData builderData =
                appData.getBuilderContext().getBean(ComponentBuilderData.class);
        assertNotNull("TestComponent not found",
                builderData.getComponent("TestComponent"));
        assertEquals("Wrong test component",
                builderData.getComponent("TestComponent"), pnl.getComponent(1));
        assertTrue("Wrong component type 1",
                pnl.getComponent(1) instanceof JTextField);
        JTextField txt = (JTextField) pnl.getComponent(1);
        assertEquals("Wrong background color", Color.WHITE, txt.getBackground());
        assertEquals("Wrong text name", "TestComponent", txt.getName());
        assertEquals("Wrong tool tip", "Tool tip for the text area",
                txt.getToolTipText());
        ComponentHandler<?> ch =
                builderData.getComponentHandler("TestComponent");
        assertEquals("Wrong component for handler", txt, ch.getComponent());
        assertTrue("Component not enabled", txt.isEnabled());
        assertTrue("Handler not enabled", ch.isEnabled());
        ch.setEnabled(false);
        assertFalse("Cannot disable component", txt.isEnabled());
        ch.setEnabled(true);
        assertTrue("Cannot enable component", txt.isEnabled());
    }

    /**
     * Tests whether the correct layout objects could be created.
     */
    @Test
    public void testCreateLayouts() throws Exception
    {
        execute();
        JPanel pnl = (JPanel) root.getComponent(0);

        assertTrue("No layout adapter for root panel",
                root.getLayout() instanceof SwingPercentLayoutAdapter);
        assertTrue("No BorderLayout",
                ((SwingPercentLayoutAdapter) root.getLayout())
                        .getPercentLayout() instanceof BorderLayout);
        BorderLayout borderLayout =
                (BorderLayout) ((SwingPercentLayoutAdapter) root.getLayout())
                        .getPercentLayout();
        assertEquals("Wrong north gap", NumberWithUnit.ZERO,
                borderLayout.getNorthGap());
        assertEquals("Wrong south gap", new NumberWithUnit(1, Unit.DLU),
                borderLayout.getSouthGap());

        assertTrue("No layout adapter for panel 1",
                pnl.getLayout() instanceof SwingPercentLayoutAdapter);
        PercentLayout layout =
                (PercentLayout) ((SwingPercentLayoutAdapter) pnl.getLayout())
                        .getPercentLayout();
        assertTrue("Got no components", layout.getPlatformAdapter()
                .getComponentCount() > 0);
        assertEquals("Wrong column count", 5, layout.getColumnCount());
        assertEquals("Wrong row count", 3, layout.getRowCount());
        assertEquals("Wrong size of column groups", 0, layout.getColumnGroups()
                .size());
        assertEquals("Wrong size of row groups", 0, layout.getRowGroups()
                .size());

        assertTrue("Wrong type of component 1",
                root.getComponent(1) instanceof JPanel);
        pnl = (JPanel) root.getComponent(1);
        assertEquals("Wrong number of components in panel 2", 2,
                pnl.getComponentCount());
        assertTrue("No layout adapter for panel 2",
                pnl.getLayout() instanceof SwingPercentLayoutAdapter);
        assertTrue("No ButtonLayout",
                ((SwingPercentLayoutAdapter) pnl.getLayout())
                        .getPercentLayout() instanceof ButtonLayout);
        ButtonLayout btnLayout =
                (ButtonLayout) ((SwingPercentLayoutAdapter) pnl.getLayout())
                        .getPercentLayout();
        assertTrue("Got no buttons", btnLayout.getPlatformAdapter()
                .getComponentCount() > 0);
        assertEquals("Wrong gap", new NumberWithUnit(3.5, Unit.DLU),
                btnLayout.getGap());
    }
}
