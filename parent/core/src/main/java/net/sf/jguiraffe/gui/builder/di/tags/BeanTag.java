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
package net.sf.jguiraffe.gui.builder.di.tags;

import net.sf.jguiraffe.di.BeanProvider;
import net.sf.jguiraffe.di.DependencyProvider;
import net.sf.jguiraffe.di.impl.ChainedInvocation;
import net.sf.jguiraffe.di.impl.ClassDescription;
import net.sf.jguiraffe.di.impl.ConstructorInvocation;
import net.sf.jguiraffe.di.impl.Invokable;
import net.sf.jguiraffe.di.impl.MethodInvocation;
import net.sf.jguiraffe.di.impl.providers.ConstantBeanProvider;
import net.sf.jguiraffe.di.impl.providers.ConstructorBeanProvider;
import net.sf.jguiraffe.di.impl.providers.FactoryBeanProvider;
import net.sf.jguiraffe.di.impl.providers.SimpleBeanProvider;
import net.sf.jguiraffe.di.impl.providers.SingletonBeanProvider;

import org.apache.commons.jelly.JellyTagException;

/**
 * <p>
 * The main tag of the tag library for the <em>dependency injection</em>
 * framework: with this tag bean definitions can be created.
 * </p>
 * <p>
 * There are several ways of defining a bean and determining where it is stored:
 * <ul>
 * <li>The most simple form is to specify a constant value for the bean. This
 * value will then be directly returned by the constructed {@link BeanProvider}.
 *
 * <pre>
 * &lt;bean name=&quot;myBean&quot; value=&quot;42&quot;
 *   valueClassName=&quot;java.lang.Integer&quot;/&gt;
 * </pre>
 *
 * </li>
 * <li>With the {@code store} attribute the name of the
 * {@link net.sf.jguiraffe.di.BeanStore BeanStore}, in which the constructed
 * {@link BeanProvider} is stored, can be specified:
 *
 * <pre>
 * &lt;bean name=&quot;myBean&quot; value=&quot;42&quot;
 *   valueClassName=&quot;java.lang.Short&quot; store=&quot;numericBeans&quot;/&gt;
 * </pre>
 *
 * </li>
 * <li>If the bean tag is placed inside a {@link BeanStoreTag}, the bean
 * definition will be stored in this bean store:
 *
 * <pre>
 * &lt;store name=&quot;specialBeans&quot;&gt;
 *   &lt;bean name=&quot;myBean&quot; value=&quot;SpecialBean1&quot;/&gt;
 * &lt;/store&gt;
 * </pre>
 *
 * </li>
 * <li>If only the class of the managed bean is provided, the default
 * constructor will be called for creating an instance:
 *
 * <pre>
 * &lt;bean name=&quot;myBean&quot;
 *   beanClassName=&quot;com.mycompany.mypckg.MyBean&quot;/&gt;
 * </pre>
 *
 * </li>
 * <li>The {@code singleton} attribute has impact on the way the bean is
 * created. If set to <b>true</b> (which is the default), only a single bean
 * instance will be created on first access. Further access to this bean
 * definition will always return the same bean. A value of <b>false</b> in
 * contrast will create a new bean instance on each access:
 *
 * <pre>
 * &lt;bean name=&quot;myBean&quot; beanClassName=&quot;com.mycompany.mypckg.MyBean&quot;
 *   singleton=&quot;false&quot;/&gt;
 * </pre>
 *
 * </li>
 * <li>If a bean is a singleton, the {@code shutdownMethod} attribute can be
 * specified. Here the name of a method can be provided, which has to be called
 * by the framework when the bean is no longer needed (i.e. when the
 * {@code BeanStore} the {@code BeanProvider} is contained is closed):
 *
 * <pre>
 * &lt;bean name=&quot;myShutdownBean&quot;
 *   beanClassName=&quot;com.mycompany.mypckg.MyBean&quot;
 *   shutdownMethod=&quot;close&quot;/&gt;
 * </pre>
 *
 * The method defined by the {@code shutdownMethod} attribute must have no
 * parameters. If a more complex shutdown operation is desired or if multiple
 * methods need to be called, consider using the {@link ShutdownHandlerTag} in
 * the body of the bean tag. Note again that shutdown methods are only supported
 * for singleton beans. If each access to the bean creates a new instance, the
 * caller is responsible for releasing the bean instances correspondingly.</li>
 * <li>If a different constructor than the default one is to be used, a nested
 * {@link ConstructorTag} tag can be used. Here the parameters to
 * be passed to the constructor can be defined:
 *
 * <pre>
 * &lt;bean name=&quot;myBean&quot;
 *   beanClass=&quot;net.sf.jguiraffe.di.ReflectionTestClass&quot;&gt;
 *   &lt;constructor&gt;
 *     &lt;param refName=&quot;anotherBean&quot;/&gt;
 *     &lt;param value=&quot;42&quot; valueClass=&quot;java.lang.Integer&quot;
 *       parameterClass=&quot;java.lang.Integer&quot;/&gt;
 *   &lt;/constructor&gt;
 * &lt;/bean&gt;
 * </pre>
 *
 * </li>
 * <li>If the bean cannot be created directly, but a factory has to be used, a
 * nested {@link FactoryTag} is appropriate. This can look as
 * follows:
 *
 * <pre>
 * &lt;bean name=&quot;myBean&quot;
 *   beanClass=&quot;net.sf.jguiraffe.di.ReflectionTestClass&quot;&gt;
 *   &lt;factory&gt;
 *     &lt;methodInvocation method=&quot;newInstance&quot;
 *       targetClass=&quot;net.sf.jguiraffe.di.ReflectionTestClass&quot;&gt;
 *       &lt;param refName=&quot;anotherBean&quot;/&gt;
 *       &lt;param value=&quot;42&quot; valueClass=&quot;java.lang.Integer&quot;
 *         parameterClass=&quot;java.lang.Integer&quot;/&gt;
 *     &lt;/methodInvocation&gt;
 *   &lt;/factory&gt;
 * &lt;/bean&gt;
 * </pre>
 *
 * </li>
 * <li>It is also possible to use a {@code <bean>} tag everywhere a
 * dependency is expected. This allows defining beans in-line as in the
 * following example:
 *
 * <pre>
 * &lt;bean name=&quot;myBean&quot;
 *   beanClass=&quot;net.sf.jguiraffe.di.ReflectionTestClass&quot;&gt;
 *   &lt;constructor&gt;
 *     &lt;param&gt;
 *       &lt;bean beanClass=&quot;net.sf.jguiraffe.di.TestBean&quot;/&gt;
 *     &lt;/param&gt;
 *     &lt;param value=&quot;42&quot; valueClass=&quot;java.lang.Integer&quot;
 *       parameterClass=&quot;java.lang.Integer&quot;/&gt;
 *   &lt;/constructor&gt;
 * &lt;/bean&gt;
 * </pre>
 *
 * Here the value of the first {@code <param>} tag is the bean
 * defined by the nested {@code <bean>} tag. This is a bit similar to
 * anonymous inner classes in Java. The result is effectively the same as if the
 * bean was defined elsewhere with a specific name and the
 * {@code <param>} tag would reference this bean.</li>
 * <li>No matter of how the bean is created, in the tag's body an arbitrary
 * number of <em>invocation tags</em> can be placed. These are collected and
 * added to a {@link ChainedInvocation} object, so that they form
 * an initialization script. This script will be executed after the bean
 * instance has been created:
 *
 * <pre>
 * &lt;bean name=&quot;myBean&quot; beanClass=&quot;com.mypackage.MyBeanClass&quot;&gt;
 *   &lt;constructor&gt;
 *     &lt;param refName=&quot;anotherBean&quot;/&gt;
 *   &lt;/constructor&gt;
 *   &lt;methodInvocation method=&quot;initLocale&quot;&gt;
 *     &lt;param refClass=&quot;java.util.Locale&quot;/&gt;
 *   &lt;/methodInvocation&gt;
 *   &lt;setProperty property=&quot;count&quot; value=&quot;10&quot;/&gt;
 * &lt;/bean&gt;
 * </pre>
 *
 * </li>
 * </ul>
 * </p>
 * <p>
 * The following table lists all attributes supported by this tag:
 * </p>
 * <p>
 * <table border="1">
 * <tr>
 * <th>Attribute</th>
 * <th>Description</th>
 * <th>Optional</th>
 * </tr>
 * <tr>
 * <td valign="top">name</td>
 * <td>Defines a name for the created bean definition. Using this name the bean
 * can be queried from an bean context.</td>
 * <td valign="top">no</td>
 * </tr>
 * <tr>
 * <td valign="top">value</td>
 * <td>If the parameter is to be set to a constant value, this attribute can be
 * used. It allows to directly specify the value.</td>
 * <td valign="top">yes</td>
 * </tr>
 * <tr>
 * <td valign="top">valueClass</td>
 * <td>If a constant value is to be used for the parameter value, it may be
 * necessary to perform some type conversion. With this attribute the type of
 * the parameter can be specified. The value will then be converted to this
 * type.</td>
 * <td valign="top">yes</td>
 * </tr>
 * <tr>
 * <td valign="top">valueClassName</td>
 * <td>Like {@code valueClass}, but the name of the property's data type
 * class is specified.</td>
 * <td valign="top">yes</td>
 * </tr>
 * <tr>
 * <td valign="top">valueClassLoader</td>
 * <td>If the data type class of the value is specified by its name only, with
 * this attribute the class loader can be determined for resolving the class.</td>
 * <td valign="top">yes</td>
 * </tr>
 * <td valign="top">beanClass</td>
 * <td>With this attribute the class of the managed bean is determined.
 * Explicitly specifying the bean class is required in some cases. For instance
 * if no further creation parameters are provided, the bean will be created by
 * invoking the default constructor of this class.</td>
 * <td valign="top">yes</td> </tr>
 * <tr>
 * <td valign="top">beanClassName</td>
 * <td>Like the {@code beanClass} attribute, but the bean class is
 * specified by its name.</td>
 * <td valign="top">yes</td>
 * </tr>
 * <tr>
 * <td valign="top">beanClassLoader</td>
 * <td>If the bean class is specified by name, with this attribute the symbolic
 * name of a registered class loader can be provided for resolving the class.</td>
 * <td valign="top">yes</td>
 * </tr>
 * <tr>
 * <td valign="top">singleton</td>
 * <td>This attribute determines whether the defined bean is a singleton. If set
 * to <b>true</b> (which is the default value if the attribute is missing), only
 * a single instance will be created on first access; later access to this bean
 * will always return the same instance. A value of <b>false</b> will cause a
 * new bean instance to be created on each access.</td>
 * <td valign="top">yes</td>
 * </tr>
 * <tr>
 * <td valign="top">store</td>
 * <td>Here the name of the {@link net.sf.jguiraffe.di.BeanStore BeanStore}, in
 * which the created bean definition is to be put is defined. If the attribute
 * is missing and this tag is placed in the body of a {@link BeanStoreTag}, the
 * store defined by this tag will be used. If no bean store is defined at all,
 * the root bean store of the current builder operation is used.</td>
 * <td valign="top">yes</td>
 * </tr>
 * <tr>
 * <td valign="top">resultVar</td>
 * <td>This attribute provides an alternative way of defining a bean: as a result
 * variable of an initializer script. When the initializer script is executed
 * local variables can be created. The {@code resultVar} attribute instructs
 * the tag to use one of these variables as the bean to be returned. With this
 * mechanism, it is possible for instance to invoke a factory bean and return
 * the object created by it. Note that the {@code resultVar} attribute takes
 * precedence over other mechanisms to define the resulting bean.</td>
 * <td valign="top">yes</td>
 * </tr>
 * </table>
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: BeanTag.java 207 2012-02-09 07:30:13Z oheger $
 */
