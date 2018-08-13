package com.lqr.wechat.ui.activity;

import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.lqr.wechat.R;
import com.lqr.wechat.ui.base.BaseActivity;
import com.lqr.wechat.ui.presenter.RegisterAtPresenter;
import com.lqr.wechat.ui.view.IRegisterAtView;
import com.lqr.wechat.util.UIUtils;
import com.lqr.wechat.ui.presenter.RegisterAtPresenter;
import com.lqr.wechat.util.UIUtils;

import butterknife.BindView;

public class RegisterActivity extends BaseActivity<IRegisterAtView, RegisterAtPresenter> implements IRegisterAtView {

    @BindView(com.lqr.wechat.R.id.etNick)
    EditText mEtNick;
    @BindView(com.lqr.wechat.R.id.vLineNick)
    View mVLineNick;

    @BindView(com.lqr.wechat.R.id.etPhone)
    EditText mEtPhone;
    @BindView(com.lqr.wechat.R.id.vLinePhone)
    View mVLinePhone;

    @BindView(com.lqr.wechat.R.id.etPwd)
    EditText mEtPwd;
    @BindView(com.lqr.wechat.R.id.ivSeePwd)
    ImageView mIvSeePwd;
    @BindView(com.lqr.wechat.R.id.vLinePwd)
    View mVLinePwd;

    @BindView(com.lqr.wechat.R.id.etVerifyCode)
    EditText mEtVerifyCode;
    @BindView(com.lqr.wechat.R.id.btnSendCode)
    Button mBtnSendCode;
    @BindView(com.lqr.wechat.R.id.vLineVertifyCode)
    View mVLineVertifyCode;

    @BindView(com.lqr.wechat.R.id.btnRegister)
    Button mBtnRegister;

    TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            mBtnRegister.setEnabled(canRegister());
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };

    @Override
    public void initListener() {
        mEtNick.addTextChangedListener(watcher);
        mEtPwd.addTextChangedListener(watcher);
        mEtPhone.addTextChangedListener(watcher);
        mEtVerifyCode.addTextChangedListener(watcher);

        mEtNick.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                mVLineNick.setBackgroundColor(UIUtils.getColor(com.lqr.wechat.R.color.green0));
            } else {
                mVLineNick.setBackgroundColor(UIUtils.getColor(com.lqr.wechat.R.color.line));
            }
        });
        mEtPwd.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                mVLinePwd.setBackgroundColor(UIUtils.getColor(com.lqr.wechat.R.color.green0));
            } else {
                mVLinePwd.setBackgroundColor(UIUtils.getColor(com.lqr.wechat.R.color.line));
            }
        });
        mEtPhone.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                mVLinePhone.setBackgroundColor(UIUtils.getColor(com.lqr.wechat.R.color.green0));
            } else {
                mVLinePhone.setBackgroundColor(UIUtils.getColor(com.lqr.wechat.R.color.line));
            }
        });
        mEtVerifyCode.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                mVLineVertifyCode.setBackgroundColor(UIUtils.getColor(com.lqr.wechat.R.color.green0));
            } else {
                mVLineVertifyCode.setBackgroundColor(UIUtils.getColor(com.lqr.wechat.R.color.line));
            }
        });

        mIvSeePwd.setOnClickListener(v -> {

            if (mEtPwd.getTransformationMethod() == HideReturnsTransformationMethod.getInstance()) {
                mEtPwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
            } else {
                mEtPwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            }

            mEtPwd.setSelection(mEtPwd.getText().toString().trim().length());
        });

        mBtnSendCode.setOnClickListener(v -> {
            if (mBtnSendCode.isEnabled()) {
                mPresenter.sendCode();
            }
        });

        mBtnRegister.setOnClickListener(v -> {
            mPresenter.register();
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.unsubscribe();
    }

    private boolean canRegister() {
        int nickNameLength = mEtNick.getText().toString().trim().length();
        int pwdLength = mEtPwd.getText().toString().trim().length();
        int phoneLength = mEtPhone.getText().toString().trim().length();
        int codeLength = mEtVerifyCode.getText().toString().trim().length();
        if (nickNameLength > 0 && pwdLength > 0 && phoneLength > 0 && codeLength > 0) {
            return true;
        }
        return false;
    }

    @Override
    protected RegisterAtPresenter createPresenter() {
        return new RegisterAtPresenter(this);
    }

    @Override
    protected int provideContentViewId() {
        return com.lqr.wechat.R.layout.activity_register;
    }

    @Override
    public EditText getEtNickName() {
        return mEtNick;
    }

    @Override
    public EditText getEtPhone() {
        return mEtPhone;
    }

    @Override
    public EditText getEtPwd() {
        return mEtPwd;
    }

    @Override
    public EditText getEtVerifyCode() {
        return mEtVerifyCode;
    }

    @Override
    public Button getBtnSendCode() {
        return mBtnSendCode;
    }
}
