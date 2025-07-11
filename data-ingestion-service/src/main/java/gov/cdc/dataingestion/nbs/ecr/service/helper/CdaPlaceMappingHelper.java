package gov.cdc.dataingestion.nbs.ecr.service.helper;

import gov.cdc.dataingestion.exception.EcrCdaXmlException;
import gov.cdc.dataingestion.nbs.ecr.model.CdaPlaceMapper;
import gov.cdc.dataingestion.nbs.ecr.model.place.PlaceField;
import gov.cdc.dataingestion.nbs.ecr.service.helper.interfaces.ICdaMapHelper;
import gov.cdc.dataingestion.nbs.ecr.service.helper.interfaces.ICdaPlaceMappingHelper;
import gov.cdc.dataingestion.nbs.repository.model.dao.EcrSelectedRecord;
import gov.cdc.dataingestion.nbs.repository.model.dto.EcrMsgPlaceDto;
import gov.cdc.nedss.phdc.cda.*;
import org.apache.xmlbeans.XmlCursor;

import java.util.List;
import java.util.Map;

import static gov.cdc.dataingestion.nbs.ecr.constant.CdaConstantValue.*;

/**
 1118 - require constructor complaint
 125 - comment complaint
 6126 - String block complaint
 1135 - todos complaint
 * */
@SuppressWarnings({"java:S1118","java:S125", "java:S6126", "java:S1135"})
public class CdaPlaceMappingHelper implements ICdaPlaceMappingHelper {
    ICdaMapHelper cdaMapHelper;
    public CdaPlaceMappingHelper(ICdaMapHelper cdaMapHelper) {
        this.cdaMapHelper = cdaMapHelper;
    }

    public CdaPlaceMapper mapToPlaceTop(EcrSelectedRecord input,
                                         int performerComponentCounter, int componentCounter,
                                         int performerSectionCounter,
                                         POCDMT000040Section section) throws EcrCdaXmlException {

            CdaPlaceMapper mapper = new CdaPlaceMapper();
            if(input.getMsgPlaces() != null && !input.getMsgPlaces().isEmpty()) {
                for(int i = 0; i < input.getMsgPlaces().size(); i++) {
                    section = mapToPlaceTopFieldCheck( input,
                             section,
                     performerComponentCounter,
                     i);
                }
            }

            mapper.setSection(section);
            mapper.setPerformerComponentCounter(performerComponentCounter);
            mapper.setComponentCounter(componentCounter);
            mapper.setPerformerComponentCounter(performerComponentCounter);
            return mapper;


    }

    private POCDMT000040Section mapToPlaceTopFieldCheck(EcrSelectedRecord input,
                                         POCDMT000040Section section,
                                         int performerComponentCounter,
                                         int i) throws EcrCdaXmlException {
        if (section == null) {
            section = POCDMT000040Section.Factory.newInstance();
            section.addNewCode();
            section.addNewTitle();
        }

        var model = this.cdaMapHelper.mapOrgPlaceDocCommonField( section, performerComponentCounter);
        section = model.getClinicalDocument();
        int c = model.getPerformerSectionCounter(); // NOSONAR
        POCDMT000040Participant2 out = model.getOut();

        POCDMT000040Participant2 output = mapToPlace(input.getMsgPlaces().get(i), out);

        section = this.cdaMapHelper.mapOrgPlaceProviderActCommonField( section,
                c,
                output);

        section.getEntryArray(c).getAct().getCode().setCode("PLC");
        section.getEntryArray(c).getAct().getCode().setCodeSystem(CLINICAL_CODE_SYSTEM);
        section.getEntryArray(c).getAct().getCode().setCodeSystemName(CLINICAL_CODE_SYSTEM_NAME);
        section.getEntryArray(c).getAct().getCode().setDisplayName(ACT_CODE_DISPLAY_NAME);
        return section;
    }


