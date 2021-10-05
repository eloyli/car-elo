package mph.trunksku.apps.myssh.core;
import android.content.*;
import com.trilead.ssh2.*;
import com.masfha.id.*;
import mph.trunksku.apps.myssh.*;
import mph.trunksku.apps.myssh.service.*;

public class SSHThread implements Runnable
{

	private SharedPreferences sp;

	private SharedPreferences dsp;
	
	private VpnTunnelService mService;
	
	private String sshHost, sshPort, sshUser, sshPass;

	private DynamicPortForwarder socksPortForwarder;

	private Pinger pinger;
	
	public SSHThread(VpnTunnelService mService, String sshHost, String sshPort, String sshUser, String sshPass) {
		sp = ApplicationBase.getSharedPreferences();
		dsp = ApplicationBase.getDefSharedPreferences();
		this.mService = mService;
		this.sshHost = sshHost;
		this.sshPort = sshPort;
		this.sshUser = sshUser;
		this.sshPass = sshPass;
	}
	
	@Override
	public void run()
	{
		// TODO: Implement this method
		if(connect()){
			return;
		}
	}
	
	public boolean connect()
	{
		Connection sshConnection = null;
        try
		{
			if (sp.getInt("VPNMod", R.id.mode_1) == R.id.mode_2)
			{
				
					sshConnection = new Connection(sshHost, Integer.parseInt(sshPort));
				    sshConnection.setProxyData(new HTTPProxyData("127.0.0.1", 8083));
				
			}
			else if (sp.getInt("VPNMod", R.id.mode_1) == R.id.mode_1)
			{
				sshConnection = new Connection(sshHost, Integer.parseInt(sshPort));
				sshConnection.setProxyData(new HTTPProxyData("127.0.0.1", 8083));
			}
			

			if (dsp.getBoolean("data_compression", false))
			{
			 	sshConnection.setCompression(true);
			}
			//connection.setTCPNoDelay(true);
		    sshConnection.addConnectionMonitor(new Monitor());
			//logs_handler.sendEmptyMessage(2);
            sshConnection.connect(new eServerHostKeyVerifier(), 10 * 1000, 20 * 1000);
            //connected = true;
			int tries = 0;
            while (!sshConnection.isAuthenticationComplete() && tries++ < 1)
			{
                authenticate(sshConnection);
                Thread.sleep(1000);
            }
			if (sshConnection.isAuthenticationComplete())
			{
                return enableSocksForward(sshConnection);
            }
        }
		catch (Exception e)
		{
            //sshMsg("[Error] " + e.getLocalizedMessage());
            return false;
        }
       // logs_handler.sendEmptyMessage(6);
        return false;
    }
	
	private void authenticate(Connection sshConnection)
	{
        try
		{
            if (sshConnection.authenticateWithNone(sshUser))
			{
                //sshMsg("Authenticate with none");
                return;
            }
			if (sshConnection.isAuthMethodAvailable(sshUser, "password"))
			{
                //sshMsg("Username: " + ssh.getSshUsername());
                //logs_handler.sendEmptyMessage(5);
				if (sshConnection.authenticateWithPassword(sshUser, sshPass))
				{
                   // logs_handler.sendEmptyMessage(4);
                    return;
                }
            }
			if (sshConnection.isAuthMethodAvailable(sshUser, "keyboard-interactive"))
			{
                if (sshConnection.authenticateWithKeyboardInteractive(sshUser, new InteractiveCallback() {
							@Override
							public String[] replyToChallenge(String name, String instruction, int numPrompts, String[] prompt, boolean[] echo) throws Exception
							{
								String[] responses = new String[numPrompts];
								for (int i = 0; i < numPrompts; i += 1)
								{
									if (prompt[i].toLowerCase().contains("password"))
									{
										responses[i] = sshPass;
									}
								}
								return responses;
							}
						}))
                    return;
            }
        }
		catch (Exception e)
		{
            //sshMsg("Something wen't wrong in authentication.");
        }
    }
	
	class Monitor implements ConnectionMonitor
    {
        @Override
        public void connectionLost(Throwable arg0)
		{
			//onDisconnect();
			
		}
    }

	public class eServerHostKeyVerifier implements ServerHostKeyVerifier
    {

		@Override
		public boolean verifyServerHostKey(String hostname, int port, String keyAlgorithm, byte[] hostKey) throws Exception
		{
			/*ConnectionInfo sci = new ConnectionInfo(); 
			String createHexFingerprint = KnownHosts.createHexFingerprint(keyAlgorithm, hostKey);
			String createHashedHostname = KnownHosts.createHashedHostname(hostname);
			String createBubblebabbleFingerprint = KnownHosts.createBubblebabbleFingerprint(keyAlgorithm, hostKey);
			sshMsg(new StringBuffer().append("<b>Hex Fingerprint:</b> ").append(createHexFingerprint).toString());
			sshMsg(new StringBuffer().append("<b>Hashed Hostname:</b> ").append(createHashedHostname).toString());
			sshMsg(new StringBuffer().append("<b>Bubble Babble  Fingerprint:</b> ").append(createBubblebabbleFingerprint).toString());
			//sshMsg(new StringBuffer().append("<b>Key exchange algorithm:</b> ").append().toString());
			sshMsg(new StringBuffer().append("<b>Using algorithm:</b> ").append(keyAlgorithm).toString());*/
			return true;
		}
	}


    private boolean enableSocksForward(Connection sshConnection)
	{
        try
		{
			//Utils.updateDnsResolvers(this);
			//dnsForwarder = connection.createLocalPortForwarder(8053, "www.google.com", 80);
			//sshMsg("Local Port Forwarding started at port: "+ssh.getLocalPort());

			socksPortForwarder = sshConnection.createDynamicPortForwarder(Integer.parseInt(dsp.getString("local_port", "1080")));//new InetSocketAddress("127.0.0.1", KPNContext.getSshSocks()));
			//sshMsg("Dynamic Port Forwarding started at port: "+ssh.getDynamicPort());
			String D = dsp.getString("ping_server", "www.google.com");
			if (!D.equals(""))
			{
				pinger = new Pinger(sshConnection, D);
				pinger.start();
			}

            //logs_handler.sendEmptyMessage(3);
            //connection.ping();
            return true;
        }
		catch (Exception e)
		{
           // sshMsg(e.toString());
            return false;
        }
    }
}
