package mph.trunksku.apps.myssh.core;

import java.io.*;
import java.net.*;
import java.security.*;
import java.util.*;
import javax.net.ssl.*;
import mph.trunksku.apps.myssh.*;
import mph.trunksku.apps.myssh.fragment.*;

import mph.trunksku.apps.myssh.model.*;
import java.nio.channels.*;
import android.annotation.*;
import android.content.*;
import java.security.cert.*;

public class SSLSupport
{

	private Socket input;

	private Config http;

	
	public SSLSocket bc;

	public Socket socket;

	private SharedPreferences dsp;

	public SSLSupport(Socket in)
	{
		input = in;
		http = ApplicationBase.getUtils();
		dsp = ApplicationBase.getDefSharedPreferences();
	}

	public Socket socket()
	{
        try
		{
            String readRequestHeader = readRequestHeader();
            if (readRequestHeader == null || !readRequestHeader.contains(":"))
			{
                //addLog(new StringBuffer().append("Invalid request: ").append(readRequestHeader).toString());
                return (Socket) null;
            }
            String host = readRequestHeader.split(":")[0];
            int port = Integer.parseInt(readRequestHeader.split(":")[1]);
            sendForwardSuccess(input);
            socket = SocketChannel.open().socket();
			socket.connect(new InetSocketAddress(host, port));

            //doVpnProtect(socket);
            //socket = doSSLHandshake(socket, utils.getPayload(), 443);
		    if (socket.isConnected())
			{
				//socket = newSocket(http.getPayload(), http.getSSHHost(), Integer.parseInt(http.getSSHPort()));
				socket = doSSLHandshakeOld(http.getSSHHost(), http.getPayload(), Integer.parseInt(http.getSSHPort()));
				//socket = doSSLHandshake(http.getPayload(), http.getSSHHost(), Integer.valueOf(http.getSSHPort()));
			}
			return socket;
        }
		catch (Exception e)
		{
            //addLog(new StringBuffer().append("Exception in proxy thread: ").append(e.getMessage()).toString());
       		return null;
		}
    }

	public Socket newSocket(String sni, String host, int port)
	{
		try
		{
			SSLSocket sSock = null;
            TLSSocketFactory d2 = new TLSSocketFactory();
			if (sni.isEmpty())
			{
				sSock = (SSLSocket)d2.createSocket(host, port);
				sSock.addHandshakeCompletedListener(new mHandshakeCompletedListener(host, port, sSock));
				sSock.startHandshake();
			}
			else
			{
				URL uRL = new URL("https://" + sni);
				String string4 = uRL.getHost();
				if (uRL.getPort() > 0)
				{
					string4 = string4 + ":" + uRL.getPort();
				}
				if (!uRL.getPath().equals((Object)"/"))
				{
					string4 = string4 + uRL.getPath();
				}
				/*if (kpn.soft.dev.kpnrevolution.c.h.M() || kpn.soft.dev.kpnrevolution.c.h.L()) {
				 string4 = j.a(string4);
				 }*/
				//u = /*true ? (HttpsURLConnection)uRL.openConnection(new Proxy(Proxy.Type.HTTP, this.v.a())) :*/ (HttpsURLConnection)uRL.openConnection();
				HttpsURLConnection con = (HttpsURLConnection) uRL.openConnection();
	      		con.setHostnameVerifier(new HostnameVerifier() {
						@SuppressLint({"BadHostnameVerifier"})
						public boolean verify(String str, SSLSession sSLSession) {
							return true;
						}
					});
				con.setSSLSocketFactory(d2);
				sSock = (SSLSocket) con.getSSLSocketFactory().createSocket(host, port);
				con.connect();
			}
			return sSock;
        }
		catch (Exception e)
		{
            //addLog(new StringBuffer().append("Exception in proxy thread: ").append(e.getMessage()).toString());
       		return null;
		}
	}

	public Socket startSocket()
	{
		try
		{
			Socket socket = SocketChannel.open().socket();
			socket.connect(new InetSocketAddress(http.getSSHHost(), Integer.valueOf(http.getSSHPort())), 10000);
			if (socket.isConnected())
			{
				socket = doTLSHandshake( http.getPayload(), http.getSSHHost(), Integer.valueOf(http.getSSHPort()));
			}
			return socket;
		}
		catch (Exception e)
		{
			return null;
		}
	}

