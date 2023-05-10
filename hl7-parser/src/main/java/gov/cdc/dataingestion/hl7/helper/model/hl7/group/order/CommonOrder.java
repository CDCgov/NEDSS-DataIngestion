package gov.cdc.dataingestion.hl7.helper.model.hl7.group.order;

import gov.cdc.dataingestion.hl7.helper.model.hl7.messageDataType.*;
import lombok.Getter;
import static gov.cdc.dataingestion.hl7.helper.helper.modelListHelper.*;
import java.util.List;

@Getter
public class CommonOrder {
    String orderControl;
    Ei placerOrderNumber;
    Ei fillerOrderNumber;
    Ei placerGroupNumber;
    String orderStatus;
    String responseFlag;
    List<Tq> quantityTiming;
    Eip parentOrder;
    Ts dateTimeOfTransaction;
    List<Xcn> enteredBy;
    List<Xcn> verifiedBy;
    List<Xcn> orderingProvider;
    Pl entererLocation;
    List<Xtn> callBackPhoneNumber;
    Ts orderEffectiveDateTime;
    Ce orderControlCodeReason;
    Ce enteringOrganization;
    Ce enteringDevice;
    List<Xcn> actionBy;
    Ce advancedBeneficiaryNoticeCode;
    List<Xon> orderingFacilityName;
    List<Xad> orderingFacilityAddress;
    List<Xtn> orderingFacilityPhoneNumber;
    List<Xad> orderingProviderAddress;
    Cwe orderStatusModifier;
    Cwe advancedBeneficiaryNoticeOverrideReason;
    Ts fillerExpectedAvailabilityDateTime;
    Cwe confidentialityCode;
    Cwe orderType;
    Cne entererAuthorizationMode;
    Cwe parentUniversalServiceIdentifier;

    public CommonOrder(ca.uhn.hl7v2.model.v251.segment.ORC orc) {
        this.orderControl = orc.getOrderControl().getValue();
        this.placerOrderNumber = new Ei(orc.getPlacerOrderNumber());
        this.fillerOrderNumber = new Ei(orc.getFillerOrderNumber());
        this.placerGroupNumber = new Ei(orc.getPlacerOrderNumber());
        this.orderStatus = orc.getOrderStatus().getValue();
        this.responseFlag = orc.getResponseFlag().getValue();
        this.quantityTiming = GetTqList(orc.getQuantityTiming());
        this.parentOrder = new Eip(orc.getORCParent());
        this.dateTimeOfTransaction = new Ts(orc.getDateTimeOfTransaction());
        this.enteredBy = GetXcnList(orc.getEnteredBy());
        this.verifiedBy = GetXcnList(orc.getVerifiedBy());
        this.orderingProvider = GetXcnList(orc.getOrderingProvider());
        this.entererLocation = new Pl(orc.getEntererSLocation());
        this.callBackPhoneNumber = GetXtnList(orc.getCallBackPhoneNumber());
        this.orderEffectiveDateTime = new Ts(orc.getOrderEffectiveDateTime());
        this.orderControlCodeReason = new Ce(orc.getOrderControlCodeReason());
        this.enteringOrganization = new Ce(orc.getOrderControlCodeReason());
        this.enteringDevice = new Ce(orc.getEnteringDevice());
        this.actionBy = GetXcnList(orc.getActionBy());
        this.advancedBeneficiaryNoticeCode = new Ce(orc.getAdvancedBeneficiaryNoticeCode());
        this.orderingFacilityName = GetXonList(orc.getOrderingFacilityName());
        this.orderingFacilityAddress = GetXadList(orc.getOrderingFacilityAddress());
        this.orderingFacilityPhoneNumber = GetXtnList(orc.getOrderingFacilityPhoneNumber());
        this.orderingProviderAddress = GetXadList(orc.getOrderingProviderAddress());
        this.orderStatusModifier = new Cwe(orc.getOrderStatusModifier());
        this.advancedBeneficiaryNoticeOverrideReason = new Cwe(orc.getAdvancedBeneficiaryNoticeOverrideReason());
        this.fillerExpectedAvailabilityDateTime = new Ts(orc.getFillerSExpectedAvailabilityDateTime());
        this.confidentialityCode = new Cwe(orc.getConfidentialityCode());
        this.orderType = new Cwe(orc.getOrderType());
        this.entererAuthorizationMode = new Cne(orc.getEntererAuthorizationMode());
        this.parentUniversalServiceIdentifier = new Cwe(orc.getParentUniversalServiceIdentifier());
    }
}
