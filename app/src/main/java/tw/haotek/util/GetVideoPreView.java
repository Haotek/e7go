package tw.haotek.util;

import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.util.HashMap;

/**
 * Created by Neo on 2016/1/14 0014.
 */
public class GetVideoPreView extends AsyncTask<Object, Void, Bitmap> {
    private static final String TAG = GetVideoPreView.class.getSimpleName();
    private MediaMetadataRetriever mMMR = new MediaMetadataRetriever();
    private ImageView mImageView;
    private String mPath;

    @Override
    protected Bitmap doInBackground(Object... params) {
        mImageView = (ImageView) params[0];
        mPath = (String) params[1];
        try {
            mMMR.setDataSource(mPath, new HashMap<String, String>());
            final String date = mMMR.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DATE);
        } catch (IllegalArgumentException e) {
            Log.d(TAG, "Show IllegalArgumentException : " + e);
        } catch (IllegalStateException e) {
            e.printStackTrace();
            Log.d(TAG, "Show IllegalStateException : " + e);
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "Show Exception : " + e);
        }
        return mMMR.getFrameAtTime();
    }

    protected void onPostExecute(Bitmap bitmap) {
        mImageView.setImageBitmap(bitmap);
    }
}
