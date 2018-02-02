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
package net.sf.jguiraffe.gui.builder.components.tags;

import java.util.HashMap;
import java.util.Map;

import net.sf.jguiraffe.di.BeanContext;

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.MissingAttributeException;
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.jelly.tags.core.UseBeanTag;

/**
 * <p>
 * A tag handler base class for tags that deal with the creation of service
 * beans.
 * </p>
 * <p>
 * The purpose of this tag is to make beans available to the currently running
 * builder script. Beans can either be newly created, fetched from existing
 * variables in the Jelly context or obtained from the current
 * {@link BeanContext} (basically allowing builder scripts access to the
 * <em>dependency injection</em> framework). The beans fetched in one of these
 * ways can then be stored in variables in the Jelly context and/or passed to
 * parent tags.
 * </p>
 * <p>
 * This tag handler base class is a thin wrapper around Jelly's core
 * <code>&lt;useBean&gt;</code> tag. From its super class it inherits the
 * ability of creating and initializing new beans. This should only be used for
 * very simple beans, which do not need a sophisticated initialization. For
 * complex objects to be created the dependency injection framework should be
 * used.
 * </p>
 * <p>
 * After the resulting bean has been obtained the <code>passResults()</code>
 * method is invoked. This method is intended to perform some post processing
 * and eventually pass the result bean to an enclosing tag. The base
 * implementation provided by this class is left empty.
 * </p>
 * <p>
 * It is possible to define a base class; the tag handler class will then check
 * whether the involved bean is an instance of this class and throw a
 * <code>JellyTagException</code> if this is not the case.
 * </p>
 * <p>
 * The typical attributes for conditional execution of tags (<code>ifName</code>
 * and <code>unlessName</code>) are supported. Further attributes can be
 * specified to initialize the bean to be created/accessed.
 * </p>
 * <p>
 * The following attributes are supported by this tag:
 * <table border="1">
 * <tr>
 * <th>Attribute</th>
 * <th>Description</th>
 * <th>Optional</th>
 * </tr>
 * <tr>
 * <td valign="top">class</td>
 * <td>Defines the class of the bean to be created by this tag. This is the same
 * as used for the original Jelly <code>UseBean</code> base tag. If this
 * attribute is defined, arbitrary other attributes can be set defining
 * properties of the new bean.</td>
 * <td rowspan="4">Exactly one of these</td>
 * </tr>
 * <tr>
 * <td valign="top">ref</td>
 * <td>This attribute can be used for accessing a bean from the Jelly context
 * (that way service beans can be shared between tags).</td>
 * </tr>
 * <tr>
 * <td valign="top">beanName</td>
 * <td>Here the name of a bean managed by the dependency injection framework can
 * be specified. The bean will be looked up and obtained from the current
 * <code>BeanContext</code>.</td>
 * </tr>
 * <tr>
 * <td valign="top">beanClass</td>
 * <td>The same as <code>beanName</code>, except that the bean is looked up in
 * the current <code>BeanContext</code> using the class specified here.</td>
 * </tr>
 * <tr>
 * <td valign="top">var</td>
 * <td>Here the name of a variable in the Jelly context can be specified, which
 * will be assigned the involved bean. It is then available for other parts of
 * the running script.</td>
 * <td>Yes</td>
 * </tr>
 * </table>
 * </p>
 * <p>
 * This tag also implements the {@link PropertySupport} interface. This means
 * that property tags can appear in its body. The properties defined by these
 * tags will be set on the bean created by the tag. Together with the
 * {@link LocalizedTag} tag handler class it is possible to use resources for
 * setting properties.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: UseBeanBaseTag.java 205 2012-01-29 18:29:57Z oheger $
 */
