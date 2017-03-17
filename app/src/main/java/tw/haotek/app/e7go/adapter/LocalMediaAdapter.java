package tw.haotek.app.e7go.adapter;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import c.min.tseng.R;
import tw.haotek.util.ThirdPartyApp;

/**
 * Created by Neo on 2015/11/25.
 */
public class LocalMediaAdapter extends RecyclerView.Adapter<LocalMediaAdapter.ViewHolder> {
    private static final String TAG = LocalMediaAdapter.class.getSimpleName();
    private Context mContext;
    private ArrayList<String> mPathList = new ArrayList<String>();

    private static Picasso sPicasso;

    private static Picasso getPicasso(Context context) {
        if (sPicasso == null) {
            sPicasso = Picasso.with(context);
            Log.d(TAG, "new Picasso");
        }
        return sPicasso;
    }

    public LocalMediaAdapter() {
        getFileAbsolutePathList(new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/e7go"));
    }

    public LocalMediaAdapter(Context context) {
        this.mContext = context;
        final ContentResolver resolver = mContext.getContentResolver();
        final Cursor cursor = resolver.query(MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI, null, null, null, null);
//        Cursor c = resolver.query(MediaStore.Video.Thumbnails.EXTERNAL_CONTENT_URI, null, null, null, null);
        cursor.moveToFirst();
        while (cursor.moveToNext()) {
            final int index = cursor.getColumnIndexOrThrow(MediaStore.Images.Thumbnails.DATA);
            final String src = cursor.getString(index);
            mPathList.add(src);
        }
        cursor.close();
    }

    private void getFileAbsolutePathList(File dir) { //FIXME  can not use  FileOperater.class  will get null
        final File listFile[] = dir.listFiles();
        if (listFile != null && listFile.length > 0) {
            for (int i = 0; i < listFile.length; i++) {
                if (listFile[i].isDirectory()) {
                    getFileAbsolutePathList(listFile[i]);
                } else {
                    if (listFile[i].getName().endsWith(".MP4")) {
                        mPathList.add(listFile[i].getAbsolutePath());
                        notifyDataSetChanged();
                    }
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return mPathList.size();
    }

    @Override
    public LocalMediaAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View child = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_local_media, parent, false);
        ViewHolder vh = new ViewHolder(child);
        return vh;
    }

    @Override
    public void onBindViewHolder(final LocalMediaAdapter.ViewHolder holder, int position) {
        final Context context = holder.itemView.getContext();
//        holder.mediasource.setImageURI(Uri.parse(mPath.get(position)));
//        getPicasso(holder.itemView.getContext()).load(mPath.get(position)).into(holder.mediasource);//FIXME can not use Picasso???
        holder.mPath = mPathList.get(position);
        final String[] rawname = mPathList.get(position).split("/");
        final int splitcount = rawname.length;
        holder.medianame.setText(rawname[splitcount - 1]);
        final MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        try {
            mmr.setDataSource(mPathList.get(position));
            final String date = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DATE);
//            Log.d(TAG, "date:" + date);
            holder.mediasource.setImageBitmap(mmr.getFrameAtTime(TimeUnit.MICROSECONDS.convert(100, TimeUnit.MILLISECONDS), mmr.OPTION_CLOSEST_SYNC));
            mmr.release();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
        holder.mediaaction.setTag(mPathList.get(position));
        holder.mediaaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File file = new File((String) holder.mediaaction.getTag());
                if (ThirdPartyApp.isAppInstall("com.tencent.mm")) {
                    Intent share = new Intent();
                    share.setAction(Intent.ACTION_SEND);
//                    ComponentName comp = new ComponentName("com.tencent.mm", "com.tencent.mm.ui.tools.ShareToTimeLineUI");
//                    share.setComponent(comp);
//                    share.setAction("android.intent.action.SEND");
                    share.setType("video/*");
                    share.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
                    if (share.resolveActivity(context.getPackageManager()) != null) {
                        context.startActivity(share);
//                    context.startActivity(Intent.createChooser(share, "Share video using"));
                    }

                }
            }
        });
    }

    @Override
    public void onViewRecycled(ViewHolder holder) {
        Log.d(TAG, "onViewRecycled() ");
        super.onViewRecycled(holder);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView mediasource;
        private TextView medianame;
        private ImageView mediaaction;
        private ImageView medialove;
        private String mPath;

        public ViewHolder(View itemView) {
            super(itemView);
            mediasource = (ImageView) itemView.findViewById(R.id.mediasource);
            medianame = (TextView) itemView.findViewById(R.id.medianame);
            mediaaction = (ImageView) itemView.findViewById(R.id.mediaaction);
            medialove = (ImageView) itemView.findViewById(R.id.medialove);
        }
    }
}
