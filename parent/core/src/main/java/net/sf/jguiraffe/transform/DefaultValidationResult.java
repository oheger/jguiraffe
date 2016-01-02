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
package net.sf.jguiraffe.transform;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import org.apache.commons.lang.StringUtils;

/**
 * <p>
 * A default implementation of the {@code ValidationResult} interface.
 * </p>
 * <p>
 * Instances of this class store a collection of validation error messages. If
 * no messages exist with a {@link ValidationMessageLevel} of {@code ERROR}, the
 * validation result is considered to be valid. It may contain other messages
 * which are only warning messages.
 * </p>
 * <p>
 * Clients do not create instances directly through the constructor, but use the
 * nested {@code Builder} class. Once a builder is created, an arbitrary number
 * of validation error messages can be added. If all messages have been added,
 * the {@code build()} method creates an immutable instance of
 * {@code DefaultValidationResult}. This has the advantage that all fields of
 * {@code DefaultValidationResult} can be made final. Therefore objects of this
 * class are thread-safe (note that is not true for the {@code Builder}
 * objects).
 * </p>
 * <p>
 * There is also a constant representing a validation result for a successful
 * validation. This field can be used in custom validator implementations rather
 * than creating a new instance of this class.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: DefaultValidationResult.java 205 2012-01-29 18:29:57Z oheger $
 */
public final class DefaultValidationResult implements ValidationResult
{
    /** Constant for a validation result object for a successful validation. */
    public static final ValidationResult VALID = new Builder().build();

    /** Stores the error messages. */
    private final Collection<ValidationMessage> messages;

    /** A flag whether error messages are available. */
    private final boolean hasErrors;

    /** A flag whether warning messages are available. */
    private final boolean hasWarnings;

    /**
     * Creates a new instance of {@code DefaultValidationResult} and initializes
     * it with the collection of error messages obtained from the given {@code
     * Builder}. Note that this constructor is private, so instances can only be
     * created through the builder.
     *
     * @param builder the {@code Builder}
     */
    private DefaultValidationResult(Builder builder)
    {
        boolean foundErrors = false;
        boolean foundWarnings = false;

        if (builder.messages.isEmpty())
        {
            messages = Collections.emptyList();
        }
        else
        {
            messages = Collections.unmodifiableCollection(builder.messages);

            for (ValidationMessage vm : messages)
            {
                if (vm.getLevel() == ValidationMessageLevel.ERROR)
                {
                    foundErrors = true;
                }
                else if (vm.getLevel() == ValidationMessageLevel.WARNING)
                {
                    foundWarnings = true;
                }
            }
        }

        hasErrors = foundErrors;
        hasWarnings = foundWarnings;
    }

    /**
     * Returns a flag whether the validation was successful. This implementation
     * tests if error messages have been added to this object.
     *
     * @return a flag if validation was successful
     */
    public boolean isValid()
    {
        return !hasErrors;
    }

    /**
     * Returns an unmodifiable collection with error messages.
     *
     * @return a collection with the error messages
     */
    public Collection<ValidationMessage> getValidationMessages()
    {
        return messages;
    }

    /**
     * Returns an unmodifiable collection with the {@code ValidationMessage}
     * objects with the specified {@code ValidationMessageLevel}.
     *
     * @param level the {@code ValidationMessageLevel}
     * @return a collection with the requested messages
     */
    public Collection<ValidationMessage> getValidationMessages(
            ValidationMessageLevel level)
    {
        if (hasMessages(level))
        {
            Collection<ValidationMessage> result = new ArrayList<ValidationMessage>(
                    messages.size());
            for (ValidationMessage vm : messages)
            {
                if (level == vm.getLevel())
                {
                    result.add(vm);
                }
            }

            return Collections.unmodifiableCollection(result);
        }

        return Collections.emptyList();
    }

