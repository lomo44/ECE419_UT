package common.enums;

public enum eKVNodeCreationMode {
    /**
     * Each created node will initialize a new cluster.
     */
    INDIVIDUAL_CLUSTER,
    /**
     * Cluster contains exactly 2 server based on node hashvalue
     */
    JOIN_CLUSTER_MAX_SIZE_3,
    /**
     * No rule, when added a node, no cluster will be formed.
     */
    FREE_NODE
}
