package mph.trunksku.apps.myssh.core;
import android.os.*;
import android.content.*;
import android.annotation.*;

@SuppressLint({"HandlerLeak"})
public class VPNHandler extends Handler
{

	private Context mContext;

	public VPNHandler(Context c) {
		mContext = c;
	}
	
	@Override
	public void handleMessage(Message msg)
	{
		// TODO: Implement this method
		switch (msg.what)
		{
			case 0: // Connecting
				break;
			case 1: // Authenticating
				break;
			case 2: // Forwarding
				break;
			case 3: // Connected
				break;
			case 4: // Disconnected
				break;
		}
		super.handleMessage(msg);
	}
	
}
