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
package net.sf.jguiraffe.transform;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;

import net.sf.jguiraffe.resources.Message;
import net.sf.jguiraffe.resources.ResourceManager;

/**
 * <p>
 * A default implementation of the {@code ValidationMessageHandler} interface.
 * </p>
 * <p>
 * This class performs the following steps for obtaining a
 * {@link ValidationMessage} object for a given key:
 * <ul>
 * <li>It checks whether the passed in {@link TransformerContext} contains a
 * property with the name of the given key. If this is the case, its value will
 * be used as error message. This makes it possible to override validation
 * messages directly in the builder script when the validator is defined.</li>
 * <li>Otherwise the error message will be loaded from the application's
 * resources using the key as resource ID. For the resource group the following
 * options exist:
 * <ul>
 * <li>With the {@link #setAlternativeResourceGroups(String)} method a list of
 * resource groups can be set, which will be searched for resources with message
 * keys. So an application can define a custom resource group containing the
 * error messages it wants to override and specify it here.</li>
 * <li>If the resource for the message key cannot be found in one of the
 * alternative resource groups, the resource group returned by the
 * {@link #getDefaultResourceGroup()} method will be used. If not otherwise set,
 * the name defined by the {@link #DEFAULT_RESOURCE_GROUP_NAME} constant is
 * used. This is a resource group shipped with the framework, which contains
 * default validation messages in a couple of supported languages.</li>
 * </ul>
 * </li>
 * </ul>
 * </p>
 * <p>
 * {@code DefaultValidationMessageHandler} also determines the
 * {@link ValidationMessageLevel} of the messages it returns. This is done by
 * evaluating the {@code errorLevel} property of the passed in
 * {@link TransformerContext}. If this property is set, its value is interpreted
 * as an enumeration literal defined by the {@link ValidationMessageLevel}
 * class. If the property is undefined, the default message level
 * {@link ValidationMessageLevel#ERROR} is used. The advantage behind this
 * concept is that the message level is completely transparent for validators.
 * So they can produce messages of any level, provided that the
 * {@link TransformerContext} is properly configured.
 * </p>
 * <p>
 * The {@link ValidationMessage} objects returned by this object are
 * thread-safe; they can be concurrently accessed without requiring further
 * synchronization.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: DefaultValidationMessageHandler.java 205 2012-01-29 18:29:57Z oheger $
 */
