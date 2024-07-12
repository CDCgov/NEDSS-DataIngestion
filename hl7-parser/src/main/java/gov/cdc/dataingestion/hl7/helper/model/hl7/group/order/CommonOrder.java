package gov.cdc.dataingestion.hl7.helper.model.hl7.group.order;

import gov.cdc.dataingestion.hl7.helper.model.hl7.message_data_type.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

import static gov.cdc.dataingestion.hl7.helper.helper.ModelListHelper.*;

@Getter
@Setter
public class CommonOrder {
    String orderControl;
    Ei placerOrderNumber = new Ei();
    Ei fillerOrderNumber = new Ei();
    Ei placerGroupNumber = new Ei();
    String orderStatus;
    String responseFlag;
    List<Tq> quantityTiming = new ArrayList<>();
    Eip parentOrder = new Eip();
    Ts dateTimeOfTransaction = new Ts();
    List<Xcn> enteredBy = new ArrayList<>();
    List<Xcn> verifiedBy = new ArrayList<>();
    List<Xcn> orderingProvider = new ArrayList<>();
    Pl entererLocation = new Pl();
    List<Xtn> callBackPhoneNumber = new ArrayList<>();
    Ts orderEffectiveDateTime = new Ts();
    Ce orderControlCodeReason = new Ce();
    Ce enteringOrganization = new Ce();
    Ce enteringDevice = new Ce();
    List<Xcn> actionBy = new ArrayList<>();
    Ce advancedBeneficiaryNoticeCode = new Ce();
    List<Xon> orderingFacilityName = new ArrayList<>();
    List<Xad> orderingFacilityAddress = new ArrayList<>();
    List<Xtn> orderingFacilityPhoneNumber = new ArrayList<>();
    List<Xad> orderingProviderAddress = new ArrayList<>();
    Cwe orderStatusModifier = new Cwe();
    Cwe advancedBeneficiaryNoticeOverrideReason = new Cwe();
    Ts fillerExpectedAvailabilityDateTime = new Ts();
    Cwe confidentialityCode = new Cwe();
    Cwe orderType = new Cwe();
    Cne entererAuthorizationMode = new Cne();
    Cwe parentUniversalServiceIdentifier = new Cwe();

    public CommonOrder(ca.uhn.hl7v2.model.v251.segment.ORC orc) {
        this.orderControl = orc.getOrderControl().getValue();
        this.placerOrderNumber = new Ei(orc.getPlacerOrderNumber());
        this.fillerOrderNumber = new Ei(orc.getFillerOrderNumber());
        this.placerGroupNumber = new Ei(orc.getPlacerOrderNumber());
        this.orderStatus = orc.getOrderStatus().getValue();
        this.responseFlag = orc.getResponseFlag().getValue();
        this.quantityTiming = getTqList(orc.getQuantityTiming());
        this.parentOrder = new Eip(orc.getORCParent());
        this.dateTimeOfTransaction = new Ts(orc.getDateTimeOfTransaction());
        this.enteredBy = getXcnList(orc.getEnteredBy());
        this.verifiedBy = getXcnList(orc.getVerifiedBy());
        this.orderingProvider = getXcnList(orc.getOrderingProvider());
        this.entererLocation = new Pl(orc.getEntererSLocation());
        this.callBackPhoneNumber = getXtnList(orc.getCallBackPhoneNumber());
        this.orderEffectiveDateTime = new Ts(orc.getOrderEffectiveDateTime());
        this.orderControlCodeReason = new Ce(orc.getOrderControlCodeReason());
        this.enteringOrganization = new Ce(orc.getOrderControlCodeReason());
        this.enteringDevice = new Ce(orc.getEnteringDevice());
        this.actionBy = getXcnList(orc.getActionBy());
        this.advancedBeneficiaryNoticeCode = new Ce(orc.getAdvancedBeneficiaryNoticeCode());
        this.orderingFacilityName = getXonList(orc.getOrderingFacilityName());
        this.orderingFacilityAddress = getXadList(orc.getOrderingFacilityAddress());
        this.orderingFacilityPhoneNumber = getXtnList(orc.getOrderingFacilityPhoneNumber());
        this.orderingProviderAddress = getXadList(orc.getOrderingProviderAddress());
        this.orderStatusModifier = new Cwe(orc.getOrderStatusModifier());
        this.advancedBeneficiaryNoticeOverrideReason = new Cwe(orc.getAdvancedBeneficiaryNoticeOverrideReason());
        this.fillerExpectedAvailabilityDateTime = new Ts(orc.getFillerSExpectedAvailabilityDateTime());
        this.confidentialityCode = new Cwe(orc.getConfidentialityCode());
        this.orderType = new Cwe(orc.getOrderType());
        this.entererAuthorizationMode = new Cne(orc.getEntererAuthorizationMode());
        this.parentUniversalServiceIdentifier = new Cwe(orc.getParentUniversalServiceIdentifier());
    }

    public CommonOrder() {

    }
}
