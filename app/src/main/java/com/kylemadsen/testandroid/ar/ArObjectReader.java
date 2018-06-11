package com.kylemadsen.testandroid.ar;

import android.media.Image;
import com.google.ar.core.Anchor;
import com.google.ar.core.Camera;
import com.google.ar.core.Frame;
import com.google.ar.core.HitResult;
import com.google.ar.core.LightEstimate;
import com.google.ar.core.Plane;
import com.google.ar.core.Point;
import com.google.ar.core.PointCloud;
import com.google.ar.core.Pose;
import com.google.ar.core.Trackable;
import com.google.ar.core.TrackingState;
import com.google.ar.core.exceptions.NotYetAvailableException;
import java.nio.FloatBuffer;
import java.util.Locale;

public class ArObjectReader {

    public static String read(HitResult hitResult) {
        return "{ ClassName=" + hitResult.getClass().getSimpleName() +
               ", getDistance=" + hitResult.getDistance() +
               ", getHitPose=" + read(hitResult.getHitPose()) +
               ", createAnchor=" + read(hitResult.createAnchor()) +
               "}";
    }

    private static String read(Pose pose) {
        return "{ ClassName=" + pose.getClass().getSimpleName() +
               ", tx=" + pose.tx() +
               ", ty=" + pose.ty() +
               ", tz=" + pose.tz() +
               ", qx=" + pose.qx() +
               ", qy=" + pose.qy() +
               ", qz=" + pose.qz() +
               ", qw=" + pose.qw() +
               "]";
    }

    private static String read(Anchor anchor) {
        return "{ ClassName=" + anchor.getClass().getSimpleName() +
               "getCloudAnchorId=" + anchor.getCloudAnchorId() +
               ", getCloudAnchorState=" + read(Anchor.CloudAnchorState.class, anchor.getCloudAnchorState()) +
               ", getTrackingState=" + read(TrackingState.class, anchor.getTrackingState()) +
               "}";
    }

    public static String read(Frame frame) {
        return "{ ClassName=" + frame.getClass().getSimpleName() +
               ", acquireCameraImage=" + readAcquireCameraImage(frame) +
               ", acquirePointCloud=" + readAcquirePointCloud(frame) +
               ", getCamera=" + read(frame.getCamera()) +
               ", getLightEstimate=" + read(frame.getLightEstimate()) +
               ", getTimestamp=" + frame.getTimestamp() +
               "}";
    }

    private static String readAcquirePointCloud(Frame frame) {
        PointCloud pointCloud = frame.acquirePointCloud();
        final String pointCloudString = "{ ClassName=" + pointCloud.getClass().getSimpleName() +
                ", getTimestamp=" + pointCloud.getTimestamp() +
                ", acquirePointCloud=" + read(pointCloud.getPoints()) +
                "}";
        pointCloud.release();
        return pointCloudString;
    }


    private static String read(FloatBuffer points) {
        if (points.hasArray()) {
            return "{ ClassName=" + points.getClass().getSimpleName() +
                    ", hasArray=true" +
                    ", arrayLength=" + points.array().length +
                    ", arrayOffset=" + points.arrayOffset() +
                    "}";
        } else {
            return "{ ClassName=" + points.getClass().getSimpleName() +
                    ", hasArray=false" +
                    "}";
        }
    }

    public static String readAcquireCameraImage(Frame frame) {
        try {
            Image image =  frame.acquireCameraImage();
            String imageDimensions = "[width=" + image.getWidth() + ", height=" + image.getHeight() + "]";
            image.close();
            return imageDimensions;
        } catch (NotYetAvailableException e) {
            return "not yet available";
        }
    }

    public static String read(Camera camera) {
        return "{ ClassName=" + camera.getClass().getSimpleName() +
                ", getDisplayOrientedPose=" + read(camera.getDisplayOrientedPose()) +
                ", getPose=" + read(camera.getPose()) +
                ", getTrackingState=" + read(TrackingState.class, camera.getTrackingState()) +
                "}";
    }

    public static String read(LightEstimate lightEstimate) {
        return "{ ClassName=" + lightEstimate.getClass().getSimpleName() +
                ", getPixelIntensity=" + lightEstimate.getPixelIntensity() +
                ", getPose=" + read(LightEstimate.State.class, lightEstimate.getState()) +
                ", getTrackingState=" + read(LightEstimate.State.class, lightEstimate.getState()) +
                "}";
    }

    public static String read(Trackable trackable) {
        return "{ ClassName=" + trackable.getClass().getSimpleName() +
                ", getTrackingState=" + read(TrackingState.class, trackable.getTrackingState()) +
                ", getAnchors.size=" + trackable.getAnchors().size() +
                "}";
    }

    public static String read(Plane plane) {
        return "{ ClassName=" + plane.getClass().getSimpleName() +
                ", getTrackingState=" + read(TrackingState.class, plane.getTrackingState()) +
                ", getType=" + read(Plane.Type.class, plane.getType()) +
                ", getCenterPose=" + read(plane.getCenterPose()) +
                ", getExtentX=" + plane.getExtentX() +
                ", getExtentZ=" + plane.getExtentZ() +
                ", getPolygon=" + plane.getPolygon().toString() +
                "}";
    }

    public static String read(Point point) {
        return "{ ClassName=" + point.getClass().getSimpleName() +
                ", getTrackingState=" + read(TrackingState.class, point.getTrackingState()) +
                ", getOrientationMode=" + read(Point.OrientationMode.class, point.getOrientationMode()) +
                ", getAnchors.size=" + point.getAnchors().size() +
                "}";
    }

    public static <T extends Enum> String read(Class<T> enumClass, T enumValue) {
        if (enumValue == null) {
            return "null";
        }

        for (T e : enumClass.getEnumConstants()) {
            String lhsValue = e.name().toLowerCase(Locale.ENGLISH);
            String rhsValue = enumValue.name().toLowerCase(Locale.ENGLISH);
            if (lhsValue.equals(rhsValue)) {
                return rhsValue;
            }
        }

        return "UNKNOWN VALUE " + enumValue.name();
    }
}
