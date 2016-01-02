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

import net.sf.jguiraffe.gui.forms.ComponentHandler;

import org.apache.commons.lang.StringUtils;

/**
 * A test implementation of the <code>PlatformEventManager</code> interface.
 * This implementation simply transforms call to the
 * <code>registerListener()</code> method to plain text that can be checked by
 * unit tests.
 *
 * @author Oliver Heger
 * @version $Id: PlatformEventManagerImpl.java 205 2012-01-29 18:29:57Z oheger $
 */
public class PlatformEventManagerImpl implements PlatformEventManager
{
    /** Constant for the type separator. */
    private static final String SEPARATOR1 = " -> ";

    /** Constant for the registration data separator. */
    private static final String SEPARATOR2 = ", ";

    /** Stores the plain text. */
    private final StringBuilder text = new StringBuilder();

    public void registerListener(String name, ComponentHandler<?> handler,
            FormEventManager eventManager, FormListenerType type)
    {
        if (text.length() > 0)
        {
            text.append(SEPARATOR2);
        }
        text.append(name).append(SEPARATOR1).append(type.name());
    }

    public void unregisterListener(String name, ComponentHandler<?> handler,
            FormEventManager eventManager, FormListenerType type)
    {
        String regText = fetchRegistrationData(name, type);
        int pos = text.indexOf(regText);
        if (pos < 0)
        {
            throw new RuntimeException("No registration data found: " + regText);
        }
        text.delete(pos, pos + regText.length());
        if (pos < text.length() - 1)
        {
            // remove separator
            text.delete(pos, pos + SEPARATOR2.length());
        }
        if (StringUtils.countMatches(text.toString(), SEPARATOR2) == 1
                && StringUtils.countMatches(text.toString(), SEPARATOR1) == 1)
        {
            pos = text.indexOf(SEPARATOR2);
            text.delete(pos, pos + SEPARATOR2.length());
        }
    }

    /**
     * Returns information about registered listeners as plain text.
     *
     * @return text information about registered listeners
     */
    public String getRegistrationData()
    {
        return text.toString();
    }

    /**
     * Counts the number of occurrences of the given combination of component
     * name and listener type. This is useful to find out how often a component
     * has been registered.
     *
     * @param name the name of the component
     * @param type the listener type; can be <b>null</b>, then all types are
     *        counted
     * @return the number of occurrences of the given combination
     */
    public int getNumberOf(String name, FormListenerType type)
    {
        String data = getRegistrationData();
        int pos1 = 0;
        int count = 0;

        while (pos1 < data.length())
        {
            int pos2 = data.indexOf(SEPARATOR1, pos1);
            int posEnd = data.indexOf(SEPARATOR2, pos2);
            if (posEnd < 0)
            {
                posEnd = data.length();
            }
            if (data.substring(pos1, pos2).equals(name))
            {
                if (type == null
                        || data.substring(pos2 + SEPARATOR1.length(), posEnd)
                                .equals(type.name()))
                {
                    count++;
                }
            }
            pos1 = posEnd + SEPARATOR2.length();
        }

        return count;
    }

    /**
     * Creates registration data for the specified component name and listener
     * type and adds it to the given buffer.
     *
     * @param buf the buffer
     * @param name the name of the component
     * @param type the listener type
     */
    private void appendRegistrationString(StringBuilder buf, String name,
            FormListenerType type)
    {
        buf.append(name).append(SEPARATOR1).append(type.name());
    }

    /**
     * Creates the registration data string for the specified component name and
     * event listener type.
     *
     * @param name the name of the component
     * @param type the event listener type
     * @return a string representation of the corresponding registration data
     */
    private String fetchRegistrationData(String name, FormListenerType type)
    {
        StringBuilder buf = new StringBuilder();
        appendRegistrationString(buf, name, type);
        return buf.toString();
    }
}
