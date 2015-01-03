/*
 * Copyright 2006-2015 The JGUIraffe Team.
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
package net.sf.jguiraffe.di;

import java.util.LinkedList;

import org.apache.commons.beanutils.ConversionException;
import org.apache.commons.beanutils.ConvertUtilsBean;
import org.apache.commons.beanutils.Converter;
import org.apache.commons.lang.ClassUtils;

/**
 * <p>
 * A helper class providing functionality related to data type conversion and
 * registration of custom converters.
 * </p>
 * <p>
 * The conversion of data types - e.g. for properties or method parameters - is
 * an important feature: Because beans to be managed by the dependency injection
 * framework are typically defined in XML builder scripts all property values or
 * method parameters are initially strings. The framework has then to find
 * appropriate methods compatible with the specified parameters. If necessary, a
 * type conversion of the values involved has to be performed.
 * </p>
 * <p>
 * This class uses <a href="http://commons.apache.org/beanutils">Commons
 * BeanUtils</a> for implementing type conversion facilities. The BeanUtils
 * library allows defining custom type converters. Such converters can be
 * registered at an instance of this class to enhance the type conversion
 * capabilities.
 * </p>
 * <p>
 * Objects of this class can be connected in a hierarchical way: an instance can
 * have a parent. If a required type converter is not found in this instance,
 * the parent's converters are searched. This allows for instance to define base
 * converters on a top-level instance. Child instances can define specialized
 * converters and even override converters of their parents.
 * </p>
 * <p>
 * Implementation note: This class is thread-safe.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: ConversionHelper.java 205 2012-01-29 18:29:57Z oheger $
 */
public class ConversionHelper
{
    /** Stores a reference to the parent instance. */
    private final ConversionHelper parent;

    /** The helper object for performing data conversions. */
    private final ConvertUtilsBean convertBean;

    /** A list for the registered base class converters. */
    private final LinkedList<ConverterData> baseClassConverters;

    /**
     * Creates a new instance of {@code ConversionHelper} which does not have a
     * parent.
     */
    public ConversionHelper()
    {
        this(null);
    }

    /**
     * Creates a new instance of {@code ConversionHelper} and initializes it
     * with the given parent instance. If the parent is defined, it may be
     * queried for type converters if this instance cannot resolve specific data
     * types.
     *
     * @param parent the parent instance (may be <b>null</b>)
     */
    public ConversionHelper(ConversionHelper parent)
    {
        baseClassConverters = new LinkedList<ConverterData>();
        convertBean = new CustomConverterBean();
        convertBean.register(true, false, 0);
        this.parent = parent;

        if (parent == null)
        {
            registerDefaultConverters();
        }
    }

    /**
     * Returns the parent of this instance. This may be <b>null</b> if no parent
     * has been set.
     *
     * @return the parent
     */
    public ConversionHelper getParent()
    {
        return parent;
    }

    /**
     * Registers a converter for the specified target class. This converter is
     * used by the {@link #convert(Class, Object)} method if a conversion to the
     * specified target class is needed.
     *
     * @param converter the converter to be registered (must not be <b>null</b>)
     * @param targetClass the target class of the converter (must not be
     *        <b>null</b>)
     * @throws IllegalArgumentException if a required parameter is missing
     */
    public final void registerConverter(Converter converter,
            Class<?> targetClass)
    {
        checkRegisterConverterArgs(converter, targetClass);
        getConvertBean().register(converter, targetClass);
    }

    /**
     * Registers a converter for the given base class and all derived classes.
     * If a data conversion is to be performed, it is checked first whether a
     * specific converter for the target class has been registered (using the
     * {@link #registerConverter(Converter, Class)} method). If this is the
     * case, this converter is used. Otherwise, it is checked whether a
     * converter has been registered using this method whose target class is a
     * super class of the desired target class. If this is the case, this
     * converter is invoked. With this method converters for whole class
     * hierarchies can be registered. This can be useful if all members of the
     * hierarchy require a similar conversion. An example are enumeration
     * classes. Base class converters are checked in reverse order they have
     * been registered. So if they form a hierarchy themselves, the least
     * specific converter should be registered first, followed by more specific
     * ones. For instance, consider some base class converters dealing with
     * collection classes. There may be one converter that handles
     * {@code java.util.List} objects and one for generic
     * {@code java.util.Collection} objects. In this scenario the collection
     * converter has to be registered first followed by the specific one for
     * lists. Otherwise, the generic collection converter will also be used for
     * lists. This reverse order approach makes it possible for applications to
     * override default base class converters with their own implementations: if
     * custom converters are added later, they take precedence over the already
     * registered converters.
     *
     * @param converter the converter to be registered (must not be <b>null</b>)
     * @param targetClass the target class of the converter (must not be
     *        <b>null</b>)
     * @throws IllegalArgumentException if a required parameter is missing
     */
    public final void registerBaseClassConverter(Converter converter,
            Class<?> targetClass)
    {
        checkRegisterConverterArgs(converter, targetClass);
        synchronized (baseClassConverters)
        {
            baseClassConverters.add(0,
                    new ConverterData(converter, targetClass));
        }
    }

