package gov.cdc.dataingestion.nbs.converter;

import gov.cdc.dataingestion.nbs.TestHelper;
import gov.cdc.dataingestion.nbs.converters.RhapsodysXmlToHl7Converter;
import gov.cdc.dataingestion.nbs.jaxb.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
/**
 1118 - require constructor complaint
 125 - comment complaint
 6126 - String block complaint
 1135 - todos complaint
 * */
@SuppressWarnings({"java:S1118","java:S125", "java:S6126", "java:S1135"})
class RhapsodysXmlToHl7ConverterTest {
    RhapsodysXmlToHl7Converter target = new RhapsodysXmlToHl7Converter();

    @Test
    void convertXmlToHL7Test() throws Exception {
        String xmlMessage = TestHelper.testFileReading();
        var result = target.convertToHl7(xmlMessage);
        Assertions.assertEquals(TestData.expected251HL7, result);
    }

    @Test
    void streamHL7SADTypeTestStreetNameAndDwelling() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        var parentClass = new RhapsodysXmlToHl7Converter();
        HL7SADType sad = new HL7SADType();
        sad.setHL7StreetOrMailingAddress("setHL7StreetOrMailingAddress");
        sad.setHL7StreetName("setHL7StreetName");
        sad.setHL7DwellingNumber("setHL7DwellingNumber");
        String expected = "setHL7StreetOrMailingAddress^setHL7StreetName^setHL7DwellingNumber";
        Method privateMethod = RhapsodysXmlToHl7Converter.class.getDeclaredMethod("streamHL7SADType", HL7SADType.class);
        privateMethod.setAccessible(true);
        var result = privateMethod.invoke(parentClass, sad);

