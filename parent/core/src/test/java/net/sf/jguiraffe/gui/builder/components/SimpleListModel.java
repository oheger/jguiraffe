/*
 * Copyright 2006-2021 The JGUIraffe Team.
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
package net.sf.jguiraffe.gui.builder.components;

import net.sf.jguiraffe.gui.builder.components.model.ListModel;

/**
 * Test implementation of the ListModel interface. Returns different display
 * and value objects.
 */
public class SimpleListModel implements ListModel
{
    /** Constant for the prefix used for value objects. */
    public static final String VALUE = "value";

    /** Constant for the prefix used for display objects. */
    public static final String DISPLAY = "display";

    private final int size;

    public SimpleListModel(int sz)
    {
        size = sz;
    }

    public int size()
    {
        return size;
    }

    public Object getDisplayObject(int index)
    {
        return DISPLAY + index;
    }

    public Object getValueObject(int index)
    {
        return VALUE + index;
    }

    public Class<?> getType()
    {
        return String.class;
    }
}
