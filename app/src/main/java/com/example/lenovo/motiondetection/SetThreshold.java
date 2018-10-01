package com.example.lenovo.motiondetection;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class SetThreshold extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener {

    private Toolbar mToolbar;
    private Button preCloser,preFarther,start;
    private SeekBar seekbar;
    private TextView progressText;
    int progressChangedValue=15;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission( Manifest.permission.CAMERA )
                    != PackageManager.PERMISSION_GRANTED) {

                // Should we show an explanation?
                if (shouldShowRequestPermissionRationale(
                        Manifest.permission.CAMERA )) {
                    // Explain to the user why we need to use the camera
                }

                requestPermissions( new String[]{Manifest.permission.CAMERA},
                        1234 );
            }
        }

        setContentView( R.layout.activity_set_threshold );

        mToolbar=(Toolbar)findViewById( R.id.imageviewToolbar );
        setSupportActionBar( mToolbar );
        getSupportActionBar().setTitle( "Set Threshold" );
        getSupportActionBar().setDisplayHomeAsUpEnabled( true );

        preCloser=(Button)findViewById( R.id.st_presetCloserBtn );
        preFarther=(Button)findViewById( R.id.st_presetFartherBtn );
        start=(Button)findViewById( R.id.st_startBtn );
        progressText=(TextView)findViewById( R.id.st_tValue ) ;
        seekbar=(SeekBar)findViewById( R.id.sb );

        seekbar.setMax( 100 );
        seekbar.setProgress( 15 );
        seekbar.setOnSeekBarChangeListener(SetThreshold.this);

        preCloser.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seekbar.setProgress( 15 );
            }
        } );

        preFarther.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seekbar.setProgress( 10 );
            }
        } );

        start.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity( new Intent( SetThreshold.this,MainActivity.class ).putExtra( "threshold",progressChangedValue ) );
            }
        } );
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        progressChangedValue=progress;
        if(progress==10){
            preFarther.setBackgroundColor( getColor( android.R.color.holo_blue_light ) );
            preCloser.setBackgroundColor( getColor( R.color.colorPrimary ) );
        }
        if(progress==15){
            preCloser.setBackgroundColor( getColor( android.R.color.holo_blue_light ) );
            preFarther.setBackgroundColor( getColor( R.color.colorPrimary ) );
        }
        progressText.setText(String.valueOf( progress ));
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