	private Socket doTLSHandshake(String sni, String host, int port)
	{
		SSLSocket sslSocket;
		try
		{
			TLSSocketFactory factory = new TLSSocketFactory();
			sslSocket = (SSLSocket) factory.createSocket(socket, sni, port, true);
			sslSocket.getClass().getMethod("setHostname", new Class[]{String.class}).invoke(sslSocket, new Object[]{sni});
			sslSocket.startHandshake();
			sslSocket.getOutputStream().write(String.format("CONNECT %s:%s HTTP/1.1\r\nHost: %s\r\n\r\n", new Object[]{host, port, sni}).getBytes());
			InputStream inStream = sslSocket.getInputStream();
			ResponseHeader resHeader = new HTTPHeaderReader(inStream).read();
			if (resHeader.getBodyLength() <= 0 || inStream.read(new byte[resHeader.getBodyLength()]) != -1)
			{
				if (resHeader.getStatusText().toLowerCase().equals("connection established"))
				{
					//this.listener.onConnectionCompleted();
				}
				return sslSocket;
			}
		}
		catch (Exception e)
		{
			//addLog(e.getMessage());
			return null;
		}
		return null;
	}

    /*private String readRequestHeader() throws IOException {
	 Reader reader = new InputStreamReader(input.getInputStream());
	 BufferedReader lineReader = new BufferedReader(reader);

	 String str = null;
	 String readLine;
	 do {
	 readLine = lineReader.readLine();
	 if (readLine == null) {
	 str = null;
	 }
	 if (readLine.startsWith("CONNECT")) {
	 str = readLine.split(" ")[1];
	 }
	 } while (readLine.length() != 0);
	 return str;
	 }*/

    private String readRequestHeader() throws IOException
	{
        Reader reader = new InputStreamReader(input.getInputStream());
		BufferedReader lineReader = new BufferedReader(reader);
        String request = null;
        String line;
        while ((line = lineReader.readLine()) != null)
		{
            if (line.startsWith("CONNECT") && request == null)
			{
                request = line.split(" ")[1];
            }
            if (line.length() == 0)
			{
                return request;
            }
        }
        return request;
    }

    private void sendForwardSuccess(Socket socket) throws IOException
	{
        String respond = "HTTP/1.1 200 OK\r\n\r\n";
        socket.getOutputStream().write(respond.getBytes());
        socket.getOutputStream().flush();
    }

	private Socket doSSLHandshakeOld(String host, String sni, int port) throws IOException
	{
        TrustManager[] trustAllCerts = new TrustManager[] {
			new X509TrustManager() {
				public java.security.cert.X509Certificate[] getAcceptedIssuers()
				{
					return null;
				}
				public void checkClientTrusted(
					java.security.cert.X509Certificate[] certs, String authType)
				{
				}
				public void checkServerTrusted(
					java.security.cert.X509Certificate[] certs, String authType)
				{
				}
			}
		};
        try
		{
            SSLContext sSLContext = SSLContext.getInstance("TLS");
            KeyManager[] keyManagerArr = (KeyManager[]) null;
            sSLContext.init(keyManagerArr, trustAllCerts, new SecureRandom());
            SSLSocket socket = (SSLSocket) sSLContext.getSocketFactory().createSocket(host, port);
            setSNIHost(sSLContext.getSocketFactory(), socket, sni);
			if(dsp.getString("tls_version_min_override", "default").equals("default")){
				socket.setEnabledProtocols(socket.getEnabledProtocols());
			}else{
				socket.setEnabledProtocols(new String[] {dsp.getString("tls_version_min_override", "default")});
            }
			socket.addHandshakeCompletedListener(new mHandshakeCompletedListener(host, port, socket));
			/*SSLEngine se = sSLContext.createSSLEngine();
			se.setUseClientMode(true);
			se.setEnableSessionCreation(true);
			se.setWantClientAuth(true);
			SSLParameters sslParams = se.getSSLParameters();
            sslParams.setEndpointIdentificationAlgorithm("HTTPS");
            se.setSSLParameters(sslParams);
			se.beginHandshake();*/
			socket.startHandshake();
            return socket;
        }
		catch (Exception e)
		{
            IOException iOException = new IOException(new StringBuffer().append("Could not do SSL handshake: ").append(e).toString());
            throw iOException;
        }
    }


	private SSLSocket doSSLHandshake(String host, String sni, int port) throws IOException
	{
        try
		{
			TLSSocketFactory tsf = new TLSSocketFactory();
            SSLSocket sslSocket = (SSLSocket) tsf.createSocket(host, port);
			setSNIHost(tsf, sslSocket, sni);
			//socket.setEnabledProtocols(socket.getSupportedProtocols());
            sslSocket.addHandshakeCompletedListener(new mHandshakeCompletedListener(host, port, sslSocket));
            addLog("Starting SSL Handshake...");
			sslSocket.startHandshake();
			return sslSocket;
        }
		catch (Exception e)
		{
            IOException iOException = new IOException(new StringBuffer().append("Could not do SSL handshake: ").append(e).toString());
            throw iOException;
        }
    }

