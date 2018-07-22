package com.kylemadsen.testandroid.ar;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;
import com.google.ar.core.ArCoreApk;
import com.google.ar.core.Config;
import com.google.ar.core.Frame;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.core.Point;
import com.google.ar.core.Session;
import com.google.ar.core.Trackable;
import com.google.ar.core.TrackingState;
import com.google.ar.core.exceptions.CameraNotAvailableException;
import com.google.ar.core.exceptions.UnavailableApkTooOldException;
import com.google.ar.core.exceptions.UnavailableArcoreNotInstalledException;
import com.google.ar.core.exceptions.UnavailableDeviceNotCompatibleException;
import com.google.ar.core.exceptions.UnavailableException;
import com.google.ar.core.exceptions.UnavailableSdkTooOldException;
import com.google.ar.sceneform.ArSceneView;
import com.google.ar.sceneform.FrameTime;
import com.google.ar.sceneform.HitTestResult;
import com.google.ar.sceneform.Scene;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.ux.FootprintSelectionVisualizer;
import com.google.ar.sceneform.ux.TransformableNode;
import com.google.ar.sceneform.ux.TransformationSystem;
import com.kylemadsen.core.logger.L;
import com.kylemadsen.testandroid.R;
import java.util.Iterator;

public class ArFragment extends Fragment implements Scene.OnPeekTouchListener, Scene.OnUpdateListener {

    private boolean installRequested;
    private boolean sessionInitializationFailed = false;
    private ArSceneView arSceneView;
    private TransformationSystem transformationSystem;
    private GestureDetector gestureDetector;
    private FrameLayout frameLayout;
    private boolean isStarted;
    private boolean canRequestDangerousPermissions = true;
    @Nullable
    private ArFragment.OnTapArPlaneListener onTapArPlaneListener;

    public ArFragment() {
    }

    public ArSceneView getArSceneView() {
        return this.arSceneView;
    }

