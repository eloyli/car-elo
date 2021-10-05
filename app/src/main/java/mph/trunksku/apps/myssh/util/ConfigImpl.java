package mph.trunksku.apps.myssh.util;
import android.content.*;
import android.os.*;
import android.text.*;
import de.blinkt.openvpn.*;
import de.blinkt.openvpn.core.*;
import java.io.*;
import java.util.*;
import com.masfha.id.*;
import mph.trunksku.apps.myssh.*;
import mph.trunksku.apps.myssh.logger.*;
import mph.trunksku.apps.myssh.model.*;
import org.json.*;

public class ConfigImpl implements Config
{
	private Context mContext;

	private Context context;

	private SharedPreferences dsp;

	@Override
	public String getUa()
	{
		// TODO: Implement this method
		String property = System.getProperty("http.agent");
        return property == null ? "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/44.0.2403.130 Safari/537.36" : property;
	}


	@Override
	public String getSshUsername()
	{
		// TODO: Implement this method
		return sp.getString("xUser", "");
	}

	@Override
	public String getSshPassword()
	{
		// TODO: Implement this method
		return sp.getString("xPass", "");
	}


	@Override
	public String getSSHHost()
	{
		// TODO: Implement this method
		return sp.getString("SSHHost", "");
	}

	@Override
	public String getSSHPort()
	{
		// TODO: Implement this method
		if (sp.getInt("VPNTunMod", R.id.ssh) == R.id.ssh)
		{
			if (sp.getInt("VPNMod", R.id.mode_1) == R.id.mode_1)
			{
				return sp.getString("SSHPort", "22");
			}
			else if (sp.getInt("VPNMod", R.id.mode_1) == R.id.mode_2)
			{
				return sp.getString("SSLPort", "443");
			}
			else
			{
				return sp.getString("SSHPort", "22");
			}
		}else{
			if (sp.getInt("VPNMod", R.id.mode_1) == R.id.mode_2)
			{
				return sp.getString("OVPNSSLPort", "443");
			}
			else 
			{
				return sp.getString("OVPNPort", "1147");
			}
		}
	}

	@Override
	public String getPayload()
	{
		// TODO: Implement this method
		if (sp.getBoolean("custom_payload_key", false))
		{
			if (sp.getInt("VPNMod", R.id.mode_1) == R.id.mode_1)
			{
				return sp.getString("custom_payload", "");
			}
			else if (sp.getInt("VPNMod", R.id.mode_1) == R.id.mode_2)
			{
				return sp.getString("custom_sni", "");
			}
			else
			{
				return "";
			}
		}
		else
		{
			return sp.getString("Payload", "");
		}
	}

	@Override
	public String getHost()
	{
		// TODO: Implement this method
		if (sp.getBoolean("custom_payload_key", false))
		{
			if (sp.getBoolean("custom_remote_key", false))
			{
				if (sp.getString("custom_remote", "").isEmpty())
				{
					return sp.getString("SSHHost", "");
				}
				else
				{
					return sp.getString("custom_remote", "").split(":")[0];
				}
			}
			else
			{
				return sp.getString("SSHHost", "");
			}
		}
		else if (TextUtils.isEmpty(sp.getString("CustomSquid", "")))
		{
			return sp.getString("SSHHost", "");
		}
		else
		{
			return sp.getString("CustomSquid", "").split(":")[0];
		}
	}

	@Override
	public String getPort()
	{
		// TODO: Implement this method
		if (sp.getBoolean("custom_payload_key", false))
		{
			if (sp.getBoolean("custom_remote_key", false))
			{
				if (sp.getString("custom_remote", "").isEmpty())
				{
					return sp.getString("DefSquidPort", "8080");
				}
				else
				{
					return sp.getString("custom_remote", "").split(":")[1];
				}
			}
			else
			{
				return sp.getString("DefSquidPort", "8080");
			}
		}
		else if (TextUtils.isEmpty(sp.getString("CustomSquid", "")))
		{
			return sp.getString("DefSquidPort", "8080");
		}
		else
		{
			return sp.getString("CustomSquid", "").split(":")[1];
		}
	}

