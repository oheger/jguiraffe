/*
 * Copyright 2006-2016 The JGUIraffe Team.
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
import static org.junit.Assert.assertFalse;

import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.easymock.EasyMock;

/**
 * A base class for testing transformers. This class provides some common
 * functionality for tests of Transformer implementations. It especially
 * maintains some mock objects for the transformer context and the validation
 * message handler. It is possible to expect specific error messages.
 *
 * @author Oliver Heger
 * @version $Id: AbstractTransformerTest.java 205 2012-01-29 18:29:57Z oheger $
 */
public abstract class AbstractTransformerTest
{
    /** Constant for the locale used by the tests. */
    protected static Locale LOCALE = Locale.GERMAN;

    /** A mock object for the transformer context. */
    protected TransformerContext context;

    /** A mock object for the validation message handler. */
    private ValidationMessageHandler messageHandler;

    /** Stores a mock object for a validation message. */
    private ValidationMessage message;

    /**
     * Creates mock objects for the transformer context and the message
     * validation handler.
     *
     * @param props the properties to be returned by the context mock (can be
     *        <b>null</b>, then an empty map will be used)
     */
    protected void setUpContextMock(Map<String, Object> props)
    {
        context = EasyMock.createMock(TransformerContext.class);
        messageHandler = EasyMock.createMock(ValidationMessageHandler.class);
        EasyMock.expect(context.getValidationMessageHandler()).andStubReturn(
                messageHandler);
        EasyMock.expect(context.properties()).andStubReturn(
                (props != null) ? props : new HashMap<String, Object>());
        EasyMock.expect(context.getLocale()).andStubReturn(LOCALE);
    }

    /**
     * Replays the mock objects.
     */
    protected void replayMocks()
    {
        EasyMock.replay(context, messageHandler);
    }

    /**
     * Verifies the mock objects.
     */
    protected void verifyMocks()
    {
        EasyMock.verify(context, messageHandler);
        if (message != null)
        {
            EasyMock.verify(message);
        }
    }

    /**
     * Initializes the mock objects to expect an error code.
     *
     * @param errorKey the key of the error message
     * @param params additional parameters for the error message
     */
    protected void expectError(String errorKey, Object... params)
    {
        message = EasyMock.createMock(ValidationMessage.class);
        EasyMock.expect(message.getKey()).andStubReturn(errorKey);
        EasyMock.expect(message.getLevel()).andReturn(ValidationMessageLevel.ERROR).anyTimes();
        EasyMock.replay(message);
        EasyMock.expect(
                messageHandler.getValidationMessage(context, errorKey, params))
                .andReturn(message);
    }

    /**
     * Tests whether the specified validation result object contains the
     * expected error message.
     *
     * @param errorKey the error key
     * @param vr the result object to check
     */
    protected void checkError(String errorKey, ValidationResult vr)
    {
        assertFalse("Date is valid", vr.isValid());
        Collection<ValidationMessage> msgs = vr.getValidationMessages();
        assertEquals("Wrong number of error messages", 1, msgs.size());
        ValidationMessage msg = msgs.iterator().next();
        assertEquals("Wrong message key", errorKey, msg.getKey());
    }
}
