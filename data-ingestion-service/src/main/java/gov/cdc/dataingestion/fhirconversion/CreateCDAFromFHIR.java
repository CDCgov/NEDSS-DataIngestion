package gov.cdc.dataingestion.fhirconversion;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Composition;
import org.openhealthtools.mdht.uml.cda.*;
import org.openhealthtools.mdht.uml.cda.util.CDAUtil;
import org.openhealthtools.mdht.uml.hl7.datatypes.*;

import java.io.*;
import java.util.List;

public class CreateCDAFromFHIR {
    private static final FhirContext context = FhirContext.forR4();
    public static void main(String[] args) throws IOException {
        System.out.println("--calling CreateCDAFromFHIR---");
        try{
            parseFhirBundle();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private static void parseFhirBundle() throws FileNotFoundException {
        // Instantiate a new parser
        String filePath="src/main/resources/eICR-fhir-bundle.json";
        try{
            String fhirContent=getLinesInFile( filePath);
            //Create parser
            IParser parser = context.newJsonParser();
            // Parse it
            Bundle bundle= parser.parseResource(Bundle.class, fhirContent);
            List entries= bundle.getEntry();
            System.out.println(bundle.getEntry().size());
            Composition composition=(Composition)bundle.getEntry().get(0).getResource();
            System.out.println("Finished parsing");
            createClinicalDocument();
        }catch (Exception e){
            e.printStackTrace();
        }

    }
    private static void createClinicalDocument() throws Exception {
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

        ST title = DatatypesFactory.eINSTANCE.createST("Public Health Case Report - FHIR Bundle to CDA");
        doc.setTitle(title);

        TS effectiveTime = DatatypesFactory.eINSTANCE.createTS("20000407");
        doc.setEffectiveTime(effectiveTime);

        CE confidentialityCode = DatatypesFactory.eINSTANCE.createCE("N", "2.16.840.1.113883.5.25");
        doc.setConfidentialityCode(confidentialityCode);

        RecordTarget recordTarget = CDAFactory.eINSTANCE.createRecordTarget();
        doc.getRecordTargets().add(recordTarget);

        PatientRole patientRole = CDAFactory.eINSTANCE.createPatientRole();
        recordTarget.setPatientRole(patientRole);

        Patient patient = CDAFactory.eINSTANCE.createPatient();
        patientRole.setPatient(patient);

        PN name = DatatypesFactory.eINSTANCE.createPN();
        name.addGiven("Henry").addFamily("Levin").addSuffix("the 7th");
        patient.getNames().add(name);

        CE administrativeGenderCode = DatatypesFactory.eINSTANCE.createCE("M", "2.16.840.1.113883.5.1");
        patient.setAdministrativeGenderCode(administrativeGenderCode);

        TS birthTime = DatatypesFactory.eINSTANCE.createTS("19320924");
        patient.setBirthTime(birthTime);

        Organization providerOrganization = CDAFactory.eINSTANCE.createOrganization();
        providerOrganization.getIds().add(DatatypesFactory.eINSTANCE.createII("2.16.840.1.113883.19.5"));
        patientRole.setProviderOrganization(providerOrganization);

        Author author = CDAFactory.eINSTANCE.createAuthor();
        author.setTime(DatatypesFactory.eINSTANCE.createTS("2000040714"));
        doc.getAuthors().add(author);

        AssignedAuthor assignedAuthor = CDAFactory.eINSTANCE.createAssignedAuthor();
        assignedAuthor.getIds().add(DatatypesFactory.eINSTANCE.createII("2.16.840.1.113883.19.5", "KP00017"));
        author.setAssignedAuthor(assignedAuthor);

        Person assignedPerson = CDAFactory.eINSTANCE.createPerson();
        assignedAuthor.setAssignedPerson(assignedPerson);

        name = DatatypesFactory.eINSTANCE.createPN();
        name.addGiven("Bob").addFamily("Dolin").addSuffix("MD");
        assignedPerson.getNames().add(name);

        System.out.println("***** Constructed example *****");
        //CDAUtil.save(doc, System.out);
        CDAUtil.save(doc, new BufferedWriter(new FileWriter("src/main/resources/eICR-from-fhir-to-phdc.xml")));

        System.out.println();
        //PatientRole patientRole = CDAFactory.eINSTANCE.createPatientRole();
    }
    /**
     * Read the specified file into a string.
     * @param pathname full path to the file.
     * @return the file's contents as a string.
     * @throws IOException thrown when file I/O goes bad.
     */
    private static String getLinesInFile( final String pathname ) throws IOException
    {
        StringBuilder sb = new StringBuilder();
        try{
            try ( BufferedReader br = new BufferedReader( new FileReader( pathname ) ) )
            {
                for( String line = br.readLine(); line != null; line = br.readLine() )
                    sb.append( line ).append( '\n' );
            }
        }catch ( Exception e){
            e.printStackTrace();
        }
        return sb.toString();
    }
}
