package co.bttrfly.view;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import co.bttrfly.R;
import co.bttrfly.auth.Auth;
import co.bttrfly.settings.SettingsActivity;
import co.bttrfly.util.DialogUtil;

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
                Intent intent;
                switch (position) {
                    case 0:
                        intent = new Intent(getContext(), SettingsActivity.class);
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
