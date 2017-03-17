package tw.haotek.dutskin;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import c.min.tseng.R;
import c.min.tseng.dutskin.IModule;

/**
 * Created by Neo on 2015/12/3.
 */
public interface IVideo extends IModule {
    public static final String TAG = IVideo.class.getSimpleName();

    public int getVideoResolution();

    public void setVideoResolution(int resolution, boolean isDefault);

    public int getLoopRecording();

    public void setLoopRecording(int looprec, boolean isDefault);

    public boolean getDateStamp();

    public void setDateStamp(boolean stamp, boolean isDefault);

    public int getTimeLapse();

    public void setTimeLapse(int timelapse, boolean isDefault);

    public int getSelfTimer();

    public void setSelfTimer(int self, boolean isDefault);

    public int getFOV();

    public void setFOV(int fov, boolean isDefault);

    public boolean getWDR();

    public void setWDR(boolean wdr, boolean isDefault);

    public int getEV();

    public void setEV(int ev, boolean isDefault);

    public int getWhiteBalance();

    public void setWhiteBalance(int whitebalance, boolean isDefault);

    public int getCustomizationWhiteBalanceW();

    public void setCustomizationWhiteBalanceW(int cw, boolean isDefault);

    public int getCustomizationWhiteBalanceR();

    public void setCustomizationWhiteBalanceR(int cwr, boolean isDefault);

    public int getFunctionLock();

    public void setFunctionLock(int lock, boolean isDefault);

    public boolean getMotionDetect();

    public void setMotionDetect(boolean motion, boolean isDefault);

    public int getVideoFequency();

    public void setVideoFequency(int fequency, boolean isDefault);

    public static class ViewBuilder extends IModule.ViewBuilder {
        public ViewBuilder(IModule module) {
            super(module);
        }

        @Override
        protected IVideo getModule() {
            return (IVideo) super.getModule();
        }

