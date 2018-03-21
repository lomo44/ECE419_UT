package common.communication;

import common.messages.KVJSONMessage;
import common.networknode.KVStorageCluster;
import testing.CommonModuleTests.KVStorageClusterTest;

public class KVClusterCommunicationModule extends IKVCommunicationModule {
    KVStorageCluster cluster;
    KVCommunicationModuleSet memberConnections = new KVCommunicationModuleSet();
    public KVClusterCommunicationModule(KVStorageCluster cluster){
        this.cluster = cluster;
    }

    @Override
    public void send(KVJSONMessage msg) throws Exception {

    }

    @Override
    public KVJSONMessage receive() throws Exception {
        return null;
    }

    @Override
    public void close() throws Exception {

    }
}
