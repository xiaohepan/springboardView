package com.panxiaohe.springboard.library;

import android.content.Context;
import android.util.AttributeSet;

import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;


/**
 * Created by panxiaohe on 16/3/9.
 * 展示文件夹的View
 */
public class FolderView extends SpringboardView {


    private boolean isOutOfFolder = false;

    private MenuView parentLayout;

    private FavoritesItem dragOutItem;

    private int folderPosition;

    public FolderView(Context context) {
        super(context);
    }

    public FolderView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FolderView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * 判断是否手指移动到文件夹外边了
     */
    public boolean moveOutFolder(int x, int y) {
        return x < 0 || y < 0 || x > getWidth() || y > getHeight();
    }

    @Override
    public void onBeingDragging(MotionEvent event, float v)
    {

        int x = (int) event.getX();
        int y = (int) event.getY();

        if (isOutOfFolder == false && moveOutFolder(x, y))
        {
//            Log.e("FolderLayout", "拖动中，拖出文件夹区域了");
            getParentLayout().hideFolder();
            dragOutItem =  getAdapter().tempRemoveItem(folderPosition,temChangPosition);
            isOutOfFolder = true;
            dragPosition = -1;
        }

        if (v < getmMinimumVelocity()) {
            if (isOutOfFolder) {
                parentLayout.dragOnChild(dragOutItem, event);
            } else {
                if (dragPosition != -1 && temChangPosition != dragPosition) {
//                    Log.e("FolderLayout", "交换位置 " + temChangPosition + " -->" + dragPosition);
                    onExchange();
                }
                countPageChange(x);
            }

        }
    }
    @Override
    public void onDragFinished(MotionEvent event) {

        int x = (int) event.getRawX();
        int y = (int) event.getRawY();
        if (isOutOfFolder) {
            getParentLayout().onActionFolderClosed(dragOutItem,event);
        } else {
//            Log.e("FolderLayout", "拖动结束，安置在" + temChangPosition + "位置");
            getChildAt(temChangPosition).setVisibility(View.VISIBLE);
//            setAnimationEnd();
//            showDropAnimation(x, y);
        }
    }

    @Override
    public void onExchange()
    {
        getAdapter().exChangeSubItem(folderPosition, temChangPosition, dragPosition);
        getChildAt(dragPosition).setVisibility(View.INVISIBLE);
        temChangPosition = dragPosition;
    }

    @Override
    public boolean ifCanMove(int position)
    {
        return true;
    }

    @Override
    public boolean ifCanDelete(int position)
    {
        return getAdapter().ifCanDelete(folderPosition, position);
    }



    public MenuView getParentLayout() {
        return parentLayout;
    }

    public void setParentLayout(MenuView parentLayout) {
        this.parentLayout = parentLayout;
    }

    public int getFolderPosition()
    {
        return folderPosition;
    }

    public void setFolderPosition(int folderPosition)
    {
        this.folderPosition = folderPosition;
    }


    public void setAdapter(SpringboardAdapter mAdapter)
    {
        mAdapter.setFolderView(this);
        super.setAdapter(mAdapter);
    }

    @Override
    public void onItemClick(int position)
    {
        if(getAdapter().isEditting())
        {
            getAdapter().setEditing(false);
        }else{
            if(getOnItemClickListener()!=null)
            {
                getOnItemClickListener().onItemClick(getAdapter().getSubItem(folderPosition,position));
            }
        }


    }

    @Override
    public int getItemCount() {
        return getAdapter().getSubItemCount(folderPosition);
    }

    @Override
    public FrameLayout initItemView(int position) {
        FrameLayout view = mAdapter.initSubItemView(folderPosition, position, this);
        mAdapter.configSubItemView(folderPosition, position, view);
        return view;
    }

    @Override
    public void onDelete(int position)
    {
        getAdapter().deleteItem(folderPosition, position);
        computePageCountChange(false);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event)
    {
        if(event.getKeyCode() == KeyEvent.KEYCODE_BACK)
        {
            getParentLayout().removeFolder();
//            Log.e("dispatchKeyEvent",event.toString());
            return true;
        }
        return super.dispatchKeyEvent(event);
    }
}
