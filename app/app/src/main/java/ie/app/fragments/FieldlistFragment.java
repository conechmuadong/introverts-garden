package ie.app.fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;

import java.util.List;
import java.util.Objects;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import ie.app.R;
import ie.app.adapter.FieldListAdapter;
import ie.app.api.FirebaseAPI;
import ie.app.databinding.FragmentListFieldBinding;
import ie.app.models.Field;
import ie.app.models.OnFieldSelectedListener;
import ie.app.models.User;

public class FieldlistFragment extends BaseFragment implements AdapterView.OnItemClickListener, OnFieldSelectedListener {

    ListView listView;
    private FragmentListFieldBinding binding;
    private FieldListAdapter adapter;
    private OnFieldSelectedListener listener;
    private Button addBtn;
    private AlertDialog.Builder builder;
    private AlertDialog alertDialog;
    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentListFieldBinding.inflate(inflater, container, false);
        super.onCreateView();
        listView = (ListView) binding.listField;
        new GetAllTask(getContext()).execute("/users/"+uid+"/fields");

        addBtn = binding.addButton;

        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        BottomNavigationView navView = binding.bottomNavigation;
        navView.setSelectedItemId(R.id.garden_button);
        navView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.home_button) {
                Bundle bundle = new Bundle();
                bundle.putString("uid", uid);
                NavHostFragment.findNavController(FieldlistFragment.this)
                        .navigate(R.id.action_fieldlistFragment_to_homepageFragment);
                return true;
            } else if (item.getItemId() == R.id.garden_button) {
                return true;
            } else if (item.getItemId() == R.id.tips_button) {
                Bundle bundle = new Bundle();
                bundle.putString("uid", uid);
                NavHostFragment.findNavController(FieldlistFragment.this)
                        .navigate(R.id.action_fieldlistFragment_to_tipsFragment);
                return true;
            }
            else if (item.getItemId() == R.id.setting_button) {
                Bundle bundle = new Bundle();
                bundle.putString("uid", uid);
                NavHostFragment.findNavController(FieldlistFragment.this)
                        .navigate(R.id.action_FieldlistFragment_to_settingsFragment);
                return true;
            }
            return false;
        });

        listener = new OnFieldSelectedListener() {
            @Override
            public void onFieldSelected(Field field, String type) {
                Log.v("onFieldSelected", field.getName() + " onViewCreated");
                Bundle bundle = new Bundle();
                bundle.putString("selectedFieldName", field.getName());
                bundle.putString("uid", uid);
                if(Objects.equals(type, "status")) {
                    NavHostFragment.findNavController(FieldlistFragment.this)
                            .navigate(R.id.action_FieldlistFragment_to_MeasuredDataFragment, bundle);
                } else if(Objects.equals(type, "adjust")) {
                    NavHostFragment.findNavController(FieldlistFragment.this)
                            .navigate(R.id.action_FieldlistFragment_to_CustomizedFragment, bundle);
                } else if (Objects.equals(type, "delete")) {
                    confirmAlert(field);
                }
            }
        };
        Bundle bundle = getArguments();
        addBtn.setOnClickListener(v -> NavHostFragment.findNavController(FieldlistFragment.this)
                .navigate(R.id.action_FieldlistFragment_to_AddNewFieldFragment, bundle));

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                NavHostFragment.findNavController(FieldlistFragment.this)
                        .navigate(R.id.action_fieldlistFragment_to_homepageFragment);
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(callback);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Field field = (Field) adapterView.getItemAtPosition(i);
    }

    @Override
    public void onFieldSelected(Field field, String type) {
        Bundle bundle = new Bundle();
        Log.v("onFieldSelected", field.getName() + " in fieldlist Fragment");
        bundle.putString("uid", uid);
        bundle.putString("selectedFieldName", field.getName());
        if(Objects.equals(type, "status")) {
            NavHostFragment.findNavController(FieldlistFragment.this)
                    .navigate(R.id.action_FieldlistFragment_to_MeasuredDataFragment, bundle);
        } else if(Objects.equals(type, "adjust")) {
            NavHostFragment.findNavController(FieldlistFragment.this)
                    .navigate(R.id.action_FieldlistFragment_to_CustomizedFragment, bundle);
        } else if (Objects.equals(type, "delete")) {
            confirmAlert(field);
        }
    }

    private void confirmAlert(Field field) {
        builder = new AlertDialog.Builder(this.getContext());
        // Set Cancelable false for when the user clicks on the outside the Dialog Box then it will remain show
        builder.setCancelable(false);

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_delete, null);
        builder.setView(dialogView);
        alertDialog = builder.create();

        dialogView.findViewById(R.id.buttonOK).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAPI.deleteField("users/" + uid +"/fields/", field.getName());
                new GetAllTask(getContext()).execute("users/"+uid+"/fields");
                Toast.makeText(getActivity(), "Deleted successfully!", Toast.LENGTH_LONG).show();
                alertDialog.dismiss();
            }
        });
        dialogView.findViewById(R.id.buttonCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        alertDialog.show();
    }

    //---------------------------ACT CLASS---------------------------
    private class GetAllTask extends AsyncTask<String, Void, List<Field>> {
        protected ProgressDialog dialog;
        protected Context context;
        public GetAllTask(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            this.dialog = new ProgressDialog(context, 1);
            this.dialog.setMessage("Getting data...");
            this.dialog.show();
        }

        @Override
        protected List<Field> doInBackground(String... params) {
            try {
                Task<User> task = FirebaseAPI.getUser((String) params[0]);
                user = Tasks.await(task);
                Log.v("GetAllTask", user.toString());
                return user.getFields();
            } catch (Exception e) {
                System.out.println("ERROR : " + e);
                Log.v("ASYNC", "ERROR : " + e);
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<Field> result) {
            super.onPostExecute(result);
            adapter = new FieldListAdapter(context, user.getFields(), FieldlistFragment.this);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(FieldlistFragment.this);
            // Đăng ký OnFieldSelectedListener cho adapter
            adapter.setOnFieldSelectedListener(listener);
            if (dialog.isShowing())
                dialog.dismiss();
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            dialog.dismiss();
            Toast.makeText(getContext(), "An error occurred while getting data.", Toast.LENGTH_SHORT).show();
            Log.e("AsyncTask", "An error occurred while getting data");
        }
    }
}
