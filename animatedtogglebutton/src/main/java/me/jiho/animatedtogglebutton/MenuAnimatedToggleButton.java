package me.jiho.animatedtogglebutton;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;

/**
 * Created by user on 2015. 2. 2..
 */
public class MenuAnimatedToggleButton extends AnimatedToggleButton {
    private static final int TOP    = 0b0001;
    private static final int LEFT   = 0b0010;
    private static final int RIGHT  = 0b0100;
    private static final int BOTTOM = 0b1000;


    private Paint paint;

    public MenuAnimatedToggleButton(Context context) {
        super(context);
        init();
    }

    public MenuAnimatedToggleButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MenuAnimatedToggleButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.FILL);
    }

    public void setColor(int color) {
        paint.setColor(color);
    }

    @Override
    protected void draw(Canvas canvas, float animationProgress) {
        int canvasWidth = canvas.getWidth();
        int canvasHeight = canvas.getHeight();
        int contentWidth = canvasWidth - getPaddingLeft() - getPaddingRight();
        int contentHeight = canvasHeight - getPaddingTop() - getPaddingBottom();

        // smaller square
        int iconSize = (contentWidth < contentHeight? contentWidth : contentHeight);
        int iconLeft = (canvasWidth - iconSize) / 2;
        int iconRight = (canvasWidth - iconLeft);
        int iconTop = (canvasHeight - iconSize) / 2;
        int iconBottom = (canvasHeight - iconTop);

        float[] targetPoints = {
                iconLeft, iconTop,
                iconRight, iconTop,
                iconRight, iconBottom,
                iconLeft, iconBottom,
                iconLeft, iconTop
        };

        drawRect(canvas, createRectPoints(TOP|LEFT, iconSize, iconLeft, iconTop));
        drawRect(canvas, createRectPoints(TOP|RIGHT, iconSize, iconLeft, iconTop));
        drawRect(canvas, createRectPoints(BOTTOM|LEFT, iconSize, iconLeft, iconTop));
        drawRect(canvas, createRectPoints(BOTTOM|RIGHT, iconSize, iconLeft, iconTop));


    }


    /**
     * @param canvas
     * @param points return value of createRect() function
     */
    private void drawRect(Canvas canvas, float[] points) {
        Path path = new Path();
        path.moveTo(points[0], points[1]);
        for (int i = 0; i < 4; i++) {
            int x = (i+1)%4*2;
            int y = x+1;
            path.lineTo(points[x], points[y]);
        }
        canvas.drawPath(path, paint);
    }

    /**
     *
     * @param position
     *          0 1
     *          3 2
     * @param iconSize width(or height - because icon is square) of actual drawing area
     * @return
     */
    private float[] createRectPoints(int position, float iconSize, float x0, float y0) {
        float barWidth = iconSize / 7;
        float rectWidth = barWidth * 3;
        float dShort = barWidth / (float)Math.sqrt(2d);
        float dLong = rectWidth - dShort;



        if ( (position&RIGHT) != 0) {
            x0 += (barWidth * 4);
        }
        if ( (position&BOTTOM) != 0) {
            y0 += (barWidth * 4);
        }

        float x1 = x0 + rectWidth;
        float y1 = y0 + rectWidth;

        float[] points;
        if ( (position == (TOP|RIGHT))
                || (position == (BOTTOM|LEFT)) ) {
            points = new float[] {
                    x0 + (dLong * animationProgress), y0,
                    x1, y0 + (dShort * animationProgress),
                    x1 - (dLong * animationProgress), y1,
                    x0, y1 - (dShort * animationProgress)
            };
        } else {
            points = new float[] {
                    x0 + (dShort * animationProgress), y0,
                    x1, y0 + (dLong * animationProgress),
                    x1 - (dShort * animationProgress), y1,
                    x0, y1 - (dLong * animationProgress)
            };
        }
        return points;

    }

}
