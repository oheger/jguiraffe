/*
 * Copyright 2006-2017 The JGUIraffe Team.
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import net.sf.jguiraffe.gui.builder.components.ComponentBuilderData;

/**
 * <p>
 * A specialized implementation of a {@link PopupMenuHandler},
 * which can be used out of the box for creating not too complex, mostly static
 * popup menus.
 * </p>
 * <p>
 * An instance of this class is initialized with a collection defining the
 * content of a context menu. This collection can contain the following
 * elements:
 * <ul>
 * <li>Action objects (i.e. objects implementing the
 * {@link FormAction} interface): Actions are directly added to the
 * context menu constructed by this object.</li>
 * <li>Further <code>SimplePopupMenuHandler</code> implementations: If another
 * <code>SimplePopupMenuHandler</code> instance is encountered, a sub menu is
 * created, and then the handler is invoked to populate it. In order to provide
 * the properties required for a sub menu, this class
 * extends {@code ActionDataImpl}. So things like the menu text or its icon can
 * be set as properties. There is also a method to initialize all properties
 * from another {@link ActionData} object.</li>
 * <li><b>null</b> elements: They are used for defining menu separators.</li>
 * </ul>
 * </p>
 * <p>
 * One advantage of this class is that instances can be fully defined in builder
 * scripts (using the facilities provided by the dependency injection
 * framework). As long as the context menus are static (i.e. they always display
 * the same menu items) no programming is required, but this class can be used
 * directly and fully defined in the application's configuration scripts.
 * </p>
 * <p>
 * There are some hooks allowing subclasses to influence the menu construction
 * process: Specific methods are called before items are added to the menu under
 * construction. Derived classes can intercept here and for instance suppress
 * actions under certain circumstances.
 * </p>
 * <p>
 * Implementation note: This class is intended to be used by the event handling
 * system of the GUI framework, i.e. to be invoked on the event dispatch thread.
 * It is not safe to call it on multiple concurrent threads.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: SimplePopupMenuHandler.java 205 2012-01-29 18:29:57Z oheger $
 */
