package qianfeng.myimageview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by Administrator on 2016/9/24 0024.
 */
public class CustomImageView extends ImageView {

    private Paint imageViewPaint;

    /*
        <enum name="circle" value="0"></enum>
          <enum name="roundrect" value="1"></enum>
          <enum name="rect" value="2"></enum>
          <enum name="oval" value="3"></enum>
     */
    private static final int CIRCLE = 0;
    private static final int ROUNDRECT = 1;
    private static final int RECT = 2;
    private static final int OVAL = 3;
    private final static int RHOMBUS = 4;
    private final static int HEXAGON = 5;

    private int shape = 0;
    private int rotateDrgree;

    public CustomImageView(Context context) {
        this(context, null);
    }

    public CustomImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        // 从属性集中获取指定xml文件中的属性名数组
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.CustomImageView);

        // 获取用户设置的shape属性的值
        shape = ta.getInt(R.styleable.CustomImageView_shape, CIRCLE); // 从指定的xml文件的属性名数组中 获取指定xml文件中的属性名
        // 记得要回收资源!
        rotateDrgree = ta.getInt(R.styleable.CustomImageView_rotate,30);

        ta.recycle();

        imageViewPaint = new Paint();
        imageViewPaint.setAntiAlias(true);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        // 不需要父类的绘制API


        // 拿到Drawable中的图片再转换为它的子类
        Drawable drawable = getDrawable();
        if (drawable == null) {
            return;
        }

        // 重置Paint的属性
        imageViewPaint.reset();

        // 获取用户所设置图片的Bitmap对象
        Bitmap srcBitmap = ((BitmapDrawable) drawable).getBitmap();


        // 接下来创建一个用于获取缩放图片的bitmap，用到缩放矩阵

        // 获取控件的宽高，就是用户使用这个自定义控件时设置的宽高
        int measuredWidth = getMeasuredWidth();
        int measuredHeight = getMeasuredHeight();

        // 获取原图的宽高
        int srcBitmapWidth = srcBitmap.getWidth();
        int srcBitmapHeight = srcBitmap.getHeight();

        // 新建一个用于放缩图片的矩阵


        Matrix matrix = new Matrix();

        // 我需要一个float类型的scale放缩比例，那么这个放缩比例肯定是根据 原图的宽高和控件的宽高 之间的 比值来决定的
        // 比较的两个分数之间取较大者，即这个是实际上的较短长度者，即宽高之间取较小的放缩比例值，(当宽放缩比例值小于高放缩比例值时，取较小者，即宽的放缩比例值)
        float scale = Math.max(measuredWidth * 1f / srcBitmapWidth, measuredHeight * 1f / srcBitmapHeight); // 放缩比例，跟bitmap第一次采样的核心代码是一样的
        // 放缩矩阵，已经设置好宽高的放缩比例了
        matrix.postScale(scale, scale);

        Bitmap bitmap = Bitmap.createBitmap(srcBitmap, 0, 0, srcBitmapWidth, srcBitmapHeight, matrix, true); // 最后一个参数是是否使用滤镜，让图片效果更佳
        // bitmap在这之后，就是经过放缩的原图的图像了了！

        // 创建一个和控件宽高一样大小的空白的bitmap
        Bitmap blankBitmap = Bitmap.createBitmap(measuredWidth, measuredHeight, Bitmap.Config.ARGB_8888);
        // canvas所绘制的所有图像都将出现在blankBitmap上
        // canvas这个画布所有的内容都是会立刻显示出来的，因此我们在绘制自定义imageview时不能使用这个画布，要自己新建一张，然后把新建画布中的bitmap的图形画好之后，再让canves显示出来即可。
        Canvas mCanvas = new Canvas(blankBitmap); // 传进去的参数是bitmap，说明这张画布的所有内容都是画到这个空白的bitmap上面的

        // 下面添加一个旋转属性，只要3行代码，但是在xml中要自定义一个rotate属性
        mCanvas.save();// 旋转的第一行代码，但是你要在使用自定义控件的main。xml中使用rotate的属性

        switch (shape) {
            case CIRCLE:
                mCanvas.drawCircle(measuredHeight / 2, measuredHeight / 2, measuredHeight / 2, imageViewPaint);
                break;
            case ROUNDRECT:
                mCanvas.drawRoundRect(new RectF(0, 0, measuredWidth, measuredHeight), 20, 20, imageViewPaint);// 这个20，20是圆角矩形的圆的半径的数值，两值最好相等
                break;

            case RECT:
                mCanvas.drawRect(new RectF(0, 0, measuredWidth, measuredHeight), imageViewPaint);
                break;

            case OVAL:
                if (Build.VERSION.SDK_INT > 20) {
                    mCanvas.drawOval(0, 0, measuredWidth, measuredHeight, imageViewPaint);
                } else {
                    mCanvas.drawOval(new RectF(0, 0, measuredWidth, measuredHeight), imageViewPaint);
                }

                break;

            case RHOMBUS: {
                Path path = new Path();
                path.moveTo(getMeasuredWidth() / 2, 0);
                path.lineTo(getMeasuredWidth(), getMeasuredHeight() / 2);
                path.lineTo(getMeasuredWidth() / 2, getMeasuredHeight());
                path.lineTo(0, getMeasuredHeight() / 2);
                path.close(); // 调用close方法，会自动形成封闭图形
                mCanvas.drawPath(path, imageViewPaint);
            }
            break;
            /*
            t RHOMBUS = 4;
t HEXAGON = 5;
             */
            case HEXAGON: {
//                mCanvas.rotate(rotateDrgree,getMeasuredWidth()/2,getMeasuredHeight()/2); // 旋转的第二行代码

                Path path = new Path();
                path.moveTo(getMeasuredWidth() / 2, 0);
                float x = (float) ((getMeasuredWidth()/4 ) * Math.atan(60));
                path.lineTo(getMeasuredWidth() / 2 + x, getMeasuredHeight() * 0.25f);
                path.lineTo(getMeasuredWidth() / 2 + x, getMeasuredHeight() * 0.75f);

                path.lineTo(getMeasuredWidth() / 2, getMeasuredHeight());
                path.lineTo(getMeasuredWidth() / 2 - x, getMeasuredHeight() * 0.75f);
                path.lineTo(getMeasuredWidth() / 2 - x, getMeasuredHeight() * 0.25f);
                path.close();

                mCanvas.drawPath(path, imageViewPaint);
            }
            break;
        }

        mCanvas.restore();// 旋转的最后一行代码

        // 设置画笔的属性，使之取图像1和图像2的交集
        imageViewPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));  // 请注意这里的模式！！！请看当日的文档!
        // 利用自己创建的画布绘制一个bitmap对象，绘制在blankBitmap上
        mCanvas.drawBitmap(bitmap, 0, 0, imageViewPaint); // 将指定bitmap绘制在--> blankBitmap 上

        // 系统的画布显示我在blankBitmap上绘制的图像
        canvas.drawBitmap(blankBitmap, 0, 0, null);

        // 最后注意回收blankBitmap
        if (blankBitmap != null && !blankBitmap.isRecycled()) // blankBitmap.isRecycled(): bitmap是否被回收
        {
            blankBitmap.isRecycled();
        }

        if (bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle();
        }

    }
}
