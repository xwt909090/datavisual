package com.example.datavisual;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.*;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.FloatMath;
import android.view.*;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import com.bumptech.glide.Glide;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ShowActivity extends AppCompatActivity implements OnTouchListener {

    private Button jianqie_button;
    private Button suoxiao_button;




    //放大缩小
    Matrix matrix = new Matrix();
    Matrix savedMatrix = new Matrix();

    PointF start = new PointF();
    PointF mid = new PointF();
    //float oldDist;

    private ImageView myImageView;

    //模式
    static final int NONE = 0;
    static final int DRAG = 1;
    static final int ZOOM = 2;
    int mode = NONE;

    //TouchImageViewActivity

    private float oldDist = 1f;
    private float oldRotation = 0;//第二个手指放下时的两点的旋转角度
    private float rotation = 0;//旋转角度差值
    private float newRotation = 0;

            ;
    private Bitmap bm;
    //TouchImageViewActivity
    private List<PowerButton> powerbuttonList = new ArrayList<>();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);// 隐藏标题

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,

                WindowManager.LayoutParams.FLAG_FULLSCREEN);// 设置全屏

        setContentView(R.layout.activity_show);

        FloatingActionButton fab = (FloatingActionButton)findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        myImageView = (ImageView) findViewById(R.id.show_image);
        //Glide.with(this).load(R.drawable.s_xiaoxiong).into(myImageView);//.override(800, 1050)
        myImageView.setOnTouchListener(this);

        //CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(false);
        }

        //collapsingToolbar.setTitle("");
        //jianqie_button.setBackgroundResource((int)myImageView.getTag());
        //TouchImageViewActivity

        //gintama = BitmapFactory.decodeResource(getResources(), R.drawable.s_xiaoxiong/*(int)myImageView.getTag()*/);
        //myImageView.setImageBitmap(gintama);


        Intent intent = getIntent();
        bm = BitmapFactory.decodeResource(getResources(), R.drawable.s_xiaoxiong/*(int)myImageView.getTag()*/);
        boolean FLAG = intent.getBooleanExtra("flag",false);

//根据图片的filepath获取到一个ExifInterface的对象
        ExifInterface exif = null;
        if(!FLAG) {
            Uri imageUri = Uri.parse(intent.getStringExtra("photo_picture"));
            try {
                bm = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));

            } catch (Exception e) {
                e.printStackTrace();
            }
        }else {
            String imagePath = intent.getStringExtra("storage_picture");
            //调整方向
            try {
                exif = new ExifInterface(imagePath);
            } catch (IOException e) {
                e.printStackTrace();
                exif = null;
            }
            //调整方向
            bm = BitmapFactory.decodeFile(imagePath);
        }
        //调整方向
        int dd = 0;
        if (exif != null) {
            // 读取图片中相机方向信息
            int ori = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED);
            // 计算旋转角度
            switch (ori) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    dd = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    dd = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    dd = 270;
                    break;
                default:
                    dd = 0;
                    break;
            }
            if (dd != 0) {
                // 旋转图片
                Matrix m = new Matrix();
                m.postRotate(dd);
                bm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(),
                        bm.getHeight(), m, true);
            }
        }
        /*else{
            Matrix m = new Matrix();
            m.postRotate(90);
            bm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(),
                    bm.getHeight(), m, true);
        }*/
        //调整方向
        createScaledBitmap();
        myImageView.setImageBitmap(bm);
        /**
         * 使用图片的矩阵类型进行图片的设置，必须设置
         * setScaleType(ScaleType.MATRIX);
         * 网上有说，设置了该类型之后，怎么设置图片在中心位置上？
         * 我的解决方法就是通过代码控制，将图片设置在屏幕中心
         * 具体代码请参看 center(true,true);方法
         */
        myImageView.setScaleType(ImageView.ScaleType.MATRIX);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        //获取屏幕的宽和高
        //widthScreen = dm.widthPixels;
        //heightScreen = dm.heightPixels;
        //初始化图片的矩阵
        matrix.set(myImageView.getImageMatrix());

        /**
         * 初始化 将图片放在屏幕中心位置
         */
        //center(true, true);
        /**
         * 图片设置中心之后，重新设置图片的缩放矩阵
         */
        myImageView.setImageMatrix(matrix);


        /*suoxiao_button = (Button)findViewById(R.id.suoxiao_button);

        suoxiao_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap img;
                img = BitmapFactory.decodeResource(getResources(), R.drawable.s_xiaoxiong);
                img = scaleImage(img, 50,50);
                myImageView.setImageBitmap(img);
            }
        });*/


        initFruits();
        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        //StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        PowerButtonAdapter adapter = new PowerButtonAdapter(powerbuttonList);
        recyclerView.setAdapter(adapter);

    }

    private void initFruits() {

        PowerButton apple = new PowerButton("编辑", R.drawable.bianji_pic);
        powerbuttonList.add(apple);
        PowerButton banana = new PowerButton("左旋转", R.drawable.zuoxuanzhuan_pic);
        powerbuttonList.add(banana);
        PowerButton orange = new PowerButton("右旋转", R.drawable.youxuanzhuan_pic);
        powerbuttonList.add(orange);
        PowerButton watermelon = new PowerButton("放大", R.drawable.fangda_pic);
        powerbuttonList.add(watermelon);
        PowerButton pear = new PowerButton("缩小", R.drawable.suoxiao_pic);
        powerbuttonList.add(pear);
        PowerButton grape = new PowerButton("剪切", R.drawable.jianqie_pic);
        powerbuttonList.add(grape);
        PowerButton pineapple = new PowerButton("恢复", R.drawable.huifu_pic);
        powerbuttonList.add(pineapple);
        PowerButton strawberry = new PowerButton("保存", R.drawable.baocun_pic);
        powerbuttonList.add(strawberry);

    }

    /*@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }*/
    //TouchImageViewActivity
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        ImageView myImageView = (ImageView) v;
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            //设置拖拉模式
            case MotionEvent.ACTION_DOWN:
                matrix.set(myImageView.getImageMatrix());

                savedMatrix.set(matrix);
                start.set(event.getX(), event.getY());
                mode = DRAG;
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                //TouchImageViewActivity
                if (mode == ZOOM) {
                    /**
                     * 双手放开，停止图片的旋转和缩放
                     * Reset_scale还原图片的缩放比例
                     */
                    /*matrix1.postScale(Reset_scale, Reset_scale, mid.x, mid.y);
                    /**
                     * 双手放开，停止缩放、旋转图片，此时根据已旋转的角度
                     * 计算还原图片的角度，最终的效果是把图片竖直或横平方正。
                     */
                    /*setRotate();
                    matrix.set(matrix1);
                    /**
                     * 将图片放在屏幕中间位置
                     */
                    //center(true, true);
                    //myImageView.setImageMatrix(matrix);
                    //matrix1.reset();
                }else if (mode == DRAG) {
                    /**
                     * 单手拖动图片，放开手指，停止拖动
                     * 此时检测图片是否已经偏离屏幕边缘
                     * 如果偏离屏幕边缘，则图片回弹
                     */
                    /*checkDxDyBounds();
                    matrix.set(matrix1);
                    myImageView.setImageMatrix(matrix);
                    matrix1.reset();*/
                }
                //TouchImageViewActivity
                mode = NONE;
                break;

