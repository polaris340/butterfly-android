package me.jiho.animatedtogglebutton;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.util.AttributeSet;

/**
 * Created by jiho on 2/4/15.
 */
public class ExpandToggleButton extends AnimatedToggleButton {

    public ExpandToggleButton(Context context) {
        super(context);
    }

    public ExpandToggleButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ExpandToggleButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void drawIcon(Canvas canvas) {
        int canvasWidth = canvas.getWidth();
        int canvasHeight = canvas.getHeight();
        int contentWidth = canvasWidth - getPaddingLeft() - getPaddingRight();
        int contentHeight = canvasHeight - getPaddingTop() - getPaddingBottom();

        // smaller square
        int iconSize = (contentWidth < contentHeight ? contentWidth : contentHeight);
        int iconLeft = (canvasWidth - iconSize) / 2;
        int iconTop = (canvasHeight - iconSize) / 2;

        drawChevron(canvas, true, iconLeft, iconTop, iconSize);
    }

    private void drawChevron(Canvas canvas, boolean isUpDirection, float x0, float y0, float iconSize) {
        float cx = x0 + (iconSize / 2);
        float cy = y0 + (iconSize / 2);
        float strokeWidth = iconSize / 7;
        float strokeLength = iconSize / 2.3f;
        float longWidth = strokeLength / (float)Math.sqrt(2);
        float shortWidth = strokeWidth / (float)Math.sqrt(2);

        float moveLength = (iconSize - longWidth - shortWidth);

        float[] points = {
                cx, y0 + (moveLength * animationProgress),
                cx + longWidth, y0 + longWidth + (moveLength * animationProgress),
                cx + longWidth - shortWidth, y0 + longWidth + shortWidth + (moveLength * animationProgress),
                cx - shortWidth, y0 + shortWidth + (moveLength * animationProgress)
        };

        Path path = new Path();
        path.moveTo(points[0], points[1]);
        for (int i = 0; i < 4; i++) {
            int x = ((i+1)%4)*2;
            int y = x+1;
            path.lineTo(points[x], points[y]);
        }
        canvas.drawPath(path, paint);


        // flip horizontal
        for (int i = 0; i < 8; i+=2) {
            points[i] = cx - (points[i] - cx);
        }
        path = new Path();
        path.moveTo(points[0], points[1]);
        for (int i = 0; i < 4; i++) {
            int x = ((i+1)%4)*2;
            int y = x+1;
            path.lineTo(points[x], points[y]);
        }
        canvas.drawPath(path, paint);


        // flip vertical
        for (int i = 1; i < 8; i+=2) {
            points[i] = cy - (points[i] - cy);
        }
        path = new Path();
        path.moveTo(points[0], points[1]);
        for (int i = 0; i < 4; i++) {
            int x = ((i+1)%4)*2;
            int y = x+1;
            path.lineTo(points[x], points[y]);
        }
        canvas.drawPath(path, paint);


        // flip horizontal
        for (int i = 0; i < 8; i+=2) {
            points[i] = cx - (points[i] - cx);
        }
        path = new Path();
        path.moveTo(points[0], points[1]);
        for (int i = 0; i < 4; i++) {
            int x = ((i+1)%4)*2;
            int y = x+1;
            path.lineTo(points[x], points[y]);
        }
        canvas.drawPath(path, paint);

    }
}
