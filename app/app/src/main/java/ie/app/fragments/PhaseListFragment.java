package ie.app.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;

import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import ie.app.R;
import ie.app.adapter.FieldListAdapter;
import ie.app.adapter.PhaseListAdapter;
import ie.app.api.FirebaseAPI;
import ie.app.databinding.FragmentListFieldBinding;
import ie.app.databinding.FragmentPhaseListBinding;
import ie.app.models.CustomizedParameter;
import ie.app.models.Field;
import ie.app.models.OnFieldSelectedListener;
import ie.app.models.Phase;
import ie.app.models.User;

public class PhaseListFragment extends BaseFragment {

    ListView listView;
    private FragmentPhaseListBinding binding;
    private PhaseListAdapter adapter;
    private Button addPhaseBtn;
    private Button updateBtn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        update();
        binding = FragmentPhaseListBinding.inflate(inflater, container, false);
        listView = (ListView) binding.listPhase;
        addPhaseBtn = binding.addPhaseButton;
        updateBtn = binding.updatePhaseButton;

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        adapter = new PhaseListAdapter(getContext(), field.customizedParameter.getFieldCapacity());
        listView.setAdapter(adapter);

        addPhaseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(PhaseListFragment.this)
                        .navigate(R.id.action_listPhase_to_addNewPhaseFragment);
            }
        });
        LinearLayout backButton = binding.backButton;
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(PhaseListFragment.this)
                        .navigate(R.id.action_listPhase_to_CustomizedFragment);
            }
        });
        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < field.customizedParameter.getFieldCapacity().size(); i++) {
                    Phase temp = (Phase) listView.getItemAtPosition(i);
//                    FirebaseAPI.addPhase(String.valueOf(temp.threshHold),
//                            temp.startTime, temp.endTime,
//                            "user", field.getName(), i + 1);
                    Log.e(String.valueOf(i), String.valueOf(temp.threshHold) +
                            temp.startTime + temp.endTime);
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private class GetTask extends AsyncTask<String, Void, ArrayList<Phase>> {
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
        protected ArrayList<Phase> doInBackground(String... params) {
            try {
                Task<ArrayList<Phase>> task = FirebaseAPI.getPhases((String) params[0], (String) params[1]);
                field.customizedParameter.setFieldCapacity(Tasks.await(task));
                return field.customizedParameter.getFieldCapacity();
            } catch (Exception e) {
                Log.v("ASYNC", "ERROR : " + e);
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<Phase> result) {
            super.onPostExecute(result);
            field.customizedParameter.setFieldCapacity(result);
            if (dialog.isShowing())
                dialog.dismiss();
        }
    }

    @SuppressLint("StaticFieldLeak")
    private void update() {
        GetTask task = new GetTask(getContext());
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "/users", "/" + field.getName());
        new AsyncTask<Void, Void, ArrayList<Phase>>() {
            @Override
            protected ArrayList<Phase> doInBackground(Void... voids) {
                try {
                    return task.get();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(ArrayList<Phase> result) {
                field.customizedParameter.setFieldCapacity(result);
                adapter = new PhaseListAdapter(getContext(), field.customizedParameter.getFieldCapacity());
                listView.setAdapter(adapter);
            }
        }.execute();
    }
}