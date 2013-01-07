/*
 * Copyright 2006-2013 The JGUIraffe Team.
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

import java.util.Set;

import net.sf.jguiraffe.di.BeanProvider;
import net.sf.jguiraffe.di.Dependency;
import net.sf.jguiraffe.di.DependencyProvider;

/**
 * <p>
 * A base class for simple bean providers.</code>
 * <p>
 * A <em>simple</em> <code>BeanProvider</code> is a bean provider that only
 * cares for creating new bean instances. It does not have further support for
 * life-cycle features. Bean providers of this type are intended to collaborate
 * with a life-cycle-aware bean provider. This provider uses the simple bean
 * provider just for the creation and implements specific initialization
 * functionality.
 * </p>
 * <p>
 * This base class provides implementations for some of the methods defined in
 * the <code>{@link BeanProvider}</code> interface. Especially dummy
 * implementations for the locking methods are available, which are typically
 * not needed by simple providers.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: SimpleBeanProvider.java 205 2012-01-29 18:29:57Z oheger $
 */
public abstract class SimpleBeanProvider implements BeanProvider
{
    /**
     * Returns the dependencies for this bean provider. This implementation
     * simply returns <b>null</b>. Simple providers often do not have any
     * dependencies.
     *
     * @return a set with the dependencies of this bean provider
     */
    public Set<Dependency> getDependencies()
    {
        return null;
    }

    /**
     * Returns the lock ID of the current transaction. Simple bean providers do
     * not require any lock handling. So this implementation always returns
     * <b>null</b>.
     *
     * @return the lock ID of the current transaction
     */
    public Long getLockID()
    {
        return null;
    }

    /**
     * Sets the lock ID of the current transaction. This is just a dummy
     * implementation; simple bean providers do not need any lock handling.
     *
     * @param lid the lock ID of the current transaction
     */
    public void setLockID(Long lid)
    {
    }

    /**
     * Returns a flag whether the managed bean is available. For simple bean
     * providers this is always the case, so this implementation always returns
     * <b>true</b>.
     *
     * @return a flag whether the managed bean is available
     */
    public boolean isBeanAvailable()
    {
        return true;
    }

    /**
     * Shuts down this {@code BeanProvider}. This implementation is just an
     * empty dummy; a simple bean provider does not need any special shutdown
     * handling.
     *
     * @param depProvider the {@code DependencyProvider}
     */
    public void shutdown(DependencyProvider depProvider)
    {
    }
}
