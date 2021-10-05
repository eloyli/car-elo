package mph.trunksku.apps.myssh.util;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Build;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import mph.trunksku.apps.myssh.logger.*;
import java.io.*;

public class xBinary {
    private static Process proceso;

    public static void ExtractPdnsd(Context context) throws IOException {
        String pdnsd_binary = new StringBuilder().append(context.getFilesDir().getAbsolutePath()).append("/pdnsd").toString();
        InputStream inputStream = Build.VERSION.SDK_INT >= 21 ? context.getAssets().open("bin/pie/pdnsd") : context.getAssets().open("bin/pdnsd");
        if (inputStream == null) {
            return;
        }
        File pdnsd_file = new File(pdnsd_binary);
		if(!pdnsd_file.exists()){
			FileOutputStream fileOutputStream = new FileOutputStream(pdnsd_file);
			WriteTo(inputStream, fileOutputStream);
			pdnsd_file.setExecutable(true);
			fileOutputStream.close();
			inputStream.close();
		}
		
		runCommand("chmod 771 " + context.getFilesDir().getAbsolutePath());
        runCommand("chmod 700 " + context.getFilesDir().getAbsolutePath() + "/pdnsd");
		//runCommand("chmod 700 " + context.getFilesDir().getAbsolutePath() + "/pdnsd.sh");
    }

    public static void ExtractExecutable(Context context) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(context.getFilesDir().getAbsolutePath());
        stringBuilder.append("/udp");
        String string2 = stringBuilder.toString();
        InputStream inputStream = Build.VERSION.SDK_INT >= 21 ? context.getAssets().open("bin/pie/udp") : context.getAssets().open("bin/udp");
        if (inputStream == null) {
            return;
        }
        File file = new File(string2);
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        WriteTo(inputStream, fileOutputStream);
        file.setExecutable(true);
        fileOutputStream.close();
        inputStream.close();
        
    }
	
	public static void runPdnsd(Context context, String dns1, String dns2) {
		runCommand(context.getFilesDir().getAbsolutePath() + "/pdnsd.sh start 0.0.0.0 " + dns1 + " 53 " + dns2 + " 53");
	}
	
	public static void stopPdnsd(Context context) {
		runCommand(context.getFilesDir().getAbsolutePath() + "/pdnsd.sh stop");
	}

    public static void ServiceGatewayDNS(Context context, boolean bl, String str, String str2) throws IOException, InterruptedException {
        String string2;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(context.getFilesDir().getAbsolutePath());
        stringBuilder.append("/pdnsd.sh");
        String string3 = stringBuilder.toString();
        if (bl) {
            StringBuilder stringBuilder2 = new StringBuilder();
            stringBuilder2.append("sh ");
            stringBuilder2.append(string3);
            stringBuilder2.append(" start 0.0.0.0 "+str+" 53 "+str2+" 53");
            string2 = stringBuilder2.toString();
        } else {
            StringBuilder stringBuilder3 = new StringBuilder();
            stringBuilder3.append("sh ");
            stringBuilder3.append(string3);
            stringBuilder3.append(" stop");
            string2 = stringBuilder3.toString();
        }
        StringBuilder stringBuilder4 = new StringBuilder();
        stringBuilder4.append("SALIDA ");
        stringBuilder4.append(string2);
        //Log.e((String)"DNS DEAMON", (String)stringBuilder4.toString());
        Runtime.getRuntime().exec(string2).waitFor();
        
    }

    public static void WriteTo(InputStream inputStream, FileOutputStream fileOutputStream) throws IOException {
        byte[] arrby = new byte[2048];
        int n;
        while ((n = inputStream.read(arrby, 0, 2048)) != -1) {
            fileOutputStream.write(arrby, 0, n);
        }
        return;
    }

    public static void startUDP(Context context, boolean bl) throws IOException, InterruptedException {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(context.getFilesDir().getAbsolutePath());
        stringBuilder.append("/udp");
        final String string2 = stringBuilder.toString();
        if (bl) {
            new Thread(new Runnable(){

                public void run() {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append(string2);
                    stringBuilder.append(" --listen-addr 127.0.0.1:7300");
                    String string22 = stringBuilder.toString();
                    StringBuilder stringBuilder2 = new StringBuilder();
                    stringBuilder2.append("SALIDA ");
                    stringBuilder2.append(string22);
                    //Log.e((String)"DNS DEAMON", (String)stringBuilder2.toString());
                    try {
                        proceso = Runtime.getRuntime().exec(string22);
                        return;
                    }
                    catch (IOException iOException) {
                        //Log.e((String)"FAILED START", (String)iOException.getMessage());
                        return;
                    }
                }
            }).start();
            return;
        }
        proceso.destroy();
    }

	public static boolean runCommand(String command)
	{
		Process process = null;
		DataOutputStream os = null;
		try
		{
			process = Runtime.getRuntime().exec("sh");
			try
			{

				os = new DataOutputStream(process.getOutputStream());
				os.writeBytes(command + "\n");
				os.writeBytes("exit\n");
				os.flush();
				process.waitFor();
				return true;
			}
			catch (Exception e)
			{

				Log.d("", e.getMessage());
				return false;

			}
			finally
			{
				try
				{
					if (os != null)
					{
						os.close();
					}
					process.destroy();
				}
				catch (IOException e)
				{
					// nothing
				}
			}
		}
		catch (IOException e)
		{
			Log.d("", e.getMessage());
			//SnapData.getSnapData().addLog(e.getMessage());
			return false;

		}
		finally
		{
			try
			{
				if (os != null)
				{
					os.close();
				}
				process.destroy();
			}
			catch (Exception e)
			{
				// nothing
			}
		}

	}
}


