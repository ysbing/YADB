package android.content;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.IInterface;

import com.ysbing.yrouter.api.YRouterSystem;

@YRouterSystem
public interface IClipboard extends IInterface {
    ClipData getPrimaryClip(String pkg);

    @TargetApi(Build.VERSION_CODES.Q)
    ClipData getPrimaryClip(String pkg, int user);

    @TargetApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    ClipData getPrimaryClip(String pkg, String attributionTag, int userId, int deviceId);

    void setPrimaryClip(ClipData clip, String callingPackage);

    @TargetApi(Build.VERSION_CODES.Q)
    void setPrimaryClip(ClipData clip, String callingPackage, int user);

    @TargetApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    void setPrimaryClip(ClipData clip, String callingPackage, String attributionTag, int userId,
                        int deviceId);
}