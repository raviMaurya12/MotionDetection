package com.example.lenovo.motiondetection;
import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.video.BackgroundSubtractorMOG2;
import org.opencv.video.Video;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2 {

    private static final String TAG = "OCVSample::Activity";

    private static CameraBridgeViewBase mOpenCvCameraView;

    private BackgroundSubtractorMOG2 sub;
    private Mat mGray;
    private Mat mRgb;
    private Mat mFGMask;
    private Mat mrgba,mRotated;
    //private List<MatOfPoint> contours;
    //private double lRate = 0.5;
    private static int check;
    private Date date;
    private long millis;
    private int flag=0;
    private File path;
    //        threshold to be set by the user
    private int threshold=15;

    public MainActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "called onCreate");
        //Toast.makeText( this, "onCreate called.", Toast.LENGTH_SHORT ).show();

        // to check the android version
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission( Manifest.permission.WRITE_EXTERNAL_STORAGE )
                    != PackageManager.PERMISSION_GRANTED) {

                // Should we show an explanation?
                if (shouldShowRequestPermissionRationale(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE )) {
                    // Explain to the user why we need to use the camera
                }

                requestPermissions( new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        12345);
            }
        }

        super.onCreate( savedInstanceState );
        threshold=getIntent().getIntExtra( "threshold",15 );
        getWindow().addFlags( WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView( R.layout.activity_main );

        date=new Date();
        millis=date.getTime();

        path= new File( Environment.getExternalStorageDirectory() + "/Motion_Detection/Images" );
        if (!path.exists()) {
            File Directory = new File( Environment.getExternalStorageDirectory() + "/Motion_Detection/Images/" );
            Directory.mkdirs();
        }

        mOpenCvCameraView = findViewById(R.id.camera);
        mOpenCvCameraView.setVisibility( SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);


        if (!OpenCVLoader.initDebug()) {
            Log.i( TAG,"OpenCV Manager not Connected." );
            check=0;
        }else{
            Log.i(TAG,"OpenCV Manager Connected.");
            check=1;
            mOpenCvCameraView.enableView();
        }

    }

    @Override
    public void onPause()
    {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.i(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
        } else {
            Log.i(TAG, "OpenCV library found inside package. Using it!");
        }
    }

    public void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onCameraViewStarted(int width, int height) {

        //Toast.makeText( this, "Camera started!", Toast.LENGTH_SHORT ).show();

        //creates matrices to hold the different frames
        mRgb = new Mat();
        //mFGMask denotes the foreground mask extracted by the BackgroundSubtractorMOG2 apply function
        mFGMask = new Mat();
        mGray = new Mat();
        mRotated=new Mat();

        sub = Video.createBackgroundSubtractorMOG2();
        //creates a new BackgroundSubtractorMOG class with the arguments
//        int history = 500;

//        sub = new BackgroundSubtractorMOG2();

        //arraylist to hold individual contours
        //contours = new ArrayList<MatOfPoint>();


    }

    @Override
    public void onCameraViewStopped() {
        mRgb.release();
        mGray.release();
        mFGMask.release();
        mrgba.release();
        mRotated.release();
    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        //contours.clear();

        //gray frame because it requires less resource to process
        mGray = inputFrame.gray();
        mrgba=inputFrame.rgba();

        //this function converts the gray frame into the correct RGB format for the BackgroundSubtractorMOG2 apply function
        Imgproc.cvtColor(mGray, mRgb, Imgproc.COLOR_GRAY2RGB);

        //apply function detects objects moving and produces a foreground mask
        //the lRate updates dynamically dependent upon seekbar changes
        sub.apply(mGray, mFGMask, 0.9);

        //erode and dilate are used  to remove noise from the foreground mask
        Imgproc.erode(mFGMask, mFGMask, new Mat());
        Imgproc.dilate(mFGMask, mFGMask, new Mat());

        //drawing contours around the objects by first called findContours and then calling drawContours
        //RETR_EXTERNAL retrieves only external contours
        //CHAIN_APPROX_NONE detects all pixels for each contour
//        Imgproc.findContours(mFGMask, contours, new Mat(), Imgproc.RETR_EXTERNAL , Imgproc.CHAIN_APPROX_NONE);

        //draws all the contours in red with thickness of 2
//        Imgproc.drawContours(mRgb, contours, -1, new Scalar(255, 0, 0), 2);
        Size s = mFGMask.size();

//        calculate % of foreground pixels in the current frame

        int count = org.opencv.core.Core.countNonZero(mFGMask);
        double total = s.height * s.width;
        double percent = (count/total)*100;
//        System.out.println("% of foreground pixels is " + percent + "%");


        String displayText = "MOTION DETECTED";
        if (percent >= threshold && percent<=95) {
//        modify/replace the code below such that the text "MOTION DETECTED" gets displayed on the camera preview
            Imgproc.putText(mrgba, displayText,
                    new org.opencv.core.Point(mrgba.cols() / 3, mrgba.rows() / 2),
                    Core.FONT_HERSHEY_COMPLEX, 1, new Scalar(255, 255, 255));

            String filename="detection_"+millis+"_"+flag+".jpg";
            File file=new File( path,filename );
            filename=file.toString();
            Boolean bool = null;

            //If orientation is Landscape The the image is rotated 90 degrees before saving it.
            Display display = ((WindowManager) this.getSystemService( Context.WINDOW_SERVICE)).getDefaultDisplay();
            int rotation = display.getRotation();
            //getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE
            if (rotation == Surface.ROTATION_90) {
              //  Core.transpose(mrgba, mrgba);
              //  Core.flip(mrgba, mrgba, 1);
                Core.rotate(mrgba,mRotated, Core.ROTATE_90_CLOCKWISE); //ROTATE_180 or ROTATE_90_COUNTERCLOCKWISE
            }
            //Saving image to the location filename
            bool = Imgcodecs.imwrite(filename, mRotated);

            final Boolean finalBool = bool;
            runOnUiThread( new Runnable() {
                @Override
                public void run() {
                    if (finalBool){
                        Toast.makeText( MainActivity.this, "Saved!", Toast.LENGTH_SHORT ).show();
                        flag++;
                    }
                    else
                        Toast.makeText( MainActivity.this, "Failed to Save!", Toast.LENGTH_SHORT ).show();
                }
            } );

        }

//        return the original input frame and add the "Motion detected text on top of it" if motion is detected
        return mrgba;
    }



}

