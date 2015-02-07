package me.jiho.animatedtogglebutton;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;

/**
 * Created by user on 2/4/15.
 */
public class ListGridToggleButton extends AnimatedToggleButton {
    public ListGridToggleButton(Context context) {
        super(context);
    }

    public ListGridToggleButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ListGridToggleButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected void drawIcon(Canvas canvas) {
        int canvasWidth = canvas.getWidth();
        int canvasHeight = canvas.getHeight();
        int contentWidth = canvasWidth - getPaddingLeft() - getPaddingRight();
        int contentHeight = canvasHeight - getPaddingTop() - getPaddingBottom();

        // smaller square
        int iconSize = (contentWidth < contentHeight? contentWidth : contentHeight);
        int iconLeft = (canvasWidth - iconSize) / 2;
        int iconTop = (canvasHeight - iconSize) / 2;

        for (int i = 0 ; i < 9; i ++) {
            drawRect(canvas, i, iconLeft, iconTop, iconSize);
        }

    }


    /**
     *
     * @param canvas canvas from onDraw()
     * @param position 0 1 2
     *                 3 4 5
     *                 6 7 8
     */
    private void drawRect(Canvas canvas, int position, float x0, float y0, float iconSize) {
        if (position < 0 || position > 8) throw new IllegalArgumentException("Position must be in range 0 to 8");

        float iconLeft = x0;
        float iconTop = y0;

        float lineWidth = iconSize / 24; // line width is 2dp for 48dp icon
        float smallSquareSize = (iconSize - (lineWidth * 6)) / 3;


        float toX0 = x0 + (lineWidth/2);
        float toY0 = y0 + (lineWidth/2) + ((smallSquareSize + lineWidth) * position);


        // adjust top left position of small rectangle
        switch (position % 3) {
            case 0:
                x0 += (lineWidth/2);
                break;
            case 1:
                x0 += (lineWidth*1.5f) + smallSquareSize;
                break;
            case 2:
                x0 += (lineWidth*2.5f) + (smallSquareSize*2);
                break;
        }

        if (position < 3) {
            y0 += (lineWidth/2);
        } else if (position < 6) {
            y0 += (lineWidth*1.5f) + smallSquareSize;
        } else {
            y0 += (lineWidth*2.5f) + (smallSquareSize*2);
        }

        float[] points = {
                x0, y0,
                x0 + smallSquareSize, y0,
                x0 + smallSquareSize, y0 + smallSquareSize,
                x0, y0 + smallSquareSize
        };

        float[] toPoints = {
                toX0, toY0,
                toX0 + (iconSize - lineWidth/2), toY0,
                toX0 + (iconSize - lineWidth/2), toY0 + smallSquareSize,
                toX0, toY0 + smallSquareSize
        };


        for (int i = 0; i < 8; i++) {
            points[i] += ((toPoints[i]-points[i]) * animationProgress);

            if (i % 2 == 0) {
                if (points[i] >  iconLeft + iconSize - (lineWidth / 2)) {
                    points[i] = iconLeft + iconSize - (lineWidth / 2);
                }
            } else {
                if (points[i] >  iconTop + iconSize - (lineWidth / 2)) {
                    points[i] = iconTop + iconSize - (lineWidth / 2);
                }
            }

        }
        if (points[1] >= iconTop + iconSize - lineWidth) {
            return;
        }

        Paint newPaint;
        if (position < 3) {
            newPaint = paint;
        } else {
            newPaint = new Paint(paint);
            newPaint.setAlpha((int)(255 * (1-animationProgress)));
        }

        Path path = new Path();
        path.moveTo(points[0], points[1]);
        for (int i = 0; i < 4; i++) {
            int x = ((i+1)%4) * 2;
            path.lineTo(points[x], points[x+1]);
        }
        canvas.drawPath(path, newPaint);
    }

    @Override
    public void setRotateAngle(float newAngle) {
        throw new UnsupportedOperationException("Function 'setRotateAngle' not supported for this class.");
    }

}
