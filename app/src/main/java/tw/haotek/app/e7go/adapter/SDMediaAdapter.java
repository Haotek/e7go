package tw.haotek.app.e7go.adapter;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import c.min.tseng.R;
import c.min.tseng.dut.Device;
import tw.haotek.dut.data.Filelist;
import tw.haotek.dut.module.HaotekStorage;
import tw.haotek.util.DownloadFileFromURL;


/**
 * Created by Neo on 2015/11/25.
 */
public class SDMediaAdapter extends RecyclerView.Adapter<SDMediaAdapter.ViewHolder> {
    private static final String TAG = SDMediaAdapter.class.getSimpleName();
    private Device mDevice;
    private ArrayList<Filelist> mLists;
    private static Picasso sPicasso;

    private static Picasso getPicasso(Context context) {
        if (sPicasso == null) {
            sPicasso = Picasso.with(context);
        }
        return sPicasso;
    }

//    private static ExecutorService sExecutor = Executors.newSingleThreadExecutor();
//    private static ExecutorService sExecutor = Executors.newCachedThreadPool();
//    private static MediaMetadataRetriever sMMR = new MediaMetadataRetriever();

    public SDMediaAdapter(HaotekStorage sd) {//FIXME
        mDevice = sd.getDevice();
        mLists = sd.mFileList;
    }

    @Override
    public int getItemCount() {
        return mLists.size();
    }

    @Override
    public SDMediaAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View child = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sd_card_media, parent, false);
        ViewHolder vh = new ViewHolder(child);
        return vh;
    }

    @Override
    public void onBindViewHolder(final SDMediaAdapter.ViewHolder holder, final int position) {
        //FIXME
        holder.medianame.setText(mLists.get(position).mName.replace("/", ""));
        holder.mPath = "http://" + mDevice.getInetAddress() + mLists.get(position).mPath.replace("\\", "/");

        holder.itemView.post(holder);
        Log.d(TAG, "Path :" + "http://" + mDevice.getInetAddress() + mLists.get(position).mPath);
        holder.mediaaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                int[] textures = new int[1];
//                GLES20.glGenTextures(1, textures, 0);
//                GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textures[0]);
//                GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
//                GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
//                GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
//                GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);
//                int texture_id = textures[0];
//                SurfaceTexture mTexture = new SurfaceTexture(texture_id);
//                Surface surface = new Surface(mTexture);
////                MediaCodecDecoderThread decode = new MediaCodecDecoderThread(surface, (String) holder.mediaaction.getTag());
//                MediaCodecDecoderAdvanThread decode = new MediaCodecDecoderAdvanThread(surface, (String) holder.mediaaction.getTag());
//                decode.start();
                holder.mediaactionpb.setVisibility(View.VISIBLE);
                holder.mediaaction.setVisibility(View.INVISIBLE);
                new DownloadFileFromURL().execute(holder.mediaaction, holder.mPath, holder.mediaactionpb);//FIXME Recycled if INVISIBLE  will all Recycled
            }
        });
        holder.itemView.removeCallbacks(holder);
        holder.itemView.postDelayed(holder, 100);
//        final File file = new File(Environment.getExternalStorageDirectory().getPath() + "/e7go" + mLists.get(position).mPath.replace(".MOV", ".MP4"));
//        Log.d(TAG, "Show File Name : " + file);
//        if (file.exists()) {//FIXME Recycled if INVISIBLE  will all Recycled
//            Log.d(TAG, "Show Fileexists  " );
//            holder.mediaaction.setVisibility(View.INVISIBLE);
//        }
    }

    @Override
    public void onViewRecycled(ViewHolder holder) {
        Log.d(TAG, "onViewRecycled() ");
        //            Log.d(TAG, "onViewRecycled() called on " + holder.instance);
//            holder.resetMediaPlayer();
//            holder.video.resume();
        super.onViewRecycled(holder);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements Runnable {
        private ImageView mediasource;
        private ProgressBar mediaactionpb;
        private MediaMetadataRetriever mmr;
        private TextView medianame;
        private ImageView mediaaction;
        private String mPath;

        public ViewHolder(View itemView) {
            super(itemView);
            mediasource = (ImageView) itemView.findViewById(R.id.mediasource);
            mediasource.setVisibility(View.INVISIBLE);//FIXME  use  MediaMetadataRetriever load PreView too slow  need fixed
            mediaactionpb = (ProgressBar) itemView.findViewById(R.id.mediaactionpb);
            mmr = new MediaMetadataRetriever();
            medianame = (TextView) itemView.findViewById(R.id.medianame);
            mediaaction = (ImageView) itemView.findViewById(R.id.mediaaction);
        }

        @Override
        public void run() {
//            try {
//                mmr.setDataSource(mPath, new HashMap<String, String>());
//                final String date = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DATE);
//                Log.d(TAG, "date:" + date);
//                mediasource.setImageBitmap(mmr.getFrameAtTime());
//                mmr.release();
//            } catch (IllegalArgumentException e) {
//                Log.d(TAG, "Show IllegalArgumentException : " + e);
//            } catch (IllegalStateException e) {
//                e.printStackTrace();
//                Log.d(TAG, "Show IllegalStateException : " + e);
//            } catch (Exception e) {
//                e.printStackTrace();
//                Log.d(TAG, "Show Exception : " + e);
//            }
        }
    }
}
