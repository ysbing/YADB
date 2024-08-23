package android.content;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.IBinder;

public interface IClipboard {
    ClipData getPrimaryClip(String pkg);

    @TargetApi(Build.VERSION_CODES.Q)
    ClipData getPrimaryClip(String pkg, int user);

    @TargetApi(Build.VERSION_CODES.TIRAMISU)
    ClipData getPrimaryClip(String pkg, String attributionTag, int userId);

    @TargetApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    ClipData getPrimaryClip(String pkg, String attributionTag, int userId, int deviceId);

    @TargetApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    ClipData getPrimaryClip(String pkg, String attributionTag, int userId, int deviceId, String targetPackage);

    void setPrimaryClip(ClipData clip, String callingPackage);

    @TargetApi(Build.VERSION_CODES.Q)
    void setPrimaryClip(ClipData clip, String callingPackage, int userId);

    @TargetApi(Build.VERSION_CODES.TIRAMISU)
    void setPrimaryClip(ClipData clip, String callingPackage, String attributionTag, int userId);

    @TargetApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    void setPrimaryClip(ClipData clip, String callingPackage, String attributionTag, int userId,
                        int deviceId);

    class Stub {
        public static IClipboard asInterface(IBinder obj) {
            return null;
        }
    }
}