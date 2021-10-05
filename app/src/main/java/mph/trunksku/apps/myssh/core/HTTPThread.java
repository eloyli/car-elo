package mph.trunksku.apps.myssh.core;

import java.io.*;
import java.net.*;
import java.util.*;
import mph.trunksku.apps.myssh.fragment.*;
import mph.trunksku.apps.myssh.*;
import android.content.*;

public class HTTPThread extends Thread
{
    private final String TAG = "ProxyThread";
    Socket incoming;
    Socket outgoing;
    private boolean clientToServer;

	private SharedPreferences sp;

    public HTTPThread(Socket socket, Socket socket2, boolean z)
	{
		incoming = socket;
		outgoing = socket2;
        this.clientToServer = z;
		sp = ApplicationBase.getSharedPreferences();
		//setDaemon(true);
    }
	

    public final void run() {
		try{
			byte[] buffer;
			if (clientToServer) {
				buffer = new byte[Integer.parseInt(sp.getString("buffer_send", "16384"))];
			} else {
				buffer = new byte[Integer.parseInt(sp.getString("buffer_receive", "32768"))];
			}
			InputStream FromClient = this.incoming.getInputStream();
			OutputStream ToClient = this.outgoing.getOutputStream();
			while (true) {
				int numberRead = FromClient.read(buffer);
				if (numberRead == -1) {
					break;
				}
				String result = new String(buffer, 0, numberRead);
				if (this.clientToServer) {
					ToClient.write(buffer, 0, numberRead);
					ToClient.flush();
				} else {
					String[] split = result.split("\r\n");
					if (split[0].toLowerCase(Locale.getDefault()).startsWith("http")) {
						result = split[0].substring(9, 12);
						addLog(split[0]);
						if (result.indexOf("200") >= 0) {
							ToClient.write(buffer, 0, numberRead);
							ToClient.flush();
						} else if (true) {
							addLog("set auto replace response");
							if (split[0].split(" ")[0].equals("HTTP/1.1")) {
								addLog("replace 200 OK");
								ToClient.write(new StringBuilder(String.valueOf(split[0].split(" ")[0])).append(" 200 OK\r\n\r\n").toString().getBytes());
							} else {
								try {
									addLog("<b>Status: 200 (Connection established) Successful</b> - The action requested by the client was successful.");
									ToClient.write(new StringBuilder(String.valueOf(split[0].split(" ")[0])).append(" 200 Connection established\r\n\r\n").toString().getBytes());
								} catch (Exception e) {
									/*if (!e.toString().contains("Socket closed")) {
										addLog("socket closed");
									}*/
									try {
										if (this.incoming != null) {
											this.incoming.close();
										}
										if (this.outgoing != null) {
											this.outgoing.close();
											return;
										}
										return;
									} catch (IOException e2) {
										return;
									}
								} catch (Throwable th) {
									try {
										if (this.incoming != null) {
											this.incoming.close();
										}
										if (this.outgoing != null) {
											this.outgoing.close();
										}
									} catch (IOException e3) {
									}
								}
							}
							ToClient.flush();
						} else {
							ToClient.write(buffer, 0, numberRead);
							ToClient.flush();
						}
					} else {
						ToClient.write(buffer, 0, numberRead);
						ToClient.flush();
					}
				}
			}
			FromClient.close();
			ToClient.close();
		}catch(Exception e){
			try {
				if (this.incoming != null) {
					this.incoming.close();
				}
				if (this.outgoing != null) {
					this.outgoing.close();
				}
			} catch (IOException e4) {
			}
		}
    }

	void addLog(String str)
	{
		LogFragment.addLog(str);
	}
}


