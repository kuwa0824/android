package io.github.kuwa0824.eyel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.lang.Math;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraActivity;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.CvType;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.android.Utils;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.imgproc.Imgproc;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class FdActivity extends CameraActivity implements CvCameraViewListener2 {

    private static final String TAG = "OCVSample::Activity";
    private static final Scalar FACE_RECT_COLOR = new Scalar(0, 255, 0, 255);
    public static final int JAVA_DETECTOR = 0;
    public static final int NATIVE_DETECTOR = 1;
    private MenuItem mItemFace50;
    private MenuItem mItemFace40;
    private MenuItem mItemFace30;
    private MenuItem mItemFace20;
    private MenuItem mItemType;
    private Mat mGray;
    private Mat mRgba;
    private Mat eye;
    private Mat lid;
    private Mat lid_mask;
    private Mat blink;
    private Mat eye_roi;
    private Mat lid_roi;
    private int resize_flg;
    private double eyex = 0;
    private double eyey = 0;
    private double wx;
    private double wy;
    private double px;
    private double py;
    private File mCascadeFile;
    private CascadeClassifier mJavaDetector;
    private io.github.kuwa0824.eyel.DetectionBasedTracker mNativeDetector;
    private int mDetectorType = JAVA_DETECTOR;
    private String[] mDetectorName;
    private float mRelativeFaceSize = 0.2f;
    private int mAbsoluteFaceSize = 0;
    private CameraBridgeViewBase mOpenCvCameraView;

    private BaseLoaderCallback  mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");
                    System.loadLibrary("detection_based_tracker");

                    try {
                        InputStream is = getResources().openRawResource(R.raw.lbpcascade_frontalface);
                        File cascadeDir = getDir("cascade", Context.MODE_PRIVATE);
                        mCascadeFile = new File(cascadeDir, "lbpcascade_frontalface.xml");
                        FileOutputStream os = new FileOutputStream(mCascadeFile);
                        byte[] buffer = new byte[4096];
                        int bytesRead;
                        while ((bytesRead = is.read(buffer)) != -1) {
                            os.write(buffer, 0, bytesRead);
                        }
                        is.close();
                        os.close();
                        mJavaDetector = new CascadeClassifier(mCascadeFile.getAbsolutePath());
                        if (mJavaDetector.empty()) {
                            Log.e(TAG, "Failed to load cascade classifier");
                            mJavaDetector = null;
                        } else
                            Log.i(TAG, "Loaded cascade classifier from " + mCascadeFile.getAbsolutePath());
                        mNativeDetector = new io.github.kuwa0824.eyel.DetectionBasedTracker(mCascadeFile.getAbsolutePath(), 0);
                        cascadeDir.delete();

                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.e(TAG, "Failed to load cascade. Exception thrown: " + e);
                    }
                    mOpenCvCameraView.enableView();
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    public FdActivity() {
        mDetectorName = new String[2];
        mDetectorName[JAVA_DETECTOR] = "Java";
        mDetectorName[NATIVE_DETECTOR] = "Native (tracking)";
        Log.i(TAG, "Instantiated new " + this.getClass());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "called onCreate");
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.face_detect_surface_view);
        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.fd_activity_surface_view);
        mOpenCvCameraView.setCameraIndex(1);
        mOpenCvCameraView.setVisibility(CameraBridgeViewBase.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);
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
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    @Override
    protected List<? extends CameraBridgeViewBase> getCameraViewList() {
        return Collections.singletonList(mOpenCvCameraView);
    }

    public void onDestroy() {
        super.onDestroy();
        mOpenCvCameraView.disableView();
    }

    public void onCameraViewStarted(int width, int height) {
        mGray = new Mat();
        mRgba = new Mat();
        eye = new Mat();
        lid = new Mat();
        lid_mask = new Mat();
        blink = new Mat();
        eye_roi = new Mat();
        lid_roi = new Mat();
        Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.eye);
        Utils.bitmapToMat(bmp, eye);
        bmp = BitmapFactory.decodeResource(getResources(), R.drawable.lid);
        Utils.bitmapToMat(bmp, lid);
        bmp = BitmapFactory.decodeResource(getResources(), R.drawable.lid_mask);
        Utils.bitmapToMat(bmp, lid_mask);
        bmp = BitmapFactory.decodeResource(getResources(), R.drawable.blink);
        Utils.bitmapToMat(bmp, blink);
        resize_flg = 0;
    }

    public void onCameraViewStopped() {
        mGray.release();
        mRgba.release();
        eye.release();
        lid.release();
        lid_mask.release();
        blink.release();
        eye_roi.release();
        lid_roi.release();
    }

    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
        mGray = inputFrame.gray();
        if (resize_flg == 0) {
            mRgba = inputFrame.rgba();
            wx = mGray.width();
            wy = mGray.height();
            double sc = wy / lid.height();
            if (wx / lid.width() < sc)
                sc = wx / lid.width();
            sc *= 0.95;
            Imgproc.resize(eye, eye, new Size(eye.width() * sc, eye.height() * sc));
            Imgproc.resize(lid, lid, new Size(lid.width() * sc, lid.height() * sc));
            Imgproc.resize(lid_mask, lid_mask, new Size(lid_mask.width() * sc, lid_mask.height() * sc));
            Imgproc.resize(blink, blink, new Size(blink.width() * sc, blink.height() * sc));
            lid_roi = mRgba.submat(new Rect((int) ((wx - lid.width()) / 2), (int) ((wy - lid.height()) / 2), lid.width(), lid.height()));
            Imgproc.rectangle(mRgba, new Point(0,0), new Point(mRgba.width(), mRgba.height()), new Scalar(0,0,0),-1);
            lid.copyTo(lid_roi);
            lid = mRgba.clone();
            blink.copyTo(lid_roi);
            blink = mRgba.clone();
            Imgproc.rectangle(mRgba, new Point(0,0), new Point(mRgba.width(), mRgba.height()), new Scalar(255,255,255),-1);
            lid_mask.copyTo(lid_roi);
            lid_mask = mRgba.clone();
            Imgproc.cvtColor(lid_mask, lid_mask, Imgproc.COLOR_RGB2GRAY);
            Imgproc.threshold(lid_mask, lid_mask, 128,255, Imgproc.THRESH_BINARY);
            lid_roi = mRgba.submat(new Rect(0,0,(int)(mRgba.width()),(int)(mRgba.height())));
            resize_flg = 1;
        }
        if (mAbsoluteFaceSize == 0) {
            int height = mGray.rows();
            if (Math.round(height * mRelativeFaceSize) > 0) {
                mAbsoluteFaceSize = Math.round(height * mRelativeFaceSize);
            }
            mNativeDetector.setMinFaceSize(mAbsoluteFaceSize);
        }
        MatOfRect faces = new MatOfRect();
        if (mDetectorType == JAVA_DETECTOR) {
            if (mJavaDetector != null)
                mJavaDetector.detectMultiScale(mGray, faces, 1.1, 2, 2, new Size(mAbsoluteFaceSize, mAbsoluteFaceSize), new Size());
        }
        else if (mDetectorType == NATIVE_DETECTOR) {
            if (mNativeDetector != null)
                mNativeDetector.detect(mGray, faces);
        }
        else {
            Log.e(TAG, "Detection method is not selected!");
        }
        Imgproc.rectangle(mRgba, new Point(0,0), new Point(mRgba.width(), mRgba.height()), new Scalar(255,255,255),-1);
        Rect[] facesArray = faces.toArray();
        if (facesArray.length > 0) {
            px = (facesArray[0].tl().x + facesArray[0].br().x) / 2;
            py = (facesArray[0].tl().y + facesArray[0].br().y) / 2;
            eyex = 0.5 - px / wx;
            eyey = py / wy - 0.5;
            eyex *= 0.5;
            eyey *= 0.5;
            if (eyex * eyex + eyey * eyey > 0.16) {
                eyex *= Math.sqrt(0.16 / (eyex * eyex + eyey * eyey));
                eyey *= Math.sqrt(0.16 / (eyex * eyex + eyey * eyey));
            }
            eyey *= 0.6;
            eyey += 0.02;
        }
        int xofs =  (int) (wx / 2 + wy * eyex - eye.width() / 2);
        int yofs =  (int) (wy / 2 + wy * eyey - eye.height() / 2);
        if (xofs < 0)
            xofs = 0;
        if (yofs < 0)
            yofs = 0;
        if (xofs + eye.width() > wx)
            xofs = (int) (wx - eye.width());
        if (yofs + eye.height() > wy)
            yofs = (int) (wy - eye.height());
        eye_roi = mRgba.submat(new Rect(xofs, yofs, eye.width(), eye.height()));
        eye.copyTo(eye_roi);
        if (resize_flg < 3) {
            blink.copyTo(lid_roi);
        } else {
            lid.copyTo(lid_roi, lid_mask);
        }
        resize_flg++;
        if (resize_flg >= 300)
            resize_flg = 1;
        return mRgba;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.i(TAG, "called onCreateOptionsMenu");
        mItemFace50 = menu.add("Face size 50%");
        mItemFace40 = menu.add("Face size 40%");
        mItemFace30 = menu.add("Face size 30%");
        mItemFace20 = menu.add("Face size 20%");
        mItemType   = menu.add(mDetectorName[mDetectorType]);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.i(TAG, "called onOptionsItemSelected; selected item: " + item);
        if (item == mItemFace50)
            setMinFaceSize(0.5f);
        else if (item == mItemFace40)
            setMinFaceSize(0.4f);
        else if (item == mItemFace30)
            setMinFaceSize(0.3f);
        else if (item == mItemFace20)
            setMinFaceSize(0.2f);
        else if (item == mItemType) {
            int tmpDetectorType = (mDetectorType + 1) % mDetectorName.length;
            item.setTitle(mDetectorName[tmpDetectorType]);
            setDetectorType(tmpDetectorType);
        }
        return true;
    }

    private void setMinFaceSize(float faceSize) {
        mRelativeFaceSize = faceSize;
        mAbsoluteFaceSize = 0;
    }

    private void setDetectorType(int type) {
        if (mDetectorType != type) {
            mDetectorType = type;
            if (type == NATIVE_DETECTOR) {
                Log.i(TAG, "Detection Based Tracker enabled");
                mNativeDetector.start();
            } else {
                Log.i(TAG, "Cascade detector enabled");
                mNativeDetector.stop();
            }
        }
    }
}
