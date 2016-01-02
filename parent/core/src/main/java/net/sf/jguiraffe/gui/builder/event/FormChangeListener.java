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
package net.sf.jguiraffe.gui.builder.event;

/**
 * <p>
 * Definition of an interface for change listeners.
 * </p>
 * <p>
 * Objects that are interested in <code>{@link FormChangeEvent}</code>
 * notifications must implement this interface.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: FormChangeListener.java 205 2012-01-29 18:29:57Z oheger $
 */
public interface FormChangeListener extends FormEventListener
{
    /**
     * This method gets called when a change events occurs. Note that depending
     * on the affected component the event could have different meanings, e.g. a
     * text change event or a selection change event.
     *
     * @param e the event object
     */
    void elementChanged(FormChangeEvent e);
}
