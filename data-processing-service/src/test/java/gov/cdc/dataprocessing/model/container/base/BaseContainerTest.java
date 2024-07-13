package gov.cdc.dataprocessing.model.container.base;


import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class BaseContainerTest {

    @Test
    void testGettersAndSetters() {
        BaseContainer baseContainer = new BaseContainer();

        // Test boolean fields
        baseContainer.setItNew(true);
        baseContainer.setItOld(true);
        baseContainer.setItDirty(true);
        baseContainer.setItDelete(true);

        assertTrue(baseContainer.isItNew());
        assertTrue(baseContainer.isItOld());
        assertTrue(baseContainer.isItDirty());
        assertTrue(baseContainer.isItDelete());

        // Test String field
        String superClassType = "TestSuperClass";
        baseContainer.setSuperClassType(superClassType);
        assertEquals(superClassType, baseContainer.getSuperClassType());

        // Test Collection field
        Collection<Object> ldfs = new ArrayList<>();
        ldfs.add("TestObject");
        baseContainer.setLdfs(ldfs);
        assertEquals(ldfs, baseContainer.getLdfs());
    }

    @Test
    void testSerialization() throws Exception {
        BaseContainer baseContainer = new BaseContainer();
        baseContainer.setItNew(true);
        baseContainer.setItOld(true);
        baseContainer.setItDirty(true);
        baseContainer.setItDelete(true);
        baseContainer.setSuperClassType("TestSuperClass");

        // Serialize the object
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(bos);
        out.writeObject(baseContainer);
        out.flush();
        byte[] serializedObject = bos.toByteArray();

        // Deserialize the object
        ByteArrayInputStream bis = new ByteArrayInputStream(serializedObject);
        ObjectInputStream in = new ObjectInputStream(bis);
        BaseContainer deserializedBaseContainer = (BaseContainer) in.readObject();

        // Test the deserialized object
        assertTrue(deserializedBaseContainer.isItNew());
        assertTrue(deserializedBaseContainer.isItOld());
        assertTrue(deserializedBaseContainer.isItDirty());
        assertTrue(deserializedBaseContainer.isItDelete());
        assertEquals("TestSuperClass", deserializedBaseContainer.getSuperClassType());
    }

}