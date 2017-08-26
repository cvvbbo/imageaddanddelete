package circlephone.example.test.savephotointo;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.bt)
    Button bt;
    @BindView(R.id.iv)
    ImageView iv;
    @BindView(R.id.bt2)
    Button bt2;


    /***
     * 头一回从相机中获取，只能是sk卡的一级路径，不能是多级的路径
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doTakePhoto();

            }
        });

        bt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleimage(MainActivity.this);
            }
        });


    }


    //第一步，拍照

    private void doTakePhoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); //调用系统相机


        //保存的路径这里修改
        Uri imageUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "image.jpg"));
        //指定照片保存路径（SD卡），image.jpg为一个临时文件，每次拍照后这个图片都会被替换

        //保存操作
        //Uri imageUri = Uri.fromFile(savetofile());


        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);


        startActivityForResult(intent, 100);  //用户点击了从相机获取
    }


    //保存图片，这个是保存bitmap的图片。
    public static void saveImage(Context m, Bitmap bmp) {
        File appDir = new File(Environment.getExternalStorageDirectory(), "emiaoqian");
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        //有了不同的文件名就能拍几张就是几张
        // String fileName = System.currentTimeMillis()+"haha.jpg";

        String fileName = "haha.jpg";


        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
            Log.e("---", "执行了么");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        //可注释可打开的方法
//        try {
//            //把照片插入到系统的相册中，模拟器的系统相册就是叫pictures
//            MediaStore.Images.Media.insertImage(m.getContentResolver(),
//                    file.getAbsolutePath(), fileName, null);
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }

        //下面是更新图库的方法,在相册中同步显示一个叫sd卡的相册路径（可注释，可打开的方法）
//        m.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
//                Uri.fromFile(new File(Environment.getExternalStorageDirectory(),"image.jpg"))));


        //这个是刷新图库的操作（可注释，可打开的方法）
        //这个就是从sd卡里面的照片展示在相册中，当照片名字是不同的时候，每张图片都是会刷新在图库里面
        m.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                Uri.fromFile(file)));


    }


    //这个是保存在文件夹中,固定文件名字


    //第三步刷新相册（已经实现刷新）


    //最后一步提交之后删除照片


    //完成！！！！！
    public void deleimage(Context m) {

        File appDir = new File(Environment.getExternalStorageDirectory(), "emiaoqian");
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName = "haha.jpg";
        File file = new File(appDir, fileName);
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        ContentResolver mContentResolver = m.getContentResolver();
        String where = MediaStore.Images.Media.DATA + "='" + file + "'";
        //删除图片
        mContentResolver.delete(uri, where, null);

        m.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                Uri.fromFile(file)));


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {

                case 100:

                    //拍照之后保存的不是在系统图库里面，保存的是在sd卡里面，还是需要改
                    Bitmap bitmap = BitmapFactory.decodeFile(new File(Environment.getExternalStorageDirectory(), "image.jpg").getPath());

                    //从sd卡的单级路径保存到某个多级路径
                    //只是保存了，但是未压缩
                    saveImage(MainActivity.this, bitmap);
                    iv.setImageBitmap(bitmap);
                    break;

            }
        }
    }
}
