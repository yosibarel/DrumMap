package adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.yossibarel.drummap.R;

import java.io.File;

public class FilesAdapter extends ArrayAdapter<File>
{

	private static class ViewHolder
	{
		TextView tvFileName;
		TextView tvType;
		TextView tvSize;
	}

	private LayoutInflater inflater;
	private File[] files;

	public FilesAdapter(Context context, int resource, File[] files)
	{
		super(context, R.layout.row_file, files);
		inflater = LayoutInflater.from(context);
		this.files = files;

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{

		ViewHolder viewHolder;

		if (convertView == null)
		{
			viewHolder = new ViewHolder();
			convertView = inflater.inflate(R.layout.row_file, parent, false);
			viewHolder.tvFileName = (TextView) convertView.findViewById(R.id.tvFileName);
			viewHolder.tvSize = (TextView) convertView.findViewById(R.id.tvSize);
			viewHolder.tvType = (TextView) convertView.findViewById(R.id.tvType);

			convertView.setTag(viewHolder);
		} else
			viewHolder = (ViewHolder) convertView.getTag();
		viewHolder.tvFileName.setText(files[position].getName());
		viewHolder.tvSize.setText(files[position].length() + "");
		viewHolder.tvType.setText(files[position].isDirectory() ? "Folder" : "File");

		return convertView;
	}

}
