package com.lqr.wechat.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lqr.optionitemview.OptionItemView;
import com.lqr.wechat.R;
import com.lqr.wechat.app.AppConst;
import com.lqr.wechat.ui.activity.FriendCircleActivity;
import com.lqr.wechat.ui.activity.MainActivity;
import com.lqr.wechat.ui.activity.ScanActivity;
import com.lqr.wechat.ui.base.BaseFragment;
import com.lqr.wechat.ui.presenter.DiscoveryFgPresenter;
import com.lqr.wechat.ui.view.IDiscoveryFgView;
import com.lqr.wechat.app.AppConst;
import com.lqr.wechat.ui.presenter.DiscoveryFgPresenter;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @创建者 CSDN_LQR
 * @描述 发现界面
 */
public class DiscoveryFragment extends BaseFragment<IDiscoveryFgView, DiscoveryFgPresenter> implements IDiscoveryFgView {

    @BindView(com.lqr.wechat.R.id.oivScan)
    OptionItemView mOivScan;
    @BindView(com.lqr.wechat.R.id.oivShop)
    OptionItemView mOivShop;
    @BindView(com.lqr.wechat.R.id.oivGame)
    OptionItemView mOivGame;
    @BindView(com.lqr.wechat.R.id.oivCirlcle)
    OptionItemView mOivCirlcle;

    @Override
    public void initListener() {
        mOivCirlcle.setOnClickListener(v -> ((MainActivity) getActivity()).jumpToActivity(FriendCircleActivity.class));
        mOivScan.setOnClickListener(v -> ((MainActivity) getActivity()).jumpToActivity(ScanActivity.class));
        mOivShop.setOnClickListener(v -> ((MainActivity) getActivity()).jumpToWebViewActivity(AppConst.WeChatUrl.JD));
        mOivGame.setOnClickListener(v -> ((MainActivity) getActivity()).jumpToWebViewActivity(AppConst.WeChatUrl.GAME));
    }

    @Override
    protected DiscoveryFgPresenter createPresenter() {
        return new DiscoveryFgPresenter((MainActivity) getActivity());
    }

    @Override
    protected int provideContentViewId() {
        return com.lqr.wechat.R.layout.fragment_discovery;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
