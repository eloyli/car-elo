package mph.trunksku.apps.myssh.model;

public interface StatusChangeListener {
    public void onStatusChanged(String status, Boolean isRunning);
    public void onLogReceived(String logString);
}
