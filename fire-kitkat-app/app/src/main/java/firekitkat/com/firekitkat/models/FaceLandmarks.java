package firekitkat.com.firekitkat.models;

import android.graphics.Point;

public class FaceLandmarks {
    private Point leftEye;
    private Point rightEye;
    private Point leftMouth;
    private Point rightMouth;
    private Point nose;

    private double xScaleFactor;
    protected double yScaleFactor;

    public double getxScaleFactor() {
        return xScaleFactor;
    }

    public void setxScaleFactor(double xScaleFactor) {
        this.xScaleFactor = xScaleFactor;

    }

    public double getyScaleFactor() {
        return yScaleFactor;
    }

    public void setyScaleFactor(double yScaleFactor) {
        this.yScaleFactor = yScaleFactor;
    }

    public Point getLeftEye() {
        return leftEye;
    }

    public void setLeftEye(Point leftEye) {
        this.leftEye = leftEye;
    }

    public Point getRightEye() {
        return rightEye;
    }

    public void setRightEye(Point rightEye) {
        this.rightEye = rightEye;
    }

    public Point getLeftMouth() {
        return leftMouth;
    }

    public void setLeftMouth(Point leftMouth) {
        this.leftMouth = leftMouth;
    }

    public Point getRightMouth() {
        return rightMouth;
    }

    public void setRightMouth(Point rightMouth) {
        this.rightMouth = rightMouth;
    }

    public Point getNose() {
        return nose;
    }

    public void setNose(Point nose) {
        this.nose = nose;
    }
}
