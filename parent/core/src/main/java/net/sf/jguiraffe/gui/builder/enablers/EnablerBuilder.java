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
package net.sf.jguiraffe.gui.builder.enablers;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

/**
 * <p>
 * A helper class for the convenient creation of standard {@link ElementEnabler}
 * objects.
 * </p>
 * <p>
 * This class supports the creation of {@link ElementEnabler} objects based on
 * textual specifications. It is intended to be used where a compact notation of
 * enabler objects is required. One example are bean declarations for the
 * <em>dependency injection framework</em>: Action tasks that issue commands can
 * be associated with an {@link ElementEnabler} to disable certain elements
 * while the action task is running. The XML-based declaration of complex
 * enabler can be pretty inconvenient - especially if a
 * {@link ChainElementEnabler} with multiple child elements is used. Here a
 * shorter form for declaring enablers is helpful.
 * </p>
 * <p>
 * The basic usage of {@code EnablerBuilder} is to call the
 * {@link #addSpecification(String)} method an arbitrary number of times. Each
 * invocation adds a string with the specification for a concrete
 * {@link ElementEnabler}. When all specifications have been added the
 * {@code build()} method is called which creates the resulting
 * {@link ElementEnabler}. The result of this method depends on the number of
 * specifications that have been added:
 * <ul>
 * <li>If there is no specification, a {@link NullEnabler} is returned.</li>
 * <li>If there is exactly one specification, an {@link ElementEnabler}
 * corresponding to the specification is returned.</li>
 * <li>In all other cases result is a {@link ChainElementEnabler} with
 * corresponding child {@link ElementEnabler} objects.</li>
 * </ul>
 * </p>
 * <p>
 * A valid specification for an {@link ElementEnabler} has the following form:
 * It starts with a prefix (see below), followed by a colon. After the colon a
 * parameter to be passed to the {@link ElementEnabler}'s constructor is
 * expected. The support prefix values are shown in the following table:
 * <table border="1">
 * <tr>
 * <th>Prefix</th>
 * <th>Element enabler class</th>
 * </tr>
 * <tr>
 * <td>action</td>
 * <td>{@link ActionEnabler}</td>
 * </tr>
 * <tr>
 * <td>actiongroup</td>
 * <td>{@link ActionGroupEnabler}</td>
 * </tr>
 * <tr>
 * <td>comp</td>
 * <td>{@link ComponentEnabler}</td>
 * </tr>
 * </table>
 * </p>
 * <p>
 * The prefix is not case sensitive. Whitespace are ignored. Multiple
 * specifications can be concatenated to a single one using a comma (","). So
 * the following are valid examples for {@link ElementEnabler} specifications:
 * <dl>
 * <dt>action:testAction</dt>
 * <dd>defines an {@link ActionEnabler} for an action named <em>testAction</em></dd>
 * <dt>ActionGroup : testGroup</dt>
 * <dd>defines an {@link ActionGroupEnabler} for an action group named
 * <em>testGroup</em></dd>
 * <dt>COMP : myButton</dt>
 * <dd>defines an {@link ComponentEnabler} for the <em>myButton</em> component</dd>
 * <dt>action:action1, action:action2,comp:button1, comp:button2</dt>
 * <dd>defines multiple enablers, two action enablers and two component enablers
 * </dd>
 * </dl>
 * </p>
 * <p>
 * This class only supports the standard {@link ElementEnabler} implementations.
 * There is no extension mechanism. This is because this class exists only for
 * convenience. If access to other {@link ElementEnabler} implementations is
 * needed, the objects have to be created directly.
 * </p>
 * <p>
 * Implementation note: This class is not thread-safe.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: EnablerBuilder.java 205 2012-01-29 18:29:57Z oheger $
 */
public class EnablerBuilder
{
    /** A regular expression for matching enabler specifications. */
    private static final Pattern PAT_SPEC = Pattern
            .compile("(\\S+)\\s*:\\s*(\\S+)");

    /** The separator for multiple specifications. */
    private static final String SPEC_SEPARATOR = ",";

    /** Constant for the prefix for an action enabler. */
    private static final String PREFIX_ACTION = "action";

    /** Constant for the prefix for an action group enabler. */
    private static final String PREFIX_ACTGROUP = "actiongroup";

