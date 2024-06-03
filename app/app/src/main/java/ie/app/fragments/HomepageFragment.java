package ie.app.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;

import java.nio.BufferUnderflowException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import ie.app.R;
import ie.app.adapter.FieldListAdapter;
import ie.app.adapter.FieldListHomeAdapter;
import ie.app.api.FirebaseAPI;
import ie.app.databinding.FragmentHomepageBinding;
import ie.app.databinding.FragmentListFieldBinding;
import ie.app.models.Field;
import ie.app.models.OnFieldSelectedListener;
import ie.app.models.User;
import org.w3c.dom.Text;

public class HomepageFragment extends BaseFragment implements AdapterView.OnItemClickListener, OnFieldSelectedListener {

    ListView listView;
    private FragmentHomepageBinding binding;
    private FieldListHomeAdapter adapter;
    private OnFieldSelectedListener listener;
    private boolean exitPressedOnce;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
//        Bundle bundle = getArguments();
        super.onCreateView();
        exitPressedOnce = false;
        binding = FragmentHomepageBinding.inflate(inflater, container, false);
        TextView textView = binding.date;
        textView.setText(getCurrentDate());
        listView = (ListView) binding.gardenList;
        new GetAllTask(getContext()).execute("/users/"+uid+"/fields");
        return binding.getRoot();
    }

    private String getCurrentDate() {
        LocalDate currentTime = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            currentTime = LocalDate.now();
        }
        DateTimeFormatter formatter = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
           formatter = DateTimeFormatter.ofPattern("EEE, MMM dd, yyyy");
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return currentTime.format(formatter);
        }
        return "Mon, Jan 01, 2021";
    }

    @SuppressLint("NonConstantResourceId")
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        listener = new OnFieldSelectedListener() {
            @Override
            public void onFieldSelected(Field field, String type) {
                Log.v("onFieldSelected", field.getName() + " onViewCreated");
                Bundle bundle = new Bundle();
                bundle.putString("uid", uid);
                bundle.putString("selectedFieldName", field.getName());
                if(Objects.equals(type, "status")) {
                    NavHostFragment.findNavController(HomepageFragment.this)
                            .navigate(R.id.action_homepageFragment_to_MeasuredDataFragment, bundle);
                }
            }
        };

        TextView textView = binding.more;
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(HomepageFragment.this)
                        .navigate(R.id.action_homepageFragment_to_FieldlistFragment);
            }
        });
        binding.detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(HomepageFragment.this)
                        .navigate(R.id.action_homepageFragment_to_tipsFragment);
            }
        });
        BottomNavigationView bottomNavigationView = binding.bottomNavigation;
        bottomNavigationView.setSelectedItemId(R.id.home_button);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            if(item.getItemId()==R.id.home_button) {
                return true;
            }
            else if(item.getItemId()==R.id.garden_button) {
                Bundle bundle = new Bundle();
                bundle.putString("uid", uid);
                NavHostFragment.findNavController(HomepageFragment.this)
                        .navigate(R.id.action_homepageFragment_to_FieldlistFragment, bundle);
                return true;
            }
            else if(item.getItemId()==R.id.tips_button) {
                Bundle bundle = new Bundle();
                bundle.putString("uid", uid);
                NavHostFragment.findNavController(HomepageFragment.this)
                        .navigate(R.id.action_homepageFragment_to_tipsFragment, bundle);
                return true;
            }
            else if(item.getItemId()==R.id.setting_button) {
                Bundle bundle = new Bundle();
                bundle.putString("uid", uid);
                NavHostFragment.findNavController(HomepageFragment.this)
                        .navigate(R.id.action_homepageFragment_to_settingsFragment);
                return true;
            }
            return false;
        });

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (exitPressedOnce) {
                    getActivity().finish();
                }
                else {
                    Toast.makeText(getContext(), "Press back again to exit", Toast.LENGTH_SHORT).show();
                    exitPressedOnce = true;
                }
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(callback);
        binding.tip1.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Bundle bundle = new Bundle();
                        bundle.putString("uid", uid);
                        bundle.putString("Tip chosen", "Tip 1");
                        NavHostFragment.findNavController(HomepageFragment.this)
                                .navigate(R.id.action_homepageFragment_to_videoShowFragment, bundle);
                    }
                }
        );
        binding.tip2.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Bundle bundle = new Bundle();
                        bundle.putString("uid", uid);
                        bundle.putString("Tip chosen", "Tip 2");
                        NavHostFragment.findNavController(HomepageFragment.this)
                                .navigate(R.id.action_homepageFragment_to_videoShowFragment, bundle);
                    }
                }
        );
        binding.tip3.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Bundle bundle = new Bundle();
                        bundle.putString("uid", uid);
                        bundle.putString("Tip chosen", "Tip 3");
                        NavHostFragment.findNavController(HomepageFragment.this)
                                .navigate(R.id.action_homepageFragment_to_videoShowFragment, bundle);
                    }
                }
        );
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
        bundle.putString("selectedField", field.getName());
        Log.v("onFieldSelected", field.getName() + " in fieldlist Fragment");
        if(Objects.equals(type, "status")) {
            NavHostFragment.findNavController(HomepageFragment.this)
                    .navigate(R.id.action_homepageFragment_to_MeasuredDataFragment, bundle);
        }
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
            this.dialog.setMessage("Loading...");
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
            adapter = new FieldListHomeAdapter(context, user.getFields(), HomepageFragment.this);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(HomepageFragment.this);
            // Đăng ký OnFieldSelectedListener cho adapter
            adapter.setOnFieldSelectedListener(listener);
            if (dialog.isShowing())
                dialog.dismiss();
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            dialog.dismiss();
            Toast.makeText(getContext(), "An error occurred while getting data", Toast.LENGTH_SHORT).show();
            Log.e("AsyncTask", "An error occurred while getting data");
        }
    }
}
