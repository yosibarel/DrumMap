package views.dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.yossibarel.drummap.R;

import effects.Effect;

/**
 * Created by yossibarel on 03/04/16.
 */
public class ListEffectDialog {

    public interface OnSelectEffectListener {
        void onSelectEffect(int indexFx);
    }

    public void show(final Context context, final OnSelectEffectListener listener) {

        AlertDialog.Builder builderSingle = new AlertDialog.Builder(context);

        builderSingle.setTitle("Effects");

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                context,
                R.layout.text_select, Effect.FX_NAMES);


        builderSingle.setNegativeButton(
                "cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        builderSingle.setAdapter(
                arrayAdapter,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listener.onSelectEffect(which);
                    }
                });
        AlertDialog dialog=builderSingle.create();
        ListView listView=dialog.getListView();
        listView.setDivider(new ColorDrawable(0x88888888)); // set color
        listView.setDividerHeight(1); // set height
        dialog.show();
    }
}
