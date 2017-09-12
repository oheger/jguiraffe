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
package net.sf.jguiraffe.transform;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Locale;

import org.apache.commons.configuration.Configuration;
import org.easymock.EasyMock;

import junit.framework.TestCase;

/**
 * Test class for DateTimeTransformer.
 *
 * @author Oliver Heger
 * @version $Id: TestDateTimeTransformer.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestDateTimeTransformer extends TestCase
{
    /** Constant for the locale used by the tests. */
    private static final Locale LOCALE = Locale.GERMAN;

    /** The transformer to be tested. */
    private DateTimeTransformer transformer;

    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        transformer = new DateTimeTransformer();
    }

    /**
     * Tests some default values after an instance has been created.
     */
    public void testInit()
    {
        assertEquals("Wrong default style for date", DateFormat.SHORT,
                transformer.getStyle());
        assertEquals("Wrong default style for time", DateFormat.SHORT,
                transformer.getTimeStyle());
    }

    /**
     * Tests whether a correct format is created.
     */
    public void testCreateFormat()
    {
        Configuration config = EasyMock.createMock(Configuration.class);
        EasyMock.expect(
                config.getInt(DateTimeTransformer.PROP_TIME_STYLE,
                        DateFormat.MEDIUM)).andReturn(DateFormat.SHORT);
        EasyMock.replay(config);
        transformer.setTimeStyle(DateFormat.MEDIUM);
        DateFormat fmt = transformer.createFormat(LOCALE, DateFormat.SHORT,
                config);
        Calendar cal = Calendar.getInstance();
        cal.clear();
        cal.set(2008, Calendar.JANUARY, 30, 17, 23, 28);
        assertEquals("Wrong formatted date", "30.01.08 17:23", fmt.format(cal
                .getTime()));
        EasyMock.verify(config);
    }
}
