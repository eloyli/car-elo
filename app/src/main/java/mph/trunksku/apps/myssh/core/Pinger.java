package mph.trunksku.apps.myssh.core;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Random;
import com.trilead.ssh2.*;
import mph.trunksku.apps.myssh.logger.*;

public class Pinger extends Thread {
    private final Connection a;
    private final String b;
    private LocalPortForwarder c;
    private boolean d;
    private final String e = "PINGER";
    private Socket f;

    public Pinger(Connection aVar, String str) {
        this.a = aVar;
        this.b = str;
    }

    private int b() {
        return (new Random().nextInt(6) + 5) * 1000;
    }

    public synchronized void close() {
        this.d = false;
        try {
            if (this.f != null) {
                this.f.close();
                this.f = null;
            }
        } catch (Exception e) {
        }
        try {
            if (this.c != null) {
                this.c.close();
                this.c = null;
            }
        } catch (Exception e2) {
        }
       // Log.d("PINGER", "Pinger stoped");
        interrupt();
    }

    public void run() {
        //Log.d("PINGER", "Pinger started");
        try {
            this.c = this.a.createLocalPortForwarder(9395/*8496*/, this.b, 80);
            this.d = true;
            while (this.d) {
                try {
                    //Log.d("PINGER", "Destination: " + this.b);
                    this.f = new Socket("127.0.0.1", 9395);
                    this.f.setSoTimeout(20000);
                    OutputStream outputStream = this.f.getOutputStream();
                    outputStream.write(("GET http://" + this.b + "/ HTTP/1.1\r\nHost: " + this.b + "\r\n\r\n").getBytes());
                    outputStream.flush();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(this.f.getInputStream()));
                    String readLine = bufferedReader.readLine();
                    if (readLine != null) {
                        //Log.d("PINGER", "Response: " + readLine);
                    } else {
                       // Log.d("PINGER", "Response: No Data");
                    }
                    bufferedReader.close();
                    outputStream.close();
                    this.f.close();
                } catch (Exception e) {
                   // Log.d("", "PINGER1"+ e.toString());
                }
                try {
                    sleep((long) b());
                } catch (Exception e2) {
                    return;
                }
            }
        } catch (Exception e3) {
            // Log.d("", "PINGER" + e3.toString());
        }
    }
}