public class DefaultValidationMessageHandler implements
        ValidationMessageHandler
{
    /**
     * Constant for the default resource group with validation messages. This
     * group name will be used if no specific default resource group has been
     * set.
     */
    public static final String DEFAULT_RESOURCE_GROUP_NAME = "validators";

    /**
     * Constant for the name of the property that defines the
     * {@link ValidationMessageLevel} of the validation messages produced by
     * this object. This property can be set in the properties of the
     * {@link TransformerContext}.
     */
    public static final String PROP_MESSAGE_LEVEL = "errorLevel";

    /** Constant for the regex for splitting the alternative groups. */
    private static final String SPLIT_REGEX = "\\s*,\\s*";

    /**
     * Constant for the format key for generating the string for validation
     * messages.
     */
    private static final String VM_FORMAT = "%s: \"%s\"";

    /** Stores the alternative resource groups to be searched for messages. */
    private String alternativeResourceGroups;

    /** Stores the default resource group for validation messages. */
    private String defaultResourceGroup;

    /** Stores an array with the alternative resource groups. */
    private String[] alternativeGroupsList;

    /**
     * Creates a new instance of <code>DefaultValidationMessageHandler</code>.
     */
    public DefaultValidationMessageHandler()
    {
        setAlternativeResourceGroups(null);
    }

    /**
     * Returns the name of the default resource group.
     *
     * @return the name of the default resource group
     */
    public String getDefaultResourceGroup()
    {
        return (defaultResourceGroup != null) ? defaultResourceGroup
                : DEFAULT_RESOURCE_GROUP_NAME;
    }

    /**
     * Sets the name of the default resource group. This group will be searched
     * for resources for validation messages if the search in the alternative
     * resource groups yielded no results. If no default resource group was set,
     * the group defined by the
     * <code>{@link #DEFAULT_RESOURCE_GROUP_NAME}</code> constant is used.
     *
     * @param defaultResourceGroup the new default resource group
     * @see #setAlternativeResourceGroups(String)
     */
    public void setDefaultResourceGroup(String defaultResourceGroup)
    {
        this.defaultResourceGroup = defaultResourceGroup;
    }

    /**
     * Returns the alternative resource groups.
     *
     * @return the alternative resource groups
     */
    public String getAlternativeResourceGroups()
    {
        return alternativeResourceGroups;
    }

    /**
     * Sets the alternative resource groups. This method expects a comma
     * separated string with names of resource groups to be searched for
     * validation messages. If, for instance, the string
     * <code>&quot;specialMessages, extendedMessages&quot;</code> is passed in,
     * the text for a validation message will be searched first in the group
     * &quot;specialMessages&quot;, then in the group
     * &quot;extendedMessages&quot;, and finally in the default resource group.
     * It is possible to set the alternative resource groups to <b>null</b>.
     * Then only the default resource group will be searched.
     *
     * @param alternativeResourceGroups a comma separated list of alternative
     *        resource groups
     */
    public void setAlternativeResourceGroups(String alternativeResourceGroups)
    {
        this.alternativeResourceGroups = alternativeResourceGroups;

        if (alternativeResourceGroups != null
                && alternativeResourceGroups.length() > 0)
        {
            alternativeGroupsList = alternativeResourceGroups
                    .split(SPLIT_REGEX);
        }
        else
        {
            alternativeGroupsList = new String[0];
        }
    }

    /**
     * Returns a validation message. This method implements the steps described
     * in the class comment for obtaining a validation message for the given
     * key.
     *
     * @param context the transformer context (must not be <b>null</b>)
     * @param key the key of the message (must not be <b>null</b>)
     * @param params additional parameters to be integrated into the message
     * @return the validation message object for this key
     * @throws IllegalArgumentException if the transformer context or the key
     *         are <b>null</b>
     */
    public ValidationMessage getValidationMessage(TransformerContext context,
            String key, Object... params)
    {
        if (context == null)
        {
            throw new IllegalArgumentException(
                    "TransformerContext must not be null!");
        }
        if (key == null)
        {
            throw new IllegalArgumentException("Message key must not be null!");
        }

        Object msg = context.properties().get(key);
        if (msg != null)
        {
            return new ValidationMessagePlain(key, getMessageLevel(context),
                    String.valueOf(msg), params);
        }
        else
        {
            return new ValidationMessageResource(key, getMessageLevel(context),
                    context, alternativeGroupsList, getDefaultResourceGroup(),
                    params);
        }
    }

    /**
     * Determines the {@code ValidationMessageLevel} for the validation messages
     * produced by this object. This implementation tests whether in the
     * {@code TransformerContext} the {@link #PROP_MESSAGE_LEVEL} property is
     * set. If so, its value is evaluated. Otherwise the default level
     * {@link ValidationMessageLevel#ERROR} is returned.
     *
     * @param context the {@code TransformerContext}
     * @return the {@code ValidationMessageLevel}
     */
    protected ValidationMessageLevel getMessageLevel(TransformerContext context)
    {
        Map<String, Object> props = context.properties();

        Object level = props.get(PROP_MESSAGE_LEVEL);
        if (level == null)
        {
            return ValidationMessageLevel.ERROR;
        }
        else
        {
            if (level instanceof ValidationMessageLevel)
            {
                return (ValidationMessageLevel) level;
            }
            else
            {
                return ValidationMessageLevel.valueOf(level.toString()
                        .toUpperCase(Locale.ENGLISH));
            }
        }
    }

    /**
     * An abstract base class for implementations of the {@code
     * ValidationMessage} interface provided by this validation message handler.
     * This class implements some common functionality.
     */
    private abstract static class ValidationMessageImpl implements
            ValidationMessage
    {
        /** Stores the key of the message. */
        private final String key;

        /** Stores the validation level. */
        private final ValidationMessageLevel level;

        /**
         * Creates a new instance of {@code ValidationMessageImpl} and
         * initializes it with the key for the message and the level.
         *
         * @param msgKey the key
         * @param msgLevel the {@code ValidationMessageLevel}
         */
        protected ValidationMessageImpl(String msgKey,
                ValidationMessageLevel msgLevel)
        {
            key = msgKey;
            level = msgLevel;
        }

        /**
         * Returns the key of this validation message.
         *
         * @return the message key
         */
        public String getKey()
        {
            return key;
        }

        /**
         * Returns the validation level of this message.
         *
         * @return the {@code ValidationMessageLevel}
         */
        public ValidationMessageLevel getLevel()
        {
            return level;
        }

        /**
         * Checks equality with another object. Two {@code
         * ValidationMessageImpl} objects are considered equal if and only if
         * their message keys are equal and they have the same error level.
         * (From the point of view of the validation framework only the key of a
         * message is relevant, while the actual content is of less importance.)
         *
         * @param obj the object to compare to
         * @return a flag of the objects are equal
         */
        @Override
        public boolean equals(Object obj)
        {
            if (this == obj)
            {
                return true;
            }
            if (!(obj instanceof ValidationMessageImpl))
            {
                return false;
            }

            assert getKey() != null : "Keys must not be null!";
            ValidationMessageImpl c = (ValidationMessageImpl) obj;
            return getKey().equals(c.getKey()) && getLevel() == c.getLevel();
        }

        /**
         * Returns a hash code for this object.
         *
         * @return a hash code
         */
        @Override
        public int hashCode()
        {
            final int seed = 17;
            final int factor = 31;

            int result = seed;
            result = factor * result + getKey().hashCode();
            result = factor * result + getLevel().hashCode();
            return result;
        }

        /**
         * Returns a string representation for this message object. The string
         * contains the key and the message.
         *
         * @return a string for this object
         */
        @Override
        public String toString()
        {
            return String.format(VM_FORMAT, getKey(), getMessage());
        }
    }

    /**
     * A specialized implementation of <code>ValidationMessage</code> that is
     * directly initialized with the message to display. This implementation is
     * pretty simple: it just returns the message it was passed.
     */
    private static class ValidationMessagePlain extends ValidationMessageImpl
    {
        /** Stores the message. */
        private final String message;

        /**
         * Creates a new instance of {@code ValidationMessagePlain} and
         * initializes it with the message key, the level, and the content and
         * optional parameters.
         *
         * @param msgKey the key
         * @param msgLevel the message level
         * @param msg the text of the message
         * @param params the parameters for this message
         */
        public ValidationMessagePlain(String msgKey,
                ValidationMessageLevel msgLevel, String msg, Object... params)
        {
            super(msgKey, msgLevel);

            if (params.length > 0)
            {
                message = MessageFormat.format(msg, params);
            }
            else
            {
                message = msg;
            }
        }

        /**
         * Returns the message text.
         *
         * @return the message
         */
        public String getMessage()
        {
            return message;
        }
    }

    /**
     * A specialized implementation of the {@code ValidationMessage} interface
     * that obtains the message text from the application's resources. When
     * created, an instance is initialized with a set of resource groups, in
     * which to look for the message text. On first access, these groups are
     * searched in the given order. The first hit is used as message text.
     */
    private static class ValidationMessageResource extends
            ValidationMessageImpl
    {
        /** An array with the resource groups to search for the message. */
        private final String[] resourceGroups;

        /** Stores an array with parameters for the message. */
        private final Object[] parameters;

        /** Stores the resource manager. */
        private final ResourceManager resourceManager;

        /** Stores the locale. */
        private final Locale locale;

        /** Stores the final message. */
        private String message;

        /**
         * Creates a new instance of {@code ValidationMessageResource} and
         * initializes it.
         *
         * @param key the key of the message
         * @param msgLevel the message level
         * @param ctx the transformer context
         * @param resGrps an array with the names of the alternative resource
         *        groups
         * @param defGrp the default resource group
         * @param params the (optional) parameters for the message
         */
        public ValidationMessageResource(String key,
                ValidationMessageLevel msgLevel, TransformerContext ctx,
                String[] resGrps, String defGrp, Object... params)
        {
            super(key, msgLevel);
            resourceManager = ctx.getResourceManager();
            locale = ctx.getLocale();
            parameters = params;
            resourceGroups = initResourceGroups(resGrps, defGrp);
        }

        /**
         * Returns the message. If this is the first access, the message will be
         * resolved from the resources (which may cause an exception).
         *
         * @return the message
         * @throws MissingResourceException if the message cannot be resolved
         */
        public synchronized String getMessage()
        {
            if (message == null)
            {
                message = resolveMessage();
            }
            return message;
        }

        /**
         * Initializes the array with the resource groups to search. The
         * alternative resource groups and the default group are combined into
         * one array.
         *
         * @param resGrps an array with alternative resource groups
         * @param defGrp the default resource group
         * @return the combined array with all resource groups
         */
        private String[] initResourceGroups(String[] resGrps, String defGrp)
        {
            if (resGrps.length == 0)
            {
                return new String[] {
                    defGrp
                };
            }

            else
            {
                String[] result = new String[resGrps.length + 1];
                System.arraycopy(resGrps, 0, result, 0, resGrps.length);
                result[resGrps.length] = defGrp;
                return result;
            }
        }

        /**
         * Resolves the message from the resources.
         *
         * @return the resolved message
         */
        private String resolveMessage()
        {
            MissingResourceException ex = null;

            for (String grp : resourceGroups)
            {
                Message msg = Message.createWithParameters(grp, getKey(),
                        parameters);
                try
                {
                    return msg.resolve(resourceManager, locale);
                }
                catch (MissingResourceException mrex)
                {
                    // not found in this group => try others
                    ex = mrex;
                }
            }

            // The message was not found in all groups.
            // Throw the last exception.
            assert ex != null : "No missing resource exception thrown!";
            throw ex;
        }
    }
}
