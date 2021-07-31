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
package net.sf.jguiraffe.transform;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Locale;

import org.apache.commons.configuration.Configuration;
import org.easymock.EasyMock;

import junit.framework.TestCase;

/**
 * Test class for DateTransformer.
 *
 * @author Oliver Heger
 * @version $Id: TestDateTransformer.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestDateTransformer extends TestCase
{
    /** Constant for the locale used by the tests. */
    private static final Locale LOCALE = Locale.GERMAN;

    /** The transformer to be tested. */
    private DateTransformer transformer;

    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        transformer = new DateTransformer();
    }

    /**
     * Tests whether a correct format is created.
     */
    public void testCreateFormat()
    {
        Configuration config = EasyMock.createMock(Configuration.class);
        EasyMock.replay(config);
        DateFormat fmt = transformer.createFormat(LOCALE, DateFormat.MEDIUM, config);
        Calendar cal = Calendar.getInstance();
        cal.clear();
        cal.set(2008, Calendar.JANUARY, 30);
        assertEquals("Wrong formatted date", "30.01.2008", fmt.format(cal
                .getTime()));
        EasyMock.verify(config);
    }
}
