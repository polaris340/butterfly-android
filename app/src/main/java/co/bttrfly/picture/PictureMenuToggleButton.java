package co.bttrfly.picture;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.CompoundButton;
import android.widget.ImageButton;

import java.util.HashMap;
import java.util.Iterator;

import co.jiho.animatedtogglebutton.MenuAnimatedToggleButton;
import co.bttrfly.view.HideableMenuButton;

/**
 * Created by jiho on 2/6/15.
 */
public class PictureMenuToggleButton {
    private View rootView;
    private MenuAnimatedToggleButton menuToggleButton;
    private HashMap<Integer, HideableMenuButton> buttons;

    public PictureMenuToggleButton() {
        buttons = new HashMap<>();
    }

    public void setRootView(View rootView) {
        this.rootView = rootView;
    }

    public View getRootView() {
        return rootView;
    }

    public void addButton(int key, HideableMenuButton button) {
        buttons.put(key, button);
    }

    public ImageButton getButton(int key) {
        return buttons.get(key);
    }

    public void setPictureId(long pictureId) {
        Iterator<HideableMenuButton> iterator = buttons.values().iterator();
        while (iterator.hasNext()) {
            iterator.next().setTag(pictureId);
        }
    }

    public MenuAnimatedToggleButton getMenuToggleButton() {
        return menuToggleButton;
    }

    public void setMenuToggleButton(MenuAnimatedToggleButton menuToggleButton) {
        this.menuToggleButton = menuToggleButton;
        menuToggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Iterator<HideableMenuButton> iterator = buttons.values().iterator();
                while (iterator.hasNext()) {
                    if (isChecked) {
                        iterator.next().show();
                    } else {
                        iterator.next().hide();
                    }
                }
            }
        });
    }

    public void setChecked(boolean checked) {
        menuToggleButton.setChecked(checked);
    }



    public static class Builder {
        private View rootView;
        private LayoutInflater mLayoutInflater;
        private PictureMenuToggleButton pictureMenuToggleButton;

        public Builder(Context context) {
            mLayoutInflater = LayoutInflater.from(context);
            pictureMenuToggleButton = new PictureMenuToggleButton();
        }


        public Builder setLayout(int layoutRes) {
            rootView = mLayoutInflater.inflate(layoutRes, null);
            pictureMenuToggleButton.setRootView(rootView);
            return this;
        }

        public Builder setLayoutParams(ViewGroup.LayoutParams layoutParams) {
            rootView.setLayoutParams(layoutParams);
            return this;
        }

        public Builder setMenuToggleButtonId(int menuToggleButtonId) {
            MenuAnimatedToggleButton menuButton
                    = (MenuAnimatedToggleButton) rootView.findViewById(menuToggleButtonId);
            menuButton.setInterpolator(new DecelerateInterpolator(), true);
            menuButton.setInterpolator(new AccelerateInterpolator(), false);
            menuButton.setRotateAngle(90f);

            pictureMenuToggleButton.setMenuToggleButton(menuButton);

            return this;
        }

        public Builder addButton(int buttonId) {
            pictureMenuToggleButton.addButton(buttonId, (HideableMenuButton) rootView.findViewById(buttonId));
            return this;
        }

        public PictureMenuToggleButton create() {
            return pictureMenuToggleButton;
        }
    }
}
