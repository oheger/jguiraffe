/*
 * Copyright 2006-2025 The JGUIraffe Team.
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
package net.sf.jguiraffe.di.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.jguiraffe.di.BeanProvider;
import net.sf.jguiraffe.di.BeanStore;
import net.sf.jguiraffe.di.Dependency;
import net.sf.jguiraffe.di.DependencyProvider;
import net.sf.jguiraffe.di.InjectionException;

/**
 * <p>
 * A special implementation of the {@code Invokable} interface that
 * allows aggregating an arbitrary number of {@code Invokable} objects to
 * a kind of script.
 * </p>
 * <p>
 * The idea behind this class is that other objects implementing the
 * {@code Invokable} interface can be added. They can then be executed en
 * bloc. In addition to that a rudimentary support for variables is available:
 * the result of an invocation can be assigned to a named variable; later this
 * variable can be accessed again using a special
 * {@link Dependency}, which can be created by calling the
 * {@code getChainDependency()} method.
 * </p>
 * <p>
 * The typical life-cycle of an instance of this class is as follows:
 * <ol>
 * <li>An instance is created using the default constructor.</li>
 * <li>The {@code Invokable} objects to be executed are added using the
 * {@code addInvokable()} methods. Note that the order of these calls is
 * important; the added objects are invoked in exactly the same order.</li>
 * <li>After adding all {@code Invokable} objects the
 * {@code invoke()} method can be called. It triggers all contained
 * objects.</li>
 * </ol>
 * By making use of different invocation implementations, indeed a kind of
 * scripting can be achieved. For instance new objects can be created,
 * initialized (by invoking methods on them or setting properties), and assigned
 * to other objects. This is quite powerful, but can also become complex and
 * hard to debug.
 * </p>
 * <p>
 * This class provides some methods for accessing and manipulating local
 * variables. So it is possible to list all currently existing variables, query
 * their values, and even modify them. However these features are mainly
 * intended for debugging purposes rather than for implementing additional
 * scripting logic.
 * </p>
 * <p>
 * Note: This class is not thread-safe. It is intended to be created and
 * initialized by a single thread and then be passed to a complex bean provider
 * with initialization support. After that no more invocations should be added.
 * Because the local variables of a currently executed script are internally
 * stored, it is especially not possible to have multiple concurrent
 * invocations.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: ChainedInvocation.java 207 2012-02-09 07:30:13Z oheger $
 */
public class ChainedInvocation implements Invokable
{
    /** Stores a map with the local variables. */
    private final Map<String, Object> variables;

    /** A cache for the so far created chain dependencies. */
    private final Map<String, ChainDependency> chainDependencies;

    /** Stores a list with the contained {@code Invokable} objects. */
    private final List<ChainInvocationData> invokables;

    /** The name of the result variable. */
    private String resultVariableName;

    /** A flag whether the variables are to be cleared before an invocation. */
    private boolean clearVariables;

    /**
     * Creates a new instance of {@code ChainedInvocation}.
     */
    public ChainedInvocation()
    {
        variables = new HashMap<String, Object>();
        chainDependencies = new HashMap<String, ChainDependency>();
        invokables = new LinkedList<ChainInvocationData>();
        clearVariables = true;
    }

    /**
     * Adds the specified {@code Invokable} object to this object. It
     * will become part of the invocation sequence.
     *
     * @param inv the object to be added (must not be <b>null</b>)
     * @throws IllegalArgumentException if the passed in {@code Invokable}
     * object is <b>null</b>
     */
    public void addInvokable(Invokable inv)
    {
        addInvokable(inv, null, null);
    }

    /**
     * Adds the specified {@code Invokable} object to this object and
     * initializes its result variable. It will become part of the invocation
     * sequence. The result of its invocation will be stored in a local variable
     * with the given name.
     *
     * @param inv the object to be added (must not be <b>null</b>)
     * @param result the name of the result variable
     * @throws IllegalArgumentException if the passed in {@code Invokable}
     * object is <b>null</b>
     */
    public void addInvokable(Invokable inv, String result)
    {
        addInvokable(inv, result, null);
    }