public class BeanTag extends AbstractBeanTag
{
    /** Constant for the string pattern for the place holder bean provider. */
    private static final String PLACE_HOLDER_PROVIDER_STR =
            "<Place holder for bean of class %s>";

    /** Stores the bean provider for creating the managed bean. */
    private BeanProvider beanCreator;

    /** Stores the data object for defining the value.*/
    private ValueData valueData;

    /** Stores an object for defining the bean's class. */
    private ClassDescData beanClassData;

    /** Stores the class description for the managed bean. */
    private ClassDescription beanClassDesc;

    /** Stores the chained invocation with the initializer script. */
    private ChainedInvocation initializerScript;

    /** Stores the shutdown handler.*/
    private Invokable shutdownHandler;

    /** Stores the name of the managed bean definition. */
    private String name;

    /** Stores the name of a shutdown method.*/
    private String shutdownMethod;

    /** Stores the singleton flag. */
    private boolean singleton;

    /**
     * Creates a new instance of {@code BeanTag}.
     */
    public BeanTag()
    {
        valueData = new ValueData(this);
        beanClassData = new ClassDescData();
        initializerScript = new ChainedInvocation();
        setSingleton(true);
    }

    /**
     * Returns the name of the managed bean definition.
     *
     * @return the name of the bean definition
     */
    @Override
    public String getName()
    {
        return name;
    }

