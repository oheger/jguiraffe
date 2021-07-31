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
package net.sf.jguiraffe.gui.builder.components.tags;

import net.sf.jguiraffe.di.ConversionHelper;
import net.sf.jguiraffe.gui.builder.di.DIBuilderData;
import net.sf.jguiraffe.gui.builder.di.tags.ClassDescData;

import org.apache.commons.beanutils.Converter;
import org.apache.commons.jelly.JellyTagException;

/**
 * <p>
 * A specialized tag handler class for adding custom data type converters in a
 * builder script.
 * </p>
 * <p>
 * The <em>dependency injection framework</em> supports data type converters
 * that are automatically invoked when methods on beans are invoked or
 * properties are set to convert data to the target types required by the method
 * signatures. There are multiple ways to extent the default converters provided
 * by the framework. Using this tag it is possible to define custom type
 * converters directly in a builder script. This is pretty convenient because it
 * does not require any specific glue code when invoking the builder.
 * </p>
 * <p>
 * The basic idea is that custom data type converters are defined as regular
 * beans using variants of the {@code <di:bean>} tag. Then this tag is used to
 * register these beans as converters at the current builder context. Because
 * {@code ConverterTag} extends {@link UseBeanBaseTag} multiple ways for
 * referencing beans are supported; it is even possible to create new bean
 * instances by calling their standard constructor.
 * </p>
 * <p>
 * The scope of the data type converters defined by this tag should determine
 * the scripts in which to place the tag. If the converters are very basic and
 * used in many builder scripts of the application, they should be already
 * registered in the script defining the main window - i.e. the first builder
 * script executed by the application. There is a kind of an inheritance
 * mechanism: data type converters registered for a root {@code BeanStore} are
 * also available in builder scripts defining children of this root store.
 * Specialized converters that are only required for a single builder script can
 * be defined locally in this script.
 * </p>
 * <p>
 * Of course, all attributes supported by the base class are also available for
 * {@code ConverterTag}. The following table lists the additional attributes
 * introduced by this class:
 * </p>
 * <p>
 * <table border="1">
 * <tr>
 * <th>Attribute</th>
 * <th>Description</th>
 * <th>Optional</th>
 * </tr>
 * <tr>
 * <td valign="top">converterTargetClass</td>
 * <td>With this attribute the target class of the converter can be specified
 * (either as a {@code java.lang.Class} object or as a string that will be
 * converted to a class.</td>
 * <td valign="top">Yes</td>
 * </tr>
 * <tr>
 * <td valign="top">converterTargetClassName</td>
 * <td>Using this attribute the name of the target class can be specified. While
 * the {@code targetClass} attribute requires that the class specified can be
 * loaded using the default class loader, this attribute - together with the
 * {@code targetClassLoader} attribute - supports loading the class from an
 * alternative class loader.</td>
 * <td valign="top">Yes</td>
 * </tr>
 * <tr>
 * <td valign="top">converterTargetClassLoader</td>
 * <td>This attribute is only evaluated if the {@code targetClassName} attribute
 * is set. In this case it references the class loader to be used for resolving
 * the class name. A class loader with the given name is queried from the
 * current {@link net.sf.jguiraffe.di.ClassLoaderProvider ClassLoaderProvider}.</td>
 * <td valign="top">Yes</td>
 * </tr>
 * <tr>
 * <td valign="top">isBaseClassConverter</td>
 * <td>Determines whether the converter should be registered as a normal or a
 * base class converter. If this attribute has an arbitrary value, it is
 * registered as base class converter.</td>
 * <td valign="top">Yes</td>
 * </tr>
 * </table>
 * </p>
 * <p>
 * The target class must have been specified in one of the supported ways,
 * otherwise an exception is thrown. The following example shows a typical usage
 * scenario for this tag:
 *
 * <pre>
 * &lt;!-- Definition of the bean for the converter.--&gt;
 * &lt;di:bean name="converterBean" beanClass="com.mypackage.MyConverter"&gt;
 *   ...
 * &lt;/di:bean&gt;
 *
 * &lt;!-- Register the bean as converter.--&gt;
 * &lt;f:converter beanName="converterBean"
 *   converterTargetClass="com.mypackage.MyDataClass"/&gt;
 * </pre>
 *
 * After this declaration bean definitions can be placed in the Jelly script
 * with properties or method parameters of the type
 * {@code com.mypackage.MyDataClass}. The newly registered converter will be
 * automatically called to perform the data conversion to this type.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: ConverterTag.java 205 2012-01-29 18:29:57Z oheger $
 */
