package mph.trunksku.apps.myssh.view;

import android.content.*;
import android.content.res.*;
import android.graphics.*;
import android.os.*;
import android.support.v7.app.*;
import android.util.*;
import com.github.mikephil.charting.charts.*;
import com.github.mikephil.charting.components.*;
import com.github.mikephil.charting.components.XAxis.*;
import com.github.mikephil.charting.data.*;
import com.github.mikephil.charting.formatter.*;
import com.github.mikephil.charting.interfaces.datasets.*;
import de.blinkt.openvpn.core.*;
import de.blinkt.openvpn.core.TrafficHistory.*;
import java.util.*;
import com.masfha.id.*;
import mph.trunksku.apps.myssh.*;
import mph.trunksku.apps.myssh.util.*;

public class GraphHelper implements VpnStatus.ByteCountListener {
    private static final int TIME_PERIOD_SECCONDS = 0;
    private static Handler mHandler;
    private static GraphHelper m_GraphHelper;
    private String TAG = "GraphHelper";
    private int mColor;
    private Context mContext;
    private LineChart mLineChart;
    private boolean mLogScale = false;
    public Runnable triggerRefresh = new Runnable() {
        @Override
        public void run() {
            refreshGraph();
            GraphHelper.mHandler.postDelayed(this, (long) 3000);
        }
    };

	private SharedPreferences sp;

    class ValueFormat extends ValueFormatter {
        @Override
        public String getFormattedValue(float f) {
            if (mLogScale && f < 2.1f) {
                //return "< 100\u2009bit/s";
            }
            if (mLogScale) {
                f = ((float) Math.pow((double) 10, (double) f)) / ((float) 8);
            }
			//return render_bandwidth((long) f);
            return humanReadableByteCount((long) f, true, mContext.getResources());
        }
    }

    @Override
    public void updateByteCount(long j, long j2, long j3, long j4) {
        ((AppCompatActivity) this.mContext).runOnUiThread(new Runnable() {
				@Override
				public void run() {
					GraphHelper.mHandler.removeCallbacks(triggerRefresh);
					refreshGraph();
					GraphHelper.mHandler.postDelayed(triggerRefresh, (long) 3000);
				}
			});
    }

    public static synchronized GraphHelper getHelper() {
        GraphHelper graphHelper;
        synchronized (GraphHelper.class) {
            if (m_GraphHelper == null) {
                m_GraphHelper = new GraphHelper();
            }
            if (mHandler == null) {
                mHandler = new Handler();
            }
            graphHelper = m_GraphHelper;
        }
        return graphHelper;
    }

    public GraphHelper color(int i) {
        this.mColor = i;
        return m_GraphHelper;
    }

    public GraphHelper chart(LineChart lineChart) {
        this.mLineChart = lineChart;
        return m_GraphHelper;
    }

    public GraphHelper with(Context context) {
		sp = ApplicationBase.getSharedPreferences();
        this.mContext = context;
        return m_GraphHelper;
    }

