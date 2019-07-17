package test.camera.com.cameratest;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private static int REQ_CODE = 1;
    private ImageView mIv;
    private String mFilePath;
    private Uri mUri;
    private FileInputStream mFis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mIv = findViewById(R.id.iv);


//        准备一个文件路径，用于存放拍照后的照片
        mFilePath = Environment.getExternalStorageDirectory().getPath();
        mFilePath = mFilePath + "/" + "temp.png";
        requestPower();


    }

    public void requestPower() {
        //判断是否已经赋予权限
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            //如果应用之前请求过此权限但用户拒绝了请求，此方法将返回 true。
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {//这里可以写个对话框之类的项向用户解释为什么要申请权限，并在对话框的确认键后续再次申请权限
            } else {
                //申请权限，字符串数组内是一个或多个要申请的权限，1是申请权限结果的返回参数，在onRequestPermissionsResult可以得知申请结果
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,}, 1);
            }
        }
    }

    public void open_sys(View view) {

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        File file = new File(mFilePath);
        // 第二个参数，即第一步中配置的authorities
        Uri mUri = FileProvider.getUriForFile(MainActivity.this, BuildConfig.APPLICATION_ID + ".fileprovider", file);//注意这里，我故意写了com.http.www.smarthttpdemo.BuildConfig，因为这里出错了我郁闷了好久，因为这里容易导错包，如果你是多module开发，一定要使用主Module（即：app）的那个BuildConfig（这里说明一下，我上面的清单文件是主Module的，所以这里也要导包导入主module的，我觉得写到被依赖module也是可以的，但要保持一致，这个我没试过）


        intent.putExtra(MediaStore.EXTRA_OUTPUT, mUri);//更改系统图片保存的路径
        startActivityForResult(intent, REQ_CODE);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (resultCode == RESULT_OK) {


            if (requestCode == REQ_CODE) {

////                最简单的做法，得到的是分辨率很低的照片
//                Bundle bundle = data.getExtras();
//                Bitmap bitmap = (Bitmap) bundle.get("data");
//                mIv.setImageBitmap(bitmap);

                try {
                    mFis = new FileInputStream(mFilePath);
                    Bitmap bitmap = BitmapFactory.decodeStream(mFis);
                    mIv.setImageBitmap(bitmap);


                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        mFis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            for (int i = 0; i < permissions.length; i++) {

            }
        }
    }

    public void open_camera_activity(View view) {
        startActivity(new Intent(MainActivity.this, CameraActivity.class));

    }
}
