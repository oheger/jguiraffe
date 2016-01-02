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
package net.sf.jguiraffe.examples.gui.layout;

import java.awt.EventQueue;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import net.sf.jguiraffe.gui.layout.BorderLayout;
import net.sf.jguiraffe.gui.layout.ButtonLayout;
import net.sf.jguiraffe.gui.layout.CellAlignment;
import net.sf.jguiraffe.gui.layout.CellConstraints;
import net.sf.jguiraffe.gui.layout.CellGroup;
import net.sf.jguiraffe.gui.layout.CellSize;
import net.sf.jguiraffe.gui.layout.NumberWithUnit;
import net.sf.jguiraffe.gui.layout.PercentData;
import net.sf.jguiraffe.gui.layout.PercentLayout;
import net.sf.jguiraffe.gui.layout.Unit;
import net.sf.jguiraffe.gui.platform.swing.layout.SwingPercentLayoutAdapter;

/**
 * An example of using PercentLayout with Swing.
 *
 * @author Oliver Heger
 * @version $Id: SwingPercentLayoutExample.java 205 2012-01-29 18:29:57Z oheger $
 */
@SuppressWarnings("serial")
public class SwingPercentLayoutExample extends JFrame
{

    public SwingPercentLayoutExample()
    {
        super();
        init();
        pack();
    }

    /**
     * Constructs the GUI of this frame.
     */
    protected void init()
    {
        // init frame
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("PercentLayout demo");

        // The layout of the content pane
        BorderLayout borderLayout = new BorderLayout();
        getContentPane().setLayout(new SwingPercentLayoutAdapter(borderLayout));

        // setup the main panel and its layout
        JPanel pnlMain = new JPanel();
        PercentLayout layout = new PercentLayout(
                "3dlu, end/preferred, 3dlu, full/preferred(1in)/50, 7dlu, end/preferred, 3dlu, full/preferred(1in)/50, 3dlu",
                "3dlu, preferred, 3dlu, preferred, 3dlu, preferred, 7dlu, preferred, 3dlu, preferred, 3dlu, full/preferred(1in)/100, 3dlu");
        layout.addColumnGroup(new CellGroup(1, 5));
        layout.addColumnGroup(new CellGroup(3, 7));
        pnlMain.setLayout(new SwingPercentLayoutAdapter(layout));

        // Builder for creating constraints objects
        CellConstraints.Builder cb = layout.getConstraintsBuilder();
        PercentData.Builder pcb = new PercentData.Builder();

        // Fill a header line
        pnlMain.add(new JLabel("General information"), pcb.xy(1, 1).spanX(7)
                .withColumnConstraints(cb.defaultColumn().create()).create());

        // Fill the first data line
        pnlMain.add(new JLabel("Name:"), pcb.pos(1, 3));
        pnlMain.add(new JTextField(), pcb.pos(3, 3));
        pnlMain.add(new JLabel("Firstname:"), pcb.pos(5, 3));
        pnlMain.add(new JTextField(), pcb.pos(7, 3));

        // Fill the second data line
        pnlMain.add(new JLabel("Street:"), pcb.pos(1, 5));
        pnlMain.add(new JTextField(), pcb.pos(3, 5));
        pnlMain.add(new JLabel("City:"), pcb.pos(5, 5));
        pnlMain.add(new JTextField(), pcb.pos(7, 5));

        // Fill another header line
        pnlMain.add(new JLabel("Specifics"), pcb.xy(1, 7).spanX(7)
                .withColumnConstraints(cb.defaultColumn().create()).create());

        // Fill third data line
        pnlMain.add(new JLabel("Email:"), pcb.pos(1, 9));
        pnlMain.add(new JTextField(), pcb.xy(3, 9).spanX(5).create());

        // Fill fourth data line
        pnlMain.add(new JLabel("Remarks:"), pcb.xy(1, 11).withRowConstraints(
                cb.withCellAlignment(CellAlignment.START).withCellSize(
                        CellSize.PREFERRED).create()).create());
        pnlMain.add(new JScrollPane(new JTextArea()), pcb.xy(3, 11).spanX(5)
                .create());

        // The button bar
        JPanel pnlButtons = new JPanel();
        ButtonLayout buttonLayout = new ButtonLayout();
        buttonLayout.setGap(new NumberWithUnit(4, Unit.DLU));
        buttonLayout.setLeftMargin(new NumberWithUnit(3, Unit.DLU));
        buttonLayout.setRightMargin(new NumberWithUnit(3, Unit.DLU));
        pnlButtons.setLayout(new SwingPercentLayoutAdapter(buttonLayout));
        pnlButtons.add(new JButton("OK"));
        pnlButtons.add(new JButton("Cancel"));
        pnlButtons.add(new JButton("Help"));

        getContentPane().add(pnlMain, BorderLayout.CENTER);
        getContentPane().add(pnlButtons, BorderLayout.SOUTH);
    }

    public static void main(String[] args)
    {
        final SwingPercentLayoutExample exFrame = new SwingPercentLayoutExample();
        EventQueue.invokeLater(new Runnable()
        {
            public void run()
            {
                exFrame.setVisible(true);
            }
        });
    }
}
