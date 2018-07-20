package firekitkat.com.firekitkat.views;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.vision.face.Landmark;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata;
import com.google.firebase.ml.vision.face.FirebaseVisionFace;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.otaliastudios.cameraview.CameraView;
import com.otaliastudios.cameraview.Facing;
import com.otaliastudios.cameraview.Frame;
import com.otaliastudios.cameraview.FrameProcessor;
import com.otaliastudios.cameraview.Size;

import java.io.ByteArrayOutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import firekitkat.com.firekitkat.FireKitkat;
import firekitkat.com.firekitkat.R;
import firekitkat.com.firekitkat.models.Artist;
import firekitkat.com.firekitkat.models.FaceLandmarks;
import firekitkat.com.firekitkat.models.FaceMap;
import firekitkat.com.firekitkat.models.Match;
import firekitkat.com.firekitkat.models.User;
import firekitkat.com.firekitkat.utils.FaceUtils;
import firekitkat.com.firekitkat.views.custom.FaceMapView;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class MatchActivity extends AppCompatActivity implements FrameProcessor, OnSuccessListener<List<FirebaseVisionFace>>, OnFailureListener {
    private CameraView cameraView;
    boolean faceMatchRequested = false;
    private FirebaseVisionFaceDetectorOptions options;
    private FirebaseVisionFaceDetector detector;
    private TextView tvWaitFaceRecognition;
    private Button btnTryAgain, btnPlayVideo, btnPost;
    private FaceMapView ivProcessedImage;
    private Gson gson;
    private FirebaseFunctions firebaseFunctions;

    private FaceLandmarks faceLandmarks;
    private ImageView ivMACameraShot, ivMAArtist;

    private boolean frozePic=false;
    private Match matchResult;
    private FrameLayout frameLayout;
    private Bitmap currentPhoto;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private StorageReference storageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match);
        cameraView = findViewById(R.id.cameraView);
        cameraView.setFacing(Facing.FRONT);
        cameraView.addFrameProcessor(this);
        faceLandmarks = new FaceLandmarks();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference("users");
        db = FirebaseFirestore.getInstance();
        gson = new Gson();
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
        ivProcessedImage.initData(faceLandmarks);
        ivMACameraShot = findViewById(R.id.ivMACameraShot);
        ivMAArtist = findViewById(R.id.ivMAArtist);
        frameLayout = findViewById(R.id.frameLayout);
        btnPlayVideo = findViewById(R.id.btnPlayMusicVideo);
        btnPost = findViewById(R.id.btnPost);
        firebaseFunctions =  FirebaseFunctions.getInstance();
    }

    private Task<Match> getMatch(FaceMap faceMap) {
        // Create the arguments to the callable function.
        Map<String, Double> data = new HashMap<>();
        data.put("leftEyeDistance", faceMap.getLeftEyeDistance());
        data.put("rightEyeDistance", faceMap.getRightEyeDistance());
        data.put("leftMouthDistance", faceMap.getLeftMouthDistance());
        data.put("rightMouthDistance", faceMap.getRightMouthDistance());
        Log.d("kitkat", " ABOUT TO EXCECUTE FUNCTION");
        return firebaseFunctions
                .getHttpsCallable("whoAmIAlike")
                .call(data)
                .continueWith(new Continuation<HttpsCallableResult, Match>() {
                    @Override
                    public Match then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                        String resultString = (String) task.getResult().getData();
                        Match result = gson.fromJson(resultString, Match.class);
                        if(result!=null && result.getArtist()!=null){
                            MatchActivity.this.matchResult= result;
                            Log.d("kitkat", result.getArtist().getName() + " ARTIST NAME");
                            //startVideo(result.getArtist().getVideoId());
                            btnTryAgain.setVisibility(VISIBLE);

                            ivMAArtist.setVisibility(VISIBLE);
                            ivMACameraShot.setVisibility(VISIBLE);
                            btnPost.setVisibility(VISIBLE);
                            btnPlayVideo.setVisibility(VISIBLE);
                            Glide.with(ivMAArtist.getContext())
                                    .load(matchResult.getArtist().getPhotoUrl())
                                    .into(ivMAArtist);
                            ivMACameraShot.setImageBitmap(currentPhoto);

                        }
                        else{
                            Log.d("kitkat", "RESULT NULL");
                        }
                        return result;
                    }
                });
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
            faceLandmarks.setxScaleFactor(size.getWidth());
            faceLandmarks.setyScaleFactor(size.getHeight());
            FirebaseVisionImage fbVisionImage = FirebaseVisionImage.fromByteArray(data, metadata);
            if(!frozePic)
                currentPhoto = fbVisionImage.getBitmapForDebugging();
            detector.detectInImage(fbVisionImage)
                    .addOnSuccessListener(MatchActivity.this)
                    .addOnFailureListener(MatchActivity.this);

        }
    }

    @Override
    public void onSuccess(List<FirebaseVisionFace> firebaseVisionFaces) {
        for (FirebaseVisionFace face : firebaseVisionFaces) {
            if(face.getLandmark(Landmark.LEFT_MOUTH)!=null)
                faceLandmarks.setLeftMouth(new Point(face.getLandmark(
                        Landmark.LEFT_MOUTH).getPosition().getX().intValue(),
                        face.getLandmark(Landmark.LEFT_MOUTH).getPosition().getY().intValue()));
            if(face.getLandmark(Landmark.RIGHT_MOUTH)!=null)
                faceLandmarks.setRightMouth(new Point(face.getLandmark(
                        Landmark.RIGHT_MOUTH).getPosition().getX().intValue(),
                        face.getLandmark(Landmark.RIGHT_MOUTH).getPosition().getY().intValue()));
            if(face.getLandmark(Landmark.LEFT_EYE)!=null)
                faceLandmarks.setLeftEye(new Point(face.getLandmark(
                        Landmark.LEFT_EYE).getPosition().getX().intValue(),
                        face.getLandmark(Landmark.LEFT_EYE).getPosition().getY().intValue()));
            if(face.getLandmark(Landmark.RIGHT_EYE)!=null)
                faceLandmarks.setRightEye(new Point(face.getLandmark(
                        Landmark.RIGHT_EYE).getPosition().getX().intValue(),
                        face.getLandmark(Landmark.RIGHT_EYE).getPosition().getY().intValue()));
            if(face.getLandmark(Landmark.NOSE_BASE)!=null)
                faceLandmarks.setNose(new Point(face.getLandmark(
                        Landmark.NOSE_BASE).getPosition().getX().intValue(),
                        face.getLandmark(Landmark.NOSE_BASE).getPosition().getY().intValue()));

            ivProcessedImage.invalidate();

            if(face.getLandmark(Landmark.LEFT_MOUTH)!=null &&
                    face.getLandmark(Landmark.RIGHT_MOUTH)!=null &&
                    face.getLandmark(Landmark.NOSE_BASE) !=null &&
                    face.getLandmark(Landmark.LEFT_EYE) !=null &&
                    face.getLandmark(Landmark.RIGHT_EYE)!=null){
               FaceMap faceMap = FaceUtils.getFaceMap(face);
               if(!faceMatchRequested){
                   frozePic=true;
                   getMatch(faceMap);
                   faceMatchRequested=true;//move this when the actual firebase function is called

               }

            }


        }
    }
    private void startVideo(String videoID) { // default youtube app
        String videoId = videoID;
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + videoId));
        intent.addFlags(Intent.FLAG_ACTIVITY_LAUNCH_ADJACENT |

                Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("VIDEO_ID", videoId);
        startActivity(intent);

    }
    @Override
    public void onFailure(@NonNull Exception e) {

    }

    public void btnTryAgainOnClick(View view) {
        faceMatchRequested =false;
        view.setVisibility(GONE);

        frozePic=false;
        btnPost.setVisibility(GONE);
        btnPlayVideo.setVisibility(GONE);
        ivMAArtist.setVisibility(GONE);
        ivMACameraShot.setVisibility(GONE);
    }

    public void btnPostClick(View view) {
        ProgressBar progressBar = new ProgressBar(MatchActivity.this,null,android.R.attr.progressBarStyleLarge);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(100,100);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        frameLayout.addView(progressBar,params);
        progressBar.setVisibility(View.VISIBLE);  //To show ProgressBar
        progressBar.setVisibility(View.GONE);
        if(FireKitkat.getInstance().getUser()!=null){
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            currentPhoto.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();
            final String fileName=FireKitkat.getInstance().getUser().getUid() + new Date().toString();
            storageRef.child(fileName).putBytes(data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    storageRef.child(fileName).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            User user = new User();
                            user.setPhotoUrl(uri.toString());
                            user.setName(FireKitkat.getInstance().getUser().getDisplayName());
                            matchResult.setUser(user);
                            db.collection("matches").add(matchResult);
                            finish();
                        }
                    });

                }
            });


        }
        else
        {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
    }

    public void btnPlayVideoOnClick(View view) {
        startVideo(matchResult.getArtist().getVideoId());

    }
}
