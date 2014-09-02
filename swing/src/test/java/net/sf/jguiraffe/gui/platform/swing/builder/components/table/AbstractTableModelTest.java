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
package net.sf.jguiraffe.gui.platform.swing.builder.components.table;

import javax.swing.JTable;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import net.sf.jguiraffe.PersonBean;
import net.sf.jguiraffe.di.BeanStore;
import net.sf.jguiraffe.di.impl.DefaultBeanContext;
import net.sf.jguiraffe.di.impl.DefaultBeanStore;
import net.sf.jguiraffe.di.impl.providers.ConstantBeanProvider;
import net.sf.jguiraffe.gui.app.Application;
import net.sf.jguiraffe.gui.app.ApplicationContextImpl;
import net.sf.jguiraffe.gui.builder.components.ComponentBuilderData;
import net.sf.jguiraffe.gui.builder.components.ComponentManager;
import net.sf.jguiraffe.gui.builder.components.FormBuilderException;
import net.sf.jguiraffe.gui.builder.components.tags.table.TableColumnTag;
import net.sf.jguiraffe.gui.builder.components.tags.table.TableTag;
import net.sf.jguiraffe.gui.builder.impl.JellyContextBeanStore;
import net.sf.jguiraffe.gui.forms.DefaultFieldHandler;
import net.sf.jguiraffe.gui.forms.bind.BeanBindingStrategy;
import net.sf.jguiraffe.gui.layout.Unit;
import net.sf.jguiraffe.transform.TransformerContext;
import org.apache.commons.jelly.JellyContext;
import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.Tag;
import org.easymock.EasyMock;

/**
 * An abstract test class for tests of Swing table functionality. This class
 * provides common functionality for setting up a table model, a table tag and
 * more.
 *
 * @author Oliver Heger
 * @version $Id: AbstractTableModelTest.java 205 2012-01-29 18:29:57Z oheger $
 */
public abstract class AbstractTableModelTest
{
    /** An array with the names of the columns. */
    public static final String[] COLUMN_NAMES = {
            "firstName", "lastName", "idNo", "salary"
    };

    /** An array with the data types of the columns. */
    public static final Class<?>[] COLUMN_TYPES = {
            String.class, String.class, Integer.TYPE, Double.TYPE
    };

    /** An array with the editable flags for the columns. */
    public static final boolean[] EDIT_FLAGS = {
            false, false, true, true
    };

    /** An array with the widths of the columns. */
    public static final int[] COLUMN_WIDTHS = {
            30, 30, 15, 20
    };

    /** An array with test data for the table model. */
    public static final Object[][] TEST_DATA = {
            {
                    "John", "Falstaff", 123, 775.0
            }, {
                    "Julia", "Capulet", 428, 1225.5
            }, {
                    "Romeo", "Montegue", 312, 1225.5
            }, {
                    "Timon", "of Athens", 764, 1099.25
            }, {
                    "Titus", "Andronicus", 111, 980.0
            }, {
                    "Cathrine", "Babtista", 456, 1000.0
            }
    };

    /** Constant for the name of the model variable. */
    protected static final String VAR_MODEL = "dataModel";

    /** Stores the underlying table tag. */
    protected TableTag tableTag;

    /**
     * Fully initializes a table tag for the test table based on the provided
     * parameters.
     *
     * @param data the data collection; can be <b>null</b>, then default data
     *        will be created
     * @param width if set to <b>true</b>, the widths of the columns are set
     * @param scrollWidth the scroll width as string (can be <b>null</b>)
     * @param scrollHeight the scroll height as string (can be <b>null</b>)
     * @return the initialized tag
     * @throws JellyTagException if the tag is used incorrectly
     * @throws FormBuilderException if an error occurs
     */
    public static TableTag setUpTableTag(Collection<?> data, boolean width,
            String scrollWidth, String scrollHeight) throws JellyTagException,
            FormBuilderException
    {
        ComponentManager cm = EasyMock.createNiceMock(ComponentManager.class);
        TransformerContext tctx = EasyMock.createNiceMock(TransformerContext.class);
        EasyMock.replay(cm, tctx);
        JellyContext ctx = new JellyContext();
        ComponentBuilderData builderData = new ComponentBuilderData();
        builderData.setComponentManager(cm);
        builderData.initializeForm(tctx, new BeanBindingStrategy());
        builderData.setBeanContext(new DefaultBeanContext(
                new JellyContextBeanStore(ctx, null)));
        builderData.put(ctx);
        ctx.setVariable(VAR_MODEL, (data != null) ? data : setUpTestData());
        TableTagTestImpl tt = new TableTagTestImpl();
        tt.setContext(ctx);
        tt.setModel(VAR_MODEL);
        tt.setScrollWidth(scrollWidth);
        tt.setScrollHeight(scrollHeight);
        processTableTag(tt);

        for (int i = 0; i < COLUMN_NAMES.length; i++)
        {
            // add a special column tag that can be directly executed
            TableColumnTagTestImpl colTag = new TableColumnTagTestImpl(tt);
            colTag.setContext(ctx);
            colTag.setHeader(COLUMN_NAMES[i]);
            colTag.setName(COLUMN_NAMES[i]);
            colTag.setColumnClass(COLUMN_TYPES[i]);
            colTag.setEditable(EDIT_FLAGS[i]);
            if (width)
            {
                colTag.setWidth(COLUMN_WIDTHS[i] + Unit.DLU.getUnitName());
            }
            colTag.processBeforeBody();
        }

        return tt;
    }

