package top.alazeprt.ndpp.util;

import java.net.InetAddress;

public abstract class NOnlinePlayer {
    private final InetAddress address;
    private final String name;

    public NOnlinePlayer(InetAddress address, String name) {
        this.name = name;
        this.address = address;
    }

    public InetAddress getAddress() {
        return address;
    }

    public abstract void kick(String reason);

    public abstract void sendMessage(String message);

    public abstract boolean hasPermission(String permission);

    public String getName() {
        return name;
    }
}
