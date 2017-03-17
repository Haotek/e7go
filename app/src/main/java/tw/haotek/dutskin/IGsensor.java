package tw.haotek.dutskin;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import c.min.tseng.R;
import c.min.tseng.dutskin.IModule;

/**
 * Created by Neo on 2015/12/3.
 */
public interface IGsensor extends IModule {
    public static final String TAG = IGsensor.class.getSimpleName();

    public int getState();

    public void setState(int state);

    public static class ViewBuilder extends IModule.ViewBuilder {
        public ViewBuilder(IModule module) {
            super(module);
        }

        @Override
        protected IGsensor getModule() {
            return (IGsensor) super.getModule();
        }

        public View build(View convertView, ViewGroup parent) {
            final Context context = parent.getContext();
            if (convertView == null) {
                convertView = View.inflate(parent.getContext(), R.layout.item_function_expand_spinner, null);
                convertView.setTag(R.id.tag_view_holder, new ViewHolder(convertView));
            }
            final ViewHolder holder = (ViewHolder) convertView.getTag(R.id.tag_view_holder);
            final IGsensor module = getModule();
            holder.modulename.setText(module.getNickName());
            holder.expand.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (holder.spinnerlayout.getVisibility() == View.GONE) {
                        holder.spinnerlayout.setVisibility(View.VISIBLE);
                    } else {
                        holder.spinnerlayout.setVisibility(View.GONE);
                    }
                }
            });
            holder.description.setText(R.string.gsensor);
            holder.spinner_backoff.setAdapter(new BaseAdapter() {
                String[] mTexts = context.getResources().getStringArray(R.array.module_gsensor_texts);

                public int getCount() {//FIXME
                    return mTexts.length;
                }

                @Override
                public String getItem(int position) {//FIXME
                    return mTexts[position];
                }

                @Override
                public long getItemId(int position) {
                    return position;
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
            holder.spinner_backoff.setOnItemSelectedListener(null);

            final int countt = holder.spinner_backoff.getCount();
            Log.d(TAG, "Show count  : " + countt);
            for (int i = 0; i < countt; ++i) {
                if (holder.spinner_backoff.getItemIdAtPosition(i) == module.getState()) {
                    Log.d(TAG, "Show State : " + module.getState());
                    holder.spinner_backoff.setSelection(i, false);
                    break;
                }
            }
            holder.spinner_backoff.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {//FIXME
                    if (id == module.getState()) {
                        return;
                    }
                    module.setState(position);
//                    module.pushSettingsWithListener();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
            return convertView;
        }

        private static final class ViewHolder {
            public TextView modulename;//Module NAME
            public ImageButton expand;
            public View spinnerlayout;
            public ImageView icon;
            public TextView description;
            public Spinner spinner_backoff;

            public ViewHolder(View view) {
                modulename = (TextView) view.findViewById(R.id.function);
                expand = (ImageButton) view.findViewById(R.id.expand);
                spinnerlayout = (View) view.findViewById(R.id.spinnerlayout);
                icon = (ImageView) spinnerlayout.findViewById(R.id.icon);
                description = (TextView) spinnerlayout.findViewById(R.id.description);
                spinner_backoff = (Spinner) spinnerlayout.findViewById(R.id.spinner_backoff);
            }
        }
    }

}