//设置多点触摸模式
            case MotionEvent.ACTION_POINTER_DOWN:
                oldDist = spacing(event);


                if (oldDist > 10f) {
                    savedMatrix.set(matrix);
                    midPoint(mid, event);
                    mode = ZOOM;
                }
                //TouchImageViewActivity
                oldRotation = rotation(event);
                //TouchImageViewActivity
                break;
//若为DRAG模式，则点击移动图片
            case MotionEvent.ACTION_MOVE:
                if (mode == DRAG) {
                    matrix.set(savedMatrix);
                    matrix.postTranslate(event.getX() - start.x, event.getY() - start.y);
                }
//若为ZOOM模式，则点击触摸缩放
                else if (mode == ZOOM) {
                    float newDist = spacing(event);
                    if (newDist > 10f) {
                        matrix.set(savedMatrix);
                        float scale = newDist / oldDist;
                        matrix.postScale(scale, scale, mid.x, mid.y);// 縮放
                    }
                        //TouchImageViewActivity
                        /**
                         * 两个手指开始移动
                         * 计算移动后旋转角度
                         */
                    if (newDist > 100f) {
                        newRotation = rotation(event);
                        /**
                         * 两个角度之差
                         * 即是图片的旋转角度
                         */
                        rotation = newRotation - oldRotation;
                        //Reset_scale = oldDist/newDist;

                        matrix.postRotate(rotation, mid.x, mid.y);// 旋轉
                    }
                        //matrix.set(matrix1);
                        //TouchImageViewActivity
//设置硕放比例和图片的中点位置

                        //TouchImageViewActivity
/**
 * 调用该方法即可重新图片
 */
                        //myImageView.setImageMatrix(matrix);
                        //TouchImageViewActivity

                }
                break;
        }
        myImageView.setImageMatrix(matrix);
        return true;
    }


    // 取旋转角度
    private float rotation(MotionEvent event) {
        double delta_x = (event.getX(0) - event.getX(1));
        double delta_y = (event.getY(0) - event.getY(1));
        /**
         * 反正切函数
         * 计算两个坐标点的正切角度
         */
        double radians = Math.atan2(delta_y, delta_x);
        return (float)(Math.toDegrees(radians));
    }

    //TouchImageViewActivity

    //计算移动距离
    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float)Math.sqrt(x * x + y * y);
    }

    //计算中点位置
    private void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }

    /**
     * 缩放图片
     * @param bm 要缩放图片
     * @param newWidth 宽度
     * @param newHeight 高度
     * @return处理后的图片
     */
    public Bitmap  scaleImage(Bitmap bm, int newWidth, int newHeight){

        if (bm == null){
            return null;
        }
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap newbm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix,true);
        if (bm != null & !bm.isRecycled()){
            bm.recycle();//销毁原图片
            bm = null;
        }
        return newbm;
    }

    private void createScaledBitmap(){
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels+1;
        int height = dm.heightPixels+1;
        int t_width;
        int t_height;
        if (bm.getWidth()>width || bm.getHeight()>height){
            t_width = width;
            t_height = bm.getHeight()*width/bm.getWidth();
            if (t_height>height){
                t_width = t_width*height/t_height;
                t_height = height;
            }
        } else
        if (bm.getWidth()<width && bm.getHeight()<height){
            t_width = width;
            t_height = bm.getHeight()*width/bm.getWidth();
            if (t_height>height){
                t_width = t_width*height/t_height;
                t_height = height;
            }
        } else {
            t_width = bm.getWidth();
            t_height = bm.getHeight();
        }
        bm = Bitmap.createScaledBitmap(bm, t_width, t_height, true);
    }

}
