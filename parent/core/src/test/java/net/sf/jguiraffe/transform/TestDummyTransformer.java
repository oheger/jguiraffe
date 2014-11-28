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
package net.sf.jguiraffe.transform;

import junit.framework.TestCase;

/**
 * Test class for DummyTransformer.
 *
 * @author Oliver Heger
 * @version $Id: TestDummyTransformer.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestDummyTransformer extends TestCase
{
    private DummyTransformer transformer;

    protected void setUp() throws Exception
    {
        super.setUp();
        transformer = DummyTransformer.getInstance();
    }

    /**
     * Tests the singleton instance.
     */
    public void testInstance()
    {
        assertNotNull(DummyTransformer.getInstance());
        assertSame(transformer, DummyTransformer.getInstance());
    }

    /**
     * Tests validation.
     */
    public void testValidate()
    {
        assertTrue(transformer.isValid(null, null).isValid());
        assertTrue(transformer.isValid(this, null).isValid());
    }

    /**
     * Tests transformation.
     */
    public void testTransform()
    {
        assertTrue(null == transformer.transform(null, null));
        assertSame(this, transformer.transform(this, null));
        assertSame(transformer, transformer.transform(transformer, null));
    }
}
