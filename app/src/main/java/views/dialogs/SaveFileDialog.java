package views.dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.yossibarel.drummap.R;

import org.json.JSONException;

import Utils.FileManager;
import saves.MainSaver;

/**
 * Created by yossibarel on 12/06/16.
 */
public class SaveFileDialog {


    public static void show(final Context context, final FileManager.TypeFile typeFile, final MainSaver.SaveListener listener) {
        LayoutInflater li = LayoutInflater.from(context);
        View promptsView = li.inflate(R.layout.dialog_save_file, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);

        final EditText userInput = (EditText) promptsView
                .findViewById(R.id.editTextDialogUserInput);

        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                switch (typeFile) {
                                    case CHANNEL:
                                        if (TextUtils.isEmpty(userInput.getText()))
                                            Toast.makeText(context, "please insert file name", Toast.LENGTH_LONG).show();
                                        else if (FileManager.isFileNameExist(userInput.getText().toString(), typeFile)) {
                                            Toast.makeText(context, "the file name already exist", Toast.LENGTH_LONG).show();
                                        } else
                                            try {
                                                FileManager.saveFile(userInput.getText().toString(), typeFile, listener);
                                            } catch (JSONException e) {
                                                listener.onFailed(e.getMessage());
                                            }
                                        break;
                                }

                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }
}