    /** Constant for the prefix for a component enabler. */
    private static final String PREFIX_COMP = "comp";

    /** Constant for the index of the prefix group. */
    private static final int GRP_PREFIX = 1;

    /** Constant for the index of the name group. */
    private static final int GRP_NAME = 2;

    /** A buffer with the specifications added so far. */
    private StringBuilder specs;

    /**
     * Adds the given specification to this builder. The specification must
     * conform to the format defined in the class comment. It must not be
     * <b>null</b> or empty.
     *
     * @param spec the specification to add
     * @return a reference to this builder for method chaining
     * @throws IllegalArgumentException if the specification is undefined
     */
    public EnablerBuilder addSpecification(String spec)
    {
        String s = StringUtils.stripToNull(spec);
        if (s == null)
        {
            throw new IllegalArgumentException(
                    "Specification must not be undefined!");
        }

        if (specs == null)
        {
            specs = new StringBuilder(spec);
        }
        else
        {
            specs.append(SPEC_SEPARATOR).append(spec);
        }

        return this;
    }

    /**
     * Creates an {@code ElementEnabler} based on the specifications that have
     * been added to this builder. This method also resets the state of this
     * builder; all specifications are removed, so that this instance can be
     * used for defining another {@code ElementEnabler}.
     *
     * @return an {@link ElementEnabler} based on the so far added
     *         specifications
     * @throws IllegalArgumentException if an invalid specification is encountered
     */
    public ElementEnabler build()
    {
        if (specs == null)
        {
            return NullEnabler.INSTANCE;
        }

        String[] singleSpecs = specs.toString().split(SPEC_SEPARATOR);
        ElementEnabler result;
        if (singleSpecs.length == 1)
        {
            result = processSpecification(singleSpecs[0].trim());
        }
        else
        {
            result = createChainEnabler(singleSpecs);
        }

        reset();
        return result;
    }

    /**
     * Resets this builder. All specifications that have been added so far are
     * removed. Note: This method is also invoked by {@link #build()}, so it is
     * not necessary to reset the builder after an object was created.
     */
    public void reset()
    {
        specs = null;
    }

    /**
     * Processes the specified specification and creates a corresponding
     * {@link ElementEnabler}. This method is called for each specification that
     * was added to this builder.
     *
     * @param spec the specification for a single {@code ElementEnabler}
     *        (already trimmed)
     * @return the corresponding {@code ElementEnabler}
     * @throws IllegalArgumentException if the specification is invalid
     */
    protected ElementEnabler processSpecification(String spec)
    {
        Matcher m = PAT_SPEC.matcher(spec);
        if (!m.matches())
        {
            throw new IllegalArgumentException(
                    "Not a valid enabler specification: " + spec);
        }

        return createEnabler(m.group(GRP_PREFIX).toLowerCase(Locale.ENGLISH), m
                .group(GRP_NAME));
    }

    /**
     * Creates an {@code ElementEnabler} for the specified prefix. This method
     * is called by {@link #processSpecification(String)} after the
     * specification was passed. It creates the actual enabler.
     *
     * @param prefix the prefix indicating the enabler type
     * @param name the name of the element to be enabled
     * @return the corresponding {@code ElementEnabler}
     * @throws IllegalArgumentException if no enabler can be created for this
     *         prefix
     */
    protected ElementEnabler createEnabler(String prefix, String name)
    {
        if (PREFIX_ACTION.equals(prefix))
        {
            return new ActionEnabler(name);
        }
        else if (PREFIX_ACTGROUP.equals(prefix))
        {
            return new ActionGroupEnabler(name);
        }
        else if (PREFIX_COMP.equals(prefix))
        {
            return new ComponentEnabler(name);
        }

        throw new IllegalArgumentException("Unknown ElementEnabler prefix: "
                + prefix);
    }

    /**
     * Creates a chain enabler for multiple specifications.
     *
     * @param allSpecs an array with the specifications
     * @return the corresponding chain enabler
     * @throws IllegalStateException if a specification is invalid
     */
    private ChainElementEnabler createChainEnabler(String[] allSpecs)
    {
        List<ElementEnabler> enablers = new ArrayList<ElementEnabler>(
                allSpecs.length);

        for (String spec : allSpecs)
        {
            enablers.add(processSpecification(spec.trim()));
        }

        return new ChainElementEnabler(enablers);
    }
}