    /**
     * Set method of the name attribute.
     *
     * @param name the attribute's name
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * Returns the class description for the managed bean.
     *
     * @return the class of the managed bean
     */
    public ClassDescription getBeanClassDesc()
    {
        return beanClassDesc;
    }

    /**
     * Sets the class description for the managed bean. Usually, this method
     * will be called by the main processing method when the tag's attributes
     * are evaluated. If the bean class is not defined by the tag's attributes,
     * it can be indirectly defined by the {@code BeanProvider} used as
     * creator.
     *
     * @param beanClass the class description for the managed bean
     */
    public void setBeanClassDesc(ClassDescription beanClass)
    {
        this.beanClassDesc = beanClass;
    }

    /**
     * Returns the {@code BeanProvider} that will be used for creating
     * the managed bean.
     *
     * @return the {@code BeanProvider} for creating the bean
     */
    public BeanProvider getBeanCreator()
    {
        return beanCreator;
    }

    /**
     * Sets the {@code BeanProvider} that will be used for creating the
     * managed bean.
     *
     * @param beanCreator the creation bean provider
     * @throws JellyTagException if a creator has already been set (only a
     * single bean creator is allowed)
     */
    public void setBeanCreator(BeanProvider beanCreator)
            throws JellyTagException
    {
        if (getBeanCreator() != null)
        {
            throw new JellyTagException(
                    "Multiple creator definitions are not allowed!");
        }
        this.beanCreator = beanCreator;
    }

