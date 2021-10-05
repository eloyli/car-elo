package mph.trunksku.apps.myssh;

import android.*;
import android.annotation.*;
import android.app.*;
import android.content.*;
import android.content.pm.*;
import android.content.res.*;
import android.net.*;
import android.os.*;
import android.support.design.widget.*;
import android.support.v4.app.*;
import android.support.v4.content.*;
import android.support.v4.view.*;
import android.support.v4.widget.*;
import android.support.v4n.view.*;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.*;
import android.text.*;
import android.util.*;
import android.view.*;
import android.widget.*;
import com.github.angads25.filepicker.controller.*;
import com.github.angads25.filepicker.model.*;
import com.github.angads25.filepicker.view.*;
import com.trilead.ssh2.sftp.*;
import java.io.*;
import java.util.concurrent.*;
import mph.trunksku.apps.myssh.async.*;
import mph.trunksku.apps.myssh.db.*;
import mph.trunksku.apps.myssh.fragment.*;
import mph.trunksku.apps.myssh.model.*;
import mph.trunksku.apps.myssh.service.*;
import mph.trunksku.apps.myssh.util.*;
import mph.trunksku.apps.myssh.view.*;
import org.json.*;
import com.masfha.id.*;
import com.masfha.id.R;
import android.support.v4n.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import com.github.mikephil.charting.charts.*;
import com.github.mikephil.charting.components.*;
import android.graphics.*;
import com.github.mikephil.charting.data.*;
import com.github.mikephil.charting.formatter.*;
import com.github.mikephil.charting.interfaces.datasets.*;
import com.github.mikephil.charting.interfaces.dataprovider.*;
import java.util.*;
import java.text.*;
import mph.trunksku.apps.myssh.core.*;
import android.support.v4.content.res.*;
import android.graphics.drawable.*;
import android.support.v4.graphics.drawable.*;
import com.luseen.spacenavigation.*;
import com.shashank.sony.fancydialoglib.Icon;
import com.shashank.sony.fancydialoglib.*;
import java.net.*;
import android.support.v4n.a;

public class MainActivity extends AppCompatActivity 
{

	public static ViewPager mPager;

	private ScreenSliderPagerAdapter mPagerAdapter;

	public static Toolbar toolbar;

	private static SharedPreferences sp;

    public static FilePickerDialog dialog;

    private String TAG;
    private static Context mContext;

	private DataBaseHelper db;

	private LineChart mChart;
	private Thread dataUpdate;
    private Handler vHandler = new Handler();
    
    DecimalFormat df = new DecimalFormat("#.##");

	private GraphHelper graph;

	public static EasyFlipView easyFlip;

	private JSONObject obj;

	private SharedPreferences defsp;

	
	private String[] torrentList = new String[] {
		"mph.trunksku.apps.dexencryptor",
		"com.guoshi.httpcanary",
		"jp.co.intip",
		"app01.greyshirts.sslcapture",
		"com.evbadroid.proxymon", 
		"app.greyshirts.sslcapture", 
		"com.minhui.networkcapture", 
		"com.minhui.networkcapture.pro", 
		"com.tdo.showbox",
		"com.nitroxenon.terrarium",
		"com.pklbox.translatorspro",
		"com.xunlei.downloadprovider",
		"com.epic.app.iTorrent",
		"hu.bute.daai.amorg.drtorrent",
		"com.mobilityflow.torrent.prof",
		"com.brute.torrentolite",
		"com.nebula.swift",
		"tv.bitx.media",
		"com.DroiDownloader",
		"bitking.torrent.downloader",
		"org.transdroid.lite",
		"com.mobilityflow.tvp",
		"com.gabordemko.torrnado",
		"com.frostwire.android",
		"com.vuze.android.remote",
		"com.akingi.torrent",
		"com.utorrent.web",
		"com.paolod.torrentsearch2",
		"com.delphicoder.flud.paid",
		"com.teeonsoft.ztorrent",
		"megabyte.tdm",
		"com.bittorrent.client.pro",
		"com.mobilityflow.torrent",
		"com.utorrent.client",
		"com.utorrent.client.pro",
		"com.bittorrent.client",
		"torrent",
		"com.AndroidA.DroiDownloader",
		"com.indris.yifytorrents",
		"com.delphicoder.flud",
		"com.oidapps.bittorrent",
		"dwleee.torrentsearch",
		"com.vuze.torrent.downloader",
		"megabyte.dm",
		"com.fgrouptech.kickasstorrents",
		"com.jrummyapps.rootbrowser.classic",
		"com.bittorrent.client",
		"hu.tagsoft.ttorrent.lite",
		"co.we.torrent"};
	
	
	private ToastUtil toastutil;

