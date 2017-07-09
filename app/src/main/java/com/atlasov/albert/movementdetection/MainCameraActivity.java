package com.atlasov.albert.movementdetection;

//import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.app.Activity;

import android.view.SurfaceView;
import android.view.WindowManager;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import static org.opencv.imgproc.Imgproc.THRESH_BINARY;

public class MainCameraActivity extends Activity implements CvCameraViewListener2 {


    private static int FRAME_MEAN_ENOUGH_TIMES = 500;
    private static Scalar MAX_IMG = new Scalar(255);


    private CameraBridgeViewBase mOpenCvCameraView;

    private Mat mGrey;
    private Mat mPreviousFrame;

    private int mFrameCountMovement;

    private Mat mDiff;
    private Mat mThresh;
    private Mat mOut;

    private Mat mElementDilation;

    static {
        System.loadLibrary("opencv_java3");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_main_camera);
        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.view);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);

        mFrameCountMovement = 0;
        mDiff = new Mat();
        mThresh = new Mat();
        mOut = new Mat();
        mPreviousFrame = new Mat();
        int dilation_size = 5;
        mElementDilation = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(2*dilation_size + 1, 2*dilation_size+1));



//        mFpsMeter = new FpsMeter();
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();

    @Override
    public void onPause() {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onResume() {
        super.onResume();
        OpenCVLoader.initDebug();
        mOpenCvCameraView.enableView();
    }

    public void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    public void onCameraViewStarted(int width, int height) {
    }

    public void onCameraViewStopped() {
        mGrey.release();
    }

    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
        mGrey = inputFrame.gray();

        if(!mPreviousFrame.empty()) {
            Core.absdiff(mGrey, mPreviousFrame, mDiff);
            Imgproc.erode(mDiff, mDiff,mElementDilation);
            Imgproc.threshold(mDiff, mThresh, 25, 255, THRESH_BINARY);


            mGrey.copyTo(mPreviousFrame);

//            Core.subtract(mGrey, mThresh, mThresh);
            mThresh.copyTo(mOut);

            Imgproc.putText(mOut, Core.mean(mOut).toString() , new Point(10, 30), 3, 1, new Scalar(255), 5);
            for (int i = 0; i < mThresh.height(); i += 50) {
                for (int j = 0; j < mThresh.width(); j += 50) {
                    double[] data = mThresh.get(i, j);
                    if (data[0] > 0 && mFrameCountMovement == 0) {
                        Utils.sendRequest();
                    }
                }
            }


        } else {
            mGrey.copyTo(mPreviousFrame);
            mGrey.copyTo(mOut);
        }

        if(mFrameCountMovement != 0) {
            mFrameCountMovement++;
        }

        if(mFrameCountMovement > 500000){
            mFrameCountMovement = 0;
        }

        return mOut;
    }
}
