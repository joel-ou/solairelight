package io.github.joelou.solairelight;

import lombok.Getter;

/**
 * @author Joel Ou
 */
public class SolairelightSettings {

    @Getter
    private static boolean cluster = false;
    @Getter
    private static String nodeIdSuffix = "";

    static void setCluster(boolean cluster) {
        SolairelightSettings.cluster = cluster;
    }

    static void setNodeIdSuffix(String nodeIdSuffix) {
        SolairelightSettings.nodeIdSuffix = nodeIdSuffix;
    }

}
