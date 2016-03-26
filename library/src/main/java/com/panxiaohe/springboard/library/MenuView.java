package com.panxiaohe.springboard.library;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.KeyEvent;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * Created by panxiaohe on 16/3/9.
 * 使用这个布局显示按钮
 */
public class MenuView extends SpringboardView
{

    // 文件夹相关

    private View folderView;

    private WindowManager windowManager;

    private WindowManager getWindowManager()
    {
        if (windowManager == null)
        {
            windowManager = (WindowManager) getContext().getSystemService(
                    Context.WINDOW_SERVICE);
        }
        return windowManager;
    }


    private WindowManager.LayoutParams getWindowParams()
    {
        WindowManager.LayoutParams windowParams = new WindowManager.LayoutParams();
        windowParams.height = LayoutParams.MATCH_PARENT;
        windowParams.width = LayoutParams.MATCH_PARENT;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
        {
            windowParams.flags = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        } else
        {
            windowParams.flags = WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
        }
        windowParams.format = PixelFormat.TRANSLUCENT;
        windowParams.windowAnimations = 0;
        windowParams.x = 0;
        windowParams.y = 0;
        return windowParams;
    }

    private int folderColCount = 3;
    private int folderRowCount = 3;

    private Drawable folderDialogBackground;
    private Drawable folderViewBackground;
    private Drawable editTextBackground;
    private int folder_editText_textColor;
    private float folder_editText_textSize;

    public MenuView(Context context)
    {
        this(context, null);
    }

    public MenuView(Context context, AttributeSet attrs)
    {
        this(context, attrs, R.style.Springboard);
    }

