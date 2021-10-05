package mph.trunksku.apps.myssh.service;

import android.content.Intent;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.Base64;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import mph.trunksku.apps.myssh.core.*;
import mph.trunksku.apps.myssh.*;
import mph.trunksku.apps.myssh.model.*;
import mph.trunksku.apps.myssh.fragment.*;

public final class ProxyThread extends Thread implements Callback {
    private final String TAG = "ProxyServer";
    private Socket cfQ;
    private int coA = 0;
    private int coB = 0;
    private int coC = 0;
    private int coD = 0;
    //private a coE = null;
    private VpnTunnelService cor;
    private ServerSocket cot;
    private Socket cou;
    private Socket cov;
    private String cow;
    private String[] cox;
    private String[] coy;
    private String[] coz;
    public boolean jb = true;
    private Handler mHandler;

	private Config utils;
	
	public ProxyThread() {
		utils = ApplicationBase.getUtils();
	}

    public Socket FS() {
        String trim;
        int i = 0;
        String payload = utils.getPayload();
		String remote = utils.getHost() + ":" + utils.getPort();
		String[] split = remote.trim().split(":");
		int i2 = 80;
        if (split.length > 1) {
            trim = split[0].trim();
            try {
                i2 = Integer.parseInt(split[1].trim());
            } catch (NumberFormatException e) {
                i2 = 80;
            }
        } else {
            trim = split[0].trim();
        }
        try {
            String[] split2;
            CharSequence charSequence;
            CharSequence f;

            cfQ = new Socket(trim, i2);
            OutputStream outputStream = cfQ.getOutputStream();
            String readLine = new BufferedReader(new InputStreamReader(cou.getInputStream())).readLine();
            String[] split3 = readLine.split(" ");
            CharSequence charSequence2 = split3[1];
            if (split3[0].equals("CONNECT")) {
                split2 = split3[1].split(":");
                charSequence2 = split2[0];
                charSequence = split2.length < 2 ? "443" : split2[1];
            } else {
                charSequence = "80";
            }
            String cR = payload;
            if (cR.contains("[random]")) {
                Random random = new Random();
                split = cR.split(Pattern.quote("[random]"));
                cR = split[random.nextInt(split.length)];
            }
            this.cow = cR;
            cR = payload;
            if (cR.contains("[repeat]")) {
                String[] split4 = cR.split(Pattern.quote("[repeat]"));
                cR = split4[this.coD];
                if (this.coD + 1 > split4.length) {
                    this.coD = 0;
                }
            }
            this.cow = cR;
            this.cow = payload.replace("realData", "netData");
            int indexOf = this.cow.indexOf("netData");
            if (indexOf < 0) {
                trim = this.cow.replace("[METHOD]", split3[0]).replace("[method]", split3[0]).replace("[SSH]", split3[1]).replace("[IP_PORT]", split3[1]).replace("[ip_port]", split3[1]).replace("[IP]", charSequence2).replace("[ip]", charSequence2).replace("[PORT]", charSequence).replace("[cr]", "\r").replace("[lf]", "\n").replace("[crlf]", "\r\n").replace("[lfcr] ", "\n\r").replace("[protocol]", split3[2]).replace("[host]", charSequence2).replace("[port]", charSequence).replace("[host_port]", split3[1]).replace("[ssh]", split3[1]).replace("[ua]", utils.getUa()).replace("[raw]", readLine + "\r\n\r\n").replace("[real_raw]", readLine + "\r\n\r\n").replace("[auth]", auth()).replace("\\r", "\r").replace("\\n", "\n");
            } else if (this.cow.substring(indexOf + 7, (indexOf + 7) + 1).equals("@")) {
                Matcher matcher = Pattern.compile("\\[.*?@(.*?)\\]").matcher(this.cow);
                cR = "";
                if (matcher.find()) {
                    cR = matcher.group(1);
                }
                trim = this.cow.replace("[netData@" + cR.trim() + "]", split3[0] + " " + split3[1] + "@" + cR.trim() + " " + split3[2]).replace("[METHOD]", split3[0]).replace("[method]", split3[0]).replace("[SSH]", split3[1]).replace("[IP_PORT]", split3[1]).replace("[ip_port]", split3[1]).replace("[IP]", charSequence2).replace("[ip]", charSequence2).replace("[PORT]", charSequence).replace("[cr]", "\r").replace("[lf]", "\n").replace("[crlf]", "\r\n").replace("[lfcr] ", "\n\r").replace("[protocol]", split3[2]).replace("[host]", charSequence2).replace("[port]", charSequence).replace("[host_port]", split3[1]).replace("[ssh]", split3[1]).replace("[ua]", utils.getUa()).replace("[raw]", readLine + "\r\n\r\n").replace("[real_raw]", readLine + "\r\n\r\n").replace("[auth]", auth()).replace("\\r", "\r").replace("\\n", "\n");
            } else {
                int i3 = indexOf == 0 ? 1 : indexOf;
                Matcher matcher2 = Pattern.compile("\\[(.*?)@.*?\\]").matcher(this.cow);
                cR = "";
                if (matcher2.find()) {
                    cR = matcher2.group(1);
                }
                trim = this.cow.substring(i3 + -1, i3).equals("@") ? this.cow.replace("[" + cR.trim() + "@netData]", split3[0] + " " + cR.trim() + "@" + split3[1] + " " + split3[2]).replace("[METHOD]", split3[0]).replace("[method]", split3[0]).replace("[SSH]", split3[1]).replace("[IP_PORT]", split3[1]).replace("[ip_port]", split3[1]).replace("[IP]", charSequence2).replace("[ip]", charSequence2).replace("[PORT]", charSequence).replace("[cr]", "\r").replace("[lf]", "\n").replace("[crlf]", "\r\n").replace("[lfcr] ", "\n\r").replace("[protocol]", split3[2]).replace("[host]", charSequence2).replace("[port]", charSequence).replace("[host_port]", split3[1]).replace("[ssh]", split3[1]).replace("[ua]", utils.getUa()).replace("[raw]", readLine + "\r\n\r\n").replace("[real_raw]", readLine + "\r\n\r\n").replace("[auth]", auth()).replace("\\r", "\r").replace("\\n", "\n") : this.cow.replace("[netData]", readLine).replace("[METHOD]", split3[0]).replace("[method]", split3[0]).replace("[SSH]", split3[1]).replace("[IP_PORT]", split3[1]).replace("[ip_port]", split3[1]).replace("[IP]", charSequence2).replace("[ip]", charSequence2).replace("[PORT]", charSequence).replace("[cr]", "\r").replace("[lf]", "\n").replace("[crlf]", "\r\n").replace("[lfcr] ", "\n\r").replace("[protocol]", split3[2]).replace("[host]", charSequence2).replace("[port]", charSequence).replace("[host_port]", split3[1]).replace("[ssh]", split3[1]).replace("[ua]", utils.getUa()).replace("[raw]", readLine + "\r\n\r\n").replace("[real_raw]", readLine + "\r\n\r\n").replace("[auth]", auth()).replace("\\r", "\r").replace("\\n", "\n");
            }
            Matcher matcher3 = Pattern.compile(".*?\\[rotation_method=(.*?)\\].*?").matcher(trim);
            while (matcher3.find()) {
                cR = matcher3.group(1);
                this.cox = cR.split(";");
                if (this.coA + 1 > this.cox.length) {
                    this.coA = 0;
                }
                trim = trim.replace("[rotation_method=" + cR + "]", this.cox[this.coA]);
            }
            matcher3 = Pattern.compile(".*?\\[rotation=(.*?)\\].*?").matcher(trim);
            while (matcher3.find()) {
                cR = matcher3.group(1);
                this.coy = cR.split(";");
                if (this.coB + 1 > this.coy.length) {
                    this.coB = 0;
                }
                trim = trim.replace("[rotation=" + cR + "]", this.coy[this.coB]);
            }
            matcher3 = Pattern.compile(".*?\\[rotate=(.*?)\\].*?").matcher(trim);
            while (matcher3.find()) {
                cR = matcher3.group(1);
                this.coz = cR.split(";");
                if (this.coC + 1 > this.coz.length) {
                    this.coC = 0;
                }
                trim = trim.replace("[rotate=" + cR + "]", this.coz[this.coC]);
            }
            trim = d(trim);
            /*if (App.Fr().db()) {
			 if (App.Fr().a(App.Fq(), Tun2SocksService.class)) {
			 Intent intent = new Intent(App.Fq(), Tun2SocksService.class);
			 intent.setAction("stop");
			 App.Fq().startService(intent);
			 }
			 if (App.Fr().a(App.Fq(), SSHTunnelService.class)) {
			 App.Fq().stopService(new Intent(App.Fq(), SSHTunnelService.class));
			 }
			 FR();
			 App.getSharedPreferences().edit().putBoolean("ssh_service", false).commit();
			 App.getSharedPreferences().edit().putBoolean("vpn_service", false).commit();
			 return null;
			 }
			 if ((App.getSharedPreferences().getBoolean("mainFragment", true) ? App.getSharedPreferences().getInt("spinSsh", 0) : App.getSharedPreferences().getInt("spinVpn", 0)) == 0) {
			 boolean z;
			 trim = "";
			 Matcher matcher4 = Pattern.compile(".*?CONNECT (" + "\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}" + ").*?").matcher(f2);
			 while (matcher4.find()) {
			 z = false;
			 for (indexOf = 0; indexOf < App.Fr().dd(); indexOf++) {
			 if (matcher4.group(1).trim().equals(InetAddress.getByName(new String(App.Fr().aA(indexOf)).trim()).getHostAddress())) {
			 new StringBuilder("ip = ").append(matcher4.group(1));
			 z = true;
			 }
			 }
			 trim = trim + " " + z;
			 if (!z) {
			 break;
			 }
			 }
			 matcher4 = Pattern.compile(".*?CONNECT (" + "([A-Za-z0-9-]{1,63}\\.)+[A-Za-z]{2,6}" + ").*?").matcher(f2);
			 while (matcher4.find()) {
			 z = false;
			 for (indexOf = 0; indexOf < App.Fr().dd(); indexOf++) {
			 if (matcher4.group(1).trim().equals(new String(App.Fr().aA(indexOf)).trim())) {
			 new StringBuilder("domain = ").append(matcher4.group(1));
			 z = true;
			 }
			 }
			 trim = trim + " " + z;
			 if (!z) {
			 break;
			 }
			 }
			 if (trim.contains("false") || trim.isEmpty()) {
			 a.a(4, "ProxyServer", "[" + new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH).format(new Date()) + "] you're not allowed inject your server from payload, contact us if you want join with us!!!", null);
			 this.mHandler.sendEmptyMessage(1);
			 FR();
			 App.getSharedPreferences().edit().putBoolean("ssh_service", false).commit();
			 App.getSharedPreferences().edit().putBoolean("vpn_service", false).commit();
			 return null;
			 }
			 }
			 try {
			 trim = App.getSharedPreferences().getString(new String(App.Fr().u(5, 0)), "");
			 if (!trim.trim().isEmpty()) {
			 trim = a(new File(trim));
			 if (trim.isEmpty()) {
			 throw new Exception("File empty");
			 }
			 App.getSharedPreferences().edit().putBoolean(new String(App.Fr().u(5, 3)), Boolean.valueOf(trim.split("\\[splitConfig\\]")[2]).booleanValue()).commit();
			 }
			 } catch (Exception e2) {
			 e2.printStackTrace();
			 App.getSharedPreferences().edit().putString(new String(App.Fr().u(5, 1)), "").commit();
			 App.getSharedPreferences().edit().putString(new String(App.Fr().u(5, 2)), "").commit();
			 App.getSharedPreferences().edit().putBoolean(new String(App.Fr().u(5, 3)), false).commit();
			 }*/
            String[] split5;
            if (trim.contains("[split]")) {
                split5 = trim.split("\\[split\\]");
                while (i < split5.length) {

                    outputStream.write(split5[i].getBytes());
                    outputStream.flush();
                    i++;
                }
            } else if (trim.contains("[splitNoDelay]")) {
                split5 = trim.split("\\[splitNoDelay\\]");
                while (i < split5.length) {

                    outputStream.write(split5[i].getBytes());
                    outputStream.flush();
                    i++;
                }
            } else if (trim.contains("[instant_split]")) {
                split5 = trim.split("\\[instant_split\\]");
                while (i < split5.length) {

                    outputStream.write(split5[i].getBytes());
                    outputStream.flush();
                    i++;
                }
            } else if (trim.contains("[delay]")) {
                split5 = trim.split("\\[delay\\]");
                while (i < split5.length) {

                    outputStream.write(split5[i].getBytes());
                    outputStream.flush();
                    if (i != split5.length - 1) {
                        Thread.sleep(1000);
                    }
                    i++;
                }
            } else if (trim.contains("[delay_split]")) {
                split5 = trim.split("\\[delay_split\\]");
                while (i < split5.length) {

                    outputStream.write(split5[i].getBytes());
                    outputStream.flush();
                    if (i != split5.length - 1) {
                        Thread.sleep(1000);
                    }
                    i++;
                }
            } else if (trim.contains("[split_delay]")) {
                split2 = trim.split("\\[split_delay\\]");
                for (int i4 = 0; i4 < split2.length; i4++) {

                    outputStream.write(split2[i4].getBytes());
                    outputStream.flush();
                    if (i4 != split2.length - 1) {
                        Thread.sleep(1000);
                    }
                }
            } else {

                outputStream.write(trim.getBytes());
                outputStream.flush();
            }
			addLog("Sending Payload");
            //a.a(4, "ProxyServer", "[" + new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH).format(new Date()) + "] sending payload", null);
            this.coB++;
            this.coA++;
            this.coC++;
            this.coD++;
            return cfQ;
        } catch (Exception e22) {
			addLog("Thread Ko "+e22.getMessage());
			//a.a("ProxyServer", "[" + new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH).format(new Date()) + "] <font color=#FF0000>" + e22.getMessage(), null);
            return null;
        }
    }

