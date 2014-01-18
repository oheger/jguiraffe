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
package net.sf.jguiraffe.gui.builder.enablers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import org.apache.commons.beanutils.ConversionException;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for {@code EnablerConverter}.
 *
 * @author Oliver Heger
 * @version $Id: TestEnablerConverter.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestEnablerConverter
{
    /** Constant for the enabler specification to convert. */
    private static final String ENABLER_SPEC = "enabler:testSpecification";

    /** The converter to be tested. */
    private EnablerConverterTestImpl converter;

    @Before
    public void setUp() throws Exception
    {
        converter = new EnablerConverterTestImpl();
    }

    /**
     * Tests whether a correct builder is created.
     */
    @Test
    public void testCreateEnablerBuilder()
    {
        assertEquals("Wrong builder object", EnablerBuilder.class, converter
                .createEnablerBuilder().getClass());
    }

    /**
     * Tries to convert a null value.
     */
    @Test(expected = ConversionException.class)
    public void testConvertNull()
    {
        converter.convert(ElementEnabler.class, null);
    }

    /**
     * Tests a successful conversion to an enabler.
     */
    @Test
    public void testConvertSuccess()
    {
        final ElementEnabler enabler =
                EasyMock.createMock(ElementEnabler.class);
        EasyMock.replay(enabler);
        EnablerBuilderTestImpl builder = new EnablerBuilderTestImpl()
        {
            @Override
            public ElementEnabler build()
            {
                return enabler;
            };
        };
        converter.mockBuilder = builder;
        assertSame("Wrong converted object", enabler,
                converter.convert(ElementEnabler.class, ENABLER_SPEC));
        EasyMock.verify(enabler);
        builder.verify();
    }

    /**
     * Tests convert() if an invalid specification is used, and the builder
     * throws an exception.
     */
    @Test
    public void testConvertException()
    {
        EnablerBuilderTestImpl builder = new EnablerBuilderTestImpl()
        {
            @Override
            public ElementEnabler build()
            {
                throw new IllegalArgumentException();
            };
        };
        converter.mockBuilder = builder;
        try
        {
            converter.convert(ElementEnabler.class, ENABLER_SPEC);
            fail("Exception of builder not detected!");
        }
        catch (ConversionException cex)
        {
            builder.verify();
        }
    }

    /**
     * A specialized test implementation of EnablerConverter that allows
     * injecting a mock enabler builder.
     */
    private static class EnablerConverterTestImpl extends EnablerConverter
    {
        /** The mock enabler builder. */
        EnablerBuilder mockBuilder;

        /**
         * Either returns the mock builder or calls the super method.
         */
        @Override
        EnablerBuilder createEnablerBuilder()
        {
            return (mockBuilder != null) ? mockBuilder : super
                    .createEnablerBuilder();
        }
    }

    /**
     * A specialized mock implementation of EnablerBuilder for checking whether
     * the expected specification was added.
     */
    private static class EnablerBuilderTestImpl extends EnablerBuilder
    {
        /** Stores the number of addSpecification() calls. */
        private int specsAdded;

        /**
         * Records this invocation and checks the passed in specification.
         */
        @Override
        public EnablerBuilder addSpecification(String spec)
        {
            assertEquals("Wrong specification", ENABLER_SPEC, spec);
            specsAdded++;
            return this;
        }

        /**
         * Tests whether this builder was used in the expected way.
         */
        public void verify()
        {
            assertEquals("Wrong number of specs", 1, specsAdded);
        }
    }
}