    /**
     * Adds the specified {@code Invokable} object to this object and
     * initializes its result variable and its source object. It will become
     * part of the invocation sequence. The result of its invocation will be
     * stored in a local variable with the given name. The invocation is not
     * performed on the current target object, but on the object stored in the
     * local variable with the given source name. This makes it possible to
     * manipulate other objects than the current main target.
     *
     * @param inv the object to be added (must not be <b>null</b>)
     * @param result the name of the result variable
     * @param source the name of the variable, which contains the target object
     * for this invocation
     * @throws IllegalArgumentException if the passed in {@code Invokable}
     * object is <b>null</b>
     */
    public void addInvokable(Invokable inv, String result, String source)
    {
        if (inv == null)
        {
            throw new IllegalArgumentException("Invokable must not be null!");
        }
        invokables.add(new ChainInvocationData(inv, result, source));
    }

    /**
     * Returns a list with the {@code Invokable} objects that have
     * already been added to this chain. Manipulations of this list do not
     * affect this object. It is empty if nothing has been added yet.
     *
     * @return a list with the children of this chain
     */
    public List<Invokable> getInvokables()
    {
        List<Invokable> result = new ArrayList<Invokable>(invokables.size());
        for (ChainInvocationData cid : invokables)
        {
            result.add(cid.getInvokable());
        }
        return result;
    }

    /**
     * Returns the number of {@code Invokable} objects contained in this
     * chain.
     *
     * @return the size of this chain
     */
    public int size()
    {
        return invokables.size();
    }

    /**
     * Returns a special {@code Dependency} for a local variable that is
     * used during the execution of a {@code ChainedInvocation}. Local
     * variables are created by specifying result names for
     * {@code Invokable}s when they are added to the chain: the result
     * of this invocation will then be stored in a variable with this name. If
     * this variable later needs to be used for another invocation (e.g. as the
     * parameter of a method call), it can be accessed using such a dependency.
     *
     * @param name the name of the local variable to be accessed (must not be
     * <b>null</b>)
     * @return the dependency for accessing the specified local variable
     * @throws IllegalArgumentException if the name is <b>null</b>
     */
    public Dependency getChainDependency(String name)
    {
        return fetchVariableDependency(name);
    }

    /**
     * Returns a {@code BeanProvider} for the local variable with the given
     * name. Using this method, variables created during script execution can be
     * accessed as beans and thus can act as providers for other tags.
     *
     * @param name the name of the local variable to be accessed (must not be
     *        <b>null</b>)
     * @return a {@code BeanProvider} wrapping this local variable
     * @throws IllegalArgumentException if the name is <b>null</b>
     * @since 1.1
     */
    public BeanProvider getVariableBean(String name)
    {
        return fetchVariableDependency(name);
    }

    /**
     * Returns a set with the names of the currently defined local variables.
     * These names can be passed to the {@code getVariable()} method for
     * querying the current values of these variables.
     *
     * @return a set with the names of the currently existing local variables
     */
    public Set<String> getVariableNames()
    {
        return variables.keySet();
    }

    /**
     * Returns the name of the variable with the given name. If there is no
     * variable with this name, an exception is thrown. (We consider an access
     * to an undefined variable an error in the invocation chain.)
     *
     * @param name the name of the variable to be queried
     * @return the value of this variable
     * @throws InjectionException if the variable cannot be found
     */
    public Object getVariable(String name)
    {
        Object result = variables.get(name);
        if (result == null)
        {
            throw new InjectionException("Variable cannot be resolved: " + name);
        }
        return result;
    }

    /**
     * Sets the value of the specified variable. If the variable does not exist
     * yet, it is created. A value of <b>null</b> removes the variable.
     *
     * @param name the name of the variable (must not be <b>null</b>)
     * @param value the new value of the variable
     * @throws IllegalArgumentException if the name is <b>null</b>
     */
    public void setVariable(String name, Object value)
    {
        variables.put(name, value);
    }

    /**
     * Returns a flag whether all local variables are to be cleared for each new
     * invocation.
     *
     * @return the clear variables flag
     */
    public boolean isClearVariables()
    {
        return clearVariables;
    }

