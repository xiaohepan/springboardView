package com.panxiaohe.springboard.library;

import android.animation.LayoutTransition;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Scroller;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by panxiaohe on 16/3/9.
 *
 * 这个类提供布局相关,各种监听的设置和基本事件的处理
 *
 * 具体的各种触摸事件处理请使用子类
 */
public abstract  class SpringboardView extends ViewGroup
{


    /**
     * callback at merge into Folder,delete item,add item,remove out Folder,exchange position and change folderName
     * 按钮添加,删除,改变位置,改变文件夹名字时回调
     * */
//    private OnItemDataChangeListener onItemDataChangeListener;
    /**
     * callback onPageCountChange() at totalPage count change
     * callback onPageScroll() at mCurScreen change
     * 在页面数量和当面页面发生变化时回调
     * */
    private OnPageChangedListener onPageChangedListener;

    /**
     * 总页面数量
     *  will count totalPage at setAdapter,merge into Folder,delete item,add item,remove out Folder
     */
    private int totalPage = 0;

    /**
     * 现在页面
     * */
    private int mCurScreen = 0;

    protected SpringboardAdapter mAdapter;

    /**
     * 保存正在动画着的点防止动画冲突
     */
    private ArrayList<Integer> animationMap = new ArrayList<Integer>();
//
//    //
//    private int halfBitmapWidth;
//    //
//    private int halfBitmapHeight;


    /**
     * 最后的触摸位置X
     */
    private float mLastMotionX;
    /**
     * 最后的触摸位置Y
     */
    private float mLastMotionY;

    /**
     * 触摸点距离View左边的距离
     */
    private int dragPointX;
    /**
     * 触摸点距离View顶部的距离
     */
    private int dragPointY;
    /**
     * view左边距离屏幕的距离
     */
    private int dragOffsetX;
    /**
     * view上边距离屏幕的距离
     */
    private int dragOffsetY;

    /**
     * 当前手指触摸的位置
     */
    protected int dragPosition = -1;

    /**
     * 拖动开始时和顺序交换之后被拖动View的位置
     */
    protected int temChangPosition = -1;

    private int startX = 0;

    private VelocityTracker mVelocityTracker;



    private MODE mode = MODE.FREE;

    enum MODE {
        //普通时,按钮被长按之后,进入这个模式,滚动模式
        FREE, DRAGGING, SCROLL
    }

    /**
     * 删除按钮
     * */
    private Drawable deleteIcon;

    /**
     * 几行
     */
    private int rowCount = 3;

    /**
     * 几列
     */
    private int colCount = 3;


    /**
     * 合并按钮,产生文件夹时的默认按钮
     * */
    protected String defaultFolderName;

    protected int dividerWidth;
    protected int dividerColor;

    private Scroller mScroller;
    private int mTouchSlop;
    private int mMinimumVelocity;
    private int mMaximumVelocity;

    private boolean isLastItemStable;

    private int stableHeaderCount = 0;

    protected int page_divider_width;
    protected int page_divider_color;

    public SpringboardView(Context context) {
        this(context, null);
//        Log.e("init", "1");
        ScrollView v;
    }

    public SpringboardView(Context context, AttributeSet attrs) {
        this(context, attrs, R.style.Springboard);
//        Log.e("init", "2 -->"+R.style.Springboard);
    }

    public SpringboardView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
//        Log.e("init", "3 -->" + defStyle);
        initSpringboardView(context,attrs,defStyle,0);
    }

    private Paint mPaint;
    private boolean isTransitionint = false;


