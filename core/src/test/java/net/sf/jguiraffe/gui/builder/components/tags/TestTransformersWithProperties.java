/*
 * Copyright 2006-2012 The JGUIraffe Team.
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

import net.sf.jguiraffe.gui.forms.DefaultFieldHandler;
import net.sf.jguiraffe.transform.ChainValidator;
import net.sf.jguiraffe.transform.Transformer;
import net.sf.jguiraffe.transform.TransformerContext;

/**
 * Test class for transformer tags in collaboration with property tags.
 *
 * @author Oliver Heger
 * @version $Id: TestTransformersWithProperties.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestTransformersWithProperties extends AbstractTagTest
{
    /** Constant for the suffix property. */
    private static final String PROP_SUFFIX = "suffix";

    /** Constant for the name of the field. */
    private static final String FIELD = "text1";

    /** Constant for the name of the test script. */
    private static final String SCRIPT = "transformers";

    /**
     * Tests whether the transformer is correctly initialized.
     */
    public void testInitTransformer() throws Exception
    {
        executeScript(SCRIPT);
        DefaultFieldHandler fh = (DefaultFieldHandler) builderData
                .getFieldHandler(FIELD);
        assertNotNull("No read transformer set", fh.getReadTransformer());
        assertEquals("Wrong transformation", "*Test#", fh.getReadTransformer()
                .transform("Test"));
    }

    /**
     * Tests whether the chain validator is correctly initialized.
     */
    public void testInitValidator() throws Exception
    {
        executeScript(SCRIPT);
        DefaultFieldHandler fh = (DefaultFieldHandler) builderData
                .getFieldHandler(FIELD);
        assertNotNull("No field validator set", fh.getSyntaxValidator());
        ChainValidator cv = (ChainValidator) ((ValidatorTag.ValidatorWrapperImpl) fh
                .getSyntaxValidator()).getValidator();
        assertEquals("Wrong number of child validators", 2, cv.size());
    }

    /**
     * A test Transformer implementation that will be created by the test
     * script.
     */
    public static class TransformerTestImpl implements Transformer
    {
        /** Stores the prefix for transformed objects. */
        private String prefix;

        public String getPrefix()
        {
            return prefix;
        }

        public void setPrefix(String prefix)
        {
            this.prefix = prefix;
        }

        /**
         * Transforms the passed in object. Transformation means that a prefix
         * (specified by an instance property) and a suffix (obtained from the
         * context properties) will be applied.
         *
         * @param o the object to transform
         * @param ctx the transformer context
         * @return the transformed object
         * @throws Exception in case of an error
         */
        public Object transform(Object o, TransformerContext ctx)
                throws Exception
        {
            StringBuilder buf = new StringBuilder();
            buf.append(getPrefix()).append(o);
            buf.append(ctx.properties().get(PROP_SUFFIX));
            return buf.toString();
        }
    }
}
