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
package net.sf.jguiraffe.gui.builder.components.tags;

import java.lang.reflect.Array;

import net.sf.jguiraffe.di.BeanContext;
import net.sf.jguiraffe.gui.builder.components.ComponentBuilderData;
import net.sf.jguiraffe.gui.builder.components.FormBuilderException;
import net.sf.jguiraffe.gui.builder.components.model.ListModel;

import org.apache.commons.jelly.MissingAttributeException;
import org.apache.commons.lang.StringUtils;

/**
 * <p>
 * A helper class for dealing with model objects.
 * </p>
 * <p>
 * This class provides some functionality that can be used by concrete
 * <code>ComponentHandler</code> implementations dealing with list box or combo
 * box components. Especially the conversion from selected indices to
 * corresponding value objects or vice versa is covered.
 * </p>
 *
 * @see net.sf.jguiraffe.gui.forms.ComponentHandler
 * @author Oliver Heger
 * @version $Id: ListModelUtils.java 205 2012-01-29 18:29:57Z oheger $
 */
public final class ListModelUtils
{
    /**
     * Private constructor, so that no instances of this static utility class
     * can be created.
     */
    private ListModelUtils()
    {
    }

    /**
     * Returns the value object with the given index from the specified list
     * model. If this object is <b>null </b>, this method will return the index
     * as an <code>Integer</code>.
     *
     * @param listModel the affected list model
     * @param index the index
     * @return the value object (can be <b>null</b> for an invalid index)
     */
    public static Object getValue(ListModel listModel, int index)
    {
        if (index < 0 || index >= listModel.size())
        {
            return null;
        }

        Object result = listModel.getValueObject(index);
        if (result == null)
        {
            result = Integer.valueOf(index);
        }
        return result;
    }

    /**
     * Returns the index of the given value object in the specified list model.
     * This method can be used for setting the data of a list box or combo box
     * component from the property of the form bean. This implementation tries a
     * linear search in the list model's value objects to determine the index.
     * If the <code>getValueObject()</code> method of the list model returns
     * <b>null </b>, it is tried to cast the value to an <code>Integer</code>.
     *
     * @param listModel the list model
     * @param value the value whose index is to be determined
     * @return the index of this value object or -1 if it cannot be determined
     */
    public static int getIndex(ListModel listModel, Object value)
    {
        int size = listModel.size();
        if (size <= 0 || value == null)
        {
            return -1;
        }

        Object v0 = listModel.getValueObject(0);
        if (v0 != null)
        {
            if (v0.equals(value))
            {
                return 0;
            }
            else
            {
                for (int index = 1; index < size; index++)
                {
                    if (value.equals(listModel.getValueObject(index)))
                    {
                        return index;
                    }
                }
                return -1;
            }
        }

        else
        {
            return (value instanceof Number) ? ((Number) value).intValue() : -1;
        }
    }

    /**
     * Returns the values from the specified list model for the given indices.
     * This method acts like <code>getValue()</code>, but can be used for lists
     * with multi selection. Note that the resulting array is of the type of the
     * list model.
     *
     * @param listModel the list model
     * @param indices the indices
     * @return an object array with the selected objects
     */
    public static Object[] getValues(ListModel listModel, int[] indices)
    {
        int length = (indices == null) ? 0 : indices.length;
        Object result = Array.newInstance(listModel.getType(), length);

        for (int i = 0; i < length; i++)
        {
            Array.set(result, i, getValue(listModel, indices[i]));
        }

        return (Object[]) result;
    }

    /**
     * Returns the indices of all given value objects in the specified list
     * model. This method is the equivalent of <code>getIndex()</code> for lists
     * with multi selection. Note that the returned array will not contain
     * components with a value of -1. If a value object cannot be found in the
     * list model, no component is added to the resulting array.
     *
     * @param listModel the list model
     * @param values an array with the value objects
     * @return an array with the indices of the value objects (never <b>null
     *         </b>)
     */
    public static int[] getIndices(ListModel listModel, Object[] values)
    {
        if (values == null || values.length < 1)
        {
            return new int[0];
        }

        int[] indices = new int[values.length];
        int count = 0;
        for (int i = 0; i < indices.length; i++)
        {
            int idx = getIndex(listModel, values[i]);
            if (idx >= 0)
            {
                indices[count++] = idx;
            }
        }

        if (count == indices.length)
        {
            return indices;
        }
        else
        {
            int[] result = new int[count];
            System.arraycopy(indices, 0, result, 0, count);
            return result;
        }
    }

    /**
     * Initializes the {@code ListModel} for the given {@code ListModelSupport}
     * object. This method implements the default algorithm for list model
     * initialization used by tags that allow the definition of a model. It
     * performs the following steps:
     * <ul>
     * <li>If a list model is already set, it does nothing.</li>
     * <li>Otherwise, the list model is resolved in the following way:
     * <ul>
     * <li>The name of the model object is obtained. If it is not set, an
     * exception is thrown.</li>
     * <li>A bean with this name is obtained from the current {@code
     * BeanContext}.</li>
     * <li>If this bean implements the {@code ListModel} interface, it is set as
     * the list model. Otherwise, an exception is thrown.</li>
     * </ul>
     * </li>
     * </ul>
     *
     * @param modelSupport the object to be initialized (must not be
     *        <b>null</b>)
     * @throws MissingAttributeException if the name for the model is not set
     * @throws FormBuilderException if the model bean cannot be resolved
     * @throws IllegalArgumentException if <b>null</b> is passed in
     */
    public static void initializeListModel(ListModelSupport modelSupport)
            throws MissingAttributeException, FormBuilderException
    {
        if (modelSupport == null)
        {
            throw new IllegalArgumentException(
                    "ListModelSupport object must not be null!");
        }

        if (modelSupport.getListModel() == null)
        {
            String modelName = modelSupport.getModelRef();
            if (StringUtils.isEmpty(modelName))
            {
                throw new MissingAttributeException("modelRef");
            }

            BeanContext beanCtx = ComponentBuilderData.get(
                    modelSupport.getContext()).getBeanContext();
            if (!beanCtx.containsBean(modelName))
            {
                throw new FormBuilderException(
                        "Cannot resolve ListModel bean: " + modelName);
            }

            Object model = beanCtx.getBean(modelName);
            if (!(model instanceof ListModel))
            {
                throw new FormBuilderException(
                        "Model bean does not implement ListModel interface: "
                                + model);
            }

            modelSupport.setListModel((ListModel) model);
        }
    }
}
