package tw.haotek.dutskin;

import android.content.Context;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import c.min.tseng.R;
import c.min.tseng.dutskin.IModule;

/**
 * Created by Neo on 2015/12/3.
 */
public interface IBattery extends IModule {
    public static final String TAG = IBattery.class.getSimpleName();

    public int getState();

    public void setState(int state);


    public static class ViewBuilder extends IModule.ViewBuilder {
        public ViewBuilder(IModule module) {
            super(module);
        }

        @Override
        protected IBattery getModule() {
            return (IBattery) super.getModule();
        }

        public View build(View convertView, ViewGroup parent) {//FIXME
            final Context context = parent.getContext();
            if (convertView == null) {
                convertView = View.inflate(parent.getContext(), R.layout.item_function_expand_switch, null);
                convertView.setTag(R.id.tag_view_holder, new ViewHolder(convertView));
            }
            final ViewHolder holder = (ViewHolder) convertView.getTag(R.id.tag_view_holder);
            final IBattery module = getModule();
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
            holder.opstatus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
            convertView.setVisibility(View.GONE);
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
