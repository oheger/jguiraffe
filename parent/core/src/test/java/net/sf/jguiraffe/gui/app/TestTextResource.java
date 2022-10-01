/*
 * Copyright 2006-2022 The JGUIraffe Team.
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
package net.sf.jguiraffe.gui.app;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;

import org.easymock.EasyMock;
import org.junit.Test;

import net.sf.jguiraffe.JGuiraffeTestHelper;

/**
 * Test class for {@code TextResource}.
 */
public class TestTextResource
{
    /**
     * Tests that the given resource is undefined.
     *
     * @param resource the resource to be checked
     */
    private static void checkUndefinedResource(TextResource resource)
    {
        assertNull("Got plain text", resource.getPlainText());
        assertNull("Got a resource ID", resource.getResourceID());

        ApplicationContext appCtx =
                EasyMock.createMock(ApplicationContext.class);
        EasyMock.replay(appCtx);
        assertNull("Got resolved text", resource.resolveText(appCtx));
    }

    /**
     * Tests the constant for the undefined resource.
     */
    @Test
    public void testUndefinedResource()
    {
        checkUndefinedResource(TextResource.UNDEFINED);
    }

    /**
     * Tests a resource with a plain text.
     */
    @Test
    public void testTextResource()
    {
        String text = "my plain text";
        TextResource textResource = TextResource.fromText(text);

        assertEquals("Wrong plain text", text, textResource.getPlainText());
        assertNull("Got a resource ID", textResource.getResourceID());
        assertEquals("Wrong resolved text", text,
                textResource.resolveText(null));
    }

    /**
     * Tests that an undefined resource is returned when a null text is passed
     * in.
     */
    @Test
    public void testFromNullText()
    {
        TextResource resource = TextResource.fromText(null);

        checkUndefinedResource(resource);
        assertSame("Wrong undefined resource", resource,
                TextResource.UNDEFINED);
    }

    /**
     * Tests whether a resource can be created from a resource ID.
     */
    @Test
    public void testResourceIDResource()
    {
        final Object resID = "myResourceID";
        final String resTxt = "Text for resource ID";
        TextResource textResource = TextResource.fromResourceID(resID);

        assertNull("Got plain text", textResource.getPlainText());
        assertEquals("Wrong resource ID", resID, textResource.getResourceID());
        ApplicationContext appCtx =
                EasyMock.createMock(ApplicationContext.class);
        EasyMock.expect(appCtx.getResourceText(resID)).andReturn(resTxt);
        EasyMock.replay(appCtx);
        assertEquals("Wrong resolved text", resTxt,
                textResource.resolveText(appCtx));
    }

    /**
     * Tests the the undefined resource is returned when a null resource ID is
     * passed in.
     */
    @Test
    public void testFromNullResourceID()
    {
        TextResource resource = TextResource.fromResourceID(null);

        checkUndefinedResource(resource);
        assertSame("Wrong undefined resource", resource,
                TextResource.UNDEFINED);
    }

    /**
     * Tests equals() if the expected result is true.
     */
    @Test
    public void testEqualsTrue()
    {
        final String text = "A plain text";
        final Object resID = "Some resource ID";
        JGuiraffeTestHelper.checkEquals(TextResource.UNDEFINED,
                TextResource.UNDEFINED, true);

        TextResource resTxt1 = TextResource.fromText(text);
        TextResource resTxt2 = TextResource.fromText(text);
        JGuiraffeTestHelper.checkEquals(resTxt1, resTxt2, true);

        TextResource res1 = TextResource.fromResourceID(resID);
        TextResource res2 = TextResource.fromResourceID(resID);
        JGuiraffeTestHelper.checkEquals(res1, res2, true);
    }

    /**
     * Tests equals() if the expected result is false.
     */
    @Test
    public void testEqualsFalse()
    {
        TextResource resTxt = TextResource.fromText("some text");
        TextResource resId = TextResource.fromResourceID(new Object());

        JGuiraffeTestHelper.checkEquals(resTxt, TextResource.UNDEFINED, false);
        JGuiraffeTestHelper.checkEquals(resId, TextResource.UNDEFINED, false);
        JGuiraffeTestHelper.checkEquals(resTxt, resId, false);
        JGuiraffeTestHelper.checkEquals(resTxt,
                TextResource.fromText("another text"), false);
        JGuiraffeTestHelper.checkEquals(resId,
                TextResource.fromResourceID(new Object()), false);
    }

    /**
     * Tests corner cases of the equals() implementation.
     */
    @Test
    public void testEqualsOtherObjects()
    {
        TextResource resource = TextResource.fromText("some resource");

        JGuiraffeTestHelper.checkEquals(resource, null, false);
        JGuiraffeTestHelper.checkEquals(resource, this, false);
    }

    /**
     * Tests the string representation of a plain text resource.
     */
    @Test
    public void testToStringPlainText()
    {
        final String text = "The Text.";
        TextResource resource = TextResource.fromText(text);

        String s = resource.toString();
        assertThat("Text not found", s, containsString("text='" + text + "'"));
        assertThat("Got resource ID", s, not(containsString("resourceID")));
    }

    /**
     * Tests the string representation if a resource ID is defined.
     */
    @Test
    public void testToStringResID()
    {
        final Object resID = 321;
        TextResource resource = TextResource.fromResourceID(resID);

        String s = resource.toString();
        assertThat("ResID not found", s,
                containsString("resourceID='" + resID + "'"));
        assertThat("Got plain text", s, not(containsString("text=")));
    }
}
