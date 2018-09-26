package com.inttech.nishat.textrecognizer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.firebase.FirebaseApp;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Date;
import java.text.SimpleDateFormat;

public class MainActivity extends Activity {
    private Button button;
    private ImageView imageView;
    private static final int CAMERA_REQUEST = 1888;
    String mCurrentPhotoPath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
       // FirebaseApp.initializeApp(getApplicationContext());
        imageView = findViewById(R.id.image1);
        button = findViewById(R.id.btn_click);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                //startActivityForResult(intent,2);
                dispatchTakePictureIntent();

            }
        });
    }
    private void dispatchTakePictureIntent(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(intent.resolveActivity(getPackageManager())!=null){
            File photoFile=null;
            try{
                photoFile = createImageFile();
            }catch (IOException e){
                Log.d("Error","Couldnt create file");
            }
            if(photoFile != null){
                Uri photoURI = FileProvider.getUriForFile(this,"com.example.android.fileprovider",photoFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT,photoURI);
                startActivityForResult(intent,CAMERA_REQUEST);
            }
        }
    }
    private File createImageFile()throws IOException{
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_"+timestamp+"_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName,".jpg",storageDir);
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;

    }
    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }
    @Override
    public  void onActivityResult(int reqc,int resc,Intent data){
        super.onActivityResult(reqc,resc,data);
        if(reqc == CAMERA_REQUEST){
            galleryAddPic();
            Bitmap bitmap = (Bitmap)data.getExtras().get("data");
            imageView.setImageBitmap(bitmap);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Intent intent = new Intent(MainActivity.this,Main2Activity.class);
            intent.putExtra("path",mCurrentPhotoPath);
            startActivity(intent);
        }
        if(reqc == 2){
            //imageView.setImageBitmap((Bitmap)data.getExtras().get("data"));
            Uri sel = data.getData();
            String[] path = {MediaStore.Images.Media.DATA};
            Cursor c = getContentResolver().query(sel,path,null,null,null);
            c.moveToFirst();
            int columnIndex = c.getColumnIndex(path[0]);
            String picturePath = c.getString(columnIndex);
            c.close();
            Bitmap thumbnail = (BitmapFactory.decodeFile(picturePath));
            //Log.w("path of image from gallery",picturePath);
            imageView.setImageBitmap(thumbnail);
        }
    }
}
