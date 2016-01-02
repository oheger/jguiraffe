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
package net.sf.jguiraffe.gui.layout;

/**
 * <p>
 * Definition of an interface for performing size calculations in a manner
 * independent of a certain component model.
 * </p>
 * <p>
 * This interface is used by the {@code Unit} class to perform unit to pixel
 * calculations. For some unit types access to certain internal details of the
 * affected components is needed, e.g. to the font for determining the font
 * size. This interface has the purpose of abstracting such direct accesses, so
 * that there can be multiple implementations for different GUI libraries (e.g.
 * Swing and SWT).
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: UnitSizeHandler.java 205 2012-01-29 18:29:57Z oheger $
 */
public interface UnitSizeHandler
{
    /**
     * Determines the font size of the given component for the specified
     * orientation. An implementation must extract the font of the passed in
     * component (after it has been correctly casted) and then determine the
     * average font size either in X- or Y-direction.
     *
     * @param component the component
     * @param y the orientation flag ( <b>true</b> for Y or vertical,
     *        <b>false</b> for X or horizontal
     * @return the font size
     */
    double getFontSize(Object component, boolean y);

    /**
     * Returns the screen resolution in dots per inch (dpi).
     *
     * @return the screen resolution in dpi
     */
    int getScreenResolution();
}