    public void refreshGraph() {
        try {
            this.mLineChart.getDescription().setEnabled(false);
            this.mLineChart.setTouchEnabled(false);
            this.mLineChart.setDrawGridBackground(false);
            this.mLineChart.getLegend().setEnabled(false);
            XAxis xAxis = this.mLineChart.getXAxis();
            xAxis.setPosition(XAxisPosition.BOTTOM);
            xAxis.setDrawGridLines(false);
            xAxis.setDrawLabels(false);
            xAxis.setDrawAxisLine(false);
            YAxis axisLeft = this.mLineChart.getAxisLeft();
            axisLeft.setLabelCount(5, false);
            axisLeft.setDrawAxisLine(false);
            //if (ApplicationBase.isDarkTheme()) {
               // axisLeft.setTextColor(Color.WHITE);
            // } else {
                axisLeft.setTextColor(Color.BLACK);
           // }
            axisLeft.setValueFormatter(new ValueFormat());
            this.mLineChart.getAxisRight().setEnabled(false);
            LineData dataSet = null;
			if(sp.getInt("VPNTunMod", R.id.ssh) == R.id.ssh) {	
				dataSet = getDataSet(0);
			}else{
				dataSet = getDataSet2(0);
			}
            float yMax = dataSet.getYMax();
            if (this.mLogScale) {
                axisLeft.setAxisMinimum(2.0f);
                axisLeft.setAxisMaximum((float) Math.ceil((double) yMax));
                axisLeft.setLabelCount((int) Math.ceil((double) (yMax - 2.0f)));
            } else {
                axisLeft.setAxisMinimum(0.0f);
                axisLeft.resetAxisMaximum();
                axisLeft.setLabelCount(6);
            }
            if (((ILineDataSet) dataSet.getDataSetByIndex(0)).getEntryCount() < 1) {
                this.mLineChart.setData((LineData) null);
            } else {
                this.mLineChart.setData(dataSet);
            }
            //this.mLineChart.setNoDataText(this.mContext.getString(R.string.hello_world));
            this.mLineChart.invalidate();
        } catch (Exception e) {
            Log.e(this.TAG, e.toString());
        }
    }
	
	private LineData getDataSet2(int i) {
        float f;
        List linkedList = new LinkedList();
        LinkedList linkedList2 = new LinkedList();
        LinkedList seconds = VpnStatus.trafficHistory.getSeconds();
        long j = (long) 2000;
        if (seconds.size() == 0) {
            seconds = TrafficHistory.getDummyList();
        }
        long j2 = (long) 0;
        if (this.mLogScale) {
            f = (float) 2;
        } else {
            f = (float) 0;
        }
        long currentTimeMillis = System.currentTimeMillis();
        long j3 = (long) 0;
        long j4 = (long) 0;
        for (TrafficDatapoint trafficDatapoint : seconds) {
            if (300000 == ((long) 0) || currentTimeMillis - trafficDatapoint.timestamp <= 300000) {
                if (j3 == ((long) 0)) {
                    j3 = ((TrafficDatapoint) seconds.peek()).timestamp;
                    j4 = ((TrafficDatapoint) seconds.peek()).in;
                }
                float f2 = ((float) (trafficDatapoint.timestamp - j3)) / 100.0f;
                float f3 = ((float) (trafficDatapoint.in - j4)) / ((float) (j / ((long) 1000)));
                j4 = trafficDatapoint.in;
                if (this.mLogScale) {
                    f3 = Math.max(2.0f, (float) Math.log10((double) (f3 * ((float) 8))));
                }
                if (j2 > ((long) 0) && trafficDatapoint.timestamp - j2 > ((long) 2) * j) {
                    linkedList.add(new Entry(((float) ((j2 - j3) + j)) / 100.0f, f));
                    linkedList2.add(new Entry(((float) ((j2 - j3) + j)) / 100.0f, f));
                    linkedList.add(new Entry(f2 - (((float) j) / 100.0f), f));
                    linkedList2.add(new Entry(f2 - (((float) j) / 100.0f), f));
                }
                j2 = trafficDatapoint.timestamp;
                linkedList.add(new Entry(f2, f3));
            }
        }
        if (j2 < currentTimeMillis - j) {
            if (currentTimeMillis - j2 > (((long) 2) * j) * ((long) 1000)) {
                linkedList.add(new Entry(((float) ((j2 - j3) + (((long) 1000) * j))) / 100.0f, f));
            }
            linkedList.add(new Entry((float) ((currentTimeMillis - j3) / ((long) 100)), f));
        }
        List arrayList = new ArrayList();
        LineDataSet lineDataSet = new LineDataSet(linkedList, "test"/*this.mContext.getString(R.string.data_in)*/);
        setLineDataAttributes(lineDataSet, this.mColor);
        arrayList.add(lineDataSet);
        return new LineData(arrayList);
    }
	

