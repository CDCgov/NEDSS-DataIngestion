package gov.cdc.dataingestion.nbs.ecr.service.helper;

import gov.cdc.dataingestion.exception.EcrCdaXmlException;
import gov.cdc.nedss.phdc.cda.TS;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static gov.cdc.dataingestion.nbs.ecr.constant.CdaConstantValue.valueName;
import static gov.cdc.dataingestion.nbs.ecr.constant.CdaConstantValue.xmlNameSpaceHolder;

public class CdaMapHelper {
    public static XmlObject mapToCData(String data) throws EcrCdaXmlException {
        try {
            XmlObject xmlObject = XmlObject.Factory.parse("<CDATA>"+data+"</CDATA>");
            return xmlObject;
        } catch (Exception e) {
            throw new EcrCdaXmlException(e.getMessage());
        }

    }


    public static XmlObject mapToStringData(String data) throws EcrCdaXmlException {
        try {
            XmlObject xmlObject = XmlObject.Factory.parse("<STRING>"+data+"</STRING>");
            return xmlObject;
        } catch (Exception e) {
            throw new EcrCdaXmlException(e.getMessage());
        }

    }

    public static XmlObject mapToUsableTSElement(String data, XmlObject output, String name) throws ParseException {
        XmlCursor cursor = output.newCursor();
        cursor.toFirstChild();  // Move to the root element

        cursor.beginElement(name);
        cursor.insertAttributeWithValue("type", "IVL_TS");
        cursor.toFirstChild();  // Move inside childName
        cursor.beginElement("low");
        cursor.insertNamespace("", xmlNameSpaceHolder);
        cursor.insertAttributeWithValue(valueName, mapToTsType(data).getValue().toString());
        cursor.dispose();
        return output;
    }

    public static TS mapToTsType(String data) throws ParseException {
        TS ts = TS.Factory.newInstance();
        String result = "";
        boolean checkerCode = data.contains("/");
        boolean checkerCodeDash = data.contains("-");
        if (!checkerCode && !checkerCodeDash) {
            result = data;
        }
        else if (checkerCodeDash && !data.isEmpty()) {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
            SimpleDateFormat outputFormat = new SimpleDateFormat("yyyyMMdd HH:mm:ss.S");
            Date date = inputFormat.parse(data);
            result = outputFormat.format(date);
        }
        else if (checkerCode && !data.isEmpty()) {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.S");
            SimpleDateFormat outputFormat = new SimpleDateFormat("yyyyMMdd HH:mm:ss.S");
            Date date = inputFormat.parse(data);
            result = outputFormat.format(date);
        }

        ts.setValue(result);
        return ts;
    }



}
