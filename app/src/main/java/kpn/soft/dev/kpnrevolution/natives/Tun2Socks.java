package kpn.soft.dev.kpnrevolution.natives;
import mph.trunksku.apps.myssh.logger.*;
import java.net.*;
import mph.trunksku.apps.myssh.fragment.*;
import java.util.*;

public class Tun2Socks {
    static {
        System.loadLibrary("tun2socks");
    }

    public static void Start(int i, int i2, String str, String str2, String str3, String str4, String str5, boolean z) {
        if (i != -1) {
            runTun2Socks(i, i2, str, str2, str3, str4, str5, z ? 1 : 0);
        }
    }

    public static void Stop() {
        terminateTun2Socks();
    }

    public static void logTun2Socks(String str, String str2, String str3) {
        if (str2.equalsIgnoreCase("tun2socks")) {
            //Log.d("", str2 + ": " + str3);
        }
    }

    private static native int runTun2Socks(int i, int i2, String str, String str2, String str3, String str4, String str5, int i3);

    private static native void terminateTun2Socks();
}