public class SimplePopupMenuHandler extends ActionDataImpl implements
        PopupMenuHandler
{
    /** A list with the content of the menu. */
    private final List<?> menuItems;

    /** Stores the last constructed menu. */
    private Object constructedMenu;

    /**
     * Creates a new instance of <code>SimplePopupMenuHandler</code> and
     * initializes it with the content of the menu. The passed in collection
     * must not be <b>null</b> and must contain only valid objects, i.e. (per
     * default)
     * <ul>
     * <li>objects implementing the <code>{@link FormAction}</code> interface or
     * </li>
     * <li>further <code>SimplePopupMenuHandler</code> objects.</li>
     * </ul>
     * The constructor does not check the content of the collection for
     * validity. This can be done by invoking the <code>checkMenuItems()</code>
     * method by hand.
     *
     * @param items a collection with the content of the menu to construct
     * @throws IllegalArgumentException if the collection is <b>null</b>
     * @see #checkMenuItems()
     */
    public SimplePopupMenuHandler(Collection<?> items)
    {
        if (items == null)
        {
            throw new IllegalArgumentException(
                    "Item collection must not be null!");
        }

        menuItems = new ArrayList<Object>(items);
    }

    /**
     * Returns a list with the content of the menu to be constructed by this
     * handler.
     *
     * @return the content definition of the menu to be constructed
     */
    public List<?> getMenuItems()
    {
        return Collections.unmodifiableList(menuItems);
    }

    /**
     * Tests whether all menu elements to be processed by this handler are
     * supported. This method iterates over the items to be added to the menu
     * and invokes the <code>checkMenuElement()</code> method on each. This
     * ensures that only valid elements are involved. Note that this method is
     * <b>not</b> automatically called by the constructor. This design was
     * chosen to support derived classes that extend the number of menu elements
     * that can be handled. Such classes have to override methods like
     * <code>checkMenuElement()</code>, which therefore cannot be called from
     * the constructor. To ensure a fail-fast approach,
     * <code>checkMenuItems()</code> should be called manually after an instance
     * was constructed. However, this is optional. Invalid data will also be
     * detected when the menu is constructed.
     *
     * @throws FormActionException if invalid data is found in the list of
     *         elements to be added to the menu
     */
    public void checkMenuItems() throws FormActionException
    {
        for (Object element : menuItems)
        {
            checkMenuElement(element);
        }
    }

    /**
     * Constructs the menu. This implementation iterates over the collection of
     * menu elements that was passed to the constructor. For each element
     * <code>addMenuElement()</code> is called, which will process the element.
     * If an element found in the collection cannot be handled, an exception is
     * thrown.
     *
     * @param builder the menu builder
     * @param compData the component builder data object
     * @throws FormActionException if an invalid element is found in the
     *         elements collection
     */
    public void constructPopup(PopupMenuBuilder builder,
            ComponentBuilderData compData) throws FormActionException
    {
        for (Object element : menuItems)
        {
            addMenuElement(builder, compData, element);
        }

        constructedMenu = builder.create();
    }

    /**
     * Returns the last menu that was constructed by this handler. This method
     * can be called after <code>constructPopup()</code> for gaining access to
     * the created menu object.
     *
     * @return the menu created by the last <code>constructPopup()</code>
     *         invocation
     */
    public Object getConstructedMenu()
    {
        return constructedMenu;
    }

    /**
     * Checks whether the specified menu element can be processed by this
     * handler. This method is called by <code>checkMenuItems()</code>. It
     * checks for <code>FormAction</code> and
     * <code>SimplePopupMenuHandler</code> objects. For all other objects an
     * exception is thrown.
     *
     * @param element the element to check
     * @throws FormActionException if the element is not supported
     */
    protected void checkMenuElement(Object element) throws FormActionException
    {
        if (element != null && !(element instanceof FormAction)
                && !(element instanceof SimplePopupMenuHandler))
        {
            throw new FormActionException("Unsupported menu element: "
                    + element);
        }
    }

    /**
     * Adds an element to the menu to be constructed. This method is called by
     * <code>constructPopup()</code> for all elements found in the collection
     * defining the menu. This base implementation checks for
     * <code>FormAction</code> and <code>SimplePopupMenuHandler</code> objects
     * and delegates to the corresponding specific <code>add</code> methods.
     *
     * @param builder the menu builder
     * @param compData the component builder data object
     * @param element the element to be added
     * @throws FormActionException if the element cannot be handled
     */
    protected void addMenuElement(PopupMenuBuilder builder,
            ComponentBuilderData compData, Object element)
            throws FormActionException
    {
        if (element == null)
        {
            addSeparator(builder, compData);
        }
        else if (element instanceof FormAction)
        {
            addAction(builder, compData, (FormAction) element);
        }
        else if (element instanceof SimplePopupMenuHandler)
        {
            addSubMenu(builder, compData, (SimplePopupMenuHandler) element);
        }
        else
        {
            throw new FormActionException("Unsupported menu element: "
                    + element);
        }
    }

    /**
     * Adds an action to the menu constructed by this handler. This method is
     * called by <code>addMenuElement()</code> when an action is encountered.
     *
     * @param builder the menu builder
     * @param compData the component builder data object
     * @param action the action to be added
     * @throws FormActionException if an error occurs
     */
    protected void addAction(PopupMenuBuilder builder,
            ComponentBuilderData compData, FormAction action)
            throws FormActionException
    {
        builder.addAction(action);
    }

    /**
     * Adds a sub menu to the menu constructed by this handler. This method is
     * called by <code>addMenuElement()</code> when another
     * <code>SimplePopupMenuHandler</code> is encountered representing a sub
     * menu.
     *
     * @param builder the menu builder
     * @param compData the component builder data object
     * @param subHandler the handler representing the sub menu
     * @throws FormActionException if an error occurs
     */
    protected void addSubMenu(PopupMenuBuilder builder,
            ComponentBuilderData compData, SimplePopupMenuHandler subHandler)
            throws FormActionException
    {
        PopupMenuBuilder subBuilder = builder.subMenuBuilder(subHandler);
        subHandler.constructPopup(subBuilder, compData);
        builder.addSubMenu(subHandler.getConstructedMenu());
    }

    /**
     * Adds a separator to the menu constructed by this handler. This method is
     * called by <code>addMenuElement()</code> when a <b>null</b> element is
     * encountered in the elements collection.
     *
     * @param builder the menu builder
     * @param compData the component builder data object
     * @throws FormActionException if an error occurs
     */
    protected void addSeparator(PopupMenuBuilder builder,
            ComponentBuilderData compData) throws FormActionException
    {
        builder.addSeparator();
    }
}