    /**
     * Sets the value of the {@code clear variables} flag. If this flag
     * is set to <b>true</b> (which is the default value), the storage for
     * local variables is cleared at the beginning of an invocation. This
     * ensures that values generated by earlier invocations do not affect the
     * current invocation. If variables have been set manually using the
     * {@code setVariable()} method, it will be necessary to disable this
     * flag; otherwise these variables will also get lost.
     *
     * @param clearVariables the new value of the flag
     */
    public void setClearVariables(boolean clearVariables)
    {
        this.clearVariables = clearVariables;
    }

    /**
     * Returns the name of the result variable.
     *
     * @return the name of the result variable
     * @since 1.1
     */
    public String getResultVariableName()
    {
        return resultVariableName;
    }

    /**
     * Sets the name of the result variable. If this property is set, the
     * {@code invoke()} method will not return the passed in target object, but
     * the object referenced by this variable. This is useful if the script
     * generates a result.
     *
     * @param resultVariableName the name of the result variable
     * @since 1.1
     */
    public void setResultVariableName(String resultVariableName)
    {
        this.resultVariableName = resultVariableName;
    }

    /**
     * Returns a list of the dependencies required for this invocation. This
     * implementation creates a union of the dependencies of all contained
     * {@code Invokable} objects.
     *
     * @return a list with the dependencies
     */
    public List<Dependency> getParameterDependencies()
    {
        Set<Dependency> unionDeps = new HashSet<Dependency>();
        for (ChainInvocationData cid : invokables)
        {
            unionDeps.addAll(cid.getInvokable().getParameterDependencies());
        }
        return new ArrayList<Dependency>(unionDeps);
    }

    /**
     * Performs the invocation represented by this class. This implementation
     * will invoke all contained {@code Invokable} objects.
     *
     * @param depProvider the dependency provider
     * @param target the target object
     * @return the result of the invocation
     * @throws InjectionException if an error occurs
     * @throws IllegalArgumentException if the dependency provider is <b>null</b>
     * or a required target object is undefined
     */
    public Object invoke(DependencyProvider depProvider, Object target)
    {
        if (isClearVariables())
        {
            variables.clear();
        }

        for (ChainInvocationData cid : invokables)
        {
            cid.performInvocation(depProvider, target);
        }
        return fetchScriptResult(target);
    }

    /**
     * Returns a string representation for this object. This implementation
     * outputs all contained {@code Invokable} objects.
     *
     * @return a string for this object
     */
    @Override
    public String toString()
    {
        final char cr = '\n';
        StringBuilder buf = new StringBuilder(getClass().getName());
        buf.append('@').append(System.identityHashCode(this));
        buf.append('[').append(cr);

        for (ChainInvocationData cid : invokables)
        {
            if (cid.resultName != null)
            {
                buf.append("(result=").append(cid.resultName).append(')');
            }
            if (cid.targetName != null)
            {
                buf.append("(source=").append(cid.targetName).append(')');
            }
            buf.append(cid.getInvokable()).append(cr);
        }

        return buf.toString();
    }

    /**
     * Returns the {@code ChainDependency} object for the specified variable.
     *
     * @param name the name of the variable (must not be <b>null</b>)
     * @return the corresponding {@code ChainDependency} object
     * @throws IllegalArgumentException if the variable name is <b>null</b>
     */
    private ChainDependency fetchVariableDependency(String name)
    {
        if (name == null)
        {
            throw new IllegalArgumentException(
                    "Variable name must not be null!");
        }

        ChainDependency cd = chainDependencies.get(name);
        if (cd == null)
        {
            cd = new ChainDependency(name);
            chainDependencies.put(name, cd);
        }
        return cd;
    }

    /**
     * Determines the result of a script execution. This is per default the
     * target object. If a result variable name has been specified, the value of
     * this variable is returned.
     *
     * @param target the target of the script execution
     * @return the result of the script execution
     * @throws InjectionException if the variable cannot be found
     */
    private Object fetchScriptResult(Object target)
    {
        return (getResultVariableName() != null) ? getVariable(getResultVariableName())
                : target;
    }

    /**
     * A data class for storing the contained invocation objects. Instances of
     * this class store all information required for executing an invocation.
     */
    private class ChainInvocationData
    {
        /** Stores the invokable object. */
        private Invokable invokable;

