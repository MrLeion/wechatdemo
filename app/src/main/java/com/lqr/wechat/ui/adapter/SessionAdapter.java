package com.lqr.wechat.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.lqr.adapter.LQRAdapterForRecyclerView;
import com.lqr.adapter.LQRViewHolderForRecyclerView;
import com.lqr.emoji.MoonUtils;
import com.lqr.wechat.R;
import com.lqr.wechat.app.AppConst;
import com.lqr.wechat.db.DBManager;
import com.lqr.wechat.manager.JsonMananger;
import com.lqr.wechat.model.cache.UserCache;
import com.lqr.wechat.model.data.GroupNotificationMessageData;
import com.lqr.wechat.model.message.RedPacketMessage;
import com.lqr.wechat.ui.activity.SessionActivity;
import com.lqr.wechat.ui.activity.UserInfoActivity;
import com.lqr.wechat.ui.presenter.SessionAtPresenter;
import com.lqr.wechat.util.MediaFileUtils;
import com.lqr.wechat.util.TimeUtils;
import com.lqr.wechat.util.UIUtils;
import com.lqr.wechat.util.VideoThumbLoader;
import com.lqr.wechat.widget.BubbleImageView;
import com.lqr.wechat.widget.CircularProgressBar;
import com.lqr.wechat.app.AppConst;
import com.lqr.wechat.model.cache.UserCache;
import com.lqr.wechat.model.data.GroupNotificationMessageData;
import com.lqr.wechat.model.message.RedPacketMessage;
import com.lqr.wechat.ui.activity.SessionActivity;
import com.lqr.wechat.ui.activity.UserInfoActivity;
import com.lqr.wechat.ui.presenter.SessionAtPresenter;
import com.lqr.wechat.util.MediaFileUtils;
import com.lqr.wechat.util.TimeUtils;
import com.lqr.wechat.util.UIUtils;
import com.lqr.wechat.util.VideoThumbLoader;

import java.io.File;
import java.util.List;

import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.MessageContent;
import io.rong.imlib.model.UserInfo;
import io.rong.message.FileMessage;
import io.rong.message.GroupNotificationMessage;
import io.rong.message.ImageMessage;
import io.rong.message.LocationMessage;
import io.rong.message.RecallNotificationMessage;
import io.rong.message.TextMessage;
import io.rong.message.VoiceMessage;
import retrofit2.adapter.rxjava.HttpException;

/**
 * @创建者 CSDN_LQR
 * @描述 会话界面的消息列表适配器
 */
public class SessionAdapter extends LQRAdapterForRecyclerView<Message> {

    private Context            mContext;
    private List<Message>      mData;
    private SessionAtPresenter mPresenter;

    private static final int SEND_TEXT = com.lqr.wechat.R.layout.item_text_send;
    private static final int RECEIVE_TEXT = com.lqr.wechat.R.layout.item_text_receive;
    private static final int SEND_IMAGE = com.lqr.wechat.R.layout.item_image_send;
    private static final int RECEIVE_IMAGE = com.lqr.wechat.R.layout.item_image_receive;
    private static final int SEND_STICKER = com.lqr.wechat.R.layout.item_sticker_send;
    private static final int RECEIVE_STICKER = com.lqr.wechat.R.layout.item_sticker_receive;
    private static final int SEND_VIDEO = com.lqr.wechat.R.layout.item_video_send;
    private static final int RECEIVE_VIDEO = com.lqr.wechat.R.layout.item_video_receive;
    private static final int SEND_LOCATION = com.lqr.wechat.R.layout.item_location_send;
    private static final int RECEIVE_LOCATION = com.lqr.wechat.R.layout.item_location_receive;
    private static final int RECEIVE_NOTIFICATION = com.lqr.wechat.R.layout.item_notification;
    private static final int RECEIVE_VOICE = com.lqr.wechat.R.layout.item_audio_receive;
    private static final int SEND_VOICE = com.lqr.wechat.R.layout.item_audio_send;
    private static final int RECEIVE_RED_PACKET = com.lqr.wechat.R.layout.item_red_packet_receive;
    private static final int SEND_RED_PACKET = com.lqr.wechat.R.layout.item_red_packet_send;
    private static final int UNDEFINE_MSG = com.lqr.wechat.R.layout.item_no_support_msg_type;
    private static final int RECALL_NOTIFICATION = com.lqr.wechat.R.layout.item_notification;

