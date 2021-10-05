package de.blinkt.openvpn;

import android.content.*;
import android.os.*;
import de.blinkt.openvpn.core.*;
import java.io.*;
import com.masfha.id.*;
import mph.trunksku.apps.myssh.*;
import mph.trunksku.apps.myssh.logger.*;

public class OpenVpnApi
{

	private static SharedPreferences sp;

	private static IOpenVPNServiceInternal mService;
    private static ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            mService = IOpenVPNServiceInternal.Stub.asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mService = null;
        }
    };
	


    /**
     * @param inlineConfig 一般都保存在文件中，通过读取文件获取配置信息
     * @param userName     某些ovpn的连接方式需要用户名和密码，可以为空
     * @param pw           某些ovpn的连接方式需要用户名和密码，可以为空
     * @throws RemoteException
     */
	public static void stopVpn(Context context)
	{
		Intent intent = new Intent(context, OpenVPNService.class);
        intent.setAction(OpenVPNService.START_SERVICE);
        context.bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
		ProfileManager.setConntectedVpnProfileDisconnected(context);
		if (mService != null) {
			try {
				mService.stopVPN(false);
			} catch (RemoteException e) {
//                VpnStatus.logException(e);
			}
		}
	}
    public static void startVpn(Context context, String inlineConfig, String userName, String pw)
	{
		try
		{
            startVpnInternal(context, inlineConfig, userName, pw);
		}
		catch (Exception e)
		{
			Log.d("", e.getMessage());
		}
    }

    static void startVpnInternal(Context context, String inlineConfig, String userName, String pw) throws RemoteException
	{
        ConfigParser cp = new ConfigParser();
		sp = ApplicationBase.getSharedPreferences();
		try
		{
			Log.d("", inlineConfig);
            cp.parseConfig(new StringReader(inlineConfig));
            VpnProfile vp = cp.convertProfile();// 解析.ovpn
            vp.mName = sp.getString("SSHName", "");
            if (vp.checkProfile(context) != R.string.no_error_found)
			{
                throw new RemoteException(context.getString(vp.checkProfile(context)));
            }
            vp.mProfileCreator = context.getPackageName();
			//vp.mUseCustomConfig = true;
			//vp.mCustomConfigOptions = "http-proxy-retry\nhttp-proxy 127.0.0.1 1699\n";
			vp.mUsername = userName;
            vp.mPassword = pw;
            ProfileManager.setTemporaryProfile(context, vp);
            VPNLaunchHelper.startOpenVpn(vp, context);
        }
		catch (IOException | ConfigParser.ConfigParseError e)
		{
            throw new RemoteException(e.getMessage());
        }
    }
}
