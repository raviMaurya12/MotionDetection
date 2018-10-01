package com.example.lenovo.motiondetection;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;

public class Imageview extends AppCompatActivity {

    private Toolbar mToolbar;
    private ImageView img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_imageview );
        mToolbar=(Toolbar)findViewById( R.id.imageviewToolbar );
        setSupportActionBar( mToolbar );
        getSupportActionBar().setTitle( "View Image" );
        getSupportActionBar().setDisplayHomeAsUpEnabled( true );

        img=(ImageView)findViewById( R.id.imgview );

        String path=getIntent().getStringExtra( "path" );
        img.setImageURI( Uri.parse( path ) );



    }
}