    public SessionAdapter(Context context, List<Message> data, SessionAtPresenter presenter) {
        super(context, data);
        mContext = context;
        mData = data;
        mPresenter = presenter;
    }

    @Override
    public void convert(LQRViewHolderForRecyclerView helper, Message item, int position) {
        setTime(helper, item, position);
        setView(helper, item, position);
        if (!(item.getContent() instanceof GroupNotificationMessage) && !(item.getContent() instanceof RecallNotificationMessage) && (getItemViewType(position) != UNDEFINE_MSG)) {
            setAvatar(helper, item, position);
            setName(helper, item, position);
            setStatus(helper, item, position);
            setOnClick(helper, item, position);
        }
    }

    private void setView(LQRViewHolderForRecyclerView helper, Message item, int position) {
        //根据消息类型设置消息显示内容
        MessageContent msgContent = item.getContent();
        if (msgContent instanceof TextMessage) {
            MoonUtils.identifyFaceExpression(mContext, helper.getView(com.lqr.wechat.R.id.tvText), ((TextMessage) msgContent).getContent(), ImageSpan.ALIGN_BOTTOM);
        } else if (msgContent instanceof ImageMessage) {
            ImageMessage imageMessage = (ImageMessage) msgContent;
            BubbleImageView bivPic = helper.getView(com.lqr.wechat.R.id.bivPic);
            Glide.with(mContext).load(imageMessage.getLocalUri() == null ? imageMessage.getRemoteUri() : imageMessage.getLocalUri()).error(com.lqr.wechat.R.mipmap.default_img_failed).override(UIUtils.dip2Px(80), UIUtils.dip2Px(150)).centerCrop().into(bivPic);
        } else if (msgContent instanceof FileMessage) {
            FileMessage fileMessage = (FileMessage) msgContent;
            if (MediaFileUtils.isImageFileType(fileMessage.getName())) {
                ImageView ivPic = helper.getView(com.lqr.wechat.R.id.ivSticker);
                Glide.with(mContext).load(fileMessage.getLocalPath() == null ? fileMessage.getMediaUrl() :
                        fileMessage.getLocalPath()).placeholder(com.lqr.wechat.R.mipmap.default_img).error(com.lqr.wechat.R.mipmap.default_img_failed).centerCrop().into(ivPic);
            } else if (MediaFileUtils.isVideoFileType(fileMessage.getName())) {
                BubbleImageView bivPic = helper.getView(com.lqr.wechat.R.id.bivPic);
                if (fileMessage.getLocalPath() != null && new File(fileMessage.getLocalPath().getPath()).exists()) {
                    VideoThumbLoader.getInstance().showThumb(fileMessage.getLocalPath().getPath(), bivPic, 200, 200);
                } else {
                    bivPic.setImageResource(com.lqr.wechat.R.mipmap.img_video_default);
                }
            }
        } else if (msgContent instanceof LocationMessage) {
            LocationMessage locationMessage = (LocationMessage) msgContent;
            helper.setText(com.lqr.wechat.R.id.tvTitle, locationMessage.getPoi());
            ImageView ivLocation = helper.getView(com.lqr.wechat.R.id.ivLocation);
            Glide.with(mContext).load(locationMessage.getImgUri()).placeholder(com.lqr.wechat.R.mipmap.default_location).centerCrop().into(ivLocation);
        } else if (msgContent instanceof GroupNotificationMessage) {
            GroupNotificationMessage groupNotificationMessage = (GroupNotificationMessage) msgContent;
            try {
                UserInfo curUserInfo = DBManager.getInstance().getUserInfo(UserCache.getId());
                GroupNotificationMessageData data = JsonMananger.jsonToBean(groupNotificationMessage.getData(), GroupNotificationMessageData.class);
                String operation = groupNotificationMessage.getOperation();
                String notification = "";
                String operatorName = data.getOperatorNickname().equals(curUserInfo.getName()) ? UIUtils.getString(com.lqr.wechat.R.string.you) : data.getOperatorNickname();
                String targetUserDisplayNames = "";
                List<String> targetUserDisplayNameList = data.getTargetUserDisplayNames();
                for (String name : targetUserDisplayNameList) {
                    targetUserDisplayNames += name.equals(curUserInfo.getName()) ? UIUtils.getString(com.lqr.wechat.R.string.you) : name;
                }
                if (operation.equalsIgnoreCase(GroupNotificationMessage.GROUP_OPERATION_CREATE)) {
                    notification = UIUtils.getString(com.lqr.wechat.R.string.created_group, operatorName);
                } else if (operation.equalsIgnoreCase(GroupNotificationMessage.GROUP_OPERATION_DISMISS)) {
                    notification = operatorName + UIUtils.getString(com.lqr.wechat.R.string.dismiss_groups);
                } else if (operation.equalsIgnoreCase(GroupNotificationMessage.GROUP_OPERATION_KICKED)) {
                    if (operatorName.contains(UIUtils.getString(com.lqr.wechat.R.string.you))) {
                        notification = UIUtils.getString(com.lqr.wechat.R.string.remove_group_member, operatorName, targetUserDisplayNames);
                    } else {
                        notification = UIUtils.getString(com.lqr.wechat.R.string.remove_self, targetUserDisplayNames, operatorName);
                    }
                } else if (operation.equalsIgnoreCase(GroupNotificationMessage.GROUP_OPERATION_ADD)) {
                    notification = UIUtils.getString(com.lqr.wechat.R.string.invitation, operatorName, targetUserDisplayNames);
                } else if (operation.equalsIgnoreCase(GroupNotificationMessage.GROUP_OPERATION_QUIT)) {
                    notification = operatorName + UIUtils.getString(com.lqr.wechat.R.string.quit_groups);
                } else if (operation.equalsIgnoreCase(GroupNotificationMessage.GROUP_OPERATION_RENAME)) {
                    notification = UIUtils.getString(com.lqr.wechat.R.string.change_group_name, operatorName, data.getTargetGroupName());
                }
                helper.setText(com.lqr.wechat.R.id.tvNotification, notification);
            } catch (HttpException e) {
                e.printStackTrace();
            }
        } else if (msgContent instanceof VoiceMessage) {
            VoiceMessage voiceMessage = (VoiceMessage) msgContent;
            int increment = (int) (UIUtils.getDisplayWidth() / 2 / AppConst.DEFAULT_MAX_AUDIO_RECORD_TIME_SECOND * voiceMessage.getDuration());

            RelativeLayout rlAudio = helper.setText(com.lqr.wechat.R.id.tvDuration, voiceMessage.getDuration() + "''").getView(com.lqr.wechat.R.id.rlAudio);
            ViewGroup.LayoutParams params = rlAudio.getLayoutParams();
            params.width = UIUtils.dip2Px(65) + UIUtils.dip2Px(increment);
            rlAudio.setLayoutParams(params);
        } else if (msgContent instanceof RedPacketMessage) {
            RedPacketMessage redPacketMessage = (RedPacketMessage) msgContent;
            helper.setText(com.lqr.wechat.R.id.tvRedPacketGreeting, redPacketMessage.getContent());
        } else if (msgContent instanceof RecallNotificationMessage) {
            RecallNotificationMessage recallNotificationMessage = (RecallNotificationMessage) msgContent;
            String operatorId = recallNotificationMessage.getOperatorId();
            String operatorName = "";
            if (operatorId.equalsIgnoreCase(UserCache.getId())) {
                operatorName = UIUtils.getString(com.lqr.wechat.R.string.you);
            } else {
                if (mPresenter.mConversationType == Conversation.ConversationType.PRIVATE) {
                    operatorName = UIUtils.getString(com.lqr.wechat.R.string.other_party);
                } else {
                    UserInfo userInfo = DBManager.getInstance().getUserInfo(operatorId);
                    if (userInfo != null) {
                        operatorName = userInfo.getName();
                    }
                }
            }
            helper.setText(com.lqr.wechat.R.id.tvNotification, UIUtils.getString(com.lqr.wechat.R.string.recall_one_message, operatorName));
        }
    }

