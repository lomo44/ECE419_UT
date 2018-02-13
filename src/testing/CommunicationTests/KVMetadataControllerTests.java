package testing.CommunicationTests;

import common.KVNetworkID;
import common.metadata.KVMetadataController;
import junit.framework.TestCase;
import org.junit.Test;

import javax.xml.bind.DatatypeConverter;
import java.math.BigInteger;

public class KVMetadataControllerTests extends TestCase{
    protected KVMetadataController controller;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        controller = new KVMetadataController();
    }


    @Test
    public void testKVMetadataContoller_MD5Hash() throws Exception {
        String exampleString = "123";
        BigInteger hash = controller.hash(exampleString);

        assertEquals(new BigInteger(DatatypeConverter.parseHexBinary("202CB962AC59075B964B07152D234B70")),hash);
    }

    @Test
    public void testKVMetadataController_Map() throws Exception {
        controller.addNetworkID(new KVNetworkID("010",001));
        //37A33999994C8F95880620D477BC313E
        controller.addNetworkID(new KVNetworkID("78999",567));
        //869622A839324EBD797811C72339A471

        String item = "test_entry_a_as"; //3FB18B08D13FB0DDF99DE01920A11EBD
        BigInteger hash = controller.hash(item);
        KVNetworkID id = controller.getNetowrkIDMap(hash);
        assertEquals("010",id.getHostName());
        assertEquals(001,id.getPortNumber());
    }
}
