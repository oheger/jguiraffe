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
package net.sf.jguiraffe.gui.platform.swing.builder.components;

import javax.swing.Icon;
import javax.swing.JLabel;

import net.sf.jguiraffe.gui.builder.components.model.StaticTextData;
import net.sf.jguiraffe.gui.builder.components.model.StaticTextHandler;
import net.sf.jguiraffe.gui.builder.components.model.TextIconAlignment;
import net.sf.jguiraffe.gui.builder.components.tags.StaticTextDataImpl;

/**
 * <p>
 * A concrete <code>ComponentHandler</code> implementation for dealing with
 * static text elements.
 * </p>
 * <p>
 * This implementation maintains a label component whose properties can be
 * queried or altered using
 * {@link net.sf.jguiraffe.gui.builder.components.model.StaticTextData
 * StaticTextData} objects.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: SwingStaticTextComponentHandler.java 205 2012-01-29 18:29:57Z oheger $
 */
class SwingStaticTextComponentHandler extends
        SwingComponentHandler<StaticTextData> implements StaticTextHandler
{
    /**
     * Creates a new instance of <code>SwingStaticTextComponentHandler</code>
     * that wraps the passed in label component.
     *
     * @param label the component to be managed by this handler
     */
    public SwingStaticTextComponentHandler(JLabel label)
    {
        super(label);
    }

    /**
     * Returns the internally wrapped label component.
     *
     * @return the underlying label
     */
    public JLabel getLabel()
    {
        return (JLabel) getJComponent();
    }

    /**
     * Returns a data object for this component. This is a
     * <code>StaticTextData</code> object in this case.
     *
     * @return a data object for this component
     */
    public StaticTextData getData()
    {
        StaticTextData data = new StaticTextDataImpl();
        data.setAlignment(getAlignment());
        data.setIcon(getIcon());
        data.setText(getText());
        return data;
    }

    /**
     * Returns the data type of this component handler.
     *
     * @return the data type
     */
    public Class<?> getType()
    {
        return StaticTextData.class;
    }

    /**
     * Sets data for this component. The passed in data object can be of various
     * types.
     *
     * @param data the data object
     */
    public void setData(StaticTextData data)
    {
        if (data == null)
        {
            setText(null);
            setIcon(null);
            setAlignment(TextIconAlignment.LEFT);
        }
        else
        {
            setAlignment(data.getAlignment());
            setIcon(data.getIcon());
            setText(data.getText());
        }
    }

    /**
     * Returns the alignment of the managed label.
     *
     * @return the alignment
     */
    public TextIconAlignment getAlignment()
    {
        try
        {
            return SwingComponentManager.transformSwingAlign(getLabel()
                    .getHorizontalAlignment());
        }
        catch (IllegalArgumentException iex)
        {
            // obviously no standard alignment => set default
            return TextIconAlignment.LEFT;
        }
    }

    /**
     * Returns the label's icon.
     *
     * @return the icon
     */
    public Object getIcon()
    {
        return getLabel().getIcon();
    }

    /**
     * Returns the text of the managed label.
     *
     * @return the text
     */
    public String getText()
    {
        return getLabel().getText();
    }

    /**
     * Sets the alignment of the managed label.
     *
     * @param alignment the new alignment
     */
    public void setAlignment(TextIconAlignment alignment)
    {
        getLabel().setHorizontalAlignment(
                SwingComponentManager.transformAlign(alignment));
    }

    /**
     * Sets the icon of the managed label. The passed in object must implement
     * the <code>Icon</code> interface of Swing.
     *
     * @param icon the new icon
     */
    public void setIcon(Object icon)
    {
        getLabel().setIcon((Icon) icon);
    }

    /**
     * Sets the text of the managed label.
     *
     * @param s the new text
     */
    public void setText(String s)
    {
        getLabel().setText(s);
    }
}
