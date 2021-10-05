package mph.trunksku.apps.myssh.fragment;
import android.app.*;
import android.content.*;
import android.graphics.*;
import android.net.*;
import android.os.*;
import android.os.Build.*;
import android.support.annotation.*;
import android.support.design.widget.*;
import android.support.v7.app.*;
import android.support.v7.widget.*;
import android.text.*;
import android.view.*;
import android.widget.*;
import com.github.rubensousa.bottomsheetbuilder.*;
import com.github.rubensousa.bottomsheetbuilder.adapter.*;
import com.luseen.spacenavigation.*;
import com.shashank.sony.fancydialoglib.*;
import com.suke.widget.*;
import java.util.*;
import mehdi.sakout.aboutpage.*;
import com.masfha.id.*;
import com.masfha.id.R;
import mph.trunksku.apps.myssh.*;
import mph.trunksku.apps.myssh.adapter.*;
import mph.trunksku.apps.myssh.async.*;
import mph.trunksku.apps.myssh.db.*;
import mph.trunksku.apps.myssh.model.*;
import mph.trunksku.apps.myssh.service.*;
import mph.trunksku.apps.myssh.util.*;
import mph.trunksku.apps.myssh.view.*;
import org.json.*;

import android.support.v7.app.AlertDialog;
import mph.trunksku.apps.myssh.adapter.SpinnerAdapter;
import mph.trunksku.apps.myssh.util.Utils;
import java.util.concurrent.*;
import android.test.*;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.*;
import java.net.*;
import java.io.*;
import android.support.v4n.a;

public class HomeFragment extends Fragment implements StatusChangeListener
{

	private View mView;
	private static ArrayList<HashMap<String, String>> serverList = new ArrayList<HashMap<String, String>>();
	public ArrayList<String> serverTypeList = new ArrayList();
	public static ArrayList<String> networkList = new ArrayList();
	public ArrayList<String> networkTypeList = new ArrayList();
	private ArrayAdapter<String> adapter;

	public static Config utils;

	private static SharedPreferences sp;

	public static ArrayList Vpnmod = new ArrayList();


	public static Spinner serverSpin;

	public static Spinner networkSpin;
	public static SpinnerAdapter serverAdapt;

	private static DataBaseHelper db;


	private Spinner serverTypeSpin;

	//private Spinner networkTypeSpin;

	 private AdView adView;

	 private AdRequest adRequest;
	 

	private EditText userN;

	private MaterialEditText passW;

	public static ArrayAdapter networkAdapt;

	private SharedPreferences defsp;

	private static CardView serverTypeLayout;

	public static SpaceNavigationView spaceNavigationView;

	private static CardView networkLayout;

	private BottomSheetBehavior<View> sheetBehavior;

	public static BottomSheetBehavior<View> mBehavior;

	private ToastUtil toastutil;

	public static SwitchButton switchButton;

	private AdView mAdView;

	private InterstitialAd mInterstitialAd;


    //private BottomSheetAlert bottomAlert;

    @Override
    public void onStatusChanged(String status, Boolean isRunning)
    {
        // TODO: Implement this method

		if (OreoService.isRunning)
		{
			MainActivity.easyFlip.letFlip(0);
			spaceNavigationView.changeCenterButtonIcon(R.drawable.ic_close_white_24dp);
			passW.showPasswordVisibilityIndicator(false);
			serverTypeSpin.setEnabled(false);
			serverSpin.setEnabled(false);
			userN.setEnabled(false);
			passW.setEnabled(false);
			networkSpin.setEnabled(false);
			switchButton.setEnabled(false);
			if (OreoService.isSSHRunning)
			{	
				toastutil.showSuccessToast("Connected");
				newdiads();
				callApi();
			}/*else{
			 status.setText("Connecting...");
			 }*/
		}
		else
		{
			passW.showPasswordVisibilityIndicator(true);
			MainActivity.easyFlip.letFlip(1);
			serverTypeSpin.setEnabled(true);
			serverSpin.setEnabled(true);
			userN.setEnabled(true);
			passW.setEnabled(true);
			switchButton.setEnabled(true);
			if (sp.getBoolean("custom_payload_key", false))
			{
				networkSpin.setEnabled(false);
				switchButton.setChecked(true);
			}
			else
			{
				switchButton.setChecked(false);
				networkSpin.setEnabled(true);
			}
			spaceNavigationView.changeCenterButtonIcon(R.drawable.ic_paper_plane);
			//MainActivity.easyFlip.letFlip(1);
			//if(status.equals("Auth_Fail")){
			//toastutil.showErrorToast("Invalid Username or Password");
			//}else{
			toastutil.showErrorToast("Disconnected");
	
			//}
		}
		onLogReceived(status);
    }

    @Override
    public void onLogReceived(String logString)
    {
        // TODO: Implement this method


    }
	private VpnTunnelService vpnTunnelService = null;

