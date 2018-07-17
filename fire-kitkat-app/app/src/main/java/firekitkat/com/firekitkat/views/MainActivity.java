package firekitkat.com.firekitkat.views;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.vision.face.Landmark;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata;
import com.google.firebase.ml.vision.face.FirebaseVisionFace;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions;
import com.otaliastudios.cameraview.CameraView;
import com.otaliastudios.cameraview.Facing;
import com.otaliastudios.cameraview.Frame;
import com.otaliastudios.cameraview.FrameProcessor;
import com.otaliastudios.cameraview.Size;

import java.util.List;

import firekitkat.com.firekitkat.R;
import firekitkat.com.firekitkat.models.FaceMap;
import firekitkat.com.firekitkat.utils.FaceUtils;

public class MainActivity extends AppCompatActivity implements FrameProcessor, OnSuccessListener<List<FirebaseVisionFace>>, OnFailureListener {
    CameraView cameraView;
    boolean faceMatchRequested = false;
    FirebaseVisionFaceDetectorOptions options;
    FirebaseVisionFaceDetector detector;
    TextView tvWaitFaceRecognition;
    Button btnTryAgain;
    ImageView ivProcessedImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cameraView = findViewById(R.id.cameraView);
        cameraView.setFacing(Facing.FRONT);
        cameraView.addFrameProcessor(this);
        options =
                new FirebaseVisionFaceDetectorOptions.Builder()
                        .setModeType(FirebaseVisionFaceDetectorOptions.ACCURATE_MODE)
                        .setLandmarkType(FirebaseVisionFaceDetectorOptions.ALL_LANDMARKS)
                        .setClassificationType(FirebaseVisionFaceDetectorOptions.NO_CLASSIFICATIONS)
                        .setMinFaceSize(0.15f)
                        .setTrackingEnabled(true)
                        .build();
        detector = FirebaseVision.getInstance()
                .getVisionFaceDetector(options);
        tvWaitFaceRecognition = findViewById(R.id.tvWaitFaceRecognition);
        btnTryAgain = findViewById(R.id.btnTryAgain);
        ivProcessedImage = findViewById(R.id.ivProcessedImage);
    }

    @Override
    protected void onResume() {
        super.onResume();
        cameraView.start();

    }

    @Override
    protected void onPause() {
        super.onPause();
        cameraView.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraView.destroy();
    }

    @Override
    public void process(@NonNull Frame frame) {
        byte[] data = frame.getData();
        int rotation = frame.getRotation();
        Size size = frame.getSize();
        int format = frame.getFormat();
        int firebaseRotation =
                rotation == 90 ? FirebaseVisionImageMetadata.ROTATION_90 :
                        rotation == 180 ? FirebaseVisionImageMetadata.ROTATION_180 :
                                rotation == 270 ? FirebaseVisionImageMetadata.ROTATION_270 :
                                        FirebaseVisionImageMetadata.ROTATION_0;
        if (size!=null) {
            FirebaseVisionImageMetadata metadata = new
                    FirebaseVisionImageMetadata.Builder()
                    .setWidth(size.getWidth())
                    .setHeight(size.getHeight())
                    .setFormat(format)
                    .setRotation(firebaseRotation)
                    .build();
            FirebaseVisionImage fbVisionImage = FirebaseVisionImage.fromByteArray(data, metadata);

            detector.detectInImage(fbVisionImage)
                    .addOnSuccessListener(MainActivity.this)
                    .addOnFailureListener(MainActivity.this);

        }
    }

    @Override
    public void onSuccess(List<FirebaseVisionFace> firebaseVisionFaces) {
        for (FirebaseVisionFace face : firebaseVisionFaces) {

            Log.d("mltest", face.getLandmark(Landmark.LEFT_MOUTH) + "LM");
            Log.d("mltest", face.getLandmark(Landmark.RIGHT_MOUTH) + "RM");
            Log.d("mltest", face.getLandmark(Landmark.NOSE_BASE) + "NB");
            Log.d("mltest", face.getLandmark(Landmark.LEFT_EYE) + "LEY");
            Log.d("mltest", face.getLandmark(Landmark.RIGHT_EYE) + "REY");
            if(face.getLandmark(Landmark.LEFT_MOUTH)!=null &&
                    face.getLandmark(Landmark.RIGHT_MOUTH)!=null &&
                    face.getLandmark(Landmark.NOSE_BASE) !=null &&
                    face.getLandmark(Landmark.LEFT_EYE) !=null &&
                    face.getLandmark(Landmark.RIGHT_EYE)!=null){
               FaceMap faceMap = FaceUtils.getFaceMap(face);
                faceMatchRequested=true;//move this when the actual firebase function is called
                btnTryAgain.setVisibility(View.VISIBLE);
                tvWaitFaceRecognition.setText("LeftEye: " + faceMap.getLeftEyeDistance() + " RightEye: " + faceMap.getRightEyeDistance() );
                //startVideo("OKrloDzGpU");
            }


        }
    }
    private void startVideo(String videoID) { // default youtube app
        String videoId = videoID;
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + videoId));
        intent.putExtra("VIDEO_ID", videoId);
        startActivity(intent);

    }
    @Override
    public void onFailure(@NonNull Exception e) {

    }

    public void btnTryAgainOnClick(View view) {
        faceMatchRequested =false;
        view.setVisibility(View.GONE);
    }
}
