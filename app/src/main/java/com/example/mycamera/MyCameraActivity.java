package com.example.mycamera;

/**
 * Created by 张利鹏 on 2017/2/12.
 */
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.example.mycamera.R;

public class MyCameraActivity extends Activity implements SensorEventListener{
    private Button btn_camera_capture = null;
    private Button btn_camera_cancel = null;
    private Button btn_camera_ok = null;

    static protected Uri pic1=null;
    static protected Uri pic2=null;
    private int i=0;
    private Camera camera = null;
    private MySurfaceView mySurfaceView = null;

    private Double[] speed = {0.0,0.0,0.0};
    private Double[] shift = {0.0,0.0,0.0};
    private boolean check;
    public static Double shiftXY;

    private SensorManager sManager;
    private Sensor mSensorAccelerometer;

    private byte[] buffer = null;

    private final int TYPE_FILE_IMAGE = 1;
    private final int TYPE_FILE_VEDIO = 2;

    private PictureCallback pictureCallback = new PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            if (data == null){
                Log.i("MyPicture", "picture taken data: null");
            }else{
                Log.i("MyPicture", "picture taken data: " + data.length);
            }

            int length=(int)(1.5*data.length);                                      ////////////////////////
            buffer = new byte[length];                                              ///////////////////////
            buffer = data.clone();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mycamera_layout);

        check=false;
        sManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        if(sManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION )!=null){
            mSensorAccelerometer = sManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION );
            sManager.registerListener(this, mSensorAccelerometer, SensorManager.SENSOR_DELAY_NORMAL );
        }
        else{
            Toast.makeText(MyCameraActivity.this, "您的手机没有加速度传感器，软件无法正常使用", Toast.LENGTH_LONG).show();
        }

        btn_camera_capture = (Button) findViewById(R.id.camera_capture);
        btn_camera_ok = (Button) findViewById(R.id.camera_ok);
        btn_camera_cancel = (Button) findViewById(R.id.camera_cancel);

        Toast.makeText(MyCameraActivity.this, "调整手机角度，使提示条与待测物体平行", Toast.LENGTH_LONG).show();

        i=0;
        btn_camera_capture.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if(ContextCompat.checkSelfPermission(MyCameraActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(MyCameraActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},3);
                }
                camera.takePicture(null, null, pictureCallback);
                btn_camera_capture.setVisibility(View.INVISIBLE);
                btn_camera_ok.setVisibility(View.VISIBLE);
                btn_camera_cancel.setVisibility(View.INVISIBLE);

                /*if(i==0)
                    check=true;
                else
                    check=false;
                i++;*/
                if(i==1) {
                    check=false;
                    countShift();
                }
            }
        });
        btn_camera_ok.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                //保存图片
                saveImageToFile();
                if(i==0) {
                    pic1 = getOutFileUri(1);
                    i++;
                    check=true;
                    Toast.makeText(MyCameraActivity.this, "移动手机，使提示条沿待测物体的方向移动", Toast.LENGTH_LONG).show();
                }
                else
                {
                    pic2=getOutFileUri(1);
                    Intent intent1=new Intent(MyCameraActivity.this,SelectionOfObjects.class);
                    startActivity(intent1);
                }
                camera.startPreview();
                btn_camera_capture.setVisibility(View.VISIBLE);
                btn_camera_ok.setVisibility(View.INVISIBLE);
                btn_camera_cancel.setVisibility(View.INVISIBLE);
            }
        });
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();

        camera.release();
        camera = null;
        sManager.unregisterListener(this);
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        if (camera == null){
            camera = getCameraInstance();
            camera.setDisplayOrientation(90);
        }
        mySurfaceView = new MySurfaceView(getApplicationContext(), camera);
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(mySurfaceView);
        sManager.registerListener(this, mSensorAccelerometer, SensorManager.SENSOR_DELAY_NORMAL );
    }

    //请求权限回调
    public void onRequestPermissionsResult(int requestCode,String[] permissions,
                                           int[] grandResults){
        switch(requestCode){

                case 3:
                    if(grandResults.length>0&&grandResults[0]==PackageManager.PERMISSION_GRANTED){

                    }
                    else{
                        Toast.makeText(MyCameraActivity.this,"您拒绝使用存储功能，软件无法正常使用",Toast.LENGTH_SHORT).show();
                        finish();
                    }
                    break;
            default:
                break;
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event){
        if(check){
            speed[0] += event.values[0]*0.2 ;//速度分量，0.2f对应采样周期，单位s
            speed[1] += event.values[1]*0.2 ;
            speed[2] += event.values[2]*0.2 ;
            shift[0] += speed[0]*0.2 ;//位移分量
            shift[1] += speed[1]*0.2 ;
            shift[2] += speed[2]*0.2 ;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor,int accuracy){

    }

    //计算位移
    public void countShift(){
        shiftXY=Math.abs(shift[1]);
    }

    /*得到一相机对象*/
    private Camera getCameraInstance(){
        Camera camera = null;
        try{
            camera = camera.open();
        }catch(Exception e){
            e.printStackTrace();
        }
        return camera;
    }


    //-----------------------保存图片---------------------------------------
    private void saveImageToFile(){
        File file = getOutFile(TYPE_FILE_IMAGE);
        if (file == null){
            Toast.makeText(getApplicationContext(), "文件创建失败,请检查SD卡读写权限", Toast.LENGTH_SHORT).show();
            return ;
        }
        Log.i("MyPicture", "自定义相机图片路径:" + file.getPath());
        //Toast.makeText(getApplicationContext(), "图片保存路径：" + file.getPath(), Toast.LENGTH_SHORT).show();
        if (buffer == null){
            Log.i("MyPicture", "自定义相机Buffer: null");
        }else{
            try{
                FileOutputStream fos = new FileOutputStream(file);
                fos.write(buffer);
                buffer=null;                                                        ////////////////////////
                fos.close();
            }catch(IOException e){
                e.printStackTrace();
            }
        }
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

}