    public void setOnTapArPlaneListener(@Nullable ArFragment.OnTapArPlaneListener onTapArPlaneListener) {
        this.onTapArPlaneListener = onTapArPlaneListener;
    }

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.frameLayout = (FrameLayout)inflater.inflate(R.layout.sceneform_ux_fragment_layout, container, true);
        this.arSceneView = frameLayout.findViewById(R.id.sceneform_ar_scene_view);
        if (Build.VERSION.SDK_INT < 24) {
            return this.frameLayout;
        } else {
            FootprintSelectionVisualizer selectionVisualizer = new FootprintSelectionVisualizer();
            this.transformationSystem = new TransformationSystem(this.getResources().getDisplayMetrics(), selectionVisualizer);
            (ModelRenderable.builder().setSource(this.getActivity(), R.raw.sceneform_footprint)).build().thenAccept((renderable) -> {
                if (selectionVisualizer.getFootprintRenderable() == null) {
                    selectionVisualizer.setFootprintRenderable(renderable);
                }

            }).exceptionally((throwable) -> {
                Toast toast = Toast.makeText(this.getContext(), "Unable to load footprint renderable", 1);
                toast.setGravity(17, 0, 0);
                toast.show();
                return null;
            });
            this.gestureDetector = new GestureDetector(this.getContext(), new GestureDetector.SimpleOnGestureListener() {
                public boolean onSingleTapUp(MotionEvent e) {
                    ArFragment.this.onSingleTap(e);
                    return true;
                }

                public boolean onDown(MotionEvent e) {
                    return true;
                }
            });
            this.arSceneView.getScene().setOnPeekTouchListener(this);
            this.arSceneView.getScene().setOnUpdateListener(this);
            if (this.isArRequired()) {
                this.requestDangerousPermissions();
            }

            this.arSceneView.getViewTreeObserver().addOnWindowFocusChangeListener((hasFocus) -> {
                this.onWindowFocusChanged(hasFocus);
            });
            return this.frameLayout;
        }
    }
    
    public boolean isArRequired() {
        return true;
    }

    protected void handleSessionException(UnavailableException sessionException) {
        String message;
        if (sessionException instanceof UnavailableArcoreNotInstalledException) {
            message = "Please install ARCore";
        } else if (sessionException instanceof UnavailableApkTooOldException) {
            message = "Please update ARCore";
        } else if (sessionException instanceof UnavailableSdkTooOldException) {
            message = "Please update this app";
        } else if (sessionException instanceof UnavailableDeviceNotCompatibleException) {
            message = "This device does not support AR";
        } else {
            message = "Failed to create AR session";
            String var3 = String.valueOf(sessionException);
            Log.e("StandardArFragment", (new StringBuilder(11 + String.valueOf(var3).length())).append("Exception: ").append(var3).toString());
        }

        Toast.makeText(this.requireActivity(), message, Toast.LENGTH_SHORT).show();
    }

    protected Config getSessionConfiguration(Session session) {
        return new Config(session);
    }

    protected void requestDangerousPermissions() {
        if (this.canRequestDangerousPermissions) {
            this.canRequestDangerousPermissions = false;
            String[] permissions = new String[] {
                    "android.permission.CAMERA"
            };
            this.requestPermissions(permissions, 1010);
        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] results) {
        if (ActivityCompat.checkSelfPermission(this.requireActivity(), "android.permission.CAMERA") != 0) {
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this.requireActivity(), 16974374);
            builder.setTitle("Camera permission required").setMessage("Add camera permission via Settings?").setPositiveButton(17039370, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent();
                    intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                    intent.setData(Uri.fromParts("package", ArFragment.this.requireActivity().getPackageName(), (String)null));
                    ArFragment.this.requireActivity().startActivity(intent);
                    ArFragment.this.canRequestDangerousPermissions = true;
                }
            }).setNegativeButton(17039360, (DialogInterface.OnClickListener)null).setIcon(17301543).setOnDismissListener(new DialogInterface.OnDismissListener() {
                public void onDismiss(final DialogInterface arg0) {
                    if (!ArFragment.this.canRequestDangerousPermissions) {
                        ArFragment.this.requireActivity().finish();
                    }

                }
            }).show();
        }
    }

    public void onResume() {
        super.onResume();
        if (this.isArRequired() && this.arSceneView.getSession() == null) {
            this.initializeSession();
        }

        this.start();
    }

    protected final void initializeSession() {
        if (!this.sessionInitializationFailed) {
            if (ContextCompat.checkSelfPermission(this.requireActivity(), "android.permission.CAMERA") == 0) {
                UnavailableException sessionException;
                try {
                    switch(ArCoreApk.getInstance().requestInstall(this.requireActivity(), !this.installRequested)) {
                        case INSTALL_REQUESTED:
                            this.installRequested = true;
                            return;
                        case INSTALLED:
                        default:
                            Session session = new Session(this.requireActivity());
                            Config config = this.getSessionConfiguration(session);
                            config.setUpdateMode(Config.UpdateMode.LATEST_CAMERA_IMAGE);
                            session.configure(config);
                            this.getArSceneView().setupSession(session);
                            return;
                    }
                } catch (UnavailableException var4) {
                    sessionException = var4;
                } catch (Exception var5) {
                    sessionException = new UnavailableException();
                    sessionException.initCause(var5);
                }

                this.sessionInitializationFailed = true;
                this.handleSessionException(sessionException);
            } else {
                this.requestDangerousPermissions();
            }

        }
    }

    protected void onWindowFocusChanged(boolean hasFocus) {
        FragmentActivity activity = this.getActivity();
        if (hasFocus && activity != null) {
            activity.getWindow().getDecorView().setSystemUiVisibility(5894);
            activity.getWindow().addFlags(128);
        }

    }

    public void onPause() {
        super.onPause();
        this.stop();
    }

    public void onDestroy() {
        this.stop();
        this.arSceneView.destroy();
        super.onDestroy();
    }

    public void onPeekTouch(HitTestResult hitTestResult, MotionEvent motionEvent) {
        this.transformationSystem.getGestureDetector().onTouch(hitTestResult, motionEvent);
        if (hitTestResult.getNode() == null) {
            this.gestureDetector.onTouchEvent(motionEvent);
        }

    }

    public void onUpdate(FrameTime frameTime) {
        Frame frame = this.arSceneView.getArFrame();
        if (frame != null) {
            int resultCount = 0;
            for (Trackable trackable : frame.getUpdatedTrackables(Trackable.class)) {
                resultCount++;
            }
            if (resultCount > 0) {
                L.i("onUpdateGetTrackables:%d", resultCount);
            }

            for (Plane plane : frame.getUpdatedTrackables(Plane.class)) {
                L.i("plane=%s", ArObjectReader.read(plane));
            }

            for (Point point : frame.getUpdatedTrackables(Point.class)) {
                L.i("point=%s", ArObjectReader.read(point));
            }
        }
    }

    private void start() {
        if (!this.isStarted) {
            if (this.getActivity() != null) {
                this.isStarted = true;

                try {
                    this.arSceneView.resume();
                } catch (CameraNotAvailableException var2) {
                    this.sessionInitializationFailed = true;
                }
            }

        }
    }

    private void stop() {
        if (this.isStarted) {
            this.isStarted = false;
            this.arSceneView.pause();
        }
    }

    private void onSingleTap(MotionEvent motionEvent) {
        Frame frame = this.arSceneView.getArFrame();
        this.transformationSystem.selectNode((TransformableNode)null);
        ArFragment.OnTapArPlaneListener onTapArPlaneListener = this.onTapArPlaneListener;

        if (frame != null) {
            L.i("is there a frame");
            L.i(ArObjectReader.read(frame));
        }

        if (frame != null && onTapArPlaneListener != null && motionEvent != null && frame.getCamera().getTrackingState() == TrackingState.TRACKING) {
            Iterator var4 = frame.hitTest(motionEvent).iterator();

            int resultCount = 1;
            while(var4.hasNext()) {
                HitResult hit = (HitResult)var4.next();
                Trackable trackable = hit.getTrackable();
                L.i("resultCount:%d\n%s", resultCount++, ArObjectReader.read(hit));
                L.i(ArObjectReader.read(trackable));
                if (trackable instanceof Plane && ((Plane)trackable).isPoseInPolygon(hit.getHitPose())) {
                    Plane plane = (Plane)trackable;
                    onTapArPlaneListener.onTapPlane(hit, plane, motionEvent);
                    break;
                }
            }
        }

    }

    public interface OnTapArPlaneListener {
        void onTapPlane(HitResult hitResult, Plane plane, MotionEvent motionEvent);
    }
}
