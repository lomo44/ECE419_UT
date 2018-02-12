package testing.InteractiveTest;

import app_kvClient.KVClient;
import app_kvServer.KVServer;
import junit.framework.TestCase;
import org.junit.Test;
import org.omg.CORBA.portable.InputStream;
import testing.KVTestPortManager;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.PrintWriter;

public class BasicTest extends TestCase {

    private KVServer server = null;
    private int port;
    private Thread clientThread = null;
    private KVClient client = null;
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        port = KVTestPortManager.getPort();
        server = new KVServer(port,10,"LRU");
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        if(server!=null){
            server.close();
            server = null;
        }
        if(clientThread!=null){
            clientThread.join();
            clientThread=null;
        }
    }

    @Test
    public void test_quit() throws InterruptedException {
        String command = "quit\n";
        client = new KVClient(new ByteArrayInputStream(command.getBytes()));
        clientThread = new Thread(client);
        clientThread.start();
        clientThread.join();
    }

    @Test
    public void test_Connect_and_Quit() throws InterruptedException {
        String command = "connect localhost "+port+"\nquit\n";
        client = new KVClient(new ByteArrayInputStream(command.getBytes()));
        clientThread = new Thread(client);
        clientThread.start();
        clientThread.join();
    }

    @Test
    public void test_Connect_Error_and_Quit() throws InterruptedException {
        String command = "connect localhost1 "+port+"\nquit\n";
        client = new KVClient(new ByteArrayInputStream(command.getBytes()));
        clientThread = new Thread(client);
        clientThread.start();
        clientThread.join();
    }


    @Test
    public void test_Put() throws InterruptedException {
        String command = "connect localhost "+port+"\n" +
                "put aaa 123\n"+
                "quit\n";
        client = new KVClient(new ByteArrayInputStream(command.getBytes()));
        clientThread = new Thread(client);
        clientThread.start();
        clientThread.join();
    }
    @Test
    public void test_Get() throws InterruptedException {
        String command = "connect localhost "+port+"\n" +
                "get aaa\n"+
                "quit\n";
        client = new KVClient(new ByteArrayInputStream(command.getBytes()));
        clientThread = new Thread(client);
        clientThread.start();
        clientThread.join();
    }
    @Test
    public void test_Help() throws InterruptedException {
        String command =
                "help"+"\n"+
                "quit"+"\n";
        client = new KVClient(new ByteArrayInputStream(command.getBytes()));
        clientThread = new Thread(client);
        clientThread.start();
        clientThread.join();
    }
    @Test
    public void test_LogLevel() throws InterruptedException {
        String command =
                "logLevel "+"ALL"+"\n"+
                "quit" + "\n";
        client = new KVClient(new ByteArrayInputStream(command.getBytes()));
        clientThread = new Thread(client);
        clientThread.start();
        clientThread.join();
    }
}