	/*private SSLSocket doSSLHandshake(String host, String sni, int port) throws IOException {
	 TrustManager[] trustAllCerts = new TrustManager[] {
	 new X509TrustManager() {
	 public java.security.cert.X509Certificate[] getAcceptedIssuers() {
	 return null;
	 }
	 public void checkClientTrusted(
	 java.security.cert.X509Certificate[] certs, String authType) {
	 }
	 public void checkServerTrusted(
	 java.security.cert.X509Certificate[] certs, String authType) {
	 }
	 }
	 };
	 try {
	 SSLContext sSLContext = SSLContext.getInstance("TLS");
	 KeyManager[] keyManagerArr = (KeyManager[]) null;
	 sSLContext.init(keyManagerArr, trustAllCerts, new SecureRandom());
	 SSLSocket socket = (SSLSocket) sSLContext.getSocketFactory().createSocket(host, port);
	 if (sSLContext.getSocketFactory() instanceof android.net.SSLCertificateSocketFactory && android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
	 ((android.net.SSLCertificateSocketFactory)sSLContext.getSocketFactory()).setHostname(socket, sni);
	 } else {
	 try {
	 socket.getClass().getMethod("setHostname", String.class).invoke(socket, sni);
	 //addLog("Setting up SNI: "+sni);
	 } catch (Throwable e) {
	 // ignore any error, we just can't set the hostname...
	 }
	 }
	 //socket.setEnabledProtocols(socket.getSupportedProtocols());
	 socket.addHandshakeCompletedListener(new mHandshakeCompletedListener(host, port, socket));
	 addLog("Starting SSL Handshake...");
	 socket.startHandshake();
	 return socket;
	 } catch (Exception e) {
	 IOException iOException = new IOException(new StringBuffer().append("Could not do SSL handshake: ").append(e).toString());
	 throw iOException;
	 }
	 }*/

	class mHandshakeCompletedListener implements HandshakeCompletedListener
	{
        private final String val$host;
        private final int val$port;
        private final SSLSocket val$sslSocket;

        mHandshakeCompletedListener(String str, int i, SSLSocket sSLSocket)
		{
            this.val$host = str;
            this.val$port = i;
            this.val$sslSocket = sSLSocket;
        }

        public void handshakeCompleted(HandshakeCompletedEvent handshakeCompletedEvent)
		{
			try
			{
				// addLog(new StringBuffer().append("<b>Established ").append(handshakeCompletedEvent.getSession().getProtocol()).append(" connection with ").append(val$host).append(":").append(this.val$port).append(" using ").append(handshakeCompletedEvent.getCipherSuite()).append("</b>").toString());
				//addLog(new StringBuffer().append("<b>Established ").append(handshakeCompletedEvent.getSession().getProtocol()).append(" connection ").append("using ").append(handshakeCompletedEvent.getCipherSuite()).append("</b>").toString());
				//addLog(new StringBuffer().append("Supported cipher suites: ").append(Arrays.toString(this.val$sslSocket.getSupportedCipherSuites())).toString());
				//addLog(new StringBuffer().append("Enabled cipher suites: ").append(Arrays.toString(this.val$sslSocket.getEnabledCipherSuites())).toString());
				addLog(new StringBuffer().append("Supported protocols <br>").append(Arrays.toString(val$sslSocket.getSupportedProtocols())).toString().replace("[", "").replace("]", "").replace(",", "<br>"));
				//addLog(new StringBuffer().append("SSL: Enabled protocols: <br>").append(Arrays.toString(val$sslSocket.getEnabledProtocols())).toString().replace("[", "").replace("]", "").replace(",", "<br>"));
				addLog("Using cipher " + handshakeCompletedEvent.getSession().getCipherSuite());
				addLog("Using protocol " + handshakeCompletedEvent.getSession().getProtocol());
				//addLog("" + handshakeCompletedEvent.getPeerPrincipal().toString());
				addLog("Handshake finished");
       		}
			catch (Exception e)
			{
				
			}
		}
    }

	private void setSNIHost(final SSLSocketFactory factory, final SSLSocket socket, final String hostname) {
        if (factory instanceof android.net.SSLCertificateSocketFactory && android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
            ((android.net.SSLCertificateSocketFactory)factory).setHostname(socket, hostname);
        } else {
            try {
                socket.getClass().getMethod("setHostname", String.class).invoke(socket, hostname);
            } catch (Throwable e) {
                // ignore any error, we just can't set the hostname...
            }
        }
    }
	
	void addLog(String str)
	{
		LogFragment.addLog(str);
	}

}


