package tw.haotek.util;

import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.util.Log;
import android.view.Surface;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Locale;

/**
 * Created by Neo on 2016/1/14 0014.
 */
public class MediaCodecDecoderAdvanThread extends Thread {
    private static final String TAG = MediaCodecDecoderAdvanThread.class.getSimpleName();
    private Surface mSurface;
    private String mUri;
    private MediaExtractor mExtractor;
    private ByteBuffer[] mInputBuffers;
    private ByteBuffer[] mOutputBuffers;
    private boolean mThreadStoped;
    private MediaCodec mDecoder;
    private MediaCodec.BufferInfo mInfo;
    boolean mPlaying;
    // Indicates reach of input video data queue full, ready for render output buffer
    boolean inputBufferFull = false;

    // Indicates reach of end of stream
    boolean endOfStream = false;


    public MediaCodecDecoderAdvanThread(Surface surface, String videoUri) {
        mSurface = surface;
        mUri = videoUri;
    }

    private void playTask() throws IOException {
        long counterTime;
        long deltaTime;
        int frameCount;

        /*
        Flow of video playback
        1.  MediaExtractor set source video resource (R.raw.xxx)
        2.  MediaExtractor get video type (In MediaFormat) and select first video track ("video/")
        3.  MediaCodec creates decoder with video type (MediaFormat.KEY_MINE)
        4.  Configure MediaCodec as "decoder" and start()
        5. Create thread for fill up input buffer until End-Of-Stream
        6.  Looping until last frame of output buffer
        7. End of loop
        9. Stop MediaCodec
        10. Release MediaCodec, MediaExtractor
        */
        // Play resource video file or video file path
        mExtractor = new MediaExtractor();

        mExtractor.setDataSource(mUri);
        // Find and select first video track. No audio in this example
        int numTracks = mExtractor.getTrackCount();
        int trackSearchIndex;
        for (trackSearchIndex = 0; trackSearchIndex < numTracks; ++trackSearchIndex) {
            final MediaFormat format = mExtractor.getTrackFormat(trackSearchIndex);
            final String mine_type = format.getString(MediaFormat.KEY_MIME);
            Log.d(TAG, "Show Video mime : " + mine_type);
            final int fp = format.getInteger(MediaFormat.KEY_FRAME_RATE);
            Log.d(TAG, "Show Video framerate : " + fp);
            final long ds = format.getLong(MediaFormat.KEY_DURATION);
            Log.d(TAG, "Show Video durationUs : " + ds);
            //
//            int numCodecs = MediaCodecList.getCodecCount();
//            Log.d(TAG, "Show MediaCodecList Count  : " + numCodecs);
            //
            byte[] header_sps = {0, 0, 0, 1, 103, 100, 0, 40, -84, 52, -59, 1, -32, 17, 31, 120, 11, 80, 16, 16, 31, 0, 0, 3, 3, -23, 0, 0, -22, 96, -108};
            byte[] header_pps = {0, 0, 0, 1, 104, -18, 60, -128};
            byte[] csd_info = {0, 0, 0, 1, 103, 100, 0, 40, -84, 52, -59, 1, -32, 17, 31, 120, 11, 80, 16, 16, 31, 0, 0, 3, 3, -23, 0, 0, -22, 96, -108, 0, 0, 0, 1, 104, -18, 60, -128};
            if (mine_type != null && mine_type.startsWith("video/")) {
                // Must select the track we are going to get data by readSampleData()
                mExtractor.selectTrack(trackSearchIndex);
                // Set required key for MediaCodec in decoder mode
                // Check http://developer.android.com/reference/android/media/MediaFormat.html
                // TODO: Program codec KEYs with proper value
                //https://android.googlesource.com/platform/cts/+/jb-mr2-release/tests/tests/media/src/android/media/cts/DecodeEditEncodeTest.java
                format.setInteger(MediaFormat.KEY_CAPTURE_RATE, 30);
                format.setInteger(MediaFormat.KEY_PUSH_BLANK_BUFFERS_ON_STOP, 1);
//                format.setByteBuffer("csd-0", ByteBuffer.wrap(csd_info));
                format.setInteger(MediaFormat.KEY_MAX_INPUT_SIZE, 1280 * 720);

                format.setInteger(MediaFormat.KEY_SAMPLE_RATE, 24);
                format.setInteger(MediaFormat.KEY_CHANNEL_COUNT, 1);
                format.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface);
//                format.setInteger(MediaFormat.KEY_BIT_RATE,format.getInteger(MediaFormat.KEY_BIT_RATE));
                format.setInteger(MediaFormat.KEY_FRAME_RATE, format.getInteger(MediaFormat.KEY_FRAME_RATE));
//                format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL,format.getInteger(MediaFormat.KEY_I_FRAME_INTERVAL));

                mDecoder = MediaCodec.createDecoderByType(mine_type);
                // Initial MediaCodec as "decoder" with the MINE_TYPE of the selected track
                mDecoder.configure(format, mSurface, null, 0/* 0:decoder 1:encoder */);
                Log.e(TAG, "Show MediaCodec : " + mDecoder);
                break;
            }
        }


        //  Check if valid track has been selected by selectTrack()
        if ((numTracks == 0) || (trackSearchIndex == numTracks) || mDecoder == null) {
            Log.d(TAG, "No video track found!");
            return;
        }

