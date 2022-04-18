package org.neogeo.util;

public class DetectOS {

    private static final String OS = System.getProperty("os.name").toLowerCase();
    private static final boolean IS_WINDOWS = (OS.contains("win"));
    private static final boolean IS_MAC = (OS.contains("mac"));
    private static final boolean IS_UNIX = (OS.contains("nix") || OS.contains("nux") || OS.contains("aix"));
    private static final boolean IS_SOLARIS = (OS.contains("sunos"));

    public static String detect() {
        String ret;
        if (IS_WINDOWS) {
            ret = "IS_WINDOWS";
        } else if (IS_MAC) {
            ret = "IS_MAC";
        } else if (IS_UNIX) {
            ret = "IS_UNIX";
        } else if (IS_SOLARIS) {
            ret = "IS_SOLARIS";
        } else {
            ret = "UNDETECTED";
        }
        return ret;
    }

    public static void main(String[] args) {
        System.out.println(detect());
    }
}