package com.alextam.simpleslidingmenu;

import android.content.Context;
import android.graphics.Rect;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

/**
 * Created by AlexTam on 2015/5/4.
 */
public class SlidingMenu extends FrameLayout {
    private static final String TAG = "SlidingMenu";

    private ViewDragHelper mDragHelper;

    private int minValue = 20;  //dp
    //边缘可触临界值
    private int leftEdgeMinSize = minValue;
    //子view左侧(LEFT)值
    private int leftValue;

    private View childViewA;
//    private View childViewBG;

    private boolean slidingMenuOpenSate = false;


    //注意的几个点,
    //如果mDragHelper.settleCapturedViewAt(left, top);方法去移动View,必须使用invalidate()刷新View才有效果.


    public SlidingMenu(Context context)
    {
        this(context,null);
    }

    public SlidingMenu(Context context, AttributeSet attrs)
    {
        this(context,attrs,0);
    }


    public SlidingMenu(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init();
    }


    private void init()
    {
        //为了提高兼容性,new ViewDragHelper()这个创建方法是私有的,只能通过Create()这个工厂方法去创建对象
        mDragHelper = ViewDragHelper.create(this, 1.0f, new sCallBack());
        int eValue = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, minValue ,
                getResources().getDisplayMetrics());
        leftEdgeMinSize = eValue > minValue ? eValue : minValue;
    }

    @Override
    protected void onFinishInflate() {
        childViewA = findViewById(R.id.ly_main_a);
//        childViewBG = findViewById(R.id.content_frame);
    }

    /**
     * 拖曳监听接口,要使用ViewDragHelper,必须实现该接口类
     */
    private class sCallBack extends ViewDragHelper.Callback
    {
        //该方法必须实现
        @Override
        public boolean tryCaptureView(View child, int pointerId)
        {
            return childViewA == child;
        }

        @Override
        public void onViewDragStateChanged(int state)
        {
            if(state == ViewDragHelper.STATE_IDLE)
            {
                //IDLE
                if(childViewA.getLeft() >= 0)
                {
                    slidingMenuOpenSate = true;
                }
            }
            else if(state == ViewDragHelper.STATE_DRAGGING)
            {
                //Drag
            }
            else if(state == ViewDragHelper.STATE_SETTLING)
            {
                //Settle
            }
        }

        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy)
        {
            if(changedView != null)
            {
                float alp = (float)(1 + (float)Math.abs(left)/leftValue);
                if(left <= leftValue)
                {
                    changedView.setAlpha(0.0f);
                }
                else
                {
                    changedView.setAlpha(alp);
                }
            }
        }

        @Override
        public void onViewCaptured(View capturedChild, int activePointerId) {}

        //手势释放子view时会回调该方法
        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel)
        {
            if(xvel < leftValue/3)
            {
                closeMenu();
            }
            else if((xvel + leftValue/3) > 0)
            {
                openMenu();
            }
            else
            {
                if(releasedChild.getLeft() > (leftValue - leftValue/3))
                {
                    openMenu();
                }
                else
                {
                    closeMenu();
                }
            }
        }

        @Override
        public void onEdgeTouched(int edgeFlags, int pointerId) {}

        @Override
        public boolean onEdgeLock(int edgeFlags) {
            return false;
        }

        @Override
        public void onEdgeDragStarted(int edgeFlags, int pointerId) {}

        @Override
        public int getOrderedChildIndex(int index) {
            return index;
        }

        @Override
        public int getViewHorizontalDragRange(View child) {
            return 0;
        }

        @Override
        public int getViewVerticalDragRange(View child)
        {
            return leftValue;
        }

        //实现水平拖曳的重要方法,返回的值是实现子view被水平拖曳移动的值
        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx)
        {
            final int paddingLeft = getPaddingLeft();
            //限制子view的拖曳不超出父view的左右边缘
            //如果直接return left; 也是可以的.但子view的拖曳就可以滑出父view以外位置了
//            final int resultLeft = Math.min(Math.max(paddingLeft,left),
//                    getWidth() - getChildAt(1).getWidth());
//            return resultLeft;

            final int resultLeft = Math.max(leftValue , Math.min(left,0));
            return resultLeft;
        }

        //实现垂直拖曳的重要方法
        @Override
        public int clampViewPositionVertical(View child, int top, int dy)
        {
            final int paddingTop = getPaddingTop();
            final int resultTop = Math.min(Math.max(paddingTop,top),
                    getHeight() - childViewA.getHeight());

            return resultTop;
        }

    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev)
    {
        final int action = MotionEventCompat.getActionMasked(ev);
        if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP)
        {
            if(mDragHelper != null)
                //取消或手指放开,都应当cancel()
                mDragHelper.cancel();
            return false;
        }
        return mDragHelper.shouldInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev)
    {
        if(ev.getAction() == MotionEvent.ACTION_UP ||
                ev.getAction() == MotionEvent.ACTION_CANCEL )
        {
            if(childViewA != null)
            {
                if(slidingMenuOpenSate && ev.getX() > childViewA.getWidth())
                {
                    closeMenu();
                }
            }
        }

        if(mDragHelper != null)
        {
            mDragHelper.processTouchEvent(ev);
            return true;
        }
        return false;
    }

    @Override
    public void computeScroll()
    {
        if (mDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed,l,t,r,b);
        leftValue = leftEdgeMinSize - childViewA.getWidth();
    }

    protected void openMenu()
    {
        if(mDragHelper != null)
        {
            if(mDragHelper.smoothSlideViewTo(childViewA,0,0))
            {
                ViewCompat.postInvalidateOnAnimation(this);
                slidingMenuOpenSate = true;
            }
        }
    }

    protected void closeMenu()
    {
        if(mDragHelper != null)
        {
            if(mDragHelper.smoothSlideViewTo(childViewA,leftValue,0))
            {
                ViewCompat.postInvalidateOnAnimation(this);
                slidingMenuOpenSate = false;
            }
        }
    }

    //获取侧滑菜单栏展开状态
    public boolean getSlidingMenuOpenSate()
    {
        return slidingMenuOpenSate;
    }



}
