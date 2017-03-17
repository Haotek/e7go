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
public interface IPhoto extends IModule {
    public static final String TAG = IPhoto.class.getSimpleName();

    public int getImageResolution();

    public void setImageResolution(int resolution);

    public boolean getDateStamp();

    public void setDateStamp(boolean stamp);

    public int getTimeLapse();

    public void setTimeLapse(int timelapse);

    public int getEV();

    public void setEV(int ev);

    public int getColorStyle();

    public void setColorStyle(int colorstyle);

    public int getWhiteBalance();

    public void setWhiteBalance(int whitebalance);

    public int getCustomizationWhiteBalanceW();

    public void setCustomizationWhiteBalanceW(int cw);

    public int getCustomizationWhiteBalanceR();

    public void setCustomizationWhiteBalanceR(int cwr);

    public static class ViewBuilder extends IModule.ViewBuilder {
        public ViewBuilder(IModule module) {
            super(module);
        }

        @Override
        protected IPhoto getModule() {
            return (IPhoto) super.getModule();
        }

        public View build(View convertView, ViewGroup parent) {
            final Context context = parent.getContext();
            final Resources resources = context.getResources();
            if (convertView == null) {
                convertView = View.inflate(context, R.layout.item_moudle_photo_view, null);
                convertView.setTag(R.id.tag_view_holder, new ViewHolder(convertView));
            }
            final ViewHolder holder = (ViewHolder) convertView.getTag(R.id.tag_view_holder);
            final IPhoto module = getModule();
            holder.modulename.setText(module.getNickName());
            holder.description.setText(R.string.imageresolution);

            holder.resolution.setAdapter(new BaseAdapter() {
                String[] mTexts = resources.getStringArray(R.array.module_image_resolution_texts);
                int[] mValues = resources.getIntArray(R.array.module_image_resolution_values);

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
                if (holder.resolution.getItemIdAtPosition(i) == module.getImageResolution()) {
                    holder.resolution.setSelection(i, false);
                    break;
                }
            }
            holder.resolution.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {//FIXME
                    if (id == module.getImageResolution()) {
                        return;
                    }
                    module.setImageResolution(position);
                    module.pushSettingsWithListener();
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

                    module.setDateStamp(isChecked);
                    module.pushSettingsWithListener();
                }
            });

            holder.timelapsedescription.setText(R.string.timelapse);
            holder.timelapse.setAdapter(new BaseAdapter() {
                String[] mTexts = resources.getStringArray(R.array.module_image_timelapse_texts);
                int[] mValues = resources.getIntArray(R.array.module_image_timelapse_values);

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
                    module.setTimeLapse(position);
                    module.pushSettingsWithListener();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
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
                    module.setEV(position);
                    module.pushSettingsWithListener();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });

            holder.colorstyledescription.setText(R.string.colorstyle);
            holder.colorstyle.setAdapter(new BaseAdapter() {
                String[] mTexts = resources.getStringArray(R.array.module_colorstyle_texts);
                int[] mValues = resources.getIntArray(R.array.module_colorstyle_values);

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
                    text.setText(getItem(position));
                    return convertView;
                }

                @Override
                public boolean hasStableIds() {
                    return true;
                }
            });
            holder.colorstyle.setOnItemSelectedListener(null);
            final int countf = holder.colorstyle.getCount();
            for (int i = 0; i < countf; ++i) {
                if (holder.colorstyle.getItemIdAtPosition(i) == module.getColorStyle()) {
                    holder.colorstyle.setSelection(i, false);
                    break;
                }
            }
            holder.colorstyle.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {//FIXME
                    if (id == module.getColorStyle()) {
                        return;
                    }
                    module.setColorStyle(position);
                    module.pushSettingsWithListener();
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
                    module.setWhiteBalance(position);
                    module.pushSettingsWithListener();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
            holder.customwdescription.setText(R.string.cwhitebalanceb);

            holder.customwbrdescription.setText(R.string.cwhitebalancer);


            return convertView;
        }

        private static final class ViewHolder {
            private View resolutionlayout;
            public TextView modulename;//Module NAME
            private View spinnerlayout;
            public ImageView icon;//Function icon
            public TextView description;//Function name
            public Spinner resolution;


            private View datestamplayout;
            public TextView datestampdesc;
            public ImageView datestampicon;
            public SwitchCompat datestampopstatus;

            private View timelapselayout;
            public ImageView timelapseicon;//Function icon
            public TextView timelapsedescription;//Function name
            public Spinner timelapse;

            private View evlayout;
            public ImageView evicon;//Function icon
            public TextView evdescription;//Function name
            public Spinner ev;

            private View colorstylelayout;
            public ImageView colorstyleicon;//Function icon
            public TextView colorstyledescription;//Function name
            public Spinner colorstyle;

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


            public ViewHolder(View view) {
                resolutionlayout = (View) view.findViewById(R.id.resolutionlayout);
                modulename = (TextView) resolutionlayout.findViewById(R.id.function);
                //Resolution
                spinnerlayout = (View) resolutionlayout.findViewById(R.id.spinnerlayout);
                icon = (ImageView) spinnerlayout.findViewById(R.id.icon);
                description = (TextView) spinnerlayout.findViewById(R.id.description);
                resolution = (Spinner) spinnerlayout.findViewById(R.id.spinner_backoff);

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

                //EV
                evlayout = (View) view.findViewById(R.id.evlayout);
                evicon = (ImageView) evlayout.findViewById(R.id.icon);
                evdescription = (TextView) evlayout.findViewById(R.id.description);
                ev = (Spinner) evlayout.findViewById(R.id.spinner_backoff);

                //ColorStyle
                colorstylelayout = (View) view.findViewById(R.id.colorstylelayout);
                colorstyleicon = (ImageView) colorstylelayout.findViewById(R.id.icon);
                colorstyledescription = (TextView) colorstylelayout.findViewById(R.id.description);
                colorstyle = (Spinner) colorstylelayout.findViewById(R.id.spinner_backoff);

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
            }
        }
    }

}
