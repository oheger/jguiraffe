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
package net.sf.jguiraffe.gui.builder.window;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import net.sf.jguiraffe.di.impl.SimpleBeanStoreImpl;

import org.apache.commons.jelly.JellyContext;

/**
 * <p>
 * A data class that stores important information needed during a window builder
 * process.
 * </p>
 * <p>
 * An instance of this class lives in the Jelly context during execution of a
 * Jelly script that makes use of the window builder library. It contains
 * parameters required by tag handler classes, e.g. the window manager to use.
 * It will also store the results of the builder operation.
 * </p>
 * <p>
 * In most cases a builder script will define only a single result window (if
 * any). In this case the result window can be set using the
 * {@link #setResultWindow(Window)} method and queried using
 * {@link #getResultWindow()}. However, scripts are allowed to define multiple
 * windows (an example use case would be a simple message box window which is
 * displayed by an action of the main window). This can be achieved by giving
 * the windows unique names; i.e. the {@code name} attribute of the window tag
 * must be defined. Then, instead of {@link #setResultWindow(Window)}, the
 * {@link #putWindow(String, Window)} method can be called to add the result
 * window to this data object. {@link #putWindow(String, Window)} adds the
 * window to an internal map so that it can be queried later by its name.
 * </p>
 * <p>
 * Some objects managed by this class are available to the dependency injection
 * framework (they can be obtained through the <code>BeanContext</code> of the
 * active builder). These are the following:
 * <ul>
 * <li>The result window (provided that it already has been set) can be obtained
 * using the key <em>{@value #KEY_CURRENT_WINDOW}</em>.</li>
 * <li>Windows that have been assigned a name can be queried directly. The
 * prefix <em>{@value #WINDOW_PREFIX}</em> has to be used, e.g. {@code
 * window:msgBox} returns the window with the name <em>msgBox</em>.</li>
 * <li>The parent window (if available) can be queried using the key
 * <em>{@value #KEY_PARENT_WINDOW}</em>.</li>
 * <li>If the current form bean is needed, it can be found in the context under
 * the key <em>{@value #KEY_FORM_BEAN}</em>.</li>
 * <li>Finally the active instance of this class can be obtained under the key
 * <em>{@value #KEY_WINDOW_BUILDER_DATA}</em>.</li>
 * </ul>
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: WindowBuilderData.java 205 2012-01-29 18:29:57Z oheger $
 */
public class WindowBuilderData implements SimpleBeanStoreImpl.BeanContributor
{
    /**
     * Constant for the key, under which the current result window can be
     * obtained from the builder's bean context.
     */
    public static final String KEY_CURRENT_WINDOW = "CURRENT_WINDOW";

    /**
     * Constant for the key, under which the parent window can be obtained from
     * the builder's bean context.
     */
    public static final String KEY_PARENT_WINDOW = "PARENT_WINDOW";

    /**
     * Constant for the key, under which the current form bean can be obtained
     * from the builder's bean context.
     */
    public static final String KEY_FORM_BEAN = "FORM_BEAN";

    /**
     * Constant for the key, under which the instance of this class can be
     * obtained from the builder's bean context.
     */
    public static final String KEY_WINDOW_BUILDER_DATA = "WINDOW_BUILDER_DATA";

    /**
     * Constant for the prefix for window beans. If a window is to be queried by
     * its name, this prefix has to be used.
     */
    public static final String WINDOW_PREFIX = "window:";

    /**
     * Constant for the key under which an instance will be stored in the Jelly
     * context.
     */
    private static final String CTX_KEY = WindowBuilderData.class.getName();

    /** A map for the named windows. */
    private final Map<String, Window> windows;

    /** Stores a reference to the current window manager. */
    private WindowManager windowManager;

    /** Stores a reference to the parent window. */
    private Window parentWindow;

    /** Stores the result window. */
    private Window resultWindow;

    /** Stores the form bean instance. */
    private Object formBean;

    /** Stores the current Jelly context. */
    private JellyContext context;

    /**
     * Creates a new instance of {@code WindowBuilderData}.
     */
    public WindowBuilderData()
    {
        windows = new HashMap<String, Window>();
    }

    /**
     * Returns the form bean.
     *
     * @return the form bean
     */
    public Object getFormBean()
    {
        return formBean;
    }

    /**
     * Sets the form bean. This information will be processed by form
     * controllers that can use it to initialize forms and store user input.
     *
     * @param formBean the form bean
     */
    public void setFormBean(Object formBean)
    {
        this.formBean = formBean;
    }

    /**
     * Returns the parent window of the new window.
     *
     * @return the parent window
     */
    public Window getParentWindow()
    {
        return parentWindow;
    }

    /**
     * Sets the parent window. The newly created window will be a child of this
     * window. For top level windows this property should be <b>null</b>.
     *
     * @param parentWindow the parent window
     */
    public void setParentWindow(Window parentWindow)
    {
        this.parentWindow = parentWindow;
    }

    /**
     * Returns the result window of the builder process.
     *
     * @return the result window
     */
    public Window getResultWindow()
    {
        return resultWindow;
    }

