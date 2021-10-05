package mph.trunksku.apps.myssh.view;
import android.content.*;
import android.support.v7.app.*;
import android.view.*;
import android.widget.*;
import com.masfha.id.*;
import mph.trunksku.apps.myssh.*;
import mph.trunksku.apps.myssh.db.*;
import mph.trunksku.apps.myssh.fragment.*;

public class VPNModeDialog
{

	private AlertDialog.Builder adb;

	private LayoutInflater inflater;

	private MRadioGroup vpnmode;

	private SharedPreferences sp;

	private DataBaseHelper db;

	
	private RadioGroup vpntunnelmode;
	
	public VPNModeDialog(final Context c){
		db = new DataBaseHelper(c);
		sp = ApplicationBase.getSharedPreferences();
		inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    final View inflate = inflater.inflate(R.layout.layout_vpnmode, (ViewGroup) null);
		
		
		
		vpntunnelmode = (RadioGroup) inflate.findViewById(R.id.vpn_mode_tunnel);
		vpnmode = (MRadioGroup) inflate.findViewById(R.id.mode_group);
		
		vpntunnelmode.check(sp.getInt("VPNTunMod", R.id.ssh));
		vpntunnelmode.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(RadioGroup p1, int p2)
				{
					// TODO: Implement this method
					if(p2 == R.id.ovpn){
						((RadioButton) inflate.findViewById(R.id.mode_3)).setEnabled(false);
						if(vpnmode.getCheckedItemId() == R.id.mode_3){
							vpnmode.check(((RadioButton) inflate.findViewById(R.id.mode_1)));
						}
					}else{
						((RadioButton) inflate.findViewById(R.id.mode_3)).setEnabled(true);
					}
				}
		});
		if(sp.getInt("VPNTunMod", R.id.ssh) == R.id.ovpn){
			((RadioButton) inflate.findViewById(R.id.mode_3)).setEnabled(false);
		}
		vpnmode.check(((RadioButton) inflate.findViewById(sp.getInt("VPNMod", R.id.mode_1)))); //sp.getInt("VPNMod", R.id.mode_1));
		vpnmode.setOnCheckedChangeListener(new MRadioGroup.OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(MRadioGroup p1, int p2)
				{
					/*modeTypeList.clear();
					if(p2 == R.id.mode_1) {
						modeTypeList.add("HTTP > Custom Payload");
						modeTypeList.add("HTTP Direct");
					}else if(p2 == R.id.mode_2) {
						modeTypeList.add("SSL V1");
						modeTypeList.add("SSL V2");
					}else{
						modeTypeList.add("Direct Only");
						modeTypeList.add("Direct > Custom Payload");
					}
					vpnAdapter.notifyDataSetChanged();*/
					
					// TODO: Implement this method
					
				}
			});
		adb = new AlertDialog.Builder(c);
	    adb.setTitle("MODO VPN");
	    adb.setMessage("Seleccione el modo VPN");
		adb.setView(inflate, 40, 0, 40, 0);
		adb.setPositiveButton("GUARDAR", new DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface p1, int p2)
				{
					// TODO: Implement this method
					sp.edit().putInt("VPNTunMod", vpntunnelmode.getCheckedRadioButtonId()).commit();
					sp.edit().putInt("VPNMod", vpnmode.getCheckedItemId()).commit();
					if(sp.getBoolean("custom_payload_key", false)){
						//networkSpin.setEnabled(false);
					}
					HomeFragment.Vpnmod.clear();
					if(sp.getInt("VPNMod", R.id.mode_1) == R.id.mode_1){
						sp.edit().putInt("ModeVersionSpin", 0).commit();
						HomeFragment.Vpnmod =HomeFragment.utils.parseNetworkSSH(HomeFragment.networkList, db.getData());
					}else if(sp.getInt("VPNMod", R.id.mode_1) == R.id.mode_2){
						HomeFragment.Vpnmod =HomeFragment.utils.parseNetworkSSL(HomeFragment.networkList, db.getData());
					}else if(sp.getInt("VPNMod", R.id.mode_1) == R.id.mode_3){
						sp.edit().putInt("ModeVersionSpin", 0).commit();
						HomeFragment.Vpnmod =HomeFragment.utils.parseNetworkSSH(HomeFragment.networkList, db.getData());
					}
					HomeFragment.networkSpin.setAdapter(new ArrayAdapter(c, android.R.layout.simple_spinner_dropdown_item,HomeFragment.Vpnmod));
					HomeFragment.vpnmode_refresh();
				}
			});
		adb.setNegativeButton("CANCELAR", null);
	}
	
	public void show(){
		adb.create().show();
	}
}
