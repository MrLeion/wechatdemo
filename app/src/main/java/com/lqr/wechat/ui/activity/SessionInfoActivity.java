package com.lqr.wechat.ui.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.kyleduo.switchbutton.SwitchButton;
import com.lqr.optionitemview.OptionItemView;
import com.lqr.recyclerview.LQRRecyclerView;
import com.lqr.wechat.R;
import com.lqr.wechat.app.AppConst;
import com.lqr.wechat.manager.BroadcastManager;
import com.lqr.wechat.ui.base.BaseActivity;
import com.lqr.wechat.ui.presenter.SessionInfoAtPresenter;
import com.lqr.wechat.ui.view.ISessionInfoAtView;

import java.util.ArrayList;

import butterknife.BindView;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;

/**
 * @创建者 CSDN_LQR
 * @描述 会话信息界面
 */
public class SessionInfoActivity extends BaseActivity<ISessionInfoAtView, SessionInfoAtPresenter> implements ISessionInfoAtView {

    public static int REQ_ADD_MEMBERS = 1000;
    public static int REQ_REMOVE_MEMBERS = 1001;
    public static int REQ_SET_GROUP_NAME = 1002;

    private String mSessionId = "";
    private Conversation.ConversationType mConversationType = Conversation.ConversationType.PRIVATE;
    private int mSessionType;

    @BindView(R.id.llGroupPart1)
    LinearLayout mLlGroupPart1;
    @BindView(R.id.llGroupPart2)
    LinearLayout mLlGroupPart2;

    @BindView(R.id.rvMember)
    LQRRecyclerView mRvMember;

    @BindView(R.id.oivGroupName)
    OptionItemView mOivGroupName;
    @BindView(R.id.oivQRCordCard)
    OptionItemView mOivQRCordCard;
    @BindView(R.id.oivNickNameInGroup)
    OptionItemView mOivNickNameInGroup;
    @BindView(R.id.oivClearMsgRecord)
    OptionItemView mOivClearMsgRecord;

    @BindView(R.id.sbToTop)
    SwitchButton mSbToTop;
    @BindView(R.id.btnQuit)
    Button mBtnQuit;

    @Override
    public void init() {
        Intent intent = getIntent();
        mSessionId = intent.getStringExtra("sessionId");
        mSessionType = intent.getIntExtra("sessionType", SessionActivity.SESSION_TYPE_PRIVATE);
        switch (mSessionType) {
            case SessionActivity.SESSION_TYPE_PRIVATE:
                mConversationType = Conversation.ConversationType.PRIVATE;
                break;
            case SessionActivity.SESSION_TYPE_GROUP:
                mConversationType = Conversation.ConversationType.GROUP;
                break;
        }
        registerBR();
    }

    @Override
    public void initView() {
        switch (mSessionType) {
            case SessionActivity.SESSION_TYPE_PRIVATE:
                mLlGroupPart1.setVisibility(View.GONE);
                mLlGroupPart2.setVisibility(View.GONE);
                mBtnQuit.setVisibility(View.GONE);
                break;
            case SessionActivity.SESSION_TYPE_GROUP:
                mLlGroupPart1.setVisibility(View.VISIBLE);
                mLlGroupPart2.setVisibility(View.VISIBLE);
                mBtnQuit.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public void initData() {
        mPresenter.loadMembers();
        mPresenter.loadOtherInfo(mSessionType, mSessionId);
    }

    @Override
    public void initListener() {
        mOivGroupName.setOnClickListener(v -> {
            Intent intent = new Intent(SessionInfoActivity.this, SetGroupNameActivity.class);
            intent.putExtra("groupId", mSessionId);
            startActivityForResult(intent, REQ_SET_GROUP_NAME);
        });
        mOivQRCordCard.setOnClickListener(v -> {
            Intent intent = new Intent(SessionInfoActivity.this, QRCodeCardActivity.class);
            intent.putExtra("groupId", mSessionId);
            jumpToActivity(intent);
        });
        mOivNickNameInGroup.setOnClickListener(v -> mPresenter.setDisplayName());
        mSbToTop.setOnCheckedChangeListener((buttonView, isChecked) -> RongIMClient.getInstance().setConversationToTop(mConversationType, mSessionId, isChecked));
        mOivClearMsgRecord.setOnClickListener(v -> mPresenter.clearConversationMsg());
        mBtnQuit.setOnClickListener(v -> mPresenter.quit());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_ADD_MEMBERS) {
            if (resultCode == RESULT_OK) {
                ArrayList<String> selectedIds = data.getStringArrayListExtra("selectedIds");
                mPresenter.addGroupMember(selectedIds);
            }
        } else if (requestCode == REQ_REMOVE_MEMBERS) {
            if (resultCode == RESULT_OK) {
                ArrayList<String> selectedIds = data.getStringArrayListExtra("selectedIds");
                mPresenter.deleteGroupMembers(selectedIds);
            }
        } else if (requestCode == REQ_SET_GROUP_NAME) {
            if (resultCode == RESULT_OK) {
                String groupName = data.getStringExtra("group_name");
                mOivGroupName.setRightText(groupName);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unRegisterBR();
    }

    private void registerBR() {
        BroadcastManager.getInstance(this).register(AppConst.UPDATE_GROUP_MEMBER, new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String groupId = intent.getStringExtra("String");
                if (mSessionId.equalsIgnoreCase(groupId)) {
                    mPresenter.loadMembers();
                }
            }
        });
    }

    private void unRegisterBR() {
        BroadcastManager.getInstance(this).unregister(AppConst.UPDATE_GROUP_MEMBER);
    }

    @Override
    protected SessionInfoAtPresenter createPresenter() {
        return new SessionInfoAtPresenter(this, mSessionId, mConversationType);
    }

    @Override
    protected int provideContentViewId() {
        return R.layout.activity_session_info;
    }

    @Override
    public LQRRecyclerView getRvMember() {
        return mRvMember;
    }

    @Override
    public OptionItemView getOivGroupName() {
        return mOivGroupName;
    }

    @Override
    public OptionItemView getOivNickNameInGroup() {
        return mOivNickNameInGroup;
    }

    @Override
    public SwitchButton getSbToTop() {
        return mSbToTop;
    }

    @Override
    public Button getBtnQuit() {
        return mBtnQuit;
    }

}
