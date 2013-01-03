/*
 * Copyright 2006-2012 The JGUIraffe Team.
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
package net.sf.jguiraffe.gui.platform.swing.layout;

import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Toolkit;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import net.sf.jguiraffe.gui.layout.UnitSizeHandler;

/**
 * <p>
 * Swing specific implementation of the <code>SizeHandler</code> interface.
 * </p>
 * <p>
 * Note: This implementation performs some caching to optimize performance, but
 * it is not synchronized. So it must be ensured that an instance is accessed by
 * a single thread only or that manual synchronization is performed.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: SwingSizeHandler.java 205 2012-01-29 18:29:57Z oheger $
 */
public class SwingSizeHandler implements UnitSizeHandler, Serializable
{
    /**
     * The serial version UID.
     */
    private static final long serialVersionUID = 20090731L;

    /**
     * Constant for the string used for determining the average character width.
     */
    private static final String WIDTH_STRING =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    /** Constant for the length of the width string. */
    private static final int WIDTH_STRING_LEN = WIDTH_STRING.length();

    /** Constant for the array index with the X font size. */
    private static final int IDX_FONTX = 0;

    /** Constant for the array index with the Y font size. */
    private static final int IDX_FONTY = 1;

    /**
     * A cache for storing the character sizes that have already been
     * determined.
     */
    private final Map<Font, double[]> sizeCache;

    /**
     * Creates a new instance of <code>SwingSizeHandler</code>.
     */
    public SwingSizeHandler()
    {
        sizeCache = new HashMap<Font, double[]>();
    }

    /**
     * Calculates the desired font size for the given component. The passed in
     * object is expected to be a <code>Component</code> instance.
     *
     * @param component the component (must not be <b>null</b>)
     * @param y the orientation flag
     * @return the font size
     * @throws IllegalArgumentException if the component is <b>null</b>
     */
    public double getFontSize(Object component, boolean y)
    {
        if (!(component instanceof Component))
        {
            throw new IllegalArgumentException(
                    "getFontSize() can only work with Component objects: "
                            + component);
        }

        Component comp = (Component) component;
        Font font = comp.getFont();

        double[] sizes = (double[]) sizeCache.get(font);
        if (sizes == null)
        {
            sizes = calculateFontSizes(font, comp);
            sizeCache.put(font, sizes);
        }

        return y ? sizes[IDX_FONTY] : sizes[IDX_FONTX];
    }

    /**
     * Returns the current screen resolution.
     *
     * @return the screen resolution
     */
    public int getScreenResolution()
    {
        return Toolkit.getDefaultToolkit().getScreenResolution();
    }

    /**
     * Helper method for calculating the relevant sizes for the given font.
     *
     * @param font the font
     * @param comp the associated component
     * @return an array with the relevant font sizes
     */
    double[] calculateFontSizes(Font font, Component comp)
    {
        double[] result = new double[2];
        FontMetrics fm = comp.getFontMetrics(font);
        result[IDX_FONTX] =
                (double) fm.stringWidth(WIDTH_STRING) / WIDTH_STRING_LEN;
        result[IDX_FONTY] = fm.getHeight();
        return result;
    }
}
