package jp.co.zensho.android.sukiya.common;

import java.io.File;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;

public class SukiyaUtils {
    public static void setImage(Context context, ImageView view, String imageName, int defaultId) {
        if (view != null) {
            if (!StringUtils.isEmpty(imageName)) {
                File root = android.os.Environment.getExternalStorageDirectory();
                StringBuilder path = new StringBuilder();
                path.append("zenshou_data").append("/");
                path.append("images").append("/").append(imageName);
                File imageFile = new File(root, path.toString());
                if (imageFile.exists()) {
                    view.setImageURI(Uri.parse(imageFile.getAbsolutePath()));
                    Log.d("image",String.valueOf(imageFile.getAbsolutePath()));
                    return;
                }
            }
            Drawable drawable = context.getResources().getDrawable(defaultId);
            if (drawable != null) {
                view.setImageDrawable(drawable);
            }
        }
    }
}
