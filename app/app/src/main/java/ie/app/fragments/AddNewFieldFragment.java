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

public class AddNewFieldFragment extends Fragment {

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

        doneBtn = binding.doneBtn;
        addNewFieldEditText = binding.newFieldEditText;

        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (addNewFieldEditText.getText().toString().equals("")) {
                    Toast.makeText(getContext(), "Không được để trống!", Toast.LENGTH_SHORT).show();
                    try {
                        File myObj = new File("filename.txt");
                        if (myObj.createNewFile()) {
                            Log.e("haiya", "File created: " + myObj.getName());
                        } else {
                            Log.e("haiya", "File already exists.");
                        }
                    } catch (Exception e) {
                        Log.e("haiya", "An error occurred.");
                        e.printStackTrace();
                    }
                } else {
                    NavHostFragment.findNavController(AddNewFieldFragment.this)
                            .navigateUp();
                    FirebaseAPI.addField("users", addNewFieldEditText.getText().toString());
                }

            }
        });
    }
}