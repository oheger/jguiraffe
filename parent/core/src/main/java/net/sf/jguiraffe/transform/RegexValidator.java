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
package net.sf.jguiraffe.transform;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.MapConfiguration;

/**
 * <p>
 * A specialized {@code Validator} implementation that uses regular expressions
 * to validate user input.
 * </p>
 * <p>
 * This is a very generic yet powerful {@code Validator} class. It can be
 * configured with a regular expression string and some flags influencing the
 * matching behavior. From this data a {@code java.util.regex.Pattern} object is
 * created which is used to validate the data passed to the
 * {@link #isValid(Object, TransformerContext)} method. {@code isValid()}
 * transforms the passed in object to a string and applies the regular
 * expression to it.
 * </p>
 * <p>
 * {@code RegexValidator} provides a couple of properties for defining the
 * {@code java.util.regex.Pattern} object that is the base for all validation
 * operations. By using a {@code <properties>} section in the builder script
 * these properties can be overridden for a local validation. Here the following
 * properties are evaluated:
 * </p>
 * <p>
 * <table * border="1">
 * <tr>
 * <th>Property</th>
 * <th>Description</th>
 * <th>Default</th>
 * </tr>
 * <tr>
 * <td valign="top">regex</td>
 * <td>The regular expression as a string. This string is used for creating the
 * {@code Pattern} object. It must be a valid regular expression which conforms
 * to the syntax expected by the {@code java.util.regex.Pattern} class.</td>
 * <td valign="top">.*</td>
 * </tr>
 * <tr>
 * <td valign="top">caseInsensitive</td>
 * <td>A boolean flag which determines whether string comparisons should be case
 * insensitive.</td>
 * <td valign="top">false</td>
 * </tr>
 * <tr>
 * <td valign="top">dotAll</td>
 * <td>A boolean flag which indicates whether strings to be matched can contain
 * line terminators. This flag corresponds to the {@code DOTALL} flag of the
 * {@code Pattern} class.</td>
 * <td valign="top">false</td>
 * </tr>
 * </table>
 * </p>
 * <p>
 * If validation fails, the following possible error messages can be produced:
 * <table border="1">
 * <tr>
 * <th>Message key</th>
 * <th>Description</th>
 * <th>Parameters</th>
 * </tr>
 * <tr>
 * <td valign="top"><code>{@link ValidationMessageConstants#ERR_PATTERN}</code></td>
 * <td valign="top">The input is not correct according the specified pattern.</td>
 * <td valign="top">{0} = the regular expression</td>
 * </tr>
 * </table>
 * </p>
 * <p>
 * This class is not thread-safe. However, a single instance can be shared
 * between multiple input fields. In a builder script, a {@code RegexValidator}
 * instance can be declared and initialized with default settings. This instance
 * can then be associated with multiple input fields that all use the same
 * validation pattern. If an input field requires slightly different settings,
 * properties can be used to override the default settings. This can look as
 * follows:
 *
 * <pre>
 * <!-- The bean for the validator -->
 * <di:bean beanClass="net.sf.jguiraffe.transform.RegexValidator"
 *   name="regexValidator">
 *   <di:setProperty property="regex" value="[0-9]*"/>
 * </di:bean>
 * ...
 * <!-- Associate the validator with an input field.-->
 * <f:textfield name="text1">
 *   <f:validator phase="syntax" beanName="regexValidator"/>
 * </f:textfield>
 * <!-- Another text field with slightly different requirements. -->
 * <f:textfield name="text2">
 *   <f:validator phase="syntax" beanName="regexValidator">
 *     <f:properties>
 *       <f:property key="dotAll" value="true"/>
 *     </f:properties>
 *   </f:validator>
 * </f:textfield>
 * </pre>
 *
 * In this example there are two text fields which are both assigned the {@code
 * RegexValidator} instance. The first text field uses the validator's default
 * settings. The second field overrides a property.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: RegexValidator.java 205 2012-01-29 18:29:57Z oheger $
 */
public class RegexValidator implements Validator
{
    /** Constant for the regex property. */
    protected static final String PROP_REGEX = "regex";

    /** Constant for the caseInsensitive property. */
    protected static final String PROP_CASE_INSENSITIVE = "caseInsensitive";

    /** Constant for the multiLine property. */
    protected static final String PROP_DOT_ALL = "dotAll";

    /** Constant for the default regular expression string. */
    private static final String DEF_REGEX = ".*";

    /** Stores the current regular expression pattern. */
    private Pattern pattern;

    /** Stores the regular expression as string. */
    private String regex = DEF_REGEX;

    /** The dot-all flag. */
    private boolean dotAll;

    /** The case insensitive flag. */
    private boolean caseInsensitive;

    /**
     * Returns the regular expression as a string.
     *
     * @return the regular expression
     */
    public String getRegex()
    {
        return regex;
    }

    /**
     * Sets the regular expression as a string. The string passed to this method
     * must be compatible with the syntax used by the {@code
     * java.util.regex.Pattern} class.
     *
     * @param regex the regular expression string (must not be <b>null</b>)
     * @throws IllegalArgumentException if the parameter is <b>null</b>
     */
    public void setRegex(String regex)
    {
        if (regex == null)
        {
            throw new IllegalArgumentException(
                    "Regular expression must not be null!");
        }

        this.regex = regex;
        pattern = null;
    }