    /**
     * Returns a flag whether messages of the specified level are available.
     * These flags are initialized at construction time, so this method is
     * pretty efficient.
     *
     * @param level the {@code ValidationMessageLevel} in question
     * @return a flag whether messages with this level are available
     */
    public boolean hasMessages(ValidationMessageLevel level)
    {
        if (level == null)
        {
            return false;
        }

        switch (level)
        {
        case ERROR:
            return hasErrors;
        default:
            return hasWarnings;
        }
    }

    /**
     * Compares this object to another one. Two validation result objects are
     * considered equal if and only if they contain the same error messages.
     *
     * @param obj the object to compare to
     * @return a flag whether the objects are equal
     */
    @Override
    public boolean equals(Object obj)
    {
        if (obj == this)
        {
            return true;
        }
        if (!(obj instanceof DefaultValidationResult))
        {
            return false;
        }

        DefaultValidationResult c = (DefaultValidationResult) obj;
        // obviously equals() does not work for unmodifiable collections, so we
        // have to check the elements ourselves
        if (messages.size() != c.messages.size())
        {
            return false;
        }
        Iterator<ValidationMessage> it = c.messages.iterator();
        for (ValidationMessage vm : messages)
        {
            if (!vm.equals(it.next()))
            {
                return false;
            }
        }
        return true;
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
        for (ValidationMessage vm : messages)
        {
            result = factor * result + vm.hashCode();
        }

        return result;
    }

    /**
     * Returns a string representation for this object. This string will also
     * contain the string representations for all contained error messages.
     *
     * @return a string for this object
     */
    @Override
    public String toString()
    {
        StringBuilder buf = new StringBuilder();
        buf.append(getClass().getName()).append('@');
        buf.append(System.identityHashCode(this));
        buf.append("[ ");

        if (isValid())
        {
            buf.append("VALID");
        }
        else
        {
            buf.append("messages = ");
            buf.append(StringUtils.join(messages, ", "));
        }

        buf.append(" ]");
        return buf.toString();
    }

    /**
     * Combines two validation result objects to a combined result. If one of
     * the passed in result objects is invalid, the resulting object will also
     * be invalid. It will contain the union of all error messages. A new
     * {@code ValidationResult} object is created only if necessary: If one of
     * the arguments is valid and has no warning messages, the other argument is
     * returned. The same is true for <b>null</b> arguments. This method returns
     * <b>null</b> only if both arguments are <b>null</b>.
     *
     * @param vr1 the first validation result object
     * @param vr2 the second validation result object
     * @return a combined validation result
     */
    public static ValidationResult merge(ValidationResult vr1,
            ValidationResult vr2)
    {
        if (isPureValid(vr1))
        {
            return vr2;
        }
        else if (isPureValid(vr2))
        {
            return vr1;
        }

        // both are non valid => create a combined result
        Builder builder = new Builder();
        return builder.addValidationMessages(vr1.getValidationMessages())
                .addValidationMessages(vr2.getValidationMessages()).build();
    }

    /**
     * Creates a {@code ValidationMessage} object for the specified error
     * message. This is a convenience method that obtains the
     * {@link ValidationMessageHandler} from the {@code TransformerContext} and
     * obtains a {@link ValidationMessage} for the specified parameters.
     *
     * @param context the {@code TransformerContext} (must not be <b>null</b>)
     * @param key the key of the error message
     * @param params optional parameters for the error message
     * @return a {@code ValidationResult} object with the specified message
     * @throws IllegalArgumentException if the {@code TransformerContext} is
     *         <b>null</b>
     */
    public static ValidationMessage createValidationMessage(
            TransformerContext context, String key, Object... params)
    {
        if (context == null)
        {
            throw new IllegalArgumentException(
                    "TransformerContext must not be null!");
        }

        ValidationMessageHandler msgHandler = context
                .getValidationMessageHandler();
        return msgHandler.getValidationMessage(context, key, params);
    }

