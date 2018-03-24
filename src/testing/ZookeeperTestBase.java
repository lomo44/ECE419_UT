package testing;

import junit.framework.TestCase;

import java.io.File;

public class ZookeeperTestBase extends TestCase {
    Process zkProcess;
    public static final String ZK_DATA_DIR = "./tmp/zookeeper";
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        zkProcess = new ProcessBuilder().inheritIO().command("./zookeeper-3.4.11/bin/zkServer.sh","start-foreground").start();
        Thread.sleep(1000);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        deleteDirectoryAndContent(new File(ZK_DATA_DIR));
        zkProcess.destroyForcibly();
    }

    private void deleteDirectoryAndContent(File path){
        File[] contents = path.listFiles();
        if(contents!=null){
            for(File f: contents){
                deleteDirectoryAndContent(f);
            }
        }
        path.delete();
    }
}
