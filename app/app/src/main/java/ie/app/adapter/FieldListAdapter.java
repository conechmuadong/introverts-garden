package ie.app.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

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

    private void showPopupMenu(View view, final Field field) {
        PopupMenu popupMenu = new PopupMenu(this.getContext(), view);
        popupMenu.getMenuInflater().inflate(R.menu.menu_popup, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.adjust) {
                    // Gửi thông tin về cánh đồng được chọn tới Fragment chỉnh sửa thông tin (CustomizedFragment)
                    Log.v("onFieldSelected", "clicked adjust item");
                    if (listener != null) {
                        listener.onFieldSelected(field, "adjust");
                        Log.v("onFieldSelected", field.getName());
                    }
                } else if (item.getItemId() == R.id.status) {
                    // Gửi thông tin về cánh đồng được chọn tới Fragment chứa thông tin chi tiết về cánh đồng (MeasuredDataFragment)
                    Log.v("onFieldSelected", "clicked status item");
                    if (listener != null) {
                        listener.onFieldSelected(field, "status");
                        Log.v("onFieldSelected", field.getName());
                    }
                } else if (item.getItemId() == R.id.delete) {
                    // xử lý tùy chọn 3 cho selectedField
                    Log.v("onFieldSelected", "clicked delete item");
                    if (listener != null) {
                        listener.onFieldSelected(field, "delete");
                        Log.v("onFieldSelected", field.getName());
                    }
                }

                // Trả về true để cho biết sự kiện đã được xử lý
                return true;
            }
        });

        popupMenu.show();
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.field_name, parent, false);
        final Field field = fields.get(position);

        TextView fieldName = (TextView) view.findViewById(R.id.field_name);
        fieldName.setText(field.getName());

        ImageView imageView = (ImageView) view.findViewById(R.id.menu_button);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(view, field);
            }
        });

        return view;
    }

}