    private void setOnClick(LQRViewHolderForRecyclerView helper, Message item, int position) {
        helper.getView(com.lqr.wechat.R.id.llError).setOnClickListener(v ->
                RongIMClient.getInstance().deleteMessages(new int[]{item.getMessageId()}, new RongIMClient.ResultCallback<Boolean>() {
                    @Override
                    public void onSuccess(Boolean aBoolean) {
                        mData.remove(position);
                        mPresenter.setAdapter();
                        MessageContent content = item.getContent();
                        if (content instanceof TextMessage) {
                            mPresenter.sendTextMsg(((TextMessage) content).getContent());
                        } else if (content instanceof ImageMessage) {
                            mPresenter.sendImgMsg(((ImageMessage) content).getThumUri(), ((ImageMessage) content).getLocalUri());
                        } else if (content instanceof FileMessage) {
                            mPresenter.sendFileMsg(new File(((FileMessage) content).getLocalPath().getPath()));
                        } else if (content instanceof VoiceMessage) {
                            VoiceMessage voiceMessage = (VoiceMessage) content;
                            mPresenter.sendAudioFile(voiceMessage.getUri(), voiceMessage.getDuration());
                        }
                    }

                    @Override
                    public void onError(RongIMClient.ErrorCode errorCode) {

                    }
                })
        );
        helper.getView(com.lqr.wechat.R.id.ivAvatar).setOnClickListener(v -> {
            UserInfo userInfo = DBManager.getInstance().getUserInfo(item.getSenderUserId());
            if (userInfo != null) {
                Intent intent = new Intent(mContext, UserInfoActivity.class);
                intent.putExtra("userInfo", userInfo);
                ((SessionActivity) mContext).jumpToActivity(intent);
            }
        });
    }

