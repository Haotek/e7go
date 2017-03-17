package tw.haotek.app.e7go.wizard.model;

import android.content.Context;

import c.min.tseng.R;
import c.min.tseng.ui.wizardpager.AbstractWizardModel;
import c.min.tseng.ui.wizardpager.PageList;
import c.min.tseng.ui.wizardpager.PageViewCallbacks;
import tw.haotek.app.e7go.wizard.ChoiceDevicePage;
import tw.haotek.app.e7go.wizard.ShowGuidePage;


/**
 * Created by Neo on 2015/11/7.
 */
public class AddDeviceWizardModel extends AbstractWizardModel {
    private static final String TAG = AddDeviceWizardModel.class.getSimpleName();


    public AddDeviceWizardModel(Context context, PageViewCallbacks callbacks) {
        super(context, callbacks);
    }

    @Override
    protected PageList onNewRootPageList(PageViewCallbacks callbacks) {
        PageList mypagelist = new PageList();
        ShowGuidePage page1 = new ShowGuidePage(this, callbacks, mContext.getString(R.string.show_guide));
        mypagelist.add(page1);
//        page1.setRequired(true);
        ChoiceDevicePage page2 = new ChoiceDevicePage(this, callbacks, mContext.getString(R.string.choice_ap_mode_device));
//        page2.setRequired(true);
        mypagelist.add(page2);
        return mypagelist;
    }
}
