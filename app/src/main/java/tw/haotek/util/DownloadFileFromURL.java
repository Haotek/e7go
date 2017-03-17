package tw.haotek.util;

import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by Neo on 2016/1/15 0015.
 */
public class DownloadFileFromURL extends AsyncTask<Object, String, String> {
    //public class DownloadFileFromURL extends AsyncTask<Object,java.lang.Integer, String> {
    private static final String TAG = DownloadFileFromURL.class.getSimpleName();
    private ImageView mImageView;
    private String mPath;
    private ProgressBar mProgressBar;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Log.d(TAG, "onPreExecute");
    }

    @Override
    protected String doInBackground(Object... params) {
        mImageView = (ImageView) params[0];
        mPath = ((String) params[1]);
        mProgressBar = (ProgressBar) params[2];
        int count;

        try {
            URL url = new URL(mPath);
            URLConnection conection = url.openConnection();
            conection.connect();
            int lenghtOfFile = conection.getContentLength();
            InputStream input = new BufferedInputStream(url.openStream(), 8192);
            final String[] path = mPath.split("/");
            for (int i = 0; i < path.length; ++i) {
                Log.d(TAG, "Show path  : " + path[i]);
            }
            final File rootFolder = new File(Environment.getExternalStorageDirectory() + "/e7go/" + path[3] + "/" + path[4]);

            if (!rootFolder.exists()) {
                rootFolder.mkdirs();//FIXME  someone say mkdirs not work
            }

            final File file = new File(rootFolder.getPath() + "/" + path[5].replace("MOV", "MP4"));
            if (!file.exists()) {
                Log.d(TAG, "! file.exists() : ");
                OutputStream output = new FileOutputStream(rootFolder.getPath() + "/" + path[5].replace("MOV", "MP4"), true);
                byte data[] = new byte[1024];
                long total = 0;
                while ((count = input.read(data)) != -1) {
                    total += count;
                    publishProgress("" + (int) ((total * 100) / lenghtOfFile));
                    output.write(data, 0, count);
                }
                output.flush();
                output.close();
                input.close();
            }
        } catch (Exception e) {
            Log.e("Error: ", e.getMessage());
        }
        return null;
    }

    //    protected void onProgressUpdate(String... progress) {
//    protected void onProgressUpdate(int... progress) {
//    protected void onProgressUpdate(java.lang.Integer... progress) {
    protected void onProgressUpdate(String... progress) {
//        pDialog.setProgress(Integer.parseInt(progress[0]));
        mProgressBar.setProgress(Integer.parseInt(progress[0]));
    }

    @Override
    protected void onPostExecute(String file_url) {
        Log.d(TAG, "onPostExecute");
        mProgressBar.setVisibility(View.INVISIBLE);
        mImageView.setVisibility(View.VISIBLE);
    }
}