    /**
     * Sets the result window. Used by window creating tag handler classes to
     * store their results.
     *
     * @param resultWindow the result window
     */
    public void setResultWindow(Window resultWindow)
    {
        this.resultWindow = resultWindow;
    }

    /**
     * Returns the result window with the given name. Windows can be assigned a
     * name. Using this name they can be queried. This is especially useful for
     * builder scripts that define multiple windows. Using this method a
     * specific window can be queried.
     *
     * @param name the name of the desired window
     * @return the window with this name or <b>null</b> if it cannot be resolved
     */
    public Window getWindow(String name)
    {
        return windows.get(WINDOW_PREFIX + name);
    }

    /**
     * Returns the current {@code JellyContext}. This information can be useful
     * when access to objects local to the current build operation is needed.
     * Note that this property is only initialized after this object was stored
     * in the context. (This is the case while a build operation is executing.)
     * If this method is called before a context is available, an
     * {@code IllegalStateException} exception is thrown.
     *
     * @return the current {@code JellyContext}
     * @throws IllegalStateException if the current context is not yet available
     * @since 1.3
     */
    public JellyContext getContext()
    {
        if (context == null)
        {
            throw new IllegalStateException("Context not available! "
                    + "This method can only be called after this instance "
                    + "has been stored in the Jelly context using put().");
        }
        return context;
    }

    /**
     * Adds a result window to this data object. This method can be called for
     * each window created during the builder operation. If a name is specified,
     * the window is stored in a map where it can be accessed later using this
     * name (using {@link #getWindow(String)}). This method also invokes
     * {@link #setResultWindow(Window)}, so that the passed in window becomes
     * the result window. This means that if a script defines multiple windows,
     * the last one becomes the official result window.
     *
     * @param name the name of the window; can be <b>null</b>, then the window
     *        cannot be queried using {@link #getWindow(String)}
     * @param window the window to be added
     */
    public void putWindow(String name, Window window)
    {
        setResultWindow(window);

        if (name != null)
        {
            windows.put(WINDOW_PREFIX + name, window);
        }
    }

    /**
     * Returns the window manager.
     *
     * @return the window manager
     */
    public WindowManager getWindowManager()
    {
        return windowManager;
    }

    /**
     * Sets the window manager. Here the platform specific window manager must
     * be set, which will be used for creating windows.
     *
     * @param windowManager the window manager to use
     */
    public void setWindowManager(WindowManager windowManager)
    {
        this.windowManager = windowManager;
    }

    /**
     * Obtains the names of the supported beans. This implementation adds the
     * names of some static beans. If there are named windows, their names are
     * added as well.
     *
     * @param names a set in which to store the bean names
     */
    public void beanNames(Set<String> names)
    {
        addBeanName(names, KEY_CURRENT_WINDOW, getResultWindow());
        addBeanName(names, KEY_FORM_BEAN, getFormBean());
        addBeanName(names, KEY_PARENT_WINDOW, getParentWindow());

        for (String name : windows.keySet())
        {
            names.add(name);
        }
    }

    /**
     * Returns the bean with the specified name. This implementation supports a
     * few objects that are managed by this object. Named windows can also be
     * queried.
     *
     * @param name the name of the desired bean
     * @return the bean with this name or <b>null</b> if it cannot be found
     */
    public Object getBean(String name)
    {
        Object bean = null;

        if (KEY_CURRENT_WINDOW.equals(name))
        {
            bean = getResultWindow();
        }
        else if (KEY_FORM_BEAN.equals(name))
        {
            bean = getFormBean();
        }
        else if (KEY_PARENT_WINDOW.equals(name))
        {
            bean = getParentWindow();
        }
        else if (name.startsWith(WINDOW_PREFIX))
        {
            bean = windows.get(name);
        }

        return bean;
    }

    /**
     * Initializes the specified bean store. This implementation adds the static
     * object references to the store and registers this object as bean
     * contributor.
     *
     * @param store the store to be initialized
     */
    public void initBeanStore(SimpleBeanStoreImpl store)
    {
        store.addBean(KEY_WINDOW_BUILDER_DATA, this);
        store.addBeanContributor(this);
    }

    /**
     * Stores this instance in the specified Jelly context.
     *
     * @param ctx the context (must not be <b>null</b>)
     * @throws IllegalArgumentException if the context is <b>null</b>
     */
    public void put(JellyContext ctx)
    {
        if (ctx == null)
        {
            throw new IllegalArgumentException("Context must not be null!");
        }
        ctx.setVariable(CTX_KEY, this);
        context = ctx;
    }

    /**
     * Returns the instance of this class stored in the specified Jelly context.
     * If no instance can be found, <b>null</b> will be returned.
     *
     * @param context the Jelly context
     * @return the instance found in this context
     */
    public static WindowBuilderData get(JellyContext context)
    {
        return (context != null) ? (WindowBuilderData) context
                .findVariable(CTX_KEY) : null;
    }

    /**
     * Helper method for adding a bean name to a collection only if the bean is
     * not <b>null</b>.
     *
     * @param col the collection
     * @param name the name
     * @param bean the bean
     */
    private static void addBeanName(Collection<String> col, String name,
            Object bean)
    {
        if (bean != null)
        {
            col.add(name);
        }
    }
}
