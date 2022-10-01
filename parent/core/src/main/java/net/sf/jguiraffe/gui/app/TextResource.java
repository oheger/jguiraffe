/*
 * Copyright 2006-2022 The JGUIraffe Team.
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

import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * <p>
 * A class representing a textual resource.
 * </p>
 * <p>
 * This class can be used in scenarios where either a plain text or a text from
 * the application's resources can be provided. Different factory methods for
 * these use cases are provided. Instances can be queried for the data they
 * contain. They also have a {@code resolve()} method which - given an
 * {@link ApplicationContext} reference - returns the textual value of this
 * resource.
 * </p>
 *
 * @since 1.4
 */
public abstract class TextResource
{
    /**
     * Constant for an undefined text resource. This instance returns
     * <strong>null</strong> for all access methods.
     */
    public static final TextResource UNDEFINED = new TextResource()
    {
    };

    /**
     * Constructor preventing subclassing outside this package.
     */
    TextResource()
    {
    }

    /**
     * Returns the plain text for this resource. This method is defined if a
     * plain text is available (i.e. the text does not have to be fetched from
     * application resources). Otherwise, it returns <strong>null</strong>.
     *
     * @return the plain text for this resource or <strong>null</strong>
     */
    public String getPlainText()
    {
        return null;
    }

    /**
     * Returns the resource ID for this object. This method is defined if this
     * resource represents an entry from the application's resources. Otherwise,
     * result is <strong>null</strong>
     *
     * @return the resource ID or <strong>null</strong>
     */
    public Object getResourceID()
    {
        return null;
    }

    /**
     * Resolves this resource and returns its text value. If necessary, does a
     * resource lookup using the given application context. The final text
     * represented by this object is returned. For an undefined resource result
     * is <strong>null</strong>.
     *
     * @param applicationContext the application context
     * @return the text represented by this resource
     */
    public String resolveText(ApplicationContext applicationContext)
    {
        return null;
    }

    @Override
    public int hashCode()
    {
        return new HashCodeBuilder().append(getPlainText())
                .append(getResourceID()).toHashCode();
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (!(obj instanceof TextResource))
        {
            return false;
        }

        TextResource c = (TextResource) obj;
        return ((getPlainText() == null) ? c.getPlainText() == null
                : getPlainText().equals(c.getPlainText()))
                && ((getResourceID() == null) ? c.getResourceID() == null
                        : getResourceID().equals(c.getResourceID()));
    }

    /**
     * Returns a string representation of this object. The string contains the
     * data that is defined: the plain text or the resource ID. (For the
     * undefined resource, neither is available.)
     *
     * @return a string for this object
     */
    @Override
    public String toString()
    {
        StringBuilder buf = new StringBuilder();
        buf.append("TextResource{");
        if (getPlainText() != null)
        {
            buf.append("text='").append(getPlainText()).append("'");
        }
        if (getResourceID() != null)
        {
            buf.append("resourceID='").append(getResourceID()).append("'");
        }
        buf.append('}');
        return buf.toString();
    }

    /**
     * Creates a new instance of {@code TextResource} that represents the given
     * plain text. No resource lookup is necessary; the text is directly
     * provided. If the text is <strong>null</strong>, however, an undefined
     * resource is returned.
     *
     * @param text the plain text
     * @return a {@code TextResource} representing this plain text
     */
    public static TextResource fromText(String text)
    {
        return (text != null) ? new PlainTextResource(text) : UNDEFINED;
    }

    /**
     * Creates a new instance of {@code TextResource} that represents an
     * application resource with the given ID. The actual text is obtained via a
     * resource lookup in the {@code resolve()} method. If the resource ID is
     * <strong>null</strong>, however, an undefined resource is returned.
     *
     * @param resID the resource ID
     * @return a {@code TextResource} representing this resource ID
     */
    public static TextResource fromResourceID(Object resID)
    {
        return (resID != null) ? new ResIDTextResource(resID) : UNDEFINED;
    }

    /**
     * A sub class for resources that have a defined plain text.
     */
    private static class PlainTextResource extends TextResource
    {
        /** The plain text. */
        private final String plainText;

        /**
         * Creates a new instance of {@code PlainTextResource} with the given
         * plain text.
         *
         * @param text the plain text
         */
        PlainTextResource(String text)
        {
            plainText = text;
        }

        @Override
        public String getPlainText()
        {
            return plainText;
        }

        @Override
        public String resolveText(ApplicationContext applicationContext)
        {
            return getPlainText();
        }
    }

    /**
     * A sub class for resources that reference a resource ID.
     */
    private static class ResIDTextResource extends TextResource
    {
        /** The resource ID. */
        private final Object resourceID;

        /**
         * Creates a new instance of {@code ResIDTextResource} with the given
         * resource ID.
         *
         * @param resID the resource ID
         */
        ResIDTextResource(Object resID)
        {
            resourceID = resID;
        }

        @Override
        public Object getResourceID()
        {
            return resourceID;
        }

        @Override
        public String resolveText(ApplicationContext applicationContext)
        {
            return applicationContext.getResourceText(getResourceID());
        }
    }
}
