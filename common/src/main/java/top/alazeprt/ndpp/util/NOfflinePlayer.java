package top.alazeprt.ndpp.util;

public abstract class NOfflinePlayer {
    private final String name;

    public NOfflinePlayer(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
