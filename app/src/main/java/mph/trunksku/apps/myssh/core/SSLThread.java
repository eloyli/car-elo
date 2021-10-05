package mph.trunksku.apps.myssh.core;

import java.io.*;
import java.net.*;
import mph.trunksku.apps.myssh.fragment.*;

public class SSLThread extends Thread {
    private InputStream mInput;
    private OutputStream mOutput;

	private boolean clientToServer ;

    public SSLThread(InputStream inputStream, OutputStream outputStream, boolean z) {
        this.mInput = inputStream;
        this.mOutput = outputStream;
		clientToServer = z;
    }

	public static void connect(Socket s, Socket s2){
		try{
			SSLThread forwardThread = new SSLThread(s2.getInputStream(), s.getOutputStream(), true);
			forwardThread.start();
			SSLThread forwardThread2 = new SSLThread(s.getInputStream(), s2.getOutputStream(), false);
			forwardThread2.start();
		}catch(Exception e){
			
		}
	}
	
    @Override
    public void run() {
		try {
            byte[] bArr;
			if (clientToServer) {
				bArr = new byte[16384];
			} else {
				bArr = new byte[32768];
			}
            while (!isInterrupted()) {
                int read = mInput.read(bArr);
                int i = read;
                if (read == -1) {
                    break;
                }
                mOutput.write(bArr, 0, i);
                mOutput.flush();
            }
            mOutput.close();
            mInput.close();
        } catch (Exception e) {
            Exception exception = e;
            try {
                mOutput.close();
            } catch (IOException e2) {
               // iOException = e2;
            }
            try {
                mInput.close();
            } catch (IOException e22) {
                //iOException = e22;
            }
        }
       // Log.d("SSLDroid", this.tcpProxyServerThread.tunnelName+"/"+sessionid+": Quitting "+side+"-side stream proxy...");
    }
}


