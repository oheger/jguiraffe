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

import net.sf.jguiraffe.gui.builder.components.model.ListModel;

/**
 * A test implementation of the ListModel interface. This implementation is
 * initialized with the size of the list model. The accessor methods for list
 * elements return constants with an index.
 *
 * @author Oliver Heger
 * @version $Id: ListModelImpl.java 205 2012-01-29 18:29:57Z oheger $
 */
class ListModelImpl implements ListModel
{
    /** Constant for the prefix of a display element.*/
    static final String DISPLAY_PREFIX = "Display";

    /** Constant for the prefix of a value element.*/
    static final String VALUE_PREFIX = "Value";

    private int count;

    public ListModelImpl(int cnt)
    {
        count = cnt;
    }

    public int size()
    {
        return count;
    }

    public Object getDisplayObject(int index)
    {
        return DISPLAY_PREFIX + index;
    }

    public Object getValueObject(int index)
    {
        return VALUE_PREFIX + index;
    }

    public Class<?> getType()
    {
        return String.class;
    }
}