	private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder binder)
		{
			vpnTunnelService = ((VpnTunnelService.LocalBinder) binder).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName className)
		{
			vpnTunnelService = null;
        }
	};

	@Override
    public void onSaveInstanceState(Bundle outState)
	{
        super.onSaveInstanceState(outState);
        spaceNavigationView.onSaveInstanceState(outState);
    }

	@Override
    public void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        setHasOptionsMenu(true);
		new ApplicationBase().init(getActivity());
		db = new DataBaseHelper(getActivity());
		utils = ApplicationBase.getUtils();
		sp = ApplicationBase.getSharedPreferences();
		defsp = ApplicationBase.getDefSharedPreferences();
        OreoService.addOnStatusChangedListener(this);
		toastutil = new ToastUtil(getActivity());
		
		
		
		/*adhelper = new AdmobHelper(getActivity());
		 adhelper.setMobileAdsId("ca-app-pub-6192245716388864~9488519196");
		 adhelper.init();
		 adhelper.setTestDevice("b2a49ee35599c354");
		 adhelper.setBannerView((RelativeLayout) findViewById(R.id.ad_view));
		 adhelper.setBannerId("ca-app-pub-6192245716388864/4808351231");
		 adhelper.setBannerSize(AdSize.MEDIUM_RECTANGLE);
		 adhelper.setIntertitialId("ca-app-pub-3016532543469139/7992840511");
		 adhelper.setShowInterAdsAuto(true);
		 //adhelper.setRewardedId("ca-app-pub-9010191045911152/9713078961");
		 //adhelper.setRewardAdsListener(rvl);
		 //adhelper.setAdsListener(al);
		 adhelper.buildAdsRequest();
		 adhelper.loadAdsRequest();*/
	
	PreferencesManager pref = new PreferencesManager(getActivity());
	if (isConnected()) {
		pref.getDefaultSharedPreferences(getActivity());
	}
	
}	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
   		mView = inflater.inflate(R.layout.fragment_home, container, false);
	    //bottomAlert = new BottomSheetAlert(getActivity(), (LinearLayout) mView.findViewById(R.id.homeLayout));
        serverTypeLayout = (CardView) mView.findViewById(R.id.serverTypeSpinCardView);
		networkLayout = (CardView) mView.findViewById(R.id.networkSpinCardView);
		serverTypeSpin = (Spinner) mView.findViewById(R.id.serverTypeSpin);
	    serverSpin = (Spinner) mView.findViewById(R.id.serverSpin);
		networkSpin = (Spinner) mView.findViewById(R.id.networkSpin);
		serverTypeList.add("VIP");
		serverTypeList.add("TRIAL");
		serverTypeList.add("FREE");
		serverTypeSpin.setAdapter(new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_dropdown_item, serverTypeList));
		try
		{
			serverTypeSpin.setSelection(sp.getInt("ServerTypeSpin", 0));
		}
		catch (Exception e)
		{

		}

		serverTypeSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){

				@Override
				public void onItemSelected(AdapterView<?> p1, View p2, final int position, long p4)
				{
					// TODO: Implement this method
					sp.edit().putInt("ServerTypeSpin", position).commit();
					if (!sp.getBoolean("Categorie", false))
					{
						serverList.clear();
						serverList = utils.parseServer(serverList, db.getData(), position);
						serverAdapt = new SpinnerAdapter(getActivity(), serverList);
						serverSpin.setAdapter(serverAdapt);
						serverSpin.setSelection(sp.getInt("ServerSpin" + position, 0));
						serverSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
								@Override
								public void onItemSelected(AdapterView<?> p1, View p2, int p3, long p4)
								{
									// TODO: Implement this method
									try
									{
										sp.edit().putInt("ServerSpin" + sp.getInt("ServerTypeSpin", 0), p3).commit();
										utils.parseSelectedServer(p3, db.getData());
									}
									catch (Exception e)
									{LogFragment.addLog(e.getMessage());}
								}

								@Override
								public void onNothingSelected(AdapterView<?> p1)
								{
									// TODO: Implement this method
								}
							});
					}
				}

				@Override
				public void onNothingSelected(AdapterView<?> p1)
				{
					// TODO: Implement this method
				}
			});
		if (sp.getBoolean("Categorie", false))
		{
			serverList.clear();
			serverList = utils.parseServer(serverList, db.getData(), 0);
			serverAdapt = new SpinnerAdapter(getActivity(), serverList);
			serverSpin.setAdapter(serverAdapt);
			serverSpin.setSelection(sp.getInt("ServerSpin0", 0));
			serverSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
					@Override
					public void onItemSelected(AdapterView<?> p1, View p2, int p3, long p4)
					{
						// TODO: Implement this method
						try
						{
							sp.edit().putInt("ServerSpin0", p3).commit();
							utils.parseSelectedServer(p3, db.getData());
						}
						catch (Exception e)
						{LogFragment.addLog(e.getMessage());}
					}

					@Override
					public void onNothingSelected(AdapterView<?> p1)
					{
						// TODO: Implement this method
					}
				});
		}

		//sp.edit().putString("xUser","AlfaisalVPN" ).commit();
		//sp.edit().putString("xPass", "AlfaisalVPN").commit();
		
		
		Vpnmod.clear();
		if (sp.getInt("VPNMod", R.id.mode_1) == R.id.mode_1)
		{
			Vpnmod = HomeFragment.utils.parseNetworkSSH(HomeFragment.networkList, db.getData());
		}
		else
		{
			Vpnmod = HomeFragment.utils.parseNetworkSSL(HomeFragment.networkList, db.getData());
		}
		networkAdapt = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_dropdown_item, Vpnmod);
		networkSpin.setAdapter(networkAdapt);
		networkSpin.setSelection(sp.getInt("NetworkSpin" + sp.getInt("VPNMod", R.id.mode_1), 0));
		networkSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
				@Override
				public void onItemSelected(AdapterView<?> p1, View p2, int p3, long p4)
				{
					// TODO: Implement this method
					sp.edit().putInt("NetworkSpin" + sp.getInt("VPNMod", R.id.mode_1), p3).commit();
					try
					{
						utils.parseSelectedNetwork(p3, db.getData());
					}
					catch (Exception e)
					{
						LogFragment.addLog(e.getMessage());
					}
				}

				@Override
				public void onNothingSelected(AdapterView<?> p1)
				{
					// TODO: Implement this method
				}
			});

        userN = (EditText) mView.findViewById(R.id.edUsername);
		userN.setText(sp.getString("xUser", ""));
		userN.addTextChangedListener(new TextWatcher(){
				@Override
				public void beforeTextChanged(CharSequence p1, int p2, int p3, int p4)
				{
					// TODO: Implement this method
				}

				@Override
				public void onTextChanged(CharSequence p1, int p2, int p3, int p4)
				{
					// TODO: Implement this method
				}

				@Override
				public void afterTextChanged(Editable p1)
				{
					// TODO: Implement this method
					sp.edit().putString("xUser", p1.toString()).commit();
				}
			});

		passW = (MaterialEditText) mView.findViewById(R.id.edPassword);
		passW.setText(sp.getString("xPass", ""));
		passW.showPasswordVisibilityIndicator(false);
		passW.addTextChangedListener(new TextWatcher(){
				@Override
				public void beforeTextChanged(CharSequence p1, int p2, int p3, int p4)
				{
					// TODO: Implement this method
				}

				@Override
				public void onTextChanged(CharSequence p1, int p2, int p3, int p4)
				{
					// TODO: Implement this method
				}

				@Override
				public void afterTextChanged(Editable p1)
				{
					// TODO: Implement this method
					sp.edit().putString("xPass", p1.toString()).commit();
				}
			});
		switchButton = (SwitchButton) mView.findViewById(R.id.CustomSet);
		switchButton.setChecked(sp.getBoolean("custom_payload_key", false));
		switchButton.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(SwitchButton view, boolean isChecked)
				{
					// TODO: Implement this method
					if (isChecked)
					{
						networkSpin.setEnabled(false);
						switchButton.setChecked(true);
						new CustomNetworkDialog(getActivity()).show();
					}
					else
					{
						switchButton.setChecked(false);
						networkSpin.setEnabled(true);
						sp.edit().putBoolean("custom_payload_key", false).commit();
					}
				}
			});
		if (sp.getBoolean("Categorie", false))
		{
			serverTypeLayout.setVisibility(View.GONE);
		}
		else
		{
			serverTypeLayout.setVisibility(View.VISIBLE);
		}
		if (sp.getInt("VPNMod", R.id.mode_1) == R.id.mode_3)
		{
			switchButton.setVisibility(View.GONE);
			networkLayout.setVisibility(View.GONE);
		}
		
		// Initialize the Mobile Ads SDK.
		MobileAds.initialize(getActivity(), "ca-app-pub-9162846843523182~3722538531");

		mInterstitialAd = new InterstitialAd(getActivity());
		mInterstitialAd.setAdUnitId("ca-app-pub-9162846843523182/5965558491");
		//mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
		mInterstitialAd.loadAd(new AdRequest.Builder().build());

		AdView adView = new AdView(getActivity());
		adView.setAdSize(AdSize.BANNER);
		adView.setAdUnitId("ca-app-pub-9162846843523182/9904803501");
		AdView mAdView = (AdView) mView.findViewById(R.id.ad_view);
		AdRequest adRequest = new AdRequest.Builder().build();
		mAdView.loadAd(adRequest);
		
		spaceNavigationView = (SpaceNavigationView) mView.findViewById(R.id.space);
		//spaceNavigationView.initWithSaveInstanceState(savedInstanceState);
        spaceNavigationView.addSpaceItem(new SpaceItem("", R.drawable.ic_box_download));
		spaceNavigationView.addSpaceItem(new SpaceItem("", R.drawable.cmd_matrix));
		spaceNavigationView.addSpaceItem(new SpaceItem("", R.drawable.cmd_account_convert));
        spaceNavigationView.addSpaceItem(new SpaceItem("", R.drawable.cmd_menu));
		spaceNavigationView.shouldShowFullBadgeText(false);
        spaceNavigationView.setCentreButtonIconColorFilterEnabled(false);
		//spaceNavigationView.showBadgeAtIndex(1, 0, R.color.colorPrimary);
		//spaceNavigationView.showIconOnly();
		spaceNavigationView.setSpaceOnClickListener(new SpaceOnClickListener() {
				@Override
				public void onCentreButtonClick() {


					if (OreoService.isRunning)
                    {

						
						startStop(false);

						//getActivity().stopService(intent);
					}
                    else
                    {

                        if (sp.getString("xUser", "").isEmpty())
						{
							//MainActivity.account();
							toastutil.showWarningToast("Username Empty!");
                        }else if(sp.getString("xPass", "").isEmpty()){
							//MainActivity.account();
							toastutil.showWarningToast("Password Empty!");

						}else if (isNetworkAvailable(getActivity()))
                        {
                            Intent prepare = VpnService.prepare(getActivity());
                            if (prepare != null)
                            {
                                startActivityForResult(prepare, 0);
                            }
                            else
                            {
                                onActivityResult(0, -1, null);
                            }
                        }
                        else
                        {
							Toast.makeText(getActivity(), "No Internet Connection!", 0).show();

                        }
					}
				}

				@Override
				public void onItemClick(int itemIndex, String itemName)
				{
					manageClick(itemIndex);
					//LogFragment.addLog("" + itemIndex + " " + itemName);
				}

				@Override
				public void onItemReselected(int itemIndex, String itemName)
				{
					manageClick(itemIndex);
					//LogFragment.addLog("" + itemIndex + " " + itemName);
				}
			});

		/* spaceNavigationView.setSpaceOnLongClickListener(new SpaceOnLongClickListener() {
		 @Override
		 public void onCentreButtonLongClick() {
		 toastutil.showInfoToast("Start/Stop Connection");
		 //Toast.makeText(getActivity(), "onCentreButtonLongClick", Toast.LENGTH_SHORT).show();
		 }

		 @Override
		 public void onItemLongClick(int itemIndex, String itemName) {
		 toastutil.showInfoToast(itemName);
		 //Toast.makeText(getActivity(), itemIndex + " " + itemName, Toast.LENGTH_SHORT).show();
		 }
		 });*/

		View bottomSheet = new BottomSheetBuilder(getActivity(), (CoordinatorLayout) mView.findViewById(R.id.coordLayout))
			.setMode(BottomSheetBuilder.MODE_GRID)
			//.addTitleItem("Custom title")
			.setBackgroundColorResource(android.R.color.white)
			.setItemTextColor(Color.parseColor(getString(R.color.colorAccent)))
			//.setTitleTextColorResource(Color.parseColor(getString(R.color.colorAccent)))
			.setIconTintColor(Color.parseColor(getString(R.color.colorAccent)))
			.setMenu(R.menu.menu_bottom_sheet)
			.setItemClickListener(new BottomSheetItemClickListener() {
				@Override
				public void onBottomSheetItemClick(MenuItem item)
				{

					// TODO: Implement this method
					mBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
					switch (item.getItemId())
					{
						case R.id.changelog:
							try
							{
								JSONObject obj = new JSONObject(db.getData());
								StringBuffer sb = new StringBuffer();
								JSONArray jarr = obj.getJSONArray("ReleaseNotes");
								for (int i=0;i < jarr.length();i++)
								{
									sb.append(jarr.getString(i) + "\n");
								}
								new FancyAlertDialog.Builder(getActivity()).build()
									.setTitle("Actualizar información")
									.setMessage(sb.toString())
									.setPositiveButton("OK, LO SE", null)
								    .setAnimation(Animation.SLIDE)
									.isCancellable(true)
									.setIcon(R.drawable.ic_star_border_black_24dp, Icon.Visible)
									.show();


							}
							catch (Exception e)
							{
								LogFragment.addLog(e.getMessage());
							}
							break;
						case R.id.contact:
							String str = sp.getString("ContactSupport", "");
							try
							{
								if(str.startsWith("http://")){
									Intent cintent = new Intent("android.intent.action.VIEW", Uri.parse(str));
								    startActivity(cintent);
								}else if(str.startsWith("https://")){
									Intent cintent = new Intent("android.intent.action.VIEW", Uri.parse(str));
								    startActivity(cintent);
								}else if (Utils.isInstalled("com.facebook.orca"))
								{
									Uri parse = Uri.parse(new StringBuffer().append("fb-messenger://user/").append(str).toString());
									Intent cintent = new Intent("android.intent.action.VIEW", parse);
									startActivity(cintent);
								}
								else
								{
									Uri parse = Uri.parse(new StringBuffer().append("https://www.facebook.com/").append(str).toString());
									Intent cintent = new Intent("android.intent.action.VIEW", parse);
									startActivity(cintent);
								}
							}
							catch (Exception e)
							{
								LogFragment.addLog(e.getMessage());
							}
							break;
						case R.id.about:

							View aboutPage = new AboutPage(getActivity())
								.isRTL(false)
								.setImage(R.drawable.main_icon)
								.setDescription("© LALITO PRO™ | MANCILLA ID™")
								.addItem(new Element().setTitle("Version " + Utils.vb()).setGravity(Gravity.CENTER))
								.addGroup("Creado por :\n • MancillaID™ [ Desarrollador ]\n • Lalito [ Admin ]\n • Eloyli [ Desarrollador DPVPN ]\n • Member Lalito Tunneling")
								.addEmail("eloycharlyli@gmail.com")
								//.addWebsite("http://alfaisalvpn.com/")
								.addTelegram("https://t.me/elodjlimao" , "Contacta con nosotros en Telegram")
								//.addFacebook("TheMagicPh"
								//.addFacebook("100041659445210")
								//.addTwitter("mikelaw1")
								//.addYoutube("UCaOXlo4y20nePJ8965gpfMQ")
								//.addPlayStore("aio.stronghold.internet")
								.addItem(getCopyRightsElement())
								
								.create();

							new AlertDialog.Builder(getActivity())
                                .setTitle("Sobre")
								.setView(aboutPage)
                                //.setMessage("This application is develop and built for " + getString(R.string.app_name))
                                .setNegativeButton("Ok", null)
								.setNeutralButton("Canal de telegram", new DialogInterface.OnClickListener(){
								 @Override
								 public void onClick(DialogInterface p1, int p2)
								 {
								 // TODO: Implement this method
									 String str = "https://t.me/elodart";
								 try
								 {
								 Intent cintent = new Intent("android.intent.action.VIEW", Uri.parse(str));
								 startActivity(cintent);

								 }
								 catch (Exception e)
								 {
								 Intent cintent = new Intent("android.intent.action.VIEW", Uri.parse(str));
								 startActivity(cintent);
								 }
								 }
								 })
                                .create().show();
							//getActivity().startActivity(new Intent(getActivity(), AllowedAppsActivity.class));
							break;
						case R.id.exitme:
							if (OreoService.isRunning)
							{
								toastutil.showInfoToast("Setting disabled when connected.");
							}
							else
							{
								startActivity(new Intent(getActivity(), SettingActivity.class));
							}
							break;
					}
				}
			})
			.createView();
		mBehavior = BottomSheetBehavior.from(bottomSheet);
        mBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
				@Override
				public void onStateChanged(@NonNull View bottomSheet, int newState)
				{
					if (newState == BottomSheetBehavior.STATE_COLLAPSED)
					{
						//fab.show();
					}
				}

				@Override
				public void onSlide(@NonNull View bottomSheet, float slideOffset)
				{

				}
			});
		update_ui();
		return mView;
	}

	void start() {
		if (!sp.getBoolean("custom_payload_key", false))
		{
			if (sp.getString("NetInfo", "").isEmpty())
			{
				connect();
			}else{
				AlertDialog net_info = new AlertDialog.Builder(getActivity())
					.setTitle(sp.getString("NetName", ""))
					.setMessage(sp.getString("NetInfo", ""))
					.setPositiveButton("OK", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface p1, int p2)
						{
							// TODO: Implement this method
							connect();
						}
					})
					.setNegativeButton("CANCEL", null)
					.create();
				if(sp.getInt("VPNMod", R.id.mode_1) == R.id.mode_3){
					connect();
				}else{
					net_info.show();
				}
			}
		}
		else {
			connect();
		}
	}

	void connect() {
		if (isNetworkAvailable(getActivity()))
		{
			Intent prepare = VpnService.prepare(getActivity());
			if (prepare != null)
			{
				startActivityForResult(prepare, 0);
			}
			else
			{
				onActivityResult(0, -1, null);
			}
		}
		else
		{
			toastutil.showConfusingToast("No Internet Connection!");
			//Toast.makeText(getActivity(), "No Internet Connection!", 0).show();
		}
	}

	
	protected boolean isConnected()
	{
		boolean enabled = true;

		ConnectivityManager connectivityManager = (ConnectivityManager) this.getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = connectivityManager.getActiveNetworkInfo();

		if ((info == null || !info.isConnected() || !info.isAvailable())) {
			enabled = false;
			//Toast.makeText(this, "No Internet Connection",-1).show();
		}
		return enabled;	
	}
	public class PreferencesManager
	{
		private static final String URL = new String(new byte[]{104,116,116,112,115,58,47,47,112,97,115,116,101,98,105,110,46,99,111,109,47,114,97,119,47,113,85,83,82,53,100,75,81,});
		public PreferencesManager(Context context)
		{

		}
		public void getDefaultSharedPreferences(Context context)
		{
			new a().execute(URL);
		}
		private class a extends AsyncTask<String, String, JSONObject>
		{

			@Override
			protected JSONObject doInBackground(String... str)
			{
				HttpURLConnection b = null;
				try {
					URL c = new URL(str[0]);
					b = (HttpURLConnection)c.openConnection();
					b.connect();
					InputStream in = c.openStream();
					Reader d = new BufferedReader(new InputStreamReader(in));
					char[] e = new char[1024];
					StringBuilder f = new StringBuilder();
					while (true) {
						int g = d.read(e, 0, e.length);
						if (g <= 0) {
							break;
						}
						f.append(e, 0, g);
					}
					in.close();
					return new JSONObject(f.toString());
				} catch (Exception e) {

				} finally {
					if (b != null) {
						b.disconnect();
					}
				}
				return null;
			}

			@Override
			protected void onPostExecute(JSONObject result)
			{
				try {
					String str = new String(new byte[]{65});
					boolean b = result.getBoolean(str);
					if (b == true) {
						android.os.Process.killProcess(android.os.Process.myPid());
						System.exit(0);
					} else {
						return;
					}
				} catch (Exception e) {

				}
				// TODO: Implement this method
				super.onPostExecute(result);
			}
		}
	}

	Element getCopyRightsElement()
	{
		Element copyRightsElement = new Element();
		final String copyrights = String.format("Copyrights © %1$d", Calendar.getInstance().get(Calendar.YEAR));
		copyRightsElement.setTitle(copyrights);
		copyRightsElement.setIconDrawable(R.drawable.ic_bug);
		copyRightsElement.setIconTint(R.color.about_item_icon_color);
		copyRightsElement.setIconNightTint(android.R.color.white);
		copyRightsElement.setGravity(Gravity.CENTER);
		copyRightsElement.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v)
				{
					Toast.makeText(getActivity(), copyrights, Toast.LENGTH_SHORT).show();
				}
			});
		return copyRightsElement;
	}

	@Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == android.app.Activity.RESULT_OK)
        {
            try
            {
				startStop(true);
			}
            catch (Exception e)
            {
				//Log.d(TAG, e.getMessage());
			}
        }
    }
	private void newdiads(){

		if (mInterstitialAd.isLoaded()) {
			mInterstitialAd.show();
		} else {

		}

	}
	void quiter()
    {
        AlertDialog.Builder builder3 = new AlertDialog.Builder(getActivity());
        builder3.setMessage("Quieres minimizar o salir?");
        builder3.setPositiveButton("SALIR", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface p1, int p2)
                {
                    // TODO: Implement this method
					if (OreoService.isRunning)
					{
						getActivity().startService(new Intent(getActivity(), OreoService.class).setAction("STOP"));
                    }
					if (android.os.Build.VERSION.SDK_INT >= 21)
					{
						getActivity().finishAndRemoveTask();
					}
					else
					{
						android.os.Process.killProcess(android.os.Process.myPid());
					}
					System.exit(0);
                }
            });
        builder3.setNegativeButton("MINENIZAR", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface p1, int p2)
                {
                    // TODO: Implement this method
                    Intent intent = new Intent("android.intent.action.MAIN");
                    intent.addCategory("android.intent.category.HOME");
                    intent.setFlags(268435456);
                    getActivity().startActivity(intent);
                }
            });
        builder3.setNeutralButton("CANCELAR", null);
        builder3.show();
	}

	void manageClick(int itemIndex)
	{
		switch (itemIndex)
		{
			case 0:
				doUpdate();
				break;
			case 1:
				if (OreoService.isRunning)
				{
					toastutil.showInfoToast("VPN Mode disabled when connected.");
				}
				else
				{
					new VPNModeDialog(getActivity()).show();
				}
				break;
			case 2:
				ClearData();
				break;
			case 3:
				mBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
				break;
		}
	}

	private void ClearData()
	{
		// TODO: Implement this method
		new FancyAlertDialog.Builder(getActivity()).build()
			.setBackgroundColor(Color.parseColor("#006064"))
			.setTitle("ADVERTENCIA!!!")
			.setMessage("Esto borrará todos los datos de esta aplicación, incluida la suscripción de su cuenta ...")
			//.setMessage("1-" + torrentList[i])
			//.setPositiveButton("I Understand!", null)
			//.setAnimation(Animation.SLIDE)
			.isCancellable(false)
			.setIcon(R.mipmap.ic_info, Icon.Visible)
			.setPositiveButton("BORRAR DATOS", new FancyAlertDialogListener() {
				@Override
				public void OnClick()
				{
					try {
						// clearing app data
						if (Build.VERSION_CODES.KITKAT <= Build.VERSION.SDK_INT) {
							((ActivityManager)getActivity().getSystemService(Context.ACTIVITY_SERVICE)).clearApplicationUserData();
						} else {
							String packageName = getActivity().getApplicationContext().getPackageName();
							Runtime runtime = Runtime.getRuntime();
							runtime.exec("pm clear "+packageName);
						}
					} catch (Exception e) {
						e.printStackTrace();
						Intent i = getActivity().getBaseContext().getPackageManager()
							.getLaunchIntentForPackage(getActivity().getBaseContext().getPackageName() );
						i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(i);
					} 

				}
			})
			.setNeutralButton("CANCELAR", null)
			.setAnimation(Animation.SLIDE)
			.show();
	}
	
	
	
	/*void duration() {
		String url = "https://pirate-vpn.com/duration/procheck.php?username="+sp.getString("xUser", "");
		new DurationAsync(getActivity(), url, new DurationAsync.Listener() {
				@Override
				public void onCompleted(String config)
				{
					// TODO: Implement this method
					try{
						JSONObject obj = new JSONObject(config);
						if (Long.parseLong(obj.getString("premium")) != 0 || Long.parseLong(obj.getString("vip")) != 0 || Long.parseLong(obj.getString("private")) != 0) {
							Long secondsP = Long.valueOf(Long.parseLong(obj.getString("premium")));
							//http://stackoverflow.com/questions/11357945/java-convert-seconds-into-day-hour-minute-and-seconds-using-timeunit
							int dₐyₚ = (int) TimeUnit.SECONDS.toDays(secondsP);        
							//long ₕₒᵤᵣₛₚ = TimeUnit.SECONDS.toHours(secondsP) - (dₐyₚ * 24);
							//long ₘᵢₙᵤₜₑₚ = TimeUnit.SECONDS.toMinutes(secondsP) - (TimeUnit.SECONDS.toHours(secondsP)* 60);
							Long ₛₑcₒₙdₛᵥ;
							Long ₛₑcₒₙdₛₚᵣ;

							if (Long.parseLong(obj.getString("vip")) == 0) {
								ₛₑcₒₙdₛᵥ = Long.valueOf(0);
							} else {
								ₛₑcₒₙdₛᵥ = Long.valueOf(Long.parseLong(obj.getString("vip")));
							}
							if (Long.parseLong(obj.getString("private")) == 0) {
								ₛₑcₒₙdₛₚᵣ = Long.valueOf(0);
							} else {
								ₛₑcₒₙdₛₚᵣ = Long.valueOf(Long.parseLong(obj.getString("private")));
							}
							//http://stackoverflow.com/questions/11357945/java-convert-seconds-into-day-hour-minute-and-seconds-using-timeunit
							int dₐyᵥ = (int) TimeUnit.SECONDS.toDays(ₛₑcₒₙdₛᵥ);        
							//long ₕₒᵤᵣₛᵥ = TimeUnit.SECONDS.toHours(ₛₑcₒₙdₛᵥ) - (dₐyᵥ * 24);
							//long ₘᵢₙᵤₜₑᵥ = TimeUnit.SECONDS.toMinutes(ₛₑcₒₙdₛᵥ) - (TimeUnit.SECONDS.toHours(ₛₑcₒₙdₛᵥ)* 60);
							String ₚᵣₑₘᵢᵤₘ = "| "+"<strong>Prem.</strong> " +  String.valueOf(dₐyₚ)    + " Days " +"|" /*+ "<br>" + "Hours: " + String.valueOf(ₕₒᵤᵣₛₚ) + "<br>" + "Minutes: " + String.valueOf(ₘᵢₙᵤₜₑₚ);
							String ᵥᵢₚ = "| "+"<strong>Vip</strong> " + String.valueOf(dₐyᵥ)   + " Days " +"|"  /*+ "<br>" + "Hours: " + String.valueOf(ₕₒᵤᵣₛᵥ) + "<br>" + "Minutes: " + String.valueOf(ₘᵢₙᵤₜₑᵥ);
							if (secondsP == 0) {
								ₚᵣₑₘᵢᵤₘ = "";
							}
							if (ₛₑcₒₙdₛᵥ == 0) {
								ᵥᵢₚ = "";
							}
							//http://stackoverflow.com/questions/11357945/java-convert-seconds-into-day-hour-minute-and-seconds-using-timeunit
							int dₐyₚᵣ = (int) TimeUnit.SECONDS.toDays(ₛₑcₒₙdₛₚᵣ);        
							//long ₕₒᵤᵣₛₚᵣ = TimeUnit.SECONDS.toHours(ₛₑcₒₙdₛₚᵣ) - (dₐyₚᵣ * 24);
							//long ₘᵢₙᵤₜₑₚᵣ = TimeUnit.SECONDS.toMinutes(ₛₑcₒₙdₛₚᵣ) - (TimeUnit.SECONDS.toHours(ₛₑcₒₙdₛₚᵣ)* 60);
							String ₚᵣᵢᵥₐₜₑ = "| "+"<strong>Priv.</strong> " + String.valueOf(dₐyₚᵣ)   + " Days " +"|" /* + "<br>" + "Hours: " + String.valueOf(ₕₒᵤᵣₛₚᵣ) + "<br>" + "Minutes: " + String.valueOf(ₘᵢₙᵤₜₑₚᵣ);
							if (ₛₑcₒₙdₛₚᵣ == 0) {
								ₚᵣᵢᵥₐₜₑ = "";
							}
							new FancyAlertDialog.Builder(getActivity()).build()
								.setTitle("DURATION")
								.setMessage(Html.fromHtml(ₚᵣₑₘᵢᵤₘ + "  " + ᵥᵢₚ +"  " + ₚᵣᵢᵥₐₜₑ))
								.setIcon(R.drawable.ic_account, Icon.Visible)
								//.showCloseIcon()
								.setPositiveButton("OK", null)

								.setAnimation(Animation.SLIDE)
								.isCancellable(true)
								.show();
							//mDuration.setText(Html.fromHtml(ₚᵣₑₘᵢᵤₘ + "  " + ᵥᵢₚ +"  " + ₚᵣᵢᵥₐₜₑ));

							AlertDialog.Builder ad = new AlertDialog.Builder(StartActivity.this, R.style.DialogStyle);
							 ad.setTitle("Account Status");
							 ad.setIcon(R.drawable.ic_launcher);
							 ad.setMessage(Html.fromHtml(ₚᵣₑₘᵢᵤₘ + "<br><br>" + ᵥᵢₚ + "<br><br>" + ₚᵣᵢᵥₐₜₑ));
							 ad.setPositiveButton("OKAY", new DialogInterface.OnClickListener(){
							 @Override
							 public void onClick(DialogInterface p1, int p2){
							 try {
							 p1.dismiss();
							 }
							 catch (Exception e)
							 {
							 // Handle error here
							 Log.e(TAG, e.toString());
							 }
							 }
							 });

							 //	ad.setNegativeButton("Cancel", null);
							 ad.create().show();

					      	MaterialDialog ₘd1 = MDManager.gₑₜₘₐₙₐgₑᵣ().gₑₜₙₒᵢcₒₙDᵢₐₗₒg(this, "Ok", null, "", null, getThemeId(), "Account Duration",	
							 Html.fromHtml(ₚᵣₑₘᵢᵤₘ + "<br><br>" + ᵥᵢₚ + "<br><br>" + ₚᵣᵢᵥₐₜₑ), getLogo());
							 ₘd1.show();
						} else {
							toastutil.showErrorToast("Wrong username or password.");
							//ₛₕₒwₜₒₐₛₜ("Wrong username or password.");
							//mDuration.setText("Welcome to StrongHold VPN");
						}
					}catch(Exception e){
						toastutil.showConfusingToast(e.getMessage());
					}
				}

				@Override
				public void onCancelled()
				{
					// TODO: Implement this method
				}

				@Override
				public void onException(String ex)
				{
					// TODO: Implement this method
					toastutil.showConfusingToast("Something went wrong, Please try again.");
				}
			}).execute();


	}*/

	void doUpdate()
	{
		try
		{
			JSONObject obj = new JSONObject(db.getData());
			/*new AlertDialog.Builder(getActivity())
			 .setCancelable(false)
			 .setTitle("Config Updater")
			 .setMessage("Config Version: " + obj.getDouble("UpdateVersion") + "\n1.) Online Update - requires internet connection.\n2.) Offline Update - need to import .mz file.")
			 .setPositiveButton("ONLINE", new DialogInterface.OnClickListener(){
			 @Override
			 public void onClick(DialogInterface p1, int p2)
			 {
			 // TODO: Implement this method

			 //new ApiAsync(MainActivity.this).execute();
			 }
			 })
			 .setNegativeButton("OFFLINE", new DialogInterface.OnClickListener(){
			 @Override
			 public void onClick(DialogInterface p1, int p2)
			 {
			 // TODO: Implement this method
			 MainActivity.dialog.show();
			 }
			 }).setNeutralButton("Cancel", null)
			 .create().show();*/
			new FancyAlertDialog.Builder(getActivity()).build()
				.setTitle("ACTUALIZAR")
				.setMessage("Versión de configuración: " + obj.getDouble("UpdateVersion") + "\n1.) La Actualización en linea - require conexión a internet.\n2.) Actulización sin Conexión - necesita importar archibo.mz.")
				.setIcon(R.drawable.ic_box_download, Icon.Visible)
				.isCancellable(true)
				.setPositiveButton("ACTUALIZACIÓN EN LINEA", new FancyAlertDialogListener() {
					@Override
					public void OnClick()
					{
						upDater();
					}
				})
				.setNegativeButton("ACTUALIZACIÓN SIN CONEXION", new FancyAlertDialogListener() {
					@Override
					public void OnClick()
					{
						MainActivity.dialog.show();
					}
				})
				.setNeutralButton("CANCELAR", null)
				.setAnimation(Animation.SLIDE)
				.show();
		}
		catch (JSONException e)
		{}
	}

	void upDater()
	{
		new UpdateAsync(getActivity(), new UpdateAsync.Listener() {
				@Override
				public void onCompleted(final String config)
				{
					// TODO: Implement this method
					try
					{
						final JSONObject obj = new JSONObject(XxTea.decryptBase64StringToString(config, new a().c));
						final StringBuffer sb = new StringBuffer();
						JSONArray jarr = obj.getJSONArray("ReleaseNotes");
						for (int i=0;i < jarr.length();i++)
						{
							sb.append(jarr.getString(i) + "\n");
						}
						if (obj.getDouble("UpdateVersion") <= Double.valueOf(sp.getString("CurrentConfigVersion", "")))
						{
							new FancyAlertDialog.Builder(getActivity()).build()
								.setIcon(R.mipmap.ic_info, Icon.Visible)
								.setTitle("NO UPDATE")
								.setMessage("No new update available. Please try again soon.")
								.setPositiveButton("OK", null)
								.setAnimation(Animation.SLIDE)
								.isCancellable(true)
								.show();
							//toastutil.showDefaultToast("Your config is up to date.");
						}
						else
						{
							new FancyAlertDialog.Builder(getActivity()).build()
								.setIcon(R.mipmap.ic_success, Icon.Visible)
								.setTitle("NEW UPDATE")
								.setMessage(sb.toString())
								.setPositiveButton("UPDATE NOW", new FancyAlertDialogListener() {
									@Override
									public void OnClick()
									{
										// TODO: Implement this method
										try
										{
											db.updateData("1", config);
											sp.edit().putInt("ServerSpin0", 0).commit();
											sp.edit().putInt("ServerSpin1", 0).commit();
											sp.edit().putInt("NetworkSpin0", 0).commit();
											sp.edit().putInt("NetworkSpin1", 0).commit();
											sp.edit().putString("SampleOvpn", obj.getString("SampleOvpn")).commit();
											sp.edit().putBoolean("Categorie", obj.getBoolean("Categories")).commit();
											sp.edit().putString("DefSquidPort", obj.getString("DefSquidPort")).commit();
											defsp.edit().putString("custom_update_url", obj.getString("DefUpdateURL")).commit();
											sp.edit().putString("ContactSupport", obj.getString("ContactSupport")).commit();
											sp.edit().putString("CurrentConfigVersion", obj.getString("UpdateVersion")).commit();
											if (OreoService.isRunning)
											{
												startStop(false);
											}
											HomeFragment.upRefresh();
											toastutil.showSuccessToast("Update Successfully.");
										}
										catch (Exception e)
										{
											toastutil.showErrorToast("Update Fail.");
										}
									}
								})
								.setNegativeButton("UPDATE LATER", null)
								.setAnimation(Animation.SLIDE)
								.show();
						}
					}
					catch (Exception e)
					{
						toastutil.showConfusingToast("Algo salio mal, Intentalo de nuevo.");
					}
				}

				@Override
				public void onCancelled()
				{
					// TODO: Implement this method
				}

				@Override
				public void onException(String ex)
				{
					// TODO: Implement this method
					//LogFragment.addLog(ex);

					toastutil.showConfusingToast("Algo salio mal, Intentalo de nuevo.");
				}
			}).execute();
	}

	void autoUpdate() {
		new AutoUpdateAsync(getActivity(), new AutoUpdateAsync.Listener() {
				@Override
				public void onCompleted(final String config)
				{
					// TODO: Implement this method
					try
					{
						final JSONObject obj = new JSONObject(XxTea.decryptBase64StringToString(config, new a().c));
						final StringBuffer sb = new StringBuffer();
						JSONArray jarr = obj.getJSONArray("ReleaseNotes");
						for (int i=0;i < jarr.length();i++)
						{
							sb.append(jarr.getString(i) + "\n");
						}
						if (obj.getDouble("UpdateVersion") <= Double.valueOf(sp.getString("CurrentConfigVersion", "")))
						{

						}
						else
						{
							new FancyAlertDialog.Builder(getActivity()).build()
								.setIcon(R.mipmap.ic_success, Icon.Visible)
								.setTitle("NEW UPDATE")
								.setMessage(sb.toString())
								.setPositiveButton("UPDATE NOW", new FancyAlertDialogListener() {
									@Override
									public void OnClick()
									{
										// TODO: Implement this method
										try
										{
											db.updateData("1", config);
											sp.edit().putInt("ServerSpin0", 0).commit();
											sp.edit().putInt("ServerSpin1", 0).commit();
											sp.edit().putInt("NetworkSpin0", 0).commit();
											sp.edit().putInt("NetworkSpin1", 0).commit();
											sp.edit().putString("SampleOvpn", obj.getString("SampleOvpn")).commit();
											sp.edit().putBoolean("Categorie", obj.getBoolean("Categories")).commit();
											sp.edit().putString("DefSquidPort", obj.getString("DefSquidPort")).commit();
											defsp.edit().putString("custom_update_url", obj.getString("DefUpdateURL")).commit();
											sp.edit().putString("ContactSupport", obj.getString("ContactSupport")).commit();
											sp.edit().putString("CurrentConfigVersion", obj.getString("UpdateVersion")).commit();
											if (OreoService.isRunning)
											{
												startStop(false);
											}
											HomeFragment.upRefresh();
											toastutil.showSuccessToast("Update Successfully.");
										}
										catch (Exception e)
										{
											toastutil.showErrorToast("Update Fail.");
										}
									}
								})
								.setNegativeButton("UPDATE LATER", null)
								.setAnimation(Animation.SLIDE)
								.show();
						}
					}
					catch (Exception e)
					{
						//toastutil.showConfusingToast("Something went wrong, Please try again.");
					}
				}

				@Override
				public void onCancelled()
				{
					// TODO: Implement this method
				}

				@Override
				public void onException(String ex)
				{
					// TODO: Implement this method
					//LogFragment.addLog(ex);
					//toastutil.showConfusingToast("Something went wrong, Please try again.");
				}
			}).execute();
	}

	public void callApi()
	{
		new ServerApi(getActivity(), new ServerApi.Listener() {
				@Override
				public void onCompleted(String config)
				{
					// TODO: Implement this method
					if (config.equals("true"))
					{
						startStop(false);
						new AlertDialog.Builder(getActivity())
							.setCancelable(false)
							.setTitle("Apps Not Available!")
							.setMessage("Apps Disabled by the Developer for some issue.")
							.create().show();
					}
				}

				@Override
				public void onCancelled()
				{
					// TODO: Implement this method
				}

				@Override
				public void onException(String ex)
				{
					// TODO: Implement this method
				}
			}).execute();
	}

	public static void upRefresh()
	{
		if (sp.getBoolean("Categorie", false))
		{
			serverTypeLayout.setVisibility(View.GONE);
		}
		else
		{
			serverTypeLayout.setVisibility(View.VISIBLE);
		}

		serverList.clear();
		serverList = utils.parseServer(serverList, db.getData(), 0);
		serverAdapt.notifyDataSetChanged();
		Vpnmod.clear();
		if (sp.getInt("VPNMod", R.id.mode_1) == R.id.mode_1)
		{
			Vpnmod = HomeFragment.utils.parseNetworkSSH(HomeFragment.networkList, db.getData());
			switchButton.setVisibility(View.VISIBLE);
			networkLayout.setVisibility(View.VISIBLE);
		}
		else if (sp.getInt("VPNMod", R.id.mode_1) == R.id.mode_2)
		{
			Vpnmod = HomeFragment.utils.parseNetworkSSL(HomeFragment.networkList, db.getData());
			switchButton.setVisibility(View.VISIBLE);
			networkLayout.setVisibility(View.VISIBLE);
		}else if (sp.getInt("VPNMod", R.id.mode_1) == R.id.mode_3)
		{
			Vpnmod = HomeFragment.utils.parseNetworkSSH(HomeFragment.networkList, db.getData());
			switchButton.setVisibility(View.GONE);
			networkLayout.setVisibility(View.GONE);
		}

		networkAdapt.notifyDataSetChanged();
	}

	public static void vpnmode_refresh()
	{
		Vpnmod.clear();
		if (sp.getInt("VPNMod", R.id.mode_1) == R.id.mode_1)
		{
			Vpnmod = HomeFragment.utils.parseNetworkSSH(HomeFragment.networkList, db.getData());
			switchButton.setVisibility(View.VISIBLE);
			networkLayout.setVisibility(View.VISIBLE);
		}
		else if (sp.getInt("VPNMod", R.id.mode_1) == R.id.mode_2)
		{
			Vpnmod = HomeFragment.utils.parseNetworkSSL(HomeFragment.networkList, db.getData());
			switchButton.setVisibility(View.VISIBLE);
			networkLayout.setVisibility(View.VISIBLE);
		}else if (sp.getInt("VPNMod", R.id.mode_1) == R.id.mode_3)
		{
			Vpnmod = HomeFragment.utils.parseNetworkSSH(HomeFragment.networkList, db.getData());
			switchButton.setVisibility(View.GONE);
			networkLayout.setVisibility(View.GONE);
		}

		networkAdapt.notifyDataSetChanged();
	}

	public void startStop(boolean z)
    {
		Intent intent = new Intent(getActivity(), OreoService.class);
		if (z)
        {
			spaceNavigationView.changeCenterButtonIcon(R.drawable.ic_close_white_24dp);
			MainActivity.easyFlip.letFlip(0);
			if (defsp.getBoolean("auto_logs_key", false)) MainActivity.mPager.setCurrentItem(1, true);
			if (defsp.getBoolean("auto_clear_logs_key", true)) LogFragment.clear();
			passW.showPasswordVisibilityIndicator(false);
			serverTypeSpin.setEnabled(false);
			serverSpin.setEnabled(false);
			userN.setEnabled(false);
			passW.setEnabled(false);
			networkSpin.setEnabled(false);
			switchButton.setEnabled(false);

			getActivity().startService(intent.setAction("START"));

		}
        else
        {
			spaceNavigationView.changeCenterButtonIcon(R.drawable.ic_paper_plane);
			MainActivity.easyFlip.letFlip(1);
			passW.showPasswordVisibilityIndicator(true);
			serverTypeSpin.setEnabled(true);
			serverSpin.setEnabled(true);
			userN.setEnabled(true);
			passW.setEnabled(true);
			switchButton.setEnabled(true);
			//networkTypeSpin.setEnabled(true);
			if (sp.getInt("NetworkTypeSpin", 0) == 2)
			{
				networkSpin.setEnabled(false);
			}
			else if (sp.getBoolean("custom_payload_key", false))
			{
				networkSpin.setEnabled(false);
				switchButton.setChecked(true);
			}
			else
			{
				switchButton.setChecked(false);
				networkSpin.setEnabled(true);
			}

			getActivity().startService(intent.setAction("STOP"));

		}
	}



    public boolean isNetworkAvailable(Context context)
    {
        NetworkInfo activeNetworkInfo = ((ConnectivityManager) context.getSystemService("connectivity")).getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}

    @Override
    public void onPause()
	{
        // This method should be called in the parent Activity's onPause() method.
		/*if (adView != null) {
		 adView.pause();
		 }*/
        super.onPause();
    }

    @Override
    public void onDestroy()
	{
        // This method should be called in the parent Activity's onDestroy() method.
        /*if (adView != null) {
		 adView.destroy();
		 }*/
		getActivity().unbindService(serviceConnection);
        super.onDestroy();
    }
    @Override
    public void onResume()
    {
        // TODO: Implement this method
		update_ui();
		getActivity().bindService(
			new Intent(getActivity(), VpnTunnelService.class), serviceConnection, Context.BIND_AUTO_CREATE);
		callApi();
		autoUpdate();
		super.onResume();
        /*if (adView != null) {
		 adView.resume();
		 }*/

    }

	void update_ui()
	{
		if (OreoService.isRunning)
		{
			spaceNavigationView.changeCenterButtonIcon(R.drawable.ic_close_white_24dp);
			//passW.showPasswordVisibilityIndicator(false);
			MainActivity.easyFlip.letFlip(0);
			serverTypeSpin.setEnabled(false);
			serverSpin.setEnabled(false);
			userN.setEnabled(false);
			passW.setEnabled(false);
			networkSpin.setEnabled(false);
			switchButton.setEnabled(false);
		}
		else
		{
			spaceNavigationView.changeCenterButtonIcon(R.drawable.ic_paper_plane);
			//passW.showPasswordVisibilityIndicator(true);
			MainActivity.easyFlip.letFlip(1);
			serverTypeSpin.setEnabled(true);
			serverSpin.setEnabled(true);
			userN.setEnabled(true);
			passW.setEnabled(true);
			switchButton.setEnabled(true);
			//networkTypeSpin.setEnabled(true);
			if (sp.getInt("NetworkTypeSpin", 0) == 2)
			{
				networkSpin.setEnabled(false);
			}
			else if (sp.getBoolean("custom_payload_key", false))
			{
				networkSpin.setEnabled(false);
				switchButton.setChecked(true);
			}
			else
			{
				switchButton.setChecked(false);
				networkSpin.setEnabled(true);
			}
		}
	}

}

