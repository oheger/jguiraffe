/*
 * Copyright 2006-2015 The JGUIraffe Team.
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

import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import net.sf.jguiraffe.gui.builder.components.ComponentBuilderData;
import net.sf.jguiraffe.gui.builder.components.ComponentGroup;
import net.sf.jguiraffe.gui.builder.components.ComponentManager;
import net.sf.jguiraffe.gui.builder.components.FormBuilderException;
import net.sf.jguiraffe.gui.forms.ComponentHandler;
import net.sf.jguiraffe.gui.forms.FieldHandler;
import net.sf.jguiraffe.gui.forms.TransformerWrapper;
import net.sf.jguiraffe.gui.forms.ValidatorWrapper;

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.MissingAttributeException;
import org.apache.commons.jelly.util.ClassLoaderUtils;

/**
 * <p>
 * A base class for tag handler classes that create input components.
 * </p>
 * <p>
 * This abstract tag handler class is on top of a hierarchy of tags that
 * generate GUI widgets that gather user input and that are maintained by the
 * {@link net.sf.jguiraffe.gui.forms.Form Form} object created
 * during the builder operation. It provides functionality for creating and
 * initializing field handler objects and pass them to the central builder data
 * object. Sub classes only have to deal with the creation of the correct
 * component handler objects.
 * </p>
 * <p>
 * This class implements support for some common attributes used by input
 * component tags. The following table lists these attributes:
 * </p>
 * <p>
 * <table border="1">
 * <tr>
 * <th>Attribute</th>
 * <th>Description</th>
 * <th>Optional</th>
 * </tr>
 * <tr>
 * <td valign="top">name</td>
 * <td>Defines the name under which the corresponding field handler will be
 * stored in the constructed form object. This name must be unique and is
 * mandatory in contrast to simple components.</td>
 * <td valign="top">no</td>
 * </tr>
 * <tr>
 * <td valign="top">displayName</td>
 * <td>This property allows to define an additional display name. The display
 * name is the one that will be presented to the user for this input component,
 * e.g. in validation error messages. Its definition is optional; if it is
 * missing, the name of the input component will be used as display name.</td>
 * <td valign="top">yes</td>
 * </tr>
 * <tr>
 * <td valign="top">displayNameres</td>
 * <td>Analog to <code>displayName</code>, but allows to define the display
 * name from a resource ID.</td>
 * <td valign="top">yes</td>
 * </tr>
 * <tr>
 * <td valign="top">displayNamegrp</td>
 * <td>If the display name is defined as a resource ID, here a special resource
 * group can be defined. If no resource group is specified, the default resource
 * group will be used.</td>
 * <td valign="top">yes</td>
 * </tr>
 * <tr>
 * <td valign="top">propertyName</td>
 * <td>With this attribute the property name can be defined. This value is used
 * to set the <code>propertyName</code> property of the corresponding field
 * handler, which makes it possible to use a different name in the form bean
 * than the internal name.</td>
 * <td valign="top">yes</td>
 * </tr>
 * <td valign="top">typeName</td>
 * <td>Allows to specify a type name for the field handler that is created for
 * this component. This should be necessary only in very special cases. Usually
 * the type can be determined from the component handler.</td>
 * <td valign="top">yes</td>
 * </tr>
 * <tr>
 * <td valign="top">noField</td>
 * <td>If this boolean attribute is set to <b>true </b>, the newly created
 * component handler will not be added to a field handler and stored in the form
 * object. It will only be added to the list of component handlers in the
 * central builder data object. This is useful for defining complex field
 * handlers that contain other handlers, e.g. a button group whose data is
 * determined by the data of the contained buttons.</td>
 * <td valign="top">yes</td>
 * </tr>
 * <tr>
 * <td valign="top">groups</td>
 * <td>In this attribute a comma separated list of names of already defined
 * component groups can be specified. This tag will then add the new component's
 * name to each group in the list. If this attribute is undefined, the tag will
 * look for an enclosing <code>{@link ComponentGroup}</code> tag. If one is
 * found, the component's name will be added to the corresponding group.</td>
 * <td valign="top">yes</td>
 * </tr>
 * </table>
 * </p>
 * <p>
 * In the tag's body a couple of other tags are supported that set other
 * properties of the input component, e.g. a font or a constraints object or
 * validators.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: InputComponentTag.java 205 2012-01-29 18:29:57Z oheger $
 */
