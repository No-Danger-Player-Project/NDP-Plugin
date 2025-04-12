package top.alazeprt.ndpp.util;

public abstract class NCommandSource {
    public abstract void sendMessage(String message);
    public abstract boolean hasPermission(String permission);
    public abstract String getName();
}
