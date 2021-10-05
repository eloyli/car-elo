package mph.trunksku.apps.myssh.service;
import android.annotation.*;
import android.content.*;
import android.content.pm.*;
import android.net.*;
import android.os.*;
import android.system.*;
import java.io.*;
import java.net.*;
import java.util.*;
import kpn.soft.dev.kpnrevolution.natives.*;
import com.masfha.id.*;
import mph.trunksku.apps.myssh.*;
import mph.trunksku.apps.myssh.core.*;
import mph.trunksku.apps.myssh.logger.*;
import mph.trunksku.apps.myssh.model.*;
import mph.trunksku.apps.myssh.util.*;

public class VpnTunnelService extends VpnService implements Runnable
{

	private String privateIpAddress;

	private java.lang.Process pdnsdProcess;
	public static boolean isRunning = false;
	public String FLAG_VPN_START = "START";
    public String FLAG_VPN_STOP = "STOP";
	private Thread mThread;
	public static final String LOCAL_SERVER_ADDRESS = "127.0.0.1";
	public static final String LOCAL_SERVER_PORT = "1080";
	private final IBinder binder = new LocalBinder();
	private String mRouter;
	private int mMtu = 1500;
	private Config config;
	private ParcelFileDescriptor tunFd;
	private Thread tun2socksThread = null;

	private NetworkSpace mRoutes;

	private SharedPreferences sp;

	private SharedPreferences dsp;

	static {
        System.loadLibrary("tun2socks");
    }

	public class LocalBinder extends Binder
	{
		public VpnTunnelService getService()
		{
			return VpnTunnelService.this;
		}
	}

	@Override
	public IBinder onBind(Intent intent)
	{
		String action = intent.getAction();
		if (action != null && action.equals(SERVICE_INTERFACE))
		{
			return super.onBind(intent);
		}
		return binder;
	}

	@Override
	public void onCreate()
	{
		// TODO: Implement this method
		super.onCreate();
		sp = ApplicationBase.getSharedPreferences();
		dsp = ApplicationBase.getDefSharedPreferences();
		mRoutes = new NetworkSpace();
		config = ApplicationBase.getUtils();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		// TODO: Implement this method
		String action = intent.getAction();
        if (action == FLAG_VPN_START)
		{
            if (mThread != null)
			{
                mThread.interrupt();
            }
            mThread = new Thread(this, "myVPNThread");
            mThread.start();
        }
        else if (action == FLAG_VPN_STOP)
		{
            onDisconnect();
			stopSelf();
			//addLog(getString(R.string.app_name) + " Disconnected");
        }

        return START_NOT_STICKY;
	}

	public void onRevoke()
	{
        super.onRevoke();
		if (OreoService.isRunning)
		{
            startService(new Intent(this, OreoService.class).setAction("STOP"));
        }
		onDisconnect();

        //stopSelf();
    }

	@Override
	public void run()
	{
		// TODO: Implement this method
		try
		{
			if (!establishVpn())
			{
				addLog("Failed to establish the VPN");
				return;
			}
			connectTunnel(getLocalServerAddress(dsp.getString("local_port", "1080")), getLocalServerAddress(dsp.getString("udp_port", "7300")), true);
		}
		catch (Exception e)
		{

		}
	}

	void onDisconnect()
	{
		if (mThread != null)
		{
			mThread.interrupt();
		}
		disconnectTunnel();
	}

