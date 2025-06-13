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
package net.sf.jguiraffe.di.impl.providers;

import net.sf.jguiraffe.di.BeanProvider;
import net.sf.jguiraffe.di.DependencyProvider;
import net.sf.jguiraffe.di.impl.Invokable;

/**
 * <p>
 * A specialized life-cycle supporting <code>{@link BeanProvider}</code>
 * implementation for creating new bean instances on each invocation.
 * </p>
 * <p>
 * Each time an instance of this class is invoked in a transaction a new
 * instance of the managed bean class will be created. The implementation
 * ensures that reentrant calls in a transaction always return the same bean
 * instance, so in each transaction exactly one bean is created.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: FactoryBeanProvider.java 205 2012-01-29 18:29:57Z oheger $
 */
public class FactoryBeanProvider extends LifeCycleBeanProvider
{
    /**
     * Creates a new instance of <code>FactoryBeanProvider</code> and
     * initializes it with the bean provider for creating a bean instance and
     * the invocation object for performing initialization.
     *
     * @param createProvider the bean provider used for creating a new bean
     * instance (must not be <b>null</b>)
     * @param initinv the (optional) invocation object for performing
     * initialization
     * @throws IllegalArgumentException if the bean provider is undefined
     */
    public FactoryBeanProvider(BeanProvider createProvider, Invokable initinv)
    {
        super(createProvider, initinv);
    }

    /**
     * Creates a new instance of <code>FactoryBeanProvider</code> and
     * initializes it with the bean provider for creating a bean instance.
     *
     * @param createProvider the bean provider used for creating a new bean
     * instance (must not be <b>null</b>)
     * @throws IllegalArgumentException if the bean provider is undefined
     */
    public FactoryBeanProvider(BeanProvider createProvider)
    {
        super(createProvider);
    }

    /**
     * Returns the bean managed by this bean provider. This implementation will
     * return a new bean instance for each transaction.
     *
     * @param dependencyProvider the dependency provider
     * @return the bean managed by this provider
     */
    public Object getBean(DependencyProvider dependencyProvider)
    {
        return fetchBean(dependencyProvider);
    }

    /**
     * Sets the ID of the locking transaction. This implementation resets the
     * managed bean when a new transaction starts. This has the effect that a
     * new bean instance will be created for this transaction.
     *
     * @param lid the ID of the locking transaction
     */
    @Override
    public void setLockID(Long lid)
    {
        super.setLockID(lid);
        if (lid != null)
        {
            resetBean();
        }
    }
}