	@Override
	public String getOvpn()
	{
		// TODO: Implement this method
		String config = "";
		String ovpn = sp.getString("SampleOvpn", "");
        try
		{
			config += ovpn;
			if (sp.getInt("VPNMod", R.id.mode_1) == R.id.mode_1)
			{
				config += "\nremote " + getSSHHost() + " " + getSSHPort();
				//config += "\nremote 127.0.0.1 8083\n";
				//config += "http-proxy-retry\nhttp-proxy 127.0.0.1 1699\n";
				//config += "route " + getSSHHost() + " 255.255.255.255 net_gateway\n";
			}else if (sp.getInt("VPNMod", R.id.mode_1) == R.id.mode_2){
				config += "\nremote 127.0.0.1 8083\n";
				//config += "route " + getSSHHost() + " 255.255.255.255 net_gateway\n";
			}
			
			return config;
        }
		catch (Exception ignore)
		{
			Log.d("", ignore.getMessage());
            //Toast.makeText(this, "发生错误,请联系开发者", Toast.LENGTH_LONG).show();
			return null;
        }
	}
	
	@Override
	public VpnProfile loadVpnProfile(String config)
	{
		// TODO: Implement this method
		try{
			ConfigParser cp = new ConfigParser();
			cp.parseConfig(new StringReader(config));
            VpnProfile vp = cp.convertProfile();// 解析.ovpn
            vp.mName = sp.getString("SSHName", "");
            if (vp.checkProfile(context) != R.string.no_error_found)
			{
                throw new RemoteException(context.getString(vp.checkProfile(context)));
            }
            vp.mProfileCreator = context.getPackageName();
			//vp.mUseCustomConfig = true;
			//vp.mCustomConfigOptions = "http-proxy-retry\nhttp-proxy 127.0.0.1 1699\n";
			vp.mUsername = getSshUsername();
            vp.mPassword = getSshPassword();
			if (dsp.getBoolean("dns_forwarder_key", true)) {
				String dns_1 = dsp.getBoolean("enable_custom_dns_key", false) ? dsp.getString("custom_primary_dns", "8.8.8.8") : "8.8.8.8";
				String dns_2 = dsp.getBoolean("enable_custom_dns_key", false) ? dsp.getString("custom_secondary_dns", "8.8.4.4") : "8.8.4.4";
                vp.mOverrideDNS = true;
                vp.mSearchDomain = "";
                vp.mDNS1 = dns_1;
                vp.mDNS2 = dns_2;
            }
           /* if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("custom", false)) {
                vp.mCustomRoutes = "0.0.0.0/0";
                vp.mExcludedRoutes = getSSHHost();
                vp.mUseCustomConfig = true;
                vp.mCustomConfigOptions = new StringBuffer().append(vp.mCustomConfigOptions).append("http-proxy 127.0.0.1 1699").toString();
            } else */
			if (sp.getInt("VPNMod", R.id.mode_1) == R.id.mode_2) {
                vp.mUseDefaultRoute = false;
                vp.mCustomRoutes = "0.0.0.0/0";
                vp.mExcludedRoutes = getSSHHost();
                vp.mUseCustomConfig = true;
                vp.mCustomConfigOptions = new StringBuffer().append(vp.mCustomConfigOptions).append("http-proxy 127.0.0.1 8083").toString();
            } else if (sp.getInt("VPNMod", R.id.mode_1) == R.id.mode_1) {
                vp.mUseDefaultRoute = false;
                vp.mCustomRoutes = "0.0.0.0/0";
                vp.mExcludedRoutes = getSSHHost();
                vp.mUseCustomConfig = true;
                vp.mCustomConfigOptions = new StringBuffer().append(vp.mCustomConfigOptions).append("http-proxy 127.0.0.1 8083").toString();
            } 
            ProfileManager.setTemporaryProfile(context, vp);
			return vp;
		}catch(Exception e){
			return null;
		}
	}

	@Override
	public void parseSelectedServer(int position, String data) throws Exception
	{
		// TODO: Implement this method
		JSONArray jarr = new JSONObject(data).getJSONArray("Servers");
		JSONArray jarr2 = null;
		switch (sp.getInt("ServerTypeSpin", 0))
		{
			case 0:
				jarr2 = jarr.getJSONObject(0).getJSONArray("Premium");
				break;
			case 1:
				jarr2 = jarr.getJSONObject(1).getJSONArray("VIP");
				break;
			case 2:
				jarr2 = jarr.getJSONObject(2).getJSONArray("Other");
				break;
		}
		jarr2 = JsonUtils.sort(jarr2, JsonUtils.getComparator(mContext,"Name",1));
		
		JSONObject obj = jarr2.getJSONObject(position);
		sp.edit().putString("SSHName", obj.getString("Name")).commit();
		sp.edit().putString("SSHHost", obj.getString("SSHHost")).commit();
		sp.edit().putString("SSHPort", obj.getString("SSHPort")).commit();
		sp.edit().putString("SSLPort", obj.getString("SSLPort")).commit();
		sp.edit().putString("OVPNPort", obj.getString("OVPNPort")).commit();
		sp.edit().putString("OVPNSSLPort", obj.getString("OVPNSSLPort")).commit();
	}

