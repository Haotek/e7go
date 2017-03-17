package tw.haotek.util;

import android.annotation.TargetApi;
import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.os.Build;
import android.util.Log;
import android.view.Surface;

import java.nio.ByteBuffer;
import java.util.Locale;

/**
 * Created by Neo on 2016/1/14 0014.
 */
public class MediaCodecDecoderThread extends Thread {
    private static final String TAG = MediaCodecDecoderThread.class.getSimpleName();
    private Surface mSurface;
    private String mUri;
    private MediaExtractor mExtractor;
    private ByteBuffer[] mInputBuffers;
    private ByteBuffer[] mOutputBuffers;
    private boolean mThreadStoped;
    private MediaCodec mDecoder;
    private MediaCodec.BufferInfo mInfo;
    boolean mPlaying;


    public MediaCodecDecoderThread(Surface surface, String videoUri) {
        mSurface = surface;
        mUri = videoUri;
        setupExtractor();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void setupExtractor() {
        mExtractor = new MediaExtractor();
        int videoIndex = 0;
        try {
            mExtractor.setDataSource(mUri);
            for (int trackIndex = 0; trackIndex < mExtractor.getTrackCount(); trackIndex++) {
                MediaFormat format = mExtractor.getTrackFormat(trackIndex);
                String mime = format.getString(MediaFormat.KEY_MIME);
                Log.d(TAG, "Show Video mime : " + mime);
                int fp = format.getInteger(MediaFormat.KEY_FRAME_RATE);
                Log.d(TAG, "Show Video framerate : " + fp);
                long ds = format.getLong(MediaFormat.KEY_DURATION);
                Log.d(TAG, "Show Video durationUs : " + ds);

                byte[] header_sps = {0, 0, 0, 1, 103, 100, 0, 40, -84, 52, -59, 1, -32, 17, 31, 120, 11, 80, 16, 16, 31, 0, 0, 3, 3, -23, 0, 0, -22, 96, -108};
                byte[] header_pps = {0, 0, 0, 1, 104, -18, 60, -128};
                byte[] csd_info = {0, 0, 0, 1, 103, 100, 0, 40, -84, 52, -59, 1, -32, 17, 31, 120, 11, 80, 16, 16, 31, 0, 0, 3, 3, -23, 0, 0, -22, 96, -108, 0, 0, 0, 1, 104, -18, 60, -128};
                if (mime != null && mime.startsWith("video/avc")) {
                    mExtractor.selectTrack(trackIndex);
                    videoIndex = trackIndex;
//                        format.setByteBuffer("csd-0", ByteBuffer.wrap(header_sps));
//                        format.setByteBuffer("csd-1", ByteBuffer.wrap(header_pps));
                    format.setByteBuffer("csd-0", ByteBuffer.wrap(csd_info));
                    format.setInteger(MediaFormat.KEY_CAPTURE_RATE, 24);
                    format.setInteger(MediaFormat.KEY_PUSH_BLANK_BUFFERS_ON_STOP, 1);
                    format.setInteger(MediaFormat.KEY_MAX_INPUT_SIZE, 1280 * 720);
                    mDecoder = MediaCodec.createDecoderByType(mime);
                    Log.e(TAG, "Show MediaCodec : " + mDecoder);
//                        mDecoder.configure(format, miSurface, null, 0);
                    mDecoder.configure(format, mSurface, null, 0);
//                            mDecoder.configure(mExtractor.getTrackFormat(videoIndex), mSurface, null, 0);
                    break;

                }
            }

        } catch (IllegalArgumentException e) {
            Log.d(TAG, "Show IllegalArgumentException : " + e);
        } catch (IllegalStateException e) {
            e.printStackTrace();
            Log.d(TAG, "Show IllegalStateException : " + e);
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "Show Exception : " + e);
        }
        if (mDecoder == null) {
            Log.e(TAG, "Can't find video info!");
            return;
        }

        mDecoder.start();
        mInfo = new MediaCodec.BufferInfo();
        mInputBuffers = mDecoder.getInputBuffers();
        mOutputBuffers = mDecoder.getOutputBuffers();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void run() {
        boolean isEOS = false;
        int timeoutUs = 0;
        for (; (!isEOS); ) {
            int inputBufferIndex = mDecoder.dequeueInputBuffer(timeoutUs);
            Log.d(TAG, " inputBufferIndex : " + inputBufferIndex);
            Log.d(TAG, String.format("Got index %d", inputBufferIndex));
            if (inputBufferIndex >= 0) {
                ByteBuffer inputBuffer = mInputBuffers[inputBufferIndex];
//                            ByteBuffer inputBuffer = mDecoder.getInputBuffer(inIndex);//FIXME API 21
                int sampleSize = mExtractor.readSampleData(inputBuffer, 0);
                if (sampleSize < 0) {
                    // Log.d( TAG, "InputBuffer BUFFER_FLAG_END_OF_STREAM" );
                    mDecoder.queueInputBuffer(inputBufferIndex, 0, 0, 0, MediaCodec.BUFFER_FLAG_END_OF_STREAM);
                    isEOS = true;
                } else {
                    // Log.d( TAG, "InputBuffer ADVANCING" );
                    mDecoder.queueInputBuffer(inputBufferIndex, 0, sampleSize, mExtractor.getSampleTime(), 0);
                    if ((mExtractor.getSampleFlags() & MediaExtractor.SAMPLE_FLAG_SYNC) > 0) {
                        Log.d(TAG, String.format(Locale.US, "Sync frame at %d", mExtractor.getSampleTime()));
                    }
                    mExtractor.advance();
                }
            } else {
                try {
                    sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            int outputBufferIndex = mDecoder.dequeueOutputBuffer(mInfo, timeoutUs);
            final int currentPosition = (int) mInfo.presentationTimeUs / 1000;
            Log.d(TAG, String.format(Locale.US, "current position is %d", currentPosition));
            switch (outputBufferIndex) {
                case MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED:
                    // Deprecated in API Level 21
                    Log.d(TAG, "Output buffer is changed (deprecated) ");
                    mInputBuffers = mDecoder.getInputBuffers();
                    mOutputBuffers = mDecoder.getOutputBuffers();
                    break;
                case MediaCodec.INFO_OUTPUT_FORMAT_CHANGED:
                    Log.d(TAG, "Output buffer format is changed  " + mDecoder.getOutputFormat());
                    break;
                case MediaCodec.INFO_TRY_AGAIN_LATER:
                    Log.d(TAG, "dequeueOutputBuffer timed out!");
                    Log.d(TAG, "Output buffer is not ready (" + outputBufferIndex + "), try again");
                    break;
                default:
                    Log.d(TAG, "default");
//                        // TODO: Retrieve decoded image by getOutputBuffer()
//                        // TODO: Count FPS
//                        // outputBuffer is ready to be processed or rendered.
                    ByteBuffer buffer = mOutputBuffers[outputBufferIndex];
                    // Log.v( TAG, "We can't use this buffer but render it due to the API limit, " + buffer );
                    mDecoder.releaseOutputBuffer(outputBufferIndex, true);
                    break;
            }

//            if (outputBufferIndex >= 0) {
//                mDecoder.releaseOutputBuffer(outputBufferIndex, true /*true:render to surface*/);
//            } else if (outputBufferIndex == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
//                Log.d(TAG, "Output buffer format is changed  " + mDecoder.getOutputFormat());
//            } else if (outputBufferIndex == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
//                // Deprecated in API Level 21
//                Log.d(TAG, "Output buffer is changed (deprecated) ");
//            } else if (outputBufferIndex == MediaCodec.INFO_TRY_AGAIN_LATER) {
//                Log.d(TAG, "Output buffer is not ready (" + outputBufferIndex + "), try again");
//            } else {
//                Log.d(TAG, "Other output buffer error (" + outputBufferIndex + ")");
//            }
//            if ((mInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
//                // Log.d( TAG, "OutputBuffer BUFFER_FLAG_END_OF_STREAM" );
//                isEOS = true;
//            }
        }
        mDecoder.stop();
        mDecoder.release();
        mExtractor.release();
    }
}