    private LineData getDataSet(int io) {
        
		List<Long> dList = StoredData.downloadList;
		ArrayList<Entry> e1 = new ArrayList<Entry>();
		float t1;

		for (int i = 0; i < dList.size(); i++) {

			t1 = (float) dList.get(i);  //convert o Kilobyte
			//t2 = (float) uList.get(i) / 1024;
			e1.add(new Entry(i, t1));
		}
        List arrayList = new ArrayList();
        LineDataSet lineDataSet = new LineDataSet(e1, this.mContext.getString(R.string.hello_world));
        setLineDataAttributes(lineDataSet, this.mColor);
        arrayList.add(lineDataSet);
        return new LineData(arrayList);
    }

    private void setLineDataAttributes(LineDataSet lineDataSet, int i) {
        lineDataSet.setLineWidth((float) 2);
        lineDataSet.setCircleRadius((float) 1);
        lineDataSet.setDrawCircles(true);
        lineDataSet.setCircleColor(i);
        lineDataSet.setColor(i);
        lineDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
		lineDataSet.setCubicIntensity(0.2f);
		lineDataSet.setDrawFilled(true);
		lineDataSet.setDrawValues(false);
		lineDataSet.setFillColor(i);
		lineDataSet.setFillAlpha(100);
		lineDataSet.setDrawHorizontalHighlightIndicator(false);
    }

    public void start() {
		if(sp.getInt("VPNTunMod", R.id.ssh) != R.id.ssh) {
			VpnStatus.addByteCountListener(this);
		}
        GraphHelper.mHandler.removeCallbacks(triggerRefresh);
		refreshGraph();
		GraphHelper.mHandler.postDelayed( triggerRefresh, (long) 3000);
    }

    public void stop() {
        mHandler.removeCallbacks(this.triggerRefresh);
        if(sp.getInt("VPNTunMod", R.id.ssh) != R.id.ssh) {	
			VpnStatus.removeByteCountListener(this);
		}
        this.mLineChart.clear();
        this.mLineChart.invalidate();
    }
	
	private static String render_bandwidth(long bw) {
        String postfix;
        float div;
        Object[] objArr;
        float bwf = (float) bw;
        if (bwf >= 1.0E12f) {
            postfix = "TB";
            div = 1.0995116E12f;
        } else if (bwf >= 1.0E9f) {
            postfix = "GB";
            div = 1.0737418E9f;
        } else if (bwf >= 1000000.0f) {
            postfix = "MB";
            div = 1048576.0f;
        } else if (bwf >= 1000.0f) {
            postfix = "KB";
            div = 1024.0f;
        } else {
            objArr = new Object[1];
            objArr[0] = Float.valueOf(bwf);
            return String.format("%.0f", objArr);
        }
        objArr = new Object[1];
        objArr[0] = Float.valueOf(bwf / div);
        objArr[1] = postfix;
        return String.format("%.2f %s", objArr);
    }
	public static String humanReadableByteCount(long bytes, boolean speed, Resources res) {
        if (speed) bytes = bytes * 8;
        int unit = speed ? 1000 : 1024;
        int exp = Math.max(0, Math.min((int) (Math.log(bytes) / Math.log(unit)), 3));
        float bytesUnit = (float) (bytes / Math.pow(unit, exp));
        if (speed) switch (exp) {
				case 0:
					return res.getString(R.string.bits_per_second, bytesUnit);
				case 1:
					return res.getString(R.string.kbits_per_second, bytesUnit);
				case 2:
					return res.getString(R.string.mbits_per_second, bytesUnit);
				default:
					return res.getString(R.string.gbits_per_second, bytesUnit);
			}
        else switch (exp) {
				case 0:
					return res.getString(R.string.volume_byte, bytesUnit);
				case 1:
					return res.getString(R.string.volume_kbyte, bytesUnit);
				case 2:
					return res.getString(R.string.volume_mbyte, bytesUnit);
				default:
					return res.getString(R.string.volume_gbyte, bytesUnit);
			}
    }
}

