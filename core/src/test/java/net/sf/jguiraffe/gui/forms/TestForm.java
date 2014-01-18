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
package net.sf.jguiraffe.gui.forms;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.sf.jguiraffe.JGuiraffeTestHelper;
import net.sf.jguiraffe.gui.forms.bind.BeanBindingStrategy;
import net.sf.jguiraffe.transform.DefaultValidationResult;
import net.sf.jguiraffe.transform.TransformerContext;
import net.sf.jguiraffe.transform.ValidationMessage;
import net.sf.jguiraffe.transform.ValidationMessageLevel;
import net.sf.jguiraffe.transform.ValidationResult;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for {@link Form}.
 *
 * @author Oliver Heger
 * @version $Id: TestForm.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestForm
{
    /** Constant for the name field. */
    private static final String FLD_NAME = "name";

    /** Constant for the first name field. */
    private static final String FLD_FIRST = "firstName";

    /** Constant for the salary field. */
    private static final String FLD_SALARY = "salary";

    /** Constant for the test first name value. */
    private static final String FIRST_NAME = "John";

    /** Constant for the special Dilbert first name. */
    private static final String DILBERT = "Dilbert";

    /** Constant for the test last name value. */
    private static final String LAST_NAME = "Smith";

    /** Constant for the date as string. */
    private static final String DATE_STR = "03/20/1960";

    /** Constant for the test birth date. */
    private static final Date BIRTH_DATE = JGuiraffeTestHelper.createDate(1960,
            2, 20);

    /** Constant for the test salary value. */
    private static final double SALARY = 1234.5;

    /** The test transformer context. */
    private TransformerContextImpl tcontext;

    /** The binding strategy used by the tests. */
    private BindingStrategy bindingStrategy;

    /** The form instance to be tested. */
    private FormTestImpl form;

    /** A component handler for a test field. */
    private ComponentHandlerImpl chName;

    private ComponentHandlerImpl chFirstName;

    private ComponentHandlerImpl chBirthDate;

    private ComponentHandlerImpl chSalary;

    /**
     * Creates a mock for a validation message that returns a given error key.
     * The message level is set to error.
     *
     * @param err the error key
     * @return the mock object
     */
    private ValidationMessage createValidationMessage(String err)
    {
        return createValidationMessage(err, ValidationMessageLevel.ERROR);
    }

    /**
     * Creates a mock for a validation message with the given error key and
     * level.
     *
     * @param err the error key
     * @param level the message level
     * @return the mock validation message
     */
    private ValidationMessage createValidationMessage(String err,
            ValidationMessageLevel level)
    {
        ValidationMessage vm = EasyMock.createMock(ValidationMessage.class);
        EasyMock.expect(vm.getKey()).andStubReturn(err);
        EasyMock.expect(vm.getLevel()).andReturn(level).anyTimes();
        EasyMock.replay(vm);
        return vm;
    }

    @Before
    public void setUp() throws Exception
    {
        tcontext = new TransformerContextImpl();
        bindingStrategy = new BeanBindingStrategy();
        form = new FormTestImpl(tcontext, bindingStrategy);

        DefaultFieldHandler fh = new DefaultFieldHandler();
        chName = new ComponentHandlerImpl();
        chName.setType(String.class);
        fh.setComponentHandler(chName);
        form.addField(FLD_NAME, fh);

        fh = new DefaultFieldHandler();
        chFirstName = new ComponentHandlerImpl();
        chFirstName.setType(String.class);
        fh.setComponentHandler(chFirstName);
        fh.setDisplayName(FLD_FIRST.toUpperCase());
        form.addField(FLD_FIRST, fh);

        fh = new DefaultFieldHandler();
        chBirthDate = new ComponentHandlerImpl();
        chBirthDate.setType(String.class);
        fh.setComponentHandler(chBirthDate);
        fh.setReadTransformer(new TransformerWrapper()
        {
            public Object transform(Object o)
            {
                try
                {
                    return new SimpleDateFormat("MM/dd/yyyy").parse((String) o);
                }
                catch (ParseException pex)
                {
                    return null;
                }
            }
        });
        fh.setSyntaxValidator(new ValidatorWrapper()
        {
            public ValidationResult isValid(Object o)
            {
                try
                {
                    new SimpleDateFormat("MM/dd/yyyy").parse((String) o);
                    return DefaultValidationResult.VALID;
                }
                catch (Exception ex)
                {
                    DefaultValidationResult result = new DefaultValidationResult.Builder()
                            .addValidationMessage(
                                    createValidationMessage("ERR_DATE"))
                            .build();
                    return result;
                }
            }
        });
        fh.setWriteTransformer(new TransformerWrapper()
        {
            public Object transform(Object o)
            {
                return new SimpleDateFormat("MM/dd/yyyy").format((Date) o);
            }
        });
        fh.setType(Date.class);
        form.addField("birthDate", fh);

        fh = new DefaultFieldHandler();
        chSalary = new ComponentHandlerImpl();
        chSalary.setType(Double.TYPE);
        fh.setComponentHandler(chSalary);
        fh.setPropertyName("averageSalary");
        fh.setLogicValidator(new ValidatorWrapper()
        {
            public ValidationResult isValid(Object o)
            {
                Number n = (Number) o;
                if (n.doubleValue() < 1000)
                {
                    DefaultValidationResult result = new DefaultValidationResult.Builder()
                            .addValidationMessage(
                                    createValidationMessage("ERR_TOO_FEW"))
                            .build();
                    return result;
                }
                else
                {
                    return DefaultValidationResult.VALID;
                }
            }
        });
        form.addField(FLD_SALARY, fh);
        setUpFormValidator();
    }

    /**
     * Initializes the form with a form validator.
     */
    private void setUpFormValidator()
    {
        form.setFormValidator(new FormValidator()
        {
            public FormValidatorResults isValid(Form form)
            {
                Map<String, ValidationResult> vmap = DefaultFormValidatorResults
                        .validResultMapForForm(form);
                PersonBean bean = new PersonBean();
                form.readFields(bean);
                // Implement the following validation rule:
                // People with first name "Dilbert" cannot earn more than 1500
                if (DILBERT.equals(bean.getFirstName()))
                {
                    double sal = bean.getAverageSalary();
                    if (sal > 1500)
                    {
                        DefaultValidationResult vr = new DefaultValidationResult.Builder()
                                .addValidationMessage(
                                        createValidationMessage("ERR_DILBERT"))
                                .build();
                        vmap.put(FLD_SALARY, vr);
                    }
                }
                return new DefaultFormValidatorResults(vmap);
            }
        });
    }

    /**
     * Tests a newly created instance.
     */
    @Test
    public void testNewInstance()
    {
        form = new FormTestImpl(tcontext, bindingStrategy);
        assertEquals("Wrong TransformerCtx", tcontext, form
                .getTransformerContext());
        assertEquals("Wrong binding strategy", bindingStrategy, form
                .getBindingStrategy());
        assertNull("Form validator is set", form.getFormValidator());
        assertTrue("Field names not empty", form.getFieldNames().isEmpty());
        assertNull("Found a field", form.getField("name"));
        assertNotNull("No component store set", form.getComponentStore());
    }

    /**
     * Tests creating an instance without a transformer context. This should
     * cause an exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testInitNullTransCtx()
    {
        new Form(null, bindingStrategy);
    }

    /**
     * Tests creating an instance without a binding strategy. This should cause
     * an exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testInitNullBindingStrategy()
    {
        new Form(tcontext, null);
    }

    /**
     * Tests accessing the underlying component store.
     */
    @Test
    public void testGetComponentStore()
    {
        ComponentStore store = form.getComponentStore();
        assertFalse("Store is empty", store.getFieldHandlerNames().isEmpty());
        assertEquals("Wrong number of fields", store.getFieldHandlerNames()
                .size(), form.getFieldNames().size());
        for (String fld : store.getFieldHandlerNames())
        {
            assertTrue("Field not in field names collection: " + fld, form
                    .getFieldNames().contains(fld));
            assertEquals("Wrong field", store.findFieldHandler(fld), form
                    .getField(fld));
        }
    }

    /**
     * Tests initializing of the form's fields from a form bean.
     */
    @Test
    public void testInitFields()
    {
        form.initFields(createTestPerson());
        assertEquals("Wrong last name", LAST_NAME, chName.getData());
        assertEquals("Wrong first name", FIRST_NAME, chFirstName.getData());
        assertEquals("03/17/1960", chBirthDate.getData());
        assertEquals("Wrong salary", new Double(SALARY), chSalary.getData());
        assertEquals("Wrong number of read property calls", 4,
                form.readModelPropertyCount);
    }

    /**
     * Tests initializing a sub set of the form's fields.
     */
    @Test
    public void testInitFieldsSubSet()
    {
        Set<String> names = new HashSet<String>();
        names.add(FLD_FIRST);
        names.add(FLD_NAME);
        form.initFields(createTestPerson(), names);
        assertEquals("Wrong last name", LAST_NAME, chName.getData());
        assertEquals("Wrong first name", FIRST_NAME, chFirstName.getData());
        assertNull("Birth date was set", chBirthDate.getData());
        assertNull("Salary was set", chSalary.getData());
        assertEquals("Wrong number of read property calls", names.size(),
                form.readModelPropertyCount);
    }

    /**
     * Tests initializing a sub set of the fields when an invalid field name is
     * contained. This should cause an exception.
     */
    @Test(expected = FormRuntimeException.class)
    public void testInitFieldsSubSetInvalid()
    {
        Set<String> names = new HashSet<String>();
        names.add(FLD_FIRST);
        names.add("an invalid field name");
        form.initFields(createTestPerson(), names);
    }

    /**
     * Tests initializing a sub set of the fields with a null set. This should
     * cause an exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testInitFieldsSubSetNull()
    {
        form.initFields(createTestPerson(), null);
    }

    /**
     * Tests initializing the form's fields if the form contains a property that
     * cannot be found in the bean. This should cause a runtime exception.
     */
    @Test(expected = FormRuntimeException.class)
    public void testInitFieldsInvalidProperty()
    {
        PersonBean bean = new PersonBean();
        DefaultFieldHandler fh = new DefaultFieldHandler();
        fh.setComponentHandler(new ComponentHandlerImpl());
        form.addField("unknownProperty", fh);
        form.initFields(bean);
    }

    /**
     * Tests initFields() when no model object is passed. This should have no
     * effect.
     */
    @Test
    public void testInitFieldsNullModel()
    {
        chName.setData(DILBERT);
        form.initFields(null);
        assertEquals("Handler was changed", DILBERT, chName.getData());
    }

    /**
     * Creates a person bean with test data.
     *
     * @return the test bean
     */
    private PersonBean createTestPerson()
    {
        PersonBean bean = new PersonBean();
        bean.setName(LAST_NAME);
        bean.setFirstName(FIRST_NAME);
        bean.setAverageSalary(SALARY);
        bean.setBirthDate(JGuiraffeTestHelper.createDate(1960, 2, 17));
        return bean;
    }

    /**
     * Tests whether the bean contains the expected values.
     *
     * @param bean the bean to test
     */
    private void checkTestPerson(PersonBean bean)
    {
        assertEquals("Wrong name", LAST_NAME, bean.getName());
        assertEquals("Wrong first name", FIRST_NAME, bean.getFirstName());
        assertEquals("Wrong date", BIRTH_DATE, bean.getBirthDate());
        assertEquals("Wrong salary", SALARY, bean.getAverageSalary(), 0.0001);
    }

    /**
     * Tests validation of the form's fields.
     */
    @Test
    public void testValidateFields() throws Exception
    {
        chName.setData("Hirsch");
        chFirstName.setData("Harry");
        chBirthDate.setData("invalid date");
        chSalary.setData(new Double(500));
        FormValidatorResults res = form.validateFields();
        assertFalse("Invalid fields not detected (1)", res.isValid());
        assertFalse("Invalid salary not detected", res.getResultsFor("salary")
                .isValid());
        assertFalse("Invalid date not detected", res.getResultsFor("birthDate")
                .isValid());
        assertTrue("Name is not valid", res.getResultsFor("name").isValid());

        chBirthDate.setData("03/17/1960");
        res = form.validateFields();
        assertFalse("Invalid fields not detected (2)", res.isValid());
        assertTrue("Date still invalid", res.getResultsFor("birthDate")
                .isValid());

        chSalary.setData(new Double(1111));
        res = form.validateFields();
        assertTrue("Form still invalid", res.isValid());
    }

    /**
     * Tests whether fields with a warning are detected during validation.
     */
    @Test
    public void testValidateFieldsWithWarning()
    {
        DefaultFieldHandler fh = (DefaultFieldHandler) form
                .getField(FLD_SALARY);
        fh.setSyntaxValidator(new ValidatorWrapper()
        {
            public ValidationResult isValid(Object o)
            {
                Number n = (Number) o;
                if (n.doubleValue() < 1200)
                {
                    return new DefaultValidationResult.Builder()
                            .addValidationMessage(
                                    createValidationMessage("ERR_FIEW",
                                            ValidationMessageLevel.WARNING))
                            .build();
                }
                else
                {
                    return DefaultValidationResult.VALID;
                }
            }
        });
        chName.setData("Hirsch");
        chFirstName.setData("Harry");
        chBirthDate.setData("invalid date");
        chSalary.setData(new Double(1100));
        FormValidatorResults res = form.validateFields();
        ValidationResult vres = res.getResultsFor("salary");
        assertTrue("Not valid", vres.isValid());
        assertTrue("No warning messages", vres
                .hasMessages(ValidationMessageLevel.WARNING));
        assertEquals("Wrong number of warning messages", 1, vres
                .getValidationMessages(ValidationMessageLevel.WARNING).size());
    }

    /**
     * Tests validating a sub set of the form's fields.
     */
    @Test
    public void testValidateFieldsSubSet()
    {
        chName.setData(LAST_NAME);
        chFirstName.setData(FIRST_NAME);
        chSalary.setData(new Double(100));
        FormValidatorResults res = form.validateFields();
        assertFalse("Invalid fields not detected", res.isValid());
        Set<String> names = new HashSet<String>();
        names.add(FLD_FIRST);
        names.add(FLD_NAME);
        res = form.validateFields(names);
        assertTrue("Validation of sub set failed", res.isValid());
    }

    /**
     * Tests validating a sub set of the form fields when the set contains an
     * invalid field name. This should cause an exception.
     */
    @Test(expected = FormRuntimeException.class)
    public void testValidateFieldsSubSetInvalid()
    {
        chName.setData(LAST_NAME);
        chFirstName.setData(FIRST_NAME);
        Set<String> names = new HashSet<String>();
        names.add(FLD_FIRST);
        names.add("an invalid field name!");
        form.validateFields(names);
    }

    /**
     * Tests validating a sub set of the form fields when the set is null. This
     * should cause an exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testValidateFieldsSubSetNull()
    {
        form.validateFields(null);
    }

    /**
     * Tests validation of the whole form.
     */
    @Test
    public void testValidateForm()
    {
        chName.setData(LAST_NAME);
        chFirstName.setData(DILBERT);
        chBirthDate.setData(DATE_STR);
        chSalary.setData(new Double(2222));
        FormValidatorResults res = form.validateFields();
        assertTrue("Field validation fails (1)", res.isValid());
        PersonBean bean = new PersonBean();
        res = form.validateForm(bean);
        assertFalse("Form validation succeeds", res.isValid());
        assertTrue("salary field not invalid", res.getErrorFieldNames()
                .contains("salary"));
        assertNull("Bean was filled", bean.getFirstName());

        chSalary.setData(new Double(1400));
        assertTrue("Field validation fails (2)", form.validateFields()
                .isValid());
        assertTrue("Form validation fails", form.validateForm(bean).isValid());
    }

    /**
     * Tests validation when a field contains invalid data. In this case the
     * model object must not be modified.
     */
    @Test
    public void testValidateFieldError()
    {
        chName.setData(LAST_NAME);
        chFirstName.setData(FIRST_NAME);
        chBirthDate.setData("not a valid date");
        chSalary.setData(SALARY);
        PersonBean bean = new PersonBean();
        assertFalse("Validation succeeds", form.validate(bean).isValid());
        assertEquals("Model was modified", new PersonBean(), bean);
    }

    /**
     * Tests validation of the form when form-level validation fails. In this
     * case the model object must not be modified.
     */
    @Test
    public void testValidateFormError()
    {
        chName.setData(LAST_NAME);
        chFirstName.setData(DILBERT);
        chBirthDate.setData(BIRTH_DATE);
        chSalary.setData(new Double(2222));
        PersonBean bean = new PersonBean();
        assertFalse("Validation succeeds", form.validate(bean).isValid());
        assertEquals("Model was modified", new PersonBean(), bean);
    }

    /**
     * Tests validation of the whole form if a warning message is produced.
     */
    @Test
    public void testValidateFormWarning()
    {
        chName.setData(LAST_NAME);
        chFirstName.setData(FIRST_NAME);
        chBirthDate.setData(DATE_STR);
        chSalary.setData(SALARY);
        ((DefaultFieldHandler) form.getField(FLD_SALARY))
                .setSyntaxValidator(new ValidatorWrapper()
                {
                    public ValidationResult isValid(Object o)
                    {
                        return new DefaultValidationResult.Builder()
                                .addValidationMessage(
                                        createValidationMessage("ERR_WARN",
                                                ValidationMessageLevel.WARNING))
                                .build();
                    }
                });
        PersonBean bean = new PersonBean();
        FormValidatorResults vres = form.validate(bean);
        assertTrue("Not valid", vres.isValid());
        ValidationResult vr = vres.getResultsFor(FLD_SALARY);
        assertTrue("No warnings", vr
                .hasMessages(ValidationMessageLevel.WARNING));
    }

    /**
     * Tests validate() when no form validator is defined.
     */
    @Test
    public void testValidateNoFormValidator()
    {
        form.setFormValidator(null);
        chName.setData(LAST_NAME);
        chFirstName.setData(FIRST_NAME);
        chBirthDate.setData(DATE_STR);
        chSalary.setData(SALARY);
        PersonBean bean = new PersonBean();
        assertTrue("Validation fails", form.validate(bean).isValid());
        checkTestPerson(bean);
    }

    /**
     * Tests validate() when no model object is passed. Validation should work,
     * but no data is copied.
     */
    @Test
    public void testValidateNoModel()
    {
        fillFields();
        assertTrue("Form not valid", form.validate(null).isValid());
    }

    /**
     * Tests the read fields method.
     */
    @Test
    public void testReadFields()
    {
        PersonBean bean = new PersonBean();
        prepareReadFieldsTest();
        form.readFields(bean);
        checkTestPerson(bean);
        assertTrue("No write property calls", form.writeModelPropertyCount > 0);
    }

    /**
     * Tests reading a sub set of the form's fields.
     */
    @Test
    public void testReadFieldsSubSet()
    {
        prepareReadFieldsTest();
        PersonBean bean = new PersonBean();
        Set<String> names = new HashSet<String>();
        names.add(FLD_FIRST);
        names.add(FLD_NAME);
        form.readFields(bean, names);
        assertEquals("Wrong first name", FIRST_NAME, bean.getFirstName());
        assertEquals("Wrong last name", LAST_NAME, bean.getName());
        assertNull("Date field was set", bean.getBirthDate());
        assertEquals("Salary field was set", Double.doubleToLongBits(0), Double
                .doubleToLongBits(bean.getAverageSalary()));
        assertTrue("No write property calls", form.writeModelPropertyCount > 0);
    }

    /**
     * Tests reading a sub set of the form's fields when the sub set contains an
     * invalid field name. This should cause an exception.
     */
    @Test(expected = FormRuntimeException.class)
    public void testReadFieldsSubSetInvalid()
    {
        prepareReadFieldsTest();
        Set<String> names = new HashSet<String>();
        names.add(FIRST_NAME);
        names.add("an invalid field name!");
        PersonBean bean = new PersonBean();
        form.readFields(bean, names);
    }

    /**
     * Tests reading fields with a null sub set. This should cause an exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testReadFieldsSubSetNull()
    {
        prepareReadFieldsTest();
        PersonBean bean = new PersonBean();
        form.readFields(bean, null);
    }

    /**
     * Tests reading the form's fields if a property cannot be found in the form
     * bean. This should cause an exception.
     */
    @Test(expected = FormRuntimeException.class)
    public void testReadFieldsUnknownProperty()
    {
        PersonBean bean = new PersonBean();
        prepareReadFieldsTest();
        DefaultFieldHandler fh = new DefaultFieldHandler();
        fh.setComponentHandler(new ComponentHandlerImpl());
        form.addField("unknownProperty", fh);
        form.readFields(bean);
    }

    /**
     * Tests the readFields() method when a null model object is provided. This
     * should have no effect. We can only test that no exception is thrown.
     */
    @Test
    public void testReadFieldsNullModel()
    {
        chName.setData(DILBERT);
        form.readFields(null);
    }

    /**
     * Tries to validate the form when a property cannot be found in the form
     * bean. This will cause an exception.
     */
    @Test(expected = FormRuntimeException.class)
    public void testValidateUnkownProperty()
    {
        DefaultFieldHandler fh = new DefaultFieldHandler();
        fh.setComponentHandler(new ComponentHandlerImpl());
        form.addField("unknownProperty", fh);
        prepareReadFieldsTest();
    }

    /**
     * Fills the input fields of the form with default values.
     */
    private void fillFields()
    {
        chName.setData(LAST_NAME);
        chFirstName.setData(FIRST_NAME);
        chSalary.setData(new Double(SALARY));
        chBirthDate.setData(DATE_STR);
    }

    /**
     * Initializes the form's fields with test values and performs validation.
     */
    private void prepareReadFieldsTest()
    {
        fillFields();
        assertTrue("Field validation failed", form.validateFields().isValid());
        assertTrue("Form validation failed", form
                .validateForm(new PersonBean()).isValid());
    }

    /**
     * Tests the validate() method.
     */
    @Test
    public void testValidate()
    {
        chName.setData("Hirsch");
        chFirstName.setData("Dilbert");
        chSalary.setData(new Double(2500));
        chBirthDate.setData("not a valid date");
        PersonBean bean = new PersonBean();

        FormValidatorResults res = form.validate(bean);
        assertFalse("Form is valid (1)", res.isValid());
        assertFalse("Invalid date not detected", res.getResultsFor("birthDate")
                .isValid());
        assertNull("Name was copied", bean.getName());
        assertNull("First name was copied", bean.getFirstName());

        chBirthDate.setData(DATE_STR);
        res = form.validate(bean);
        assertFalse("Form is valid (2)", res.isValid());
        assertFalse("Wrong salary not detected", res.getResultsFor("salary")
                .isValid());
        assertNull("Bean was filled", bean.getBirthDate());

        chSalary.setData(new Double(1111));
        res = form.validate(bean);
        assertTrue("Form not valid", res.isValid());
        assertEquals("Wrong first name", "Dilbert", bean.getFirstName());
        assertEquals("Wrong last name", "Hirsch", bean.getName());
        assertEquals("Wrong birth date", JGuiraffeTestHelper.createDate(1960,
                2, 20), bean.getBirthDate());
        assertEquals("Wrong salary", 1111.0, bean.getAverageSalary(), 0.0001);
    }

    /**
     * Tests usage of the default form validator.
     */
    @Test
    public void testDefaultFormValidator()
    {
        form.setFormValidator(null);
        chName.setData("Hirsch");
        chFirstName.setData("Harry");
        chBirthDate.setData("03/26/1960");
        chSalary.setData(new Double(1975.25));
        assertTrue("Wrong result of field validation", form.validateFields()
                .isValid());
        PersonBean bean = new PersonBean();
        FormValidatorResults vres = form.validateForm(bean);
        assertTrue("Wrong result of form validation", vres.isValid());
        Set<String> fields = vres.getFieldNames();
        assertEquals("Wrong number of fields", 4, fields.size());
        for (String fldName : fields)
        {
            assertNotNull("Field not found: " + fldName, form.getField(fldName));
        }
        assertNotNull("Bean was not filled", bean.getFirstName());
    }

    /**
     * Tests whether a default form validator instance exists that is always
     * used if no form validator was set explicitly.
     */
    @Test
    public void testFetchFormValidatorDefault()
    {
        form.setFormValidator(null);
        FormValidator val = form.fetchFormValidator();
        assertNotNull("No default form validator", val);
        assertSame("Got multiple instances", val, form.fetchFormValidator());
    }

    /**
     * Tries to add a field with an undefined name. This should cause an
     * exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAddFieldNullName()
    {
        DefaultFieldHandler fh = new DefaultFieldHandler();
        fh.setComponentHandler(new ComponentHandlerImpl());
        form.addField(null, fh);
    }

    /**
     * Tests adding a field with an undefined field handler. This should cause
     * an exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAddFieldNullHandler()
    {
        form.addField("nullField", null);
    }

    /**
     * Tests querying the display name if one is defined.
     */
    @Test
    public void testGetDisplayNameDisplay()
    {
        assertEquals("Wrong display name", FLD_FIRST.toUpperCase(), form
                .getDisplayName(FLD_FIRST));
    }

    /**
     * Tests querying the display name if none is defined. In this case the name
     * should be returned.
     */
    @Test
    public void testGetDisplayNameName()
    {
        assertEquals("Wrong display name", FLD_NAME, form
                .getDisplayName(FLD_NAME));
    }

    /**
     * Tests querying the display name for an unknown field. Result should be
     * null.
     */
    @Test
    public void testGetDisplayNameUnknown()
    {
        assertNull("Non-null display name for unknown field", form
                .getDisplayName("unknown field"));
    }

    /**
     * Tests reading a property from a model object.
     */
    @Test
    public void testReadModelProperty()
    {
        BindingStrategy strat = EasyMock.createMock(BindingStrategy.class);
        final Object model = new Object();
        final String name = "Hirsch";
        EasyMock.expect(strat.readProperty(model, LAST_NAME)).andReturn(name);
        EasyMock.replay(strat);
        form.mockBindingStrategy = strat;
        assertEquals("Wrong property value", name, form.readModelProperty(
                model, LAST_NAME));
        EasyMock.verify(strat);
    }

    /**
     * Tests writing a property to the model.
     */
    @Test
    public void testWriteModelProperty()
    {
        BindingStrategy strat = EasyMock.createMock(BindingStrategy.class);
        final Object model = new Object();
        final String name = "Hirsch";
        strat.writeProperty(model, LAST_NAME, name);
        EasyMock.replay(strat);
        form.mockBindingStrategy = strat;
        form.writeModelProperty(model, LAST_NAME, name);
        EasyMock.verify(strat);
    }

    /**
     * A test implementation of Form.
     */
    private static class FormTestImpl extends Form
    {
        /** A mock binding strategy. */
        BindingStrategy mockBindingStrategy;

        /** A counter for readModelProperty() invocations. */
        int readModelPropertyCount;

        /** A counter for writeModelProperty() invocations. */
        int writeModelPropertyCount;

        public FormTestImpl(TransformerContext ctx, BindingStrategy strat)
        {
            super(ctx, strat);
        }

        /**
         * Either returns the mock binding strategy or calls the super method.
         */
        @Override
        BindingStrategy fetchBindingStrategy()
        {
            return (mockBindingStrategy != null) ? mockBindingStrategy : super
                    .fetchBindingStrategy();
        }

        /**
         * Records this invocation.
         */
        @Override
        Object readModelProperty(Object model, String propertyName)
        {
            ++readModelPropertyCount;
            return super.readModelProperty(model, propertyName);
        }

        /**
         * Records this invocation.
         */
        @Override
        void writeModelProperty(Object model, String propertyName, Object value)
        {
            ++writeModelPropertyCount;
            super.writeModelProperty(model, propertyName, value);
        }
    }
}
