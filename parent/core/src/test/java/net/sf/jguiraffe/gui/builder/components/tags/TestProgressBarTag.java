/*
 * Copyright 2006-2015 The JGUIraffe Team.
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

/**
 * Test class for ProgressBarTag.
 *
 * @author Oliver Heger
 * @version $Id: TestProgressBarTag.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestProgressBarTag extends AbstractTagTest
{
	/** Constant for the name of the test script. */
	private static final String SCRIPT = "progressbar";

	/** Constant for the name of the created element. */
	private static final String ELEM_NAME = "p1";

	/** Constant for the prefix of the expected result. */
	private static final String RESULT_PREFIX = "Container: ROOT { "
			+ "PROGRESSBAR [ NAME = " + ELEM_NAME;

	/** Constant for the suffix of the expected result. */
	private static final String RESULT_SUFFIX = " ] }";

	/** Constant for the expected result. */
	private static final String RESULT_TEXT = RESULT_PREFIX
			+ " MIN = 1 MAX = 200 " + "VALUE = 17 ALLOWTEXT = true TEXT = progress"
			+ RESULT_SUFFIX;

	/** Constant for the expected default result. */
	private static final String RESULT_DEFAULT = RESULT_PREFIX
			+ " MIN = 0 MAX = 100 ALLOWTEXT = false" + RESULT_SUFFIX;

	/** Constant for the test default builder. */
	private static final String BUILDER_DEFAULT = "TEST_DEFAULT";

	/** Constant for the test text builder. */
	private static final String BUILDER_TEXT = "TEST_TEXT";

	/** Constant for the test text res builder. */
	private static final String BUILDER_TEXTRES = "TEST_TEXTRES";

	/** Constant for the test form builder. */
	private static final String BUILDER_TESTFORM = "TEST_FORM";

	/**
     * Helper method for performing a test. This method executes the test script
     * with the specified builder and checks the expected result. It will also
     * check if a component handler was created and, optionally, whether the
     * static text element was not added to the form.
     *
     * @param builderName the name of the builder
     * @param expected the expected result
     * @param checkForm a flag whether the form is to be checked
     * @throws Exception if an error occurs
     */
	private void check(String builderName, String expected, boolean checkForm)
			throws Exception
	{
		builderData.setBuilderName(builderName);
		checkScript(SCRIPT, expected);
		assertNotNull("No component handler was created", builderData
				.getComponentHandler(ELEM_NAME));
		if (checkForm)
		{
			assertNull("Element was added to form", builderData.getForm()
					.getField(ELEM_NAME));
		}
	}

	/**
     * Tests creating a progress bar with all default values.
     */
	public void testCreateDefault() throws Exception
	{
		check(BUILDER_DEFAULT, RESULT_DEFAULT, true);
	}

	/**
     * Tests creating a fully defined progress bar with a progress text.
     */
	public void testCreateWithText() throws Exception
	{
		check(BUILDER_TEXT, RESULT_TEXT, true);
	}

	/**
     * Tests creating a fully defined progress bar with a progress text defined
     * as a resource ID.
     */
	public void testCreateWithTextRes() throws Exception
	{
		check(BUILDER_TEXTRES, RESULT_TEXT, true);
	}

	/**
     * Tests creating a progress bar that will be added to a form.
     */
	public void testCreateWithForm() throws Exception
	{
		check(BUILDER_TESTFORM, RESULT_DEFAULT, false);
		assertNotNull("Element was not added to form", builderData.getForm()
				.getField(ELEM_NAME));
	}
}
