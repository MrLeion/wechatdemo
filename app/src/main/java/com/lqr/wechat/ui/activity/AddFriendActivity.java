package com.lqr.wechat.ui.activity;

import android.widget.LinearLayout;
import android.widget.TextView;

import com.lqr.wechat.R;
import com.lqr.wechat.model.cache.UserCache;
import com.lqr.wechat.ui.base.BaseActivity;
import com.lqr.wechat.ui.presenter.AddFriendAtPresenter;
import com.lqr.wechat.ui.view.IAddFriendAtView;
import com.lqr.wechat.util.UIUtils;
import com.lqr.wechat.model.cache.UserCache;
import com.lqr.wechat.ui.presenter.AddFriendAtPresenter;
import com.lqr.wechat.util.UIUtils;

import butterknife.BindView;

/**
 * @创建者 CSDN_LQR
 * @描述 添加朋友界面
 */

public class AddFriendActivity extends BaseActivity<IAddFriendAtView, AddFriendAtPresenter> implements IAddFriendAtView {

    @BindView(com.lqr.wechat.R.id.llSearchUser)
    LinearLayout mLlSearchUser;
    @BindView(com.lqr.wechat.R.id.tvAccount)
    TextView mTvAccount;

    @Override
    public void initView() {
        setToolbarTitle(UIUtils.getString(com.lqr.wechat.R.string.add_friend));
        mTvAccount.setText(UserCache.getId() + "");
    }

    @Override
    public void initListener() {
        mLlSearchUser.setOnClickListener(v -> jumpToActivity(SearchUserActivity.class));
    }

    @Override
    protected AddFriendAtPresenter createPresenter() {
        return new AddFriendAtPresenter(this);
    }

    @Override
    protected int provideContentViewId() {
        return com.lqr.wechat.R.layout.activity_add_friend;
    }
}