        Assertions.assertEquals(expected, result);
    }

    @Test
    void streamHL7XADTypeTestCoverAllMissingConditional() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        var parentClass = new RhapsodysXmlToHl7Converter();
        HL7XADType model = new HL7XADType();
        model.setHL7StreetAddress(new HL7SADType());
        model.setHL7OtherDesignation("setHL7OtherDesignation");
        model.setHL7City("setHL7City");
        model.setHL7StateOrProvince("province");
        model.setHL7ZipOrPostalCode("zip");
        model.setHL7Country("county");
        model.setHL7AddressType("type");
        model.setHL7OtherGeographicDesignation("Other Geo");
        model.setHL7CountyParishCode("Parish");
        model.setHL7CensusTract("Census");
        model.setHL7AddressRepresentationCode("Represent");
        model.setHL7AddressValidityRange( new HL7DRType());
        model.setHL7EffectiveDate(new HL7TSType());
        model.setHL7ExpirationDate(new HL7TSType());

        String expected = "null^setHL7OtherDesignation^setHL7City^province^zip^county";
        Method privateMethod = RhapsodysXmlToHl7Converter.class.getDeclaredMethod("streamHL7XADType", HL7XADType.class);
        privateMethod.setAccessible(true);
        var result = privateMethod.invoke(parentClass, model);

        Assertions.assertEquals(expected, result);
    }

    @Test
    void streamHL7XTNTypeTestCoverAllMissingConditional() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        var parentClass = new RhapsodysXmlToHl7Converter();
        HL7XTNType model = new HL7XTNType();
        HL7NMType nm = new HL7NMType();
        nm.setHL7Numeric(new BigInteger("123456789"));
        model.setHL7TelecommunicationUseCode("Tele Code");
        model.setHL7TelecommunicationEquipmentType("Tele Equip");
        model.setHL7AreaCityCode(nm);
        model.setHL7LocalNumber(nm);
        model.setHL7EmailAddress("Ext");
        model.setHL7AnyText("Any Text");
        model.setHL7ExtensionPrefix("Prefix");
        model.setHL7Extension(nm);
        model.setHL7SpeedDialCode("Dial Code");
        model.setHL7UnformattedTelephonenumber("Num Unformatted");

        String expected = "^Tele Code^Tele Equip^Ext^^123456789^123456789^123456789^Any Text^Prefix^Dial Code^Num Unformatted";
        Method privateMethod = RhapsodysXmlToHl7Converter.class.getDeclaredMethod("streamHL7XTNType", HL7XTNType.class);
        privateMethod.setAccessible(true);
        var result = privateMethod.invoke(parentClass, model);

        Assertions.assertEquals(expected, result);
    }

    @Test
    void streamHL7NTETypeListTestCoverAllMissingConditional() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        var parentClass = new RhapsodysXmlToHl7Converter();
        List<HL7NTEType> models = new ArrayList<>();
        HL7NTEType model = new HL7NTEType();
        model.setHL7SourceOfComment("Comment 1");
        models.add(model);
        model = new HL7NTEType();
        model.setHL7SourceOfComment("Comment 2");
        models.add(model);
        String expected = "NTE||Comment 1|||~NTE||Comment 2|||";
        Method privateMethod = RhapsodysXmlToHl7Converter.class.getDeclaredMethod("streamHL7NTETypeList", List.class);
        privateMethod.setAccessible(true);
        var result = privateMethod.invoke(parentClass, models);
        Assertions.assertEquals(expected, result);
    }

    @Test
    void streamHL7XTNTypeListTestCoverAllMissingConditional() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        var parentClass = new RhapsodysXmlToHl7Converter();
        List<HL7XTNType> models = new ArrayList<>();
        HL7XTNType model = new HL7XTNType();
        model.setHL7SpeedDialCode("Comment 1");
        models.add(model);
        model = new HL7XTNType();
        model.setHL7SpeedDialCode("Comment 2");
        models.add(model);
        String expected = "^^^^^^^^^^Comment 1^~^^^^^^^^^^Comment 2^";
        Method privateMethod = RhapsodysXmlToHl7Converter.class.getDeclaredMethod("streamHL7XTNTypeList", List.class);
        privateMethod.setAccessible(true);
        var result = privateMethod.invoke(parentClass, models);
        Assertions.assertEquals(expected, result);
    }

    @Test
    void streamHL7XADTypeListListTestCoverAllMissingConditional() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        var parentClass = new RhapsodysXmlToHl7Converter();
        List<HL7XADType> models = new ArrayList<>();
        HL7XADType model = new HL7XADType();
        model.setHL7CountyParishCode("Comment 1");
        models.add(model);
        model = new HL7XADType();
        model.setHL7CountyParishCode("Comment 2");
        models.add(model);
        String expected = "^^^^^~^^^^^";
        Method privateMethod = RhapsodysXmlToHl7Converter.class.getDeclaredMethod("streamHL7XADTypeList", List.class);
        privateMethod.setAccessible(true);
        var result = privateMethod.invoke(parentClass, models);
        Assertions.assertEquals(expected, result);
    }

    @Test
    void streamHL7XCNTypeListTestCoverAllMissingConditional() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        var parentClass = new RhapsodysXmlToHl7Converter();
        List<HL7XCNType> models = new ArrayList<>();
        HL7XCNType model = new HL7XCNType();
        HL7CWEType cwe = new HL7CWEType();
        cwe.setHL7AlternateText("Alter Text");
        cwe.setHL7NameofAlternateCodingSystem("Coding System");
        cwe.setHL7AlternateCodingSystemVersionID("Version Id");
        cwe.setHL7AlternateCodingSystemVersionID("Alter Version Id");
        cwe.setHL7OriginalText("Ori Text");
        model.setHL7CheckDigitScheme("Comment 1");
        model.setHL7ProfessionalSuffix("Profess Suffix");
        model.setHL7AssigningAgencyOrDepartment(cwe);
        models.add(model);
        model = new HL7XCNType();
        model.setHL7CheckDigitScheme("Comment 2");
        models.add(model);
        String expected = "null^^null^^^^^^^^^^^^^^^^^^^^^^~null^^null^^^^^^^^^^^^^^^^^^^^^^";
        Method privateMethod = RhapsodysXmlToHl7Converter.class.getDeclaredMethod("streamHL7XCNTypeList", List.class);
        privateMethod.setAccessible(true);
        var result = privateMethod.invoke(parentClass, models);
        Assertions.assertEquals(expected, result);
    }

    @Test
    void streamHL7XONTypeListTestCoverAllMissingConditional() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        var parentClass = new RhapsodysXmlToHl7Converter();
        List<HL7XONType> models = new ArrayList<>();
        HL7XONType model = new HL7XONType();
        model.setHL7CheckDigitScheme("Comment 1");
        model.setHL7OrganizationNameTypeCode("Type Code");
        model.setHL7AssigningAuthority(new HL7HDType());
        model.setHL7AssigningFacility(new HL7HDType());
        models.add(model);
        model = new HL7XONType();
        model.setHL7CheckDigitScheme("Comment 2");
        models.add(model);
        String expected = "null^Type Code^^^^&&^^&&^^~null^^^^^^^^^";
        Method privateMethod = RhapsodysXmlToHl7Converter.class.getDeclaredMethod("streamHL7XONTypeList", List.class);
        privateMethod.setAccessible(true);
        var result = privateMethod.invoke(parentClass, models);
        Assertions.assertEquals(expected, result);
    }

    @Test
    void streamHL7XPNTypeListTestCoverAllMissingConditional() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        var parentClass = new RhapsodysXmlToHl7Converter();
        List<HL7XPNType> models = new ArrayList<>();
        HL7XPNType model = new HL7XPNType();
        model.setHL7Degree("Comment 1");
        model.setHL7ProfessionalSuffix("Suffix");
        models.add(model);
        model = new HL7XPNType();
        model.setHL7Degree("Comment 2");
        models.add(model);
        String expected = "^null^^^^Comment 1^^~^null^^^^Comment 2^^";
        Method privateMethod = RhapsodysXmlToHl7Converter.class.getDeclaredMethod("streamHL7XPNTypeList", List.class);
        privateMethod.setAccessible(true);
        var result = privateMethod.invoke(parentClass, models);
        Assertions.assertEquals(expected, result);
    }

    @Test
    void streamHL7CXTypeListTestCoverAllMissingConditional() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        var parentClass = new RhapsodysXmlToHl7Converter();
        List<HL7CXType> models = new ArrayList<>();
        HL7CXType model = new HL7CXType();
        HL7CWEType cwe = new HL7CWEType();
        cwe.setHL7CodingSystemVersionID("VERSION Id");
        model.setHL7EffectiveDate(new HL7DTType());
        model.setHL7ExpirationDate(new HL7DTType());
        model.setHL7AssigningJurisdiction(cwe);
        model.setHL7AssigningAgencyOrDepartment(cwe);
        models.add(model);
        model = new HL7CXType();
        model.setHL7EffectiveDate(new HL7DTType());
        models.add(model);
        Method privateMethod = RhapsodysXmlToHl7Converter.class.getDeclaredMethod("streamHL7CXTypeList", List.class);
        privateMethod.setAccessible(true);
        var result = privateMethod.invoke(parentClass, models);
        Assertions.assertNotNull(result);
    }

    @Test
    void streamHL7SITypeListTestCoverAllMissingConditional() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        var parentClass = new RhapsodysXmlToHl7Converter();
        List<HL7SIType> models = new ArrayList<>();
        HL7SIType model = new HL7SIType();
        model.setHL7SequenceID("Comment 1");
        models.add(model);
        model = new HL7SIType();
        model.setHL7SequenceID("Comment 2");
        models.add(model);
        String expected = "Comment 1~Comment 2";
        Method privateMethod = RhapsodysXmlToHl7Converter.class.getDeclaredMethod("streamHL7SITypeList", List.class);
        privateMethod.setAccessible(true);
        var result = privateMethod.invoke(parentClass, models);
        Assertions.assertEquals(expected, result);
    }

    @Test
    void streamHL7CWETypeListTestCoverAllMissingConditional() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        var parentClass = new RhapsodysXmlToHl7Converter();
        List<HL7CWEType> models = new ArrayList<>();
        HL7CWEType model = new HL7CWEType();
        model.setHL7AlternateCodingSystemVersionID("Comment 1");
        models.add(model);
        model = new HL7CWEType();
        model.setHL7AlternateCodingSystemVersionID("Comment 2");
        models.add(model);
        String expected = "^^^^^^^Comment 1^~^^^^^^^Comment 2^";
        Method privateMethod = RhapsodysXmlToHl7Converter.class.getDeclaredMethod("streamHL7CWETypeList", List.class);
        privateMethod.setAccessible(true);
        var result = privateMethod.invoke(parentClass, models);
        Assertions.assertEquals(expected, result);
    }

    @Test
    void streamHL7XCNTypeTestCoverAllMissingConditional() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        var parentClass = new RhapsodysXmlToHl7Converter();
        HL7XCNType model = new HL7XCNType();
        model.setHL7SecondAndFurtherGivenNamesOrInitialsThereof("1");
        model.setHL7Suffix("suffix");
        model.setHL7Prefix("prefix");
        model.setHL7Degree("degree");
        model.setHL7SourceTable("scr");
        model.setHL7IdentifierTypeCode("type");

        String expected = "null^^null^1^suffix^prefix^degree^scr^^^^^type^^^^^^^^^^^^";
        Method privateMethod = RhapsodysXmlToHl7Converter.class.getDeclaredMethod("streamHL7XCNType", HL7XCNType.class);
        privateMethod.setAccessible(true);
        var result = privateMethod.invoke(parentClass, model);
        Assertions.assertEquals(expected, result);
    }

    @Test
    void streamHL7XONTypeTestCoverAllMissingConditional() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        var parentClass = new RhapsodysXmlToHl7Converter();
        HL7XONType model = new HL7XONType();
        model.setHL7IdentifierTypeCode("12");
        model.setHL7NameRepresentationCode("present");
        model.setHL7OrganizationIdentifier("identifier");

        String expected = "null^^^^^^12^^present^identifier";
        Method privateMethod = RhapsodysXmlToHl7Converter.class.getDeclaredMethod("streamHL7XONType", HL7XONType.class);
        privateMethod.setAccessible(true);
        var result = privateMethod.invoke(parentClass, model);
        Assertions.assertEquals(expected, result);
    }

    @Test
    void streamHL7XPNTypeTestCoverAllMissingConditional() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        var parentClass = new RhapsodysXmlToHl7Converter();
        HL7XPNType model = new HL7XPNType();
        model.setHL7SecondAndFurtherGivenNamesOrInitialsThereof("12");
        model.setHL7Suffix("present");
        model.setHL7Prefix("identifier");
        model.setHL7NameTypeCode("identifier");

        String expected = "^null^12^present^identifier^^identifier^";
        Method privateMethod = RhapsodysXmlToHl7Converter.class.getDeclaredMethod("streamHL7XPNType", HL7XPNType.class);
        privateMethod.setAccessible(true);
        var result = privateMethod.invoke(parentClass, model);
        Assertions.assertEquals(expected, result);
    }

    @Test
    void streamHL7FNTypeTestCoverAllMissingConditional() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        var parentClass = new RhapsodysXmlToHl7Converter();
        HL7FNType model = new HL7FNType();
        model.setHL7OwnSurnamePrefix("12");
        model.setHL7OwnSurnamePrefix("present");
        model.setHL7SurnameFromPartnerSpouse("identifier");
        model.setHL7OwnSurname("test");
        model.setHL7SurnamePrefixFromPartnerSpouse("test");
        model.setHL7SurnameFromPartnerSpouse("partner");

        String expected = "null&present&present&test&partner";
        Method privateMethod = RhapsodysXmlToHl7Converter.class.getDeclaredMethod("streamHL7FNType", HL7FNType.class);
        privateMethod.setAccessible(true);
        var result = privateMethod.invoke(parentClass, model);
        Assertions.assertEquals(expected, result);
    }

    @Test
    void streamHL7CXTypeTestCoverAllMissingConditional() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        var parentClass = new RhapsodysXmlToHl7Converter();
        HL7CXType model = new HL7CXType();
        model.setHL7CheckDigit("12");
        model.setHL7CheckDigitScheme("present");

        String expected = "^12^present^^^^^^^^";
        Method privateMethod = RhapsodysXmlToHl7Converter.class.getDeclaredMethod("streamHL7CXType", HL7CXType.class);
        privateMethod.setAccessible(true);
        var result = privateMethod.invoke(parentClass, model);
        Assertions.assertEquals(expected, result);
    }

    @Test
    void streamHL7CWETypeTestCoverAllMissingConditional() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        var parentClass = new RhapsodysXmlToHl7Converter();
        HL7CWEType model = new HL7CWEType();
        model.setHL7AlternateText("12");
        model.setHL7NameofAlternateCodingSystem("present");
        model.setHL7OriginalText("present");

        String expected = "^^^^12^present^^^present";
        Method privateMethod = RhapsodysXmlToHl7Converter.class.getDeclaredMethod("streamHL7CWEType", HL7CWEType.class);
        privateMethod.setAccessible(true);
        var result = privateMethod.invoke(parentClass, model);
        Assertions.assertEquals(expected, result);
    }
}