public abstract class InputComponentTag extends ComponentBaseTag
{
    /** Constant for the groups separators. */
    private static final String GROUP_SEPARATORS = ", ";

    /** Stores the created component handler. */
    private ComponentHandler<?> componentHandler;

    /** Stores the read transformer. */
    private TransformerWrapper readTransformer;

    /** Stores the write transformer. */
    private TransformerWrapper writeTransformer;

    /** Stores the field level validator. */
    private ValidatorWrapper fieldValidator;

    /** Stores the form level validator. */
    private ValidatorWrapper formValidator;

    /** Stores the definition of the display name.*/
    private TextData displayData;

    /** Stores the value of the propertyName attribute. */
    private String propertyName;

    /** Stores the data type of the corresponding field handler. */
    private Class<?> dataType;

    /** Stores the value of the typeName attribute. */
    private String typeName;

    /** Stores the list of group names. */
    private String groups;

    /** Stores the value of the noField attribute. */
    private boolean noField;

    /**
     * Creates a new instance of <code>InputComponentTag</code>.
     */
    public InputComponentTag()
    {
        displayData = new TextData(this);
    }

    /**
     * Returns the component handler that was created by this tag. Note that
     * depending on the implementation of the <code>ComponentManager</code>
     * this value may be <b>null </b> before the tag's body has been completely
     * evaluated.
     *
     * @return the created component handler
     */
    public ComponentHandler<?> getComponentHandler()
    {
        return componentHandler;
    }

    /**
     * Returns the component managed by this tag. This implementation obtains
     * this component from the <code>ComponentHandler</code>. If no
     * <code>ComponentHandler</code> has been created yet, <b>null</b> is
     * returned.
     *
     * @return the component managed by this tag
     */
    @Override
    public Object getComponent()
    {
        return (getComponentHandler() != null) ? getComponentHandler()
                .getComponent() : null;
    }

    /**
     * Returns the field level validator.
     *
     * @return the field level validator
     */
    public ValidatorWrapper getFieldValidator()
    {
        return fieldValidator;
    }

    /**
     * Sets the field level validator.
     *
     * @param fieldValidator the field level validator
     */
    public void setFieldValidator(ValidatorWrapper fieldValidator)
    {
        this.fieldValidator = fieldValidator;
    }

    /**
     * Returns the form level validator.
     *
     * @return the form level validator
     */
    public ValidatorWrapper getFormValidator()
    {
        return formValidator;
    }

    /**
     * Sets the form level validator.
     *
     * @param formValidator the form level validator
     */
    public void setFormValidator(ValidatorWrapper formValidator)
    {
        this.formValidator = formValidator;
    }

    /**
     * Returns a flag whether for this input component no field handler should
     * be created.
     *
     * @return the no field flag
     */
    public boolean isNoField()
    {
        return noField;
    }

    /**
     * Setter method for the no field attribute.
     *
     * @param noField the attribute value
     */
    public void setNoField(boolean noField)
    {
        this.noField = noField;
    }

    /**
     * Returns the name of the associated property in the form bean.
     *
     * @return the name of the property
     * @see net.sf.jguiraffe.gui.forms.FieldHandler#getPropertyName()
     */
    public String getPropertyName()
    {
        return propertyName;
    }

    /**
     * Setter method for the propertyName attribute.
     *
     * @param propertyName the attribute value
     */
    public void setPropertyName(String propertyName)
    {
        this.propertyName = propertyName;
    }

    /**
     * Returns the name of the type used by the field handler.
     *
     * @return the field handler data type
     */
    public String getTypeName()
    {
        return typeName;
    }