//    public SpringboardView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes)
//    {
//        super(context, attrs, defStyleAttr, defStyleRes);
//        Log.e("init", "4 -->"+defStyleAttr);
//
//        initSpringboardView();
//    }

    private void initSpringboardView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        final TypedArray a = context.obtainStyledAttributes(
                attrs, R.styleable.Springboard, defStyleAttr, defStyleRes);
        deleteIcon = a.getDrawable(R.styleable.Springboard_delete_icon);
        rowCount = a.getInt(R.styleable.Springboard_row_count, 3);
        colCount = a.getInt(R.styleable.Springboard_column_count, 3);
        defaultFolderName = a.getString(R.styleable.Springboard_default_folder_name);
        dividerWidth = a.getDimensionPixelOffset(R.styleable.Springboard_divider_width, 0);
        dividerColor = a.getColor(R.styleable.Springboard_divider_color, 0);
        isLastItemStable = a.getBoolean(R.styleable.Springboard_is_last_item_stable, false);
        stableHeaderCount= a.getInt(R.styleable.Springboard_stable_header_count, 0);
        page_divider_width = a.getDimensionPixelOffset(R.styleable.Springboard_page_divider_width, 0);

        if(dividerWidth != 0 && dividerColor != 0)
        {
            setWillNotDraw(false);
        }
        a.recycle();
        setFocusable(true);
        setFocusableInTouchMode(true);
        this.mScroller = new Scroller(getContext());
        FingerFlowViewManager.getInstance().init(getContext());
        setOverScrollMode(OVER_SCROLL_ALWAYS);
        final ViewConfiguration configuration = ViewConfiguration.get(getContext());
        mTouchSlop = configuration.getScaledTouchSlop();
        mMinimumVelocity = 40;
        mMaximumVelocity = configuration.getScaledMaximumFlingVelocity();
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        setLayoutTransition(new LayoutTransition());
        LayoutTransition mLayoutTransition = new LayoutTransition();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        int childWidth;
        int childHeight;
        int measuredHeight;

        //父给出的宽度,不管什么模式用完
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);

        //可以使用的宽度
        int usedWidth = width - getPaddingLeft() - getPaddingRight() - (colCount + 1)
                * dividerWidth;
        childWidth = usedWidth / colCount;

        int height = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        //如果有准确高度,按准确高度计算.
        if(heightMode == MeasureSpec.EXACTLY)
        {
//            Log.e("高度","MeasureSpec.EXACTLY");
            int usedHeight = height - getPaddingTop() - getPaddingBottom() - (rowCount +1)
                    * dividerWidth;

            childHeight = usedHeight / rowCount;

            measuredHeight = height;
        }else
        {
//            如果没有准确高度,和宽度等长
//            Log.e("高度","MeasureSpec.atmost");
            childHeight = childWidth;
            //        计算出需要的高度
            measuredHeight = childHeight * rowCount + (rowCount +1) * dividerWidth + getPaddingTop() + getPaddingBottom();
        }
        if(measuredHeight > height)
        {
            measuredHeight = height;
        }

        int childWidthSpec = getChildMeasureSpec(
                MeasureSpec
                        .makeMeasureSpec(childWidth, MeasureSpec.EXACTLY),
                0, childWidth);

        int childHeightSpec = getChildMeasureSpec(
                MeasureSpec
                        .makeMeasureSpec(childWidth, MeasureSpec.EXACTLY),
                0, childHeight);

        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            child.measure(childWidthSpec, childHeightSpec);

        }
        setMeasuredDimension(width, measuredHeight);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b)
    {
        final int childCount = getChildCount();

        int  measuredWidth = getMeasuredWidth();

        for (int i = 0; i < childCount; i++)
        {
            final View childView = getChildAt(i);
            if (childView.getVisibility() != View.GONE)
            {

                int page = i / rowCount / colCount;

                int row = i / colCount % rowCount;

                int col = i % colCount;

                int left = getPaddingLeft() + page * (measuredWidth+page_divider_width) + col
                        * (dividerWidth + childView.getMeasuredWidth()) + dividerWidth;

                int top = getPaddingTop() + dividerWidth+row * (dividerWidth + childView.getMeasuredHeight());

                childView.layout(left, top, left + childView.getMeasuredWidth(),
                        top + childView.getMeasuredHeight());

            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        Log.e("onDraw", getTotalPage() + "");
        mPaint.setColor(dividerColor);
        int childHeight = getChildHeight();
        int childWidth = getChildWidth();
        int rowCount = getRowCount()+1;

        int pageCount = getTotalPage();

        for(int j = 0; j < pageCount;j++)
        {
            for(int i = 0;i <rowCount;i++)
            {
                int count = (childHeight+dividerWidth) * i;

                canvas.drawRect(j*(getWidth()+page_divider_width),count,(j+1)*getWidth()+ j*page_divider_width,count+dividerWidth ,mPaint);
            }

            int colCount = getColCount()+1;
            for(int i = 0;i < colCount;i++)
            {
                int count = (childWidth+dividerWidth) * i + j*(getWidth()+page_divider_width);
                canvas.drawRect(count,0,count+dividerWidth,getHeight() ,mPaint);
            }
        }
    }



    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev)
    {
//        Log.e("onInterceptTouchEvent", getActionName(ev));
        int action = ev.getAction();
        float x = ev.getX();
        float y = ev.getY();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                startX = (int) x;
                dragOffsetX = (int) (ev.getRawX() - x);
                dragOffsetY = (int) (ev.getRawY() - y);
                mLastMotionX = x;
                mLastMotionY = y;
                stopScroll();
                break;
            case MotionEvent.ACTION_MOVE:
//                 判断是否可以左右滑动
                if(mode == MODE.DRAGGING)
                {
                    return true;
                }else
                {
                    int deltaX = (int) (mLastMotionX - x);
                    if (ifCanScroll(deltaX))
                    {
                        getParent().requestDisallowInterceptTouchEvent(true);
                        mode = MODE.SCROLL;
//                        Log.e("SpringboardView", "开始滑动");
                        return true;
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
//                如果不是处于free，也要处理事件
                if(mode == MODE.DRAGGING) {
                    dragPosition = pointToPosition((int)x, (int)y);
//                    Log.e("SpringboardView", "结束拖动");
                    endDrag((int)x,(int)y);
                } else if(mode == MODE.SCROLL ){
//                     获得这次触摸事件的移动距离
//                    Log.e("SpringboardView", "手指离开，滑动文件夹到目标页面");
                	getParent().requestDisallowInterceptTouchEvent(false);
                    float distance = ev.getRawX() - startX;
                    endScroll(distance);

                }
                if (mode != MODE.FREE){
                    return  true;
                }
                break;
            case MotionEvent.ACTION_CANCEL:
//                取消事件也要处理
            	mode = MODE.FREE;
                return true;
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
//        Log.e("onTouchEvent", getActionName(event));
        int action = event.getAction();
        int x = (int) event.getX();
        int y = (int) event.getY();
        switch (action) {
            case MotionEvent.ACTION_MOVE:
                switch (mode) {
                    case DRAGGING:
                        int viewX = x - dragPointX + dragOffsetX;
                        int viewY = y - dragPointY + dragOffsetY;
                        onFling(viewX, viewY);

                        if (mVelocityTracker != null)
                        {
                            mVelocityTracker.addMovement(event);
                            mVelocityTracker.computeCurrentVelocity(1000,mMaximumVelocity);
                            //  判断速度如果速度大于highSpeed，不处理移动
                            dragPosition = pointToPosition(x, y);
                            if(mScroller.isFinished())
                            {
                                float v = calculateV(mVelocityTracker.getXVelocity(),mVelocityTracker.getYVelocity());
                                onBeingDragging(event, v);
                            }
                        }
                        break;
                    case SCROLL:
                        int deltaX = (int) (mLastMotionX - x);
                        mLastMotionX = x;
                        scrollBy(deltaX,0);
                        break;
                    case FREE:
                        int deltaX1 = (int) (mLastMotionX - x);
                        if (ifCanScroll(deltaX1))
                        {
//                            Log.e("SpringboardView", "开始滑动");
                            getParent().requestDisallowInterceptTouchEvent(true);
                            mode = MODE.SCROLL;
                            return true;
                        }
                        break;
                }
                break;
            case MotionEvent.ACTION_UP:
                if(mode == MODE.DRAGGING)
                {
//                    Log.e("SpringboardView", "结束拖动");
                    dragPosition = pointToPosition(x, y);
                    onDragFinished(event);
                    endDrag((int) x, (int) y);
                } else if(mode == MODE.SCROLL ){
//                     获得这次触摸事件的移动距离
//                    Log.e("SpringboardView", "手指离开，滑动文件夹到目标页面");
                    float distance = event.getRawX() - startX;
                    endScroll(distance);
                }else
                {
                    if(getAdapter().isEditting())
                    {
                        getAdapter().setEditing(false);
                    }
                }
                startX = 0;
                mode = MODE.FREE;
                break;
            case MotionEvent.ACTION_CANCEL:
                if (getAdapter().isEditting()) {
                    getAdapter().setEditing(false);
                }
                if(mode == MODE.DRAGGING) {
                    dragPosition = pointToPosition(x, y);
                    onDragFinished(event);
                    endDrag((int)x,(int)y);
//                    Log.e("SpringboardView", "结束拖动");
                } else if(mode == MODE.SCROLL ){
//                     获得这次触摸事件的移动距离
                    float distance = event.getRawX() - startX;
                    endScroll(distance);
//                    Log.e("SpringboardView", "手指离开，滑动文件夹到目标页面");
                }
                startX = 0;
                mode = MODE.FREE;
                break;
        }
        return true;
    }

    private float calculateV(float xVelocity, float yVelocity)
    {
        return (float)Math.sqrt(xVelocity * xVelocity + yVelocity * yVelocity);
    }

    /**
    * 拖动时处理
    * */
    public abstract void onBeingDragging(MotionEvent event,float v);

    public abstract void onDragFinished(MotionEvent event);

   public abstract void onExchange();

    /**
     * 拖动开始时的初始化
     */
    public void onStartFling(View itemView, int x, int y) {
        itemView.destroyDrawingCache();
        itemView.setDrawingCacheEnabled(true);
        Bitmap bm = Bitmap.createBitmap(itemView.getDrawingCache());
        itemView.setVisibility(View.INVISIBLE);
        dragPointX = x - itemView.getLeft() + getmCurScreen() * getMeasuredWidth();
        dragPointY = y - itemView.getTop();
        int viewX = x - dragPointX + dragOffsetX;
        int viewy = y - dragPointY + dragOffsetY;
        FingerFlowViewManager.getInstance().setUp(getContext(), bm, viewX, viewy);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
//        Log.e("SpringboardView", "onDetachedFromWindow");
        FingerFlowViewManager.onDestory();
    }

    /**
     * 开始拖动
     */
    private void startDrag(View itemView) {
        mode = MODE.DRAGGING;
        getParent().requestDisallowInterceptTouchEvent(true);
//        halfBitmapWidth = itemView.getWidth() / 2;
//        halfBitmapHeight = itemView.getHeight() / 2;
        temChangPosition = dragPosition = getChildIndex(itemView);
//        Log.e("初始拖动位置", "temChangPosition ＝ " + temChangPosition);
        initOrResetVelocityTracker();
    }

    public void endDrag(int x,int y){
        mode = MODE.FREE;
        if(temChangPosition!=-1)
        {
            getChildAt(temChangPosition).setVisibility(View.VISIBLE);
        }
//        showDropAnimation(x, y);
        getParent().requestDisallowInterceptTouchEvent(false);
        recycleVelocityTracker();
        dragPosition = temChangPosition = -1;
        FingerFlowViewManager.getInstance().remove();
    }

    private void recycleVelocityTracker() {
        if (mVelocityTracker != null) {
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }

    private void initOrResetVelocityTracker() {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        } else {
            mVelocityTracker.clear();
        }
    }

    /**
     * 更新拖动的坐标，让图标跟随手一起动
     *
     * @param x view左上角坐标X
     * @param y view左上角坐标Y
     */
    public void onFling(int x, int y) {
        FingerFlowViewManager.getInstance().updatePosition(x, y);
    }



    /**
    * 如果正在进行滑动动画，就停止动画
    * */
    protected void stopScroll()
    {
        if (!mScroller.isFinished()) {
            //	中止移动动画
            mScroller.abortAnimation();
        }

    }

    /**
     * 判断能否进行滑动
     */
    protected boolean ifCanScroll(int deltaX) {

        if(Math.abs(deltaX) > mTouchSlop)
        {
            if(deltaX < 0)
            {
                if(getScrollX() > 0)
                {
                    return true;
                }
            }else
            {
                if(getScrollX() < (getTotalPage() - 1) * getMeasuredWidth())
                {
                    return true;
                }
            }
        }
        return  false;
    }

    protected void countPageChange(int x)
    {
//        Log.e("countPageChange"," x = " +x );
        if (x > getWidth() - 40
                && mCurScreen < getTotalPage() - 1 && mScroller.isFinished()
                && x > startX+10) {
            snapToScreen(mCurScreen + 1);
        } else if (x  < 40
                && mCurScreen > 0 && mScroller.isFinished() && x < startX-10) {
            snapToScreen(mCurScreen - 1);
        }
    }


    public void endScroll(float distance){
        if (distance > getMeasuredWidth() / 6 && getmCurScreen() > 0) {
            snapToScreen(getmCurScreen() - 1);
        } else if (distance < -getMeasuredWidth() / 6
                && getmCurScreen() < getTotalPage() - 1) {
            snapToScreen(getmCurScreen() + 1);
        } else {
            final int screenWidth = getWidth();
            final int destScreen = (getScrollX() + screenWidth / 2) / screenWidth;
            if (destScreen >= 0 && destScreen < getTotalPage()) {
                snapToScreen(destScreen);
            }
        }
    }


    public void snapToScreen(int whichScreen)
    {
        // get the valid layout page
//        Log.e("snapToScreen","whichScreen" + whichScreen );
        whichScreen = Math.max(0, Math.min(whichScreen, getChildCount() - 1));
        if (getScrollX() != (whichScreen * getWidth()))
        {
            final int delta = whichScreen * (getWidth()+page_divider_width) - getScrollX();

            if (whichScreen!=mCurScreen && onPageChangedListener != null)
            {
                onPageChangedListener.onPageScroll(mCurScreen, whichScreen);
            }
            mScroller.startScroll(getScrollX(), 0, delta, 0, 800);
            mCurScreen = whichScreen;
            postInvalidate();// Redraw the layout
        }
    }



    @Override
    public void computeScroll()
    {
        if (mScroller.computeScrollOffset())
        {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            postInvalidate();
        }
    }

    /**
     *  判断是否在文件夹内
     * */
    public boolean isInCenter(int position, MotionEvent event)
    {

        FrameLayout child = (FrameLayout) getChildAt(position);
        View view = child.findViewById(R.id.shake_zone_id).findViewById(R.id.center_zone_id);

        int[] location = new int[2];
        // 获取控件在屏幕中的位置，返回的数组分别为控件左顶点的 x、y 的值
        view.getLocationOnScreen(location);

        float x = event.getRawX(); // 获取相对于屏幕左上角的 x 坐标值
        float y = event.getRawY();
        RectF rect  = new RectF(location[0], location[1], location[0] + view.getWidth(),
                location[1] + view.getHeight());

        return rect.contains(x,y);
    }



    protected void setEditingMode(int position,View child,boolean isEditing,boolean ifShake)
    {
        ImageView imageView = (ImageView)child.findViewById(R.id.delete_button_id);
        FrameLayout layout = (FrameLayout)child.findViewById(R.id.shake_zone_id);;
        if(isEditing)
        {
            if(ifCanDelete(position))
            {
                imageView.setVisibility(View.VISIBLE);
//                Log.e("setEditingMode","position  = canDelete" + position);
            }else
            {
//                Log.e("setEditingMode", "position  = cannotDelete" + position);
                imageView.clearAnimation();
                imageView.setVisibility(View.GONE);
            }

            if(ifCanMove(position)&&ifShake)
            {
                Animation shake;
                if(imageView.getVisibility() == View.VISIBLE)
                {
                    shake = AnimationUtils.loadAnimation(getContext(), R.anim.shake);
                    imageView.startAnimation(shake);
                }
                shake = AnimationUtils.loadAnimation(getContext(), R.anim.shake_rotate);
                layout.startAnimation(shake);
            }else
            {
                if(imageView.getVisibility() == View.VISIBLE)
                {
                    imageView.clearAnimation();
                }
                layout.clearAnimation();;
            }
        }else
        {
            imageView.setVisibility(View.GONE);
            imageView.clearAnimation();
            layout.clearAnimation();;
        }

    }

    public abstract  boolean ifCanMove(int position);

    public abstract boolean ifCanDelete(int position);

    /**
     * 获得View在父容器中的位置
     * */
    public int getChildIndex(View view) {
        if (view != null) {
            final int childCount =  ((ViewGroup)view.getParent())
                    .getChildCount();
            for (int i = 0; i < childCount; i++) {
                if (view == ((ViewGroup) view.getParent()).getChildAt(i)) {
                    return i;
                }
            }
        }
        return -1;
    }

    /**
     * @param x,y 触摸点所在的位置
     *            如果不在位置上返回－1，在的话返回位置
     */
    public int pointToPosition(int x, int y) {
        int locX = x + mCurScreen * getWidth();
        Rect frame = new Rect();
        final int count = getChildCount();
        for (int i = count - 1; i >= 0; i--) {
            final View child = getChildAt(i);
            child.getHitRect(frame);
            if (frame.contains(locX, y)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 当长按时开启编辑模式
     * 并开始滑动
     */
    protected boolean onItemLongClick(View v)
    {
        if (mode != MODE.DRAGGING)
        {
            int index = getChildIndex(v);
//            Log.e("SpringboardView", "onItemLongClick 长按 位置是 " + index);
            getAdapter().setEditing(true);
            if(ifCanMove(index))
            {
                startDrag(v);
                onStartFling(v, (int) mLastMotionX, (int) mLastMotionY);
            }
            return true;
        }
        return false;
    }

    public abstract void onItemClick(int position);

    /**
    * 计算有几页
    * */
    protected void computePageCountChange(boolean ifCallback)
    {
        int pages = (int) Math.ceil(getChildCount() * 1.0 / getPageItemCount());

        if(pages == 0)
        {
            pages++;
        }

        if (pages != totalPage)
        {
            if (this.onPageChangedListener != null && ifCallback)
            {
                onPageChangedListener.onPageCountChange(totalPage, pages);
            }
            totalPage = pages;
        }
    }

    protected void onMerge(int fromPosition, int toPosition)
    {
        getAdapter().mergeItem(fromPosition, toPosition, defaultFolderName);
        temChangPosition = -1;
        computePageCountChange(true);
    }

    public void configView(FrameLayout view)
    {
        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return onItemLongClick(v);
            }
        });

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                int position = indexOfChild(arg0);
                onItemClick(position);
            }
        });
        ImageView imageView = new ImageView(getContext());
        imageView.setImageDrawable(deleteIcon);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.TOP | Gravity.END;
        params.setMargins(0,4,4,0);
        imageView.setId(R.id.delete_button_id);
        view.addView(imageView, params);
        imageView.setVisibility(View.GONE);
        imageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                int index = getChildIndex((ViewGroup) v.getParent());
                delete(index);
            }
        });
    }


    public void setAdapter(SpringboardAdapter mAdapter)
    {
        this.mAdapter = mAdapter;
        refreshView();
        computePageCountChange(false);
    }

    /**
     * 刷新页面
     */
    private void refreshView()
    {
        removeAllViews();
        int count = getItemCount();

        for (int i = 0; i < count; i++)
        {
            FrameLayout view =  initItemView(i);
            configView(view);
            this.addView(view);
        }
    }

    public abstract  int getItemCount();

    public abstract  FrameLayout initItemView(int position);

    public  void delete(int position)
    {
        onDelete(position);
        if(totalPage<= mCurScreen)
        {
            snapToScreen(totalPage - 1);
        }
    }

    public abstract  void onDelete(int position);

    /**
     * 移动动画
     *
     * @param fromPosition 拖动开始的位置
     * @param toPosition   拖动到达的位置
     */
    protected void movePostionAnimation(int fromPosition, int toPosition)
    { // 0,2
        int moveNum = toPosition - fromPosition; //  2
//		不再同一个按钮内并且没有快速滑动冲突
        if (moveNum != 0 && !isMovingFastConflict(moveNum)) {
            int absMoveNum = Math.abs(moveNum);
            int j = moveNum / absMoveNum;
            for (int i = 0; i < absMoveNum; i++) {
                int holdPosition = fromPosition + j;   //1\2,
                View view = getChildAt(holdPosition);
                view.clearAnimation();
                view.startAnimation(animationPositionToPosition(fromPosition,
                            holdPosition));
                fromPosition = holdPosition;
            }
        }
    }

