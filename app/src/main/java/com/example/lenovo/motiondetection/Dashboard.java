package com.example.lenovo.motiondetection;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;

public class Dashboard extends AppCompatActivity {

    private Toolbar mToolbar;
    private GridView gv;
    private ArrayList<File> list;
    private File path;
    private FloatingActionButton fab_new;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission( Manifest.permission.READ_EXTERNAL_STORAGE )
                    != PackageManager.PERMISSION_GRANTED) {

                // Should we show an explanation?
                if (shouldShowRequestPermissionRationale(
                        Manifest.permission.READ_EXTERNAL_STORAGE )) {
                    // Explain to the user why we need to use the camera
                }

                requestPermissions( new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        123 );
            }
        }

        setContentView( R.layout.activity_dashboard );

        mToolbar=(Toolbar)findViewById( R.id.imageviewToolbar );
        fab_new=(FloatingActionButton)findViewById( R.id.fabPlus );
        setSupportActionBar( mToolbar );
        getSupportActionBar().setTitle( "Dashboard" );

    }

    @Override
    protected void onResume() {
        super.onResume();

        path= new File( Environment.getExternalStorageDirectory() + "/Motion_Detection/Images" );
        if (!path.exists()) {
            File Directory = new File( Environment.getExternalStorageDirectory() + "/Motion_Detection/Images/" );
            Directory.mkdirs();
        }
        list=imageLoader(path);

        gv=(GridView)findViewById( R.id.gv );
        gv.setAdapter( new gridAdapter() );


        gv.setOnItemClickListener( new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startActivity( new Intent( Dashboard.this,Imageview.class ).putExtra( "path",list.get( position ).toString() ) );
            }
        } );

        fab_new.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity( new Intent( Dashboard.this,SetThreshold.class ) );
            }
        } );

    }

    private ArrayList<File> imageLoader(File path) {
        ArrayList<File> a=new ArrayList<>();
        File[] files=path.listFiles();


           try{
               for(int i=0;i<files.length;i++){
                   if(files[i].isDirectory()){
                       a.addAll( imageLoader( files[i]) );
                   }
                   else{
                       if(files[i].getName().endsWith( ".jpg" )){
                           a.add(files[i]);
                       }
                   }
               }
           }catch (Exception e){
               e.printStackTrace();
           }

        return a;
    }

    public class gridAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get( position );
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView=getLayoutInflater().inflate( R.layout.single_image,parent,false );
            ImageView imageView=(ImageView)convertView.findViewById( R.id.iv );
            Glide.with(getApplicationContext()).load(getItem( position )).into(imageView);
            //imageView.setImageURI( Uri.parse( getItem( position ).toString() ) );
            return convertView;
        }
    }

}
