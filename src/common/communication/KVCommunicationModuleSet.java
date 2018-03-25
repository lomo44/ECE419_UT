package common.communication;

import common.messages.KVJSONMessage;
import common.networknode.KVNetworkNode;

import java.io.IOException;
import java.net.SocketException;
import java.util.*;

/**
 * Class that maintain set of communications
 * KVCommunication module will not be initialize until used.
 */
public class KVCommunicationModuleSet extends HashMap<KVNetworkNode,KVCommunicationModule> {
    /**
     * Asynchronous sending of broadcast
     * @param msg KVJSONMessage to be send
     */
    public void asyncBroadcastSend(KVJSONMessage msg){
        for(KVNetworkNode node: this.keySet()){
            if(initialConnection(node)){
                try {
                    this.get(node).send(msg);
                } catch (SocketException e) {
                    remove(node);
                }
            }
        }
    }
    public void asyncBroadcastSend(KVJSONMessage msg, Collection<? extends KVNetworkNode> nodes){
        for(KVNetworkNode node: nodes){
            try {
                getCommunicationModule(node).send(msg);
            } catch (SocketException e) {
            }
        }
    }
    public Vector<KVJSONMessage> asyncBroadcastReceive(KVJSONMessage msg, Collection<? extends KVNetworkNode> nodes){
        Vector<KVJSONMessage> received = new Vector<>();
        for(KVNetworkNode node: nodes){
            try {
                received.add(getCommunicationModule(node).receive());
            } catch (IllegalArgumentException | IOException e) {
            }
        }
        return received;
    }
    /**
     * Asynchronous receiving of broadcast
     * @return list of msg return by the connections
     */
    public Vector<KVJSONMessage> asyncBroadcastReceive(){
        Vector<KVJSONMessage> msg = new Vector<>();
        for(KVNetworkNode node: this.keySet()){
            if(initialConnection(node)){
                try {
                    msg.add(get(node).receive());
                } catch (IllegalArgumentException | IOException e) {
                    remove(node);
                }
            }
        }
        return msg;
    }
    /**
     * Synchronoust broadcast
     * @param msg msg to be sent
     * @return list of msg return by the connections
     */
    public Vector<KVJSONMessage> syncBroadcast(KVJSONMessage msg){
        asyncBroadcastSend(msg);
        return asyncBroadcastReceive();
    }
    public Vector<KVJSONMessage> syncBroadcast(KVJSONMessage msg, Collection<? extends KVNetworkNode> nodes){
        asyncBroadcastSend(msg,nodes);
        return asyncBroadcastReceive(msg,nodes);
    }
    /**
     * Send message to one of the connection
     * @param msg msg to be sent
     * @return
     */
    public KVJSONMessage sendOne(KVJSONMessage msg){
        KVJSONMessage ret = null;
        for(KVNetworkNode node: this.keySet()){
            if(initialConnection(node)){
                try {
                    get(node).send(msg);
                    ret = get(node).receive();
                    break;
                } catch (IllegalArgumentException | IOException e) {
                    // Socket exception, reset connection
                    remove(node);
                }
            }
        }
        return ret;
    }
    /**
     * Add connection to the Communication module set
     * @param node
     */
    public void add(KVNetworkNode node){
        super.put(node,null);
    }
    /**
     * Add set of nodes into the connection set
     * @param nodeList set of nodes
     */
    public void addNodes(List<? extends KVNetworkNode > nodeList){
        for (KVNetworkNode node :
                nodeList) {
            super.put(node,null);
        }
    }
    /**
     * initiate connection based on given node
     * @param node KVNetworkNode Definition
     * @return true if connection initiation is successful
     */
    private boolean initialConnection(KVNetworkNode node){
        if(this.get(node)==null){
            try {
                KVCommunicationModule module = node.createCommunicationModule();
                this.put(node,module);
                return true;
            } catch (IOException e) {
                return false;
            }
        }
        else{
            return true;
        }
    }
    public KVCommunicationModule getCommunicationModule(KVNetworkNode node) {
        if(initialConnection(node)){
            return this.get(node);
        }
        return null;
    }
    public KVJSONMessage syncSend(KVJSONMessage msg, KVNetworkNode node){
        KVJSONMessage ret = null;
        try {
            KVCommunicationModule module = getCommunicationModule(node);
            if(module!=null){
                module.send(msg);
                ret = module.receive();
            }
        } catch (IllegalArgumentException | IOException e) {

        }
        return ret;
    }
}