    private void setStatus(LQRViewHolderForRecyclerView helper, Message item, int position) {
        MessageContent msgContent = item.getContent();
        if (msgContent instanceof TextMessage || msgContent instanceof LocationMessage || msgContent instanceof VoiceMessage) {
            //只需要设置自己发送的状态
            Message.SentStatus sentStatus = item.getSentStatus();
            if (sentStatus == Message.SentStatus.SENDING) {
                helper.setViewVisibility(com.lqr.wechat.R.id.pbSending, View.VISIBLE).setViewVisibility(com.lqr.wechat.R.id.llError, View.GONE);
            } else if (sentStatus == Message.SentStatus.FAILED) {
                helper.setViewVisibility(com.lqr.wechat.R.id.pbSending, View.GONE).setViewVisibility(com.lqr.wechat.R.id.llError, View.VISIBLE);
            } else if (sentStatus == Message.SentStatus.SENT) {
                helper.setViewVisibility(com.lqr.wechat.R.id.pbSending, View.GONE).setViewVisibility(com.lqr.wechat.R.id.llError, View.GONE);
            }
        } else if (msgContent instanceof ImageMessage) {
            ImageMessage imageMessage = (ImageMessage) msgContent;
            BubbleImageView bivPic = helper.getView(com.lqr.wechat.R.id.bivPic);
            boolean isSend = item.getMessageDirection() == Message.MessageDirection.SEND ? true : false;
            if (isSend) {
                Message.SentStatus sentStatus = item.getSentStatus();
                if (sentStatus == Message.SentStatus.SENDING) {
                    bivPic.setProgressVisible(true);
                    if (!TextUtils.isEmpty(item.getExtra()))
                        bivPic.setPercent(Integer.valueOf(item.getExtra()));
                    bivPic.showShadow(true);
                    helper.setViewVisibility(com.lqr.wechat.R.id.llError, View.GONE);
                } else if (sentStatus == Message.SentStatus.FAILED) {
                    bivPic.setProgressVisible(false);
                    bivPic.showShadow(false);
                    helper.setViewVisibility(com.lqr.wechat.R.id.llError, View.VISIBLE);
                } else if (sentStatus == Message.SentStatus.SENT) {
                    bivPic.setProgressVisible(false);
                    bivPic.showShadow(false);
                    helper.setViewVisibility(com.lqr.wechat.R.id.llError, View.GONE);
                }
            } else {
                Message.ReceivedStatus receivedStatus = item.getReceivedStatus();
                bivPic.setProgressVisible(false);
                bivPic.showShadow(false);
                helper.setViewVisibility(com.lqr.wechat.R.id.llError, View.GONE);
            }
        } else if (msgContent instanceof FileMessage) {
            BubbleImageView bivPic = helper.getView(com.lqr.wechat.R.id.bivPic);
            FileMessage fileMessage = (FileMessage) msgContent;
            boolean isSend = item.getMessageDirection() == Message.MessageDirection.SEND ? true : false;

            if (MediaFileUtils.isImageFileType(fileMessage.getName())) {
                if (isSend) {
                    Message.SentStatus sentStatus = item.getSentStatus();
                    if (sentStatus == Message.SentStatus.SENDING) {
                    } else if (sentStatus == Message.SentStatus.FAILED) {
                        helper.setViewVisibility(com.lqr.wechat.R.id.llError, View.VISIBLE);
                    } else if (sentStatus == Message.SentStatus.SENT) {
                        helper.setViewVisibility(com.lqr.wechat.R.id.llError, View.GONE);
                    }
                } else {
                    if (bivPic != null) {
                        bivPic.setProgressVisible(false);
                        bivPic.showShadow(false);
                    }
                    helper.setViewVisibility(com.lqr.wechat.R.id.llError, View.GONE);
                }
            } else if (MediaFileUtils.isVideoFileType(fileMessage.getName())) {
                CircularProgressBar cpbLoading = helper.getView(com.lqr.wechat.R.id.cpbLoading);
                if (isSend) {
                    Message.SentStatus sentStatus = item.getSentStatus();
                    if (sentStatus == Message.SentStatus.SENDING || fileMessage.getLocalPath() == null || (fileMessage.getLocalPath() != null && !new File(fileMessage.getLocalPath().getPath()).exists())) {
                        if (!TextUtils.isEmpty(item.getExtra())) {
                            cpbLoading.setMax(100);
                            cpbLoading.setProgress(Integer.valueOf(item.getExtra()));
                        } else {
                            cpbLoading.setMax(100);
                            cpbLoading.setProgress(0);
                        }
                        helper.setViewVisibility(com.lqr.wechat.R.id.llError, View.GONE).setViewVisibility(com.lqr.wechat.R.id.cpbLoading, View.VISIBLE);
                        bivPic.showShadow(true);
                    } else if (sentStatus == Message.SentStatus.FAILED) {
                        helper.setViewVisibility(com.lqr.wechat.R.id.llError, View.VISIBLE).setViewVisibility(com.lqr.wechat.R.id.cpbLoading, View.GONE);
                        bivPic.showShadow(false);
                    } else if (sentStatus == Message.SentStatus.SENT) {
                        helper.setViewVisibility(com.lqr.wechat.R.id.llError, View.GONE).setViewVisibility(com.lqr.wechat.R.id.cpbLoading, View.GONE);
                        bivPic.showShadow(false);
                    }
                } else {
                    Message.ReceivedStatus receivedStatus = item.getReceivedStatus();
                    if (receivedStatus.isDownload() || fileMessage.getLocalPath() != null) {
                        helper.setViewVisibility(com.lqr.wechat.R.id.llError, View.GONE).setViewVisibility(com.lqr.wechat.R.id.cpbLoading, View.GONE);
                        bivPic.showShadow(false);
                    } else {
                        if (!TextUtils.isEmpty(item.getExtra())) {
                            cpbLoading.setMax(100);
                            cpbLoading.setProgress(Integer.valueOf(item.getExtra()));
                        } else {
                            cpbLoading.setMax(100);
                            cpbLoading.setProgress(0);
                        }
                        helper.setViewVisibility(com.lqr.wechat.R.id.llError, View.GONE).setViewVisibility(com.lqr.wechat.R.id.cpbLoading, View.VISIBLE);
                        bivPic.showShadow(true);
                    }
                }
            }
        }
    }

