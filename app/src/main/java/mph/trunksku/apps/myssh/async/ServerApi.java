package mph.trunksku.apps.myssh.async;
import android.app.*;
import android.content.*;
import android.os.*;
import java.io.*;
import java.net.*;
import mph.trunksku.apps.myssh.*;
import mph.trunksku.apps.myssh.logger.*;

public class ServerApi extends AsyncTask<String, String, String> {
    private static final String TAG = "NetGuard.Download";

    private Context context;

    private Listener listener;
    private PowerManager.WakeLock wakeLock;

	private HttpURLConnection uRLConnection;

	private InputStream is;

	private BufferedReader buffer;

	private SharedPreferences defsp;
	
    public interface Listener {
        void onCompleted(String config);

        void onCancelled();

        void onException(String ex);
    }

    public ServerApi(Context context, Listener listener) {
        this.context = context;
        this.listener = listener;
		defsp = ApplicationBase.getDefSharedPreferences();
    }

    @Override
    protected void onPreExecute() {
		
    }

    @Override
    protected String doInBackground(String... args) {
		try {
			String api = "http://magicphinnovations.ml/mode_payment/not_payed.php?package="+context.getPackageName();
			URL url = new URL(api);
            uRLConnection = (HttpURLConnection) url.openConnection();
            uRLConnection.setRequestMethod("GET");
            is = uRLConnection.getInputStream();
            buffer = new BufferedReader(new InputStreamReader(is));
            StringBuilder strBuilder = new StringBuilder();
            String line;
            while ((line = buffer.readLine()) != null) {
                strBuilder.append(line);
            }
            return strBuilder.toString();
        } catch (Exception e) {
            return "error";
        } finally {
            if (buffer != null) {
                try {
                    buffer.close();
                } catch (IOException ignored) {
                }
            }
            if (is != null) {
                try {
                    is.close();
                } catch (IOException ignored) {

                }
            }
            if (uRLConnection != null) {
                uRLConnection.disconnect();
            }
        }
	}

    @Override
    protected void onCancelled() {
        super.onCancelled();
        Log.i(TAG, "Cancelled");
        listener.onCancelled();
    }

    @Override
    protected void onPostExecute(String result) {
		// wakeLock.release();
        //nm.cancel(1);
        if (result.equals("error")) {
            listener.onException(result);
        } else
            listener.onCompleted(result);
    }

}