//    /*
//   * 当移动太快时，判断是否有位置正在进行动画
//   * **/
    protected boolean isMovingFastConflict(int moveNum) {
        int itemsMoveNum = Math.abs(moveNum);
        int j = moveNum / itemsMoveNum;
        int temp = dragPosition;
        for (int i = 0; i < itemsMoveNum; i++) {
            temp = temp + j;
            if (animationMap.contains(temp)) {
                return true;
            }
        }
        return false;
    }


//    public void setAnimationEnd(){
//        animationMap.clear();
//    }

    /**
     * 动画监听，作用是在动画开始和结束时，维护animationMap，以保证快速滑动时动画不会冲突
     */
    private class NotifyDataSetListener implements Animation.AnimationListener {

        private int movedPosition;

        public NotifyDataSetListener(int primaryPosition) {
            this.movedPosition = primaryPosition;
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            if (animationMap.contains(movedPosition)) {
                // remove from map when end
                animationMap.remove(Integer.valueOf(movedPosition));
            }
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
        }

        @Override
        public void onAnimationStart(Animation animation) {
            // put into map when start
            animationMap.add(movedPosition);
        }
    }
    /**
     * 根据位置生成动画
     * 该位置是交换后的位置，所以必须要调用adapter.exchange();
     */
    protected Animation animationPositionToPosition(int oldP, int newP) {
//		Log.e("movePostionAnimation","from position "+oldP+"to position"+newP);
        PointF oldPF = positionToPoint2(oldP);
        PointF newPF = positionToPoint2(newP);
        TranslateAnimation animation;
        // when moving forward across pages,the first item of the new page moves
        // backward
        if (newP > oldP && newP % getPageItemCount() == 0) {
            animation = new TranslateAnimation(getMeasuredWidth() - oldPF.x, 0,
                    25 - getMeasuredWidth(), 0);
            animation.setDuration(800);
        }
        // when moving backward across pages,the last item of the new page moves
        // forward
        else if (newP < oldP && oldP != 0 && oldP % getPageItemCount() == 0) {
            animation = new TranslateAnimation(newPF.x - getMeasuredWidth(), 0,
                    getMeasuredWidth() -25, 0);
            animation.setDuration(800);
        }
        // regular animation between two neighbor items
        else {
            animation = new TranslateAnimation(newPF.x - oldPF.x, 0, newPF.y
                    - oldPF.y, 0);
            animation.setDuration(500);
        }
        animation.setFillAfter(true);
        animation.setAnimationListener(new NotifyDataSetListener(oldP));
        return animation;
    }