	public synchronized boolean establishVpn()
	{
		addLog("Starting VPNService...");
		try
		{
			Locale.setDefault(Locale.ENGLISH);
		    privateIpAddress = Utils.selectPrivateAddress();

			String subnet = Utils.getPrivateAddressSubnet(privateIpAddress);
            int prefixLength = Utils.getPrivateAddressPrefixLength(privateIpAddress);
            mRouter = Utils.getPrivateAddressRouter(privateIpAddress);

			VpnService.Builder builder = new VpnService.Builder();
			builder.setSession(getApplicationName());
			String release = Build.VERSION.RELEASE;
			if ((Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT && !release.startsWith("4.4.3")
                && !release.startsWith("4.4.4") && !release.startsWith("4.4.5") && !release.startsWith("4.4.6"))
                && mMtu < 1280)
			{ 
				mMtu = 1280;
			}
			builder.setMtu(mMtu);
			//addLocalNetworksToRoutes(mIpAddress);
			builder.addAddress(privateIpAddress, prefixLength);
			mRoutes.addIP(new CIDRIP("0.0.0.0", 0), true);

			//mRoutes.addIP(new CIDRIP(InetAddress.getByName(config.getHost()).getHostAddress(), 32), false);
			mRoutes.addIP(new CIDRIP("10.0.0.0", 8), false);
			mRoutes.addIP(new CIDRIP("192.168.0.0", 16), false);
			mRoutes.addIP(new CIDRIP("172.16.0.0", 12), false);
			if (dsp.getBoolean("reqBlock", false))
			{
				mRoutes.addIP(new CIDRIP(InetAddress.getByName(config.getHost()).getHostAddress(), 32), false);
				//Tun2SocksService.this.bXv.a(new de.blinkt.openvpn.core.a(InetAddress.getByName(App.Fr().cS().trim().split("@")[0].split(":")[0]).getHostAddress(), 32), false);
			}
			else
			{
				mRoutes.addIP(new CIDRIP(InetAddress.getByName(config.getSSHHost()).getHostAddress(), 32), false);
				//Tun2SocksService.this.bXv.a(new de.blinkt.openvpn.core.a((App.getSharedPreferences().getInt("spinSsh", 0) == 0 ? InetAddress.getByName(new String(App.Fr().u(1, App.getSharedPreferences().getInt("spinSsh", 0)))) : InetAddress.getByName(App.getSharedPreferences().getString("ip_ssh", "127.0.0.1"))).getHostAddress(), 32), false);
			}

			boolean allowUnsetAF = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ;
			if (allowUnsetAF)
			{
				allowAllAFFamilies(builder);
				setAllowedVpnPackages(builder);
			}

			String dns_1 =  sp.getString("PrimaryDns", "8.8.8.8");
			String dns_2 = sp.getString("SecondaryDns", "8.8.4.4");

			builder.addDnsServer(dns_1);
			builder.addDnsServer(dns_2);

			String str4 = getFilesDir().getAbsolutePath() + "/";
			File file = new File(getCacheDir().getAbsolutePath(), "pdnsd.cache");
			if (file.exists())
			{
				file.delete();
			}
			file.createNewFile();
			if (file.setReadable(true) && file.setWritable(true))
			{
				String format = String.format(b(this, R.raw.pdnsd_conf), new Object[]{getCacheDir().getAbsolutePath(), "0.0.0.0", dns_1, dns_2});
				FileOutputStream openFileOutput = openFileOutput("a.conf", 0);
				//addLog(format);
				openFileOutput.write(format.getBytes());
				openFileOutput.flush();
				openFileOutput.close();
				pdnsdProcess = new ProcessBuilder(new String[0]).command(new String[]{str4 + "go", "-c", str4 + "a.conf"}).redirectErrorStream(false).start();
				pdnsdProcess.getOutputStream().close();
				new Thread(){
					public void run()
					{
						BufferedReader bfr;
						try
						{
							bfr = new BufferedReader((Reader)new InputStreamReader(pdnsdProcess.getErrorStream()));
							String var8_2 = null;
							while ((var8_2 = bfr.readLine()) != null)
							{
								//addLog("Pdnsd: " + var8_2);
							}
						}
						catch (Exception var4_3)
						{
							String var5_4 = var4_3.getLocalizedMessage();
							if (var5_4 == null)
							{
								//addLog("Pdnsd: " + var5_4);
								if (this.isAlive())
								{
									interrupted();
								}
							}
						}
					}
				}.start();
			}
			else
			{
				addLog("Can't set read-write pdnsd.cache, pdnsd aborted");
			}
			mRoutes.addIP(new CIDRIP(dns_1, 32), true);
			mRoutes.addIP(new CIDRIP(dns_2, 32), true);

			Collection<NetworkSpace.IpAddress> positiveIPv4Routes = mRoutes.getPositiveIPList();
			NetworkSpace.IpAddress multicastRange = new NetworkSpace.IpAddress(new CIDRIP("224.0.0.0", 3), true);
			for (NetworkSpace.IpAddress route : positiveIPv4Routes)
			{
				try
				{
					if (!multicastRange.containsNet(route))
					{
						builder.addRoute(route.getIPv4Address(), route.networkMask);
					}
				}
				catch (IllegalArgumentException ia)
				{
					addLog("Route rejected by Android" + route + " " + ia.getLocalizedMessage());
				}
			}
			//addLog("Routes: " + TextUtils.join(", ", mRoutes.getNetworks(true)));
			//addLog("Routes excluded: " + TextUtils.join(", ", mRoutes.getNetworks(false)));
			//addLog("Routes installed: " + TextUtils.join(", ", positiveIPv4Routes));
			mRoutes.clear();
			tunFd = builder.establish();

			OreoService.a(this);
			return tunFd != null;
		}
		catch (Exception e)
		{
			addLog("Failed to establish the VPN " + e);
		}
		return false;
	}

	/* Stops routing device traffic through the VPN. */


