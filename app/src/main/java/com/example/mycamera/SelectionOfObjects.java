package com.example.mycamera;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileNotFoundException;

public class SelectionOfObjects extends AppCompatActivity {

    private Bitmap bm1=null;
    private ImageView picture1=null;

    private int count;
    int []a=new int[2];
    private Paint paint;
    // 画布
    private Canvas canvas;
    // 缩放后的图片
    private Bitmap bitmap;
    // 缩放后的图片副本
    private Bitmap copyBitmap;
    private float []downx=null;
    private float []downy=null;

    static protected float []percent_x=null;
    static protected float []percent_y=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selection_of_objects);


        picture1=(ImageView)findViewById(R.id.picture1);
        Button finish_bt=(Button)findViewById(R.id.button);
        Button redo=(Button)findViewById(R.id.button3);

        //----------------------------------- new ------------------------------------

        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inSampleSize = 5;
        try {
            count=0;
            // 获取缩放后的图片
            bitmap = BitmapFactory.decodeStream(getContentResolver()
                    .openInputStream(MyCameraActivity.pic1), null, opts);
            bitmap=adjustPhotoRotation(bitmap,90);         //rotate the picture by 90 degrees
            // 创建缩放后的图片副本
            copyBitmap = Bitmap.createBitmap(bitmap.getWidth(),
                    bitmap.getHeight(), bitmap.getConfig());
            // 创建画布
            canvas = new Canvas(copyBitmap);
            // 创建画笔
            paint = new Paint();
            // 设置画笔颜色
            paint.setColor(Color.RED);
            // 设置画笔宽度
            int stroke=(int)(0.0108*bitmap.getHeight());
            paint.setStrokeWidth(stroke);
            // 开始作画，把原图的内容绘制在白纸上
            canvas.drawBitmap(bitmap, new Matrix(), paint);
            // 将处理后的图片放入imageview中
            picture1.setImageBitmap(copyBitmap);
            init();
        } catch (FileNotFoundException e) {

            e.printStackTrace();
        }

        //----------------------------------- new ------------------------------------------

        finish_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent intent=new Intent(SelectionOfObjects.this,Compare.class);
                startActivity(intent);
            }
        });
        redo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                bitmap.recycle();
                copyBitmap.recycle();
                bitmap=null;
                copyBitmap=null;
                System.gc();
                BitmapFactory.Options opts = new BitmapFactory.Options();
                opts.inSampleSize = 5;
                try {
                    // 获取缩放后的图片
                    bitmap = BitmapFactory.decodeStream(getContentResolver()
                            .openInputStream(MyCameraActivity.pic1), null,opts);
                    bitmap=adjustPhotoRotation(bitmap,90);         //rotate the picture by 90 degrees
                    // 创建缩放后的图片副本
                    copyBitmap = Bitmap.createBitmap(bitmap.getWidth(),
                            bitmap.getHeight(), bitmap.getConfig());
                    // 创建画布
                    canvas = new Canvas(copyBitmap);
                    // 创建画笔
                    paint = new Paint();
                    // 设置画笔颜色
                    paint.setColor(Color.RED);
                    // 设置画笔宽度
                    int stroke=(int)(0.0108*bitmap.getHeight());
                    paint.setStrokeWidth(stroke);
                    // 开始作画，把原图的内容绘制在白纸上
                    canvas.drawBitmap(bitmap, new Matrix(), paint);
                    // 将处理后的图片放入imageview中
                    picture1.setImageBitmap(copyBitmap);
                    if(count==2)
                    {
                        canvas.drawPoint(downx[0],downy[0],paint);
                        count--;
                    }
                    else
                    {
                        count=0;
                        init();
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void init()
    {
        downx=new float[10];
        downy=new float[10];
        percent_x=new float[10];
        percent_y=new float[10];
    }

    public boolean onTouchEvent(MotionEvent event) {
        // 如果是按下操作
        if (event.getAction() == MotionEvent.ACTION_DOWN)
        {
            calculating(event.getRawX(), event.getRawY());
            downx[count] = (event.getX()-a[0])*bitmap.getWidth()/picture1.getWidth();
            downy[count] = (event.getY()-a[1])*bitmap.getHeight()/picture1.getHeight();
            canvas.drawPoint(downx[count],downy[count],paint);
            if(count++==1)
                canvas.drawLine(downx[0],downy[0],downx[1],downy[1],paint);
            picture1.invalidate();
            //Toast.makeText(SelectionOfObjects.this,bitmap.getHeight()+" "+bitmap.getWidth(),Toast.LENGTH_LONG).show();
        }
        return super.onTouchEvent(event);
    }

    // 获取到坐标，进行判断
    private void calculating(float x, float y) {
        picture1.getLocationOnScreen(a);
        percent_x[count]=(x-a[0])/picture1.getWidth()*100;
        percent_y[count]=(y-a[1])/picture1.getHeight()*100;
    }
    Bitmap adjustPhotoRotation(Bitmap bm, final int orientationDegree)
    {
        Matrix m = new Matrix();
        m.setRotate(orientationDegree, (float) bm.getWidth() / 2, (float) bm.getHeight() / 2);

        try {
            Bitmap bm1 = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), m, true);
            return bm1;
        } catch (OutOfMemoryError ex) {
        }
        return null;
    }
}
