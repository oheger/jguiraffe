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
package net.sf.jguiraffe.gui.app;

import net.sf.jguiraffe.resources.Message;

/**
 * <p>
 * A class defining constants for default resources provided by the application
 * framework.
 * </p>
 * <p>
 * Some components of the <em>JGUIraffe</em> library can produce messages
 * visible to the end user. The messages are defined as resource IDs, so they
 * can be translated into different languages. This class defines constants for
 * these messages. They correspond to resource keys used by the default
 * application resource bundle shipped with the library.
 * </p>
 * <p>
 * Typically a concrete application can use this default messages. It is also
 * possible to override them with custom resource IDs. The message producing
 * components typically allow specifying custom resource IDs.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: ApplicationResources.java 205 2012-01-29 18:29:57Z oheger $
 */
public final class ApplicationResources
{
    /**
     * Constant for the name of the resource group used for the
     * application-related resources.
     */
    public static final String APPLICATION_RESOURCE_GROUP = "application";

    /**
     * Private constructor so that no instances can be created.
     */
    private ApplicationResources()
    {
    }

    /**
     * <p>
     * An enumeration class defining the keys of all resources contained within
     * the <em>application</em> default resource bundle.
     * </p>
     *
     * @author Oliver Heger
     * @version $Id: ApplicationResources.java 205 2012-01-29 18:29:57Z oheger $
     */
    public static enum Keys
    {
        /** The message of the application exit prompt dialog. */
        EXIT_PROMPT_MSG,

        /** The title of the application exit prompt dialog. */
        EXIT_PROMPT_TIT,

        /** The standard text for the exit action. */
        EXIT_ACTION_TEXT,

        /** The standard tool tip for the exit action. */
        EXIT_ACTION_TOOLTIP,

        /** The standard mnemonic for the exit action. */
        EXIT_ACTION_MNEMO
    }

    /**
     * A convenience method for generating the resource ID for the specified
     * enumeration literal. The literals cannot be passed directly to a resource
     * manager. Rather, their name has to be extracted. This is done by this
     * method.
     *
     * @param key the key (must not be <b>null</b>)
     * @return the corresponding resource ID
     * @throws IllegalArgumentException if the key is <b>null</b>
     */
    public static Object resourceID(Keys key)
    {
        if (key == null)
        {
            throw new IllegalArgumentException("Resource key must not be null!");
        }
        return key.name();
    }

    /**
     * Returns a {@code Message} object for the specified enumeration literal.
     * This object is initialized with the default application resource group
     * and the resource ID extracted from the literal.
     *
     * @param key the key (must not be <b>null</b>)
     * @return a {@code Message} object for this key
     * @throws IllegalArgumentException if the key is <b>null</b>
     */
    public static Message message(Keys key)
    {
        return new Message(APPLICATION_RESOURCE_GROUP, resourceID(key));
    }
}
