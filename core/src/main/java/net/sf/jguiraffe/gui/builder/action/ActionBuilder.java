/*
 * Copyright 2006-2014 The JGUIraffe Team.
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
package net.sf.jguiraffe.gui.builder.action;

import java.util.Set;

import net.sf.jguiraffe.di.impl.SimpleBeanStoreImpl;

import org.apache.commons.jelly.JellyContext;

/**
 * <p>
 * A central data class used during the action builder process.
 * </p>
 * <p>
 * This class holds all information needed during the processing of Jelly
 * scripts with actions and related tags. An instance is stored in the Jelly
 * context and can be accessed by all interested components. The role of this
 * class is almost analogous to the
 * {@link net.sf.jguiraffe.gui.forms.components.ComponentBuilderData ComponentBuilderData}
 * class in the form builder package.
 * </p>
 * <p>
 * By implementing the <code>BeanContributor</code> interface this class makes
 * some of its managed objects available to the dependency injection framework.
 * These are the following:
 * <ul>
 * <li>All actions stored in the associated <code>{@link ActionStore}</code>
 * can be accessed under their name with the prefix
 * <em>{@value #KEY_ACTION_PREFIX}</em>.</li>
 * <li>The <code>{@link ActionStore}</code> itself is available under the
 * name <em>{@value #KEY_ACTION_STORE}</em>.</li>
 * <li>The instance of this class can be queried under the name
 * <em>{@value #KEY_ACTION_BUILDER}</em>.</li>
 * </ul>
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: ActionBuilder.java 205 2012-01-29 18:29:57Z oheger $
 */
public class ActionBuilder implements SimpleBeanStoreImpl.BeanContributor
{
    /**
     * Constant for the key for accessing the action store from the builder's
     * bean context.
     */
    public static final String KEY_ACTION_STORE = "ACTION_STORE";

    /**
     * Constant for the key for accessing the instance of this class from the
     * builder's bean context.
     */
    public static final String KEY_ACTION_BUILDER = "ACTION_BUILDER";

    /**
     * Constant for the prefix used for accessing actions.
     */
    public static final String KEY_ACTION_PREFIX = "action:";

    /**
     * Constant for the name under which an instance will be stored in the Jelly
     * context.
     */
    private static final String CTX_KEY = ActionBuilder.class.getName();

    /** Holds a reference to the used action store. */
    private ActionStore actionStore;

    /** Holds a reference to the platform specific action manager. */
    private ActionManager actionManager;

    /** Stores the menu icon flag. */
    private boolean menuIcon;

    /** Stores the toolbar text flag. */
    private boolean toolbarText;

    /**
     * Returns a reference to the current action store.
     *
     * @return the action store
     */
    public ActionStore getActionStore()
    {
        return actionStore;
    }

    /**
     * Sets the action store. Newly created actions will be stored in this
     * objects. If actions are requested, they will be looked up here, too.
     *
     * @param actionStore the action store
     */
    public void setActionStore(ActionStore actionStore)
    {
        this.actionStore = actionStore;
    }

    /**
     * Returns a reference to the action manager.
     *
     * @return the action manager
     */
    public ActionManager getActionManager()
    {
        return actionManager;
    }

    /**
     * Sets the action manager. This is a GUI library specific component that
     * knows how to create all affected objects.
     *
     * @param actionManager the action manager to use
     */
    public void setActionManager(ActionManager actionManager)
    {
        this.actionManager = actionManager;
    }

    /**
     * Returns the value of the menu icon flag.
     *
     * @return the menu icon flag
     */
    public boolean isMenuIcon()
    {
        return menuIcon;
    }

    /**
     * Sets the value of the menu icon flag. If this flag is set, menu items
     * will have an icon if one is defined. If set to <b>false</b>, these icons
     * will be suppressed.
     *
     * @param menuIcon the value of the flag
     */
    public void setMenuIcon(boolean menuIcon)
    {
        this.menuIcon = menuIcon;
    }

    /**
     * Returns the value of the toolbar text flag.
     *
     * @return the toolbar text flag
     */
    public boolean isToolbarText()
    {
        return toolbarText;
    }

    /**
     * Sets the value of the toolbar text flag. If this flag is set, toolbar
     * buttons will display a text if one is defined. Otherwise the text is
     * suppressed.
     *
     * @param toolbarText the value of the flag
     */
    public void setToolbarText(boolean toolbarText)
    {
        this.toolbarText = toolbarText;
    }

    /**
     * Obtains the names of the beans supported by this bean contributor. This
     * implementation returns the names of the actions stored in the internal
     * action store and some other helper objects.
     *
     * @param names the set to which to add the names
     */
    public void beanNames(Set<String> names)
    {
        if (getActionStore() != null)
        {
            for (String name : getActionStore().getAllActionNames())
            {
                names.add(KEY_ACTION_PREFIX + name);
            }
            names.add(KEY_ACTION_STORE);
        }
    }

    /**
     * Returns the bean with the given name.
     *
     * @param name the name of the bean
     * @return the bean with this name
     * @throws java.util.NoSuchElementException if an unknown action is requested
     */
    public Object getBean(String name)
    {
        Object bean = null;

        if (getActionStore() != null)
        {
            if (KEY_ACTION_STORE.equals(name))
            {
                bean = getActionStore();
            }

            else if (name != null && name.startsWith(KEY_ACTION_PREFIX))
            {
                bean = getActionStore().getAction(
                        name.substring(KEY_ACTION_PREFIX.length()));
            }
        }

        return bean;
    }

    /**
     * Initializes the specified bean store object. This method is called by the
     * builder when the <code>BeanContext</code> used during the builder
     * operation is constructed. This implementation will add the static beans
     * to the given store and register this object as
     * <code>BeanContributor</code>.
     *
     * @param store the store to be initialized
     */
    public void initBeanStore(SimpleBeanStoreImpl store)
    {
        store.addBean(KEY_ACTION_BUILDER, this);
        store.addBeanContributor(this);
    }

    /**
     * Stores this object in the given Jelly context.
     *
     * @param context the context (must not be <b>null</b>)
     * @throws IllegalArgumentException if the context is <b>null</b>
     */
    public void put(JellyContext context)
    {
        if (context == null)
        {
            throw new IllegalArgumentException("Context must not be null!");
        }
        context.setVariable(CTX_KEY, this);
    }

    /**
     * Returns the instance of this class stored in the specified Jelly context.
     * If no instance can be found, <b>null</b> will be returned.
     *
     * @param context the Jelly context
     * @return the instance found in this context
     */
    public static ActionBuilder get(JellyContext context)
    {
        return (context != null) ? (ActionBuilder) context
                .findVariable(CTX_KEY) : null;
    }
}
