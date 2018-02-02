/*
 * Copyright 2006-2018 The JGUIraffe Team.
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

import java.util.Collection;
import java.util.Iterator;

import net.sf.jguiraffe.gui.builder.components.ComponentManager;
import net.sf.jguiraffe.gui.builder.components.FormBuilderException;
import net.sf.jguiraffe.gui.builder.components.Orientation;

import org.apache.commons.jelly.JellyTagException;

/**
 * <p>
 * A tag for implementing a splitter component.
 * </p>
 * <p>
 * A splitter is an element, which separates two arbitrary elements, either
 * vertically or horizontally. The user can drag the splitter and thus change
 * the size of the associated elements.
 * </p>
 * <p>
 * This tag expects exactly two other component tags in its body (which itself
 * can be container tags holding arbitrary other components). These are the
 * components whose size is controlled by the splitter. The following attributes
 * are supported:
 * </p>
 * <p>
 * <table border="1">
 * <tr>
 * <th>Attribute</th>
 * <th>Description</th>
 * <th>Optional</th>
 * </tr>
 * <tr>
 * <td valign="top">size</td>
 * <td>Defines the size of the slider component in pixels. This is either the
 * width or the height of the slider bar (depending on the slider's
 * orientation).</td>
 * <td valign="top">Yes</td>
 * </tr>
 * <tr>
 * <td valign="top">orientation</td>
 * <td>This attribute defines the slider's orientation (i.e. vertical or
 * horizontal). Allowed values are specified by the {@link Orientation}
 * enumeration class. Case does not matter. If this attribute is not provided,
 * the slider will be vertical.</td>
 * <td valign="top">Yes</td>
 * </tr>
 * <tr>
 * <td valign="top">pos</td>
 * <td>With this attribute an initial position of the slider can be specified in
 * pixels.</td>
 * <td valign="top">Yes</td>
 * </tr>
 * <tr>
 * <td valign="top">resizeWeight</td>
 * <td>Determines the behavior of the slider when the available space changes.
 * This attribute can take a value between 0 and 1. A value of 0 means that only
 * the right/bottom component is affected by the change, a value of 1 would only
 * modify the left/top component. 0.5 means that both components are equally
 * affected, and so on.</td>
 * <td valign="top">Yes</td>
 * </tr>
 * </table>
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: SplitterTag.java 205 2012-01-29 18:29:57Z oheger $
 */
public class SplitterTag extends ContainerTag
{
    /** Stores the first component of the splitter. */
    private Object firstComponent;

    /** Stores the second component of the splitter. */
    private Object secondComponent;

    /** Stores the resolved orientation of the splitter. */
    private Orientation splitterOrientation;

    /** Stores the splitter's orientation as a string. */
    private String orientation;

    /** Stores the resize weight. */
    private float resizeWeight;

    /** Stores the slider's size. */
    private int size;

    /** Stores the initial position. */
    private int pos;

    /**
     * Returns the orientation of the splitter as a string value. This is the
     * value passed to the {@code orientation} attribute. It may not be a valid
     * instance of the {@link Orientation} enumeration class.
     *
     * @return the orientation as string
     */
    public String getOrientation()
    {
        return orientation;
    }

    /**
     * Set method for the {@code orientation} attribute.
     *
     * @param orientation the attribute's value
     */
    public void setOrientation(String orientation)
    {
        this.orientation = orientation;
    }

    /**
     * Returns the final orientation of the splitter. This value is determined
     * based on the value of the {@code orientation} attribute when the tag is
     * processed. If the orientation cannot be resolved, processing of the tag
     * throws an exception.
     *
     * @return the {@code Orientation} value of the splitter
     */
    public Orientation getSplitterOrientation()
    {
        return splitterOrientation;
    }

    /**
     * Returns the initial position of the splitter.
     *
     * @return the initial position
     */
    public int getPos()
    {
        return pos;
    }

    /**
     * Setter method for the pos attribute.
     *
     * @param pos the attribute's value
     */
    public void setPos(int pos)
    {
        this.pos = pos;
    }

    /**
     * Returns the splitter' resize weight. This factor determines how the
     * associated components are affected by a change of the size.
     *
     * @return the resize weight factor
     */
    public float getResizeWeight()
    {
        return resizeWeight;
    }

    /**
     * Setter method for the resizeWeight attribute.
     *
     * @param resizeWeight the attribute's value
     */
    public void setResizeWeight(float resizeWeight)
    {
        this.resizeWeight = resizeWeight;
    }

    /**
     * Returns the splitter's size. This is the width or height (depending on
     * the orientation) of the drawn bar.
     *
     * @return the size of the splitter
     */
    public int getSize()
    {
        return size;
    }

    /**
     * Setter method of the size attribute.
     *
     * @param size the attribute's value
     */
    public void setSize(int size)
    {
        this.size = size;
    }

    /**
     * Returns the first component of the splitter.
     *
     * @return the first component
     */
    public Object getFirstComponent()
    {
        return firstComponent;
    }

    /**
     * Returns the second component of the splitter.
     *
     * @return the second component
     */
    public Object getSecondComponent()
    {
        return secondComponent;
    }

    /**
     * Performs processing before the evaluation of this tag's body. This
     * implementation tries to resolve the value passed to the {@code
     * orientation} attribute.
     *
     * @throws JellyTagException if the tag is used incorrectly
     * @throws FormBuilderException if an error occurs
     */
    @Override
    protected void processBeforeBody() throws JellyTagException,
            FormBuilderException
    {
        super.processBeforeBody();
        splitterOrientation = Orientation.getOrientation(getOrientation(),
                Orientation.VERTICAL);
    }

    /**
     * Creates the container component. This implementation will delegate to the
     * component manager to create and initialize the splitter component.
     *
     * @param manager the component manager
     * @param create the create flag
     * @param components a collection with the container's children
     * @return the newly created component
     * @throws FormBuilderException if the tag is incorrectly used
     * @throws JellyTagException if the tag is incorrectly used
     */
    @Override
    protected Object createContainer(ComponentManager manager, boolean create,
            Collection<Object[]> components) throws FormBuilderException, JellyTagException
    {
        if (!create)
        {
            if (getSize() < 0)
            {
                throw new FormBuilderException(
                        "Splitter size must be positive!");
            }
            if (getResizeWeight() < 0 || getResizeWeight() > 1)
            {
                throw new FormBuilderException(
                        "ResizeWeight must be in [0..1]!");
            }
            if (components.size() != 2)
            {
                throw new FormBuilderException(
                        "A splitter must have exactly 2 child components!");
            }
            else
            {
                Iterator<Object[]> it = components.iterator();
                firstComponent = it.next()[0];
                secondComponent = it.next()[0];
            }
        }

        return manager.createSplitter(this, create);
    }

    /**
     * Adds the components to this container. This implementation is left empty
     * because the splitter's components are handled in a different way.
     *
     * @param manager the component manager
     * @param container the container object
     * @param comps a collection with the child components
     */
    @Override
    protected void addComponents(ComponentManager manager, Object container,
            Collection<Object[]> comps)
    {
        // not needed here
    }
}