    private String d(String str) {
        String str2 = str;
        String str3 = str2;
        if (str2.contains("[cr*")) {
            str3 = a(str2, "[cr*", "\r");
        }
        String str4 = str3;
        if (str3.contains("[lf*")) {
            str4 = a(str3, "[lf*", "\n");
        }
        str2 = str4;
        if (str4.contains("[crlf*")) {
            str2 = a(str4, "[crlf*", "\r\n");
        }
        String str5 = str2;
        if (str2.contains("[lfcr*")) {
            str5 = a(str2, "[lfcr*", "\n\r");
        }
        return str5;
    }
	
	private String a(String str, String str2, String str3) {
        while (str.contains(str2)) {
            Matcher matcher = Pattern.compile("\\[.*?\\*(.*?[0-9])\\]").matcher(str);
            if (matcher.find()) {
                int intValue = Integer.valueOf(matcher.group(1)).intValue();
                String str7 = "";
                for (int i = 0; i < intValue; i++) {
                    str7 = new StringBuffer().append(str7).append(str3).toString();
                }
                String str8 = str;
                str = str8.replace(new StringBuffer().append(str2).append(String.valueOf(intValue)).append("]").toString(), str7);
            }
        }
        return str;
    }
	
	private String auth() {
        String str = "";
        try {
			/* if (!http.getUser().isEmpty() && !http.getPass().isEmpty()) {
			 byte[] encode = Base64.encode(new StringBuffer().append(http.getUser()).append(":").append(http.getPass()).toString().getBytes("ISO-8859-1"), Base64.DEFAULT);
			 str = new StringBuffer().append("Proxy-Authorization: Basic ").append(new String(encode)).toString();
			 }*/
        } catch (Exception e) {
            //addLog(e.getMessage());
        }
        return str;
    }

