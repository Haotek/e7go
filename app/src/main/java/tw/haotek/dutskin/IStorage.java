package tw.haotek.dutskin;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import c.min.tseng.R;
import c.min.tseng.dutskin.IModule;

/**
 * Created by Neo on 2015/12/3.
 */
public interface IStorage extends IModule {
    public static final String TAG = IStorage.class.getSimpleName();

    public int getVolume();

    public void setVolume(int volume);

    public int getTotalVolume();

    public int getState();

    public void setState(int state);

    public void setFormat(boolean format);


    public static class ViewBuilder extends IModule.ViewBuilder {
        public ViewBuilder(IModule module) {
            super(module);
        }

        @Override
        protected IStorage getModule() {
            return (IStorage) super.getModule();
        }

        public View build(View convertView, ViewGroup parent) {
            final Context context = parent.getContext();
            if (convertView == null) {
                convertView = View.inflate(parent.getContext(), R.layout.item_function_expand_confirm, null);
                convertView.setTag(R.id.tag_view_holder, new ViewHolder(convertView));
            }
            final ViewHolder holder = (ViewHolder) convertView.getTag(R.id.tag_view_holder);
            final IStorage module = getModule();
            holder.modulename.setText(module.getNickName());
            holder.expand.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (holder.buttonlayout.getVisibility() == View.GONE) {
                        holder.buttonlayout.setVisibility(View.VISIBLE);
                    } else {
                        holder.buttonlayout.setVisibility(View.GONE);
                    }
                }
            });
            holder.confirm.setText(R.string.storage_format);
            holder.confirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    module.setFormat(true);
                }
            });

            return convertView;
        }

        private static final class ViewHolder {
            public TextView modulename;//Module NAME
            public ImageButton expand;
            public View buttonlayout;
            public Button confirm;

            public ViewHolder(View view) {
                modulename = (TextView) view.findViewById(R.id.function);
                expand = (ImageButton) view.findViewById(R.id.expand);
                buttonlayout = (View) view.findViewById(R.id.buttonlayout);
                confirm = (Button) buttonlayout.findViewById(R.id.confirm);
            }
        }
    }

}
