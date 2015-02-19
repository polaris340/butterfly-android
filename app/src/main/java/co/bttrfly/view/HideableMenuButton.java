package co.bttrfly.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageButton;

/**
 * Created by jiho on 2/6/15.
 */
public abstract class HideableMenuButton extends ImageButton implements Hideable {

    public HideableMenuButton(Context context) {
        super(context);
    }

    public HideableMenuButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HideableMenuButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

}
