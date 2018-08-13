package com.lqr.wechat.ui.activity;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lqr.wechat.R;
import com.lqr.wechat.ui.base.BaseActivity;
import com.lqr.wechat.ui.presenter.SearchUserAtPresenter;
import com.lqr.wechat.ui.view.ISearchUserAtView;
import com.lqr.wechat.ui.presenter.SearchUserAtPresenter;

import butterknife.BindView;

/**
 * @创建者 CSDN_LQR
 * @描述 搜索用户界面
 */
public class SearchUserActivity extends BaseActivity<ISearchUserAtView, SearchUserAtPresenter> implements ISearchUserAtView {

    @BindView(com.lqr.wechat.R.id.llToolbarSearch)
    LinearLayout mLlToolbarSearch;
    @BindView(com.lqr.wechat.R.id.etSearchContent)
    EditText mEtSearchContent;

    @BindView(com.lqr.wechat.R.id.rlNoResultTip)
    RelativeLayout mRlNoResultTip;
    @BindView(com.lqr.wechat.R.id.llSearch)
    LinearLayout mLlSearch;
    @BindView(com.lqr.wechat.R.id.tvMsg)
    TextView mTvMsg;

    @Override
    public void initView() {
        mToolbarTitle.setVisibility(View.GONE);
        mLlToolbarSearch.setVisibility(View.VISIBLE);
    }

    @Override
    public void initListener() {
        mEtSearchContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String content = mEtSearchContent.getText().toString().trim();
                mRlNoResultTip.setVisibility(View.GONE);
                if (content.length() > 0) {
                    mLlSearch.setVisibility(View.VISIBLE);
                    mTvMsg.setText(content);
                } else {
                    mLlSearch.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mLlSearch.setOnClickListener(v -> mPresenter.searchUser());
    }

    @Override
    protected SearchUserAtPresenter createPresenter() {
        return new SearchUserAtPresenter(this);
    }

    @Override
    protected int provideContentViewId() {
        return com.lqr.wechat.R.layout.activity_search_user;
    }

    @Override
    public EditText getEtSearchContent() {
        return mEtSearchContent;
    }

    @Override
    public RelativeLayout getRlNoResultTip() {
        return mRlNoResultTip;
    }

    @Override
    public LinearLayout getLlSearch() {
        return mLlSearch;
    }
}