    /**
     * Performs a type conversion. This method tries to convert the specified
     * value to the given target class. Under the hood converters of the
     * <em>Commons BeanUtils</em> library are used to actually perform the
     * conversion. If custom data types are involved, specialized converters can
     * be registered. If no conversion is possible, an
     * {@code InjectionException} exception is thrown.
     *
     * @param <T> the type of the target class
     * @param targetClass the target class (must not be <b>null</b>)
     * @param value the value to be converted
     * @return the converted value
     * @throws InjectionException if no conversion is possible
     * @throws IllegalArgumentException if the target class is <b>null</b>
     */
    public <T> T convert(Class<T> targetClass, Object value)
    {
        checkTargetClass(targetClass);
        if (value == null)
        {
            return null;
        }

        if (!targetClass.isInstance(value))
        {
            return convertObject(targetClass, value);
        }
        else
        {
            @SuppressWarnings("unchecked")
            // because of instance check
            T result = (T) value;
            return result;
        }
    }

    /**
     * Returns the helper object for type conversions.
     *
     * @return the {@code ConvertUtilsBean} object responsible for conversions
     */
    protected ConvertUtilsBean getConvertBean()
    {
        return convertBean;
    }

    /**
     * Performs a type conversion to the given target class if possible. This
     * method is called by {@link #convert(Class, Object)} if a conversion is
     * actually required. The arguments have already been checked for
     * <b>null</b>. This implementation uses the conversion helper object
     * returned by {@link #getConvertBean()} to do the conversion.
     *
     * @param <T> the type of the target class
     * @param targetClass the target class of the conversion
     * @param value the value to be converted
     * @return the converted value
     * @throws InjectionException if conversion fails
     */
    protected <T> T convertObject(Class<T> targetClass, Object value)
    {
        Class<?> destinationClass =
                targetClass.isPrimitive() ? ClassUtils
                        .primitiveToWrapper(targetClass) : targetClass;
        Object result;

        try
        {
            result = getConvertBean().convert(value, destinationClass);
        }
        catch (ConversionException cex)
        {
            throw new InjectionException("Error when converting '" + value
                    + "' to class " + targetClass.getName(), cex);
        }

        if (!destinationClass.isInstance(result))
        {
            throw new InjectionException("Cannot convert value '" + value
                    + "' to class " + targetClass.getName());
        }

        // because of previous checks result is of type targetClass or of a
        // corresponding primitive wrapper class
        @SuppressWarnings("unchecked")
        T castResult = (T) result;
        return castResult;
    }

    /**
     * Helper method for checking whether the target class is specified. Throws
     * an exception if not.
     *
     * @param targetClass the target class to be checked
     */
    static void checkTargetClass(Class<?> targetClass)
    {
        if (targetClass == null)
        {
            throw new IllegalArgumentException("Target class must not be null!");
        }
    }

    /**
     * Registers default converters. This method is called by the constructor.
     */
    private void registerDefaultConverters()
    {
        registerBaseClassConverter(EnumConverter.getInstance(), Enum.class);
    }

    /**
     * Helper method for verifying parameters for registering converters. This
     * implementation throws an exception if the parameters are invalid.
     *
     * @param conv the converter to be registered
     * @param targetClass the target class
     * @throws IllegalArgumentException if the arguments are invalid
     */
    private static void checkRegisterConverterArgs(Converter conv,
            Class<?> targetClass)
    {
        if (conv == null)
        {
            throw new IllegalArgumentException("Converter must not be null!");
        }
        checkTargetClass(targetClass);
    }

    /**
     * A specialized implementation of {@code ConvertUtilsBean}. This
     * implementation handles base class converters, i.e. converters that can
     * deal with a whole class hierarchy rather than a specific target class.
     */
    private class CustomConverterBean extends ConvertUtilsBean
    {
        /**
         * Searches for a converter that can handle the specified class. This
         * implementation supports base class converters. It first delegates to
         * the inherited method. If a suitable converter is found, it is
         * returned. Otherwise, the registered base class converters are
         * checked. The first one found whose base class is a super class of the
         * specified class is returned.
         *
         * @param clazz the target class of the conversion
         * @return a converter that can handle this class or <b>null</b> if none
         *         is found
         */
        @Override
        public Converter lookup(@SuppressWarnings("rawtypes") Class clazz)
        {
            Converter conv = super.lookup(clazz);

            if (conv == null)
            {
                synchronized (baseClassConverters)
                {
                    for (ConverterData cd : baseClassConverters)
                    {
                        if (cd.canHandleConverter(clazz))
                        {
                            conv = cd.getConverter();
                            break;
                        }
                    }
                }
            }

            if (conv == null && getParent() != null)
            {
                conv = getParent().getConvertBean().lookup(clazz);
            }

            return conv;
        }
    }

    /**
     * A simple data class for storing information about a converter and its
     * target class.
     */
    private static class ConverterData
    {
        /** The converter. */
        private final Converter converter;

        /** The target class. */
        private Class<?> targetClass;

        /**
         * Creates a new instance of {@code ConverterData} and initializes it.
         *
         * @param conv the converter
         * @param cls the target class of the converter
         */
        public ConverterData(Converter conv, Class<?> cls)
        {
            converter = conv;
            targetClass = cls;
        }

        /**
         * Returns the converter.
         *
         * @return the converter
         */
        public Converter getConverter()
        {
            return converter;
        }

        /**
         * Tests whether this converter can handle a conversion to the given
         * target class.
         *
         * @param clazz the desired target class of the conversion
         * @return a flag whether this conversion is supported
         */
        public boolean canHandleConverter(Class<?> clazz)
        {
            return ClassUtils.isAssignable(clazz, targetClass);
        }
    }
}
