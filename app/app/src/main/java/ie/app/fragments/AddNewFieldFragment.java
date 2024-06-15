package ie.app.fragments;

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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

import ie.app.R;
import ie.app.api.FirebaseAPI;
import ie.app.databinding.FragmentAddNewFieldBinding;
import ie.app.databinding.FragmentListFieldBinding;
import ie.app.models.Field;
import ie.app.models.OnFieldSelectedListener;

public class AddNewFieldFragment extends BaseFragment {

    private FragmentAddNewFieldBinding binding;
    private Button doneBtn;
    private EditText addNewFieldEditText;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAddNewFieldBinding.inflate(inflater, container, false);
        super.onCreateView();
        doneBtn = binding.doneBtn;
        addNewFieldEditText = binding.newFieldEditText;

        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("uid", uid);
                NavHostFragment.findNavController(AddNewFieldFragment.this)
                        .navigate(R.id.action_AddNewFieldFragment_to_FieldlistFragment, bundle);
            }
        });
        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (addNewFieldEditText.getText().toString().equals("")) {
                    Toast.makeText(getContext(), "Name cannot be left blank!", Toast.LENGTH_SHORT).show();
                } else {
                    Bundle bundle = new Bundle();
                    bundle.putString("selectedFieldName",addNewFieldEditText.getText().toString());
                    bundle.putString("uid", uid);
                    FirebaseAPI.addField("users/"+uid+"/fields", addNewFieldEditText.getText().toString());
                    NavHostFragment.findNavController(AddNewFieldFragment.this)
                            .navigate(R.id.action_AddNewFieldFragment_to_CustomizedFragment, bundle);
                }

            }
        });
    }
}