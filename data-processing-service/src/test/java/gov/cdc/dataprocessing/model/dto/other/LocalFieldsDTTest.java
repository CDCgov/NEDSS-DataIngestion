package gov.cdc.dataprocessing.model.dto.other;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LocalFieldsDTTest {

    private LocalFieldsDT localFieldsDT;

    @BeforeEach
    void setUp() {
        localFieldsDT = new LocalFieldsDT();
    }

    @Test
    void testSettersAndGetters() {
        Long id = 12345L;
        String questionLabel = "Question Label";
        String typeCdDesc = "Type Description";
        Integer orderNbr = 1;
        Long questionUid = 67890L;
        Long parentUid = 11112L;
        String tab = "Tab1";
        String section = "Section1";
        String subSection = "SubSection1";
        String viewLink = "http://view.link";
        String editLink = "http://edit.link";
        String deleteLink = "http://delete.link";

        localFieldsDT.setNbsUiMetadataUid(id);
        localFieldsDT.setQuestionLabel(questionLabel);
        localFieldsDT.setTypeCdDesc(typeCdDesc);
        localFieldsDT.setOrderNbr(orderNbr);
        localFieldsDT.setNbsQuestionUid(questionUid);
        localFieldsDT.setParentUid(parentUid);
        localFieldsDT.setTab(tab);
        localFieldsDT.setSection(section);
        localFieldsDT.setSubSection(subSection);
        localFieldsDT.setViewLink(viewLink);
        localFieldsDT.setEditLink(editLink);
        localFieldsDT.setDeleteLink(deleteLink);

        assertEquals(id, localFieldsDT.getNbsUiMetadataUid());
        assertEquals(questionLabel, localFieldsDT.getQuestionLabel());
        assertEquals(typeCdDesc, localFieldsDT.getTypeCdDesc());
        assertEquals(orderNbr, localFieldsDT.getOrderNbr());
        assertEquals(questionUid, localFieldsDT.getNbsQuestionUid());
        assertEquals(parentUid, localFieldsDT.getParentUid());
        assertEquals(tab, localFieldsDT.getTab());
        assertEquals(section, localFieldsDT.getSection());
        assertEquals(subSection, localFieldsDT.getSubSection());
        assertEquals(viewLink, localFieldsDT.getViewLink());
        assertEquals(editLink, localFieldsDT.getEditLink());
        assertEquals(deleteLink, localFieldsDT.getDeleteLink());
    }
}
