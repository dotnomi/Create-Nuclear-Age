package net.dotnomi.nuclearage.client;

public class ClientRadiationData {
    private static int playerRadiation;

    private static int playerRadiationPerSecond;

    public static void setPlayerRadiation(int radiation) {
        ClientRadiationData.playerRadiation = radiation;
    }

    public static int getPlayerRadiation() {
        return playerRadiation;
    }

    public static void setPlayerRadiationPerSecond(int radiation) {
        ClientRadiationData.playerRadiationPerSecond = radiation;
    }

    public static int getPlayerRadiationPerSecond() {
        return playerRadiationPerSecond;
    }
}
