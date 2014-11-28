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

import static org.junit.Assert.assertEquals;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Locale;

import org.apache.commons.configuration.Configuration;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for TimeTransformer.
 *
 * @author Oliver Heger
 * @version $Id: TestTimeTransformer.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestTimeTransformer
{
    /** Constant for the locale used by the tests. */
    private static final Locale LOCALE = Locale.GERMAN;

    /** The transformer to be tested. */
    private TimeTransformer transformer;

    @Before
    public void setUp() throws Exception
    {
        transformer = new TimeTransformer();
    }

    /**
     * Tests whether a correct format is created.
     */
    @Test
    public void testCreateFormat()
    {
        Configuration config = EasyMock.createMock(Configuration.class);
        EasyMock.replay(config);
        DateFormat fmt = transformer.createFormat(LOCALE, DateFormat.MEDIUM,
                config);
        Calendar cal = Calendar.getInstance();
        cal.clear();
        cal.set(2008, Calendar.JANUARY, 31, 8, 26, 28);
        assertEquals("Wrong formatted time", "08:26:28", fmt.format(cal
                .getTime()));
        EasyMock.verify(config);
    }

    /**
     * Tests creating a result object for an invalid time.
     */
    @Test
    public void testErrorResult()
    {
        TransformerContext ctx = EasyMock.createMock(TransformerContext.class);
        ValidationMessageHandler msgHandler = EasyMock
                .createMock(ValidationMessageHandler.class);
        ValidationMessage msg = EasyMock.createMock(ValidationMessage.class);
        EasyMock.expect(msg.getLevel()).andReturn(ValidationMessageLevel.ERROR)
                .anyTimes();
        EasyMock.expect(ctx.getValidationMessageHandler())
                .andReturn(msgHandler);
        EasyMock.expect(
                msgHandler.getValidationMessage(ctx,
                        ValidationMessageConstants.ERR_TIME_AFTER, "test"))
                .andReturn(msg);
        EasyMock.replay(ctx, msgHandler, msg);
        ValidationResult vr = transformer.errorResult(
                ValidationMessageConstants.ERR_DATE_AFTER, ctx, "test");
        assertEquals("Wrong number of messages", 1, vr.getValidationMessages()
                .size());
        assertEquals("Wrong error message", msg, vr.getValidationMessages()
                .iterator().next());
        EasyMock.verify(ctx, msgHandler, msg);
    }
}
