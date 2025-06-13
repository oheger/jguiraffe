/*
 * Copyright 2006-2025 The JGUIraffe Team.
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.apache.commons.jelly.JellyTagException;

import net.sf.jguiraffe.transform.ChainValidator;
import net.sf.jguiraffe.transform.Validator;

/**
 * <p>
 * A tag handler class for combining multiple validators to be assigned to an
 * input component.
 * </p>
 * <p>
 * In some cases a single validator is not sufficient for testing the validity
 * of an input field; multiple validators are needed (e.g. a required validator
 * plus a data type specific validator). With this tag handler class a
 * <code>{@link ChainValidator}</code> can be created, which can combine an
 * arbitrary number of child validators.
 * </p>
 * <p>
 * This tag can be put in the body of a tag defining an input component. In the
 * tag's body an arbitrary number of nested <code>&lt;validator&gt;</code>
 * tags (as implemented by <code>{@link ValidatorTag}</code>) can be placed.
 * Each validator defined by these child tags will be added to the resulting
 * <code>{@link ChainValidator}</code>.
 * </p>
 * <p>
 * The following table lists the attributes supported by this tag: <table
 * border="1">
 * <tr>
 * <th>Attribute</th>
 * <th>Description</th>
 * <th>Optional</th>
 * </tr>
 * <tr>
 * <td valign="top">shortEvaluation</td>
 * <td>Here a boolean value can be specified controlling the
 * <code>shortEvaluation</code> flag of the resulting
 * <code>ChainValidator</code>. Have a look at
 * <code>{@link ChainValidator}</code> for more details. The default value is
 * <b>true</b>.</td>
 * <td valign="top">Yes</td>
 * </tr>
 * </table>
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: ValidatorsTag.java 205 2012-01-29 18:29:57Z oheger $
 */
public class ValidatorsTag extends ValidatorBaseTag<ChainValidator>
{
    /** A list with the child validators to add. */
    private Collection<ChildValidatorData> validators;

    /**
     * Creates a new instance of <code>ValidatorsTag</code>.
     */
    public ValidatorsTag()
    {
        super(ChainValidator.class);
        setBaseClass(ChainValidator.class);
        validators = new ArrayList<ChildValidatorData>();
    }

    /**
     * Adds a new child validator. This method is intended to be called by tags
     * in the body that define child validators.
     *
     * @param child the validator to add
     * @param props a map with optional properties for this child validator
     */
    public void addChildValidator(Validator child, Map<String, Object> props)
    {
        validators.add(new ChildValidatorData(child, props));
    }

    /**
     * Passes the resulting bean to its target. This implementation initializes
     * the resulting <code>ChainValidator</code> with the child validators
     * added by child tags.
     *
     * @param bean the (validator) bean to initialize
     * @return a flag whether the bean could be passed to the target
     * @throws JellyTagException if an error occurs
     */
    @Override
    protected boolean passResults(Object bean) throws JellyTagException
    {
        assert bean instanceof ChainValidator : "Invalid bean class!";
        ChainValidator cv = (ChainValidator) bean;
        for (ChildValidatorData cvd : validators)
        {
            cvd.initChainValidator(cv);
        }

        return super.passResults(bean);
    }

    /**
     * A simple data class for storing information about a child validator.
     */
    private static class ChildValidatorData
    {
        /** Stores the child validator. */
        private Validator validator;

        /** A map with properties. */
        private Map<String, Object> properties;

        /**
         * Creates a new instance of <code>ChildValidatorData</code> and
         * initializes it.
         *
         * @param v the child validator
         * @param props the properties
         */
        public ChildValidatorData(Validator v, Map<String, Object> props)
        {
            validator = v;
            properties = props;
        }

        /**
         * Initializes the specified chain validator with the data stored in
         * this instance.
         *
         * @param cv the chain validator
         */
        public void initChainValidator(ChainValidator cv)
        {
            cv.addChildValidator(validator, properties);
        }
    }
}
