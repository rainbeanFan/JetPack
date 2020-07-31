package cn.rainbean.basemodule.view;

import android.annotation.SuppressLint;
import android.content.res.TypedArray;
import android.graphics.Outline;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewOutlineProvider;

import cn.rainbean.basemodule.R;

public class ViewHelper {

    public static final int RADIUS_ALL = 0;
    public static final int RADIUS_LEFT = 1;
    public static final int RADIUS_TOP = 2;
    public static final int RADIUS_RIGHT = 3;
    public static final int RADIUS_BOTTOM = 4;


    public static void setViewOutline(View view, AttributeSet attributes,int defStyleAttr,int defStyleRes){

        @SuppressLint("Recycle") TypedArray typedArray = view.getContext()
                .obtainStyledAttributes(attributes, R.styleable.viewOutLineStrategy, defStyleAttr, defStyleRes);

        int radius = typedArray.getDimensionPixelSize(R.styleable.viewOutLineStrategy_clip_radius, 0);

        int hideSide = typedArray.getInt(R.styleable.viewOutLineStrategy_clip_side, 0);

        typedArray.recycle();
        setViewOutline(view,radius,hideSide);
    }


    public static void setViewOutline(View owner, final int radius, final int radiusSide){

        owner.setOutlineProvider(new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                int width = view.getWidth();
                int height = view.getHeight();

                if (width == 0 || height == 0) {
                    return;
                }

                if (radiusSide != RADIUS_ALL){
                    int left = 0;
                    int top = 0;
                    int right = width;
                    int bottom = height;

                    if (radiusSide == RADIUS_LEFT){
                        right+=radius;
                    }else if (radiusSide == RADIUS_TOP){
                        bottom+=radius;
                    }else if (radiusSide == RADIUS_RIGHT){
                        left -=radius;
                    }else if (radiusSide == RADIUS_BOTTOM){
                        top -= radius;
                    }

                    outline.setRoundRect(left,top,right,bottom,radius);
                    return;
                }

                int left = 0;
                int top = 0;
                int right = width;
                int bottom = height;
                if (radius<=0){
                    outline.setRect(left,top,right,bottom);
                }else {
                    outline.setRoundRect(left,top,right,bottom,radius);
                }
            }
        });

        owner.setClipToOutline(radius>0);
        owner.invalidate();

    }


}