    private POCDMT000040Participant2 mapToPlace(EcrMsgPlaceDto in, POCDMT000040Participant2 out) throws EcrCdaXmlException {
        String state="";
        String streetAddress1="";
        String streetAddress2="";
        String city = "";
        String county = "";
        String country = "";
        String zip = "";
        String workPhone= "";
        String workExtn = "";
        String workURL = "";
        String workEmail = "";
        String workCountryCode="";
        String placeComments="";

        String placeAddressComments="";
        int teleCounter=0;
        String teleAsOfDate="";
        String postalAsOfDate="";
        String censusTract="";

        for (Map.Entry<String, Object> entry : in.getDataMap().entrySet()) {
            String name = entry.getKey();
            String value = this.cdaMapHelper.getValueFromMap(entry);

            var param = new PlaceField();
            param.setState(state);
            param.setStreetAddress1(streetAddress1);
            param.setStreetAddress2(streetAddress2);
            param.setCity(city);
            param.setCounty(county);
            param.setCountry(country);
            param.setZip(zip);
            param.setWorkPhone(workPhone);
            param.setWorkExtn(workExtn);
            param.setWorkURL(workURL);
            param.setWorkEmail(workEmail);
            param.setWorkCountryCode(workCountryCode);
            param.setPlaceComments(placeComments);
            param.setPlaceAddressComments(placeAddressComments);
            param.setTeleAsOfDate(teleAsOfDate);
            param.setPostalAsOfDate(postalAsOfDate);
            param.setCensusTract(censusTract);
            param.setOut(out);
            var placeField = mapToPlaceFieldCheckP1( in,
                     out,
                     name,
                     value,
                     param);

            state = placeField.getState();
            streetAddress1 = placeField.getStreetAddress1();
            streetAddress2 = placeField.getStreetAddress2();
            city = placeField.getCity();
            county = placeField.getCounty();
            country = placeField.getCountry();
            zip = placeField.getZip();
            workPhone = placeField.getWorkPhone();
            workExtn = placeField.getWorkExtn();
            workURL = placeField.getWorkURL();
            workEmail = placeField.getWorkEmail();
            workCountryCode = placeField.getWorkCountryCode();
            placeComments = placeField.getPlaceComments();
            placeAddressComments = placeField.getPlaceAddressComments();
            teleAsOfDate = placeField.getTeleAsOfDate();
            postalAsOfDate = placeField.getPostalAsOfDate();
            censusTract = placeField.getCensusTract();
            out = placeField.getOut();
        }

        PlaceField param2 = new PlaceField();
        param2.setState(state);
        param2.setStreetAddress1(streetAddress1);
        param2.setStreetAddress2(streetAddress2);
        param2.setCity(city);
        param2.setCounty(county);
        param2.setCountry(country);
        param2.setZip(zip);
        param2.setWorkPhone(workPhone);
        param2.setWorkExtn(workExtn);
        param2.setWorkURL(workURL);
        param2.setWorkEmail(workEmail);
        param2.setWorkCountryCode(workCountryCode);
        param2.setPlaceAddressComments(placeAddressComments);
        param2.setTeleAsOfDate(teleAsOfDate);
        param2.setPostalAsOfDate(postalAsOfDate);
        param2.setCensusTract(censusTract);
        param2.setOut(out);
        var field2 = mapToPlaceFieldCheckP2(
                 out,
                 param2,
                teleCounter);
        out  = field2.getOut();

        return out;
    }

