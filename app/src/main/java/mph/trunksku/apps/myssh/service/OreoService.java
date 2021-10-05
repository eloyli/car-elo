package mph.trunksku.apps.myssh.service;
import android.annotation.*;
import android.app.*;
import android.content.*;
import android.graphics.*;
import android.net.*;
import android.os.*;
import com.comxa.universo42.injector.modelo.*;
import com.trilead.ssh2.*;
import de.blinkt.openvpn.core.*;
import java.io.*;
import java.lang.reflect.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;
import com.masfha.id.*;
import mph.trunksku.apps.myssh.*;
import mph.trunksku.apps.myssh.core.*;
import mph.trunksku.apps.myssh.fragment.*;
import mph.trunksku.apps.myssh.logger.*;
import mph.trunksku.apps.myssh.model.*;
import mph.trunksku.apps.myssh.util.*;
import mph.trunksku.apps.myssh.view.*;

import com.trilead.ssh2.Connection;
import mph.trunksku.apps.myssh.model.Config;

public class OreoService extends Service implements InteractiveCallback
{
	private IOpenVPNServiceInternal mService;
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service)
		{
            mService = IOpenVPNServiceInternal.Stub.asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0)
		{
            mService = null;
			
        }
    };

	VpnStatus.StateListener mState = new VpnStatus.StateListener() {
		@Override
		public void updateState(String str, String logmessage, int localizedResId, ConnectionStatus level)
		{
			// TODO: Implement this method
			if (str.equals("WAIT"))
			{
				sshMsg("Waiting for server reply");
			}else if (str.equals("RECONNECTING"))
			{
				sshMsg("Reconnecting");
			}else if (str.equals("TCP_CONNECT"))
			{
				sshMsg("Connecting to server");
			}else if (str.equals("GET_CONFIG"))
			{
				sshMsg("Getting server configuration");
			}else if (str.equals("PUSH_REQUEST"))
			{
				sshMsg("Pushing request to server");
			}else if (str.equals("AUTH"))
			{
				logs_handler.sendEmptyMessage(4);
			}else if (str.equals("AUTH_FAILED"))
			{
				logs_handler.sendEmptyMessage(6);
				isRunning = false;
				isSSHRunning = false;
				onStatusChanged("Auth_Fail", false);
				stopInjectThread();
			}else if (str.equals("ASSIGN_IP"))
			{
				sshMsg("Assigning ip address");
			}else if (str.equals("CONNECTED"))
			{
				sshMsg("<b>OpenVPN Connected");
				isRunning = true;
				isSSHRunning = true;
				onStatusChanged("Connected", false);
			}else if (str.equals("NOPROCESS"))
			{
				sshMsg("<b>OpenVPN Disconnected");
				isSSHRunning = false;
				//VpnStatus.removeStateListener(mState);
			}
			//sshMsg(str);
		}

		@Override
		public void setConnectedVPN(String uuid)
		{
			// TODO: Implement this method
		}
	};
	private NotificationManager nm;
	private SharedPreferences dsp;
	public static boolean isRunning = false;
	public static boolean isSSHRunning = false;
	public static boolean isStopping = false;
	private static final String AUTH_PASSWORD = "password";
    private static final int AUTH_TRIES = 1;
    private final int MSG_CONNECTED = AUTH_TRIES;
    private final int MSG_CONNECTING = 3;
    private final int MSG_FINISH = 2;
    private final String TAG = "SSH Tunnel";
    private Connection connection;
	private Handler m_Handler=new Handler();
    private static ConcurrentHashMap<StatusChangeListener, Object> m_OnStatusChangedListeners=new ConcurrentHashMap<StatusChangeListener, Object>();
	private Config ssh;
	private SharedPreferences sp;
	private final String CHANNEL_ID = "mphssh_channel_1";
    private final String CHANNEL_NAME = "mphssh_channel";
	private int listen_port = 8083;
	Thread dataThread;
	private DynamicPortForwarder socksPortForwarder;
	private PowerManager.WakeLock wakeLock;

	private LocalPortForwarder local_port;

	private ProxyThread bXP;

	private HTTPProxyData coS;

	private Thread mInjectThread;

	private ServerSocket listen_socket;

	private Socket input;

	private static Socket output = null;

	private Pinger local_port_pinger;

	private HTTPThread sc1;

	private HTTPThread sc2;
	

	private void setWakelock()
	{
        try
		{
            this.wakeLock = ((PowerManager) getSystemService("power")).newWakeLock(1, "SocksIP::Tag");
            this.wakeLock.acquire();
        }
		catch (Exception e)
		{
            Log.d("WAKELOCK", e.getMessage());
        }
    }

	private void unsetWakelock()
	{
		if (this.wakeLock != null && this.wakeLock.isHeld())
		{
			// Log.e("WAKELOCK", "is disabled");
            this.wakeLock.release();
        }
	}

	public static boolean a(VpnService vpnService)
	{
		if (output == null)
		{
			//h.a("Vpn Protect", "Socket is null");
			return false;
		}
		else if (output.isClosed())
		{
			//h.a("Vpn Protect", "Socket is closed");
			return false;
		}
		else if (!output.isConnected())
		{
			//h.a("Vpn Protect", "Socket not connected");
			return false;
		}
		else if (vpnService.protect(output))
		{
			//h.a("Vpn Protect", "Socket has protected");
			return true;
		}
		else
		{
			//h.a("Vpn Protect", "Failed to protecting socket, reboot this device required");
			return false;
		}
	}

	@Override
	public IBinder onBind(Intent p1)
	{
		// TODO: Implement this method
		return null;
	}

	@Override
	public void onCreate()
	{
		// TODO: Implement this method
		super.onCreate();
		sp = ApplicationBase.getSharedPreferences();
		dsp = ApplicationBase.getDefSharedPreferences();
		ssh = ApplicationBase.getUtils();
		nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		// TODO: Implement this method
		String action = intent.getAction();
		if (action == "START")
		{
            setWakelock();
			status_handler.sendEmptyMessage(11);
			if (dataThread != null)
			{
                dataThread.interrupt();
            }
			dataThread = new Thread(new MyThreadClass(startId));
			dataThread.setName("showNotification");
			dataThread.start();
			if (!StoredData.isSetData)
			{
				StoredData.setZero();
			}
			if (sp.getInt("VPNMod", R.id.mode_1) == R.id.mode_3)
			{
				runTunnel();
			}
			else
			{
				if (mInjectThread != null)
				{
					mInjectThread.interrupt();
				}
				mInjectThread = new Thread(ssl, "mInjectThread");
				mInjectThread.start();
			}
        }
        else if (action == "STOP")
		{
            tearDown();
			stopSelf();
			//addLog(getString(R.string.app_name) + " Disconnected");
        }
		
		return 1;
	}


	Runnable http = new Runnable() {
		@Override
		public void run()
		{
			// TODO: Implement this method
			try
			{
				if (listen_socket == null)
				{
					listen_socket = new ServerSocket(listen_port, 50, InetAddress.getByName("127.0.0.1"));
					listen_socket.setReuseAddress(true);
				}
				oreo.sendEmptyMessage(1);
				while (isRunning)
				{
					try
					{
						input = listen_socket.accept();
						input.setSoTimeout(0);
						Host hostProxy = new Host(ssh.getHost(), Integer.parseInt(ssh.getPort()));
						Host hostCliente = new Host(input);

						InjectService reqInject = new InjectService(hostProxy, hostCliente, input.getPort()) {
							@Override
							public void onLogReceived(String log, int level, Exception e)
							{
								//Injector.this.onLogReceived(log, level, e);
							}

							@Override
							public void onConnectionClosed()
							{
								//Injector.this.onConnectionClosed(this);
							}
						};
						reqInject.setPayload(ssh.getPayload());

						new Thread(reqInject).start();
					}
					catch (IOException e32)
					{
						e32.printStackTrace();
					}
				}
			}
			catch (Exception e)
			{
				mInjectThread.interrupt();
			}
		}
	};

	Runnable ssl = new Runnable() {

		@Override
		public void run()
		{
			// TODO: Implement this method
			try
			{
				listen_socket = new ServerSocket(listen_port);
				listen_socket.setReuseAddress(true);
				sshMsg("Listening for incoming connection");
				oreo.sendEmptyMessage(1);
				while (true)
				{
					input = listen_socket.accept();
					input.setSoTimeout(0);
					if (sp.getInt("VPNMod", R.id.mode_1) == R.id.mode_1)
					{
						output = new HTTPSupport(input).socket2();
					}
					else
					{
						output = new SSLSupport(input).socket();
					}
					if (input != null)
					{
						input.setKeepAlive(true);
					}
					if (output != null)
					{
						output.setKeepAlive(true);
					}
					if (output == null)
					{
						output.close();
					}
					else if (output.isConnected())
					{ 
						sshMsg("Running - Proxy Thread");
					    sc1 = new HTTPThread(input, output, true);
						sc2 = new HTTPThread(output, input, false);
						sc1.setDaemon(true);
						sc1.start();
						sc2.setDaemon(true);
						sc2.start();
					}
				}
			}
			catch (Exception e)
			{
				//sshMsg(e.getMessage());
				//stopInjectThread();
			}

		}
	};

	private Handler oreo = new Handler() {
        public void handleMessage(Message msg)
		{
			if (msg.what == 1)
			{
				runTunnel();
			}
        }
    };

	void runTunnel()
	{
		switch (sp.getInt("VPNTunMod", R.id.ssh))
		{
			case R.id.ssh:
				new Thread(new Runnable() {
						public void run()
						{
							status_handler.sendEmptyMessage(3);
							if (connect())
							{
								logs_handler.sendEmptyMessage(0);
								status_handler.sendEmptyMessage(MSG_CONNECTED);
								isSSHRunning = true;
								vpn_handler(true);
								return;
							}
							status_handler.sendEmptyMessage(MSG_FINISH);
							isSSHRunning = false;
							onDisconnect();

						}
					}).start();
				break;
			case R.id.ovpn:
				Intent intent2 = new Intent(OreoService.this, OpenVPNService.class);
				intent2.setAction(OpenVPNService.START_SERVICE);
				bindService(intent2, mConnection, Context.BIND_AUTO_CREATE);
				VpnStatus.addStateListener(mState);
				VPNLaunchHelper.startOpenVpn(ssh.loadVpnProfile(ssh.getOvpn()), OreoService.this);
				break;
		}
	}



	/*@Override
	public void onDestroy()
	{
		// TODO: Implement this method
		tearDown();
		super.onDestroy();
	}*/

	void tearDown()
	{
		unsetWakelock();
		if (dataThread != null)
		{
			dataThread.interrupt();
		}
		isRunning = false;
		//stop_notification();
		isStopping = true;
		/*if(sp.getInt("NetworkTypeSpin", 0) == 2){
		 oreo.sendEmptyMessage(0);
		 }else{*/

		new Thread(new Runnable() {
				public void run()
				{
					onDisconnect();
				}
			}).start();


	}

	@SuppressLint({"HandlerLeak"})
    final Handler logs_handler = new Handler() {
        public void handleMessage(Message msg)
		{
            switch (msg.what)
			{
                case 0:
					sshMsg("<b>SSH Connected");
					break;
                case 1:
					sshMsg("<b>SSH Disconnected");
					break;
                case 2:
					sshMsg("Start tunnel service");
                    break;
				case 3:
					sshMsg("Forward Successful");
                    break;
				case 4:
					sshMsg("Authenticate with password");
                    break;
				case 5:
					sshMsg("Password auth available");
                    break;
				case 6:
					sshMsg("Cannot authenticate - incorrect user name or password.");
					break;
            }
            super.handleMessage(msg);
        }
    };


    @SuppressLint({"HandlerLeak"})
    final Handler status_handler = new Handler() {
        public void handleMessage(Message msg)
		{
            switch (msg.what)
			{
                case MSG_CONNECTED:

					onStatusChanged("Connected", true);
					showNotification("Connected", "Connected", 0);
					//update_notification_event(R.drawable.ic_cloud_check, "Connected");
					break;
                case MSG_FINISH:

					onStatusChanged("Disconnected", false);
					showNotification("Disconnected", "Disconnected", 0);
					hideNotification();
					//update_notification_event(R.drawable.ic_cloud_outline_off, "Disconnected");
					break;
                case MSG_CONNECTING:
					onStatusChanged("Connecting...", false);
					showNotification("Connecting...", "Connecting...", 0);
					//update_notification_event(R.drawable.ic_cloud_circle, "Connecting...");
                    writeLog("Connecting...");
                    break;
				case 10:
					isRunning = false;
					onStatusChanged("Stop Inject", false);
					break;
				case 11:
					isRunning = true;
					onStatusChanged("Start Inject", true);
					sshMsg("<b>[START] service requested");
					break;

            }
            super.handleMessage(msg);
        }
    };

	final class MyThreadClass implements Runnable
	{

        int service_id;

        MyThreadClass(int service_id)
		{
            this.service_id = service_id;

        }

        @Override
        public void run()
		{
            int i = 0;
            synchronized (this)
			{
                while (dataThread.getName() == "showNotification")
				{
                    //  Log.e("insidebroadcast", Integer.toString(service_id) + " " + Integer.toString(i));
                    getData();
                    try
					{
                        wait(1000);
                        i++;
                    }
					catch (InterruptedException e)
					{
                        //sshMsg(e.getMessage());
                    }

                }
                //stopSelf(service_id);
            }

        }
    }

	public void getData()
	{

        List<Long> allData;

        //if (!network_status.equals("no_connection")) {
        //receiveData = RetrieveData.findData();
        allData = RetrieveData.findData();
        Long mDownload, mUpload;
		long upload = DataTransferGraph.upload;
		long download = DataTransferGraph.access$1;


        mDownload = allData.get(0);
        mUpload = allData.get(1);

        //receiveData = mDownload + mUpload;
        //storedData(mDownload, mUpload);
		try{
			storedData(download, upload);
		}catch(Exception e){
			
		}
    }

	public void storedData(Long mDownload, Long mUpload) throws Exception
	{

        StoredData.downloadSpeed = mDownload;
        StoredData.uploadSpeed = mUpload;

        if (StoredData.isSetData)
		{
            StoredData.downloadList.remove(0);
            StoredData.uploadList.remove(0);

            StoredData.downloadList.add(mDownload);
            StoredData.uploadList.add(mUpload);
        }

		// Log.e("storeddata","test "+toString().valueOf(StoredData.downloadList.size()));

    }

	public static void addOnStatusChangedListener(StatusChangeListener listener)
	{
        if (!m_OnStatusChangedListeners.containsKey(listener))
		{
            m_OnStatusChangedListeners.put(listener, 1);
        }
    }

    public static void removeOnStatusChangedListener(StatusChangeListener listener)
	{
        if (m_OnStatusChangedListeners.containsKey(listener))
		{
            m_OnStatusChangedListeners.remove(listener);
        }
    }

    private void onStatusChanged(final String status, final boolean isRunning)
	{
        m_Handler.post(new Runnable() {
				@Override
				public void run()
				{
					for (Map.Entry<StatusChangeListener, Object> entry : m_OnStatusChangedListeners.entrySet())
					{
						entry.getKey().onStatusChanged(status, isRunning);
					}
				}
			});
	}

    public void writeLog(final String logs)
	{
        m_Handler.post(new Runnable() {
                @Override
                public void run()
				{
                    for (Map.Entry<StatusChangeListener, Object> entry : m_OnStatusChangedListeners.entrySet())
					{
                        entry.getKey().onLogReceived(logs);
                    }
                }
            });
	}

    private void authenticate()
	{
        try
		{
            if (connection.authenticateWithNone(ssh.getSshUsername()))
			{
                sshMsg("Authenticate with none");
                return;
            }
			if (connection.isAuthMethodAvailable(ssh.getSshUsername(), "password"))
			{
                sshMsg("Username: " + ssh.getSshUsername());
                logs_handler.sendEmptyMessage(5);
				if (connection.authenticateWithPassword(ssh.getSshUsername(), ssh.getSshPassword()))
				{
                    logs_handler.sendEmptyMessage(4);
                    return;
                }
            }
			if (connection.isAuthMethodAvailable(ssh.getSshUsername(), "keyboard-interactive"))
			{
                if (connection.authenticateWithKeyboardInteractive(ssh.getSshUsername(), this))
                    return;
            }
        }
		catch (Exception e)
		{
            sshMsg("Something wen't wrong in authentication.");
        }
    }

    public boolean connect()
	{
        try
		{   
			switch (sp.getInt("VPNMod", R.id.mode_1))
			{
				case R.id.mode_1:
					coS = new HTTPProxyData("127.0.0.1", listen_port);
					break;
				case R.id.mode_2:
					coS = new HTTPProxyData("127.0.0.1", listen_port);
					break;
				case R.id.mode_3:
					coS = new HTTPProxyData(ssh.getHost(), Integer.parseInt(ssh.getPort()));
					break;
			}
			connection = new Connection(ssh.getSSHHost(), Integer.parseInt(ssh.getSSHPort()));
		    if (this.coS != null)
			{
				connection.setProxyData(coS);
            }
			//connection.setCompression(dsp.getBoolean("data_compression", false));
			//connection.setTCPNoDelay(true);
		    connection.addConnectionMonitor(new Monitor());
			logs_handler.sendEmptyMessage(2);
            connection.connect(new eServerHostKeyVerifier(), 20000, 30000);
            //connected = true;
			int tries = 0;
            while (!connection.isAuthenticationComplete() && tries++ < 1)
			{
                authenticate();
                Thread.sleep(1000);
            }
			if (connection.isAuthenticationComplete())
			{
				local_port = connection.createLocalPortForwarder(8053, "www.google.com", 80);
				//sshMsg("Local Port Forwarding started at port: "+ssh.getLocalPort());

				socksPortForwarder = connection.createDynamicPortForwarder(Integer.parseInt(dsp.getString("local_port", "1080")));//new InetSocketAddress("127.0.0.1", KPNContext.getSshSocks()));
				
                return true;
            }
        }
		catch (Exception e)
		{
            //sshMsg("[Error] " + e.getLocalizedMessage());
            return false;
        }
        logs_handler.sendEmptyMessage(6);
		//onStatusChanged("Auth_Fail", false);
        return false;
    }

	public class eServerHostKeyVerifier implements ServerHostKeyVerifier
    {

		@Override
		public boolean verifyServerHostKey(String hostname, int port, String keyAlgorithm, byte[] hostKey) throws Exception
		{
			String createHexFingerprint = KnownHosts.createHexFingerprint(keyAlgorithm, hostKey);
			String createHashedHostname = KnownHosts.createHashedHostname(hostname);
			String createBubblebabbleFingerprint = KnownHosts.createBubblebabbleFingerprint(keyAlgorithm, hostKey);
			sshMsg(new StringBuffer().append("<b>Hex Fingerprint:</b> ").append(createHexFingerprint).toString());
			//sshMsg(new StringBuffer().append("<b>Hashed Hostname:</b> ").append(createHashedHostname).toString());
			//sshMsg(new StringBuffer().append("<b>Bubble Babble  Fingerprint:</b> ").append(createBubblebabbleFingerprint).toString());
			//sshMsg(new StringBuffer().append("<b>Key exchange algorithm:</b> ").append().toString());
			sshMsg(new StringBuffer().append("<b>Using algorithm:</b> ").append(keyAlgorithm).toString());
			return true;
		}
	}

	class Monitor implements ConnectionMonitor
    {

		@Override
        public void connectionLost(Throwable arg0)
		{
			//onDisconnect();
			if (isStopping)
			{
				return;
			}
			stopSelf();
			//stopReconnect();
		}
    }

	public void stopReconnect()
	{
		new Thread(new Runnable() {
				public void run()
				{
					onDisconnect();
				}
			}).start();
	}

	public boolean isOnline()
    {
        NetworkInfo activeNetworkInfo = ((ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}

	void stopInjectThread()
	{
		try
		{
			if(sc1 != null){
				sc1.interrupt();
				sc1 = null;
			}
			if(sc2 != null){
				sc2.interrupt();
				sc2 = null;
			}
			if (listen_socket != null)
			{
				listen_socket.close();
				listen_socket = null;
			}
			if (input != null)
			{
				input.close();
				input = null;
			}
			if (output != null)
			{
				output.close();
				output = null;
			}
			if (mInjectThread != null)
			{
				mInjectThread.interrupt();
			}
			status_handler.sendEmptyMessage(10);
			
		}
		catch (Exception e)
		{

		}
	}

	void stopSshThread()
	{
		try
		{
			isSSHRunning = false;

			if (socksPortForwarder != null)
			{
                socksPortForwarder.close();
                socksPortForwarder = null;
            }
			if (local_port_pinger != null)
			{
				local_port_pinger.interrupt();
				local_port_pinger = null;
			}
			if (this.local_port != null)
			{
				local_port.close();
				local_port = null;
			}
			if (connection != null)
			{
				connection.close();
				connection = null;
			}
			/*if(dnsProxy != null) {
			 dnsProxy.Stop();
			 dnsProxy = null;
			 }*/

        }
		catch (Exception e)
		{
        }
	}

	void stopOvpnThread()
	{
		try
		{
			ProfileManager.setConntectedVpnProfileDisconnected(OreoService.this);
			if (mService != null)
			{
				try
				{
					mService.stopVPN(false);
					VpnStatus.removeStateListener(mState);
					
				}
				catch (Exception e)
				{
					sshMsg(e.getMessage());
				}
			}
			
		}
		catch (Exception e)
		{
			sshMsg(e.getMessage());
		}
	}

    private void onDisconnect()
	{
		stopInjectThread();
		switch (sp.getInt("VPNTunMod", R.id.ssh))
		{
			case R.id.ssh:
				stopSshThread();
				vpn_handler(false);
				status_handler.sendEmptyMessage(2);
				logs_handler.sendEmptyMessage(1);
				break;
			case R.id.ovpn:
				stopOvpnThread();
				break;
		}
		
		sshMsg("<b>[STOP] service requested");
		
    }

	private void showNotification(final String msg, String tickerText, int priority)
	{
		if (dsp.getBoolean("show_notification", true))
		{
			Notification.Builder nbuilder = null;
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
			{
				NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_LOW);
				channel.setDescription(CHANNEL_NAME);
				channel.enableLights(true);
				channel.setLightColor(Color.BLUE);
				nm.createNotificationChannel(channel);
				nbuilder = new Notification.Builder(this, CHANNEL_ID);
			}
			else
			{
				nbuilder = new Notification.Builder(this);
			}
			String title = sp.getBoolean("custom_payload_key", false) ? "Custom" : sp.getString("NetName", "");
			nbuilder.setContentTitle(getString(R.string.app_name) + " - " + sp.getString("SSHName", "") + " using " + title);

			nbuilder.setContentText(msg);
			nbuilder.setOnlyAlertOnce(true);
			nbuilder.setOngoing(true);
			nbuilder.setAutoCancel(false);
			nbuilder.setSmallIcon(R.drawable.ic_launcher);

			nbuilder.setWhen(new Date().getTime());


			// Try to set the priority available since API 16 (Jellybean)
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)

				jbNotificationExtras(priority, nbuilder);

			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
				lpNotificationExtras(nbuilder);

			if (tickerText != null && !tickerText.equals(""))
				nbuilder.setTicker(tickerText);



			@SuppressWarnings("deprecation")
				Notification notification = nbuilder.getNotification();


			nm.notify(2615, notification);
			startForeground(2615, notification);

			// Check if running on a TV
		}
    }

	private void hideNotification()
	{
		if (dsp.getBoolean("show_notification", true))
		{
			new Thread(new Runnable() {
					public void run()
					{
						if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
							stopForeground(Service.STOP_FOREGROUND_DETACH);
						else
							stopForeground(false);
						nm.cancel(2615);
					}
				}).start();
		}
	}

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void lpNotificationExtras(Notification.Builder nbuilder)
	{
        nbuilder.setCategory(Notification.CATEGORY_SERVICE);
        nbuilder.setLocalOnly(true);

    }

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void jbNotificationExtras(int priority,
                                      android.app.Notification.Builder nbuilder)
	{
        try
		{
            if (priority != 0)
			{
                Method setpriority = nbuilder.getClass().getMethod("setPriority", int.class);
                setpriority.invoke(nbuilder, priority);

                Method setUsesChronometer = nbuilder.getClass().getMethod("setUsesChronometer", boolean.class);
                setUsesChronometer.invoke(nbuilder, true);

            }
            //ignore exception
        }
		catch (NoSuchMethodException | IllegalArgumentException |
		InvocationTargetException | IllegalAccessException e)
		{
			//  VpnStatus.logException(e);
        }

    }





	@SuppressLint({"DefaultLocale"})
    public String[] replyToChallenge(String name, String instruction, int numPrompts, String[] prompt, boolean[] echo) throws Exception
	{
        String[] responses = new String[numPrompts];
        for (int i = 0; i < numPrompts; i += AUTH_TRIES)
		{
            if (prompt[i].toLowerCase().contains(AUTH_PASSWORD))
			{
                responses[i] = ssh.getSshPassword();
            }
        }
        return responses;
    }

	private void vpn_handler(boolean on)
	{
		try
		{
			if (on)
			{
				Intent intent = new Intent(OreoService.this, VpnTunnelService.class);
				startService(intent.setAction("START"));
			}
			else
			{
				if (VpnTunnelService.isRunning)
				{
					Intent intent = new Intent(OreoService.this, VpnTunnelService.class);
					startService(intent.setAction("STOP"));
				}
			}
		}
		catch (Exception e)
		{
			sshMsg("Something wen't wrong in vpn service.");
		}
	}

    private void sshMsg(String msg)
	{
		//VPNLog.logInfo(msg);
        LogFragment.addLog(msg);
    }
}
