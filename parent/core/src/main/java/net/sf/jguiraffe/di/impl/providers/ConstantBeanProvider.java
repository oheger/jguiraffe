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
package net.sf.jguiraffe.di.impl.providers;

import net.sf.jguiraffe.di.BeanProvider;
import net.sf.jguiraffe.di.BeanStore;
import net.sf.jguiraffe.di.Dependency;
import net.sf.jguiraffe.di.DependencyProvider;

/**
 * <p>
 * A concrete implementation of the <code>BeanProvider</code> interface that
 * will always return the same bean instance.
 * </p>
 * <p>
 * When an instance of this class is created, the managed bean instance (and
 * optionally the target class if a type conversion is required) is passed in.
 * On its first call the {@link #getBean(DependencyProvider)} method checks
 * whether a type conversion if required. If this is the case, the conversion is
 * performed, and the resulting object is stored. Later invocations will then
 * always return this bean instance. The other methods defined by the
 * <code>BeanProvider</code> interface are implemented as dummies: There are no
 * dependencies, and synchronization support is not needed.
 * </p>
 * <p>
 * In addition to the <code>BeanProvider</code> interface, the
 * <code>Dependency</code> interface is also implemented. This makes it possible
 * to have static dependencies, which are always resolved to a
 * <code>BeanProvider</code> returning a constant bean. Dependencies of this
 * type may seem strange first, but they make sense in some cases. For instance
 * when a method is to be invoked, its parameters need to be defined. The
 * current parameter values may be dependencies to other beans in the current
 * bean store; so they need to be defined using dependencies. In simple cases
 * however constant values need to be passed (e.g. integers, flags, or string
 * values). For this purpose such a constant dependency can be used.
 * </p>
 * <p>
 * Instances of this class can be created using the static
 * <code>getInstance()</code> factory methods. In any case the constant value of
 * the dependency must be passed in. It is also possible to specify the class of
 * this dependency. In this case the class will try a type conversion as
 * described above.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: ConstantBeanProvider.java 205 2012-01-29 18:29:57Z oheger $
 */
public final class ConstantBeanProvider extends SimpleBeanProvider implements
        BeanProvider, Dependency
{
    /** Constant for a dependency that will be resolved to <b>null</b>. */
    public static final ConstantBeanProvider NULL = new ConstantBeanProvider(
            null, null);

    /** Stores the managed bean. */
    private final Object bean;

    /** Stores the class of the bean. */
    private final Class<?> beanClass;

    /** The converted bean. */
    private Object convertedBean;

    /**
     * Creates a new instance of {@code ConstantBeanProvider} and initializes it
     * with the bean to manage. The class of the bean can also be specified -
     * this is required if type conversion is needed. Clients use the static
     * factory method for creating new instances.
     *
     * @param conversionClass the class of the bean
     * @param obj the bean to be managed
     */
    private ConstantBeanProvider(Class<?> conversionClass, Object obj)
    {
        bean = obj;

        if (conversionClass != null)
        {
            beanClass = conversionClass;
        }
        else
        {
            convertedBean = obj;
            beanClass = (obj != null) ? obj.getClass() : Object.class;
        }
    }

    /**
     * Returns the bean managed by this provider. This is the same bean as was
     * passed to the constructor. No type conversion has been performed.
     *
     * @return the bean passed to the constructor
     */
    public Object getBean()
    {
        return bean;
    }

    /**
     * Returns the bean managed by this provider. This implementation operates
     * on the bean that was passed when this object was created. If necessary,
     * type conversion is performed (the result of this conversion is cached, so
     * that the conversion is only done on first access).
     *
     * @param dependencyProvider the dependency provider
     * @return the bean managed by this provider
     */
    public Object getBean(DependencyProvider dependencyProvider)
    {
        if (convertedBean == null && bean != null)
        {
            convertedBean =
                    dependencyProvider.getInvocationHelper()
                            .getConversionHelper().convert(beanClass, bean);
        }

        return convertedBean;
    }

    /**
     * Returns the class of the managed bean. If a class was passed on creation
     * time, it is directly returned. Otherwise, the class is obtained from the
     * managed bean. If the bean is <b>null</b>, the type
     * {@code java.lang.Object} is returned.
     *
     * @param dependencyProvider the dependency provider
     * @return the class of the managed bean
     */
    public Class<?> getBeanClass(DependencyProvider dependencyProvider)
    {
        return beanClass;
    }

    /**
     * Returns the <code>BeanProvider</code> this <code>Dependency</code> refers
     * to. This implementation simply returns the <b>this</b> pointer.
     *
     * @param store the bean store
     * @param depProvider the dependency provider
     * @return the {@code BeanProvider} this {@code Dependency} refers to
     */
    public BeanProvider resolve(BeanStore store, DependencyProvider depProvider)
    {
        return this;
    }

    /**
     * Returns a string representation of this object. This string will contain
     * the value of this bean provider.
     *
     * @return a string for this object
     */
    @Override
    public String toString()
    {
        StringBuilder buf = new StringBuilder(getClass().getName());
        buf.append(" [ value = ").append(getBean()).append(" ]");
        return buf.toString();
    }

    /**
     * Creates a new instance of this class and initializes it with the bean to
     * be managed.
     *
     * @param bean the bean to be managed
     * @return the new instance of this class
     */
    public static ConstantBeanProvider getInstance(Object bean)
    {
        return new ConstantBeanProvider(null, bean);
    }

    /**
     * Returns an instance of this class that refers to the specified value of
     * the given class. If necessary, a conversion will be performed to convert
     * the value to the given class.
     *
     * @param valueClass the class of this bean provider (can be <b>null</b>)
     * @param value the value of the managed bean
     * @return the instance representing this value
     * @throws IllegalArgumentException if a conversion is necessary, but cannot
     *         be performed
     */
    public static ConstantBeanProvider getInstance(Class<?> valueClass,
            Object value)
    {
        return new ConstantBeanProvider(valueClass, value);
    }
}