        mDecoder.start();
        mOutputBuffers = mDecoder.getOutputBuffers();

//         Thread for preparing input buffer without impact FPS
        new Thread(new Runnable() {
            public void run() {
                try {
                    inputBufferTask();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        // Count FPS
        counterTime = -1; // -1: Not initialized
        frameCount = 0;
        // Output buffer index
        int outputBufferIndex;
        // Wait until output buffer available for rendering
        int timeoutDequeueOutUs = -1;
        // Timestamp of last frame rendered
        long lastRenderTime = 0;
        // Time has consumed by loop when output buffer timestamp is eariler ss
        // MediaCodec BufferInfo for output buffer timestamp information, for frame rate control
        MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
        // Main output buffer render loop until last frame
        while (true) {
            // Looping for fill up input buffer
            if (!inputBufferFull && !endOfStream) {
                continue;
            }

            // Wait until output buffer available for rendering with "timeoutDequeueOutUs = -1"
            outputBufferIndex = mDecoder.dequeueOutputBuffer(bufferInfo, timeoutDequeueOutUs);
            final int currentPosition = (int) bufferInfo.presentationTimeUs / 1000;
            Log.d(TAG, String.format(Locale.US, "current position is %d", currentPosition));
            if (outputBufferIndex >= 0) {
                Log.d(TAG, "Default");
                // Frame rate control
                if (lastRenderTime == 0) {
                    lastRenderTime = System.currentTimeMillis();
                } else {
                    long renderTime = lastRenderTime + (bufferInfo.presentationTimeUs / 1000);
                    while (renderTime > System.currentTimeMillis()) {
                        // Loop until  correct render time
                    }
                }

                // outputBuffer is ready to be processed or rendered.
                // If surface is SurfaceTexture, onFrameAvailable() will be called.
                ByteBuffer buffer = mOutputBuffers[outputBufferIndex];
                mDecoder.releaseOutputBuffer(outputBufferIndex, true /*true:render to surface*/);

                // Count FPS
                frameCount++;
                if (counterTime > 0) {
                    deltaTime = System.currentTimeMillis() - counterTime;
                    if (deltaTime > 1000) {
                        Log.d(TAG, (((float) frameCount / (float) deltaTime) * 1000) + " fps");
                        counterTime = System.currentTimeMillis();
                        frameCount = 0;
                    }
                } else {
                    // Initialize FPS start count timestamp in first frame
                    counterTime = System.currentTimeMillis();
                }

                // End up rendering if last frame
                if (bufferInfo.flags == MediaCodec.BUFFER_FLAG_END_OF_STREAM) {
                    Log.v(TAG, "Last frame");
                    break;
                }
            } else if (outputBufferIndex == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                Log.d(TAG, "Output buffer format is changed  " + mDecoder.getOutputFormat());
            } else if (outputBufferIndex == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
                // Deprecated in API Level 21
                mOutputBuffers = mDecoder.getOutputBuffers();
                Log.d(TAG, "Output buffer is changed (deprecated) ");
            } else if (outputBufferIndex == MediaCodec.INFO_TRY_AGAIN_LATER) {
                Log.d(TAG, "Output buffer is not ready (" + outputBufferIndex + "), try again");
            } else {
                Log.d(TAG, "Other output buffer error (" + outputBufferIndex + ")");
            }
        }
        Log.d(TAG, "Play complete");
        mDecoder.stop();
        mDecoder.release();
        mExtractor.release();
    }

    // Thread of task that fill up MediaCodec inputbuffer
    private void inputBufferTask() throws IOException {
        Log.d(TAG, "inputBufferTask()");
        // Available input buffer index
        int inputBufferIndex;
        // TODO: Optimize timeout value to improve performance
        int timeoutDequeueInUs = 10;
        // MediaCodec Input buffer
        ByteBuffer inputBuffer;
        // Input video data size
        int sampleSize;
        // Input video timestamp
        long sampleTime;

        endOfStream = false;
        while (true) {
            inputBufferIndex = mDecoder.dequeueInputBuffer(timeoutDequeueInUs);
            if (inputBufferIndex >= 0) {
                inputBufferFull = false;
                inputBuffer = mDecoder.getInputBuffers()[inputBufferIndex];
                //                            inputBufferr = mDecoder.getInputBuffer(inIndex);//FIXME API 21
                sampleSize = mExtractor.readSampleData(inputBuffer, 0);
                Log.d(TAG, "Show video data size : " + sampleSize);
                if (sampleSize > 0) {
                    // Video data is valid,send input buffer to MediaCodec for decode
                    sampleTime = mExtractor.getSampleTime();
                    Log.d(TAG, "Show video timestamp : " + sampleTime);
                    mDecoder.queueInputBuffer(inputBufferIndex, 0, sampleSize, sampleTime, 0);
                    // Advance to next video data
                    if ((mExtractor.getSampleFlags() & MediaExtractor.SAMPLE_FLAG_SYNC) > 0) {
                        Log.d(TAG, String.format(Locale.US, "Sync frame at %d", mExtractor.getSampleTime()));
                    }
//                    mExtractor.advance();
                    if (!mExtractor.advance()) {
                        // // End-Of-Stream (EOS). No more data in video source.
                        break;
                    }
                } else {
                    // // End-Of-Stream (EOS). No more data in video source.
                    mDecoder.queueInputBuffer(inputBufferIndex, 0, 0, 0, MediaCodec.BUFFER_FLAG_END_OF_STREAM);
                    break;
                }
            } else {
                inputBufferFull = true;
                try {
                    sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        Log.d(TAG, "End-Of-Stream");
        endOfStream = true;
    }

    @Override
    public void run() {
        Log.v(TAG, "run()");
        try {
            playTask();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