    /**
     * Returns the data object with information about the value.
     *
     * @return the value data object
     */
    public ValueData getValueData()
    {
        return valueData;
    }

    /**
     * Returns a flag whether the created bean definition is for a singleton
     * bean.
     *
     * @return the singleton flag
     */
    public boolean isSingleton()
    {
        return singleton;
    }

    /**
     * Set method of the singleton attribute.
     *
     * @param singleton the attribute's value
     */
    public void setSingleton(boolean singleton)
    {
        this.singleton = singleton;
    }

    /**
     * Set method of the beanClass attribute.
     *
     * @param c the attribute's value
     */
    public void setBeanClass(Class<?> c)
    {
        getBeanClassData().setTargetClass(c);
    }

    /**
     * Set method of the beanClassName attribute.
     *
     * @param s the attribute's value
     */
    public void setBeanClassName(String s)
    {
        getBeanClassData().setTargetClassName(s);
    }

    /**
     * Set method of the beanClassLoader attribute.
     *
     * @param s the attribute's value
     */
    public void setBeanClassLoader(String s)
    {
        getBeanClassData().setClassLoaderName(s);
    }

    /**
     * Set method for the value attribute.
     *
     * @param value the attribute's value
     */
    public void setValue(Object value)
    {
        getValueData().setValue(value);
    }

