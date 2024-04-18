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

public class FieldListHomeAdapter extends ArrayAdapter<Field> {
    Context context;
    List<Field> fields;
    OnFieldSelectedListener listener;
    public FieldListHomeAdapter(Context context, List<Field> fields, OnFieldSelectedListener listener) {
        super(context, 0, fields);
        Log.v("Adapter", "Constructor: " + fields.size());
        this.context = context;
        this.fields = fields;
        this.listener = listener;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.garden, parent, false);
        }

        final Field field = fields.get(position);

        TextView fieldName = (TextView) convertView.findViewById(R.id.field_name);
        fieldName.setText(field.getName());

        ImageView garden = (ImageView) convertView.findViewById(R.id.garden_icon);
        garden.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onFieldSelected(field, "status");
            }
        });
        return convertView;
    }

    public void setOnFieldSelectedListener(OnFieldSelectedListener listener) {
        this.listener = listener;
    }
}