    public MenuView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        init(context, attrs, defStyle, 0);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes)
    {
        final TypedArray a = context.obtainStyledAttributes(
                attrs, R.styleable.Springboard, defStyleAttr, defStyleRes);
        folderDialogBackground = a.getDrawable(R.styleable.Springboard_folder_dialog_background);
        folderViewBackground = a.getDrawable(R.styleable.Springboard_folder_view_background);
        editTextBackground = a.getDrawable(R.styleable.Springboard_folder_edittext_background);
        folderColCount = a.getInt(R.styleable.Springboard_folder_column_count, 3);
        folderRowCount = a.getInt(R.styleable.Springboard_folder_row_count, 3);
        folder_editText_textColor = a.getColor(R.styleable.Springboard_folder_edittext_textcolor, 0);
        folder_editText_textSize = a.getDimensionPixelSize(R.styleable.Springboard_folder_edittext_textsize, 15);
        a.recycle();
        // 点击区域外取消编辑状态
        this.setOnClickListener(new OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                getAdapter().setEditing(false);
            }
        });
    }


    @Override
    public void onItemClick(int position)
    {
        if (getAdapter().getItem(position).isFolder())
        {
            showFolder(position);
        } else
        {
            if (getAdapter().isEditting())
            {
                getAdapter().setEditing(false);
            } else
            {
                if (getOnItemClickListener() != null)
                {
                    getOnItemClickListener().onItemClick(getAdapter().getItem(position));
                }
            }
        }
    }

    @Override
    public int getItemCount()
    {
        return getAdapter().getCount();
    }

    @Override
    public FrameLayout initItemView(int position)
    {
        FrameLayout view = mAdapter.initItemView(position, this);
        mAdapter.configItemView(position, view);
        return view;
    }

    @Override
    public void onBeingDragging(MotionEvent event, float v)
    {

        int x = (int) event.getX();
//        int y = (int)event.getY();
        if (v < getmMinimumVelocity())
        {
//            Log.e("速度",v+"");
            //初始位置和触摸位置不同时
            if (dragPosition != -1 && dragPosition != temChangPosition)
            {
//        		如果在头部或尾部区域，不进行操作
                if (ifCanMove(dragPosition))
                {
                    //如果被拖动的是文件夹或者不在item内部区域，交换位置
                    if ((getAdapter().getItem(temChangPosition).isFolder()) || (!isInCenter(dragPosition, event)))
                    {
//             		   Log.e("MenuView", "拖动中,交换位置，从" + temChangPosition + "到" + dragPosition);
                        onExchange();
                    }
//                    else
//                    {
//                        Log.e("MenuView", "在文件夹中dragPosition = "+dragPosition);
//                    }
                }
            }
            countPageChange(x);
        }
//        else
//        {
//            Log.e("MenuView", "speed too fast v= "+v);
//        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b)
    {
        super.onLayout(changed, l, t, r, b);

    }

    @Override
    public void onDragFinished(MotionEvent event)
    {
//        int x = (int) event.getX();
//        int y = (int) event.getY();
        //可以合并
        if (getAdapter().ifCanMerge(temChangPosition, dragPosition))
        {
            //在目标位置中心区域
            if (dragPosition != -1 && temChangPosition != dragPosition && isInCenter(dragPosition, event))
            {
                onMerge(temChangPosition, dragPosition);
            }
        }
//        getChildAt(temChangPosition).setVisibility(View.VISIBLE);
//        setAnimationEnd();
//        showDropAnimation(x, y);
    }

    @Override
    public void onExchange()
    {
        mAdapter.exchangeItem(temChangPosition, dragPosition);
        temChangPosition = dragPosition;
//        movePostionAnimation(temChangPosition, dragPosition);
        getChildAt(dragPosition).setVisibility(View.INVISIBLE);

    }

    @Override
    public boolean ifCanMove(int position)
    {
        return position >= getStableHeaderCount() && !(position == getChildCount() - 1 && isLastItemStable());

    }

    @Override
    public boolean ifCanDelete(int position)
    {
        if (position < getStableHeaderCount())
        {
            return false;
        }
        return !(position == getChildCount() - 1 && isLastItemStable()) && getAdapter().ifCanDelete(position);

    }

    protected void onActionFolderClosed(FavoritesItem dragOutItem, MotionEvent event)
    {
        int x = (int) event.getRawX();
        int y = (int) event.getRawY();
        removeFolder();
        if (temChangPosition != -1)
        {
            getChildAt(temChangPosition).setVisibility(View.VISIBLE);
        } else
        {
            int[] locations = new int[2];
            this.getLocationOnScreen(locations);
            x = x - locations[0];
            y = y - locations[1];
            dragPosition = pointToPosition(x, y);
//            Log.e("dragPosition = ",dragPosition+"");
            if (dragPosition != -1 && isInCenter(dragPosition, event) && ifCanMove(dragPosition))
            {
//                Log.e("ScrollLayout", "拖动结束，合并到文件夹 position = "+dragOutItem);
                getAdapter().addItemToFolder(dragPosition, dragOutItem, defaultFolderName);
            } else
            {
//                Log.e("ScrollLayout", "拖动结束，添加按钮到最后");
                int position = getChildCount();
                if (isLastItemStable())
                {
                    position--;
                }
                getAdapter().addItem(position, dragOutItem);

                computePageCountChange(true);

                if (getmCurScreen() < getTotalPage() - 1)
                {
                    snapToScreen(getTotalPage() - 1);
                }
            }
        }
        dragPosition = temChangPosition = -1;
    }

    /**
     * @param event       触摸点位置
     * @param dragOutItem 被拖出的按钮
     */
    protected void dragOnChild(FavoritesItem dragOutItem, MotionEvent event)
    {

        if (!mScroller.isFinished() || mLayoutTransition.isRunning())
        {
            return;
        }

        int x = (int) event.getRawX();
        int y = (int) event.getRawY();
        int[] locations = new int[2];
        this.getLocationOnScreen(locations);
        x = x - locations[0];
        y = y - locations[1];
        dragPosition = pointToPosition(x, y);
//        Log.e("dragOnChild", "mCurScreen" + getmCurScreen() + "  dragPosition = " + dragPosition + "  temChangPosition = " + temChangPosition);
        if (dragPosition != -1)
        {
            if (temChangPosition != -1)
            {
                if (isLastItemStable() && dragPosition == getItemCount() - 1)
                {
//                    Log.e("dragOnChild", "在最后区域不交换");
                } else
                {
                    if (temChangPosition != dragPosition && ifCanMove(dragPosition))
                    {
                        if (isInCenter(dragPosition, event))
                        {
//                            Log.e("dragOnChild", "删除位置是temChangPosition 的按钮");
                            onDelete(temChangPosition);
                            temChangPosition = -1;
                        } else
                        {
//                            Log.e("dragOnChild","交换位置 "+temChangPosition +""+dragPosition);
                            onExchange();
                        }
                    } else
                    {
//                        Log.e("dragOnChild","位置没变");
                    }
                }
            } else
            {
                if (isLastItemStable() && dragPosition == getItemCount() - 1)
                {
//                    Log.e("dragOnChild","添加按钮到位置 "+dragPosition);
                    getAdapter().addItem(dragPosition, dragOutItem);
                    temChangPosition = dragPosition;
                    getChildAt(temChangPosition).setVisibility(View.INVISIBLE);
                    computePageCountChange(true);
                } else
                {
                    if (!isInCenter(dragPosition, event) && ifCanMove(dragPosition))
                    {
//                        Log.e("dragOnChild","添加按钮到位置 "+dragPosition);
                        getAdapter().addItem(dragPosition, dragOutItem);
                        temChangPosition = dragPosition;
                        getChildAt(temChangPosition).setVisibility(View.INVISIBLE);
                        computePageCountChange(true);
                    } else
                    {
//                        Log.e("dragOnChild","在文件夹中或者"+dragPosition);
                    }
                }
            }
        } else if (temChangPosition != -1)
        {
//            Log.e("dragOnChild","移除View区域,删除位置是"+temChangPosition + " 的按钮");
            onDelete(temChangPosition);
            temChangPosition = -1;
        }
        countPageChange(x);
    }

    /**
     * 显示文件夹
     */
    public void showFolder(int position)
    {
        if (getAdapter().isEditting())
        {
            stopShake();
            isStopShake = true;
        }
        FavoritesItem info = getAdapter().getItem(position);
        folderView = LayoutInflater.from(getContext()).inflate(R.layout.folder_layout, this, false);
        if (folderDialogBackground != null)
        {
            folderView.setBackground(folderDialogBackground);
        }
        LinearLayout linearLayout = (LinearLayout) folderView.findViewById(R.id.folder_container);

        if (folderViewBackground != null)
        {
            linearLayout.setBackground(folderViewBackground);
        }
        EditText editText = (EditText) folderView.findViewById(R.id.editText1);
        if (editTextBackground != null)
        {
            editText.setBackground(editTextBackground);
        }
        if (folder_editText_textColor != 0)
        {
            editText.setTextColor(folder_editText_textColor);
        }
        editText.setTextSize(TypedValue.COMPLEX_UNIT_PX, folder_editText_textSize);
        editText.setText(info.getActionName());

        FolderView layout = (FolderView) folderView.findViewById(R.id.container);

        layout.setParentLayout(this);
        layout.setColCount(folderColCount);
        layout.setRowCount(folderRowCount);
        layout.setFolderPosition(position);
        layout.setAdapter(getAdapter());
        layout.setOnItemClickListener(getOnItemClickListener());
        folderView.setOnClickListener(new OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                removeFolder();
            }
        });
        getWindowManager().addView(folderView, getWindowParams());

        getAdapter().initFolderEditingMode();

    }

    public void stopShake()
    {
        int count = getChildCount();
        for (int i = 0; i < count; i++)
        {
            View child = getChildAt(i);
            ImageView imageView = (ImageView) child.findViewById(R.id.delete_button_id);
            FrameLayout layout = (FrameLayout) child.findViewById(R.id.shake_zone_id);
            ;
            imageView.clearAnimation();
            layout.clearAnimation();
        }
    }

    public void startShake()
    {
        int count = getChildCount();
        for (int i = 0; i < count; i++)
        {
            View child = getChildAt(i);
            ImageView imageView = (ImageView) child.findViewById(R.id.delete_button_id);
            FrameLayout layout = (FrameLayout) child.findViewById(R.id.shake_zone_id);
            Animation shake;
            if (ifCanMove(i))
            {
                if (imageView.getVisibility() == View.VISIBLE)
                {
                    shake = AnimationUtils.loadAnimation(getContext(), R.anim.shake);
                    imageView.startAnimation(shake);
                }
                shake = AnimationUtils.loadAnimation(getContext(), R.anim.shake_rotate);
                layout.startAnimation(shake);
            }
        }
    }

    /**
     * 隐藏View
     */
    public void hideFolder()
    {
//        Log.e("ScrollLayout", "隐藏文件夹");
        if (folderView != null)
        {
            WindowManager.LayoutParams params = getWindowParams();
            params.alpha = 0;
            getWindowManager().updateViewLayout(folderView, params);
        }

        if (getAdapter().isEditting())
        {
            if (isStopShake == true)
            {
                startShake();
                isStopShake = false;
            }
        }
    }

    private boolean isStopShake = false;

    /**
     * 清除View
     */
    public void removeFolder()
    {
//    	Log.e("ScrollLayout", "是否在编辑状态"+getAdapter().isEditting());
        if (getAdapter().isEditting())
        {
            if (isStopShake == true)
            {
                startShake();
                isStopShake = false;
            }
        }

        if (folderView != null)
        {

            EditText editText = (EditText) folderView.findViewById(R.id.editText1);
            String name = editText.getText().toString();
            if (TextUtils.isEmpty(name))
            {
                name = defaultFolderName;
            }
            getAdapter().changeFolderName(name);
            getWindowManager().removeView(folderView);
            folderView = null;
            getAdapter().removeFolder();
        }
    }


    @Override
    public boolean dispatchKeyEvent(KeyEvent event)
    {

        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && getAdapter().isEditting())
        {
            getAdapter().setEditing(false);
            return true;
        }

        return super.dispatchKeyEvent(event);
    }

    public boolean isShowingFolder()
    {
        return folderView != null;
    }


    public void setAdapter(SpringboardAdapter mAdapter)
    {
        mAdapter.setSpringboardView(this);
        super.setAdapter(mAdapter);
    }

    @Override
    public void onDelete(int position)
    {
        getAdapter().deleteItem(position);
        computePageCountChange(true);
    }
}