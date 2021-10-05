package mph.trunksku.apps.myssh.core;

import android.util.Log;
import java.util.HashMap;

public class ResponseHeader {
    private byte[] mBody = null;
    int mBodyLength = 0;
    private HashMap<String, String> mHeaders;
    private int mStatus;
    String mStatusText = "";

    private ResponseHeader(HashMap<String, String> headerList) {
        this.mStatus = Integer.parseInt((String) headerList.get("Status"));
        headerList.remove("Status");
        this.mHeaders = headerList;
    }

    public int getStatus() {
        return this.mStatus;
    }

    public byte[] getBody() {
        return this.mBody;
    }

    public void setBody(byte[] data) {
        this.mBody = data;
    }

    public void setStatusText(String statusText) {
        this.mStatusText = statusText;
    }

    public String getStatusText() {
        return this.mStatusText;
    }

    public String getHeaderValue(String name) {
        return (String) this.mHeaders.get(name);
    }

    public void setBodyLength(int size) {
        this.mBodyLength = size;
    }

    public int getBodyLength() {
        return this.mBodyLength;
    }

    public static ResponseHeader parse(String headerString) {
        Log.i("Response RequestHeader", headerString);
        HashMap<String, String> parsed = new HashMap();
        headerString = headerString.replace("HTTP/1.0", "HTTP/1.1");
        headerString = headerString.substring(headerString.indexOf("HTTP/1.1"));
        Log.i("Response Header", headerString);
        String[] lines = headerString.split("\r\n");
        String[] statusTokens = lines[0].split(" ");
        String statusText = "";
        if (statusTokens.length == 4) {
            statusText = statusTokens[2] + " " + statusTokens[3];
        } else {
            statusText = statusTokens[2];
        }
        parsed.put("Status", statusTokens[1]);
        for (int i = 1; i < lines.length; i++) {
            String l = lines[i];
            Log.i("Response Header", l);
            if (l.length() > 0 && l.contains(": ")) {
                String[] vPairs = l.split(": ");
                parsed.put(vPairs[0], vPairs[1]);
            }
        }
        ResponseHeader temp = new ResponseHeader(parsed);
        temp.setStatusText(statusText);
        if (temp.getHeaderValue("Content-Length") != null) {
            temp.setBodyLength(Integer.parseInt(temp.getHeaderValue("Content-Length")));
        }
        return temp;
    }
}

