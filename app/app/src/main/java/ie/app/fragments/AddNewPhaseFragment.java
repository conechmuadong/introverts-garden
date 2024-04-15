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

        doneBtn = binding.doneButton;
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.humidEditText.getText().toString().equals("") ||
                        binding.startDatEditText.getText().toString().equals("") ||
                        binding.endDatEditText.getText().toString().equals("")) {
                    Toast.makeText(getContext(), "Không được để trống", Toast.LENGTH_SHORT).show();
                } else {
                    Integer num = field.customizedParameter.getFieldCapacity().size() + 1;
                    Phase x = new Phase("phase" + num,
                            Float.parseFloat(binding.humidEditText.getText().toString()),
                            binding.startDatEditText.getText().toString(),
                            binding.endDatEditText.getText().toString());
                    field.customizedParameter.getFieldCapacity().add(x);
                    FirebaseAPI.addPhase(binding.humidEditText.getText().toString(),
                            binding.startDatEditText.getText().toString(),
                            binding.endDatEditText.getText().toString(), "user", field.getName(), num);
                    NavHostFragment.findNavController(AddNewPhaseFragment.this)
                            .navigateUp();
                }
            }
        });
    }
}