public class ConverterTag extends UseBeanBaseTag
{
    /** Constant for the name of the converter target class attribute. */
    protected static final String ATTR_CONVERTER_TARGET_CLASS =
            "converterTargetClass";

    /** Constant for the name of the converter target class name attribute. */
    protected static final String ATTR_CONVERTER_TARGET_CLASS_NAME =
            "converterTargetClassName";

    /** Constant for the name of the converter target class loader attribute. */
    protected static final String ATTR_CONVERTER_TARGET_CLASS_LOADER =
            "converterTargetClassLoader";

    /** Constant for the name of the base class converter attribute. */
    protected static final String ATTR_BASE_CLASS_CONVERTER =
            "isBaseClassConverter";

    /**
     * Creates a new instance of {@code ConverterTag}.
     */
    public ConverterTag()
    {
        super(null, Converter.class);
        addIgnoreProperties(ATTR_BASE_CLASS_CONVERTER,
                ATTR_CONVERTER_TARGET_CLASS,
                ATTR_CONVERTER_TARGET_CLASS_LOADER,
                ATTR_CONVERTER_TARGET_CLASS_NAME);
    }

    /**
     * Processes the bean created by this tag. This implementation registers the
     * specified bean as a converter at the central {@link ConversionHelper}
     * object. A target class for the converter must have been provided,
     * otherwise an exception is thrown.
     *
     * @param bean the newly created bean
     * @return a flag whether the bean could be processed
     * @throws JellyTagException if an error occurs
     */
    @Override
    protected boolean passResults(Object bean) throws JellyTagException
    {
        ClassDescData cdd = createTargetClassDescription();
        Class<?> convTargetClass = fetchTargetClass(cdd);
        boolean baseClassConv =
                getAttributeStr(ATTR_BASE_CLASS_CONVERTER) != null;

        if (baseClassConv)
        {
            fetchConversionHelper().registerBaseClassConverter(
                    (Converter) bean, convTargetClass);
        }
        else
        {
            fetchConversionHelper().registerConverter((Converter) bean,
                    convTargetClass);
        }

        return true;
    }

    /**
     * Creates a {@link ClassDescData} object for the target class of the
     * converter. This method evaluates the several attributes that can be used
     * to specify the converter's target class. From these attributes the
     * {@link ClassDescData} object is populated.
     *
     * @return the initialized {@link ClassDescData} object
     */
    protected ClassDescData createTargetClassDescription()
    {
        ClassDescData cdd = new ClassDescData();
        cdd.setClassLoaderName(getAttributeStr(ATTR_CONVERTER_TARGET_CLASS_LOADER));
        cdd.setTargetClassName(getAttributeStr(ATTR_CONVERTER_TARGET_CLASS_NAME));
        cdd.setTargetClass(evaluateTargetClassAttribute());
        return cdd;
    }

    /**
     * Obtains the target class of the converter from the specified
     * {@link ClassDescData} object. This method is called by
     * {@link #passResults(Object)}.
     *
     * @param cdd the description object populated from the attributes of this
     *        tag
     * @return the target class of the converter
     * @throws JellyTagException if the class cannot be resolved
     */
    protected Class<?> fetchTargetClass(ClassDescData cdd)
            throws JellyTagException
    {
        DIBuilderData diData = fetchDIBuilderData();
        return cdd.resolveClass(diData.getClassLoaderProvider());
    }

    /**
     * Helper method for obtaining the current {@link DIBuilderData} object from
     * the Jelly context.
     *
     * @return the {@link DIBuilderData} object
     */
    private DIBuilderData fetchDIBuilderData()
    {
        return DIBuilderData.get(getContext());
    }

    /**
     * Returns the target class that was specified using the target class
     * attribute (if any). This implementation returns the value of this
     * attribute and converts it if necessary to a Class object. Result is
     * <b>null</b> if the attribute was not specified.
     *
     * @return the class obtained from the target class attribute
     */
    private Class<?> evaluateTargetClassAttribute()
    {
        Object clsAttr = getAttributes().get(ATTR_CONVERTER_TARGET_CLASS);
        return fetchConversionHelper().convert(Class.class, clsAttr);
    }

    /**
     * Helper method for retrieving the current {@link ConversionHelper} object
     * from the Jelly context.
     *
     * @return the conversion helper object
     */
    private ConversionHelper fetchConversionHelper()
    {
        DIBuilderData diData = fetchDIBuilderData();
        return diData.getInvocationHelper().getConversionHelper();
    }
}
