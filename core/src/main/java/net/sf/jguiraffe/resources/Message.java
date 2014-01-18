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
package net.sf.jguiraffe.resources;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Locale;

import org.apache.commons.lang.ObjectUtils;

/**
 * <p>
 * A convenience class for dealing with localized messages that can have
 * parameters.
 * </p>
 * <p>
 * Instances of this class can be initialized with a resource group and a
 * resource key of a message and optionally with a set of parameters. The text
 * resource referred to can contain placeholders as supported by
 * <code>java.text.MessageFormat</code>. When the message is to be displayed the
 * {@link #resolve(ResourceManager, Locale)} method fetches the message text
 * from the passed in {@link ResourceManager} and replaces the placeholders by
 * actual parameter values.
 * </p>
 * <p>
 * Instances of this class are immutable (provided that the parameters are
 * immutable) and thus can be shared between multiple threads.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: Message.java 205 2012-01-29 18:29:57Z oheger $
 */
public class Message
{
    /** Constant for an empty parameters array. */
    private static final Object[] NO_PARAMS = new Object[0];

    /** Stores the resource group of this message. */
    private final Object resourceGroup;

    /** Stores the resource key of this message. */
    private final Object resourceKey;

    /** Stores an array with the parameter values. */
    private final Object[] parameters;

    /**
     * Creates a new instance of <code>Message</code> and initializes it with a
     * resource key. The default resource group is used. No parameters are set.
     *
     * @param resKey the resource key (must not be <b>null</b>)
     * @throws IllegalArgumentException if the resource key is <b>null</b>
     */
    public Message(Object resKey)
    {
        this(null, resKey);
    }

    /**
     * Creates a new instance of <code>Message</code> and initializes it with a
     * resource group and a resource key. No parameters are set
     *
     * @param resGrp the resource group
     * @param resKey the resource key (must not be <b>null</b>)
     * @throws IllegalArgumentException if the resource key is <b>null</b>
     */
    public Message(Object resGrp, Object resKey)
    {
        checkResourceKey(resKey);
        resourceGroup = resGrp;
        resourceKey = resKey;
        parameters = NO_PARAMS;
    }

    /**
     * Creates a new instance of {@code Message} and initializes it with a
     * resource group, a resource key, and a single parameter. This constructor
     * can be used for the frequent case that only a single parameter is needed.
     *
     * @param resGrp the resource group (can be <b>null</b> for the default
     *        resource group)
     * @param resKey the resource key (must not be <b>null</b>)
     * @param param the single parameter of this {@code Message}
     * @throws IllegalArgumentException if the resource key is <b>null</b>
     */
    public Message(Object resGrp, Object resKey, Object param)
    {
        checkResourceKey(resKey);
        resourceGroup = resGrp;
        resourceKey = resKey;
        parameters = new Object[] {
            param
        };
    }

    /**
     * Creates a new instance of {@code Message} and initializes it with a
     * resource group, a resource key, and two parameters. This constructor can
     * be used for the case that exactly two parameters are needed.
     *
     * @param resGrp the resource group (can be <b>null</b> for the default
     *        resource group)
     * @param resKey the resource key (must not be <b>null</b>)
     * @param param1 the first parameter of this {@code Message}
     * @param param2 the second parameter of this {@code Message}
     * @throws IllegalArgumentException if the resource key is <b>null</b>
     */
    public Message(Object resGrp, Object resKey, Object param1, Object param2)
    {
        checkResourceKey(resKey);
        resourceGroup = resGrp;
        resourceKey = resKey;
        parameters = new Object[] {
                param1, param2
        };
    }

    /**
     * Creates a new instance of {@code Message} and initializes it with a
     * resource group, a resource key, and an arbitrary number of parameters
     * that are passed as variable arguments. Use this constructor if the number
     * of parameters is greater than 2. For one or two parameters there are
     * specialized constructors that are more efficient as there is no need to
     * create an array.
     *
     * @param resGrp the resource group
     * @param resKey the resource key (must not be <b>null</b>)
     * @param param1 the first parameter of this {@code Message}
     * @param param2 the second parameter of this {@code Message}
     * @param params an arbitrary number of additional parameters
     * @throws IllegalArgumentException if the resource key is <b>null</b>
     */
    public Message(Object resGrp, Object resKey, Object param1, Object param2,
            Object... params)
    {
        checkResourceKey(resKey);
        resourceGroup = resGrp;
        resourceKey = resKey;
        int paramLength = (params == null) ? 0 : params.length;
        parameters = new Object[2 + paramLength];
        parameters[0] = param1;
        parameters[1] = param2;
        if (paramLength > 0)
        {
            System.arraycopy(params, 0, parameters, 2, params.length);
        }
    }

