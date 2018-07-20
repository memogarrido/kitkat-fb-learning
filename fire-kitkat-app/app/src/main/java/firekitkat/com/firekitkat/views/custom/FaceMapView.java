package firekitkat.com.firekitkat.views.custom;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import firekitkat.com.firekitkat.R;
import firekitkat.com.firekitkat.models.FaceLandmarks;

public class FaceMapView extends View {

    private FaceLandmarks faceLandmarks;

    private Paint pointLandMarkColorLeftEye, pointLandMarkColorRightEye, pointLandMarkColorLeftMouth,
            pointLandMarkColorRightMouth, pointLandMarkColorNose;
    private Rect dotRectLeftEye, dotRectRightEye, dotRectLeftMouth, dotRectRightMouth, dotRectNose;


    public FaceMapView(Context context) {
        super(context);
    }

    public FaceMapView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FaceMapView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void initData(FaceLandmarks faceLandmarks) {
        this.faceLandmarks = faceLandmarks;
        pointLandMarkColorLeftEye = new Paint();
        pointLandMarkColorRightEye = new Paint();
        pointLandMarkColorLeftMouth = new Paint();
        pointLandMarkColorRightMouth = new Paint();
        pointLandMarkColorNose = new Paint();
        pointLandMarkColorLeftEye.setColor(this.getResources().getColor(R.color.leftEyeColor));
        pointLandMarkColorRightEye.setColor(this.getResources().getColor(R.color.rightEyeColor));
        pointLandMarkColorLeftMouth.setColor(this.getResources().getColor(R.color.leftMouthColor));
        pointLandMarkColorRightMouth.setColor(this.getResources().getColor(R.color.rightMouthColor));
        pointLandMarkColorNose.setColor(this.getResources().getColor(R.color.noseColor));
        dotRectLeftEye = new Rect();
        dotRectRightEye = new Rect();
        dotRectLeftMouth = new Rect();
        dotRectRightMouth = new Rect();
        dotRectNose = new Rect();
    }


    protected void onDraw(Canvas canvas) {
        canvas.scale(-1, 1, canvas.getWidth()/2, canvas.getHeight()/2);
        if (faceLandmarks.getNose() != null) {
            dotRectNose.top = (int)(faceLandmarks.getNose().y*canvas.getHeight()/faceLandmarks.getyScaleFactor()) - 5;
            dotRectNose.left = (int)(faceLandmarks.getNose().x*canvas.getWidth()/faceLandmarks.getxScaleFactor()) - 5;
            dotRectNose.bottom = (int)(faceLandmarks.getNose().y*canvas.getHeight()/faceLandmarks.getyScaleFactor()) + 5;
            dotRectNose.right = (int)(faceLandmarks.getNose().x*canvas.getWidth()/faceLandmarks.getxScaleFactor()) + 5;
            canvas.drawRect(dotRectNose, pointLandMarkColorNose);
        }
        if (faceLandmarks.getLeftEye() != null) {
            dotRectLeftEye.top = (int)(faceLandmarks.getLeftEye().y*canvas.getHeight()/faceLandmarks.getyScaleFactor()) - 5;
            dotRectLeftEye.left = (int)(faceLandmarks.getLeftEye().x*canvas.getWidth()/faceLandmarks.getxScaleFactor()) - 5;
            dotRectLeftEye.bottom = (int)(faceLandmarks.getLeftEye().y*canvas.getHeight()/faceLandmarks.getyScaleFactor()) + 5;
            dotRectLeftEye.right = (int)(faceLandmarks.getLeftEye().x*canvas.getWidth()/faceLandmarks.getxScaleFactor()) + 5;
            canvas.drawRect(dotRectLeftEye, pointLandMarkColorLeftEye);
        }
        if (faceLandmarks.getRightEye() != null) {
            dotRectRightEye.top = (int)(faceLandmarks.getRightEye().y*canvas.getHeight()/faceLandmarks.getyScaleFactor()) - 5;
            dotRectRightEye.left = (int)(faceLandmarks.getRightEye().x*canvas.getWidth()/faceLandmarks.getxScaleFactor()) - 5;
            dotRectRightEye.bottom = (int)(faceLandmarks.getRightEye().y*canvas.getHeight()/faceLandmarks.getyScaleFactor()) + 5;
            dotRectRightEye.right = (int)(faceLandmarks.getRightEye().x*canvas.getWidth()/faceLandmarks.getxScaleFactor()) + 5;
            canvas.drawRect(dotRectRightEye, pointLandMarkColorRightEye);
        }
        if (faceLandmarks.getLeftMouth() != null) {
            dotRectLeftMouth.top = (int)(faceLandmarks.getLeftMouth().y*canvas.getHeight()/faceLandmarks.getyScaleFactor())-5;
            dotRectLeftMouth.left = (int)(faceLandmarks.getLeftMouth().x*canvas.getWidth()/faceLandmarks.getxScaleFactor())-5;
            dotRectLeftMouth.bottom = (int)(faceLandmarks.getLeftMouth().y*canvas.getHeight()/faceLandmarks.getyScaleFactor()) + 5;
            dotRectLeftMouth.right = (int)(faceLandmarks.getLeftMouth().x*canvas.getWidth()/faceLandmarks.getxScaleFactor()) + 5;
            canvas.drawRect(dotRectLeftMouth, pointLandMarkColorLeftMouth);
        }
        if (faceLandmarks.getRightMouth() != null) {
            dotRectRightMouth.top = (int)(faceLandmarks.getRightMouth().y*canvas.getHeight()/faceLandmarks.getyScaleFactor())-5;
            dotRectRightMouth.left = (int)(faceLandmarks.getRightMouth().x*canvas.getWidth()/faceLandmarks.getxScaleFactor());
            dotRectRightMouth.bottom = (int)(faceLandmarks.getRightMouth().y*canvas.getHeight()/faceLandmarks.getyScaleFactor()) + 5;
            dotRectRightMouth.right = (int)(faceLandmarks.getRightMouth().x*canvas.getWidth()/faceLandmarks.getxScaleFactor()) + 5;
            canvas.drawRect(dotRectRightMouth, pointLandMarkColorRightMouth);
        }
        //

        super.onDraw(canvas);


    }
}