public class UseBeanBaseTag extends UseBeanTag implements ConditionalTag,
        PropertySupport
{
    /** Constant for the name of the ifName attribute. */
    protected static final String ATTR_IF_NAME = "ifName";

    /** Constant for the name of the unlessName attribute. */
    protected static final String ATTR_UNLESS_NAME = "unlessName";

    /** Constant for the name of the ref attribute. */
    protected static final String ATTR_REF = "ref";

    /** Constant for the name of the class attribute. */
    protected static final String ATTR_CLASS = "class";

    /** Constant for the name of the var attribute. */
    protected static final String ATTR_VAR = "var";

    /** Constant for the name of the beanName attribute. */
    protected static final String ATTR_BEAN_NAME = "beanName";

    /** Constant for the name of the beanClass attribute. */
    protected static final String ATTR_BEAN_CLASS = "beanClass";

    /** An array with the names of the attributes used for defining the bean. */
    private static final String[] BEAN_DEFINITION_ATTRS = {
            ATTR_BEAN_CLASS, ATTR_BEAN_NAME, ATTR_CLASS, ATTR_REF
    };

    /**
     * An array with the names of the attributes that refer to an existing bean.
     */
    private static final String[] REFERENCE_ATTRS = {
            ATTR_BEAN_NAME, ATTR_BEAN_CLASS, ATTR_REF
    };

    /** Stores the base class of the bean. */
    private Class<?> baseClass;

    /** A map with additional properties to be set. */
    private Map<String, Object> additionalProperties;

    /**
     * Creates a new instance of <code>UseBeanBaseTag</code>.
     */
    public UseBeanBaseTag()
    {
        super();
        initReservedAttributes();
    }

    /**
     * Creates an instance of <code>UseBeanBaseTag</code> and sets the default
     * class for newly created beans. This class will be used when the
     * <code>class</code> attribute is missing.
     *
     * @param defaultClass the default class
     */
    public UseBeanBaseTag(Class<?> defaultClass)
    {
        this(defaultClass, null);
    }

    /**
     * Creates an instance of <code>UseBeanBaseTag</code> and sets the default
     * class for newly created beans and the base class for new beans as well.
     *
     * @param defaultClass the default class; this class will be used if the
     * <code>class</code> attribute is not specified
     * @param baseClass the base class for the obtained beans; if defined, the
     * tag will check if the bean is an instance of this class
     */
    public UseBeanBaseTag(Class<?> defaultClass, Class<?> baseClass)
    {
        super(defaultClass);
        initReservedAttributes();
        setBaseClass(baseClass);
    }

    /**
     * Initializes the reserved attributes.
     */
    private void initReservedAttributes()
    {
        addIgnoreProperty(ATTR_IF_NAME);
        addIgnoreProperty(ATTR_REF);
        addIgnoreProperty(ATTR_UNLESS_NAME);
        addIgnoreProperty(ATTR_BEAN_CLASS);
        addIgnoreProperty(ATTR_BEAN_NAME);
    }

    /**
     * Returns the bean's base class.
     *
     * @return the base class of the bean
     */
    public Class<?> getBaseClass()
    {
        return baseClass;
    }

    /**
     * Sets the bean's base class. If this class is set, the tag will ensure
     * whether the obtained bean is an instance of this class.
     *
     * @param baseClass the bean's base class
     */
    public void setBaseClass(Class<?> baseClass)
    {
        this.baseClass = baseClass;
    }

    /**
     * Returns the value of the ifName attribute.
     *
     * @return the ifName attribute
     */
    public String getIfName()
    {
        return (String) getAttributes().get(ATTR_IF_NAME);
    }

    /**
     * Returns the value of the unlessName attribute.
     *
     * @return the unlessName attribute
     */
    public String getUnlessName()
    {
        return (String) getAttributes().get(ATTR_UNLESS_NAME);
    }

    /**
     * Sets a property. This method can be called by tags in the body. The
     * properties added through this method will be set on the bean created by
     * this tag.
     *
     * @param name the name of the property
     * @param value the value of the property
     */
    public void setProperty(String name, Object value)
    {
        if (additionalProperties == null)
        {
            additionalProperties = new HashMap<String, Object>();
        }
        additionalProperties.put(name, value);
    }

    /**
     * Executes this tag. Tests if execution is allowed and whether a new bean
     * should be created or an existing should be fetched from the context.
     *
     * @param output the output object
     * @throws JellyTagException if an error occurs
     */
    @Override
    public void doTag(XMLOutput output) throws JellyTagException
    {
        if (FormBaseTag.canProcess(this))
        {
            checkBeanDefinition();
            if (isReference())
            {
                useExistingBean(output);
            }
            else
            {
                if (isOptional() && getAttributes().get(ATTR_CLASS) == null)
                {
                    // an optional tag, which does not create a bean
                    passResults(null);
                }
                else
                {
                    // let the base implementation create a new instance
                    super.doTag(output);
                }
            }
        }
    }

    /**
     * Processes the newly created bean. This method is called when a new bean
     * instance was created. This implementation invokes the
     * <code>passResults()</code> method after correct bean processing.
     *
     * @param var the value of the var attribute
     * @param bean the newly created bean
     * @throws JellyTagException if an error occurs
     */
    @Override
    protected void processBean(String var, Object bean)
            throws JellyTagException
    {
        checkBeanClass(bean);
        super.processBean(var, bean);
        callPassResults(bean, var);
    }

    /**
     * This method gets called if an existing bean should be used rather than
     * creating a new one. This implementation tries to obtain the bean either
     * from the jelly context or the current bean context.
     *
     * @param output the current output object
     * @throws JellyTagException if the bean cannot be found
     */
    protected void useExistingBean(XMLOutput output) throws JellyTagException
    {
        Object bean = fetchBean();
        if (bean == null)
        {
            throw new JellyTagException("Bean cannot be resolved!");
        }
        checkBeanClass(bean);

        // invoke the tag's body; this may impact some of the properties
        invokeBody(output);

        String var = (String) getAttributes().get(ATTR_VAR);
        super.processBean(var, bean);
        callPassResults(bean, var);
    }

    /**
     * Obtains the bean for this tag. This method is called when one of the
     * reference attributes is set.
     *
     * @return the referenced bean (can be <b>null</b>, then an exception will
     * be thrown in later processing
     */
    protected Object fetchBean()
    {
        if (isAttributeDefined(ATTR_REF))
        {
            return getContext().findVariable(getStringAttr(ATTR_REF));
        }

        else
        {
            BeanContext bc = FormBaseTag.getBuilderData(getContext())
                    .getBeanContext();
            if (isAttributeDefined(ATTR_BEAN_CLASS))
            {
                Class<?> beanClass = FormBaseTag.convertToClass(getAttributes()
                        .get(ATTR_BEAN_CLASS));
                return bc.getBean(beanClass);
            }
            else
            {
                return bc.getBean(getStringAttr(ATTR_BEAN_NAME));
            }
        }
    }

    /**
     * This method is called after the result bean is fully initialized. Sub
     * classes can perform arbitrary post processing. The return value indicates
     * whether the bean could be passed to a target object. If here <b>false</b>
     * is returned and no <code>var</code> attribute is specified, the tag
     * will throw an exception (because the bean is obviously of no use). This
     * base implementation is empty and only returns <b>true</b>.
     *
     * @param bean the bean to be passed
     * @return a flag whether the bean could be passed to a target
     * @throws JellyTagException if an error occurs
     */
    protected boolean passResults(Object bean) throws JellyTagException
    {
        return true;
    }

    /**
     * This method can be overridden in a derived class to indicate that the
     * bean creation operation is optional. If no <code>class</code> and no
     * <code>ref</code> attribute are provided and this method returns <b>true</b>,
     * no error is issued, but <code>passResults()</code> is called with a
     * <b>null</b> argument. The default implementation of this method returns
     * always <b>false</b>.
     *
     * @return a flag whether the bean creation operation is optional
     */
    protected boolean isOptional()
    {
        return false;
    }

    /**
     * Returns additional properties that have been added by tags in the body.
     * These properties will also be set on the current bean. The result may be
     * <b>null</b> if no additional properties have been added.
     *
     * @return a map with additional properties
     */
    protected Map<String, Object> getAdditionalProperties()
    {
        return additionalProperties;
    }

    /**
     * Helper method for returning an attribute as string. If the attribute is
     * present, its string representation is returned. Otherwise result is
     * <b>null</b>.
     *
     * @param attr the name of the attribute
     * @return the attribute value
     */
    protected String getAttributeStr(String attr)
    {
        Object res = getAttributes().get(attr);
        return (res != null) ? res.toString() : null;
    }

    /**
     * A convenience method for adding multiple properties that should be
     * ignored. This implementation calls {@code addIgnoreProperty()} for each
     * property name passed in.
     *
     * @param props the names of the properties to be ignored
     */
    protected void addIgnoreProperties(String... props)
    {
        for (String p : props)
        {
            addIgnoreProperty(p);
        }
    }

    /**
     * Checks whether the created bean is of the expected base class. Throws an
     * exception otherwise.
     *
     * @param bean the bean to be tested
     * @throws JellyTagException if the bean is not of the base class set for
     * this tag handler class
     */
    private void checkBeanClass(Object bean) throws JellyTagException
    {
        if (getBaseClass() != null
                && !getBaseClass().isAssignableFrom(bean.getClass()))
        {
            throw new JellyTagException("Bean is not of expected base class: "
                    + getBaseClass().getName());
        }
    }

    /**
     * Checks whether the definition of the bean is consistent, i.e. exactly one
     * way of specifying the target bean is used.
     *
     * @throws JellyTagException if the tag is used incorrectly
     */
    private void checkBeanDefinition() throws JellyTagException
    {
        int count = countAttributes(BEAN_DEFINITION_ATTRS);

        if (count > 1)
        {
            throw new JellyTagException(
                    "Exactly one of the attributes must be used: "
                            + "class, ref, beanName, beanClass!");
        }
        if (count < 1 && !isOptional() && getDefaultClass() == null)
        {
            throw new MissingAttributeException(ATTR_CLASS);
        }
    }

    /**
     * Checks whether this tag refers to an existing bean. This means that no
     * bean instance is to be created, but a context must be asked for obtaining
     * the resulting bean.
     *
     * @return a flag whether this is a reference tag
     */
    private boolean isReference()
    {
        return countAttributes(REFERENCE_ATTRS) > 0;
    }

    /**
     * Iterates over the specified array and counts the defined attributes.
     *
     * @param attrs the array with the attribute names
     * @return the number of defined attributes
     */
    private int countAttributes(String[] attrs)
    {
        int count = 0;
        for (String s : attrs)
        {
            if (isAttributeDefined(s))
            {
                count++;
            }
        }
        return count;
    }

    /**
     * Tests whether the specified attribute is defined.
     *
     * @param attrName the name of the attribute
     * @return a flag whether this attribute is defined
     */
    private boolean isAttributeDefined(String attrName)
    {
        return getAttributes().get(attrName) != null;
    }

    /**
     * Helper method for returning the value of the specified attribute.n
     *
     * @param attrName the attribute name
     * @return the (string) value of this attribute
     */
    private String getStringAttr(String attrName)
    {
        return String.valueOf(getAttributes().get(attrName));
    }

    /**
     * Invokes the <code>passResults()</code> method. If this method returns
     * <b>false</b> and no <code>var</code> attribute is specified, an
     * exception will be thrown.
     *
     * @param bean the bean
     * @param var the value of the <code>var</code> attribute
     * @throws JellyTagException if an error occurs or no target is defined
     */
    private void callPassResults(Object bean, String var)
            throws JellyTagException
    {
        handleAdditionalProperties(bean);

        if (!passResults(bean))
        {
            if (var == null)
            {
                // neither passResult() found a target nor a variable is defined
                // so throw an exception
                throw new JellyTagException("Bean cannot be assigned a target!");
            }
        }
    }

    /**
     * Sets additional properties on the current bean.
     *
     * @param bean the current bean
     * @throws JellyTagException if an error occurs
     */
    private void handleAdditionalProperties(Object bean)
            throws JellyTagException
    {
        if (getAdditionalProperties() != null)
        {
            setBeanProperties(bean, getAdditionalProperties());
        }
    }
}
