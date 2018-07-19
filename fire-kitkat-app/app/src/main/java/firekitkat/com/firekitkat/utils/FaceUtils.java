package firekitkat.com.firekitkat.utils;

import android.graphics.Rect;

import com.google.android.gms.vision.face.Landmark;
import com.google.firebase.ml.vision.common.FirebaseVisionPoint;
import com.google.firebase.ml.vision.face.FirebaseVisionFace;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceLandmark;

import firekitkat.com.firekitkat.models.FaceMap;

/***
 * Class intended to help on distance calculations to get a FaceMap Object
 */
public class FaceUtils {
    /***
     * Get distance from one FirebaseVisionPoint to another
     * @param startPoint a FirebaseVisionPoint start point
     * @param endPoint a FirebaseVisionPoint end point
     * @return returns the resulting distance between those points
     */
    private static double getDistance(FirebaseVisionPoint startPoint, FirebaseVisionPoint endPoint){
        return Math.sqrt(Math.pow((double)(endPoint.getX() - startPoint.getX()), 2) +
                Math.pow((double)(endPoint.getY() - startPoint.getY()), 2));
    }

    /***
     * Used to get the distance between a FirebaseVisionPoint and the Face Rect
     * @param rect Face rect
     * @param endPoint ideally the nose FirebaseVisionPoint
     * @return resulting distance between the left-top corner of the rect and the FirebaseVisionPoint
     */
    private static double getDistanceRect(Rect rect, FirebaseVisionPoint endPoint){
        return Math.sqrt(Math.pow((double)(endPoint.getX() - rect.left), 2) +
                Math.pow((double)(endPoint.getY() - rect.top), 2));
    }

    /***
     * Get the distance in a percentage unit taking a distance  as a reference
     * (Distance between face rect corner and nose)
     * @param startPoint
     * @param endPoint
     * @param reference
     * @return
     */
    private static double getDistanceRatio(FirebaseVisionPoint startPoint, FirebaseVisionPoint endPoint, double reference){
        double distance = FaceUtils.getDistance(startPoint, endPoint);
        return distance/reference*100;
    }

    /***
     * Methods create a FaceMap object
     * of a given FirebaseVisionFace
     * @param face Detected face FirebaseVisionFace
     * @return  FaceMap object (object holding distances from nose to other points)
     */
    public static FaceMap getFaceMap (FirebaseVisionFace face){
        FirebaseVisionFaceLandmark leftMouth = face.getLandmark(Landmark.LEFT_MOUTH);
        FirebaseVisionFaceLandmark rightMouth = face.getLandmark(Landmark.RIGHT_MOUTH);
        FirebaseVisionFaceLandmark leftEye = face.getLandmark(Landmark.LEFT_EYE);
        FirebaseVisionFaceLandmark rightEye = face.getLandmark(Landmark.RIGHT_EYE);
        FirebaseVisionFaceLandmark noseBase = face.getLandmark(Landmark.NOSE_BASE);

        double distanceReference = getDistanceRect(face.getBoundingBox(), noseBase.getPosition());
        FaceMap facemap = new FaceMap();
        facemap.setLeftMouthDistance(
                FaceUtils.getDistanceRatio(
                        leftMouth.getPosition(), noseBase.getPosition(), distanceReference)
        );
        facemap.setRightMouthDistance(
                FaceUtils.getDistanceRatio(
                        rightMouth.getPosition(), noseBase.getPosition(), distanceReference)
        );
        facemap.setLeftEyeDistance(
                FaceUtils.getDistanceRatio(
                        leftEye.getPosition(), noseBase.getPosition(), distanceReference)
        );
        facemap.setRightEyeDistance(
                FaceUtils.getDistanceRatio(
                        rightEye.getPosition(), noseBase.getPosition(), distanceReference)
        );

        return facemap;
    }
}
