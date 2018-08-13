package com.lqr.wechat.ui.activity;

import android.view.View;

import com.lqr.optionitemview.OptionItemView;
import com.lqr.wechat.R;
import com.lqr.wechat.app.AppConst;
import com.lqr.wechat.app.MyApp;
import com.lqr.wechat.model.cache.UserCache;
import com.lqr.wechat.ui.base.BaseActivity;
import com.lqr.wechat.ui.base.BasePresenter;
import com.lqr.wechat.widget.CustomDialog;
import com.lqr.wechat.app.AppConst;
import com.lqr.wechat.app.MyApp;
import com.lqr.wechat.model.cache.UserCache;

import butterknife.BindView;
import io.rong.imlib.RongIMClient;

/**
 * @创建者 CSDN_LQR
 * @描述 设置界面
 */
public class SettingActivity extends BaseActivity {

    private View mExitView;

    @BindView(com.lqr.wechat.R.id.oivAbout)
    OptionItemView mOivAbout;
    @BindView(com.lqr.wechat.R.id.oivHelpFeedback)
    OptionItemView mOivHelpFeedback;
    @BindView(com.lqr.wechat.R.id.oivExit)
    OptionItemView mOivExit;
    private CustomDialog mExitDialog;

    @Override
    public void initListener() {
        mOivAbout.setOnClickListener(v -> jumpToActivity(AboutActivity.class));
        mOivHelpFeedback.setOnClickListener(v1 -> jumpToWebViewActivity(AppConst.WeChatUrl.HELP_FEED_BACK));
        mOivExit.setOnClickListener(v -> {
            if (mExitView == null) {
                mExitView = View.inflate(this, com.lqr.wechat.R.layout.dialog_exit, null);
                mExitDialog = new CustomDialog(this, mExitView, com.lqr.wechat.R.style.MyDialog);
                mExitView.findViewById(com.lqr.wechat.R.id.tvExitAccount).setOnClickListener(v1 -> {
                    RongIMClient.getInstance().logout();
                    UserCache.clear();
                    mExitDialog.dismiss();
                    MyApp.exit();
                    jumpToActivityAndClearTask(LoginActivity.class);
                });
                mExitView.findViewById(com.lqr.wechat.R.id.tvExitApp).setOnClickListener(v1 -> {
                    RongIMClient.getInstance().disconnect();
                    mExitDialog.dismiss();
                    MyApp.exit();
                });
            }
            mExitDialog.show();
        });
    }

    @Override
    protected BasePresenter createPresenter() {
        return null;
    }

    @Override
    protected int provideContentViewId() {
        return com.lqr.wechat.R.layout.activity_setting;
    }
}
