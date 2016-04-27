package eu.elqet.BlueCapa;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.util.Log;
/* future API 23
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
*/

/**
 * Created by Matej Baran on 12.3.2016.
 */
public class FlashLight {

    private static FlashLight instanceFlashLight = new FlashLight();
    private Camera camera;
    //CameraManager cameraManager; /* future API 23 */
    private Context context;
    private boolean flashLightOK = false;
    private boolean flashLightState = false;

    private FlashLight() {

    }

    public static FlashLight getInstance() {
        return instanceFlashLight;
    }

    public boolean InitFlashLight(Context context) {
        instanceFlashLight.context = context.getApplicationContext();
        if (instanceFlashLight.hasFlashLight(instanceFlashLight.context)) {
            flashLightOK = true;
            return true;
        }
        return false;
        /* future API 23
        instanceFlashLight.context = context;
        //if (instanceFlashLight.isFlashLight(instanceFlashLight.context)) {
            flashLightOK = true;
            cameraManager = (CameraManager) instanceFlashLight.context.getSystemService(Context.CAMERA_SERVICE);
            try {
                for (String id : cameraManager.getCameraIdList()) {
                    cameraManager.setTorchMode(id, false);
                }
            }
            catch(CameraAccessException e) {
                return false;
            }
            return true;
        //}
        //return false;
        */
    }

    public boolean switchLightOn() {
        if (isLightOn()) return false;
        if (flashLightOK) {
            try {
                if (isLightOn()) return false;
                camera = Camera.open();
                Camera.Parameters p = camera.getParameters();
                p.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                camera.setParameters(p);
                camera.startPreview();
                setFlashLightState(true);
                return true;
            } catch (Exception e) {
                Log.e(getClass().getName(), "switchLightOn error: " + e.toString());
                return false;
            }
        }
        /* future API 23
        if (flashLightOK) {
            try {
                for (String id : cameraManager.getCameraIdList()) {
                    cameraManager.setTorchMode(id, true);
                }
            }
            catch(CameraAccessException e){
                return false;
            }
            return true;
        }
        */
        return false;
    }

    public boolean switchLightOff() {
        if (!isLightOn()) return false;
        if (flashLightOK) {
            try {
                if ((camera != null) && (isLightOn())) {
                    camera.stopPreview();
                    camera.release();
                    setFlashLightState(false);
                }
                return true;
            } catch (Exception e) {
                Log.e(getClass().getName(), "switchLightOff error: " + e.toString());
                return false;
            }
        }
        /* future API 23
        if (flashLightOK) {
            try {
                for (String id : cameraManager.getCameraIdList()) {
                    cameraManager.setTorchMode(id, false);
                }
            }
            catch(CameraAccessException e){
                return false;
            }
            return true;
        }
        */
        return false;
    }

    public boolean isLightOn() {
        return this.flashLightState;
    }

    public void setFlashLightState(boolean state) {
        this.flashLightState = state;
    }

    public boolean hasFlashLight(Context context) {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
    }
}