    /**
     * Returns a flag whether the dot place holder should also match line
     * terminators.
     *
     * @return the dot all flag
     */
    public boolean isDotAll()
    {
        return dotAll;
    }

    /**
     * Sets a flag whether the dot place holder should also match line
     * terminators. This property corresponds to the {@code DOTALL} flag of the
     * {@code java.util.regex.Pattern} class.
     *
     * @param dotAll the dot all flag
     */
    public void setDotAll(boolean dotAll)
    {
        this.dotAll = dotAll;
        pattern = null;
    }

    /**
     * Returns a flag whether string matches are case insensitive.
     *
     * @return the case insensitive flag
     */
    public boolean isCaseInsensitive()
    {
        return caseInsensitive;
    }

    /**
     * Sets a flag whether string matches are case insensitive. This property
     * corresponds to the {@code CASE_INSENSITIVE} flag of the {@code
     * java.util.regex.Pattern} class.
     *
     * @param caseInsensitive the case insensitive flag
     */
    public void setCaseInsensitive(boolean caseInsensitive)
    {
        this.caseInsensitive = caseInsensitive;
        pattern = null;
    }

    /**
     * Performs the validation as described in the class comment. The passed in
     * object is transformed to a string and matched against the regular
     * expression. <b>null</b> objects are accepted.
     *
     * @param o the object to be validated
     * @param ctx the transformer context
     * @return an object with the results of the validation
     */
    public ValidationResult isValid(Object o, TransformerContext ctx)
    {
        if (o == null)
        {
            return DefaultValidationResult.VALID;
        }

        Pattern pat = getPattern(ctx);
        Matcher m = pat.matcher(o.toString());
        if (m.matches())
        {
            return DefaultValidationResult.VALID;
        }

        ValidationMessage msg = ctx
                .getValidationMessageHandler()
                .getValidationMessage(ctx,
                        ValidationMessageConstants.ERR_PATTERN, fetchRegex(ctx));
        return new DefaultValidationResult.Builder().addValidationMessage(msg)
                .build();
    }

    /**
     * Returns a {@code Pattern} object to be used for the validation operation.
     * This class maintains a {@code Pattern} object that corresponds to the
     * properties set for an instance. If a property is changed, the pattern is
     * reset. It is then created on demand if it is accessed. It is also
     * possible to override properties in the {@code TransformerContext}. If
     * this is the case, a new {@code Pattern} object is always created.
     *
     * @param ctx the {@code TransformerContext}
     * @return the {@code Pattern} object to be used
     */
    protected Pattern getPattern(TransformerContext ctx)
    {
        if (overridesProperties(ctx))
        {
            return createPattern(ctx);
        }
        else
        {
            if (pattern == null)
            {
                pattern = createPattern(ctx);
            }
            return pattern;
        }
    }

    /**
     * Tests whether properties in the specified {@code TransformerContext}
     * override properties of this object. This method is called by
     * {@link #getPattern(TransformerContext)}. If it returns <b>true</b>, a new
     * {@code Pattern} object is created.
     *
     * @param ctx the {@code TransformerContext}
     * @return a flag whether default properties are overridden in the context
     */
    protected boolean overridesProperties(TransformerContext ctx)
    {
        Map<?, ?> props = ctx.properties();
        return props.containsKey(PROP_REGEX)
                || props.containsKey(PROP_CASE_INSENSITIVE)
                || props.containsKey(PROP_DOT_ALL);
    }

    /**
     * Creates a new {@code Pattern} object. This method is called by
     * {@link #getPattern(TransformerContext)} whenever a new pattern needs to
     * be created. This implementation creates a new {@code Pattern} object
     * based on the properties set in the {@code TransformerContext}. The
     * properties of this object are used as defaults if a property is not set
     * in the context. Thus the properties in the context override local
     * properties if they are set.
     *
     * @param ctx the {@code TransformerContext}
     * @return the newly created {@code Pattern} object
     */
    protected Pattern createPattern(TransformerContext ctx)
    {
        Configuration config = new MapConfiguration(ctx.properties());

        int flags = 0;
        if (config.getBoolean(PROP_CASE_INSENSITIVE, isCaseInsensitive()))
        {
            flags |= Pattern.CASE_INSENSITIVE;
        }
        if (config.getBoolean(PROP_DOT_ALL, isDotAll()))
        {
            flags |= Pattern.DOTALL;
        }

        return Pattern.compile(config.getString(PROP_REGEX, getRegex()), flags);
    }

    /**
     * Obtains the current regular expression string. This is either the value
     * of the corresponding instance property or it is overridden in the
     * context.
     *
     * @param ctx the {@code TransformerContext}
     * @return the regular expression string
     */
    private String fetchRegex(TransformerContext ctx)
    {
        return ctx.properties().containsKey(PROP_REGEX) ? String.valueOf(ctx
                .properties().get(PROP_REGEX)) : getRegex();
    }
}