    /**
     * Creates a {@code ValidationResult} object with the specified error
     * message. This is a convenience method for the frequent case that a
     * {@code ValidationResult} object with exactly one error message has to be
     * created. It obtains the {@link ValidationMessageHandler} from the {@code
     * TransformerContext}, obtains a {@link ValidationMessage} for the
     * specified parameters, and creates a {@code ValidationResult} object with
     * exactly this message.
     *
     * @param context the {@code TransformerContext} (must not be <b>null</b>)
     * @param key the key of the error message
     * @param params optional parameters for the error message
     * @return a {@code ValidationResult} object with this error message
     * @throws IllegalArgumentException if the {@code TransformerContext} is
     *         <b>null</b>
     */
    public static ValidationResult createValidationErrorResult(
            TransformerContext context, String key, Object... params)
    {
        return new DefaultValidationResult.Builder().addValidationMessage(
                createValidationMessage(context, key, params)).build();
    }

    /**
     * Helper method for checking whether a validation result can be considered
     * "pure" valid. This means that there are no messages at all. A null object
     * is also pure valid!
     *
     * @param vr the validation result to check
     * @return a flag whether this result object is pure valid
     */
    private static boolean isPureValid(ValidationResult vr)
    {
        return vr == null
                || (vr.isValid() && !vr
                        .hasMessages(ValidationMessageLevel.WARNING));
    }

    /**
     * <p>
     * A <em>builder</em> class for creating instances of {@code
     * DefaultValidationResult}.
     * </p>
     * <p>
     * In order to create a new {@code DefaultValidationResult} instance, create
     * a builder, call its {@code addErrorMessage()} methods, and finally invoke
     * the {@code build()} method. This can look as follows:
     *
     * <pre>
     * DefaultValidationResult vres = new DefaultValidationResult.Builder()
     *         .addErrorMessage(msg1).addErrorMessage(msg2).build();
     * </pre>
     *
     * </p>
     */
    public static class Builder
    {
        /** A collection with the validation messages added so far. */
        private Collection<ValidationMessage> messages;

        /**
         * Creates a new instance of {@code Builder}
         */
        public Builder()
        {
            reset();
        }

        /**
         * Adds an object with a validation message to this instance. If the
         * message has the level {@code ERROR}, this also
         * means that the validation failed.
         *
         * @param msg the message object (must not be <b>null</b>)
         * @return a reference to this builder
         * @throws IllegalArgumentException if the message is <b>null</b>
         */
        public Builder addValidationMessage(ValidationMessage msg)
        {
            if (msg == null)
            {
                throw new IllegalArgumentException(
                        "Error message must not be null!");
            }

            messages.add(msg);
            return this;
        }

        /**
         * Adds all messages stored in the given collection to this object.
         *
         * @param msgs the collection with the messages to add (must not be
         *        <b>null</b>)
         * @return a reference to this builder
         * @throws IllegalArgumentException if the collection with the messages
         *         is <b>null</b> or one of its elements is <b>null</b>
         */
        public Builder addValidationMessages(Collection<ValidationMessage> msgs)
        {
            if (msgs == null)
            {
                throw new IllegalArgumentException(
                        "Message collection must not be null!");
            }

            Collection<ValidationMessage> copy = new ArrayList<ValidationMessage>(
                    msgs);
            for (ValidationMessage m : copy)
            {
                if (m == null)
                {
                    throw new IllegalArgumentException(
                            "Message collection contains a null element!");
                }
            }

            messages.addAll(copy);
            return this;
        }

        /**
         * Returns the {@code DefaultValidationResult} defined by this builder.
         *
         * @return the {@code DefaultValidationResult} created by this builder
         */
        public DefaultValidationResult build()
        {
            DefaultValidationResult res = new DefaultValidationResult(this);
            reset();
            return res;
        }

        /**
         * Resets this builder. After that definition of a new validation result
         * object can be started.
         */
        public final void reset()
        {
            messages = new ArrayList<ValidationMessage>();
        }
    }
}