    private void setAvatar(LQRViewHolderForRecyclerView helper, Message item, int position) {
        ImageView ivAvatar = helper.getView(com.lqr.wechat.R.id.ivAvatar);
        UserInfo userInfo = DBManager.getInstance().getUserInfo(item.getSenderUserId());
        if (userInfo != null) {
            Glide.with(mContext).load(userInfo.getPortraitUri()).centerCrop().into(ivAvatar);
        }
    }

    private void setName(LQRViewHolderForRecyclerView helper, Message item, int position) {
        if (item.getConversationType() == Conversation.ConversationType.PRIVATE) {
            helper.setViewVisibility(com.lqr.wechat.R.id.tvName, View.GONE);
        } else {
            helper.setViewVisibility(com.lqr.wechat.R.id.tvName, View.GONE);
//                    .setText(R.id.tvName, item.getContent().getUserInfo().getName());
        }
    }

    private void setTime(LQRViewHolderForRecyclerView helper, Message item, int position) {
        boolean isSend = item.getMessageDirection() == Message.MessageDirection.SEND ? true : false;
        long msgTime = isSend ? item.getSentTime() : item.getReceivedTime();
        if (position > 0) {
            Message preMsg = mData.get(position - 1);
            boolean isSendForPreMsg = preMsg.getMessageDirection() == Message.MessageDirection.SEND ? true : false;
            long preMsgTime = isSendForPreMsg ? preMsg.getSentTime() : preMsg.getReceivedTime();
            if (msgTime - preMsgTime > (5 * 60 * 1000)) {
                helper.setViewVisibility(com.lqr.wechat.R.id.tvTime, View.VISIBLE).setText(com.lqr.wechat.R.id.tvTime, TimeUtils.getMsgFormatTime(msgTime));
            } else {
                helper.setViewVisibility(com.lqr.wechat.R.id.tvTime, View.GONE);
            }
        } else {
            helper.setViewVisibility(com.lqr.wechat.R.id.tvTime, View.VISIBLE).setText(com.lqr.wechat.R.id.tvTime, TimeUtils.getMsgFormatTime(msgTime));
        }
    }

