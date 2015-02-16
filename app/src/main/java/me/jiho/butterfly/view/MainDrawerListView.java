package me.jiho.butterfly.view;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import me.jiho.butterfly.R;
import me.jiho.butterfly.auth.Auth;
import me.jiho.butterfly.settings.SettingsActivity;
import me.jiho.butterfly.util.DialogUtil;

/**
 * Created by jiho on 2/13/15.
 */
public class MainDrawerListView extends ListView {
    public MainDrawerListView(Context context) {
        super(context);
        init();
    }

    public MainDrawerListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MainDrawerListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        String[] menuList = getContext().getResources().getStringArray(R.array.main_drawer_menu);
        setAdapter(new MainDrawerArrayAdapter(
                getContext(),
                R.layout.listitem_text_with_icon,
                R.id.text,
                menuList
        ));
        setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        Intent intent = new Intent(getContext(), SettingsActivity.class);
                        getContext().startActivity(intent);
                        break;
                    case 1:
                        new DialogUtil.ConfirmDialog(getContext()) {

                            @Override
                            protected void onPositiveButtonClicked() {
                                dismiss();
                                Auth.logout();
                            }
                        }.setTitle(R.string.app_name)
                                .setMessage(R.string.confirm_log_out)
                                .create()
                                .show();
                        break;
                }
            }
        });
    }

    private class MainDrawerArrayAdapter extends ArrayAdapter<String> {

        public MainDrawerArrayAdapter(Context context, int resource, int textViewResourceId, String[] objects) {
            super(context, resource, textViewResourceId, objects);
        }
    }
}