    private PlaceField mapToPlaceFieldCheckP2(
                                        POCDMT000040Participant2 out,
                                        PlaceField param,
                                        int teleCounter) throws EcrCdaXmlException {
        String state= param.getState();
        String streetAddress1= param.getStreetAddress1();
        String streetAddress2= param.getStreetAddress2();
        String city = param.getCity();
        String county = param.getCounty();
        String country = param.getCountry();
        String zip = param.getZip();
        String workPhone= param.getWorkPhone();
        String workExtn = param.getWorkExtn();
        String workURL = param.getWorkURL();
        String workEmail = param.getWorkEmail();
        String workCountryCode= param.getWorkCountryCode();
        String placeAddressComments= param.getPlaceAddressComments();
        String teleAsOfDate=param.getTeleAsOfDate();
        String postalAsOfDate= param.getPostalAsOfDate();
        String censusTract= param.getCensusTract();

        int isAddressPopulated= 0;
        if(!streetAddress1.isEmpty() ){
            mapToPlaceFieldCheckP2Address1(streetAddress1, out );
            isAddressPopulated=1;
        }
        if(!streetAddress2.isEmpty()){
            mapToPlaceFieldCheckP2Address2( streetAddress2, out );
            isAddressPopulated=1;
        }
        if(!city.isEmpty()){
            mapToPlaceFieldCheckP2City( city, out );
            isAddressPopulated=1;
        }
        if(!state.isEmpty()){
            mapToPlaceFieldCheckP2State( state, out );
            isAddressPopulated=1;
        }
        if(!county.isEmpty()){
            mapToPlaceFieldCheckP2County( county, out );
            isAddressPopulated=1;
        }
        if(!zip.isEmpty()){
            mapToPlaceFieldCheckP2Zip( zip, out );
            isAddressPopulated=1;
        }
        if(!country.isEmpty()){
            mapToPlaceFieldCheckP2Country( country, out );
            isAddressPopulated=1;
        }
        if(!censusTract.isEmpty()){
            mapToPlaceFieldCheckP2Census( censusTract, out );
        }
        if(isAddressPopulated>0){
            mapToPlaceFieldCheckP2Postal( postalAsOfDate, out );
        }
        if(!placeAddressComments.isEmpty()){
            mapToPlaceFieldCheckP2AddressComment( placeAddressComments, out );
        }
        if(!workPhone.isEmpty()){

            mapToPlaceFieldCheckP2WorkPhone( teleCounter,
             out,
             workCountryCode,
             workPhone,
             workExtn,
             teleAsOfDate);
            teleCounter = teleCounter+1;
        }
        if(!workEmail.isEmpty()){
            mapToPlaceFieldCheckP2Email( teleCounter,
             out,
             workEmail,
             teleAsOfDate);
            teleCounter = teleCounter +1;
        }
        if(!workURL.isEmpty()){
            mapToPlaceFieldCheckP2Url( teleCounter,
             out,
             workURL,
             teleAsOfDate);
        }

        param.setOut(out);

        return param;
    }

    private void mapToPlaceFieldCheckP2Url(int teleCounter,
                                                                 POCDMT000040Participant2 out,
                                                                 String workURL,
                                                                 String teleAsOfDate)  {
        if (out.getParticipantRole() == null) {
            out.addNewParticipantRole().addNewTelecom();
        } else {
            out.getParticipantRole().addNewTelecom();
        }
        out.getParticipantRole().getTelecomArray(teleCounter).setUse(List.of("WP"));
        out.getParticipantRole().getTelecomArray(teleCounter).setValue(workURL);
        if(!teleAsOfDate.isEmpty()){
            // IGNORE:
        }
    }

    private void mapToPlaceFieldCheckP2Email(int teleCounter,
                                                                     POCDMT000040Participant2 out,
                                                                     String workEmail,
                                                                     String teleAsOfDate)  {
        if (out.getParticipantRole() == null) {
            out.addNewParticipantRole().addNewTelecom();
        } else {
            out.getParticipantRole().addNewTelecom();
        }
        out.getParticipantRole().getTelecomArray(teleCounter).setUse(List.of("WP"));
        out.getParticipantRole().getTelecomArray(teleCounter).setValue(MAIL_TO+workEmail);
        if(!teleAsOfDate.isEmpty()){
            // IGNORE:
        }
    }


    private void mapToPlaceFieldCheckP2WorkPhone(int teleCounter,
                                                                     POCDMT000040Participant2 out,
                                                                     String workCountryCode,
                                                                     String workPhone,
                                                                     String workExtn,
                                                                     String teleAsOfDate)  {
        if (out.getParticipantRole() == null) {
            out.addNewParticipantRole().addNewTelecom();
        } else {
            out.getParticipantRole().addNewTelecom();
        }
        out.getParticipantRole().getTelecomArray(teleCounter).setUse(List.of("WP"));
        int countryphoneCodeSize= workCountryCode.length();
        if(countryphoneCodeSize>0){
            workPhone = workCountryCode+"-"+ workPhone;
        }

        int phoneExtnSize = workExtn.length();
        if(phoneExtnSize>0){
            workPhone=workPhone+ EXTN_STR+ workExtn;
        }
        out.getParticipantRole().getTelecomArray(teleCounter).setValue(workPhone);
        if(!teleAsOfDate.isEmpty()){
            // IGNORE
        }
    }

