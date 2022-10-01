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

import java.util.ArrayList;
import java.util.List;

import net.sf.jguiraffe.di.Dependency;
import net.sf.jguiraffe.di.impl.ClassDescription;
import net.sf.jguiraffe.di.impl.Invokable;

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.TagSupport;
import org.apache.commons.jelly.XMLOutput;

/**
 * <p>
 * A base class for tag handler classes defining invocation objects.
 * </p>
 * <p>
 * This abstract base class provides common functionality for the creation and
 * initialization of invocation objects. It is especially capable of dealing
 * with the parameters of the invocation. Concrete sub classes only have to take
 * care that the correct invocation instance is created, e.g. a method or a
 * constructor invocation.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: InvocationTag.java 205 2012-01-29 18:29:57Z oheger $
 */
public abstract class InvocationTag extends TagSupport
{
    /** A list with the parameter tags found in the body of this tag. */
    private List<ParameterTag> parameterTags;

    /** An array with the data types of the parameters. */
    private ClassDescription[] parameterTypes;

    /** An array with the dependencies of the parameters. */
    private Dependency[] parameterDependencies;

    /** A class description for the target class of the invocation. */
    private ClassDescription targetClass;

    /** A data object for defining the target class of this invocation. */
    private ClassDescData targetClassData;

    /** Stores the name of the result variable. */
    private String result;

    /**
     * Creates a new instance of <code>InvocationTag</code>.
     */
    protected InvocationTag()
    {
        targetClassData = new ClassDescData();
        parameterTags = new ArrayList<ParameterTag>();
    }

    /**
     * Returns the name of the result variable. This variable can be set if this
     * invocation tag belongs to a <code>ChainedInvocation</code>.
     *
     * @return the name of the result variable
     */
    public String getResult()
    {
        return result;
    }

    /**
     * Set method of the result attribute.
     *
     * @param result the attribute's value
     */
    public void setResult(String result)
    {
        this.result = result;
    }

    /**
     * Returns the name of the source variable. This variable can be set if this
     * invocation tag belongs to a <code>ChainedInvocation</code>. This base
     * implementation always returns <b>null</b>. Derived classes can overwrite
     * it if they support the <code>source</code> attribute.
     *
     * @return the name of the source variable
     */
    public String getSource()
    {
        return null;
    }

    /**
     * Returns the target class data object.
     *
     * @return the data object for defining the target class
     */
    public ClassDescData getTargetClassData()
    {
        return targetClassData;
    }

    /**
     * Returns the class description for the target class of this invocation.
     * Depending on a concrete sub class this description may or may not be
     * required.
     *
     * @return the class description for the target class
     */
    public ClassDescription getTargetClassDescription()
    {
        return targetClass;
    }

    /**
     * Set method of the targetClass attribute.
     *
     * @param c the attribute's value
     */
    public void setTargetClass(Class<?> c)
    {
        getTargetClassData().setTargetClass(c);
    }

    /**
     * Set method of the targetClassName attribute.
     *
     * @param s the attribute's value
     */
    public void setTargetClassName(String s)
    {
        getTargetClassData().setTargetClassName(s);
    }

    /**
     * Set method of the targetClassLoader attribute.
     *
     * @param s the attribute's value
     */
    public void setTargetClassLoader(String s)
    {
        getTargetClassData().setClassLoaderName(s);
    }

    /**
     * The main method of this tag. Processes the parameters in the body of this
     * tag and then delegates to the <code>createInvocation()</code> method
     * for actually creating the invocation object.
     *
     * @param out the output object
     * @throws JellyTagException if the tag is incorrectly used or an error
     * occurs
     */
    public void doTag(XMLOutput out) throws JellyTagException
    {
        invokeBody(out);
        process();
    }

    /**
     * Adds a parameter to this invocation. This method is called by parameter
     * tags found in the body of this tag.
     *
     * @param paramTag the parameter tag to be added
     */
    public void addParameter(ParameterTag paramTag)
    {
        parameterTags.add(paramTag);
    }

    /**
     * Executes this tag. This method is invoked by <code>doTag()</code> after
     * the tag's body has been executed. It does the real work.
     *
     * @throws JellyTagException if the tag is incorrectly used or an error
     * occurs
     */
    protected void process() throws JellyTagException
    {
        if (getTargetClassData().isDefined())
        {
            targetClass = getTargetClassData().createClassDescription();
        }
        parameterTypes = new ClassDescription[parameterTags.size()];
        parameterDependencies = new Dependency[parameterTags.size()];
        for (int i = 0; i < parameterTypes.length; i++)
        {
            parameterTypes[i] = parameterTags.get(i).getParameterClassDesc();
            parameterDependencies[i] = parameterTags.get(i)
                    .getParameterDependency();
        }

        processInvokable(createInvocation());
    }

    /**
     * Processes the <code>Invokable</code> object after its creation. This
     * method will add it to the current <code>{@link InvokableSupport}</code>
     * object.
     *
     * @param inv the <code>Invokable</code> object
     * @throws JellyTagException if an error occurs
     */
    protected void processInvokable(Invokable inv) throws JellyTagException
    {
        InvocationData.get(getContext()).addInvokable(inv, getResult(),
                getSource());
    }

    /**
     * Returns an array with the data types of the parameters of this
     * invocation. This information is probably needed for creating the
     * invocation instance.
     *
     * @return an array with the data types of the parameters (some elements may
     * be <b>null</b> if they have not been defined)
     */
    protected ClassDescription[] getParameterTypes()
    {
        return parameterTypes;
    }

    /**
     * Returns an array with the dependencies of the this invocation's
     * parameters. This information is probably needed for creating the
     * invocation instance.
     *
     * @return an array with the dependencies of the parameters
     */
    protected Dependency[] getParameterDependencies()
    {
        return parameterDependencies;
    }

    /**
     * Creates the invocation object. Concrete sub classes must here return the
     * fully initialized invocation object.
     *
     * @return the newly created invocation object
     * @throws JellyTagException if an error occurs
     */
    protected abstract Invokable createInvocation() throws JellyTagException;
}