    /**
     * Setter method for the typeName attribute.
     *
     * @param typeName the attribute value
     */
    public void setTypeName(String typeName)
    {
        this.typeName = typeName;
    }

    /**
     * Returns a comma separated list of names of the component groups this
     * component should be added to.
     *
     * @return a list of group names
     */
    public String getGroups()
    {
        return groups;
    }

    /**
     * Setter method of the groups attribute.
     *
     * @param groups the attribute value
     */
    public void setGroups(String groups)
    {
        this.groups = groups;
    }

    /**
     * Returns the read transformer.
     *
     * @return the read transformer
     */
    public TransformerWrapper getReadTransformer()
    {
        return readTransformer;
    }

    /**
     * Sets the read transformer.
     *
     * @param readTransformer the read transformer
     */
    public void setReadTransformer(TransformerWrapper readTransformer)
    {
        this.readTransformer = readTransformer;
    }

    /**
     * Returns the write transformer.
     *
     * @return the write transformer
     */
    public TransformerWrapper getWriteTransformer()
    {
        return writeTransformer;
    }

    /**
     * Sets the write transformer.
     *
     * @param writeTransformer the write transformer
     */
    public void setWriteTransformer(TransformerWrapper writeTransformer)
    {
        this.writeTransformer = writeTransformer;
    }

    /**
     * Returns the display name of this input component. This name can be either
     * directly defined or using a resource ID. If no display name is defined,
     * <b>null</b> is returned.
     *
     * @return the display name for this input component
     */
    public String getDisplayName()
    {
        return displayData.getCaption();
    }

    /**
     * Set method of the displayName attribute.
     *
     * @param s the attribute's value
     */
    public void setDisplayName(String s)
    {
        displayData.setText(s);
    }

    /**
     * Set method of the displayNameres attribute.
     *
     * @param s the attribute's value
     */
    public void setDisplayNameres(String s)
    {
        displayData.setTextres(s);
    }

    /**
     * Set method of the displayNamegrp attribute.
     *
     * @param s the attribute's value
     */
    public void setDisplayNamegrp(String s)
    {
        displayData.setResgrp(s);
    }

    /**
     * Returns the type of the field handler that is associated with this
     * component. This implementation checks whether a type name attribute was
     * specified. If this is the case, the name is converted to a class object.
     * Otherwise <b>null </b> is returned.
     *
     * @return the type of the field handler
     * @throws FormBuilderException if an error occurs
     */
    public Class<?> getComponentType() throws FormBuilderException
    {
        if (dataType != null)
        {
            return dataType;
        }
        else if (getTypeName() == null)
        {
            return null;
        }
        else
        {
            try
            {
                return ClassLoaderUtils.loadClass(getTypeName(), getClass());
            }
            catch (ClassNotFoundException cex)
            {
                throw new FormBuilderException(
                        "Invalid value for the typeName attribute!", cex);
            }
        }
    }

    /**
     * Allows to explicitly set the type of the corresponding field handler
     * object. The class set here takes precedence over a typeName attribute.
     * The method could be called by nested transformer tags.
     *
     * @param type the data type of this component
     */
    public void setComponentType(Class<?> type)
    {
        dataType = type;
    }

    /**
     * Checks whether the {@code name} attribute is set. If this is not the
     * case, an exception is thrown. This method is called by {@link #process()}
     * . If derived classes need to check the existence of a {@code name}
     * attribute at an earlier point of time, they can use this method.
     *
     * @throws MissingAttributeException if no {@code name} attribute is
     *         specified
     */
    protected void checkName() throws MissingAttributeException
    {
        if (getName() == null)
        {
            throw new MissingAttributeException("name");
        }
    }

    /**
     * Performs steps before evaluation of this tag's body. This implementation
     * calls <code>createComponentHandler()</code> for the first time.
     *
     * @throws JellyTagException if the tag is incorrectly used
     * @throws FormBuilderException if an error occurs
     */
    @Override
    protected void processBeforeBody() throws JellyTagException,
            FormBuilderException
    {
        componentHandler = createComponentHandler(getBuilderData()
                .getComponentManager(), true);
    }

