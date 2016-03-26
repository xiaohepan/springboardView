/**
 *
 */
package com.panxiaohe.springboard.demo;

import java.io.ByteArrayOutputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.util.Base64;

/**
 * @author panxiaohe
 */
public class ImageUtil
{

    private Context context;

    public ImageUtil(Context context)
    {
        this.context = context;
    }

    private static Drawable createDrawable(Drawable d, Paint p)
    {

        BitmapDrawable bd = (BitmapDrawable) d;
        Bitmap b = bd.getBitmap();
        Bitmap bitmap = Bitmap.createBitmap(bd.getIntrinsicWidth(), bd.getIntrinsicHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawBitmap(b, 0, 0, p); // 关键代码，使用新的Paint画原图，

        return new BitmapDrawable(bitmap);
    }

    /**
     * 设置Selector。 本次只增加点击变暗的效果，注释的代码为更多的效果
     */
    public static StateListDrawable createSLD(Drawable drawable)
    {
        StateListDrawable bg = new StateListDrawable();
        Paint p = new Paint();
        p.setColor(0x40222222); // Paint ARGB色值，A = 0x40 不透明。RGB222222 暗色

        Drawable normal = drawable;
        Drawable pressed = createDrawable(drawable, p);
        // p = new Paint();
        // p.setColor(0x8000FF00);
        // Drawable focused = createDrawable(drawable, p);
        // p = new Paint();
        // p.setColor(0x800000FF);
        // Drawable unable = createDrawable(drawable, p);
        // View.PRESSED_ENABLED_STATE_SET
        bg.addState(new int[]{android.R.attr.state_pressed, android.R.attr.state_enabled}, pressed);
        // View.ENABLED_FOCUSED_STATE_SET
        // bg.addState(new int[] { android.R.attr.state_enabled,
        // android.R.attr.state_focused }, focused);
        // View.ENABLED_STATE_SET
        bg.addState(new int[]{android.R.attr.state_enabled}, normal);
        // View.FOCUSED_STATE_SET
        // bg.addState(new int[] { android.R.attr.state_focused }, focused);
        // // View.WINDOW_FOCUSED_STATE_SET
        // bg.addState(new int[] { android.R.attr.state_window_focused },
        // unable);
        // View.EMPTY_STATE_SET
        bg.addState(new int[]{}, normal);
        return bg;
    }

    //	/**
//	 * 获取客户端本地图片
//	 * */
//	public static Drawable getImageDrawable(String imageName, Context context) {
//		Util util = new Util(context);
//		int id = util.getDrawableId(imageName);
//		if (id != 0) {
//			return context.getResources().getDrawable(id);
//		} else {
//			return null;
//		}
//	}
//
    public static Drawable getStateListDrawable(Drawable background)
    {
        // TODO Auto-generated method stub
        return createSLD(background);
    }

//	/**
//	 * 获取带点击效果的图片对象
//	 * */
//	public static StateListDrawable getStateListDrawable(int imageName, Context context) {
//		return getStateListDrawable(imageName, context);
//	}

    /**
     * @param imageName drawable的名称
     * @param context   包名
     * @return
     */
    public static StateListDrawable getStateListDrawable(int imageName, Context context)
    {
        Drawable drawable;
        drawable = context.getResources().getDrawable(imageName);
        if (drawable != null)
        {
            return createSLD(drawable);
        } else
        {
            return null;
        }
    }

    /**
     * 将Bitmap转换成字符串
     *
     * @param bitmap
     * @return
     * @author guo_fenghua
     */
    public static String bitmaptoString(Bitmap bitmap)
    {
        if (bitmap == null)
        {
            return "";
        }
        String string = null;
        ByteArrayOutputStream bStream = new ByteArrayOutputStream();
        bitmap.compress(CompressFormat.PNG, 100, bStream);
        byte[] bytes = bStream.toByteArray();
        string = Base64.encodeToString(bytes, Base64.DEFAULT);
        return string;
    }

    /**
     * 将字符串转换成Bitmap类型
     *
     * @param string
     * @return
     * @author guo_fenghua
     */
    public static Bitmap stringtoBitmap(String string)
    {
        Bitmap bitmap = null;
        try
        {
            byte[] bitmapArray;
            bitmapArray = Base64.decode(string, Base64.DEFAULT);
            bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length);
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        return bitmap;
    }

}
