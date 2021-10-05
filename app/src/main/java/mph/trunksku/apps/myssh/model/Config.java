package mph.trunksku.apps.myssh.model;
import java.util.*;
import de.blinkt.openvpn.*;

public interface Config 
{
	public ArrayList parseServer(ArrayList arraylist, String data, int type);
	public ArrayList parseNetworkSSH(ArrayList arraylist, String data);
	public ArrayList parseNetworkSSL(ArrayList arraylist, String data);
	public void parseSelectedServer(int position, String data) throws Exception;
	public void parseSelectedNetwork(int position, String data) throws Exception;
	public VpnProfile loadVpnProfile(String config);
	public String getOvpn();
	public String getPayload();
	public String getHost();
	public String getPort();
	public String getSSHHost();
	public String getSSHPort();
	public String getSshUsername();
	public String getSshPassword();
	public String getUa();
	public int getVPNMod();
	public int getVPNTunMod();
}