        /** Stores the name of the result variable if any. */
        private String resultName;

        /** Stores the name of the target variable for the invocation. */
        private String targetName;

        /**
         * Creates a new instance of {@code ChainInvocationData}.
         *
         * @param inv the wrapped {@code Invokable} object
         * @param result the name of the result variable
         * @param target the name of the target variable
         */
        public ChainInvocationData(Invokable inv, String result, String target)
        {
            invokable = inv;
            resultName = result;
            targetName = target;
        }

        /**
         * Returns the managed {@code Invokable} object.
         *
         * @return the internal {@code Invokable} object
         */
        public Invokable getInvokable()
        {
            return invokable;
        }

        /**
         * Performs the invocation. Calls the internally stored invocation
         * object. If a target name is set, this variable is resolved, and the
         * invocation is executed on this object; otherwise then provided target
         * object is used. If a result name is set, the result of the invocation
         * is stored in this variable.
         *
         * @param depProvider the dependency provider
         * @param target the default target object for this invocation
         * @throws InjectionException if the invocation causes an error
         */
        public void performInvocation(DependencyProvider depProvider,
                Object target)
        {
            Object t = (targetName != null) ? getVariable(targetName) : target;
            Object res = getInvokable().invoke(depProvider, t);
            if (resultName != null)
            {
                setVariable(resultName, res);
            }
        }
    }

    /**
     * An internally used {@code Dependency} implementation for accessing
     * local variables. Instances of this class simply store the name of the
     * desired local variable. The getBean() method is implemented by querying
     * the map with the local variables.
     */
    private class ChainDependency implements Dependency, BeanProvider
    {
        /** Stores the name of the desired variable. */
        private String varName;

        /**
         * Creates a new instance of {@code ChainDependency} for the
         * specified variable.
         *
         * @param var the name of the variable
         */
        public ChainDependency(String var)
        {
            varName = var;
        }

        /**
         * Resolves this dependency. This implementation ignores all parameters
         * and simply returns a pointer to itself. So the
         * {@code BeanProvider} implementation is used for querying the
         * managed bean.
         *
         * @param store the bean store
         * @param depProvider the dependency provider
         * @return the bean provider this dependency refers to
         */
        public BeanProvider resolve(BeanStore store,
                DependencyProvider depProvider)
        {
            return this;
        }

        /**
         * Returns the bean managed by this provider. This implementation
         * queries the local variable with the specified name.
         *
         * @param dependencyProvider the dependency provider (ignored)
         * @return the managed bean
         */
        public Object getBean(DependencyProvider dependencyProvider)
        {
            return getVariable(varName);
        }

        /**
         * Returns the class of the managed bean. This implementation evaluates
         * the managed object and obtains its class.
         *
         * @param dependencyProvider the dependency provider (ignored)
         * @return the class of the managed bean
         */
        public Class<?> getBeanClass(DependencyProvider dependencyProvider)
        {
            return getBean(dependencyProvider).getClass();
        }

        /**
         * Returns the dependencies of this bean provider. This implementation
         * always returns an empty set because there are no dependencies.
         *
         * @return a set with the dependencies
         */
        public Set<Dependency> getDependencies()
        {
            return Collections.emptySet();
        }

        /**
         * Returns the ID of the locking transaction. This implementation always
         * returns <b>null</b> because there is no life-cycle support.
         *
         * @return the ID of the locking transaction
         */
        public Long getLockID()
        {
            return null;
        }

        /**
         * Sets the ID of the locking transaction. This is just a dummy.
         *
         * @param lid the locking ID
         */
        public void setLockID(Long lid)
        {
        }

        /**
         * Returns a flag whether the managed bean is available. This is always
         * the case.
         *
         * @return a flag whether the managed bean can be accessed
         */
        public boolean isBeanAvailable()
        {
            return true;
        }

        /**
         * Notifies this bean provider that it is no more needed. This is just
         * an empty dummy implementation.
         *
         * @param depProvider the {@code DependencyProvider}
         */
        public void shutdown(DependencyProvider depProvider)
        {
        }
    }
}