//    /**
//     * 往下掉的动画
//     */
//    protected void showDropAnimation(int x, int y) {
//        ViewGroup moveView = (ViewGroup) getChildAt(temChangPosition);
//        TranslateAnimation animation = new TranslateAnimation(x
//                - halfBitmapWidth - moveView.getLeft(), 0, y - halfBitmapHeight
//                - moveView.getTop(), 0);
//        animation.setFillAfter(false);
//        animation.setDuration(300);
//        moveView.setAnimation(animation);
//        for (int i = 0; i < getChildCount(); i++) {
//            getChildAt(i).clearAnimation();
//        }
//    }

    public PointF positionToPoint2(int position)
    {
        PointF point = new PointF();

        int page = position / getColCount() / getRowCount();

        int row = position / getColCount() % getRowCount();

        int col = (position  - (page * getPageItemCount())) % getColCount();


        int left = getPaddingLeft() + (getWidth() + page_divider_width)*page + col * (dividerWidth + getChildWidth()) + dividerWidth;

        int top = getPaddingTop() + row * (dividerWidth + getChildHeight());

        point.x = left;

        point.y = top;

        return point;
    }

    public int getmMinimumVelocity()
    {
        return mMinimumVelocity;
    }

    public int getColCount() {
        return colCount;
    }

    public int getPageItemCount(){
        return rowCount * colCount;
    }

    public void setColCount(int colCount) {
        this.colCount = colCount;
    }

    public int getmCurScreen() {
        return mCurScreen;
    }

    public void setmCurScreen(int mCurScreen) {
        this.mCurScreen = mCurScreen;
    }

    public int getRowCount() {
        return rowCount;
    }

    public void setRowCount(int rowCount) {
        this.rowCount = rowCount;
    }

    public int getChildHeight(){
        return getChildAt(0).getWidth();
    }

    public int getChildWidth(){
        return getChildAt(0).getHeight();
    }

    public int getTotalPage() {
        return totalPage;
    }

    public SpringboardAdapter getAdapter()
    {
        return mAdapter;
    }

    public OnPageChangedListener getOnPageChangedListener()
    {
        return onPageChangedListener;
    }

    public void setOnPageChangedListener(OnPageChangedListener onPageChangedListener)
    {
        this.onPageChangedListener = onPageChangedListener;
    }