	public synchronized void connectTunnel(final String socksServerAddress, final String udpServerAddress, final boolean remoteUdpForwardingEnabled)
	{
		if (socksServerAddress == null)
		{
			throw new IllegalArgumentException("Must provide an IP address to a SOCKS server.");
		}
		if (tunFd == null)
		{
			throw new IllegalStateException("Must establish the VPN before connecting the tunnel.");
		}
		if (tun2socksThread != null)
		{
			throw new IllegalStateException("Tunnel already connected");
		}

		addLog("VPNService Connected");
		isRunning = true;
		//xBinary.runPdnsd(this, "8.8.8.8", "8.8.4.4");
		// Disable IPv6 due to apparent lack of Happy Eyeballs fallback in the Facebook apps, when
		// using Intra on IPv4-only networks.
		// TODO: Re-enable IPv6 once we solve this compatibility problem.  This might require swapping
		// the tunfd to enable/disable v6 when moving between v4only and non-v4only networks.

		tun2socksThread =
			new Thread() {
			public void run()
			{
				Tun2Socks.Start(tunFd.detachFd(), 
								mMtu, 
								mRouter, 
								"255.255.255.0", 
								socksServerAddress, 
								udpServerAddress, 
								String.valueOf(dsp.getBoolean("dns_forwarder_key", true) ? privateIpAddress + ":9395" : "0.0.0.0:0"), 
								remoteUdpForwardingEnabled);
			}
        };
		tun2socksThread.start();
	}

	/* Disconnects a tunnel created by a previous call to |connectTunnel|. */
	public synchronized void disconnectTunnel()
	{
		addLog("Stopping VPNService...");
		if (pdnsdProcess != null)
		{
            pdnsdProcess.destroy();
            pdnsdProcess = null;
        }
		if (tun2socksThread != null)
		{
			try
			{
				//xBinary.stopPdnsd(this);
				//Tun2SocksJni.stop();
				Tun2Socks.Stop();
				tun2socksThread.join();
			}
			catch (InterruptedException e)
			{
				Thread.currentThread().interrupt();
			}
			finally
			{
				tun2socksThread = null;
			}
		}
		if (tunFd == null)
		{
			return;
		}
		try
		{
			tunFd.close();
		}
		catch (IOException e)
		{
			addLog("Failed to close the VPN interface file descriptor.");
		}
		finally
		{
			tunFd = null;
		}
		isRunning = false;
		addLog("VPNService Disconnected");
	}

	public static String b(Context context, int i)
	{
        Scanner useDelimiter = new Scanner(context.getResources().openRawResource(i), "UTF-8").useDelimiter("\\A");
        StringBuilder stringBuilder = new StringBuilder();
        while (useDelimiter.hasNext())
		{
            stringBuilder.append(useDelimiter.next());
        }
        useDelimiter.close();
        return stringBuilder.toString();
    }

	private void allowAllAFFamilies(VpnService.Builder builder)
	{
        builder.allowFamily(OsConstants.AF_INET);
        builder.allowFamily(OsConstants.AF_INET6);
    }

	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setAllowedVpnPackages(Builder builder)
	{
        boolean atLeastOneAllowedApp = false;
        for (String pkg : dsp.getStringSet("mAllowedAppsVpn", new HashSet()))
		{
            try
			{
                if (dsp.getBoolean("mAllowedAppsVpnAreDisallowed", true))
				{
                    builder.addDisallowedApplication(pkg);
                }
				else
				{
                    builder.addAllowedApplication(pkg);
                    atLeastOneAllowedApp = true;
                }
            }
			catch (PackageManager.NameNotFoundException e)
			{
                //mProfile.mAllowedAppsVpn.remove(pkg);
                addLog(getString(R.string.app_no_longer_exists) + pkg);
            }
        }
        if (!dsp.getBoolean("mAllowedAppsVpnAreDisallowed", true) && !atLeastOneAllowedApp)
		{
            addLog(getString(R.string.no_allowed_app) + getPackageName());
            try
			{
                builder.addAllowedApplication(getPackageName());
            }
			catch (PackageManager.NameNotFoundException e)
			{
                addLog("This should not happen: " + e.getLocalizedMessage());
            }
        }
        /*if (dsp.getBoolean("mAllowedAppsVpnAreDisallowed", true)) {
		 addLog(TextUtils.join(", ", dsp.getStringSet("mAllowedAppsVpn", new HashSet())));
		 } else {
		 addLog(TextUtils.join(", ", dsp.getStringSet("mAllowedAppsVpn", new HashSet())));
		 }*/
    }


	public synchronized String getLocalServerAddress(String port) throws IllegalStateException
	{
		return String.format(Locale.ROOT, "%s:%s", LOCAL_SERVER_ADDRESS, port);
	}

	void addLog(String msg)
	{
		Log.d("", msg);
		//VPNLog.logInfo(msg);
	}

	public final String getApplicationName() throws PackageManager.NameNotFoundException
	{
		PackageManager packageManager = getApplicationContext().getPackageManager();
		ApplicationInfo appInfo = packageManager.getApplicationInfo(getPackageName(), 0);
		return (String) packageManager.getApplicationLabel(appInfo);
	}
}
