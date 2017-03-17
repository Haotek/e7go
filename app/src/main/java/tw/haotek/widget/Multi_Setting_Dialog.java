package tw.haotek.widget;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import c.min.tseng.R;

/**
 * Created by Neo on 2015/12/15.
 */
public class Multi_Setting_Dialog extends AlertDialog implements DialogInterface.OnClickListener {//FIXME auto support multi ?
    private final String TAG = Multi_Setting_Dialog.class.getSimpleName();
    Context mContext;
    boolean click_dismiss = true;
    On_Dialog_button_click_Listener On_button_click_Listener;

    public Multi_Setting_Dialog(Context context) {
        super(context, R.style.ThemeDialogCustom);
        mContext = context;
        LayoutInflater mInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = mInflater.inflate(R.layout.multi_setting_dialog, null);
        setView(view);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.multi_setting_dialog);

        ImageView videomode = (ImageView) findViewById(R.id.videomode);
        videomode.setImageResource(R.drawable.ic_normal_rec);
        videomode.setOnClickListener(button_click);
        TextView videomodet = (TextView) findViewById(R.id.videomodet);
        videomodet.setText(R.string.normal_rec);

        ImageView timelapsemode = (ImageView) findViewById(R.id.timelapsemode);
        timelapsemode.setImageResource(R.drawable.ic_timer_pic);
        timelapsemode.setOnClickListener(button_click);
        TextView timelapsemodet = (TextView) findViewById(R.id.timelapsemodet);
        timelapsemodet.setText(R.string.timer_pic);

        ImageView photomode = (ImageView) findViewById(R.id.photomode);
        photomode.setImageResource(R.drawable.ic_normal_pic);
        photomode.setOnClickListener(button_click);
        TextView photomodet = (TextView) findViewById(R.id.photomodet);
        photomodet.setText(R.string.normal_pic);
    }

    public void set_button_click_Listener(On_Dialog_button_click_Listener _On_button_click_Listener) {
        On_button_click_Listener = _On_button_click_Listener;
    }

    @Override
    public void onClick(DialogInterface arg0, int arg1) {

    }

    private View.OnClickListener button_click = new View.OnClickListener() {
        public void onClick(View v) {
            if (On_button_click_Listener == null) {
                dismiss();
                return;
            }
            switch (v.getId()) {
                case R.id.videomode:
                    take_video_click(Multi_Setting_Dialog.this);
                    break;
                case R.id.timelapsemode:
                    take_timelapse_click(Multi_Setting_Dialog.this);
                    break;
                case R.id.photomode:
                    take_photo_click(Multi_Setting_Dialog.this);
                    break;
                default:
                    break;
            }
            if (click_dismiss)
                dismiss();
        }
    };

    public interface On_Dialog_button_click_Listener {
        public void take_photo_click(final DialogInterface Dialog);

        public void take_video_click(final DialogInterface Dialog);

        public void take_timelapse_click(final DialogInterface Dialog);
    }

    public void take_photo_click(DialogInterface Dialog) {
        On_button_click_Listener.take_photo_click(Dialog);
    }

    public void take_video_click(DialogInterface Dialog) {
        On_button_click_Listener.take_video_click(Dialog);
    }

    public void take_timelapse_click(DialogInterface Dialog) {
        On_button_click_Listener.take_timelapse_click(Dialog);
    }
}
