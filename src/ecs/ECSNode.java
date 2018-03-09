package ecs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import common.datastructure.KVRange;
import common.networknode.KVStorageNode;

public class ECSNode implements IECSNode{

	private String serverName;
	private String host;
	private int port;
	private String[] hashRange;
	
	public ECSNode(KVStorageNode node) {
		host = node.getHostName();
		port = node.getPortNumber();
		serverName = node.getserverName();
		hashRange = node.getHashRange().toStringArray();
	}
	
	@Override
	public String getNodeName() {
		return serverName;
	}

	@Override
	public String getNodeHost() {
		return host;
	}

	@Override
	public int getNodePort() {
		return port;
	}

	@Override
	public String[] getNodeHashRange() {
		return hashRange;
	}
	
	public static Collection<IECSNode> fromKVStorageNode (List<KVStorageNode> nodelist){
		Collection<IECSNode> output = new ArrayList<IECSNode>();
		for (KVStorageNode node : nodelist) {
			output.add(new ECSNode(node));
		}
		return output;
	}

}
