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
package net.sf.jguiraffe.gui.builder.components.tags;

import java.lang.reflect.ParameterizedType;
import java.util.Map;

import net.sf.jguiraffe.transform.TransformerContext;
import net.sf.jguiraffe.transform.TransformerContextPropertiesWrapper;

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.Tag;

/**
 * <p>
 * A base class for tags that create transformers or validators.
 * </p>
 * <p>
 * This class provides common functionality for creating service objects for
 * field handlers. It determines the <code>InputComponentTag</code> this tag
 * is nested inside and prepares the passing of the newly created transformer or
 * validator. Concrete sub classes only have to call the correct set method on
 * the input component tag.
 * </p>
 * <p>
 * The transformer or validator itself is created using the functionality
 * provided by the ancestor <code>UseBeanTag</code>. This results in a
 * reference to a bean. For this bean properties can be defined by tags
 * specified in the body of this tag. When the bean is invoked these properties
 * are then taken into account.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: TransformerBaseTag.java 205 2012-01-29 18:29:57Z oheger $
 * @param <T> the type of service object created by this tag
 */
public abstract class TransformerBaseTag<T> extends UseBeanBaseTag implements
        PropertiesSupport
{
    /** Stores properties for the created bean. */
    private Map<String, Object> properties;

    /**
     * Creates a new instance of <code>TransformerBaseTag</code>.
     */
    public TransformerBaseTag()
    {
        super();
    }

    /**
     * Creates a new instance of <code>TransformerBaseTag</code> and sets the
     * default class.
     *
     * @param defClass the default class
     */
    public TransformerBaseTag(Class<?> defClass)
    {
        super(defClass);
    }

    /**
     * Returns properties for the created bean.
     *
     * @return properties
     */
    public Map<String, Object> getProperties()
    {
        return properties;
    }

    /**
     * Sets properties for the created bean. It is possible to define properties
     * for a specific transformer or validator. These properties are stored and
     * are available through the <code>TransformerContext</code> interface.
     * Using this mechanism it is possible to customize the behavior of a
     * transformer or validator for a specific invocation.
     *
     * @param properties the properties for the current bean
     */
    public void setProperties(Map<String, Object> properties)
    {
        this.properties = properties;
    }

    /**
     * Returns a <code>TransformerContext</code> for the managed bean. If no
     * properties have been set, the global context is returned. Otherwise a
     * wrapped context is created that takes the properties into account.
     *
     * @return a <code>TransformerContext</code> to be used for the managed
     * bean
     */
    protected TransformerContext getTransformerContext()
    {
        TransformerContext globalCtx = FormBaseTag.getBuilderData(getContext())
                .getTransformerContext();
        assert globalCtx != null : "No transformer context set!";
        return (getProperties() != null) ? new TransformerContextPropertiesWrapper(
                globalCtx, getProperties())
                : globalCtx;
    }

    /**
     * Processes the parent tag. This method is called if the parent of this tag
     * is no <code>InputComponentTag</code>. This base implementation will
     * simply return <b>false</b> in this case (because per default only input
     * component tags are supported as parents). If a derived class supports
     * other parent tags, this method can be overridden.
     *
     * @param parent the parent tag
     * @param bean the bean created by this tag
     * @return a flag whether the parent tag is supported
     * @throws JellyTagException if an error occurs
     */
    protected boolean handleOtherParent(Tag parent, T bean)
            throws JellyTagException
    {
        return false;
    }

    /**
     * Passes the bean to its target. This implementation checks whether the
     * parent of this tag is an <code>InputComponentTag</code>. If yes,
     * <code>handleInputComponentTag()</code> will be called. Otherwise
     * <code>handleOtherParent()</code> is invoked.
     *
     * @param bean the affected bean
     * @return a flag whether the bean could be passed to a target
     * @throws JellyTagException if an error occurs
     */
    @Override
    @SuppressWarnings("unchecked")
    protected boolean passResults(Object bean) throws JellyTagException
    {
        Class<?> beanCls = findGenericType(getClass());
        if (!beanCls.isInstance(bean))
        {
            throw new JellyTagException("Invalid bean: " + bean
                    + ", not of expected class " + beanCls.getName());
        }

        if (getParent() instanceof InputComponentTag)
        {
            handleInputComponentTag((InputComponentTag) getParent(), (T) bean);
            return true;
        }
        else
        {
            return handleOtherParent(getParent(), (T) bean);
        }
    }

    /**
     * Passes the bean to the given <code>InputComponentTag</code>. This
     * method is called when this tag is placed inside the body of an
     * <code>InputComponentTag</code>. Derived classes can here perform the
     * necessary steps to initialize the input component tag.
     *
     * @param tag the input component tag
     * @param bean the bean
     * @throws JellyTagException if an error occurs
     */
    protected abstract void handleInputComponentTag(InputComponentTag tag,
            T bean) throws JellyTagException;

    /**
     * Determines the generic type for this class. This method is needed for
     * obtaining access to the generic parameter.
     *
     * @param baseClass the class to start with
     * @return the generic type
     */
    private Class<?> findGenericType(Class<?> baseClass)
    {
        return (Class<?>) ((ParameterizedType) getClass()
                .getGenericSuperclass()).getActualTypeArguments()[0];
    }
}
