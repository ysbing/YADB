package android.content;

import android.os.IInterface;

import com.ysbing.yrouter.api.YRouterSystem;

@YRouterSystem
public interface IClipboard extends IInterface {
    ClipData getPrimaryClip(String pkg);

    ClipData getPrimaryClip(String pkg, int user);

    void setPrimaryClip(ClipData clip, String callingPackage);

    void setPrimaryClip(ClipData clip, String callingPackage, int user);
}