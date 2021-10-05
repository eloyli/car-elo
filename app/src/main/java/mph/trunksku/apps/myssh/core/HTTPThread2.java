package mph.trunksku.apps.myssh.core;

import java.io.InputStream;
import java.io.OutputStream;
import mph.trunksku.apps.myssh.a.*;
import java.net.*;
import mph.trunksku.apps.myssh.fragment.*;

public class HTTPThread2 extends Thread {
    private final InputStream a;
    private final OutputStream b;
    private final boolean c;
    private final boolean persistun = false;
	private boolean paylock = false;
    public HTTPThread2(Socket inputStream, Socket outputStream, boolean z) throws Exception{
        this.a = inputStream.getInputStream();
        this.b = outputStream.getOutputStream();
        this.c = z;
    }
	
	public static void connect(Socket socket, Socket socket2)
	{
		HTTPThread sc1 = new HTTPThread(socket, socket2, true);
		HTTPThread sc2 = new HTTPThread(socket2, socket, false);
		sc1.setDaemon(true);
		sc1.start();
		sc2.setDaemon(true);
		sc2.start();
	}

    private void a(String str, int i) {
        String str2;
        if (str.contains("\r\n\r\n")) {
            if (paylock && str.contains("\r\n")) {
                if (str.regionMatches(true, 0, "http/", 0, 5)) {
                    str2 = str.split("\r\n")[0];
                }
            }
            str2 = (str.split("\r\n\r\n")[0] + "\r\n\r\n").replace("\r", "\\r").replace("\n", "\\n");
        } else {
            str2 = str.length() >= 50 ? str.substring(0, 50).replace("\r", "").replace("\n", "") : str.replace("\r", "").replace("\n", "");
        }
		//addLog(str2);
       // e.a(i, new Object[]{str2});
    }

    private String[] a(String str) {
        try {
            if (!str.contains(" ")) {
                return null;
            }
            String str2 = str.contains("\r\n") ? "\r\n" : str.contains("\r") ? "\r" : str.contains("\n") ? "\n" : null;
            return str2 != null ? str.split(str2)[0].split(" ") : null;
        } catch (Exception e) {
            return null;
        }
    }

    public void run() {
        Object obj = null;
        super.run();
        try {
            byte[] bArr = new byte[(this.c ? 16384 : 32768)];
            while (true) {
                int read = this.a.read(bArr);
                if (read == -1) {
                    break;
                }
                Object obj2;
                if (this.c) {
                    String str = new String(bArr, 0, read);
                    if (str.regionMatches(true, 0, "http/", 0, 5)) {
                        if (obj != null) {
                            a(str, 2131558490);
                            long available = (long) this.a.available();
                            if (available > 0) {
                                this.a.skip(available);
                            }
                        } else {
                            String[] a = a(str);
                            if (a == null) {
                                obj2 = 1;
                                this.b.write(bArr, 0, read);
                                this.b.flush();
                                if (this.persistun) {
                                    obj = obj2;
                                } else {
                                    a(new String(bArr, 0, read), this.c ? 2131558482 : 2131558489);
                                    obj = obj2;
                                }
                            } else if (a[1].equals("200")) {
                                this.b.write(bArr, 0, read);
                                this.b.flush();
                                a(str, 2131558482);
								addLog("<b>Status: 200 (Connection established) Successful</b> - The action requested by the client was successful.");
                                obj = 1;
                            } else {
                                a(str, 2131558482);
								addLog("<b>Status: 200 (Connection established) Successful</b> - The action requested by the client was successful.");
                               // e.a(2131558484, new Object[]{"HTTP/1.0 200 Connection Established\r\n\r\n".replace("\r\n", "\\r\\n")});
                                this.b.write("HTTP/1.0 200 Connection Established\r\n\r\n".getBytes());
                                this.b.flush();
                                obj = 1;
                            }
                        }
                    }
                }
                obj2 = obj;
                this.b.write(bArr, 0, read);
                this.b.flush();
                if (this.persistun) {
                    obj = obj2;
                } else {
                    if (this.c) {
                    }
                    a(new String(bArr, 0, read), this.c ? 2131558482 : 2131558489);
                    obj = obj2;
                }
            }
            if (this.c) {
                i.a();
            }
        } catch (Exception e) {
			//addLog(e.getMessage());
            //e.a(2131558483, new Object[]{e.getLocalizedMessage()});
            if (isAlive()) {
                interrupt();
            }
        } finally {
            if (this.c) {
                i.a();
            }
        }
        if (isAlive()) {
            interrupt();
        }
    }
	
	void addLog(String str)
	{
		LogFragment.addLog("Thread: " + str);
	}
}

