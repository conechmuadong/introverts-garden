package ie.app.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.NonNull;

import java.util.List;

import ie.app.R;
import ie.app.models.Field;
import ie.app.models.OnFieldSelectedListener;


public class FieldListAdapter extends ArrayAdapter<Field> {
    private Context context;
    public List<Field> fields;
    private OnFieldSelectedListener listener;
    public void setOnFieldSelectedListener(OnFieldSelectedListener listener) {
        this.listener = listener;
    }
    public FieldListAdapter(Context context, List<Field> fields, OnFieldSelectedListener listener) {
        super(context, R.layout.fragment_list_field, fields);
        Log.v("Adapter", "Constructor: " + fields.size());
        this.context = context;
        this.fields = fields;
        this.listener = listener;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.field_name, parent, false);
        final Field field = fields.get(position);

        TextView fieldName = (TextView) view.findViewById(R.id.field_name);
        fieldName.setText(field.getName());
        LinearLayout viewButton = (LinearLayout) view.findViewById(R.id.view);
        LinearLayout adjustButton = (LinearLayout) view.findViewById(R.id.edit);
        LinearLayout deleteButton = (LinearLayout) view.findViewById(R.id.delete);
        viewButton.setVisibility(View.GONE);
        viewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onFieldSelected(field, "status");
            }
        });
        adjustButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onFieldSelected(field, "adjust");
            }
        });
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onFieldSelected(field, "delete");
            }
        });
        adjustButton.setVisibility(View.GONE);
        deleteButton.setVisibility(View.GONE);
        ImageView imageView = (ImageView) view.findViewById(R.id.menu_button);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (adjustButton.getVisibility() == View.VISIBLE) {
                    adjustButton.setVisibility(View.GONE);
                    deleteButton.setVisibility(View.GONE);
                    viewButton.setVisibility(View.GONE);
                }
                else {
                    adjustButton.setVisibility(View.VISIBLE);
                    deleteButton.setVisibility(View.VISIBLE);
                    viewButton.setVisibility(View.VISIBLE);
                }
            }
        });

        return view;
    }

}