    private void mapToPlaceFieldCheckP2AddressComment(String placeAddressComments,POCDMT000040Participant2 out ) throws EcrCdaXmlException {
        if (out.getParticipantRole() == null) {
            out.addNewParticipantRole().addNewAddr();
        } else {
            out.getParticipantRole().addNewAddr();
        }
        out.getParticipantRole().getAddrArray(0).addNewAdditionalLocator();

        out.getParticipantRole().getAddrArray(0).setAdditionalLocatorArray(0,  AdxpAdditionalLocator.Factory.newInstance());
        out.getParticipantRole().getAddrArray(0).getAdditionalLocatorArray(0).set(cdaMapHelper.mapToCData(placeAddressComments));
    }

    private void mapToPlaceFieldCheckP2Postal(String postalAsOfDate,POCDMT000040Participant2 out )   {
        if (out.getParticipantRole() == null) {
            out.addNewParticipantRole().addNewAddr();
        } else {
            out.getParticipantRole().addNewAddr();
        }
        out.getParticipantRole().getAddrArray()[0].setUse(List.of("WP"));
        if(!postalAsOfDate.isEmpty()){
            // IGNORE
        }
    }

    private void mapToPlaceFieldCheckP2Census(String censusTract,POCDMT000040Participant2 out ) throws EcrCdaXmlException {
        if (out.getParticipantRole() == null) {
            out.addNewParticipantRole().addNewAddr();
        } else {
            out.getParticipantRole().addNewAddr();
        }
        out.getParticipantRole().getAddrArray(0).addNewCensusTract();

        out.getParticipantRole().getAddrArray(0).setCensusTractArray(0,  AdxpCensusTract.Factory.newInstance());
        out.getParticipantRole().getAddrArray(0).getCensusTractArray(0).set(cdaMapHelper.mapToPCData(censusTract));
    }

    private void mapToPlaceFieldCheckP2Country(String country,POCDMT000040Participant2 out ) throws EcrCdaXmlException {
        if (out.getParticipantRole() == null) {
            out.addNewParticipantRole().addNewAddr();
        } else {
            out.getParticipantRole().addNewAddr();
        }
        AdxpCountry val = AdxpCountry.Factory.newInstance();
        val.set(cdaMapHelper.mapToCData(country));
        out.getParticipantRole().getAddrArray(0).addNewCountry();

        out.getParticipantRole().getAddrArray(0).setCountryArray(0,  AdxpCountry.Factory.newInstance());
        out.getParticipantRole().getAddrArray(0).getCountryArray(0).set(cdaMapHelper.mapToCData(country));
    }

    private void mapToPlaceFieldCheckP2Zip(String zip,POCDMT000040Participant2 out ) throws EcrCdaXmlException {
        if (out.getParticipantRole() == null) {
            out.addNewParticipantRole().addNewAddr();
        } else {
            out.getParticipantRole().addNewAddr();
        }
        out.getParticipantRole().getAddrArray(0).addNewPostalCode();

        out.getParticipantRole().getAddrArray(0).setPostalCodeArray(0,  AdxpPostalCode.Factory.newInstance());
        out.getParticipantRole().getAddrArray(0).getPostalCodeArray(0).set(cdaMapHelper.mapToPCData(zip   ));
    }

    private void mapToPlaceFieldCheckP2County(String county,POCDMT000040Participant2 out ) throws EcrCdaXmlException {
        if (out.getParticipantRole() == null) {
            out.addNewParticipantRole().addNewAddr();
        } else {
            out.getParticipantRole().addNewAddr();
        }
        out.getParticipantRole().getAddrArray(0).addNewCounty();

        out.getParticipantRole().getAddrArray(0).setCountyArray(0,  AdxpCounty.Factory.newInstance());
        out.getParticipantRole().getAddrArray(0).getCountyArray(0).set(cdaMapHelper.mapToCData(county));
    }

