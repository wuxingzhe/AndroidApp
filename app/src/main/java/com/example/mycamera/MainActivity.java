package com.example.mycamera;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.R.attr.breadCrumbShortTitle;
import static android.R.attr.data;

public class MainActivity extends AppCompatActivity {

    private final int SYSTEM_CAMERA_REQUESTCODE = 1;
    private final int MYAPP_CAMERA_REQUESTCODE = 2;
    private Uri imageFileUri = null;
    private final int TYPE_FILE_IMAGE = 1;
    private final int TYPE_FILE_VEDIO = 2;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            /*findViewById(R.id.system_camera_btn).setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                    imageFileUri = getOutFileUri(TYPE_FILE_IMAGE);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageFileUri);
                    startActivityForResult(intent, SYSTEM_CAMERA_REQUESTCODE);
                }
            });*/

            findViewById(R.id.myapp_camera_btn).setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (checkCameraHardWare(getApplicationContext())) {
                        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA)!=PackageManager.PERMISSION_GRANTED){
                            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.CAMERA},2);
                        }

                        Intent intent = new Intent(getApplicationContext(), MyCameraActivity.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(getApplicationContext(), "没有相机存在", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            findViewById(R.id.help_btn).setOnClickListener(new View.OnClickListener(){
                public void onClick(View v){
                    Intent intent=new Intent(MainActivity.this,HelpActivity.class);
                    startActivity(intent);
                }
            });

            if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.BODY_SENSORS)!=PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.BODY_SENSORS},1);
            }



        }

        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);

            if (requestCode == SYSTEM_CAMERA_REQUESTCODE && resultCode == RESULT_OK){
			/*从保存的文件中取这个拍好的图片*/
                Log.i("MyPicture", imageFileUri.getEncodedPath());
                setPicToImageView((ImageView)findViewById(R.id.imageview)
                        , new File(imageFileUri.getEncodedPath()));

			/*上面没有intent.putExtra(MediaStore.EXTRA_OUTPUT, imageFileUri);的时候*/
			/*将返回Bitmap的缩小图放入到data中，可以通过这样的方式取得*/
//			Bitmap bitmap = (Bitmap) data.getExtras().get("data");
//			((ImageView)findViewById(R.id.imageview)).setImageBitmap(bitmap);
            }
        }

        //请求权限回调
        public void onRequestPermissionsResult(int requestCode,String[] permissions,
                                               int[] grandResults){
            switch(requestCode){
                case 1:
                    if(grandResults.length>0&&grandResults[0]==PackageManager.PERMISSION_GRANTED){

                    }
                    else{
                        Toast.makeText(MainActivity.this,"您拒绝使用传感器，软件无法正常使用",Toast.LENGTH_SHORT).show();
                        finish();
                    }
                    break;
                case 2:
                    if(grandResults.length>0&&grandResults[0]==PackageManager.PERMISSION_GRANTED){

                    }
                    else{
                        Toast.makeText(MainActivity.this,"您拒绝使用摄像头，软件无法正常使用",Toast.LENGTH_SHORT).show();
                        finish();
                    }
                    break;
               /* case 3:
                    if(grandResults.length>0&&grandResults[0]==PackageManager.PERMISSION_GRANTED){

                    }
                    else{
                        Toast.makeText(MainActivity.this,"您拒绝使用存储功能，软件无法正常使用",Toast.LENGTH_SHORT).show();
                        finish();
                    }
                    break;*/
                default:
                    break;
            }
        }

        //-----------------------Android大图的处理方式---------------------------
        private void setPicToImageView(ImageView imageView, File imageFile){
            int imageViewWidth = imageView.getWidth();
            int imageViewHeight = imageView.getHeight();
            BitmapFactory.Options opts = new BitmapFactory.Options();

            //设置这个，只得到Bitmap的属性信息放入opts，而不把Bitmap加载到内存中
            opts.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(imageFile.getPath(), opts);

            int bitmapWidth = opts.outWidth;
            int bitmapHeight = opts.outHeight;

            int scale = Math.max(imageViewWidth / bitmapWidth, imageViewHeight / bitmapHeight);

            //缩放的比例
            opts.inSampleSize = scale;
            //内存不足时可被回收
            opts.inPurgeable = true;
            //设置为false,表示不仅Bitmap的属性，也要加载bitmap
            opts.inJustDecodeBounds = false;

            Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getPath(), opts);
            imageView.setImageBitmap(bitmap);
        }

        //-----------------------生成Uri---------------------------------------
        //得到输出文件的URI
        private Uri getOutFileUri(int fileType) {
            return Uri.fromFile(getOutFile(fileType));
        }

        //生成输出文件
        private File getOutFile(int fileType) {

            String storageState = Environment.getExternalStorageState();
            if (Environment.MEDIA_REMOVED.equals(storageState)){
                Toast.makeText(getApplicationContext(), "oh,no, SD卡不存在", Toast.LENGTH_SHORT).show();
                return null;
            }

            File mediaStorageDir = new File (Environment
                    .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                    ,"MyPictures");
            if (!mediaStorageDir.exists()){
                if (!mediaStorageDir.mkdirs()){
                    Log.i("MyPictures", "创建图片存储路径目录失败");
                    Log.i("MyPictures", "mediaStorageDir : " + mediaStorageDir.getPath());
                    return null;
                }
            }

            File file = new File(getFilePath(mediaStorageDir,fileType));

            return file;
        }
        //生成输出文件路径
        private String getFilePath(File mediaStorageDir, int fileType){
            String timeStamp =new SimpleDateFormat("yyyyMMdd_HHmmss")
                    .format(new Date());
            String filePath = mediaStorageDir.getPath() + File.separator;
            if (fileType == TYPE_FILE_IMAGE){
                filePath += ("IMG_" + timeStamp + ".jpg");
            }else if (fileType == TYPE_FILE_VEDIO){
                filePath += ("VIDEO_" + timeStamp + ".mp4");
            }else{
                return null;
            }
            return filePath;
        }


	/*检测相机是否存在*/
        private boolean checkCameraHardWare(Context context){
            PackageManager packageManager = context.getPackageManager();
            if (packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA)){
                return true;
            }
            return false;
        }
}
