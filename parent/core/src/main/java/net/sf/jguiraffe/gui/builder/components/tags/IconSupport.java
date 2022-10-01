/*
 * Copyright 2006-2022 The JGUIraffe Team.
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

/**
 * <p>
 * Definition of an interface for GUI components that support icons.
 * </p>
 * <p>
 * This interface defines a single setter method for an icon object. This method
 * is intended to be called by an <code>IconTag</code>, which loads an icon
 * and sets it on the component it is nested inside.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: IconSupport.java 205 2012-01-29 18:29:57Z oheger $
 */
public interface IconSupport
{
    /**
     * Allows to set an icon on this component.
     *
     * @param icon the icon
     */
    void setIcon(Object icon);
}
