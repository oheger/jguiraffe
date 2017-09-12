/*
 * Copyright 2006-2017 The JGUIraffe Team.
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

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import org.apache.commons.lang.StringUtils;

/**
 * A simple class that represents a GUI container. It is used by the test
 * implementation of the ComponentManager interface whenever containers are
 * involved. Instead of GUI widgets this class stores string representations of
 * the components that have been added to it.
 *
 * @author Oliver Heger
 * @version $Id: Container.java 205 2012-01-29 18:29:57Z oheger $
 */
public class Container
{
    /** Stores the name of the container. */
    private String name;

    /** Stores further attributes as string.*/
    private String attributes;

    /** Stores the layout object. */
    private Object layout;

    /** Stores the contained components. */
    private Collection<Object> components;

    /**
     * Creates a new instance of <code>Container</code>.
     */
    public Container()
    {
        this(null);
    }

    /**
     * Creates a new instance of <code>Container</code> and sets the name.
     *
     * @param name the name
     */
    public Container(String name)
    {
        components = new LinkedList<Object>();
        setName(name);
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public Object getLayout()
    {
        return layout;
    }

    public String getAttributes()
    {
        return attributes;
    }
    public void setAttributes(String attributes)
    {
        this.attributes = attributes;
    }
    public void setLayout(Object layout)
    {
        this.layout = layout;
    }

    /**
     * Adds a component to this container.
     *
     * @param c the component
     * @param constr the constraints
     */
    public void addComponent(Object c, Object constr)
    {
        components.add(new Object[] { c, constr });
    }

    /**
     * Returns a string representation of this object. This is the container's
     * name, followed by the layout object and all of the contained components.
     */
    @Override
    public String toString()
    {
        StringBuilder buf = new StringBuilder(64);
        buf.append("Container: ").append(getName());
        StringBuilder attrs = new StringBuilder();
        if(!StringUtils.isEmpty(getAttributes()))
        {
            attrs.append(getAttributes());
        }
        if (getLayout() != null)
        {
            attrs.append(" LAYOUT = ").append(getLayout());
        }
        if(attrs.length() > 0)
        {
            buf.append(" [").append(attrs.toString()).append(" ]");
        }
        buf.append(" { ");
        boolean first = true;
        for (Iterator<Object> it = components.iterator(); it.hasNext();)
        {
            if (first)
            {
                first = false;
            }
            else
            {
                buf.append(", ");
            }
            Object[] dat = (Object[]) it.next();
            buf.append(dat[0]);
            if(dat[1] != null)
            {
                buf.append(" (").append(dat[1]).append(")");
            }
        }

        buf.append(" }");
        return buf.toString();
    }
}