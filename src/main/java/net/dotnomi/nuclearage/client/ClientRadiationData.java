package net.dotnomi.nuclearage.client;

public class ClientRadiationData {
    private static int playerRadiation;

    public static void set(int radiation) {
        ClientRadiationData.playerRadiation = radiation;
    }

    public static int getPlayerRadiation() {
        return playerRadiation;
    }
}