    /**
     * Executes this tag. This implementation performs all steps for creating an
     * input component and storing it in the builder results. Derived classes
     * only need to implement the real component create operation.
     *
     * @throws FormBuilderException if an error occurs
     * @throws JellyTagException if a Jelly related error occurs
     */
    @Override
    protected void process() throws FormBuilderException, JellyTagException
    {
        checkName();
        super.process();

        ComponentBuilderData data = getBuilderData();
        componentHandler = createComponentHandler(data.getComponentManager(),
                false);
        if (isNoField())
        {
            data.storeComponentHandler(getName(), getComponentHandler());
        }
        else
        {
            insertField(createFieldHandler(getComponentHandler()));
        }
        if (getComponentHandler().getOuterComponent() != null)
        {
            insertComponent(getName(), getComponentHandler()
                    .getOuterComponent());
        }

        handleGroups();
    }

    /**
     * Creates a field handler object for the input component. When this method
     * is invoked the component handler has already been created. This
     * implementation uses the current field handler factory to create and
     * initialize the handler object.
     *
     * @param componentHandler the component handler
     * @return the field handler
     * @throws FormBuilderException if an error occurs
     */
    protected FieldHandler createFieldHandler(ComponentHandler<?> componentHandler)
            throws FormBuilderException
    {
        return getBuilderData().getFieldHandlerFactory().createFieldHandler(
                this, componentHandler);
    }

    /**
     * Stores a newly created field handler in the central builder data object.
     * This ensures that the field is also added to the form object constructed
     * in the builder process.
     *
     * @param fieldHandler the field handler
     */
    protected void insertField(FieldHandler fieldHandler)
    {
        getBuilderData().storeFieldHandler(getName(), fieldHandler);
    }

    /**
     * Handles the assignment of this component to component groups. Evaluates
     * the <code>groups</code> attribute and checks whether this tag is nested
     * inside a group tag.
     *
     * @throws FormBuilderException if an error occurs when associating this
     * component to a group
     */
    protected void handleGroups() throws FormBuilderException
    {
        if (getGroups() == null)
        {
            ComponentGroupTag groupTag =
                    (ComponentGroupTag) findAncestorWithClass(ComponentGroupTag.class);
            if (groupTag != null)
            {
                setGroups(groupTag.getName());
            }
        }

        if (getGroups() != null)
        {
            addToGroups(getGroups());
        }
    }

    /**
     * Adds this component's name to all groups in the given list.
     *
     * @param groupNames a comma separated list of group names
     * @throws FormBuilderException if at least one group name is invalid
     */
    protected void addToGroups(String groupNames) throws FormBuilderException
    {
        StringTokenizer tok = new StringTokenizer(groupNames, GROUP_SEPARATORS);
        while (tok.hasMoreTokens())
        {
            String grpName = tok.nextToken();
            try
            {
                ComponentGroup group = ComponentGroup.fromContext(getContext(),
                        grpName);
                group.addComponent(getName());
            }
            catch (NoSuchElementException nex)
            {
                throw new FormBuilderException(
                        "Could not find group with name " + grpName);
            }
        }
    }

    /**
     * Creates the component handler used by this input component. This method
     * must be implemented by concrete sub classes to create and initialize the
     * specific component. It is called twice during execution of this tag: Once
     * before evaluation of the tag's body with a value of <b>true </b> for the
     * <code>create</code> argument, and once after body evaluation with an
     * argument value of <b>false </b>.
     *
     * @param manager the component manager
     * @param create the create flag
     * @return the new component handler
     * @throws FormBuilderException if an error occurs
     * @throws JellyTagException if a Jelly-related error occurs, e.g. the tag
     *         is incorrectly used or required attributes are missing
     */
    protected abstract ComponentHandler<?> createComponentHandler(
            ComponentManager manager, boolean create)
            throws FormBuilderException, JellyTagException;
}
