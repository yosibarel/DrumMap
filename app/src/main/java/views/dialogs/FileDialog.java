package views.dialogs;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

import com.yossibarel.drummap.R;

import java.io.File;
import java.util.Stack;

import adapters.FilesAdapter;
import interfaces.DeleteFileListener;

public class FileDialog implements DeleteFileListener {

    @Override
    public void onDeleteFile(File file) {
        Explor(file.getParent());
    }


    public interface IOnFileSelected {
        void OnSelected(File file, int channelIndex);

        void OnLongPressItem(File file, int channelIndex, View viewSelected, DeleteFileListener listener);
    }

    private Context context;
    private ListView lvFile;
    FilesAdapter adapter;
    private Builder builder;
    private Button btnBack;
    File[] files;
    AlertDialog dialog;
    IOnFileSelected onFileSelected;

    public void SetOnFileSelected(IOnFileSelected i) {
        onFileSelected = i;
    }

    public FileDialog(Context context) {
        this.context = context;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialoglayout = inflater.inflate(R.layout.file_dialog, null);

        builder = new Builder(context);

        builder.setView(dialoglayout);
        lvFile = (ListView) dialoglayout.findViewById(R.id.lvFiles);
        btnBack = (Button) dialoglayout.findViewById(R.id.btnBack);
        Button btnCancle = (Button) dialoglayout.findViewById(R.id.btnCancle);
        btnCancle.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();

            }
        });
        btnBack.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (pathes.size() != 0) {
                    String path = pathes.pop();
                    Explor(path);
                }
            }
        });
        lvFile.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (files[position].isDirectory()) {
                    String path = files[position].getPath();
                    pathes.push(files[position].getParent());
                    Explor(path);
                } else {
                    if (onFileSelected != null)
                        onFileSelected.OnSelected(files[position], channelIndex);
                    dialog.dismiss();
                }
            }
        });
        lvFile.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                if (onFileSelected != null)
                    onFileSelected.OnLongPressItem(files[position], channelIndex, view, FileDialog.this);


                return true;
            }
        });
        dialog = builder.create();

    }

    Stack<String> pathes = new Stack<String>();

    private void Explor(String path) {
        File f = new File(path);
        if (!f.exists())
            f = new File(Environment.getExternalStorageDirectory().toString());
        adapter = new FilesAdapter(context, 0, files = f.listFiles());
        lvFile.setAdapter(adapter);
    }

    public void Show(String path, int index) {
        channelIndex = index;
        pathes.clear();

        Explor(path);
        //Explor(Environment.getExternalStorageDirectory() + "/A");
        dialog.show();
    }

    int channelIndex = 0;

    void Dissmis() {

    }
}