	private CollapsingToolbarLayout collapsingToolbarLayout;

	
	@Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
		new ApplicationBase().init(this);
		db = new DataBaseHelper(this);
        mContext = MainActivity.this;
		toastutil = new ToastUtil(this);
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
		new Utils(this);
		sp = ApplicationBase.getSharedPreferences();
		defsp = ApplicationBase.getDefSharedPreferences();
        
		if(Utils.r()){
            new FancyAlertDialog.Builder(MainActivity.this).build()
                .setIcon(R.mipmap.ic_info, Icon.Visible)
                .setTitle("OOPS PROBLEMA DETECTADO")
                .setMessage("Instale la versión original de la aplicación. Tampoco use un dispositivo rooteado en esta aplicación.")
                .setPositiveButton("OK", new FancyAlertDialogListener(){
                    @Override
                    public void OnClick()
                    {
                        // TODO: Implement this method
                        if (android.os.Build.VERSION.SDK_INT >= 21) {
                            finishAndRemoveTask();
                        } else {
                            android.os.Process.killProcess(android.os.Process.myPid());
                        }
                        System.exit(0);
                    }
                })
                .setAnimation(Animation.SLIDE)
                .isCancellable(false)
				.show();
        }
        if(Utils.q()){
            new FancyAlertDialog.Builder(MainActivity.this).build()
                .setIcon(R.mipmap.ic_info, Icon.Visible)
                .setTitle("OOPS PROBLEMA DETECTADO")
                .setMessage("Instale la versión original de la aplicación. Tampoco utilice un dispositivo rooteado en esta aplicación.")
                .setPositiveButton("OK", new FancyAlertDialogListener(){
                    @Override
                    public void OnClick()
                    {
                        // TODO: Implement this method
                        if (android.os.Build.VERSION.SDK_INT >= 21) {
                            finishAndRemoveTask();
                        } else {
                            android.os.Process.killProcess(android.os.Process.myPid());
                        }
                        System.exit(0);
                    }
                })
                .setAnimation(Animation.SLIDE)
                .isCancellable(false)
				.show();
        }
        if (!(((String) getPackageManager().getApplicationLabel(getApplicationInfo())).equals(new a().a) && getPackageName().equals(new a().b))) {
			new FancyAlertDialog.Builder(MainActivity.this).build()
				.setIcon(R.mipmap.ic_info, Icon.Visible)
				.setTitle("APLICACIÓN OOPS MODIFICADA")
				.setMessage("Instale la versión original de la aplicación")
				.setPositiveButton("OK", new FancyAlertDialogListener(){
					@Override
					public void OnClick()
					{
						// TODO: Implement this method
						if (android.os.Build.VERSION.SDK_INT >= 21) {
							finishAndRemoveTask();
						} else {
							android.os.Process.killProcess(android.os.Process.myPid());
						}
						System.exit(0);
					}
				})
				.setAnimation(Animation.SLIDE)
				.isCancellable(false)
				.show();
		}
		new TorrentDetection(this, torrentList).init();
        
		AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
        View mView = getLayoutInflater().inflate(R.layout.dialog_app_updates, null);
        CheckBox mCheckBox = mView.findViewById(R.id.checkBox);
        mBuilder.setTitle("DESCARGO DE RESPONSABILIDAD");
        mBuilder.setMessage("\n Esta aplicación se creó con una interfaz fácil de usar. Si no sabe lo que está haciendo, no dude en ponerse en contacto con nosotros para obtener orientación y consultas. \n \n \n Puede ver nuestra información de contacto en el menú inferior derecho y navegar por el botón para mostrar la información de contacto disponible ...");
        mBuilder.setView(mView);
        mBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialogInterface, int i) {
					dialogInterface.dismiss();
				}
			});

        AlertDialog mDialog = mBuilder.create();
        mDialog.show();
        mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
					if(compoundButton.isChecked()){
						storeDialogStatus(true);
					}else{
						storeDialogStatus(false);
					}
				}
			});

        if(getDialogStatus()){
            mDialog.hide();
        }else{
            mDialog.show();
		}
    
		
		
		if (new Boolean(sp.getBoolean("firstStart", true)).booleanValue())
        {
			db.insertData(new a().d);
			//db.insertData(cfgData());
			//Utils.v(cfgData());
			try
			{
				ApplicationBase.ExtractGo();
			}
			catch (IOException e)
			{
				Log.d("", e.getMessage());
			}
			try
			{
				obj = new JSONObject(db.getData());
				sp.edit().putInt("VPNTunMod", R.id.ssh).commit();
				sp.edit().putInt("VPNMod", R.id.mode_1).commit();
				sp.edit().putString("SampleOvpn", obj.getString("SampleOvpn")).commit();
				sp.edit().putBoolean("Categorie", obj.getBoolean("Categories")).commit();
				sp.edit().putString("DefSquidPort", obj.getString("DefSquidPort")).commit();
				defsp.edit().putString("custom_update_url", obj.getString("DefUpdateURL")).commit();
				sp.edit().putString("ContactSupport", obj.getString("ContactSupport")).commit();
				sp.edit().putInt("CurrentConfigVersion", obj.getInt("UpdateVersion")).commit();
				sp.edit().putString("PrimaryDns", obj.getString("PrimaryDns")).commit();
				sp.edit().putString("SecondaryDns", obj.getString("SecondaryDns")).commit();
			}
			catch (JSONException e)
			{}
			sp.edit().putBoolean("firstStart", false).commit();
		}
		//if (ApplicationBase.isDarkTheme())
		//{
           // setTheme(R.style.AppTheme_Dark);
       // }
		setContentView(R.layout.main);


		setupToolbar();
		// Instantiate a ViewPager and a PagerAdapter.
        mPager = (ViewPager) findViewById(R.id.pager);
        mPagerAdapter = new ScreenSliderPagerAdapter(getFragmentManager(), this);
        /* Toolbar and slider should have the same elevation */

        mPagerAdapter.addTab("TU HOGAR", HomeFragment.class);
        mPagerAdapter.addTab("INICIANDO SESIÓN", LogFragment.class);
        //mPagerAdapter.addTab(R.string.faq, FaqFragment.class);
		mPager.setAdapter(mPagerAdapter);
        TabBarView tabs = (TabBarView) findViewById(R.id.sliding_tabs);
        tabs.setViewPager(mPager);
		easyFlip = (EasyFlipView) findViewById(R.id.easyFlipView);
			
		mChart = (LineChart) findViewById(R.id.chart1);
		// mChart.setViewPortOffsets(0, 0, 0, 0);
		// mChart.setBackgroundColor(Color.rgb(104, 241, 175));
		graph = GraphHelper.getHelper().with(this).color(Color.parseColor("#03293F")).chart(mChart);
	    
        DialogProperties properties=new DialogProperties();
        properties.selection_mode = DialogConfigs.SINGLE_MODE;
        properties.selection_type = DialogConfigs.FILE_SELECT;
		properties.extensions = new String[] {".mz", ".MZ"};
		properties.root = Environment.getExternalStorageDirectory();
        //Instantiate FilePickerDialog with Context and DialogProperties.
        dialog = new FilePickerDialog(this, properties);
        dialog.setTitle("Select a File");
        dialog.setPositiveBtnName("Select");
        dialog.setNegativeBtnName("Cancel");
        dialog.setDialogSelectionListener(new DialogSelectionListener() {
                @Override
                public void onSelectedFilePaths(final String[] files)
				{
                    for (String path:files)
					{
                        File file=new File(path);
                        if (file.getName().endsWith(".mz") || file.getName().endsWith(".MZ"))
						{
                            final String data = inet(file.getAbsolutePath());
                            if (TextUtils.isEmpty(data))
							{
                                toastutil.showErrorToast("Invalid File.");
                            }
							else
							{
                                try
								{
									final JSONObject obj = new JSONObject(XxTea.decryptBase64StringToString(data, new a().c));
									final StringBuffer sb = new StringBuffer();
									JSONArray jarr = obj.getJSONArray("ReleaseNotes");
									for (int i=0;i < jarr.length();i++)
									{
										sb.append(jarr.getString(i) + "\n");
									}
									if (obj.getDouble("UpdateVersion") <= Double.valueOf(sp.getString("CurrentConfigVersion", "")))
									{
										new FancyAlertDialog.Builder(MainActivity.this).build()
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
										new FancyAlertDialog.Builder(MainActivity.this).build()
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
														db.updateData("1", data);
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
															stopService(new Intent(MainActivity.this, OreoService.class));
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
									toastutil.showConfusingToast("Something went wrong, Please try again.");
								}
                            }
                            //Toast.makeText(mContext,data , 0).show();
                        }
						else
						{
                            toastutil.showConfusingToast("Something went wrong, Please try again.");
                        }
                    }
                }
			});
		
		// setGraph();
		liveData();
		   int permissionCheck = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED)
		{
            //ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 101);
		}
    }
	
	private void storeDialogStatus(boolean isChecked){
        SharedPreferences mSharedPreferences = getSharedPreferences("CheckItem", MODE_PRIVATE);
        SharedPreferences.Editor mEditor = mSharedPreferences.edit();
        mEditor.putBoolean("item", isChecked);
        mEditor.apply();
    }

    private boolean getDialogStatus(){
        SharedPreferences mSharedPreferences = getSharedPreferences("CheckItem", MODE_PRIVATE);
        return mSharedPreferences.getBoolean("item", false);
    }
	
	public void liveData()
	{

        dataUpdate = new Thread(new Runnable() {
				@Override
				public void run()
				{
					while (!dataUpdate.getName().equals("stopped"))
					{
						vHandler.post(new Runnable() {

								@Override
								public void run()
								{
									//addDataSet();
									if (OreoService.isRunning)
									{
										graph.start();
									}
									else
									{
										graph.stop();
									}
								}
							});

						try
						{
							Thread.sleep(1000);
						}
						catch (InterruptedException e)
						{
							e.printStackTrace();
						}
						//  progressStatus--;
					}

				}
			});

        dataUpdate.setName("started");
        dataUpdate.start();

    }

	

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
	{
        switch (requestCode)
		{
            case 101:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
				{
                    toastutil.showSuccessToast("Application permissiom granted");
                }
				else
				{
                    toastutil.showErrorToast("Application permissiom denied");
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

	@Override
	protected void onDestroy()
	{
		// TODO: Implement this method
		//stopService(new Intent(MainActivity.this, OreoService.class));
		mph.trunksku.apps.myssh.logger.VPNLog.clearLog();
		mph.trunksku.apps.myssh.logger.VPNLog.flushLog();
		super.onDestroy();
	}

    public static void account()
	{
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View inflate = inflater.inflate(R.layout.layout_account, (ViewGroup) null);
        final EditText edUser = (EditText) inflate.findViewById(R.id.edUser);
        edUser.setText(sp.getString("xUser", ""));
        final EditText edPass = (EditText) inflate.findViewById(R.id.edPassword);
        edPass.setText(sp.getString("xPass", ""));
        new AlertDialog.Builder(mContext)
            .setTitle(mContext.getString(R.string.app_name) + " Account")
            .setView(inflate)
            .setPositiveButton("SAVE", new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface p1, int p2)
                {
                    // TODO: Implement this method
                    sp.edit().putString("xUser", edUser.getText().toString()).commit();
                    sp.edit().putString("xPass", edPass.getText().toString()).commit();
                }
            })
            .setNegativeButton("CANCEL", null)
            .create().show();
    }

    public void mRestart(Context c)
	{
        try
		{
            if (c != null)
			{
                PackageManager pm = c.getPackageManager();
                if (pm != null)
				{
                    Intent mStartActivity = pm.getLaunchIntentForPackage(
                        c.getPackageName()
                    );
                    if (mStartActivity != null)
					{
                        mStartActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        int mPendingIntentId = android.os.Process.myPid();
                        PendingIntent mPendingIntent = PendingIntent
                            .getActivity(c, mPendingIntentId, mStartActivity,
                                         PendingIntent.FLAG_CANCEL_CURRENT);
                        AlarmManager mgr = (AlarmManager) c.getSystemService(Context.ALARM_SERVICE);
                        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
                        //kill the application
                        System.exit(0);
                    }
					else
					{
                        Log.e(TAG, "Was not able to restart application, mStartActivity null");
                    }
                }
				else
				{
                    Log.e(TAG, "Was not able to restart application, PM null");
                }
            }
			else
			{
                Log.e(TAG, "Was not able to restart application, Context null");
            }
        }
		catch (Exception ex)
		{
            Log.e(TAG, "Was not able to restart application");
        }
    }

    public String inet(String path)
    {
        try
        {
            InputStream openRawResource = new FileInputStream(path);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

            for (int read = openRawResource.read(); read != -1; read = openRawResource.read())
            {
                byteArrayOutputStream.write(read);
            }
            openRawResource.close();
            return byteArrayOutputStream.toString();
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return "";
        }

    }
	
	
	
	/*public String cfgData()
    {
        InputStream openRawResource = getResources().openRawResource(R.raw.config);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try
        {
            for (int read = openRawResource.read(); read != -1; read = openRawResource.read())
            {
                byteArrayOutputStream.write(read);
            }
            openRawResource.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return byteArrayOutputStream.toString();
    }*/

	@SuppressWarnings("ConstantConditions")
	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setupToolbar()
	{
        toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setTitle("");
		
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setDisplayShowHomeEnabled(true);
		//Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.abc_ic_menu_overflow_material, null);
		//drawable = DrawableCompat.wrap(drawable);
		//DrawableCompat.setTint(drawable, Color.parseColor(getString(R.color.colorPrimary)));
		//getSupportActionBar().setHomeAsUpIndicator(drawable);
		/*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            ActionBar toolbar = getSupportActionBar();
			toolbar.setElevation(0);
        }*/
    }

	@Override
	protected void onResume()
	{
		// TODO: Implement this method
		super.onResume();
		
	}

	
	@Override
	public void onBackPressed()
	{
		// TODO: Implement this method
		if(HomeFragment.mBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED){
			HomeFragment.mBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
		}else{
			quiter();
		}
	}
	

	void quiter()
    {
        AlertDialog.Builder builder3 = new AlertDialog.Builder(MainActivity.this);
        builder3.setMessage("Quieres minimizar o salir?");
        builder3.setPositiveButton("SALIR", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface p1, int p2)
                {
                    // TODO: Implement this method
					if (OreoService.isRunning)
					{
						startService(new Intent(MainActivity.this, OreoService.class).setAction("STOP"));
                    }
                    if (android.os.Build.VERSION.SDK_INT >= 21) {
						finishAndRemoveTask();
					} else {
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
                    startActivity(intent);
                }
            });
        builder3.setNeutralButton("CANCELAR", null);
        builder3.show();
	}
}
