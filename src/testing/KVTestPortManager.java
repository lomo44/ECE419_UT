package testing;

import java.util.concurrent.atomic.AtomicInteger;

public class KVTestPortManager {
    public static AtomicInteger port = new AtomicInteger(50000);
    public static int getPort(){
        return port.incrementAndGet();
    }
}
