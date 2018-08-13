package com.lqr.wechat.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.widget.AppCompatImageView;
import android.view.View;
import android.widget.ImageView;

import com.lqr.wechat.ui.base.BaseActivity;
import com.lqr.wechat.ui.presenter.FriendCircleAtPresenter;
import com.lqr.wechat.ui.view.IFriendCircleAtView;
import com.lqr.wechat.util.FileProvider7;
import com.lqr.wechat.util.LogUtils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @创建者 CSDN_LQR
 * @描述 朋友圈
 */
public class FriendCircleActivity extends BaseActivity<IFriendCircleAtView, FriendCircleAtPresenter> implements IFriendCircleAtView {

    @BindView(com.lqr.wechat.R.id.ivTakePhoto)
    AppCompatImageView mIvTakePhoto;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    @BindView(com.lqr.wechat.R.id.ivResult)
    ImageView mIvResult;
    @BindView(com.lqr.wechat.R.id.ivToolbarNavigation)
    ImageView mIvToolbarNavigation;

//    private static final String TAG = TakePhotoActivity.class.getName();
//    private TakePhoto   takePhoto;
//    private InvokeParam invokeParam;

    @Override
    protected FriendCircleAtPresenter createPresenter() {
        return new FriendCircleAtPresenter(this);
    }


    @Override
    protected int provideContentViewId() {
        return com.lqr.wechat.R.layout.activity_friend_circle;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
        bindOnClickListener();
    }

    private void bindOnClickListener() {
        LogUtils.e("进入拍照");
        //动态获取权限
        mIvTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                //camera demo
//                dispatchTakePictureIntent();


                //take photo library

                //cameraLibrary
                jumpToActivity(new Intent(FriendCircleActivity.this,TakePhotoActivity.class));

            }
        });

        mIvToolbarNavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }


    private void dispatchTakePictureIntent() {
        /**
         * 系统相机
         */
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                LogUtils.e(ex.toString());
            }
            // Continue only if the File was successfully created
//            if (photoFile != null) {
//                Uri photoURI = FileProvider.getUriForFile(this,
//                        "com.example.android.fileprovider",
//                        photoFile);
//                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
//                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
//            }
            if (null != photoFile) {
                //适配 7.0 fileExposed
                Uri uri = FileProvider7.getUriForFile(this, photoFile);
                //适配 4.4 以下 Permission denail
//                FileProvider7.grantPermissions(this,takePictureIntent,uri,true);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            }
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
//            Bundle extras = data.getExtras();
//            Bitmap imageBitmap = (Bitmap) extras.get("data");
//            mIvResult.setImageBitmap(imageBitmap);
            switch (requestCode) {
                case REQUEST_IMAGE_CAPTURE:
                    LogUtils.e(mCurrentPhotoPath);
                    galleryAddPic(this,new File(mCurrentPhotoPath));
                    break;
            }
        } else if (resultCode == RESULT_CANCELED) {
            // 取消掉了
//            LogUtils.e(mCurrentPhotoPath);
        }
    }


    String mCurrentPhotoPath;


    /**
     * @return
     * @throws IOException
     */
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }


    /**
     * 压缩图片
     */
    private void setPic() {
        // Get the dimensions of the View
        int targetW = mIvResult.getWidth();
        int targetH = mIvResult.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;
        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        mIvResult.setImageBitmap(bitmap);
    }


    /**
     * 发送系统相册广播
     */
    private void galleryAddPic(Context context,File file) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri contentUri = Uri.fromFile(file);
        mediaScanIntent.setData(contentUri);
        context.sendBroadcast(mediaScanIntent);
    }





}
