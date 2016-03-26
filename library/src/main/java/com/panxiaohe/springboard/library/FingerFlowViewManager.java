package com.panxiaohe.springboard.library;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.WindowManager;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;

/**
 * Created by panxiaohe on 16/3/9.
 *
 */
public class FingerFlowViewManager
{

    private static FingerFlowViewManager instance;

    private FingerFlowViewManager()
    {
        super();
    }

    public static FingerFlowViewManager getInstance()
    {
        if (instance == null)
        {
            instance = new FingerFlowViewManager();
        }
        return instance;
    }

    public WindowManager init(Context context)
    {
        if (figerFlowWindowManager == null)
        {
            figerFlowWindowManager = (WindowManager) context.getSystemService(
                    Context.WINDOW_SERVICE);
        }
        return figerFlowWindowManager;
    }

    public void setUp(Context context, Bitmap bitmap, int x, int y)
    {

        WindowManager.LayoutParams windowParams = getLayoutParams();

        windowParams.x = x;

        windowParams.y = y;

        getDrawImageView(context).setImageBitmap(bitmap);

        init(context).addView(drawImageView, windowParams);
    }

    public ImageView getDrawImageView(Context context)
    {

        if (drawImageView == null)
        {
            drawImageView = new ImageView(context);
        }

        return drawImageView;
    }

    public void updatePosition(int x, int y)
    {

        WindowManager.LayoutParams windowParams = getLayoutParams();

        windowParams.x = x;

        windowParams.y = y;

        figerFlowWindowManager.updateViewLayout(drawImageView, windowParams);
    }

    public void remove()
    {
        figerFlowWindowManager.removeView(drawImageView);
        drawImageView = null;
    }


    private WindowManager figerFlowWindowManager;

    private ImageView drawImageView;

    private WindowManager.LayoutParams windowParams;

    private WindowManager.LayoutParams getLayoutParams()
    {
        if (windowParams == null)
        {
            windowParams = new WindowManager.LayoutParams();
            windowParams.gravity = Gravity.TOP | Gravity.START;
            windowParams.height = LayoutParams.WRAP_CONTENT;
            windowParams.width = LayoutParams.WRAP_CONTENT;
            windowParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                    | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                    | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                    | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
            windowParams.format = PixelFormat.TRANSLUCENT;
            windowParams.windowAnimations = 0;
            windowParams.alpha = 0.8f;
        }
        return windowParams;
    }


    public static void onDestory()
    {
        instance = null;
    }
}
