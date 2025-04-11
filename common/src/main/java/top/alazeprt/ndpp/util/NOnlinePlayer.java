package top.alazeprt.ndpp.util;

import java.net.InetAddress;

public abstract class NOnlinePlayer extends NOfflinePlayer {
    private final InetAddress address;

    public NOnlinePlayer(InetAddress address, String name) {
        super(name);
        this.address = address;
    }

    public InetAddress getAddress() {
        return address;
    }

    public abstract void kick(String reason);

    public abstract void sendMessage(String message);

    public abstract boolean hasPermission(String permission);
}