    private void mapToPlaceFieldCheckP2State(String state,POCDMT000040Participant2 out ) throws EcrCdaXmlException {
        if (out.getParticipantRole() == null) {
            out.addNewParticipantRole().addNewAddr();
        } else {
            out.getParticipantRole().addNewAddr();
        }
        out.getParticipantRole().getAddrArray(0).addNewState();
        out.getParticipantRole().getAddrArray(0).setStateArray(0,  AdxpState.Factory.newInstance());
        out.getParticipantRole().getAddrArray(0).getStreetAddressLineArray(0).set(cdaMapHelper.mapToCData(state  ));
    }

    private void mapToPlaceFieldCheckP2City(String city,POCDMT000040Participant2 out ) throws EcrCdaXmlException {
        if (out.getParticipantRole() == null) {
            out.addNewParticipantRole().addNewAddr();
        } else {
            out.getParticipantRole().addNewAddr();
        }

        out.getParticipantRole().getAddrArray(0).addNewCity();
        out.getParticipantRole().getAddrArray(0).setCityArray(0,  AdxpCity.Factory.newInstance());
        out.getParticipantRole().getAddrArray(0).getCityArray(0).set(cdaMapHelper.mapToCData(city));
    }

    private void mapToPlaceFieldCheckP2Address2(String streetAddress2,POCDMT000040Participant2 out ) throws EcrCdaXmlException {
        if (out.getParticipantRole() == null) {
            out.addNewParticipantRole().addNewAddr();
        } else {
            out.getParticipantRole().addNewAddr();
        }
        out.getParticipantRole().getAddrArray(0).addNewStreetAddressLine();
        if (out.getParticipantRole().getAddrArray().length > 1) {
            out.getParticipantRole().getAddrArray(0).setStreetAddressLineArray(1,  AdxpStreetAddressLine.Factory.newInstance());
            out.getParticipantRole().getAddrArray(0).getStreetAddressLineArray(1).set(cdaMapHelper.mapToCData(streetAddress2));
        }
        else {
            out.getParticipantRole().getAddrArray(0).setStreetAddressLineArray(0,  AdxpStreetAddressLine.Factory.newInstance());
            out.getParticipantRole().getAddrArray(0).getStreetAddressLineArray(0).set(cdaMapHelper.mapToCData(streetAddress2));
        }
    }

    private void mapToPlaceFieldCheckP2Address1(String streetAddress1,POCDMT000040Participant2 out ) {
        AdxpStreetAddressLine val = AdxpStreetAddressLine.Factory.newInstance();
        XmlCursor cursor = val.newCursor();
        cursor.setTextValue(CDATA + streetAddress1 + CDATA);
        cursor.dispose();

        int c = 0;
        if (out.getParticipantRole().getAddrArray().length == 0) {
            out.getParticipantRole().addNewAddr().addNewStreetAddressLine();
        } else {
            c = out.getParticipantRole().getAddrArray().length;
            out.getParticipantRole().addNewAddr().addNewStreetAddressLine();
        }

        out.getParticipantRole().getAddrArray(c).setStreetAddressLineArray(0, val);
    }

    private PlaceField mapToPlaceFieldCheckP1Helper4(String value, String name, EcrMsgPlaceDto in, PlaceField param) {
        String workPhone= param.getWorkPhone();
        String teleAsOfDate=param.getTeleAsOfDate();
        String censusTract= param.getCensusTract();
        String postalAsOfDate= param.getPostalAsOfDate();
        String country = param.getCountry();
        String workExtn = param.getWorkExtn();


        if (name.equals("plaAddrCountryCd")&& value != null && !in.getPlaAddrCountryCd().isEmpty()){
            country=in.getPlaAddrCountryCd();
        }
        if (name.equals("plaPhoneNbrTxt") && value != null&& !in.getPlaPhoneNbrTxt().isEmpty()){
            workPhone=in.getPlaPhoneNbrTxt();
        }
        if (name.equals("plaAddrAsOfDt") && value != null && in.getPlaAddrAsOfDt() != null){
            postalAsOfDate=in.getPlaAddrAsOfDt().toString();
        }
        if (name.equals("plaCensusTractTxt") && value != null&& !in.getPlaCensusTractTxt().isEmpty()){
            censusTract=in.getPlaCensusTractTxt();
        }
        if (name.equals("plaPhoneAsOfDt") && value != null&& in.getPlaPhoneAsOfDt() != null ){
            teleAsOfDate=in.getPlaPhoneAsOfDt().toString();
        }
        if (name.equals("plaPhoneExtensionTxt") && value != null&& !in.getPlaPhoneExtensionTxt().isEmpty()){
            workExtn= in.getPlaPhoneExtensionTxt();
        }
        param.setWorkExtn(workExtn);
        param.setTeleAsOfDate(teleAsOfDate);
        param.setPostalAsOfDate(postalAsOfDate);
        param.setCensusTract(censusTract);
        param.setCountry(country);
        param.setWorkPhone(workPhone);

        return param;

    }

