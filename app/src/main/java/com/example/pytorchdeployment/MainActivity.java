package com.example.pytorchdeployment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.io.FileNotFoundException;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    int cameraRequestCode = 001;
    public static final int GET_FROM_GALLERY = 3;
    Classifier classifier;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        classifier = new Classifier(Utils.assetFilePath(this,"mobilenet-v2.pt"));

        Button capture = findViewById(R.id.capture);
        Button pick = findViewById(R.id.pick);


        capture.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view){

                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                startActivityForResult(cameraIntent,cameraRequestCode);

            }


        });
        pick.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view){

                startActivityForResult(new Intent(
                        Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), GET_FROM_GALLERY);

            }


        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == cameraRequestCode && resultCode == RESULT_OK) {

            Intent resultView = new Intent(this, Result.class);

            resultView.putExtra("imagedata", data.getExtras());

            Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");

            String pred = classifier.predict(imageBitmap);
            resultView.putExtra("pred", pred);

            startActivity(resultView);

        }
        if(requestCode==GET_FROM_GALLERY && resultCode == Activity.RESULT_OK) {
            Intent resultView = new Intent(this, Result2.class);
            Uri selectedImage = data.getData();
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            resultView.putExtra("imageUri", selectedImage.toString());


            String pred = classifier.predict(bitmap);
            resultView.putExtra("pred", pred);
            startActivity(resultView);
        }


    }

}