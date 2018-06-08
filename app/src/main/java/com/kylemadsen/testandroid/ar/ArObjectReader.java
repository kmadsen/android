package com.kylemadsen.testandroid.ar;

import com.google.ar.core.Anchor;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.core.TrackingState;

public class ArObjectReader {

    public static String read(HitResult hitResult) {
        return "HitResult {\n"
             + "  distance: " + hitResult.getDistance() + "\n"
             + "  pose: " + hitResult.getHitPose().toString() + "\n"
             + read(hitResult.createAnchor())
             + "}\n";
    }

    private static String read(Anchor anchor) {
        return "Anchor {\n"
                + "  CloudAnchorId: " + anchor.getCloudAnchorId() + "\n"
                + "  CloudAnchorState: " + read(anchor.getCloudAnchorState()) + "\n"
                + "  TrackingState: " + read(anchor.getTrackingState()) + "\n"
                + "}\n";
    }

    private static String read(TrackingState trackingState) {
        switch (trackingState) {
            case TRACKING:
                return "tracking";
            case PAUSED:
                return "paused";
            case STOPPED:
                return "stopped";
            default:
                return "UNKNOWN VALUE " + trackingState;
        }
    }

    private static String read(Anchor.CloudAnchorState cloudAnchorState) {
        switch (cloudAnchorState) {
            case NONE:
                return "none";
            case TASK_IN_PROGRESS:
                return "task_in_progress";
            case SUCCESS:
                return "success";
            case ERROR_INTERNAL:
                return "error_internal";
            case ERROR_NOT_AUTHORIZED:
                return "error_not_authorized";
            case ERROR_SERVICE_UNAVAILABLE:
                return "error_service_unavailable";
            case ERROR_RESOURCE_EXHAUSTED:
                return "error_resource_exhausted";
            case ERROR_HOSTING_DATASET_PROCESSING_FAILED:
                return "error_hosting_dataset_processing_failed";
            case ERROR_CLOUD_ID_NOT_FOUND:
                return "error_cloud_id_not_found";
            case ERROR_RESOLVING_LOCALIZATION_NO_MATCH:
                return "error_resolving_localization_no_match";
            case ERROR_RESOLVING_SDK_VERSION_TOO_OLD:
                return "error_resolving_sdk_version_too_old";
            case ERROR_RESOLVING_SDK_VERSION_TOO_NEW:
                return "error_resolving_sdk_version_too_new";
            default:
                return "UNKNOWN VALUE " + cloudAnchorState;
        }
    }

    public static String read(Plane plane) {
        return "Plane {\n"
                + "  Type: " + read(plane.getType()) + "\n"
                + "  CenterPose: " + plane.getCenterPose().toString() + "\n"
                + "  ExtentX: " + plane.getExtentX() + "\n"
                + "  ExtentZ: " + plane.getExtentZ() + "\n"
                + "  PolygonFloatBuffer: " + plane.getPolygon().toString() + "\n"
                + "}\n";
    }

    private static String read(Plane.Type planeType) {
        switch (planeType) {
            case HORIZONTAL_UPWARD_FACING:
                return "horizontal_upward_facing";
            case HORIZONTAL_DOWNWARD_FACING:
                return "horizontal_downward_facing";
            case VERTICAL:
                return "vertical";
            default:
                return "UNKNOWN VALUE " + planeType;
        }
    }
}