    private PlaceField mapToPlaceFieldCheckP1Helper1(String value, String name, EcrMsgPlaceDto in, PlaceField param) {
        String zip = param.getZip();
        String state= param.getState();
        String city = param.getCity();
        String streetAddress1= param.getStreetAddress1();
        String streetAddress2= param.getStreetAddress2();
        String county = param.getCounty();


        if (name.equals("plaAddrStreetAddr1Txt")&& value != null && !in.getPlaAddrStreetAddr1Txt().isEmpty()){
            streetAddress1= in.getPlaAddrStreetAddr1Txt();
        }
        if (name.equals("plaAddrStreetAddr2Txt")&& value != null && !in.getPlaAddrStreetAddr2Txt().isEmpty()){
            streetAddress2 =in.getPlaAddrStreetAddr2Txt();
        }
        if (name.equals("plaAddrCityTxt")&& value != null && !in.getPlaAddrCityTxt().isEmpty()){
            city= in.getPlaAddrCityTxt();
        }
        if (name.equals("plaAddrCountyCd")&& value != null && !in.getPlaAddrCountyCd().isEmpty()){
            county= in.getPlaAddrCountyCd();
        }
        if (name.equals("plaAddrStateCd")&& value != null && !in.getPlaAddrStateCd().isEmpty()){
            state= in.getPlaAddrStateCd();
        }
        if (name.equals("plaAddrZipCodeTxt")&& value != null && !in.getPlaAddrZipCodeTxt().isEmpty()){
            zip = in.getPlaAddrZipCodeTxt();
        }

        param.setState(state);
        param.setStreetAddress1(streetAddress1);
        param.setStreetAddress2(streetAddress2);
        param.setCity(city);
        param.setCounty(county);
        param.setZip(zip);

        return param;
    }

    private POCDMT000040Participant2 mapToPlaceFieldCheckP1Helper5(String value, String name, EcrMsgPlaceDto in,
                                                POCDMT000040Participant2 out) {
        if (name.equals("plaIdQuickCode") && value != null&& !in.getPlaIdQuickCode().isEmpty()){

            int c = 0;
            if (out.getParticipantRole() == null) {
                out.addNewParticipantRole().addNewId();
            } else {
                if (out.getParticipantRole().getIdArray().length > 0) {
                    c = out.getParticipantRole().getIdArray().length;
                }
                out.addNewParticipantRole().addNewId();
            }

            out.getParticipantRole().getIdArray(c).setRoot(ID_ARR_ROOT);
            out.getParticipantRole().getIdArray(c).setExtension(in.getPlaIdQuickCode());
            out.getParticipantRole().getIdArray(c).setAssigningAuthorityName("LR_QEC");
        }
        return out;
    }
    private POCDMT000040Participant2 mapToPlaceFieldCheckP1Helper2(String value, String name, EcrMsgPlaceDto in,
                                                     POCDMT000040Participant2 out) throws EcrCdaXmlException {
        if (name.equals("plaTypeCd") && value != null&& !in.getPlaTypeCd().isEmpty()){
            if (out.getParticipantRole() == null) {
                out.addNewParticipantRole().addNewCode();
            } else {
                out.getParticipantRole().addNewCode();
            }

            String questionCode= this.cdaMapHelper.mapToQuestionId("PLA_TYPE_CD");
            out.getParticipantRole().addNewCode();
            out.getParticipantRole().setCode(this.cdaMapHelper.mapToCEAnswerType(in.getPlaTypeCd(), questionCode));
        }
        if (name.equals("plaCommentTxt") && value != null&& !in.getPlaCommentTxt().isEmpty()){
            if (out.getParticipantRole() == null) {
                out.addNewParticipantRole().addNewPlayingEntity().addNewDesc();
            } else {
                out.getParticipantRole().addNewPlayingEntity().addNewDesc();
            }

            out.getParticipantRole().getPlayingEntity().getDesc().set(cdaMapHelper.mapToCData(in.getPlaCommentTxt()));
        }

        return out;
    }

