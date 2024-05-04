package ie.app.fragments;

import static androidx.core.content.ContextCompat.getSystemService;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

import ie.app.R;
import ie.app.adapter.FieldListAdapter;
import ie.app.api.FirebaseAPI;
import ie.app.databinding.FragmentCustomizedBinding;
import ie.app.models.CustomizedParameter;
import ie.app.models.Field;
import ie.app.models.IrrigationInformation;
import ie.app.models.MeasuredData;
import ie.app.models.OnFieldSelectedListener;
import ie.app.models.Phase;

public class CustomizedFragment extends BaseFragment implements AdapterView.OnItemClickListener {

    private FragmentCustomizedBinding binding;
    private OnFieldSelectedListener listener;

    private ArrayList<Phase> phases = field.customizedParameter.fieldCapacity;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCustomizedBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        super.onCreateView();

        Bundle bundle = getArguments();
        Log.v("CustomizedFragment", "onCreateView ");
        if (bundle != null) {
            Log.v("CustomizedFragment", "bundle not null");
            String selectedFieldName = bundle.getString("selectedFieldName");
            getFieldByName(selectedFieldName);
        }

        updateUI();

        return view;
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LinearLayout backButton = binding.backButton;
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("uid", uid);
                NavHostFragment.findNavController(CustomizedFragment.this)
                        .navigate(R.id.action_CustomizedFragment_to_FieldlistFragment, bundle);
            }
        });
        binding.updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                field.customizedParameter.acreage = Float.parseFloat(binding.areaEditText.getText().toString());
                field.customizedParameter.numberOfHoles = Integer.parseInt(binding.numberHolesEditText.getText().toString());
                field.customizedParameter.distanceBetweenHole = Float.parseFloat(binding.distanceHoleEditText.getText().toString());
                field.customizedParameter.distanceBetweenRow = Float.parseFloat(binding.distanceRowEditText.getText().toString());
                field.customizedParameter.dripRate = Float.parseFloat(binding.dripRateEditText.getText().toString());
                field.customizedParameter.scaleRain = Float.parseFloat(binding.scaleRainEditText.getText().toString());
                field.customizedParameter.fertilizationLevel = Float.parseFloat("0.2");
                field.customizedParameter.fieldCapacity = phases;
                FirebaseAPI.changeCustomizedParameter("users/"+uid+"/fields", field.name, field.customizedParameter);
                updateUI();
                Toast.makeText(getContext(), "Your changes are updated!", Toast.LENGTH_SHORT).show();
            }
        });

        binding.next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("uid", uid);
                NavHostFragment.findNavController(CustomizedFragment.this)
                        .navigate(R.id.action_CustomizedFragment_to_listPhase, bundle);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @SuppressLint("StaticFieldLeak")
    private void getFieldByName(String name) {
        field.name = name;
        CustomizedFragment.GetTask task = new CustomizedFragment.GetTask(getContext());
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "/users", "/" + name);
        new AsyncTask<Void, Void, CustomizedParameter>() {
            @Override
            protected CustomizedParameter doInBackground(Void... voids) {
                try {
                    return task.get();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(CustomizedParameter customizedParameter) {
                Log.v("CustomizedFragment", "FieldByName " + name + " " + customizedParameter.toString());
                field.customizedParameter = customizedParameter;
                updateUI();
            }
        }.execute();
    }

    private void updateUI() {
        if (field == null) return;
        String fieldname = field.name;
        Log.v("MeasuredDataFragment", fieldname);
        TextView fieldnameView = binding.fieldName;
        fieldnameView.setText(fieldname);
        fieldnameView.setLineSpacing(10f, 1f);

        String areaText = "" + field.customizedParameter.acreage;
        TextView areaView = binding.areaEditText;
        areaView.setText(areaText);
        areaView.setLineSpacing(10f, 1f);

        String numberHoleText = "" + field.customizedParameter.numberOfHoles;
        TextView numberHoleView = binding.numberHolesEditText;
        numberHoleView.setText(numberHoleText);
        numberHoleView.setLineSpacing(10f, 1f);

        String distanceHoleText = "" + field.customizedParameter.distanceBetweenHole;
        TextView distanceHoleView = binding.distanceHoleEditText;
        distanceHoleView.setText(distanceHoleText);
        distanceHoleView.setLineSpacing(10f, 1f);

        String distanceRowText = "" + field.customizedParameter.distanceBetweenRow;
        TextView distanceRowView = binding.distanceRowEditText;
        distanceRowView.setText(distanceRowText);
        distanceRowView.setLineSpacing(10f, 1f);

        String dripRateText = "" + field.customizedParameter.dripRate;
        TextView dripRateView = binding.dripRateEditText;
        dripRateView.setText(dripRateText);
        dripRateView.setLineSpacing(10f, 1f);

        String scaleRainText = "" + field.customizedParameter.scaleRain;
        TextView scaleRainView = binding.scaleRainEditText;
        scaleRainView.setText(scaleRainText);
        scaleRainView.setLineSpacing(10f, 1f);

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Phase phase = (Phase) adapterView.getItemAtPosition(i);
    }


    //---------------------------ACT CLASS---------------------------

    private class GetTask extends AsyncTask<String, Void, CustomizedParameter> {
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
        protected CustomizedParameter doInBackground(String... params) {
            try {
                Task<CustomizedParameter> task = FirebaseAPI.getCustomizedParameter((String) params[0], (String) params[1]);
                field.customizedParameter = Tasks.await(task);
                Log.v("CustomizedFragment", "Got data: " + field.customizedParameter.toString());
                return field.customizedParameter;
            } catch (Exception e) {
                Log.v("ASYNC", "ERROR : " + e);
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(CustomizedParameter result) {
            super.onPostExecute(result);
            // Đăng ký OnFieldSelectedListener cho adapter
            field.customizedParameter = result;
            if (dialog.isShowing())
                dialog.dismiss();
        }
    }


}