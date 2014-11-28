/*
 * Copyright 2006-2014 The JGUIraffe Team.
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
package net.sf.jguiraffe.gui.builder.components.model;

import net.sf.jguiraffe.gui.forms.ComponentHandler;

/**
 * <p>
 * Definition of an interface for a handler that represents a static text
 * component.
 * </p>
 * <p>
 * Though static text elements are no input elements in the common sense it is
 * possible to update some of their properties. For this purpose this interface
 * provides some convenience methods, so that specific properties can be set and
 * it is unnecessary to call <code>setData()</code> all the time.
 * </p>
 * <p>
 * The <code>setData()</code> and <code>getData()</code> methods of this handler
 * interface operate on <code>{@link StaticTextData}</code> objects. By calling
 * <code>setData()</code> with a <code>StaticTextData</code> object all
 * properties of the managed static text object are updated. If <b>null</b> is
 * passed in, all properties (text and icon) and cleared.
 * </p>
 * <p>
 * The <code>getData()</code> method will always return a
 * <code>{@link StaticTextData}</code> object representing the current state
 * of the static text element. Note that the returned object is disconnected
 * from the static text component, i.e. a manipulation of its properties will
 * not affect the static text component directly. You have to call
 * <code>setData()</code> explicitly for setting the changed values.
 * </p>
 * <p>
 * This interface simply combines the <code>{@link StaticTextData}</code>
 * interface with the <code>{@link ComponentHandler}</code> interface.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: StaticTextHandler.java 205 2012-01-29 18:29:57Z oheger $
 */
public interface StaticTextHandler extends ComponentHandler<StaticTextData>, StaticTextData
{
}