    /**
     * Set method for the valueClass attribute.
     *
     * @param cls the attribute's value
     */
    public void setValueClass(Class<?> cls)
    {
        getValueData().setValueClass(cls);
    }

    /**
     * Set method for the valueClassName attribute.
     *
     * @param clsName the attribute's value
     */
    public void setValueClassName(String clsName)
    {
        getValueData().setValueClassName(clsName);
    }

    /**
     * Set method for the valueClassLoader attribute.
     *
     * @param loader the attribute's value
     */
    public void setValueClassLoader(String loader)
    {
        getValueData().setValueClassLoader(loader);
    }

    /**
     * Set method for the shutdownMethod attribute.
     *
     * @param s the attribute's value
     */
    public void setShutdownMethod(String s)
    {
        shutdownMethod = s;
    }

    /**
     * Returns the name of the result variable in the initializer script.
     *
     * @return the name of the result variable
     * @since 1.1
     */
    public String getResultVar()
    {
        return getInitializerScript().getResultVariableName();
    }

    /**
     * Set method of the {@code resultVar} attribute.
     *
     * @param resultVar the attribute's value
     * @since 1.1
     */
    public void setResultVar(String resultVar)
    {
        getInitializerScript().setResultVariableName(resultVar);
    }

    /**
     * Returns the shutdown handler.
     *
     * @return the shutdown handler
     */
    public Invokable getShutdownHandler()
    {
        return shutdownHandler;
    }

    /**
     * Sets a shutdown handler. This handler will be set at the {@code
     * BeanProvider} created by this tag if supported. This tag handler class
     * allows multiple ways of defining a shutdown handler, but for a concrete
     * tag instance only a single way can be used. Thus this method throws an
     * exception when called more than once. This method is expected to be
     * called by nested tags.
     *
     * @param inv the {@code Invokable} serving as shutdown handler
     * @throws JellyTagException if already a shutdown handler is defined and
     *         the parameter is not <b>null</b>
     */
    public void setShutdownHandler(Invokable inv) throws JellyTagException
    {
        if (getShutdownHandler() != null && inv != null)
        {
            throw new JellyTagException(
                    "Multiple shutdown handler definitions!");
        }

        shutdownHandler = inv;
    }

    /**
     * Performs pre-processing before the body of this tag is evaluated. This
     * method performs some initialization related to the value of this tag (if
     * a constant value is set) and prepares storing invocations for an
     * initializer script and a shutdown method.
     *
     * @throws JellyTagException if the tag is incorrectly used
     */
    @Override
    protected void processBeforeBody() throws JellyTagException
    {
        super.processBeforeBody();

        if (getValueData().isValueDefined())
        {
            setBeanCreator(getValueData().createValueProvider());
        }
        if (getBeanClassData().isDefined())
        {
            setBeanClassDesc(getBeanClassData().createClassDescription());
        }

        InvocationData.get(getContext()).registerInvokableSupport(
                initializerScript);

        if (shutdownMethod != null)
        {
            setShutdownHandler(createShutdownHandlerForMethod(shutdownMethod));
        }
    }

    /**
     * Processes this tag after its body has been evaluated. This implementation
     * performs some additional checks and cleanup at the end of this tag's
     * execution.
     *
     * @throws JellyTagException if the tag is incorrectly used
     */
    @Override
    protected void process() throws JellyTagException
    {
        handleResultVariable();
        if (getBeanCreator() == null && getBeanClassDesc() == null)
        {
            throw new JellyTagException("No bean creator defined!");
        }

        super.process();
        InvocationData.get(getContext()).unregisterInvokableSupport();

        // Check if the shutdown handler could be consumed
        if (getShutdownHandler() != null)
        {
            throw new JellyTagException(
                    "Bean provider does not support shutdown handling!");
        }
    }

    /**
     * Returns the {@code ChainedInvocation} for storing the initializer
     * script.
     *
     * @return the initializer script
     */
    protected ChainedInvocation getInitializerScript()
    {
        return initializerScript;
    }

