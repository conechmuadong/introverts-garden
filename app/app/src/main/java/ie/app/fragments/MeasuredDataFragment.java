package ie.app.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;

import java.util.concurrent.ExecutionException;

import ie.app.R;
import ie.app.api.FirebaseAPI;
import ie.app.databinding.FragmentMeasuredDataBinding;
import ie.app.models.CustomizedParameter;
import ie.app.models.Field;
import ie.app.models.IrrigationInformation;
import ie.app.models.MeasuredData;

public class MeasuredDataFragment extends BaseFragment {
    private FragmentMeasuredDataBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMeasuredDataBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        Bundle bundle = getArguments();
        Log.v("MeasuredDataFragment", "onCreateView ");
        if (bundle != null) {
            Log.v("MeasuredDataFragment", "bundle not null");
            String selectedFieldName = bundle.getString("selectedFieldName");
            getFieldByName(selectedFieldName);
        }

        updateUI();

        return view;
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.waterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(MeasuredDataFragment.this)
                        .navigate(R.id.action_MeasuredDataFragment_to_IrrigationFragment);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void getFieldByName(String name) {
        field.name = name;
        GetTask task = new GetTask(getContext());
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "/users", "/" + name);
        new AsyncTask<Void, Void, MeasuredData>() {
            @Override
            protected MeasuredData doInBackground(Void... voids) {
                try {
                    return task.get();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(MeasuredData measuredData) {
                Log.v("MeasuredDataFragment", "FieldByName " + name + " " + measuredData.toString());
                field.measuredData = measuredData;
                updateUI();
            }
        }.execute();
    }

    private void updateUI() {
        if(field != null) {
            String fieldname = field.name;
            Log.v("MeasuredDataFragment", fieldname);
            TextView fieldnameView = binding.fieldName;
            fieldnameView.setText(fieldname);
            fieldnameView.setLineSpacing(10f, 1f);

            String airHumidityText = "Độ ẩm không khí\n" + field.measuredData.air_humidity;
            TextView airHumidityView = binding.airHumidityView;
            airHumidityView.setText(airHumidityText);
            airHumidityView.setLineSpacing(10f, 1f);

            String radiationText = "Bức xạ\n" + field.measuredData.radiation;
            TextView radiationView = binding.radiationView;
            radiationView.setText(radiationText);
            radiationView.setLineSpacing(10f, 1f);

            String soilHumidity30Text = "Độ ẩm đất\n" + field.measuredData.soil_humidity;
            TextView soilHumidity30View = binding.soilHumidity;
            soilHumidity30View.setText(soilHumidity30Text);
            soilHumidity30View.setLineSpacing(10f, 1f);

            String temperatureText = "Nhiệt độ\n" + field.measuredData.temperature;
            TextView temperatureView = binding.temperatureView;
            temperatureView.setText(temperatureText);
            temperatureView.setLineSpacing(10f, 1f);
        }
    }

    //---------------------------ACT CLASS---------------------------

    private class GetTask extends AsyncTask<String, Void, MeasuredData> {
        protected ProgressDialog dialog;
        protected Context context;
        public GetTask(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            this.dialog = new ProgressDialog(context, 1);
            this.dialog.setMessage("Đang lấy dữ liệu");
            this.dialog.show();
        }

        @Override
        protected MeasuredData doInBackground(String... params) {
            try {
                Task<MeasuredData> task = FirebaseAPI.getMeasuredData((String) params[0], (String) params[1]);
                field.measuredData = Tasks.await(task);
                Task<IrrigationInformation> task1 = FirebaseAPI.getIrrigationInformation((String) params[0], (String) params[1]);
                field.irrigationInformation = Tasks.await(task1);
                Log.v("MeasuredDataFragment", "Got data: " + field.measuredData.toString()
                        + field.irrigationInformation.toString());
                return field.measuredData;
            } catch (Exception e) {
                Log.v("ASYNC", "ERROR : " + e);
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(MeasuredData result) {
            super.onPostExecute(result);
            field.measuredData = result;
            if (dialog.isShowing())
                dialog.dismiss();
        }
    }

}