package ie.app.fragments;

import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.text.method.SingleLineTransformationMethod;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import ie.app.R;
import ie.app.databinding.FragmentSignUpBinding;
import ie.app.models.User;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class SignUpFragment extends Fragment {
    private FragmentSignUpBinding binding;
    private FirebaseAuth mAuth;
    private EditText edtUsr, edtPwd, edtPwdCf;
    private TextView tvswitchToSignIn;

    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentSignUpBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        edtUsr = getActivity().findViewById(R.id.edUsername);
        edtPwd = getActivity().findViewById(ie.app.R.id.edPassword);
        edtPwdCf = getActivity().findViewById(ie.app.R.id.edPasswordConfirmation);


        // Show/hide password
        edtPwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edtPwd.getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance())) {
                    // If password is visible then hide it
                    edtPwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    // Change icon
                    edtPwd.setCompoundDrawablesRelativeWithIntrinsicBounds(ie.app.R.drawable.key, 0, ie.app.R.drawable.visability, 0);
                } else {
                    edtPwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    edtPwd.setCompoundDrawablesRelativeWithIntrinsicBounds(ie.app.R.drawable.key, 0, ie.app.R.drawable.visability_off, 0);
                }
                // Set cursor back to end of text
                edtPwd.setSelection(edtPwd.getText().length());
            }
        });

        // Show/hide password confirmation
        edtPwdCf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edtPwdCf.getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance())) {
                    // If password is visible then hide it
                    edtPwdCf.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    // Change icon
                    edtPwdCf.setCompoundDrawablesRelativeWithIntrinsicBounds(ie.app.R.drawable.key, 0, ie.app.R.drawable.visability, 0);
                } else {
                    edtPwdCf.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    edtPwdCf.setCompoundDrawablesRelativeWithIntrinsicBounds(ie.app.R.drawable.key, 0, ie.app.R.drawable.visability_off, 0);
                }
                // Set cursor back to end of text
                edtPwdCf.setSelection(edtPwdCf.getText().length());
            }
        });

        binding.buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser();
            }
        });

        tvswitchToSignIn = getActivity().findViewById(ie.app.R.id.switchToSignIn);
        tvswitchToSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(SignUpFragment.this)
                        .navigate(ie.app.R.id.action_signUpFragment_to_signInFragment);
            }
        });
    }

    private void registerUser() {
        // get data from EditText into String variables
        String email = edtUsr.getText().toString();
        String password = edtPwd.getText().toString();
        String passwordConfirm = edtPwdCf.getText().toString();
        String uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        if (email.isEmpty() || password.isEmpty() || passwordConfirm.isEmpty()) {
            Toast.makeText(getActivity(), "Please fill in all fields.", Toast.LENGTH_LONG).show();
            return;
        }
        else if (!password.equals(passwordConfirm)) {
            Toast.makeText(getActivity(), "Password confirmation does not match.", Toast.LENGTH_LONG).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            User user = new User(email);
                            // Send data to Firebase Realtime Database, using uid as unique identity of each user
                            FirebaseDatabase.getInstance().getReference("users")
                                    .child(uid).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            // Sign up success
                                            NavHostFragment.findNavController(SignUpFragment.this)
                                                    .navigate(ie.app.R.id.action_signUpFragment_to_signInFragment);
                                            Toast.makeText(getActivity(), "Registered successfully!", Toast.LENGTH_LONG).show();
                                        }
                                    });
                        } else {
                            // If sign up fails, display a message to the user.
                            Toast.makeText(getActivity(), "Authentication failed.", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}