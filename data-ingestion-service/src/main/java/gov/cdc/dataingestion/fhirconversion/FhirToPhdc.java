package gov.cdc.dataingestion.fhirconversion;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.*;
import org.hl7.fhir.r4.model.Reference;
import org.openhealthtools.mdht.uml.cda.*;
import org.openhealthtools.mdht.uml.cda.Organization;
import org.openhealthtools.mdht.uml.cda.Patient;
import org.openhealthtools.mdht.uml.cda.Person;
import org.openhealthtools.mdht.uml.cda.util.CDAUtil;
import org.openhealthtools.mdht.uml.hl7.datatypes.*;
import org.openhealthtools.mdht.uml.hl7.vocab.EntityNameUse;
import org.openhealthtools.mdht.uml.hl7.vocab.PostalAddressUse;
import org.openhealthtools.mdht.uml.hl7.vocab.TelecommunicationAddressUse;

import java.io.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class FhirToPhdc {
    private static final FhirContext context = FhirContext.forR4();
    private static final Map<String, String> addressUseMap;

    static {
        //https://build.fhir.org/ig/HL7/ccda-on-fhir/ConceptMap-FC-AddressUse.html
        addressUseMap = new HashMap<>();
        addressUseMap.put("home", "H");
        addressUseMap.put("work", "WP");
        addressUseMap.put("temp", "TMP");
        addressUseMap.put("old", "BAD");
        addressUseMap.put("billing", "PST");
    }

    private static final Map<String, String> telecomTypeMap;

    static {
        //https://build.fhir.org/ig/HL7/ccda-on-fhir/ConceptMap-FC-TelecomType.html
        telecomTypeMap = new HashMap<>();
        telecomTypeMap.put("email", "mailto");
        telecomTypeMap.put("url", "http");
        telecomTypeMap.put("phone", "tel (Telephone)");
        telecomTypeMap.put("pager", "tel (Telephone)");
        telecomTypeMap.put("fax", "x-text-fax (Fax)");
    }

    private static final Map<String, String> telecommAddressUseMap;

    static {
        https:
//build.fhir.org/ig/HL7/ccda-on-fhir/ConceptMap-FC-TelecomUse.html
        telecommAddressUseMap = new HashMap<>();
        telecommAddressUseMap.put("home", "HP");
        telecommAddressUseMap.put("work", "WP");
        telecommAddressUseMap.put("temp", "TMP");
        telecommAddressUseMap.put("old", "BAD");
        telecommAddressUseMap.put("mobile", "MC");
    }

    private static final Map<String, String> nameUseMap;

    static {
        //https://build.fhir.org/ig/HL7/ccda-on-fhir/ConceptMap-FC-NameUse.html
        nameUseMap = new HashMap<>();
        nameUseMap.put("usual", "L");
        nameUseMap.put("official", "L");//TODO C
        nameUseMap.put("nickname", "P");
        nameUseMap.put("anonymous", "P");
    }

    private static final Map<String, String> genderMap;

    static {
        //https://build.fhir.org/ig/HL7/ccda-on-fhir/ConceptMap-FC-AdministrativeGender.html
        genderMap = new HashMap<>();
        genderMap.put("male", "M");
        genderMap.put("female", "F");
        genderMap.put("other", "UN");
    }
    private static final Map<String, String> fhirURI_cdaOID_Map;
    static {
        //https://build.fhir.org/ig/HL7/ccda-on-fhir/mappingGuidance.html#mapping-oid--uri
        fhirURI_cdaOID_Map = new HashMap<>();
        fhirURI_cdaOID_Map.put("http://loinc.org","2.16.840.1.113883.6.1");
        fhirURI_cdaOID_Map.put("urn:oid:2.16.840.1.113883.4.123456789","2.16.840.1.113883.4.123456789");
    }

    private static final String RACE_ETHNICITY_SYSTEM="2.16.840.1.113883.6.238";

    public static void main(String[] args) throws IOException {
        System.out.println("--calling FhirToPhdc---");
        try {
            parseFhirBundle();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void parseFhirBundle() throws FileNotFoundException {
        // Instantiate a new parser
        //String filePath = "src/main/resources/eICR-TC-COVID-DX_20210412-bundle.json";
        String filePath = "src/main/resources/eICR-bundle-all-content-testing.json";

        try {
            String fhirContent = getLinesInFile(filePath);
            //Create parser
            IParser parser = context.newJsonParser();
            // Parse it
            Bundle bundle = parser.parseResource(Bundle.class, fhirContent);
            //composition.getI
            createClinicalDocument(bundle);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static void createClinicalDocument(Bundle bundle) throws Exception {
        //FHIR content

        Composition composition = (Composition) bundle.getEntry().get(0).getResource();
        //System.out.println("meta:"+bundle.getMeta().getSecurity());
        //CDA content
        ClinicalDocument doc = CDAFactory.eINSTANCE.createClinicalDocument();
        InfrastructureRootTypeId typeId = CDAFactory.eINSTANCE.createInfrastructureRootTypeId();
        typeId.setExtension("POCD_HD000040");
        doc.setTypeId(typeId);
        II id = DatatypesFactory.eINSTANCE.createII("2.16.840.1.113883.19", "c266");
        doc.setId(id);

        II templateId = DatatypesFactory.eINSTANCE.createII("2.16.840.1.113883.3.27.1776");
        doc.getTemplateIds().add(templateId);

        CE code = DatatypesFactory.eINSTANCE.createCE("55751-2", "2.16.840.1.113883.6.1", "LOINC", "Public Health Case Report - PHRI");
        doc.setCode(code);

        //ST title = DatatypesFactory.eINSTANCE.createST("Public Health Case Report - FHIR Bundle to CDA");
        ST title = DatatypesFactory.eINSTANCE.createST(composition.getTitle());
        doc.setTitle(title);

        //TS effectiveTime = DatatypesFactory.eINSTANCE.createTS("20000407");
        String dateTime = fhirDateToCDATime(composition.getDate());
        TS effectiveTime = DatatypesFactory.eINSTANCE.createTS(dateTime);//TODO

        doc.setEffectiveTime(effectiveTime);

        CE confidentialityCode = DatatypesFactory.eINSTANCE.createCE("N", "2.16.840.1.113883.5.25");
        doc.setConfidentialityCode(confidentialityCode);

        //Composition composition=(Composition)bundle.getEntry().get(0).getResource();
        System.out.println("Finished parsing");

        //ClinicalDocument/recordTarget/patientRole
        RecordTarget recordTarget = CDAFactory.eINSTANCE.createRecordTarget();
        doc.getRecordTargets().add(recordTarget);

        //PatientRole
        PatientRole patientRole = CDAFactory.eINSTANCE.createPatientRole();
        Patient patient = CDAFactory.eINSTANCE.createPatient();
        patientRole.setPatient(patient);

        List<Bundle.BundleEntryComponent> entries = bundle.getEntry();
        System.out.println(bundle.getEntry().size());
        for (Bundle.BundleEntryComponent entry : entries) {
            Resource resource = entry.getResource();
            String resourceType = resource.fhirType();
            //System.out.println("fhirType:"+resourceType);
            if (resource instanceof Composition) {
                System.out.println("Composition");
            } else if (resource instanceof org.hl7.fhir.r4.model.Patient) {
                System.out.println("Patient");
                //createPatient(resource);
                patientRole = createPatientRole(patientRole, resource);
            }
        }
        recordTarget.setPatientRole(patientRole);

//        Patient patient = CDAFactory.eINSTANCE.createPatient();
//        patientRole.setPatient(patient);

//        PN name = DatatypesFactory.eINSTANCE.createPN();
//        name.addGiven("Test name").addFamily("test family name").addSuffix("the 7th");
//        patient.getNames().add(name);

//        CE administrativeGenderCode = DatatypesFactory.eINSTANCE.createCE("M", "2.16.840.1.113883.5.1");
//        patient.setAdministrativeGenderCode(administrativeGenderCode);

//        TS birthTime = DatatypesFactory.eINSTANCE.createTS("19320924");
//        patient.setBirthTime(birthTime);

//        Organization providerOrganization = CDAFactory.eINSTANCE.createOrganization();
//        providerOrganization.getIds().add(DatatypesFactory.eINSTANCE.createII("2.16.840.1.113883.19.5"));
//        patientRole.setProviderOrganization(providerOrganization);

        Author author = CDAFactory.eINSTANCE.createAuthor();
        author.setTime(DatatypesFactory.eINSTANCE.createTS("2000040714"));
        doc.getAuthors().add(author);

        AssignedAuthor assignedAuthor = CDAFactory.eINSTANCE.createAssignedAuthor();
        assignedAuthor.getIds().add(DatatypesFactory.eINSTANCE.createII("2.16.840.1.113883.19.5", "KP00017"));
        author.setAssignedAuthor(assignedAuthor);

        Person assignedPerson = CDAFactory.eINSTANCE.createPerson();
        assignedAuthor.setAssignedPerson(assignedPerson);

        PN name = DatatypesFactory.eINSTANCE.createPN();
        name.addGiven("Bob").addFamily("Dolin").addSuffix("MD");
        assignedPerson.getNames().add(name);

        System.out.println("***** Constructed example *****");
        //CDAUtil.save(doc, System.out);
        CDAUtil.save(doc, new BufferedWriter(new FileWriter("src/main/resources/eICR-TC-COVID-DX_20210412_phdc.xml")));

        System.out.println();
        //PatientRole patientRole = CDAFactory.eINSTANCE.createPatientRole();
    }

    private static Patient createPatient(Resource fhirResource) {
        Patient patient = CDAFactory.eINSTANCE.createPatient();
        return patient;
    }

    private static PatientRole createPatientRole(PatientRole patientRole, Resource fhirResource) {
        //PatientRole patientRole = CDAFactory.eINSTANCE.createPatientRole();
        Patient patient = patientRole.getPatient();
        //FHIR identifier
        org.hl7.fhir.r4.model.Patient fhirPatient = (org.hl7.fhir.r4.model.Patient) fhirResource;
        //Patient Race, ethnicity
        List<Extension> extensionList=fhirPatient.getExtension();
        for (Extension extension : extensionList) {
            String extnUrl = extension.getUrl();
            System.out.println("top level extn:"+extension.getUrl());
            if(extnUrl.endsWith("us-core-race")){
                List<Extension> subExtList =extension.getExtension();
                for (Extension subExt : subExtList) {
                    System.out.println("getUrl:" + subExt.getUrl() + " getValue:" + subExt.getValue() + " getExtension:" + subExt.getExtension() + " getId:" + subExt.getId());
                    if (subExt.getUrl() != null && subExt.getUrl().equalsIgnoreCase("ombCategory")) {
                        System.out.println("1111value type:" + subExt.getValue().getClass().getName());
                        Coding raceCoding = (Coding) subExt.getValue();
                        CE raceCode = DatatypesFactory.eINSTANCE.createCE();
                        raceCode.setCode(raceCoding.getCode());
                        raceCode.setCodeSystem(RACE_ETHNICITY_SYSTEM);
                        raceCode.setDisplayName(raceCoding.getDisplay());
                        //ED for original text//TODO
                        //translation//TODO
                        patient.setRaceCode(raceCode);
                    } else {
                        System.out.println("2222value type:" + subExt.getValue().getClass().getName());
                        StringType raceType = (StringType) subExt.getValue();

                        CE raceCode = DatatypesFactory.eINSTANCE.createCE();
                        raceCode.setCode(raceType.getValue());
                        raceCode.setCodeSystem(RACE_ETHNICITY_SYSTEM);
                        raceCode.setDisplayName(raceType.getValue());
                        //ED for original text//TODO
                        //translation//TODO
                        patient.getSDTCRaceCodes().add(raceCode);
                    }
                }
            }else if(extnUrl.endsWith("us-core-ethnicity")){
                List<Extension> subExtList =extension.getExtension();
                for (Extension subExt : subExtList) {
                    System.out.println("33333getUrl:" + subExt.getUrl() + " getValue:" + subExt.getValue() + " getExtension:" + subExt.getExtension() + " getId:" + subExt.getId());
                    if (subExt.getUrl() != null && subExt.getUrl().equalsIgnoreCase("ombCategory")) {
                        System.out.println("444444value type:" + subExt.getValue().getClass().getName());
                        Coding raceCoding = (Coding) subExt.getValue();
                        CE ce = DatatypesFactory.eINSTANCE.createCE();
                        ce.setCode(raceCoding.getCode());
                        ce.setCodeSystem(RACE_ETHNICITY_SYSTEM);
                        ce.setDisplayName(raceCoding.getDisplay());
                        //ED for original text//TODO
                        //translation//TODO
                        patient.setEthnicGroupCode(ce);
                    } else {
                        System.out.println("55555value type:" + subExt.getValue().getClass().getName());
//                        StringType raceType = (StringType) subExt.getValue();
//
//                        CE raceCode = DatatypesFactory.eINSTANCE.createCE();
//                        raceCode.setCode(raceType.getValue());
//                        raceCode.setCodeSystem(RACE_ETHNICITY_SYSTEM);
//                        raceCode.setDisplayName(raceType.getValue());
                        //ED for original text//TODO
                        //translation//TODO
                    }
                }

            }else if(extnUrl.endsWith("us-core-birthsex")){

            }
        }
        //FHIR identifier
        System.out.println("pateint id:" + fhirPatient.getId());
        List<Identifier> identifierList = fhirPatient.getIdentifier();
//        for (Identifier identifier : identifierList) {
//            II id = DatatypesFactory.eINSTANCE.createII(identifier.getSystem(), identifier.getValue());
//            patientRole.getIds().add(id);
//        }
        List<II> idList=createIdListFromFhir(identifierList);
        patientRole.getIds().addAll(idList);
        //Address
        List<Address> addressList = fhirPatient.getAddress();
        List<AD> adList= createAddress(addressList);
        patientRole.getAddrs().addAll(adList);
        //maritalStatus
        System.out.println("hasMaritalStatus:"+fhirPatient.hasMaritalStatus()+" MaritalStatus:"+fhirPatient.getMaritalStatus());
        if(fhirPatient.hasMaritalStatus()){
            CodeableConcept codeableConcept =fhirPatient.getMaritalStatus();
            List<Coding> codingList=codeableConcept.getCoding();
            for (Coding coding : codingList) {
                CE ce=DatatypesFactory.eINSTANCE.createCE();
                ce.setCode(coding.getCode());
                ce.setCodeSystem(coding.getSystem());
                //ce.setCodeSystemName("");
                ce.setDisplayName(coding.getDisplay());
                patient.setMaritalStatusCode(ce);
            }
        }
        //Telecom
        List<ContactPoint> fhirContactlist = fhirPatient.getTelecom();
        List<TEL> telList= createTelecom(fhirContactlist);
        patientRole.getTelecoms().addAll(telList);
//        for (ContactPoint contactPoint : list) {
//            TEL tel = DatatypesFactory.eINSTANCE.createTEL(telecomTypeMap.get(contactPoint.getSystem().toCode()) + ":" + contactPoint.getValue());
//            tel.getUses().add(TelecommunicationAddressUse.get(telecommAddressUseMap.get(contactPoint.getUse().toCode())));
//            patientRole.getTelecoms().add(tel);
//        }
        //Name
        List<HumanName> nameList = fhirPatient.getName();
        for (HumanName humanName : nameList) {
            PN name = DatatypesFactory.eINSTANCE.createPN();
            name.addGiven(humanName.getGivenAsSingleString()).addFamily(humanName.getFamily());
            if (humanName.getSuffixAsSingleString() != null && !humanName.getSuffixAsSingleString().isEmpty()) {
                name.addSuffix(humanName.getSuffixAsSingleString());
            }
            if (humanName.getPrefixAsSingleString() != null && !humanName.getPrefixAsSingleString().isEmpty()) {
                name.addPrefix(humanName.getPrefixAsSingleString());
            }
            System.out.println("humanName.getUse().toCode():" + humanName.getUse().toCode());
            name.getUses().add(EntityNameUse.get(nameUseMap.get(humanName.getUse().toCode())));
            patient.getNames().add(name);
        }
        //Gender
        Enumerations.AdministrativeGender administrativeGender = fhirPatient.getGender();
        CE administrativeGenderCode = DatatypesFactory.eINSTANCE.createCE();
        administrativeGenderCode.setCode(genderMap.get(administrativeGender.toCode()));
        administrativeGenderCode.setDisplayName(administrativeGender.getDisplay());
        patient.setAdministrativeGenderCode(administrativeGenderCode);//TODO system code?
        //birthdate - birthTime
        String dateTime = fhirDateToCDATime(fhirPatient.getBirthDate());
        TS birthTime = DatatypesFactory.eINSTANCE.createTS(dateTime);//TODO check timestamp conversion
        patient.setBirthTime(birthTime);

        //deceasedBoolean - deceasedInd
        //CDAPackage.eINSTANCE.getSubjectPerson_SDTCDeceasedInd();
        //Missing in the CDA document code, not required?.//TODO

        //maritalStatus
        CE maritalStatusCode = DatatypesFactory.eINSTANCE.createCE();//need sample fhir //TODO
        patient.setMaritalStatusCode(maritalStatusCode);
        //communication.language
        List<org.hl7.fhir.r4.model.Patient.PatientCommunicationComponent> langCommnList = fhirPatient.getCommunication();
        for (org.hl7.fhir.r4.model.Patient.PatientCommunicationComponent patientCommn : langCommnList) {
            Coding coding = patientCommn.getLanguage().getCodingFirstRep();//TODO check coding
            CS languageCode = DatatypesFactory.eINSTANCE.createCS(coding.getCode());
            BL referenceInd = DatatypesFactory.eINSTANCE.createBL(patientCommn.getPreferred());
            LanguageCommunication langCommn = CDAFactory.eINSTANCE.createLanguageCommunication();
            langCommn.setLanguageCode(languageCode);
            langCommn.setPreferenceInd(referenceInd);
            //langCommn.setModeCode(CE);
            //langCommn.setProficiencyLevelCode(CE);
            patient.getLanguageCommunications().add(langCommn);
        }
        //managingOrganization
        Reference fhirOrg = fhirPatient.getManagingOrganization();
        //Identifier ordId = fhirOrg.getIdentifier();
        IBaseResource resource = fhirOrg.getResource();
        if (resource instanceof org.hl7.fhir.r4.model.Organization org) {
            Organization providerOrganization = CDAFactory.eINSTANCE.createOrganization();
            List<Identifier> orgIdList = org.getIdentifier();
            List<II> ordIdList=createIdListFromFhir(orgIdList);
            providerOrganization.getIds().addAll(ordIdList);
            //Name
            ON oname = DatatypesFactory.eINSTANCE.createON();
            oname.addText(org.getName());
            providerOrganization.getNames().add(oname);
            ///Address
            List<AD> orgADList= createAddress(org.getAddress());
            providerOrganization.getAddrs().addAll(orgADList);
            //Telephone
            List<ContactPoint> orgContactlist =org.getTelecom();
            List<TEL> orgTelList= createTelecom(orgContactlist);
            providerOrganization.getTelecoms().addAll(orgTelList);
            ////
            patientRole.setProviderOrganization(providerOrganization);
        }

        ////////////////

        return patientRole;
    }

    private static List<II> createIdListFromFhir(List<Identifier> identifierList) {
        List<II> list = new ArrayList<>();
        for (Identifier identifier : identifierList) {
            II id = DatatypesFactory.eINSTANCE.createII(identifier.getSystem(), identifier.getValue());
            list.add(id);
        }
        return list;
    }
    private static List<AD> createAddress(List<Address> fhirAddrList) {
        List<AD> addrList = new ArrayList<>();
        for(Address address : fhirAddrList) {
            AD ad = DatatypesFactory.eINSTANCE.createAD();
            List<StringType> lineList= address.getLine();
            for(StringType addrline:lineList){
                ad.addStreetAddressLine(addrline.asStringValue());
            }
            ad.addCity(address.getCity());
            ad.addState(address.getState());
            if(address.getCountry()!=null) {
                ad.addCountry(address.getCountry());
            }
            if(address.getUse()!=null) {
                Address.AddressUse addrUse=address.getUse();
                if(addrUse.toCode()!=null) {
                    ad.getUses().add(PostalAddressUse.get(addressUseMap.get(addrUse.toCode().toLowerCase())));
                }
            }
            if(address.getPostalCode()!=null) {
                ad.addPostalCode(address.getPostalCode());
            }
            addrList.add(ad);
        }
        return addrList;
    }
    private static List<TEL> createTelecom(List<ContactPoint> fhirContactlist){
        List<TEL> telList = new ArrayList<>();
        for (ContactPoint contactPoint : fhirContactlist) {
            TEL tel = DatatypesFactory.eINSTANCE.createTEL(telecomTypeMap.get(contactPoint.getSystem().toCode()) + ":" + contactPoint.getValue());
            tel.getUses().add(TelecommunicationAddressUse.get(telecommAddressUseMap.get(contactPoint.getUse().toCode())));
            telList.add(tel);
        }
        return telList;
    }
    /**
     * Read the specified file into a string.
     *
     * @param pathname full path to the file.
     * @return the file's contents as a string.
     * @throws IOException thrown when file I/O goes bad.
     */
    private static String getLinesInFile(final String pathname) throws IOException {
        StringBuilder sb = new StringBuilder();
        try {
            try (BufferedReader br = new BufferedReader(new FileReader(pathname))) {
                for (String line = br.readLine(); line != null; line = br.readLine())
                    sb.append(line).append('\n');
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    private static String fhirDateToCDATime(Date date) {
        //TODO -may need to support hr, sec too
        //https://build.fhir.org/ig/HL7/ccda-on-fhir/mappingGuidance.html#cda--fhir-timedates
        String pattern = "yyyyMMdd";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        Instant instant = date.toInstant();
        LocalDateTime ldt = instant.atZone(ZoneId.systemDefault()).toLocalDateTime();
        return ldt.format(formatter);
    }
}
