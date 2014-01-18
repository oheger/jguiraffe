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
package net.sf.jguiraffe.gui.builder.components.tags;

import net.sf.jguiraffe.di.BeanContext;
import net.sf.jguiraffe.di.ReflectionTestClass;
import net.sf.jguiraffe.di.impl.DefaultBeanContext;
import net.sf.jguiraffe.gui.builder.di.DIBuilderData;
import net.sf.jguiraffe.gui.builder.di.tags.DITagLibrary;

import org.apache.commons.beanutils.Converter;

/**
 * A test class for {@code ConverterTag} which executes a test script. This is
 * more like an integration test as it already checks the newly registered
 * converter in action.
 *
 * @author Oliver Heger
 * @version $Id: TestConverterTagScript.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestConverterTagScript extends AbstractTagTest
{
    /** Constant for the name of the script. */
    private static final String SCRIPT = "customconverter";

    @Override
    protected void setUpJelly()
    {
        super.setUpJelly();
        context.registerTagLibrary("diBuilder", new DITagLibrary());
        DefaultBeanContext bctx =
                new DefaultBeanContext(DIBuilderData.get(context)
                        .getRootBeanStore());
        builderData.setBeanContext(bctx);
    }

    /**
     * Tests whether a custom converter can be registered.
     */
    public void testRegisterConverter() throws Exception
    {
        executeScript(SCRIPT);
        BeanContext bctx = builderData.getBeanContext();
        ReflectionTestClass bean = (ReflectionTestClass) bctx.getBean("bean");
        CustomBean customBean = (CustomBean) bean.getData();
        assertEquals("Wrong text property", "Custom converter test",
                customBean.getText());
    }

    /**
     * A simple test bean class. Our custom converter creates an instance of
     * this class.
     */
    public static class CustomBean
    {
        /** A text property. */
        private String text;

        public String getText()
        {
            return text;
        }

        public void setText(String text)
        {
            this.text = text;
        }
    }

    /**
     * A simple test converter implementation. This implementation creates an
     * instance of {@code CustomBean} and sets the property to the text value of
     * the passed in object.
     */
    public static class CustomConverter implements Converter
    {
        public Object convert(@SuppressWarnings("rawtypes") Class type,
                Object value)
        {
            CustomBean bean = new CustomBean();
            bean.setText(String.valueOf(value));
            return bean;
        }
    }
}