    private POCDMT000040Participant2 mapToPlaceFieldCheckP1Helper3(String value, String name, EcrMsgPlaceDto in,
                                                                   POCDMT000040Participant2 out) {
        if(name.equals("plaLocalId") && value != null && !in.getPlaLocalId().isEmpty()){
            if (out.getParticipantRole() == null) {
                out.addNewParticipantRole().addNewId();
            } else {
                out.getParticipantRole().addNewId();
            }

            out.setTypeCode("PRF");
            out.getParticipantRole().getIdArray(0).setRoot(ID_ARR_ROOT);
            out.getParticipantRole().getIdArray(0).setExtension(in.getPlaLocalId());
            out.getParticipantRole().getIdArray(0).setAssigningAuthorityName("LR");
        }

        if (name.equals("plaNameTxt") && value != null && !in.getPlaNameTxt().isEmpty()){
            if (out.getParticipantRole() == null) {
                out.addNewParticipantRole().addNewPlayingEntity().addNewName();
            } else {
                out.getParticipantRole().addNewPlayingEntity().addNewName();
            }

            PN val = PN.Factory.newInstance();

            XmlCursor cursor = val.newCursor();
            cursor.setTextValue(CDATA + in.getPlaNameTxt() + CDATA);
            cursor.dispose();

            out.getParticipantRole().getPlayingEntity().addNewName();
            out.getParticipantRole().getPlayingEntity().setNameArray(0, val);
        }
        return out;
    }

    @SuppressWarnings("java:S6541")
    private PlaceField mapToPlaceFieldCheckP1(EcrMsgPlaceDto in,
                                        POCDMT000040Participant2 out,
                                        String name,
                                        String value,
                                        PlaceField param) throws EcrCdaXmlException {


        String workURL = param.getWorkURL();
        String workEmail = param.getWorkEmail();
        String workCountryCode= param.getWorkCountryCode();
        String placeComments= param.getPlaceComments();
        String placeAddressComments= param.getPlaceAddressComments();

        mapToPlaceFieldCheckP1Helper3( value,  name,  in,
                 out);

        if (name.equals("plaCommentTxt") && value != null&& !in.getPlaCommentTxt().isEmpty()){
            placeAddressComments= in.getPlaCommentTxt();
        }
        if (name.equals("plaPhoneCountryCodeTxt") && value != null&& !in.getPlaPhoneCountryCodeTxt().isEmpty()){
            workCountryCode= in.getPlaPhoneCountryCodeTxt();
        }
        if (name.equals("plaEmailAddressTxt") && value != null&& !in.getPlaEmailAddressTxt().isEmpty()){
            workEmail= in.getPlaEmailAddressTxt();
        }
        if (name.equals("plaUrlAddressTxt") && value != null&& !in.getPlaUrlAddressTxt().isEmpty()){
            workURL= in.getPlaUrlAddressTxt();
        }
        if (name.equals("plaPhoneCommentTxt") && value != null&& !in.getPlaPhoneCommentTxt().isEmpty()){
            placeComments= in.getPlaPhoneCommentTxt();
        }

        mapToPlaceFieldCheckP1Helper2( value,  name,  in,
                 out);
        mapToPlaceFieldCheckP1Helper5( value,  name,  in,
                out);

        mapToPlaceFieldCheckP1Helper1( value,  name,  in,  param);
        mapToPlaceFieldCheckP1Helper4( value,  name,  in,  param);
        param.setWorkURL(workURL);
        param.setWorkEmail(workEmail);
        param.setWorkCountryCode(workCountryCode);
        param.setPlaceComments(placeComments);
        param.setPlaceAddressComments(placeAddressComments);
        param.setOut(out);

        return param;
    }

}