        public View build(View convertView, ViewGroup parent) {
            final Context context = parent.getContext();
            final Resources resources = context.getResources();
            if (convertView == null) {
                convertView = View.inflate(context, R.layout.item_moudle_video_view, null);
                convertView.setTag(R.id.tag_view_holder, new ViewHolder(convertView));
            }
            final ViewHolder holder = (ViewHolder) convertView.getTag(R.id.tag_view_holder);
            final IVideo module = getModule();
            holder.modulename.setText(module.getNickName());
            holder.description.setText(R.string.resolution);

            holder.resolution.setAdapter(new BaseAdapter() {
                String[] mTexts = resources.getStringArray(R.array.module_video_resolution_texts);
                int[] mValues = resources.getIntArray(R.array.module_video_resolution_values);

                public int getCount() {
                    return mTexts.length;
                }

                @Override
                public String getItem(int position) {
                    return mTexts[position];
                }

                @Override
                public long getItemId(int position) {
                    return mValues[position];
                }

                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    if (convertView == null) {
                        convertView = View.inflate(parent.getContext(), android.R.layout.simple_list_item_single_choice, null);
                    }
                    final CheckedTextView text = (CheckedTextView) convertView;
                    text.setTextSize(12);
                    text.setText(getItem(position));
                    return convertView;
                }

                @Override
                public boolean hasStableIds() {
                    return true;
                }
            });
            holder.resolution.setOnItemSelectedListener(null);
            final int count = holder.resolution.getCount();
            for (int i = 0; i < count; ++i) {
                if (holder.resolution.getItemIdAtPosition(i) == module.getVideoResolution()) {
                    holder.resolution.setSelection(i, false);
                    break;
                }
            }
            holder.resolution.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {//FIXME
                    if (id == module.getVideoResolution()) {
                        return;
                    }
                    module.setVideoResolution(position, false);
//                    module.pushSettingsWithListener();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });

            holder.loopdesc.setText(R.string.looprecording);
            holder.loopr.setAdapter(new BaseAdapter() {
                String[] mTexts = resources.getStringArray(R.array.module_video_loopr_texts);
                int[] mValues = resources.getIntArray(R.array.module_video_loopr_values);

                public int getCount() {
                    return mTexts.length;
                }

                @Override
                public String getItem(int position) {
                    return mTexts[position];
                }

                @Override
                public long getItemId(int position) {
                    return mValues[position];
                }

                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    if (convertView == null) {
                        convertView = View.inflate(parent.getContext(), android.R.layout.simple_list_item_single_choice, null);
                    }
                    final CheckedTextView text = (CheckedTextView) convertView;
                    text.setTextSize(12);
                    text.setText(getItem(position));
                    return convertView;
                }

                @Override
                public boolean hasStableIds() {
                    return true;
                }
            });
            holder.loopr.setOnItemSelectedListener(null);
            final int countl = holder.loopr.getCount();
            for (int i = 0; i < countl; ++i) {
                if (holder.loopr.getItemIdAtPosition(i) == module.getLoopRecording()) {
                    holder.loopr.setSelection(i, false);
                    break;
                }
            }
            holder.loopr.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {//FIXME
                    if (id == module.getLoopRecording()) {
                        return;
                    }
                    module.setLoopRecording(position, false);
//                    module.pushSettingsWithListener();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });

            holder.datestampdesc.setText(R.string.datestamp);
            holder.datestampopstatus.setOnCheckedChangeListener(null);
            holder.datestampopstatus.setChecked(module.getDateStamp());
            holder.datestampopstatus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (module.getDateStamp() == isChecked)
                        return;

                    module.setDateStamp(isChecked, false);
//                    module.pushSettingsWithListener();
                }
            });

            holder.timelapsedescription.setText(R.string.timelapse);
            holder.timelapse.setAdapter(new BaseAdapter() {
                String[] mTexts = resources.getStringArray(R.array.module_video_timelapse_texts);
                int[] mValues = resources.getIntArray(R.array.module_video_timelapse_values);

                public int getCount() {
                    return mTexts.length;
                }

                @Override
                public String getItem(int position) {
                    return mTexts[position];
                }

                @Override
                public long getItemId(int position) {
                    return mValues[position];
                }

                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    if (convertView == null) {
                        convertView = View.inflate(parent.getContext(), android.R.layout.simple_list_item_single_choice, null);
                    }
                    final CheckedTextView text = (CheckedTextView) convertView;
                    text.setTextSize(12);
                    text.setText(getItem(position));
                    return convertView;
                }

                @Override
                public boolean hasStableIds() {
                    return true;
                }
            });
            holder.timelapse.setOnItemSelectedListener(null);
            final int countt = holder.timelapse.getCount();
            for (int i = 0; i < countt; ++i) {
                if (holder.timelapse.getItemIdAtPosition(i) == module.getTimeLapse()) {
                    holder.timelapse.setSelection(i, false);
                    break;
                }
            }
            holder.timelapse.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {//FIXME
                    if (id == module.getTimeLapse()) {
                        return;
                    }
                    module.setTimeLapse(position, false);
//                    module.pushSettingsWithListener();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });

            holder.selftimerdescription.setText(R.string.selftimer);
            holder.selftimer.setAdapter(new BaseAdapter() {
                String[] mTexts = resources.getStringArray(R.array.module_video_selftimer_texts);
                int[] mValues = resources.getIntArray(R.array.module_video_selftimer_values);

                public int getCount() {
                    return mTexts.length;
                }

                @Override
                public String getItem(int position) {
                    return mTexts[position];
                }

                @Override
                public long getItemId(int position) {
                    return mValues[position];
                }

                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    if (convertView == null) {
                        convertView = View.inflate(parent.getContext(), android.R.layout.simple_list_item_single_choice, null);
                    }
                    final CheckedTextView text = (CheckedTextView) convertView;
                    text.setTextSize(12);
                    text.setText(getItem(position));
                    return convertView;
                }

                @Override
                public boolean hasStableIds() {
                    return true;
                }
            });
            holder.selftimer.setOnItemSelectedListener(null);
            final int counts = holder.selftimer.getCount();
            for (int i = 0; i < counts; ++i) {
                if (holder.selftimer.getItemIdAtPosition(i) == module.getSelfTimer()) {
                    holder.selftimer.setSelection(i, false);
                    break;
                }
            }
            holder.selftimer.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {//FIXME
                    if (id == module.getSelfTimer()) {
                        return;
                    }
                    module.setSelfTimer(position, false);
//                    module.pushSettingsWithListener();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });

            holder.fovdescription.setText(R.string.fov);
            holder.fov.setAdapter(new BaseAdapter() {
                String[] mTexts = resources.getStringArray(R.array.module_video_fov_texts);
                int[] mValues = resources.getIntArray(R.array.module_video_fov_values);

                public int getCount() {
                    return mTexts.length;
                }

                @Override
                public String getItem(int position) {
                    return mTexts[position];
                }

                @Override
                public long getItemId(int position) {
                    return mValues[position];
                }

                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    if (convertView == null) {
                        convertView = View.inflate(parent.getContext(), android.R.layout.simple_list_item_single_choice, null);
                    }
                    final CheckedTextView text = (CheckedTextView) convertView;
                    text.setTextSize(12);
                    text.setText(getItem(position));
                    return convertView;
                }

                @Override
                public boolean hasStableIds() {
                    return true;
                }
            });
            holder.fov.setOnItemSelectedListener(null);
            final int countf = holder.fov.getCount();
            for (int i = 0; i < countf; ++i) {
                if (holder.fov.getItemIdAtPosition(i) == module.getFOV()) {
                    holder.fov.setSelection(i, false);
                    break;
                }
            }
            holder.fov.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {//FIXME
                    if (id == module.getFOV()) {
                        return;
                    }
                    module.setFOV(position, false);
//                    module.pushSettingsWithListener();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });

            holder.wdrdesc.setText(R.string.wdr);
            holder.wdr.setOnCheckedChangeListener(null);
            holder.wdr.setChecked(module.getWDR());
            holder.wdr.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (module.getWDR() == isChecked)
                        return;

                    module.setWDR(isChecked, false);
//                    module.pushSettingsWithListener();
                }
            });

            holder.evdescription.setText(R.string.ev);
            holder.ev.setAdapter(new BaseAdapter() {
                String[] mTexts = resources.getStringArray(R.array.module_video_ev_texts);
                int[] mValues = resources.getIntArray(R.array.module_video_ev_values);

                public int getCount() {
                    return mTexts.length;
                }

                @Override
                public String getItem(int position) {
                    return mTexts[position];
                }

                @Override
                public long getItemId(int position) {
                    return mValues[position];
                }

                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    if (convertView == null) {
                        convertView = View.inflate(parent.getContext(), android.R.layout.simple_list_item_single_choice, null);
                    }
                    final CheckedTextView text = (CheckedTextView) convertView;
                    text.setTextSize(12);
                    text.setText(getItem(position));
                    return convertView;
                }

                @Override
                public boolean hasStableIds() {
                    return true;
                }
            });
            holder.ev.setOnItemSelectedListener(null);
            final int countev = holder.ev.getCount();
            for (int i = 0; i < countev; ++i) {
                if (holder.ev.getItemIdAtPosition(i) == module.getEV()) {
                    holder.ev.setSelection(i, false);
                    break;
                }
            }
            holder.ev.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {//FIXME
                    if (id == module.getEV()) {
                        return;
                    }
                    module.setEV(position, false);
//                    module.pushSettingsWithListener();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
            holder.whitebalancedescription.setText(R.string.whitebalance);
            holder.whitebalance.setAdapter(new BaseAdapter() {
                String[] mTexts = resources.getStringArray(R.array.module_video_whitebalance_texts);
                int[] mValues = resources.getIntArray(R.array.module_video_whitebalance_values);

                public int getCount() {
                    return mTexts.length;
                }

                @Override
                public String getItem(int position) {
                    return mTexts[position];
                }

                @Override
                public long getItemId(int position) {
                    return mValues[position];
                }

                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    if (convertView == null) {
                        convertView = View.inflate(parent.getContext(), android.R.layout.simple_list_item_single_choice, null);
                    }
                    final CheckedTextView text = (CheckedTextView) convertView;
                    text.setTextSize(12);
                    text.setText(getItem(position));
                    return convertView;
                }

                @Override
                public boolean hasStableIds() {
                    return true;
                }
            });
            holder.whitebalance.setOnItemSelectedListener(null);
            final int countewb = holder.whitebalance.getCount();
            for (int i = 0; i < countewb; ++i) {
                if (holder.whitebalance.getItemIdAtPosition(i) == module.getWhiteBalance()) {
                    holder.whitebalance.setSelection(i, false);
                    break;
                }
            }
            holder.whitebalance.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {//FIXME
                    if (id == module.getWhiteBalance()) {
                        return;
                    }
                    module.setWhiteBalance(position, false);
//                    module.pushSettingsWithListener();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
            holder.customwdescription.setText(R.string.cwhitebalanceb);

            holder.customwbrdescription.setText(R.string.cwhitebalancer);

            holder.lockdescription.setText(R.string.exposurelock);
            holder.lock.setAdapter(new BaseAdapter() {
                String[] mTexts = resources.getStringArray(R.array.module_video_exposurelock_texts);
                int[] mValues = resources.getIntArray(R.array.module_video_exposurelock_values);

                public int getCount() {
                    return mTexts.length;
                }

                @Override
                public String getItem(int position) {
                    return mTexts[position];
                }

                @Override
                public long getItemId(int position) {
                    return mValues[position];
                }

                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    if (convertView == null) {
                        convertView = View.inflate(parent.getContext(), android.R.layout.simple_list_item_single_choice, null);
                    }
                    final CheckedTextView text = (CheckedTextView) convertView;
                    text.setTextSize(12);
                    text.setText(getItem(position));
                    return convertView;
                }

                @Override
                public boolean hasStableIds() {
                    return true;
                }
            });
            holder.lock.setOnItemSelectedListener(null);
            final int countewlock = holder.lock.getCount();
            for (int i = 0; i < countewlock; ++i) {
                if (holder.lock.getItemIdAtPosition(i) == module.getFunctionLock()) {
                    holder.lock.setSelection(i, false);
                    break;
                }
            }
            holder.lock.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {//FIXME
                    if (id == module.getFunctionLock()) {
                        return;
                    }
                    module.setFunctionLock(position, false);
//                    module.pushSettingsWithListener();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });

            holder.motiondesc.setText(R.string.motiondetection);
            holder.motion.setOnCheckedChangeListener(null);
            holder.motion.setChecked(module.getMotionDetect());
            holder.motion.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (module.getMotionDetect() == isChecked)
                        return;

                    module.setMotionDetect(isChecked, false);
//                    module.pushSettingsWithListener();
                }
            });


            holder.fequencydescription.setText(R.string.fequency);
            holder.fequency.setAdapter(new BaseAdapter() {
                String[] mTexts = resources.getStringArray(R.array.module_video_fequency_texts);
                int[] mValues = resources.getIntArray(R.array.module_video_fequency_values);

                public int getCount() {
                    return mTexts.length;
                }

                @Override
                public String getItem(int position) {
                    return mTexts[position];
                }

                @Override
                public long getItemId(int position) {
                    return mValues[position];
                }

                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    if (convertView == null) {
                        convertView = View.inflate(parent.getContext(), android.R.layout.simple_list_item_single_choice, null);
                    }
                    final CheckedTextView text = (CheckedTextView) convertView;
                    text.setTextSize(12);
                    text.setText(getItem(position));
                    return convertView;
                }

                @Override
                public boolean hasStableIds() {
                    return true;
                }
            });
            holder.fequency.setOnItemSelectedListener(null);
            final int countfequency = holder.fequency.getCount();
            for (int i = 0; i < countfequency; ++i) {
                if (holder.fequency.getItemIdAtPosition(i) == module.getVideoFequency()) {
                    holder.fequency.setSelection(i, false);
                    break;
                }
            }
            holder.fequency.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {//FIXME
                    if (id == module.getVideoFequency()) {
                        return;
                    }
                    module.setVideoFequency(position, false);
//                    module.pushSettingsWithListener();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });

            return convertView;
        }

        private static final class ViewHolder {
            private View resolutionlayout;
            public TextView modulename;//Module NAME
            private View spinnerlayout;
            public ImageView icon;//Function icon
            public TextView description;//Function name
            public Spinner resolution;

            private View looprecordinglayout;
            public TextView loopdesc;
            public ImageView loopicon;
            public Spinner loopr;

            private View datestamplayout;
            public TextView datestampdesc;
            public ImageView datestampicon;
            public SwitchCompat datestampopstatus;

            private View timelapselayout;
            public ImageView timelapseicon;//Function icon
            public TextView timelapsedescription;//Function name
            public Spinner timelapse;

            private View selftimerlayout;
            public ImageView selftimericon;//Function icon
            public TextView selftimerdescription;//Function name
            public Spinner selftimer;

            private View fovlayout;
            public ImageView fovicon;//Function icon
            public TextView fovdescription;//Function name
            public Spinner fov;

            private View wdrlayout;
            public TextView wdrdesc;
            public ImageView wdricon;
            public SwitchCompat wdr;

            private View evlayout;
            public ImageView evicon;//Function icon
            public TextView evdescription;//Function name
            public Spinner ev;

            private View whitebalancelayout;
            public ImageView whitebalanceicon;//Function icon
            public TextView whitebalancedescription;//Function name
            public Spinner whitebalance;

            private View customwblayout;
            public ImageView customwicon;//Function icon
            public TextView customwdescription;//Function name
            public Spinner customw;

            private View customwbrlayout;
            public ImageView customwbricon;//Function icon
            public TextView customwbrdescription;//Function name
            public Spinner customwbr;

            private View locklayout;
            public ImageView lockicon;//Function icon
            public TextView lockdescription;//Function name
            public Spinner lock;

            private View motionlayout;
            public TextView motiondesc;
            public ImageView motionicon;
            public SwitchCompat motion;


            private View fequencylayout;
            public ImageView fequencyicon;//Function icon
            public TextView fequencydescription;//Function name
            public Spinner fequency;

            public ViewHolder(View view) {
                resolutionlayout = (View) view.findViewById(R.id.resolutionlayout);
                modulename = (TextView) resolutionlayout.findViewById(R.id.function);
                //Resolution
                spinnerlayout = (View) resolutionlayout.findViewById(R.id.spinnerlayout);
                icon = (ImageView) spinnerlayout.findViewById(R.id.icon);
                description = (TextView) spinnerlayout.findViewById(R.id.description);
                resolution = (Spinner) spinnerlayout.findViewById(R.id.spinner_backoff);

                //looprecording
                looprecordinglayout = (View) view.findViewById(R.id.looprecordinglayout);
                loopicon = (ImageView) looprecordinglayout.findViewById(R.id.icon);
                loopdesc = (TextView) looprecordinglayout.findViewById(R.id.description);
                loopr = (Spinner) looprecordinglayout.findViewById(R.id.spinner_backoff);

                //DateStamp
                datestamplayout = (View) view.findViewById(R.id.datestamplayout);
                datestampicon = (ImageView) datestamplayout.findViewById(R.id.icon);
                datestampdesc = (TextView) datestamplayout.findViewById(R.id.description);
                datestampopstatus = (SwitchCompat) datestamplayout.findViewById(R.id.opstatus);

                //TimeLapse
                timelapselayout = (View) view.findViewById(R.id.timelapselayout);
                timelapseicon = (ImageView) timelapselayout.findViewById(R.id.icon);
                timelapsedescription = (TextView) timelapselayout.findViewById(R.id.description);
                timelapse = (Spinner) timelapselayout.findViewById(R.id.spinner_backoff);

                //SelfTimer
                selftimerlayout = (View) view.findViewById(R.id.selftimerlayout);
                selftimericon = (ImageView) selftimerlayout.findViewById(R.id.icon);
                selftimerdescription = (TextView) selftimerlayout.findViewById(R.id.description);
                selftimer = (Spinner) selftimerlayout.findViewById(R.id.spinner_backoff);

                //FOV
                fovlayout = (View) view.findViewById(R.id.fovlayout);
                fovicon = (ImageView) fovlayout.findViewById(R.id.icon);
                fovdescription = (TextView) fovlayout.findViewById(R.id.description);
                fov = (Spinner) fovlayout.findViewById(R.id.spinner_backoff);

                //WDR
                wdrlayout = (View) view.findViewById(R.id.wdrlayout);
                wdricon = (ImageView) wdrlayout.findViewById(R.id.icon);
                wdrdesc = (TextView) wdrlayout.findViewById(R.id.description);
                wdr = (SwitchCompat) wdrlayout.findViewById(R.id.opstatus);

                //EV
                evlayout = (View) view.findViewById(R.id.evlayout);
                evicon = (ImageView) evlayout.findViewById(R.id.icon);
                evdescription = (TextView) evlayout.findViewById(R.id.description);
                ev = (Spinner) evlayout.findViewById(R.id.spinner_backoff);

                //WhiteBalance
                whitebalancelayout = (View) view.findViewById(R.id.whitebalancelayout);
                whitebalanceicon = (ImageView) whitebalancelayout.findViewById(R.id.icon);
                whitebalancedescription = (TextView) whitebalancelayout.findViewById(R.id.description);
                whitebalance = (Spinner) whitebalancelayout.findViewById(R.id.spinner_backoff);

                //CWhiteBalance
                customwblayout = (View) view.findViewById(R.id.customwblayout);
                customwicon = (ImageView) customwblayout.findViewById(R.id.icon);
                customwdescription = (TextView) customwblayout.findViewById(R.id.description);
                customw = (Spinner) customwblayout.findViewById(R.id.spinner_backoff);

                //CWhiteBalanceR
                customwbrlayout = (View) view.findViewById(R.id.customwbrlayout);
                customwbricon = (ImageView) customwbrlayout.findViewById(R.id.icon);
                customwbrdescription = (TextView) customwbrlayout.findViewById(R.id.description);
                customwbr = (Spinner) customwbrlayout.findViewById(R.id.spinner_backoff);

                //Exposure lock
                locklayout = (View) view.findViewById(R.id.locklayout);
                lockicon = (ImageView) locklayout.findViewById(R.id.icon);
                lockdescription = (TextView) locklayout.findViewById(R.id.description);
                lock = (Spinner) locklayout.findViewById(R.id.spinner_backoff);

                //Motion Detect
                motionlayout = (View) view.findViewById(R.id.motionlayout);
                motionicon = (ImageView) motionlayout.findViewById(R.id.icon);
                motiondesc = (TextView) motionlayout.findViewById(R.id.description);
                motion = (SwitchCompat) motionlayout.findViewById(R.id.opstatus);

                //Fequency
                fequencylayout = (View) view.findViewById(R.id.fequencylayout);
                fequencyicon = (ImageView) fequencylayout.findViewById(R.id.icon);
                fequencydescription = (TextView) fequencylayout.findViewById(R.id.description);
                fequency = (Spinner) fequencylayout.findViewById(R.id.spinner_backoff);
            }
        }
    }

}