	@Override
	public void parseSelectedNetwork(int position, String data) throws Exception
	{
		// TODO: Implement this method
		JSONArray jarr = new JSONObject(data).getJSONArray("Networks");
		JSONArray jarr2 = null;
		if (sp.getInt("VPNMod", R.id.mode_1) == R.id.mode_1)
		{
			jarr2 = jarr.getJSONObject(1).getJSONArray("INJECT");
		}
		else if (sp.getInt("VPNMod", R.id.mode_1) == R.id.mode_2)
		{
			jarr2 = jarr.getJSONObject(0).getJSONArray("SSL");
		}
		jarr2 = JsonUtils.sort(jarr2, JsonUtils.getComparator(mContext,"Name",1));
		
		JSONObject obj = jarr2.getJSONObject(position);
		sp.edit().putString("NetName", obj.getString("Name")).commit();
		sp.edit().putString("NetInfo", obj.getString("Info")).commit();
		if (sp.getInt("VPNMod", R.id.mode_1) == R.id.mode_1)
		{
			sp.edit().putString("Payload", obj.getString("Payload")).commit();
		}
		else
		{
			sp.edit().putString("Payload", obj.getString("Sni")).commit();
		}
		sp.edit().putString("CustomSquid", obj.getString("CustomSquid")).commit();
	}

	@Override
	public ArrayList parseServer(ArrayList<HashMap<String, String>> arraylist, String data, int type)
	{
		// TODO: Implement this method
		try
		{
			JSONArray jarr = new JSONObject(data).getJSONArray("Servers");
			JSONArray jarr2 = null;
			String xtype = "[PREM] ";
			switch (type)
			{
				case 0:
					jarr2 = jarr.getJSONObject(0).getJSONArray("Premium");
					xtype = "[PREM] ";
					break;
				case 1:
					jarr2 = jarr.getJSONObject(1).getJSONArray("VIP");
					xtype = "[VIP] ";
					break;
				case 2:
					jarr2 = jarr.getJSONObject(2).getJSONArray("Other");
					xtype = "[PRIV] ";
					break;
			}
			jarr2 = JsonUtils.sort(jarr2, JsonUtils.getComparator(mContext,"Name",1));
			for (int i = 0; i < jarr2.length(); i++)
			{
				JSONObject obj = jarr2.getJSONObject(i);
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("COUNTRY", obj.getString("Name"));
				map.put("FLAG", obj.getString("Flag"));

				arraylist.add(map);
			}
			return arraylist;
		}
		catch (Exception e)
		{
			return arraylist;
		}
	}

	@Override
	public ArrayList parseNetworkSSL(ArrayList arraylist, String data)
	{
		// TODO: Implement this method
		try
		{
			JSONArray jarr = new JSONObject(data).getJSONArray("Networks");
			JSONArray jarr2 = jarr.getJSONObject(0).getJSONArray("SSL");
			jarr2 = JsonUtils.sort(jarr2, JsonUtils.getComparator(mContext,"Name",1));
			
			for (int i = 0; i < jarr2.length(); i++)
			{
				JSONObject obj = jarr2.getJSONObject(i);
				arraylist.add(obj.getString("Name"));
			}
			return arraylist;
		}
		catch (Exception e)
		{
			return arraylist;
		}
	}

	@Override
	public ArrayList parseNetworkSSH(ArrayList arraylist, String data)
	{
		// TODO: Implement this method
		try
		{
			JSONArray jarr = new JSONObject(data).getJSONArray("Networks");
			JSONArray jarr2 = jarr.getJSONObject(1).getJSONArray("INJECT");
			jarr2 = JsonUtils.sort(jarr2, JsonUtils.getComparator(mContext,"Name",1));
			
			for (int i = 0; i < jarr2.length(); i++)
			{
				JSONObject obj = jarr2.getJSONObject(i);
				arraylist.add(obj.getString("Name"));
			}
			return arraylist;
		}
		catch (Exception e)
		{
			return arraylist;
		}
	}

	@Override
	public int getVPNMod()
	{
		// TODO: Implement this method
		return sp.getInt("VPNMod", R.id.mode_1);
	}

	@Override
	public int getVPNTunMod()
	{
		// TODO: Implement this method
		return sp.getInt("VPNTunMod", R.id.ssh);
	}
	
	private SharedPreferences sp;
	public ConfigImpl()
	{
		sp = ApplicationBase.getSharedPreferences();
		dsp = ApplicationBase.getDefSharedPreferences();
		context = ApplicationBase.getContext();
	}
}
