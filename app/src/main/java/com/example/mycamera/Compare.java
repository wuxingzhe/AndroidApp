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
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.FileNotFoundException;
import java.io.IOException;

import static android.R.attr.bitmap;
import static com.example.mycamera.R.id.textView;
import static com.example.mycamera.R.id.textView2;

public class Compare extends AppCompatActivity {

    private Bitmap bitmap1=null;
    private Bitmap bitmap2=null;
    private Bitmap copyBitmap1=null;
    private Bitmap copyBitmap2=null;
    private ImageView picture1=null;
    private ImageView picture2=null;
    private Canvas canvas1=null;
    private Canvas canvas2=null;
    private Paint paint=null;
    int []a=new int[2];
    protected static Double ratio=0.0;
    TextView tv=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compare);

        picture1=(ImageView)findViewById(R.id.picture2);
        picture2=(ImageView)findViewById(R.id.picture3);

        tv=(TextView)findViewById(R.id.textView2);

        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inSampleSize = 5;
        try {
            // 获取缩放后的图片
            bitmap1 = BitmapFactory.decodeStream(getContentResolver()
                    .openInputStream(MyCameraActivity.pic1), null, opts);
            bitmap1=adjustPhotoRotation(bitmap1,90);
            // 创建缩放后的图片副本
            copyBitmap1 = Bitmap.createBitmap(bitmap1.getWidth(),
                    bitmap1.getHeight(), bitmap1.getConfig());
            // 创建画布
            canvas1 = new Canvas(copyBitmap1);
            // 创建画笔
            paint = new Paint();
            // 设置画笔颜色
            paint.setColor(Color.RED);
            // 设置画笔宽度
            int stroke=(int)(0.0192*bitmap1.getHeight());
            paint.setStrokeWidth(stroke);
            // 开始作画，把原图的内容绘制在白纸上
            canvas1.drawBitmap(bitmap1, new Matrix(), paint);
            // 将处理后的图片放入imageview中
            picture1.setImageBitmap(copyBitmap1);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            bitmap2 = BitmapFactory.decodeStream(getContentResolver()
                    .openInputStream(MyCameraActivity.pic2), null, opts);
            bitmap2=adjustPhotoRotation(bitmap2,90);
            // 创建缩放后的图片副本
            copyBitmap2 = Bitmap.createBitmap(bitmap2.getWidth(),
                    bitmap2.getHeight(), bitmap2.getConfig());
            // 创建画布
            canvas2 = new Canvas(copyBitmap2);
            // 设置画笔宽度
            int stroke=(int)(0.0192*bitmap1.getHeight());
            paint.setStrokeWidth(stroke);
            // 开始作画，把原图的内容绘制在白纸上
            canvas2.drawBitmap(bitmap2, new Matrix(), paint);
            // 将处理后的图片放入imageview中
            picture2.setImageBitmap(copyBitmap2);
        }
        catch(FileNotFoundException e)
        {
            e.printStackTrace();
            Log.e("btmap2","wrong!");
        }
        paint.setColor(Color.RED);
        canvas1.drawPoint(bitmap1.getWidth()*SelectionOfObjects.percent_x[1]/100,bitmap1.getHeight()*SelectionOfObjects.percent_y[1]/100,paint);
        canvas1.drawLine(bitmap1.getWidth()*SelectionOfObjects.percent_x[0]/100,bitmap1.getHeight()*SelectionOfObjects.percent_y[0]/100,bitmap1.getWidth()*SelectionOfObjects.percent_x[1]/100,bitmap1.getHeight()*SelectionOfObjects.percent_y[1]/100,paint);
        paint.setColor(Color.GREEN);
        canvas1.drawPoint(bitmap1.getWidth()*SelectionOfObjects.percent_x[0]/100,bitmap1.getHeight()*SelectionOfObjects.percent_y[0]/100,paint);
        Button bt=(Button)findViewById(R.id.button2);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Compare.this,Result.class);
                startActivity(intent);
            }
        });
        Button redo=(Button)findViewById(R.id.button4);
        redo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bitmap2.recycle();
                copyBitmap2.recycle();
                bitmap2=null;
                copyBitmap2=null;
                System.gc();

                BitmapFactory.Options opts = new BitmapFactory.Options();
                opts.inSampleSize = 5;
                try {
                    bitmap2 = BitmapFactory.decodeStream(getContentResolver()
                            .openInputStream(MyCameraActivity.pic2), null, opts);
                    bitmap2=adjustPhotoRotation(bitmap2,90);                                         //rotate the picture by 90 degrees
                    // 创建缩放后的图片副本
                    copyBitmap2 = Bitmap.createBitmap(bitmap2.getWidth(),
                            bitmap2.getHeight(), bitmap2.getConfig());
                    // 创建画布
                    canvas2 = new Canvas(copyBitmap2);
                    // 设置画笔宽度
                    int stroke=(int)(0.0192*bitmap1.getHeight());
                    paint.setStrokeWidth(stroke);
                    // 开始作画，把原图的内容绘制在白纸上
                    canvas2.drawBitmap(bitmap2, new Matrix(), paint);
                    // 将处理后的图片放入imageview中
                    picture2.setImageBitmap(copyBitmap2);
                } catch(FileNotFoundException e)
                {
                    e.printStackTrace();
                    Log.e("btmap2","wrong!");
                }
            }
        });
    }

    /*
    @Override
    protected void onResume()
    {
        super.onResume();
        restore();
        Toast.makeText(Compare.this, "this is onresume", Toast.LENGTH_LONG).show();
    }

    private void restore()
    {
        picture1.getLocationOnScreen(a);
        float new_x=picture1.getWidth();//*SelectionOfObjects.percent_x[0];
        float new_y=picture1.getHeight();//*SelectionOfObjects.percent_y[0];
        Toast.makeText(Compare.this, new_x+"  "+new_y, Toast.LENGTH_LONG).show();
    }*/

    public boolean onTouchEvent(MotionEvent event) {
        // 如果是按下操作
        if (event.getAction() == MotionEvent.ACTION_DOWN)
        {
            calculating(event.getRawX(), event.getRawY());
            //paint.setStrokeWidth(5);
            canvas2.drawPoint((event.getX()-a[0])*bitmap2.getWidth()/picture2.getWidth(),(event.getY()-a[1])*bitmap2.getHeight()/picture2.getHeight(),paint);
            picture2.invalidate();
            ratio=getRatio(event.getX(), event.getY());
            //Toast.makeText(Compare.this,bitmap1.getHeight()+" "+bitmap1.getWidth(),Toast.LENGTH_LONG).show();
        }
        return super.onTouchEvent(event);
    }

    // 获取到坐标，进行判断
    private void calculating(float x, float y) {
        picture2.getLocationOnScreen(a);
    }

    private Double getRatio(float x,float y)
    {
        double det=Math.abs((y-a[1])/picture2.getHeight()*100-SelectionOfObjects.percent_y[0]);
        Double length=Math.sqrt((SelectionOfObjects.percent_y[1]-SelectionOfObjects.percent_y[0])*(SelectionOfObjects.percent_y[1]-SelectionOfObjects.percent_y[0])
                +(SelectionOfObjects.percent_x[1]-SelectionOfObjects.percent_x[0])*(SelectionOfObjects.percent_x[1]-SelectionOfObjects.percent_x[0]));
        return (length/det);
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
