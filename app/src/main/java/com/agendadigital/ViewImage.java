package com.agendadigital;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;

public class ViewImage extends AppCompatActivity {
    private SubsamplingScaleImageView img;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_image);
        img = findViewById(R.id.imageviewfull);
        String filename = getIntent().getStringExtra("imagen");
        String path = Environment.getExternalStorageDirectory().toString()+"/saved_images/"+filename;
        img.setImage(ImageSource.bitmap(BitmapFactory.decodeFile(path)));

    }
}