    /**
     * Initializes a table tag for the test table according to the specified
     * parameters. No preferred scroll sizes are set.
     *
     * @param data the data collection; can be <b>null</b>, then default data
     *        will be created
     * @param width if set to <b>true</b>, the widths of the columns are set
     * @return the initialized tag
     * @throws JellyTagException if the tag is used incorrectly
     * @throws FormBuilderException if an error occurs
     */
    public static TableTag setUpTableTag(Collection<?> data, boolean width)
            throws JellyTagException, FormBuilderException
    {
        return setUpTableTag(data, width, null, null);
    }

    /**
     * Initializes a table tag for the test table. Widths for the columns are
     * set.
     *
     * @param data the data collection; can be <b>null</b>, then default data
     *        will be created
     * @return the initialized tag
     * @throws JellyTagException if the tag is used incorrectly
     * @throws FormBuilderException if an error occurs
     */
    public static TableTag setUpTableTag(Collection<?> data)
            throws JellyTagException, FormBuilderException
    {
        return setUpTableTag(data, true);
    }

    /**
     * Invokes the {@code processBeforeBody()} method on the passed in
     * table tag. This method can be used if the tag in its processed form is
     * needed. It assumes that the passed in tag has been created by the
     * {@code setUpTableTag()} method.
     *
     * @param tt the table tag
     * @throws FormBuilderException if an exception occurs
     * @throws JellyTagException if the tag is used incorrectly
     */
    public static void processTableTag(TableTag tt) throws JellyTagException,
            FormBuilderException
    {
        ComponentBuilderData builderData =
                ComponentBuilderData.get(tt.getContext());
        builderData.setBeanContext(new DefaultBeanContext(
                new JellyContextBeanStore(tt.getContext(),
                        createParentBeanStore())));
        ((TableTagTestImpl) tt).processBeforeBody();
    }

    /**
     * Creates a bean store which contains an application bean. This is needed
     * when processing a table tag.
     *
     * @return the bean store
     */
    private static BeanStore createParentBeanStore()
    {
        DefaultBeanStore store = new DefaultBeanStore();
        Application app = new Application();
        app.setApplicationContext(new ApplicationContextImpl());
        store.addBeanProvider(Application.BEAN_APPLICATION,
                ConstantBeanProvider.getInstance(Application.class, app));
        return store;
    }

    /**
     * Returns a collection with test data.
     *
     * @return the data collection
     */
    public static Collection<PersonBean> setUpTestData()
    {
        List<PersonBean> data = new LinkedList<PersonBean>();
        for (Object[] td : TEST_DATA)
        {
            PersonBean bean = new PersonBean();
            bean.setFirstName(td[0].toString());
            bean.setLastName(td[1].toString());
            bean.setIdNo(((Integer) td[2]).intValue());
            bean.setSalary(((Double) td[3]).doubleValue());
            data.add(bean);
        }
        return data;
    }

    /**
     * Creates a table model. If a table tag has already been created, this tag
     * is used for the new model. Otherwise a new tag is created and initialized
     * now.
     *
     * @return the table model
     */
    protected SwingTableModel createTableModel()
    {
        try
        {
            if (tableTag == null)
            {
                tableTag = setUpTableTag(null);
            }
            return new SwingTableModel(tableTag, new JTable());
        }
        catch (Exception ex)
        {
            throw new RuntimeException(ex);
        }
    }

    /**
     * A special table column tag implementation that can be used and
     * manipulated without executing Jelly scripts.
     */
    protected static class TableColumnTagTestImpl extends TableColumnTag
    {
        /** Stores the parent table tag. */
        private TableTag tableTag;

        /**
         * Creates a new instance of {@code TableColumnTagTestImpl} and
         * sets the parent table tag.
         *
         * @param tt the table tag
         */
        public TableColumnTagTestImpl(TableTag tt)
        {
            tableTag = tt;
        }

        @SuppressWarnings("rawtypes")
        @Override
        protected Tag findAncestorWithClass(Class cls)
        {
            return (TableTag.class.equals(cls)) ? tableTag : null;
        }

        @Override
        public void processBeforeBody() throws JellyTagException,
                FormBuilderException
        {
            super.processBeforeBody();
            DefaultFieldHandler fh = new DefaultFieldHandler();
            fh.setComponentHandler(createComponentHandler(null, false));
            insertField(fh);
        }

        /**
         * Sets an editor for this column.
         *
         * @param component the editor component
         */
        public void installEditor(Object component)
        {
            initEditorComponent(component);
        }

        /**
         * Sets a renderer for this column.
         *
         * @param component the renderer component
         */
        public void installRenderer(Object component)
        {
            initRendererComponent(component);
        }
    }

    /**
     * A special table tag test implementation which is more test-friendly.
     */
    protected static class TableTagTestImpl extends TableTag
    {
        @Override
        public void processBeforeBody() throws JellyTagException,
                FormBuilderException
        {
            super.processBeforeBody();
        }
    }
}
