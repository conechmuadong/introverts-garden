package ie.app.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import java.util.Date;

import ie.app.R;
import ie.app.adapter.PhaseListAdapter;
import ie.app.api.FirebaseAPI;
import ie.app.databinding.FragmentAddNewFieldBinding;
import ie.app.databinding.FragmentAddNewPhaseBinding;
import ie.app.databinding.FragmentListFieldBinding;
import ie.app.models.Field;
import ie.app.models.OnFieldSelectedListener;
import ie.app.models.Phase;

public class AddNewPhaseFragment extends BaseFragment {

    private FragmentAddNewPhaseBinding binding;
    private Button doneBtn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAddNewPhaseBinding.inflate(inflater, container, false);
        super.onCreateView();
        doneBtn = binding.addPhaseButton;
        binding.stageName.setText("phase" + String.format("%d", field.customizedParameter.getFieldCapacity().size() + 1));
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.humidEditText.getText().toString().equals("") ||
                        binding.startDateEditText.getText().toString().equals("") ||
                        binding.endDateEditText.getText().toString().equals("")) {
                    Toast.makeText(getContext(), "Cannot be left blank!", Toast.LENGTH_SHORT).show();
                } else {
                    Integer num = field.customizedParameter.getFieldCapacity().size() + 1;
                    Phase x = new Phase("phase" + num,
                            Float.parseFloat(binding.humidEditText.getText().toString()),
                            binding.startDateEditText.getText().toString(),
                            binding.endDateEditText.getText().toString());
                    field.customizedParameter.getFieldCapacity().add(x);
                    FirebaseAPI.addPhase(binding.humidEditText.getText().toString(),
                            binding.startDateEditText.getText().toString(),
                            binding.endDateEditText.getText().toString(), "users/"+uid+"/fields", field.getName(), num);
                    Bundle bundle = new Bundle();
                    bundle.putString("uid", uid);
                    NavHostFragment.findNavController(AddNewPhaseFragment.this)
                            .navigate(R.id.action_addNewPhaseFragment_to_listPhase, bundle);
                }
            }
        });
    }
}