	public String ua() {
        String property = System.getProperty("http.agent");
        return property == null ? "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/44.0.2403.130 Safari/537.36" : property;
    }
    

    public final void FQ() {
        this.jb = true;
        this.mHandler = new Handler(this);
        /*if ((App.getSharedPreferences().getBoolean("autoOnOff", false) || App.getSharedPreferences().getBoolean("autoPing", false)) && this.coE == null) {
            this.coE = new a();
            a aVar = this.coE;
            aVar.jb = true;
            aVar.start();
        }*/
        try {
            if (this.cot == null) {
                this.cot = new ServerSocket(8083);
            }
            start();
        } catch (IOException e) {
            e.printStackTrace();
            FR();
        }
    }

    public final void FR() {
        this.jb = false;
        this.mHandler = null;
        /*if ((App.getSharedPreferences().getBoolean("autoOnOff", false) || App.getSharedPreferences().getBoolean("autoPing", false)) && this.coE != null) {
            a aVar = this.coE;
            aVar.jb = false;
            if (aVar.isAlive()) {
                aVar.interrupt();
            }
        }*/
        if (this.cot != null) {
            try {
                this.cot.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            if (this.cou != null) {
                this.cou.close();
            }
            if (this.cov != null) {
                this.cov.close();
            }
            if (isAlive()) {
                interrupt();
            }
        } catch (IOException e2) {
            e2.printStackTrace();
        }
    }

    public final boolean doVpnProtect(Socket socket) {
        if (this.cor == null) {
            this.cor = new VpnTunnelService();
        }
        if (this.cor.protect(socket)) {
            //a.a(4, "ProxyServer", "[" + new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH).format(new Date()) + "] " + App.Fq().getString(2131165800), null);
            return true;
        }
        //a.a(4, "ProxyServer", "[" + new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH).format(new Date()) + "] <font color=#FF0000>" + App.Fq().getString(2131165799), null);
        if (ApplicationBase.isMyServiceRunning(OreoService.class)) {
            ApplicationBase.getContext().stopService(new Intent(ApplicationBase.getContext(), OreoService.class));
        }
        return false;
    }

    public final boolean handleMessage(Message message) {
        switch (message.what) {
            case 1:
                //Toast.makeText(App.Fq(), "You're not allowed inject your server from payload, contact us if you want join with us!!!", 1).show();
                break;
        }
        return false;
    }

    public final void run() {
        while (this.jb) {
            try {
                this.cou = this.cot.accept();
                this.cov = FS();
                if (!(this.cou == null || this.cou.isClosed())) {
                    this.cou.setKeepAlive(true);
                    this.cou.setSoTimeout(0);
                }
                if (!(this.cov == null || this.cov.isClosed())) {
                    this.cov.setKeepAlive(true);
                    this.cov.setSoTimeout(0);
                }
                if (this.cov == null) {
                    addLog("failed connect to remote proxy");
                    this.cou.close();
                } else if (!this.cov.isConnected()) {
                    continue;
                } else if (doVpnProtect(this.cov)) {
                    addLog("connected to remote proxy");
                    Socket socket = this.cou;
                    Socket socket2 = this.cov;
                    try
					{
						HTTPThread2 cVar = new HTTPThread2(socket, socket2, true);
						HTTPThread2 cVar2 = new HTTPThread2(socket2, socket, false);
						cVar.start();
						cVar2.start();
					}
					catch (Exception e3)
					{
						e3.printStackTrace();
					}
                } else {
                    this.cou.close();
                    interrupt();
                    return;
                }
            } catch (IOException e32) {
                e32.printStackTrace();
            }
        }
        interrupt();
    }
	
	void addLog(String msg){
		LogFragment.addLog(msg);
	}
}

