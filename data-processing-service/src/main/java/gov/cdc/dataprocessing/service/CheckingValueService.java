package gov.cdc.dataprocessing.service;

import gov.cdc.dataprocessing.constant.elr.ELRConstant;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.repository.nbs.srte.*;
import gov.cdc.dataprocessing.repository.nbs.srte.model.CodeValueGeneral;
import gov.cdc.dataprocessing.repository.nbs.srte.model.JurisdictionCode;
import gov.cdc.dataprocessing.repository.nbs.srte.model.RaceCode;
import gov.cdc.dataprocessing.repository.nbs.srte.model.StateCountyCodeValue;
import gov.cdc.dataprocessing.service.interfaces.ICheckingValueService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;
import java.util.TreeMap;

@Service
@Slf4j
public class CheckingValueService implements ICheckingValueService {
    private static final Logger logger = LoggerFactory.getLogger(CheckingValueService.class);

    private final JurisdictionCodeRepository jurisdictionCodeRepository;
    private final CodeValueGeneralRepository codeValueGeneralRepository;
    private final ElrXrefRepository elrXrefRepository;
    private  final RaceCodeRepository raceCodeRepository;
    private final StateCountyCodeValueRepository stateCountyCodeValueRepository;

    public CheckingValueService(JurisdictionCodeRepository jurisdictionCodeRepository,
                                CodeValueGeneralRepository codeValueGeneralRepository,
                                ElrXrefRepository elrXrefRepository,
                                RaceCodeRepository raceCodeRepository,
                                StateCountyCodeValueRepository stateCountyCodeValueRepository) {
        this.jurisdictionCodeRepository = jurisdictionCodeRepository;
        this.codeValueGeneralRepository = codeValueGeneralRepository;
        this.elrXrefRepository = elrXrefRepository;
        this.raceCodeRepository = raceCodeRepository;
        this.stateCountyCodeValueRepository = stateCountyCodeValueRepository;
    }

    public TreeMap<String, String> getRaceCodes() throws DataProcessingException {
        TreeMap<String, String> map = new TreeMap<>();
        try {
            var result = raceCodeRepository.findAllActiveRaceCodes();
            if (result.isPresent()) {
                var raceCode = result.get();
                for (RaceCode obj :raceCode) {
                    map.put(obj.getCode(), obj.getCodeShortDescTxt());
                }
            } else {
                throw  new DataProcessingException("Race Code Not Found In Database");
            }
        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage());
        }
        return  map;
    }

    public TreeMap<String, String> getCodedValues(String pType) throws DataProcessingException {
        TreeMap<String, String> map;
        if (pType.equals("S_JURDIC_C")) {
            map = getJurisdictionCode();
        } else {
            map = getCodedValue(pType);
        }
        return map;
    }

    public  String getCodeDescTxtForCd(String code, String codeSetNm) throws DataProcessingException {
        var map = getCodedValues(codeSetNm);
        String codeDesc = "";

        if (map.containsKey(code)) {
            codeDesc = map.get(code);
        }
        return codeDesc;
    }

    public String findToCode(String fromCodeSetNm, String fromCode, String toCodeSetNm) throws DataProcessingException {
        try {
            var result = elrXrefRepository.findToCodeByConditions(fromCodeSetNm, fromCode, toCodeSetNm);
            if (result.isPresent()) {
                return result.get().getToCode();
            }
            else {
                throw new DataProcessingException("Error");
            }
        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage());
        }

    }

    public String getCountyCdByDesc(String county, String stateCd) throws DataProcessingException {
        try {
            String code;
            String cnty = county.toUpperCase();
            if (!cnty.endsWith("COUNTY")) {
                cnty = cnty + " COUNTY";
            }
            Optional<List<StateCountyCodeValue>> result;
            if( stateCd==null || stateCd.trim().equals("")) {
                result = stateCountyCodeValueRepository.findByIndentLevelNbr();
            } else {
                result = stateCountyCodeValueRepository.findByIndentLevelNbrAndParentIsCdOrderByCodeDescTxt(stateCd);
            }

            if (result.isPresent()) {
                String comparer = cnty;
                var res = result.get().stream().filter(x -> x.getCode().equals(comparer)).findFirst();
                code = res.get().getCode();
            }
            else {
                throw new DataProcessingException("Error");
            }
            return code;
        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage());
        }

    }

    private TreeMap<String, String> getJurisdictionCode() throws DataProcessingException {
        TreeMap<String, String> map = new TreeMap<>();
        try {
            var codes = jurisdictionCodeRepository.findJurisdictionCodeValues();
            if (codes.isPresent()) {
                for (JurisdictionCode obj : codes.get()) {
                    map.put(obj.getCode(), obj.getCodeDescTxt());
                }
            }
            else {
                throw new DataProcessingException("Jurisdiction Code Not Exist In Database");
            }
        } catch (Exception e) {
            throw  new DataProcessingException(e.getMessage());
        }
        return map;
    }

    private TreeMap<String, String> getCodedValue(String code) throws DataProcessingException {
        TreeMap<String, String> map = new TreeMap<>();
        try {
            List<CodeValueGeneral> codeValueGeneralList;
            if (code.equals(ELRConstant.ELR_LOG_PROCESS)) {
                 var result = codeValueGeneralRepository.findCodeDescriptionsByCodeSetNm(code);
                 if (result.isPresent()) {
                     codeValueGeneralList = result.get();
                     for (CodeValueGeneral obj : codeValueGeneralList) {
                         map.put(obj.getCode(), obj.getCodeDescTxt());
                     }
                 }
                 else {
                     throw new DataProcessingException("Coded Value Not Exist In Database");
                 }
            }
            else {
                var result = codeValueGeneralRepository.findCodeValuesByCodeSetNm(code);
                if (result.isPresent()) {
                    codeValueGeneralList = result.get();
                    for (CodeValueGeneral obj : codeValueGeneralList) {
                        map.put(obj.getCode(), obj.getCodeShortDescTxt());
                    }
                }
                else {
                    throw new DataProcessingException("Coded Value Not Exist In Database");
                }
            }

        } catch (Exception e) {
            throw  new DataProcessingException(e.getMessage());
        }
        return map;
    }



}
