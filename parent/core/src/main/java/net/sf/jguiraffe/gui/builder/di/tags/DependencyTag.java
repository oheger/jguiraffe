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
package net.sf.jguiraffe.gui.builder.di.tags;

import net.sf.jguiraffe.di.Dependency;
import net.sf.jguiraffe.di.impl.ClassDependency;
import net.sf.jguiraffe.di.impl.NameDependency;

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.TagSupport;

/**
 * <p>
 * A base class for tag handler classes that support the definition of a
 * dependency.
 * </p>
 * <p>
 * This base class supports multiple ways of defining a dependency to another
 * bean:
 * <ul>
 * <li>By specifying the name of the affected bean; this can be done using the
 * {@code refName} attribute.</li>
 * <li>By specifying the class of the required bean; for this purpose the
 * attributes {@code refClass}, {@code refClassName}, and
 * {@code refClassLoader} are available, which gather the information
 * needed for a {@link ClassDescData} object.</li>
 * <li>By specifying a constant value for the dependency; to achieve this the
 * constant value can be set using the {@code value} attribute.
 * Optionally the class of the value can be specified (to cause an automatic
 * conversion). For this use case the attributes {@code valueClass},
 * {@code valueClassName}, and {@code valueClassLoader} are
 * responsible, which again fill the properties of a
 * {@link ClassDescData}.</li>
 * <li>If this dependency is defined in the scope of a
 * {@code ChainedInvocation}, it is possible to create a dependency to a
 * local variable. Use the {@code var} attribute in this case.</li>
 * </ul>
 * </p>
 * <p>
 * The class checks whether the definition of the dependency is consistent. This
 * means that only a single way of specifying a dependency can be used.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: DependencyTag.java 207 2012-02-09 07:30:13Z oheger $
 */
public abstract class DependencyTag extends TagSupport implements ValueSupport
{
    /** Stores the name reference to a bean. */
    private String refName;

    /** Stores the name of a local variable. */
    private String var;

    /** Stores the data object for the value.*/
    private ValueData valueData;

    /** Stores the class description data for a class dependency. */
    private ClassDescData refClassData;

    /** Caches the dependency created by this class. */
    private Dependency dependency;

    /**
     * Creates a new instance of {@code DependencyTag}.
     */
    protected DependencyTag()
    {
        valueData = new ValueData(this);
        refClassData = new ClassDescData();
    }

    /**
     * Returns the data object with information about the tag's (constant)
     * value.
     *
     * @return the value data object
     */
    public ValueData getValueData()
    {
        return valueData;
    }

    /**
     * Returns the class description data for the specification of a class
     * dependency.
     *
     * @return the class description data for a class dependency
     */
    public ClassDescData getRefClassData()
    {
        return refClassData;
    }

    /**
     * Returns the name of the dependent bean.
     *
     * @return the name reference
     */
    public String getRefName()
    {
        return refName;
    }

    /**
     * Set method of the refName attribute.
     *
     * @param refName the attribute's value
     */
    public void setRefName(String refName)
    {
        this.refName = refName;
    }

    /**
     * Set method for the refClass attribute.
     *
     * @param c the attribute's value
     */
    public void setRefClass(Class<?> c)
    {
        getRefClassData().setTargetClass(c);
    }

    /**
     * Set method for the refClassName attribute.
     *
     * @param s the attribute's value
     */
    public void setRefClassName(String s)
    {
        getRefClassData().setTargetClassName(s);
    }

    /**
     * Set method for the refClassLoader attribute.
     *
     * @param s the attribute's value
     */
    public void setRefClassLoader(String s)
    {
        getRefClassData().setClassLoaderName(s);
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
     * Returns the name of the local variable to be used as dependency. This way
     * of defining a dependency is available only if the current dependency is
     * in the scope of a chained invocation.
     *
     * @return the local variable to be used as dependency
     */
    public String getVar()
    {
        return var;
    }

    /**
     * Set method of the var attribute.
     *
     * @param var the attribute's value
     */
    public void setVar(String var)
    {
        this.var = var;
    }

    /**
     * Evaluates the definitions of the dependency. The return value is number
     * of found definitions (for performing a validity check). If there is
     * exactly one definition, the dependency is created and stored by invoking
     * {@link #setDependency(Dependency)}.
     *
     * @return the number of found dependency definitions
     * @throws JellyTagException if the creation of the dependency causes an
     * error
     */
    protected int processDependencyDefinitions() throws JellyTagException
    {
        Dependency result = null;
        int count = 0;

        if (getValueData().isValueDefined())
        {
            count++;
            result = createValueDependency();
        }
        if (getRefName() != null)
        {
            count++;
            result = NameDependency.getInstance(getRefName());
        }
        if (getRefClassData().isDefined())
        {
            count++;
            result = ClassDependency.getInstance(getRefClassData()
                    .createClassDescription());
        }
        if (getVar() != null)
        {
            count++;
            InvocationData invData = InvocationData.get(getContext());
            result = invData.getVariableDependency(getVar());
        }

        if (count == 1)
        {
            setDependency(result);
        }
        return count;
    }

    /**
     * Creates the {@code Dependency} based on the values of the
     * attributes. This method also checks for consistency of the defined
     * properties and throws an exception if something is strange.
     *
     * @return the {@code Dependency} defined by this tag's attributes
     * @throws JellyTagException if the attributes are not consistent
     */
    protected Dependency createDependency() throws JellyTagException
    {
        int count = processDependencyDefinitions();

        if (count < 1)
        {
            throw new JellyTagException("No dependency defined!");
        }
        else if (count > 1)
        {
            throw new JellyTagException("Ambiguous dependency definition!");
        }
        else
        {
            return getDependency();
        }
    }

    /**
     * Returns the {@code Dependency} defined by this tag. If this
     * dependency has not yet been created, {@code createDependency()} is
     * called. Otherwise the existing {@code Dependency} object is
     * returned.
     *
     * @return the {@code Dependency} defined by this tag
     * @throws JellyTagException if the tag is incorrectly used or an error
     * occurred
     */
    protected Dependency getDependency() throws JellyTagException
    {
        if (dependency == null)
        {
            dependency = createDependency();
        }
        return dependency;
    }

    /**
     * Sets the internal dependency. This method is called when the tag's
     * dependency could be successfully created. It stores the dependency in an
     * internal field where it can be directly accessed from
     * {@code getDependency()}.
     *
     * @param dep the dependency to store
     */
    protected void setDependency(Dependency dep)
    {
        dependency = dep;
    }

    /**
     * Checks whether a dependency is defined by this tag.
     *
     * @return a flag whether a dependency is defined
     */
    protected boolean hasDependency()
    {
        if (dependency != null)
        {
            return true;
        }
        try
        {
            return processDependencyDefinitions() > 0;
        }
        catch (JellyTagException jtex)
        {
            return true;
        }
    }

    /**
     * Creates the dependency when a direct value is set. This method is called
     * by {@code processDependencyDefinitions()} when a value for this tag
     * is set. The base implementation delegates to the
     * {@code createValueDependency()} method of the {@code ValueData}
     * object.
     *
     * @return the dependency referring to the constant value
     * @throws JellyTagException if creation of the dependency causes an error
     */
    protected Dependency createValueDependency() throws JellyTagException
    {
        return getValueData().createValueProvider();
    }
}
