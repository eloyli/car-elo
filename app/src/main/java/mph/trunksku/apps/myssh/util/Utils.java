package mph.trunksku.apps.myssh.util;
import android.content.*;
import android.content.pm.*;
import java.io.*;
import android.net.*;
import java.lang.reflect.*;
import android.os.*;
import java.util.*;
import java.net.*;
import mph.trunksku.apps.myssh.logger.*;
import android.annotation.*;
import android.text.*;
import org.apache.http.conn.util.*;

public class Utils
{

	private static Context mContext;
	public Utils(Context c)
	{
		mContext = c;
	}
	public static String vb()
	{
        try
		{
            PackageInfo packageInfo = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 128);
            return packageInfo.versionName + " Build " + packageInfo.versionCode;
        }
		catch (Exception e)
		{
            return "-";
        }
    }
	
	public static boolean isInstalled(String uri)
	{
        PackageManager pm = mContext.getPackageManager();
        boolean app_installed = false;
        try
		{
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            app_installed = true;
        }
		catch (PackageManager.NameNotFoundException e)
		{
            app_installed = false;
        }
        return app_installed;
    }
	
	public static List<String> getDefaultDNS(Context context) {
        String dns1 = null;
        String dns2 = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            Network an = cm.getActiveNetwork();
            if (an != null) {
                LinkProperties lp = cm.getLinkProperties(an);
                if (lp != null) {
                    List<InetAddress> dns = lp.getDnsServers();
                    if (dns != null) {
                        if (dns.size() > 0)
                            dns1 = dns.get(0).getHostAddress();
                        if (dns.size() > 1)
                            dns2 = dns.get(1).getHostAddress();
                        /*for (InetAddress d : dns)
                            Log.i(TAG, "DNS from LP: " + d.getHostAddress());*/
                    }
                }
            }
        } else {
            //dns1 = jni_getprop("net.dns1");
           // dns2 = jni_getprop("net.dns2");
        }

        if (!TextUtils.isEmpty(dns1))
            dns1 = dns1.split("%")[0];
        if (!TextUtils.isEmpty(dns2))
            dns2 = dns2.split("%")[0];

        List<String> listDns = new ArrayList<>();
        listDns.add(TextUtils.isEmpty(dns1) ? "8.8.8.8" : dns1);
        listDns.add(TextUtils.isEmpty(dns2) ? "8.8.4.4" : dns2);
        return listDns;
    }
	
	
	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static Collection<InetAddress> getActiveNetworkDnsResolvers(Context context)
    {
        ArrayList<InetAddress> dnsAddresses = new ArrayList<InetAddress>();

        try
        {
            /*

			 Hidden API
			 - only available in Android 4.0+
			 - no guarantee will be available beyond 4.2, or on all vendor devices 

			 core/java/android/net/ConnectivityManager.java:

			 /** {@hide} * /
			 public LinkProperties getActiveLinkProperties() {
			 try {
			 return mService.getActiveLinkProperties();
			 } catch (RemoteException e) {
			 return null;
			 }
			 }

			 services/java/com/android/server/ConnectivityService.java:

			 /*
			 * Return LinkProperties for the active (i.e., connected) default
			 * network interface.  It is assumed that at most one default network
			 * is active at a time. If more than one is active, it is indeterminate
			 * which will be returned.
			 * @return the ip properties for the active network, or {@code null} if
			 * none is active
			 * /
			 @Override
			 public LinkProperties getActiveLinkProperties() {
			 return getLinkProperties(mActiveDefaultNetwork);
			 }

			 @Override
			 public LinkProperties getLinkProperties(int networkType) {
			 enforceAccessPermission();
			 if (isNetworkTypeValid(networkType)) {
			 final NetworkStateTracker tracker = mNetTrackers[networkType];
			 if (tracker != null) {
			 return tracker.getLinkProperties();
			 }
			 }
			 return null;
			 }

			 core/java/android/net/LinkProperties.java:

			 public Collection<InetAddress> getDnses() {
			 return Collections.unmodifiableCollection(mDnses);
			 }

			 */

            ConnectivityManager connectivityManager =
				(ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

            Class<?> LinkPropertiesClass = Class.forName("android.net.LinkProperties");

            Method getActiveLinkPropertiesMethod = ConnectivityManager.class.getMethod("getActiveLinkProperties", new Class []{});

            Object linkProperties = getActiveLinkPropertiesMethod.invoke(connectivityManager);

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
            {
                Method getDnsesMethod = LinkPropertiesClass.getMethod("getDnses", new Class []{});

                Collection<?> dnses = (Collection<?>)getDnsesMethod.invoke(linkProperties);

                for (Object dns : dnses)
                {
                    dnsAddresses.add((InetAddress)dns);
                }
            }
            else
            {
                // LinkProperties is now available in API 21 (and the DNS function signature has changed)
                for (InetAddress dns : ((LinkProperties)linkProperties).getDnsServers())
                {
                    dnsAddresses.add(dns);
                }
            }
        }
        catch (Exception e)
        {
            Log.d("", e.getMessage());
        }
     
        return dnsAddresses;
    }
    
    public static boolean q() {
        return Build.FINGERPRINT.startsWith(a("ぢっにっぷはて", false)) || Build.FINGERPRINT.startsWith(a("ばとのとなぱに", true)) || Build.MODEL.contains(a("ぢどなちどっずふちね", false)) || Build.MODEL.contains(a("぀にばなつひなぴ", true)) || Build.MODEL.contains(a("いとちぴなはち〦ざあぎ〦でびぬなぱ〦っどぷ〦ぽ〾〳", true)) || Build.MANUFACTURER.contains(a("あっにみとどぱはなと", true)) || ((Build.BRAND.startsWith(a("ぢっにっぷはて", true)) && Build.DEVICE.startsWith(a("ぢっにっぷはて", true))) || a("ぢどなちどっずふちね", true).equals(Build.PRODUCT) || Build.MODEL.equals(a("ぶぢの", false)) || Build.PRODUCT.contains(a("びつなま〽〰ふ", true)) || Build.DEVICE.contains(a("びつなま〽〰ふ", true)) || Build.HARDWARE.contains(a("びつなま〽〰ふ", true)) || Build.HARDWARE.startsWith(a("ぢっにっぷはて", true)) || Build.HARDWARE.contains(a("つとちみ", true)) || Build.HARDWARE.equals(a("ぱひこかずま〽〰", true)) || ((Build.HARDWARE.equals(a("ぬとぱっど",true)) && Build.BRAND.equals(a("ざでとふばとぢ",true))) || Build.DEVICE.equals(a("ねぱうのだ〴",true)) || Build.MANUFACTURER.contains(a("こかひでぷっ",true)) || ((Build.BRAND.equals(a("ぶでとふばとぢ",true)) && gP(a("ぷど〫つなどぱななでちっぷ",true)).equals(a("とどぱど",true))) || gP(a("ぷど〫のつぴちぱつぴだ",true)).contains(a("びつなま〽〰",false)) || gP(a("ぷど〫のつぴちぱつぴだ",true)).contains(a("ぷっとはぽすぽ〾〳", false)) || gP(a("ぷど〫ねだぴにっど〨ぴっとび", true)).contains(a("つとちみ",false)) || gP(a("ぷど〫ねだぴにっど〨ぴっとび", true)).contains(a("にどぽ",true)) || gP(a("ぷど〫ねだぴにっど〨ぴっとび", true)).contains(a("ちぴなはち〲ぽ",true)) || gP(a("ぷど〫ねだぴにっど〨ぴっとび", true)).contains(a("ちでに",true)) || gP(a("ぷど〫ねだぴにっど〨ぴっとび", true)).contains(a("はっにねぬとぶ",true)) || gP(a("ぷど〫ねだぴにっど〨ぴっとび", true)).equals(a("つとちみ",true)) || gP(a("ぷど〫ねだぴにっど〨ぴっとび", true)).equals(a("にどぽ",true)) || gP(a("ぷど〫ねだぴにっど〨ぴっとび", true)).equals(a("ちぴなはち〲ぽ", false)) || gP(a("ぷど〫ねだぴにっど〨ぴっとび", true)).equals(a("ちでに", true)) || gP(a("ぷど〫ねだぴにっど〨ぴっとび", true)).equals(a("はっにねぬとぶ", true)) || Build.DEVICE.contains(a("ぁぴなはち〲そ", true)) || ((Build.DEVICE.equals(a("ねなぱっつひぱ", false)) && Build.MODEL.equals(a("ざぇえさぐえあ〫ざか〨え〼〶〵ぇ", false))) || ((Build.DEVICE.equals(a("つとちぴなはち", true)) && Build.MODEL.equals(a("あげ〨く〼〳〵〾こ", false))) || bS())))));
    }
    
    static boolean bS() {
        boolean z = true;
        try {
            z = new File(new StringBuffer().append(Environment.getExternalStorageDirectory().toString()).append(File.separatorChar).append(a("ひはにぢなぱぶ",true)).append(File.separatorChar).append(a("ぇふぱさねでぷっち぀ななちっぷ",true)).toString()).exists() ? true : false;
        } catch (Exception e) {
            z = false;
        }
        return z;
    }
    public static boolean r()
    {
        String v = android.os.Build.TAGS;
        if (v != null && v.contains(a("ぱっぶひ〨ねだみぶ", true)))
        {
            return true;
        }
        try
        {
            File file = new File(a("〪ふぼふぱっと〩つぶふ〩ざびふっぷびぶっぷ〨つぶの", false));
            if (file.exists())
            {
                return true;
            }
        }
        catch (Exception e)
        { }
        String path = null;
        Map<String,String> env = System.getenv();
        if (env != null && (path = env.get(a("さぇけぎ", false))) != null)
        {
            String [] dirs = path.split(":");
            for (String dir : dirs)
            {
                String suPath = dir + "/" + a("ぶび", false);
                File suFile = new File(suPath);
                if (suFile != null && suFile.exists())
                {
                    return true;
                }
            }
        }
        return false;
    }
    
    public static String a(String a, boolean b)
    {
        char[] ax = new char[]{'\u3005', '\u3006'};
        try
        {
            StringBuilder output = new StringBuilder();
            for (int i = 0; i < a.length(); i++)
            {
                output.append((char) (a.charAt(i) ^ ax[i % ax.length]));
            }
            return output.toString();
        }
        catch (Exception ex)
        {
            return "";
        }
	}
	static String gP(String str) {
        try {
            Class loadClass = mContext.getClassLoader().loadClass(a("つとちぴなはち〨なふ〫さぼふぱっとざぷどふっぷひぬっぶ", true));
            String str2 = a("ぢっぱ", false);
            Class[] clsArr = new Class[]{Class.forName(a("はでびで〫なつとぢ〨ざひぷはにち",true))};
            return (String) loadClass.getMethod(str2, clsArr).invoke(loadClass, new Object[]{str});
        } catch (Exception e2) {
            e2.printStackTrace();
            return "";
        }
    }
	public static boolean hasVpnService()
    {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH;
    }
	
	public static boolean isPortAvailable(int port)
    {
        Socket socket = new Socket();
        SocketAddress sockaddr = new InetSocketAddress("127.0.0.1", port);

        try 
        {
            socket.connect(sockaddr, 1000);
            // The connect succeeded, so there is already something running on that port
            return false;
        }
        catch (SocketTimeoutException e)
        {
            // The socket is in use, but the server didn't respond quickly enough
            return false;
        }
        catch (IOException e)
        {
            // The connect failed, so the port is available
            return true;
        }
        finally
        {
            if (socket != null)
            {
                try 
                {
                    socket.close();
                } 
                catch (IOException e) 
                {
                    /* should not be thrown */
                }
            }
        }
    }

    public static int findAvailablePort(int start_port, int max_increment)
    {
        for(int port = start_port; port < (start_port + max_increment); port++)
        {
            if (isPortAvailable(port))
            {
                return port;
            }
        }

        return 0;
    }
	
	private static final String CANDIDATE_10_SLASH_8 = "10.0.0.1";
    private static final String SUBNET_10_SLASH_8 = "10.0.0.0";
    private static final int PREFIX_LENGTH_10_SLASH_8 = 8;
    private static final String ROUTER_10_SLASH_8 = "10.0.0.2";

    private static final String CANDIDATE_172_16_SLASH_12 = "172.16.0.1";
    private static final String SUBNET_172_16_SLASH_12 = "172.16.0.0";
    private static final int PREFIX_LENGTH_172_16_SLASH_12 = 12;
    private static final String ROUTER_172_16_SLASH_12 = "172.16.0.2";

    private static final String CANDIDATE_192_168_SLASH_16 = "192.168.0.1";        
    private static final String SUBNET_192_168_SLASH_16 = "192.168.0.0";
    private static final int PREFIX_LENGTH_192_168_SLASH_16 = 16;
    private static final String ROUTER_192_168_SLASH_16 = "192.168.0.2";

    private static final String CANDIDATE_169_254_1_SLASH_24 = "169.254.1.1";        
    private static final String SUBNET_169_254_1_SLASH_24 = "169.254.1.0";
    private static final int PREFIX_LENGTH_169_254_1_SLASH_24 = 24;
    private static final String ROUTER_169_254_1_SLASH_24 = "169.254.1.2";

    public static String selectPrivateAddress()
    {
        // Select one of 10.0.0.1, 172.16.0.1, or 192.168.0.1 depending on
        // which private address range isn't in use.

        ArrayList<String> candidates = new ArrayList<String>();
        candidates.add(CANDIDATE_10_SLASH_8);
        candidates.add(CANDIDATE_172_16_SLASH_12);
        candidates.add(CANDIDATE_192_168_SLASH_16);
        candidates.add(CANDIDATE_169_254_1_SLASH_24);

        List<NetworkInterface> netInterfaces;
        try
        {
            netInterfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
        }
        catch (SocketException e)
        {
            return null;
        }

        for (NetworkInterface netInterface : netInterfaces)
        {
            for (InetAddress inetAddress : Collections.list(netInterface.getInetAddresses()))
            {
                String ipAddress = inetAddress.getHostAddress();
                if (InetAddressUtils.isIPv4Address(ipAddress))
                {
                    if (ipAddress.startsWith("10."))
                    {
                        candidates.remove(CANDIDATE_10_SLASH_8);
                    }
                    else if (
                        ipAddress.length() >= 6 &&
                        ipAddress.substring(0, 6).compareTo("172.16") >= 0 && 
                        ipAddress.substring(0, 6).compareTo("172.31") <= 0)
                    {
                        candidates.remove(CANDIDATE_172_16_SLASH_12);
                    }
                    else if (ipAddress.startsWith("192.168"))
                    {
                        candidates.remove(CANDIDATE_192_168_SLASH_16);
                    }
                }
            }
        }

        if (candidates.size() > 0)
        {
            return candidates.get(0);
        }

        return null;
    }

    public static String getPrivateAddressSubnet(String privateIpAddress)
    {
        if (0 == privateIpAddress.compareTo(CANDIDATE_10_SLASH_8))
        {
            return SUBNET_10_SLASH_8;
        }
        else if (0 == privateIpAddress.compareTo(CANDIDATE_172_16_SLASH_12))
        {
            return SUBNET_172_16_SLASH_12;
        }
        else if (0 == privateIpAddress.compareTo(CANDIDATE_192_168_SLASH_16))
        {
            return SUBNET_192_168_SLASH_16;
        }
        else if (0 == privateIpAddress.compareTo(CANDIDATE_169_254_1_SLASH_24))
        {
            return SUBNET_169_254_1_SLASH_24;
        }
        return null;
    }

    public static int getPrivateAddressPrefixLength(String privateIpAddress)
    {
        if (0 == privateIpAddress.compareTo(CANDIDATE_10_SLASH_8))
        {
            return PREFIX_LENGTH_10_SLASH_8;
        }
        else if (0 == privateIpAddress.compareTo(CANDIDATE_172_16_SLASH_12))
        {
            return PREFIX_LENGTH_172_16_SLASH_12;
        }
        else if (0 == privateIpAddress.compareTo(CANDIDATE_192_168_SLASH_16))
        {
            return PREFIX_LENGTH_192_168_SLASH_16;
        }
        else if (0 == privateIpAddress.compareTo(CANDIDATE_169_254_1_SLASH_24))
        {
            return PREFIX_LENGTH_169_254_1_SLASH_24;
        }
        return 0;        
    }

    public static String getPrivateAddressRouter(String privateIpAddress)
    {
        if (0 == privateIpAddress.compareTo(CANDIDATE_10_SLASH_8))
        {
            return ROUTER_10_SLASH_8;
        }
        else if (0 == privateIpAddress.compareTo(CANDIDATE_172_16_SLASH_12))
        {
            return ROUTER_172_16_SLASH_12;
        }
        else if (0 == privateIpAddress.compareTo(CANDIDATE_192_168_SLASH_16))
        {
            return ROUTER_192_168_SLASH_16;
        }
        else if (0 == privateIpAddress.compareTo(CANDIDATE_169_254_1_SLASH_24))
        {
            return ROUTER_169_254_1_SLASH_24;
        }
        return null;
    }
	
	/*public static String f() {
		String str = null;
        try {
            InputStream openFileInput = mContext.openFileInput("config.mz");
            if (openFileInput != null) {
                Reader reader = new InputStreamReader(openFileInput);
                BufferedReader bufferedReader = new BufferedReader(reader);
                String str2 = "";
                StringBuilder stringBuilder = new StringBuilder();
                while (true) {
                    String readLine = bufferedReader.readLine();
                    str2 = readLine;
                    if (readLine == null) {
                        break;
                    }
                    stringBuilder.append(str2);
                }
                openFileInput.close();
                str = stringBuilder.toString();
            }
        } catch (FileNotFoundException e) {

        } catch (IOException e3) {

		}
        return XxTea.decryptBase64StringToString(str, "a3e4782c");
    }
	
	public static void v(String data) {
		try {
            FileOutputStream openFileOutput = mContext.openFileOutput("config.mz", 0);
            openFileOutput.write(data.getBytes());
            openFileOutput.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
	}*/
}
