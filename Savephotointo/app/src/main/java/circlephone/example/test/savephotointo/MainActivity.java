package circlephone.example.test.savephotointo;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
    @BindView(R.id.bt3)
    Button bt3;
    private Bitmap albumbitmap;
    //private Bitmap afterbitmap;


    /***
     * 头一回从相机中获取，只能是sk卡的一级路径，不能是多级的路径
     * @param savedInstanceState
     *
     *
     * 7.0模拟器会导致崩溃，需要加别的东西。然后6.0需要加权限
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        makepermission();

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

        bt3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getimagefromalbun();

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
            //100%品质就是不会压缩
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

    //这个是删除照片，拍完之后删除
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
        //注意前面两个参数别弄错了
        if (resultCode == RESULT_OK) {
            switch (requestCode) {

                case 100:

                    //拍照之后保存的不是在系统图库里面，保存的是在sd卡里面，还是需要改
//                    Bitmap bitmap = BitmapFactory.decodeFile(new File(Environment.getExternalStorageDirectory(),
//                            "image.jpg").getPath());

                    //压缩的过程
                    Bitmap bitmap = compressImage(new File(Environment.getExternalStorageDirectory(),
                            "image.jpg").getPath());

                    //从sd卡的单级路径保存到某个多级路径,因为相机之坑，拍照只能保存在sd卡里面
                    //只是保存了，但是未压缩
                    saveImage(MainActivity.this, bitmap);
                    iv.setImageBitmap(bitmap);
                    break;
                case 102:

                    ContentResolver resolver = getContentResolver();
                    //照片的原始资源地址，这样获取并不是压缩过的，如果拍照的图片也是这样获取就是压缩过的！！
                    Uri originalUri = data.getData();

                    //使用ContentProvider通过URI获取原始图片
                    try {
                        //下面这个是原始图片的bitmap，但是并不在本应用的文件夹下面
                        albumbitmap = MediaStore.Images.Media.getBitmap(resolver, originalUri);

                        //先放图片，然后在保存，很骚。。。，这样展示就快了
                        iv.setImageBitmap(albumbitmap);
                        // 第一次保存时将系统的保存在自己的文件夹中
//                        File file = saveImageofalbum(MainActivity.this, albumbitmap);
//                        //最后压缩保存
//                        Bitmap afterbitmap = compressImage(file.getPath());
//
//                        //这个是最终保存的的
//                        saveImage(MainActivity.this,afterbitmap);

                        //成功！！！
                        new Thread(){
                            @Override
                            public void run() {
                                // 第一次保存时将系统的保存在自己的文件夹中
                        File file = saveImageofalbum(MainActivity.this, albumbitmap);
                        //最后压缩保存
                        Bitmap afterbitmap = compressImage(file.getPath());

                        //这个是最终保存的的
                        saveImage(MainActivity.this,afterbitmap);

                            }
                        }.start();






                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                    break;


            }
        }
    }


    //6.0的动态权限
    public void makepermission() {
        int checkCallPhonePermission = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA);
        if (checkCallPhonePermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, 222);
        }
    }


    //压缩图片的方法
    public Bitmap compressImage(String filepath) {
        int height = getWindowManager().getDefaultDisplay().getHeight();
        int width = getWindowManager().getDefaultDisplay().getWidth();
        Point p = new Point();

        getWindowManager().getDefaultDisplay().getSize(p);

        width = p.x;
        height = p.y;

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        //下面这个是获取不到大小的，因为加载进内存的大小为0
        Bitmap bitmap = BitmapFactory.decodeFile(filepath);
        BitmapFactory.decodeFile(filepath, options);



        Log.e("--压缩之前", bitmap.getByteCount() + " ");

        int outHeight = options.outHeight;
        int outWidth = options.outWidth;
        int index = 1;
        if (outHeight > height || outWidth > width) {
            float heightRate = outHeight / height;
            float widthrate = outHeight / width;

            index = (int) Math.max(heightRate, widthrate);
        }
        options.inSampleSize = index;
        options.inJustDecodeBounds = false;
        Bitmap afterbitmap = BitmapFactory.decodeFile(filepath, options);
        Log.e("--压缩之后", afterbitmap.getByteCount() + " ");
        return afterbitmap;
    }


    //从相册中获取
    public void getimagefromalbun() {
        Intent intent = new Intent();
        intent.setType("image/*");  // 开启Pictures画面Type设定为image
        intent.setAction(Intent.ACTION_GET_CONTENT); //使用Intent.ACTION_GET_CONTENT这个Action 
        startActivityForResult(intent, 102); //取得相片后返回到本画面

    }


    //保存图片，这个是保存bitmap的图片从相册
    public static File saveImageofalbum(Context m, Bitmap bmp) {
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
            //100%品质就是不会压缩
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
            Log.e("-从相册中选取，并保存-", "执行了么");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        //因为这个方法是为了临时保存的，所以看看不刷新会怎么样
        m.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                Uri.fromFile(file)));

        return file;

    }

}
