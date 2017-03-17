package tw.haotek.app.e7go.adapter;

import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tutk.IOTC.Camera;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.mobicents.rtsp.DefaultRtspRequest;
import org.mobicents.rtsp.RtspClientStackImpl;
import org.mobicents.rtsp.RtspListener;
import org.mobicents.rtsp.RtspMethod;
import org.mobicents.rtsp.RtspRequest;
import org.mobicents.rtsp.RtspResponse;
import org.mobicents.rtsp.RtspVersion;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.ArrayList;

import c.min.tseng.R;
import c.min.tseng.dut.Device;
import c.min.tseng.managers.DeviceManager;
import c.min.tseng.managers.DeviceObserver;
import c.min.tseng.ui.AutoVLCTextureVideoView;
import tw.haotek.app.e7go.C;
import tw.haotek.util.HaotekCallback;

/**
 * Created by Neo on 2016/1/6 0006.
 */
public class FullScreenAdapter extends RecyclerView.Adapter<FullScreenAdapter.ViewHolder> {
    private static final String TAG = FullScreenAdapter.class.getSimpleName();
    private HaotekCallback mCallback;
    private Device mDevice;
    private ArrayList<Device> mDeviceList = new ArrayList<>();
    final DeviceObserver mDeviceObserver = new DeviceObserver() {
        @Override
        public void onDeviceAdded(Device device) {
            onDevicesChanged();
        }

        @Override
        public void onDeviceRemoved(Device device) {
            onDevicesChanged();
        }

        @Override
        public void onDeviceMadeSeen(Device device) {
            Log.d(TAG, "onDeviceMadeSeen");
            onDevicesChanged();
        }

        @Override
        public void onDeviceMadeUnseen(Device device) {
            onDevicesChanged();
        }

        @Override
        public void onDevicesChanged() {
            Log.d(TAG, "onDevicesChanged  ");
            mCallback.removeCallbacks(mUpdateRunnable);
            mCallback.postDelayed(mUpdateRunnable, C.TIMEOUT_UI_POST_HOLDOFF);
        }

        @Override
        public void onDeviceContentChanged(Device device, Uri uri) {
            Log.d(TAG, "onDeviceContentChanged Uri : " + uri);
            onDevicesChanged();
        }
    };

    final Runnable mUpdateRunnable = new Runnable() {
        @Override
        public void run() {
            checkDevice();
            notifyDataSetChanged();
        }
    };


    public FullScreenAdapter(Device device, HaotekCallback callback) {
        this.mDevice = device;
        mDeviceList.add(device);
        this.mCallback = callback;
        mDeviceObserver.onDevicesChanged();
    }

    private void checkDevice() {
        final DeviceManager manager = DeviceManager.getDeviceManager();
        final int count = manager.getSeenDeviceCount();
        mDeviceList.clear();
        for (int i = 0; i < count; ++i) {
            final Device device = manager.getSeenDevice(i);
            if (!mDeviceList.contains(device)) {
                mDeviceList.add(device);
            }
        }
    }

    public void startMonitor() {
        DeviceManager.getDeviceManager().registerObserver(mDeviceObserver);
    }

    public void stopMonitor() {
        DeviceManager.getDeviceManager().unregisterObserver(mDeviceObserver);
    }

    @Override
    public int getItemCount() {
        return mDeviceList.size();
    }

    @Override
    public FullScreenAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View child = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_liveview_video, parent, false);
        ViewHolder vh = new ViewHolder(child);
        return vh;
    }

    @Override
    public void onBindViewHolder(final FullScreenAdapter.ViewHolder holder, int position) {
        final Device device = mDeviceList.get(position);
        if (device.getP2PMode()) {
            holder.tutkvideo.setMaxZoom(3.0f);
            final Camera agent = (Camera) device.getDeviceP2PAgent();
            holder.tutkvideo.enableDither(agent.mEnableDither);
            holder.tutkvideo.attachCamera(agent, 0);
            agent.startShow(0, true, true);
            holder.video.setVisibility(View.INVISIBLE);
        } else {
            holder.tutkvideo.setVisibility(View.INVISIBLE);
            Log.d(TAG, "Show IP : " + device.getInetAddress());
            final Uri myUri = Uri.parse("rtsp://" + device.getInetAddress() + "/xxx.mov");//FIXME  VLC use Uri
            holder.video.stePath(myUri);
            holder.video.setLandscape(true);
        }
    }

    private Runnable mutiThread = new Runnable() {
        public void run() {
            URI uri = null;
            try {
                uri = new URI("rtsp://192.168.1.254");
                RtspRequest request = new DefaultRtspRequest(RtspVersion.RTSP_1_0, RtspMethod.DESCRIBE, uri.toASCIIString());
                request.setHeader(HttpHeaders.Names.HOST, request.getHost());
                RtspClientStackImpl clientStack = new RtspClientStackImpl("127.0.0.1", 5051);
                MyRtspListener listener = new MyRtspListener();
                clientStack.setRtspListener(listener);
                clientStack.start();
                clientStack.sendRquest(request);
            } catch (URISyntaxException e) {
                e.printStackTrace();
                Log.d(TAG, "URISyntaxException : " + e);
            } catch (UnknownHostException e) {
                e.printStackTrace();
                Log.d(TAG, "UnknownHostException : " + e);
            }

        }
    };

    private class MyRtspListener implements RtspListener {
        public void onRtspRequest(RtspRequest request, Channel chanel) {
            System.out.println("Received request " + request);

        }

        public void onRtspResponse(RtspResponse response) {
            System.out.println("Received RtspResponse " + response);

        }
    }

    @Override
    public void onViewRecycled(ViewHolder holder) {
        Log.d(TAG, "onViewRecycled() ");
        super.onViewRecycled(holder);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        AutoVLCTextureVideoView video;
        com.tutk.IOTC.Monitor tutkvideo;

        public ViewHolder(View itemView) {
            super(itemView);
            video = (AutoVLCTextureVideoView) itemView.findViewById(R.id.video);
            tutkvideo = (com.tutk.IOTC.Monitor) itemView.findViewById(R.id.tutkvideo);

        }
    }
}

