/*
 * Copyright 2006-2021 The JGUIraffe Team.
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

import java.util.Set;

/**
 * <p>
 * Definition of an interface for objects that provide access to beans.
 * </p>
 * <p>
 * A <em>bean</em> is a plain old Java object that is somehow defined (e.g. its
 * concrete class, some initialization properties, or some methods to be
 * invoked). When the <code>BeanProvider</code> is asked for the bean it is
 * responsible for, it has to ensure that it has been created and completely
 * initialized. Then it can be handed over to the calling instance. It is up to
 * a concrete implementation how this is achieved. One implementation can for
 * instance always return the same bean instance, effectively implementing the
 * <em>singleton</em> pattern. Another implementation could create a new bean
 * instance on each request, which would be appropriate for a stateful service
 * bean.
 * </p>
 * <p>
 * The most interesting part of a bean provider is the fact that it can depend
 * on other bean providers (which in turn can have dependencies on further
 * providers and so on). These dependencies are dynamically resolved when a bean
 * is requested, which can lead to a whole bunch of objects being created and
 * initialized. The resulting bean is then fully initialized and ready for
 * service. (These are the well-known concepts of <em>dependency injection</em>
 * and <em>inversion of control (IoC)</em>.
 * </p>
 * <p>
 * The most important method of a <code>BeanProvider</code> is the
 * <code>getBean()</code> method, which returns the fully initialized bean
 * maintained by this provider. For the proper initialization of the bean and
 * dynamic resolving of dependencies the <code>getDependencies()</code> method
 * is also of importance.
 * </p>
 * <p>
 * While providing a reference to the managed bean is naturally the main task of
 * a bean provider, there are some other methods defined in this interface.
 * These methods have a more technical background; they allow the dependency
 * injection framework to effectively manage access to bean stores, especially
 * if they are concurrently used by multiple threads. To better understand these
 * methods some words about synchronization and threading issues are necessary:
 * </p>
 * <p>
 * An access to the bean provided by a <code>BeanProvider</code> is
 * automatically synchronized by the framework. This means that until the bean
 * is completely created and initialized, no other thread can access this
 * provider. If there are cyclic references in the dependency graph however
 * (e.g. bean A depends on bean B, which depends on bean C, which again depends
 * on bean A), the <code>getBean()</code> method can be entered again by the
 * same thread. An implementation should be aware of this. The invocation of a
 * bean provider's <code>getBean()</code> method is also called a
 * <em>transaction</em>. If the bean store is concurrently accessed by other
 * threads, it has to be ensured that the whole initialization process of a bean
 * is not disturbed by other threads. The framework takes care about this. With
 * the additional methods defined by this interface a <code>BeanProvider</code>
 * implementation can obtain information about the start and the end of such
 * transactions.
 * </p>
 * <p>
 * When an application is about to shut down or when a {@code BeanStore} is
 * closed, it is often necessary to do some clean up. For beans created by the
 * framework - especially singleton service beans - it may be required to invoke
 * a specific shutdown method, for instance if the bean is a database connection
 * pool or something like that. For this purpose the {@code BeanProvider}
 * interface defines the {@code shutdown()} method. Here a concrete
 * implementation can place code for cleaning up the beans created by it.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: BeanProvider.java 205 2012-01-29 18:29:57Z oheger $
 */
public interface BeanProvider
{
    /**
     * <p>
     * Returns the bean managed by this provider. This is the main method of a
     * <code>BeanProvider</code>. A concrete implementation can do whatever
     * is necessary to obtain the requested bean. Depending on the provider's
     * semantic it can decide whether the bean is cached after it has been
     * created, or whether for each request a new bean has to be created.
     * </p>
     * <p>
     * The passed in reference to a <code>DependencyProvider</code> can be
     * used for obtaining needed dependencies. This method will be automatically
     * synchronized by the framework. However if there are cyclic dependencies,
     * querying the <code>DependencyProvider</code> may cause the method to be
     * entered again. An implementation must be aware of this to avoid endless
     * loops.
     * </p>
     * <p>
     * If a problem occurs when creating and initializing the bean, an
     * implementation should throw a <code>{@link InjectionException}</code>
     * exception.
     * </p>
     *
     * @param dependencyProvider the dependency provider
     * @return the bean managed by this provider
     */
    Object getBean(DependencyProvider dependencyProvider);