    /**
     * Returns the data object with the definition of the bean class.
     *
     * @return the bean class data object
     */
    protected ClassDescData getBeanClassData()
    {
        return beanClassData;
    }

    /**
     * Returns the {@code Invokable} serving as a shutdown handler and resets
     * the field. Because not all variants of beans that can be created using
     * this tag support shutdown handlers, the handler is reset when it could be
     * processed. So the tag handler class can check whether a shutdown handler
     * was provided in a scenario where this is not allowed and throw an
     * exception. Note that only the first invocation of this method can return
     * a value; subsequent invocations return <b>null</b>.
     *
     * @return the shutdown handler if one is defined; <b>null</b> otherwise
     */
    protected Invokable consumeShutdownHandler()
    {
        Invokable handler = getShutdownHandler();
        shutdownHandler = null;
        return handler;
    }

    /**
     * Creates an {@code Invokable} for a shutdown handler for a shutdown
     * method. This method is called by {@link #processBeforeBody()} when the
     * {@code shutdownMethod} attribute is set. It returns an {@code Invokable},
     * which calls exactly this method.
     *
     * @param methodName the name of the shutdown method
     * @return the {@code Invokable} for the shutdown handler
     */
    protected Invokable createShutdownHandlerForMethod(String methodName)
    {
        return new MethodInvocation(methodName, null);
    }

    /**
     * Creates the bean provider defined by this tag. This method is invoked by
     * {@code process()} after validation of the attributes.
     *
     * @return the bean provider defined by this tag
     * @throws JellyTagException in case of an error
     */
    @Override
    protected BeanProvider createBeanProvider() throws JellyTagException
    {
        if (getValueData().isValueDefined())
        {
            // if a value is set and no initializer, return the value provider
            if (getInitializerScript().size() < 1)
            {
                assert getBeanCreator() instanceof ConstantBeanProvider : "Wrong creator set";
                return getBeanCreator();
            }
        }

        // if no creator has been set, use the default ctor of the bean class
        if (getBeanCreator() == null)
        {
            assert getBeanClassDesc() != null : "No bean creator and not class set!";
            setBeanCreator(new ConstructorBeanProvider(
                    new ConstructorInvocation(getBeanClassDesc(), null)));
        }

        Invokable initializer = (getInitializerScript().size() > 0) ? getInitializerScript()
                : null;
        return isSingleton() ? new SingletonBeanProvider(getBeanCreator(),
                initializer, consumeShutdownHandler())
                : new FactoryBeanProvider(getBeanCreator(), initializer);
    }

    /**
     * Evaluates the {@code resultVar} attribute. If it is set and no bean
     * creator is specified, a dummy bean creator is set now. We expect that the
     * bean is actually created by the initializer script, therefore no bean
     * creator is necessary.
     *
     * @throws JellyTagException if an error occurs
     */
    private void handleResultVariable() throws JellyTagException
    {
        if (getResultVar() != null && getBeanCreator() == null)
        {
            setBeanCreator((getBeanClassDesc() != null) ? createPlaceholderBeanCreator()
                    : ConstantBeanProvider.NULL);
        }
    }

    /**
     * Creates a bean creator acting as a place holder for a bean that is
     * created using a complex initializer script. A bean provider of this type
     * is used if a result variable and a class description are defined. It
     * returns a <b>null</b> bean, but provides information about the bean
     * class.
     *
     * @return a place holder {@code BeanProvider}
     */
    private BeanProvider createPlaceholderBeanCreator()
    {
        return new SimpleBeanProvider()
        {
            public Class<?> getBeanClass(DependencyProvider dependencyProvider)
            {
                return getBeanClassDesc().getTargetClass(dependencyProvider);
            }

            public Object getBean(DependencyProvider dependencyProvider)
            {
                return null;
            }

            @Override
            public String toString()
            {
                return String.format(PLACE_HOLDER_PROVIDER_STR,
                        getBeanClassDesc().toString());
            }
        };
    }
}