    /**
     * Creates an instance of {@code Message} with an arbitrary number of
     * parameters. This factory method can be used instead of the constructors
     * if the parameters are already available as {@code Object[]}.
     *
     * @param resGrp the resource group
     * @param resKey the resource key (must not be <b>null</b>)
     * @param params the parameters
     * @return the {@code Message} instance
     * @throws IllegalArgumentException if the resource key is <b>null</b>
     */
    public static Message createWithParameters(Object resGrp, Object resKey,
            Object... params)
    {
        if (params == null || params.length == 0)
        {
            return new Message(resGrp, resKey);
        }
        else if (params.length == 1)
        {
            return new Message(resGrp, resKey, params[0]);
        }
        else if (params.length == 2)
        {
            return new Message(resGrp, resKey, params[0], params[1]);
        }

        else
        {
            // create a new array with the parameter indices greater than 2
            Object[] varParams = new Object[params.length - 2];
            System.arraycopy(params, 2, varParams, 0, varParams.length);
            return new Message(resGrp, resKey, params[0], params[1],
                    (Object[]) varParams);
        }
    }

    /**
     * Creates a new {@code Message} instance with the same resource group and
     * resource key as the passed in instance, but with different parameters.
     *
     * @param msg the original {@code Message} (must not be <b>null</b>)
     * @param params the new parameters
     * @return the new {@code Message} instance
     * @throws IllegalArgumentException if the original {@code Message} is
     *         <b>null</b>
     */
    public static Message createFromMessage(Message msg, Object... params)
    {
        if (msg == null)
        {
            throw new IllegalArgumentException("Message must not be null!");
        }

        return createWithParameters(msg.getResourceGroup(), msg
                .getResourceKey(), params);
    }

    /**
     * Returns the resource group.
     *
     * @return the resource group
     */
    public Object getResourceGroup()
    {
        return resourceGroup;
    }

    /**
     * Returns the resource key.
     *
     * @return the resource key
     */
    public Object getResourceKey()
    {
        return resourceKey;
    }

    /**
     * Returns an array with the parameters stored in this {@code Message}
     * instance. These parameters are applied to the resource text when calling
     * the {@link #resolve(ResourceManager, Locale)} method. The array returned
     * by this method is a copy of the actual parameters, so modifying it does
     * not affect this {@code Message} instance.
     *
     * @return an array with the parameter values
     */
    public Object[] getParameters()
    {
        return (parameters.length == 0) ? parameters : parameters.clone();
    }

    /**
     * Resolves this message and returns its text value in the given locale. If
     * parameters are defined, they are replaced.
     *
     * @param resMan the resource manager
     * @param locale the locale
     * @return the text of the message
     * @throws java.util.MissingResourceException if the resource cannot be resolved
     */
    public String resolve(ResourceManager resMan, Locale locale)
    {
        String txt = resMan.getText(locale, getResourceGroup(),
                getResourceKey());
        if (parameters != null)
        {
            // replace parameters
            MessageFormat fmt = new MessageFormat(txt, locale);
            txt = fmt.format(parameters, new StringBuffer(), null).toString();
        }

        return txt;
    }

    /**
     * Tests if this message equals another object. This method returns
     * <b>true</b> only if the other object is a <code>Message</code> object,
     * too, and all of its properties are equal to the properties of this
     * object.
     *
     * @param obj the other object
     * @return a flag whether these objects are equal
     */
    @Override
    public boolean equals(Object obj)
    {
        if (obj == null)
        {
            return false;
        }
        if (!(obj instanceof Message))
        {
            return false;
        }

        Message c = (Message) obj;
        return getResourceKey().equals(c.getResourceKey())
                && ObjectUtils.equals(getResourceGroup(), c.getResourceGroup())
                && Arrays.equals(parameters, c.parameters);
    }

    /**
     * Calculates a hash code for this message.
     *
     * @return a hash code
     */
    @Override
    public int hashCode()
    {
        final int seed = 17;
        final int factor = 31;

        int result = seed;
        result = factor * result + getResourceKey().hashCode();
        if (getResourceGroup() != null)
        {
            result = factor * result + getResourceGroup().hashCode();
        }
        result = factor * result + Arrays.hashCode(parameters);

        return result;
    }

    /**
     * Returns a string representation of this object. This string contains the
     * resource key and the resource group and the values of the parameters.
     *
     * @return a string for this object
     */
    @Override
    public String toString()
    {
        StringBuilder buf = new StringBuilder();
        buf.append("Message [");
        if (getResourceGroup() != null)
        {
            buf.append(" group = ").append(getResourceGroup());
        }
        buf.append(" key = ").append(getResourceKey());
        if (parameters.length > 0)
        {
            buf.append(" parameters = ");
            buf.append(Arrays.toString(parameters));
        }
        return buf.toString();
    }

    /**
     * Tests whether the resource key is specified. Otherwise throws an
     * exception.
     *
     * @param resKey the resource key
     * @throws IllegalArgumentException if the resource key is <b>null</b>
     */
    private static void checkResourceKey(Object resKey)
    {
        if (resKey == null)
        {
            throw new IllegalArgumentException("Resource key must not be null!");
        }
    }
}