    @Override
    public int getItemViewType(int position) {
        Message msg = mData.get(position);
        boolean isSend = msg.getMessageDirection() == Message.MessageDirection.SEND ? true : false;

        MessageContent msgContent = msg.getContent();
        if (msgContent instanceof TextMessage) {
            return isSend ? SEND_TEXT : RECEIVE_TEXT;
        }
        if (msgContent instanceof ImageMessage) {
            return isSend ? SEND_IMAGE : RECEIVE_IMAGE;
        }
        if (msgContent instanceof FileMessage) {
            FileMessage fileMessage = (FileMessage) msgContent;
            if (MediaFileUtils.isImageFileType(fileMessage.getName())) {
                return isSend ? SEND_STICKER : RECEIVE_STICKER;
            } else if (MediaFileUtils.isVideoFileType(fileMessage.getName())) {
                return isSend ? SEND_VIDEO : RECEIVE_VIDEO;
            }
        }
        if (msgContent instanceof LocationMessage) {
            return isSend ? SEND_LOCATION : RECEIVE_LOCATION;
        }
        if (msgContent instanceof GroupNotificationMessage) {
            return RECEIVE_NOTIFICATION;
        }
        if (msgContent instanceof VoiceMessage) {
            return isSend ? SEND_VOICE : RECEIVE_VOICE;
        }
        if (msgContent instanceof RedPacketMessage) {
            return isSend ? SEND_RED_PACKET : RECEIVE_RED_PACKET;
        }
        if (msgContent instanceof RecallNotificationMessage) {
            return RECALL_NOTIFICATION;
        }
        return UNDEFINE_MSG;
    }
}
