package com.example.mycamera;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.FileNotFoundException;

public class Result extends AppCompatActivity {

    private Bitmap bitmap=null;
    private Bitmap copyBitmap=null;
    private Canvas canvas=null;
    private Paint paint=null;
    private ImageView picture=null;

    private TextView result=null;
    private TextView distance=null;
    private TextView length=null;
    private TextView EditRadio=null;
    private TextView EditDistance=null;
    private TextView EditLength=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        picture=(ImageView)findViewById(R.id.picture5);
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inSampleSize = 5;
        try {
            // 获取缩放后的图片
            bitmap = BitmapFactory.decodeStream(getContentResolver()
                    .openInputStream(MyCameraActivity.pic1), null, opts);
            bitmap=adjustPhotoRotation(bitmap,90);
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
            paint.setStrokeWidth(5);
            // 开始作画，把原图的内容绘制在白纸上
            canvas.drawBitmap(bitmap, new Matrix(), paint);
            // 将处理后的图片放入imageview中
            picture.setImageBitmap(copyBitmap);
            canvas.drawPoint(bitmap.getWidth()*SelectionOfObjects.percent_x[0]/100,bitmap.getHeight()*SelectionOfObjects.percent_y[0]/100,paint);
            canvas.drawPoint(bitmap.getWidth()*SelectionOfObjects.percent_x[1]/100,bitmap.getHeight()*SelectionOfObjects.percent_y[1]/100,paint);
            canvas.drawLine(bitmap.getWidth()*SelectionOfObjects.percent_x[0]/100,bitmap.getHeight()*SelectionOfObjects.percent_y[0]/100,bitmap.getWidth()*SelectionOfObjects.percent_x[1]/100,bitmap.getHeight()*SelectionOfObjects.percent_y[1]/100,paint);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        /*result=(TextView)findViewById(R.id.textView);
        result.setText(""+Compare.ratio);*/

        result=(TextView)findViewById(R.id.textView_ratio);
        distance=(TextView)findViewById(R.id.textView_diatance);
        length=(TextView)findViewById(R.id.textView_length);

        EditRadio=(TextView) findViewById(R.id.text_ra);
        EditRadio.setText(""+Compare.ratio);

        EditDistance=(TextView)findViewById(R.id.text_dis);
        EditDistance.setText(""+MyCameraActivity.shiftXY);

        EditLength=(TextView)findViewById(R.id.text_leng);
        EditLength.setText(""+MyCameraActivity.shiftXY*Compare.ratio);
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
