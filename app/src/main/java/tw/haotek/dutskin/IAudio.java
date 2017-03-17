package tw.haotek.dutskin;

import android.content.Context;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import c.min.tseng.R;
import c.min.tseng.dutskin.IModule;

/**
 * Created by Neo on 2015/12/3.
 */
public interface IAudio extends IModule {
    public static final String TAG = IAudio.class.getSimpleName();

    public boolean getRecordingAudio();

    public void setRecordingAudio(boolean state);


    public static class ViewBuilder extends IModule.ViewBuilder {
        public ViewBuilder(IModule module) {
            super(module);
        }

        @Override
        protected IAudio getModule() {
            return (IAudio) super.getModule();
        }

        public View build(View convertView, ViewGroup parent) {
            final Context context = parent.getContext();
            if (convertView == null) {
                convertView = View.inflate(parent.getContext(), R.layout.item_function_expand_switch, null);
                convertView.setTag(R.id.tag_view_holder, new ViewHolder(convertView));
            }
            final ViewHolder holder = (ViewHolder) convertView.getTag(R.id.tag_view_holder);
            final IAudio module = getModule();
            holder.modulename.setText(module.getNickName());
            holder.expand.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (holder.switchlayout.getVisibility() == View.GONE) {
                        holder.switchlayout.setVisibility(View.VISIBLE);
                    } else {
                        holder.switchlayout.setVisibility(View.GONE);
                    }
                }
            });
            holder.description.setText(R.string.recordaudio);

            holder.opstatus.setOnCheckedChangeListener(null);
            holder.opstatus.setChecked(module.getRecordingAudio());
            holder.opstatus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (module.getRecordingAudio() == isChecked)
                        return;

                    module.setRecordingAudio(isChecked);
//                    module.pushSettingsWithListener();
                }
            });
            return convertView;
        }

        private static final class ViewHolder {
            public TextView modulename;//Module NAME
            public ImageButton expand;
            public View switchlayout;
            public ImageView icon;
            public TextView description;
            public SwitchCompat opstatus;

            public ViewHolder(View view) {
                modulename = (TextView) view.findViewById(R.id.function);
                expand = (ImageButton) view.findViewById(R.id.expand);
                switchlayout = (View) view.findViewById(R.id.switchlayout);
                icon = (ImageView) switchlayout.findViewById(R.id.icon);
                description = (TextView) switchlayout.findViewById(R.id.description);
                opstatus = (SwitchCompat) switchlayout.findViewById(R.id.opstatus);
            }
        }
    }
}