//    public OnItemDataChangeListener getOnItemDataChangeListener()
//    {
//        return onItemDataChangeListener;
//    }
//
//    public void setOnItemDataChangeListener(OnItemDataChangeListener onItemDataChangeListener)
//    {
//        this.onItemDataChangeListener = onItemDataChangeListener;
//    }

    public interface OnPageChangedListener
    {
        void onPageScroll(int from, int to);
        void onPageCountChange(int oldCount,int newCount);
    }

    public interface OnItemDataChangeListener
    {
        void onItemDataChange();
    }

    public interface OnItemClickListener
    {
        void onItemClick(FavoritesItem item);
    }

    private OnItemClickListener onItemClickListener;

    public OnItemClickListener getOnItemClickListener() {
        return onItemClickListener;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    private String getActionName(MotionEvent ev)
    {
        String action;
        switch(ev.getAction())
        {
            case 0:
                action = "ACTION_DOWN";
                break;
            case 1:
                action = "ACTION_UP";
                break;
            case 2:
                action = "ACTION_MOVE";
                break;
            case 3:
                action = "ACTION_CANCEL";
                break;
            default:
                action = "其它";
                break;
        }
        return action;
    }

    public int getStableHeaderCount() {
        return stableHeaderCount;
    }

    public void setStableHeaderCount(int stableHeaderCount) {
        this.stableHeaderCount = stableHeaderCount;
    }

    public boolean isLastItemStable() {
        return isLastItemStable;
    }

    public void setIsLastItemStable(boolean isLastItemStable) {
        this.isLastItemStable = isLastItemStable;
    }
//    protected  void onDataChange()
//    {
//        if(onItemDataChangeListener != null)
//        {
//            onItemDataChangeListener.onItemDataChange();
//        }
//    }
}
