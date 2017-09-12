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
package net.sf.jguiraffe.gui.builder.di.tags;

import net.sf.jguiraffe.di.impl.ClassDescription;
import net.sf.jguiraffe.di.impl.providers.ConstantBeanProvider;
import net.sf.jguiraffe.gui.builder.di.DIBuilderData;

import org.apache.commons.jelly.JellyContext;
import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.Tag;

/**
 * <p>
 * A data class for holding all information required for creating a value.
 * </p>
 * <p>
 * Some tag handler classes support the creation of a constant value. Such a
 * value is defined by the value object itself and optionally by a target class
 * used for type conversion. This class supports all properties required for
 * defining a value. The value is passed to the <code>value</code> attribute.
 * Its target class can be specified using the <code>valueClass</code>,
 * <code>valueClassName</code>, and <code>valueClassLoader</code> attributes
 * (which gather the data supported by a {@link ClassDescription} object. There
 * are also some helper methods for validating the passed in data and creating a
 * constant bean provider.
 * </p>
 * <p>
 * Tag handler implementations that need to support a value definition can
 * define a member variable of this type. Then they have to define set methods
 * for the attributes determining the value and its class and delegate to the
 * corresponding set methods of this class.
 * </p>
 * <p>
 * Implementation note: This class is not thread-safe.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: ValueData.java 205 2012-01-29 18:29:57Z oheger $
 */
public class ValueData
{
    /** Stores a reference to the owning tag. */
    private final Tag owner;

    /** Stores the data object for defining the target class of the value. */
    private final ClassDescData valueClassData;

    /** Stores the value of a constant dependency. */
    private Object value;

    /** A flag whether the value of this tag is defined.*/
    private boolean defined;

    /**
     * Creates a new instance of <code>ValueData</code>.
     *
     * @param owningTag the tag that owns this object (must not be <b>null</b>)
     * @throws IllegalArgumentException if the reference to the owner is
     *         <b>null</b>
     */
    public ValueData(Tag owningTag)
    {
        if (owningTag == null)
        {
            throw new IllegalArgumentException("Owner must not be null!");
        }

        valueClassData = new ClassDescData();
        owner = owningTag;
    }

    /**
     * Returns a reference to the owning tag.
     *
     * @return the owner
     */
    public Tag getOwner()
    {
        return owner;
    }

    /**
     * Returns the <code>ClassDescData</code> object that defines the target
     * class of the value.
     *
     * @return the target class data of the value
     */
    public ClassDescData getValueClassData()
    {
        return valueClassData;
    }

    /**
     * Returns the value for a constant dependency.
     *
     * @return the value
     */
    public Object getValue()
    {
        return value;
    }

    /**
     * Set method of the value attribute. Here the actual value can be set.
     *
     * @param value the attribute's value
     */
    public void setValue(Object value)
    {
        this.value = value;
        defined = true;
    }

    /**
     * Set method for the valueClass attribute. This attribute allows defining
     * the class of the value. When creating the value bean provider a
     * conversion to this type will be performed.
     *
     * @param c the attribute's value
     */
    public void setValueClass(Class<?> c)
    {
        getValueClassData().setTargetClass(c);
    }

    /**
     * Set method for the valueClassName attribute. This is analogous to
     * <code>setValueClass()</code>, but the value's class can be specified by
     * name.
     *
     * @param s the attribute's value
     */
    public void setValueClassName(String s)
    {
        getValueClassData().setTargetClassName(s);
    }

    /**
     * Set method for the valueClassLoader attribute. If the value class is
     * specified by name, with this property a class loader can be selected,
     * which is to be used for resolving the actual value class.
     *
     * @param s the attribute's value
     */
    public void setValueClassLoader(String s)
    {
        getValueClassData().setClassLoaderName(s);
    }

    /**
     * Returns a flag whether the value is defined. Defined means that the
     * {@link #setValue(Object)} method has been called at least once - even if
     * a value of <b>null</b> was passed. This makes it possible to set
     * <b>null</b> values.
     *
     * @return a flag whether a value for this tag was specified
     */
    public boolean isValueDefined()
    {
        return defined;
    }

    /**
     * Creates the bean provider for the specified value. If necessary, type
     * conversion is performed.
     *
     * @return the bean provider managing the constant value specified for this
     *         tag
     * @throws JellyTagException if the target class for a conversion is
     *         ambiguous
     * @throws IllegalArgumentException if type conversion fails
     */
    public ConstantBeanProvider createValueProvider() throws JellyTagException
    {
        return createValueProvider(null);
    }

    /**
     * Creates the bean provider for the specified value using the given default
     * class description. If a class of the value is specified using the
     * properties of this instance, this class will be used. Otherwise the
     * passed in class description (if defined) will be used.
     *
     * @param defClass the default class description
     * @return the bean provider managing the constant value specified for this
     *         tag
     * @throws JellyTagException if the target class for a conversion is
     *         ambiguous
     * @throws IllegalArgumentException if type conversion fails
     */
    public ConstantBeanProvider createValueProvider(ClassDescription defClass)
            throws JellyTagException
    {
        Class<?> valCls;

        if (getValueClassData().isDefined())
        {
            valCls = resolveClassDescData(getOwner().getContext(),
                    getValueClassData());
        }
        else
        {
            valCls = resolveClassDescription(getOwner().getContext(), defClass);
        }

        return ConstantBeanProvider.getInstance(valCls, getValue());
    }

    /**
     * Resolves the specified <code>ClassDescData</code> object. This method
     * obtains an optional <code>ClassDescription</code> from the class data
     * object (this may be <b>null</b> if the class is undefined or throw an
     * exception if the class definition is ambiguous). Then
     * <code>resolveClassDescription()</code> is invoked.
     *
     * @param context the Jelly context
     * @param cdata the class description data object
     * @return the resolved class (can be <b>null</b> if no class is defined)
     * @throws JellyTagException if the class description is invalid
     */
    public static Class<?> resolveClassDescData(JellyContext context,
            ClassDescData cdata) throws JellyTagException
    {
        return resolveClassDescription(context, cdata
                .getOptionalClassDescription());
    }

    /**
     * Resolves the specified <code>ClassDescription</code>. This method obtains
     * the <code>ClassLoaderProvider</code> from the
     * <code>{@link DIBuilderData}</code> object found in the passed in Jelly
     * context and uses it to obtain the target class from the class
     * description. The passed in description can be <b>null</b>, then result
     * will also be <b>null</b>.
     *
     * @param context the Jelly context
     * @param cd the class description to resolve (can be <b>null</b>)
     * @return the class the class description refers to
     */
    public static Class<?> resolveClassDescription(JellyContext context,
            ClassDescription cd)
    {
        if (cd == null)
        {
            return null;
        }

        else
        {
            DIBuilderData builderData = DIBuilderData.get(context);
            return cd.getTargetClass(builderData.getClassLoaderProvider());
        }
    }
}
