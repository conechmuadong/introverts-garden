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
import ie.app.models.Phase;


public class PhaseListAdapter extends ArrayAdapter<Phase> {
    private Context context;
    public List<Phase> phases;

    public PhaseListAdapter(Context context, List<Phase> phases) {
        super(context, R.layout.fragment_customized, phases);
        Log.v("ADAPTER", "Constructor: " + phases.size());
        for(Phase phase : phases) Log.v("ADAPTER","Constructor: " +  phase.toString());
        this.context = context;
        this.phases = phases;
    }

    @Override
    public int getCount() {
        return phases.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            // Nếu convertView chưa được sử dụng trước đó, tạo mới view mới
            convertView = LayoutInflater.from(context).inflate(R.layout.phase, parent, false);
        }

        // Lấy phần tử tại vị trí position
        final Phase phase = phases.get(position);

        Log.v("ADAPTER", phase.getName());

        // Update các thành phần trong view cho phần tử hiện tại
        TextView stageName = convertView.findViewById(R.id.stageName);
        stageName.setText(phase.getName());

        TextView stageEditHumid = convertView.findViewById(R.id.stageEditHumid);
        stageEditHumid.setText("" + phase.threshHold);

        TextView stageStartDate = convertView.findViewById(R.id.stageStartDate);
        stageStartDate.setText(phase.startTime);

        TextView stageEndDate = convertView.findViewById(R.id.stageEndDate);
        stageEndDate.setText(phase.endTime);

        return convertView;
    }

}