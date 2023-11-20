package gov.cdc.dataingestion.hl7.helper.helper;

import ca.uhn.hl7v2.model.Varies;
import ca.uhn.hl7v2.model.v251.datatype.*;
import gov.cdc.dataingestion.hl7.helper.model.hl7.messageDataType.*;

import java.util.ArrayList;

public class ModelListHelper {
    private ModelListHelper() {

    }
    public static ArrayList<Xpn> getXpnList(XPN[] xpns) {
        var lst = new ArrayList<Xpn>();
        for(var data: xpns) {
            Xpn item = new Xpn(data);
            lst.add(item);
        }
        return lst;
    }

    public static ArrayList<Cx> getCxList(CX[] cxs) {
        var lst = new ArrayList<Cx>();
        for(var data: cxs) {
            Cx item = new Cx(data);
            lst.add(item);
        }
        return lst;
    }

    public static ArrayList<Ce> getCeList(CE[] ces) {
        var lst = new ArrayList<Ce>();
        for(var data: ces) {
            Ce item = new Ce(data);
            lst.add(item);
        }
        return lst;
    }

    public static ArrayList<Xad> getXadList(XAD[] messages) {
        var lst = new ArrayList<Xad>();
        for(var data: messages) {
            Xad item = new Xad(data);
            lst.add(item);
        }
        return lst;
    }

    public static ArrayList<Tq> getTqList(TQ[] messages) {
        var lst = new ArrayList<Tq>();
        for(var data: messages) {
            Tq item = new Tq(data);
            lst.add(item);
        }
        return lst;
    }

    public static ArrayList<Ndl> getNdlList(NDL[] messages) {
        var lst = new ArrayList<Ndl>();
        for(var data: messages) {
            Ndl item = new Ndl(data);
            lst.add(item);
        }
        return lst;
    }

    public static ArrayList<Pln> getPlnList(PLN[] messages) {
        var lst = new ArrayList<Pln>();
        for(var data: messages) {
            Pln item = new Pln(data);
            lst.add(item);
        }
        return lst;
    }

    public static ArrayList<Xtn> getXtnList(XTN[] messages) {
        var lst = new ArrayList<Xtn>();
        for(var data: messages) {
            Xtn item = new Xtn(data);
            lst.add(item);
        }
        return lst;
    }

    public static ArrayList<Xcn> getXcnList(XCN[] messages) {
        var lst = new ArrayList<Xcn>();
        for(var data: messages) {
            Xcn item = new Xcn(data);
            lst.add(item);
        }
        return lst;
    }

    public static ArrayList<Xon> getXonList(XON[] messages) {
        var lst = new ArrayList<Xon>();
        for(var data: messages) {
            Xon item = new Xon(data);
            lst.add(item);
        }
        return lst;
    }

    public static ArrayList<String> getIsStringList(IS[] messages) {
        var lst = new ArrayList<String>();
        for(var data: messages) {
            lst.add(data.getValue());
        }
        return lst;
    }

    public static ArrayList<String> getSiStringList(SI[] messages) {
        var lst = new ArrayList<String>();
        for(var data: messages) {
            lst.add(data.getValue());
        }
        return lst;
    }

    public static ArrayList<String> getFtStringList(FT[] messages) {
        var lst = new ArrayList<String>();
        for(var data: messages) {
            lst.add(data.getValue());
        }
        return lst;
    }

    public static ArrayList<String> getNmStringList(NM[] messages) {
        var lst = new ArrayList<String>();
        for(var data: messages) {
            lst.add(data.getValue());
        }
        return lst;
    }

    public static ArrayList<String> getDtStringList(DT[] messages) {
        var lst = new ArrayList<String>();
        for(var data: messages) {
            lst.add(data.getValue());
        }
        return lst;
    }

    public static ArrayList<String> getStStringList(ST[] messages) {
        var lst = new ArrayList<String>();
        for(var data: messages) {
            lst.add(data.getValue());
        }
        return lst;
    }

    public static ArrayList<String> getTmStringList(TM[] messages) {
        var lst = new ArrayList<String>();
        for(var data: messages) {
            lst.add(data.getValue());
        }
        return lst;
    }

    public static ArrayList<String> getIdStringList(ID[] messages) {
        var lst = new ArrayList<String>();
        for(var data: messages) {
            lst.add(data.getValue());
        }
        return lst;
    }

    public static ArrayList<String> getVariesStringList(Varies[] messages) {
        var lst = new ArrayList<String>();
        for(var data: messages) {
            lst.add(data.getData().toString());
        }
        return lst;
    }


    public static ArrayList<Cwe> getCweList(CWE[] messages) {
        var lst = new ArrayList<Cwe>();
        for(var data: messages) {
            Cwe item = new Cwe(data);
            lst.add(item);
        }
        return lst;
    }

    public static ArrayList<Eip> getEipList(EIP[] messages) {
        var lst = new ArrayList<Eip>();
        for(var data: messages) {
            Eip item = new Eip(data);
            lst.add(item);
        }
        return lst;
    }

    public static ArrayList<Cq> getCqList(CQ[] messages) {
        var lst = new ArrayList<Cq>();
        for(var data: messages) {
            Cq item = new Cq(data);
            lst.add(item);
        }
        return lst;
    }

    public static ArrayList<Ei> getEiList(EI[] messages) {
        var lst = new ArrayList<Ei>();
        for(var data: messages) {
            Ei item = new Ei(data);
            lst.add(item);
        }
        return lst;
    }


    public static ArrayList<Rpt> getRptList(RPT[] messages) {
        var lst = new ArrayList<Rpt>();
        for(var data: messages) {
            Rpt item = new Rpt(data);
            lst.add(item);
        }
        return lst;
    }

    public static ArrayList<Fc> getFcList(FC[] messages) {
        var lst = new ArrayList<Fc>();
        for(var data: messages) {
            Fc item = new Fc(data);
            lst.add(item);
        }
        return lst;
    }

    public static ArrayList<Ts> getTsList(TS[] messages) {
        var lst = new ArrayList<Ts>();
        for(var data: messages) {
            Ts item = new Ts(data);
            lst.add(item);
        }
        return lst;
    }

}