    /**
     * Returns the class of the bean managed by this provider. Clients may query
     * beans based on their class. So it becomes necessary to ask a provider for
     * the bean class without having to create the bean first. A
     * <code>DependencyProvider</code> is passed in that can be used for
     * resolving the class (in case only the class name is known to the
     * provider)
     *
     * @param dependencyProvider the dependency provider, which can be used for
     * resolving the bean class
     * @return the class of the bean managed by this provider
     */
    Class<?> getBeanClass(DependencyProvider dependencyProvider);

    /**
     * Returns a set with the descriptions of the beans this provider depends
     * on. These are typically beans the managed bean is to be initialized with
     * through dependency injection. This method is called once by the framework
     * whenever a transaction starts (i.e. when client code queries either this
     * bean or a bean that depends on this provider). Its return value need not
     * be constant over time (for instance a provider that always returns the
     * same bean does not need any dependencies any more after the bean has been
     * initialized on first access).
     *
     * @return a set with the dependency descriptions of the beans this bean
     * provider depends on (can be <b>null</b> if there are no dependencies)
     * @see Dependency
     */
    Set<Dependency> getDependencies();

    /**
     * Returns the ID of the transaction that locked this bean provider. A
     * transaction locks each <code>BeanProvider</code> it depends on, so that
     * it cannot be concurrently accessed by another thread. This is done using
     * the <code>setLockID()</code>. An implementation will typically store
     * the value passed in here and return it in <code>getLockID()</code>.
     * However if a concrete implementation does not need any synchronization
     * (e.g. because it only returns a constant object), it can always return
     * <b>null</b> here.
     *
     * @return the ID of the locking transaction or <b>null</b> if there is
     * none
     */
    Long getLockID();

    /**
     * Locks or unlocks this <code>BeanProvider</code>. This method is called
     * with the ID of the current transaction to mark this provider as blocked.
     * After that it cannot be accessed by a different thread until the locking
     * transaction ends. Before it ends it will call this method again with a
     * value of <b>null</b>. This method, in conjunction with the
     * <code>getLockID()</code> method, is related to the implementation of
     * synchronized access to bean providers. A <code>BeanProvider</code>
     * implementation that requires synchronization should store the value
     * passed in here in a member field and return it in the
     * <code>getLockID()</code> method. An implementation that is thread-safe
     * can leave this method empty and return always <b>null</b> in
     * <code>getLockID()</code>.
     *
     * @param lid the ID of the locking transaction
     * @see #getLockID()
     */
    void setLockID(Long lid);

    /**
     * Checks whether the bean managed by this provider can now be retrieved.
     * This method can be used for detecting cyclic references: A bean may need
     * other beans as arguments for its creation (e.g. for calling its
     * constructor). This may cause the creation of these dependent beans. If
     * one of these depends on the original bean, there is a cyclic dependency.
     * Complex bean providers supporting enhanced bean creation and
     * initialization should implement this method to return <b>false</b> if
     * their managed bean is currently in the creation phase and thus does not
     * really exist yet. A caller may then decide to try again later when the
     * creation is complete. Simple bean providers can always return <b>true</b>.
     *
     * @return a flag whether the managed bean is available
     */
    boolean isBeanAvailable();

    /**
     * Notifies this {@code BeanProvider} that it (and the bean(s) created by
     * it) is no longer needed. A concrete implementation can use this method to
     * perform some cleanup. It is also an opportunity for invoking a shutdown
     * method on the bean created by this provider. Typically this method will
     * be called when the {@code BeanStore} this {@code BeanProvider} belongs to
     * is closed.
     *
     * @param dependencyProvider the {@code DependencyProvider}
     */
    void shutdown(DependencyProvider dependencyProvider);
}
