package ie.app.fragments;

import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
//        String uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        // bunch of error handlings
        if (email.isEmpty()) {
            if (password.isEmpty()) {
                if (passwordConfirm.isEmpty()) {
                    Toast.makeText(getActivity(), "Please fill in all fields.", Toast.LENGTH_LONG).show();
                    edtUsr.setBackgroundResource(R.drawable.error_background);
                    edtPwd.setBackgroundResource(R.drawable.error_background);
                    edtPwdCf.setBackgroundResource(R.drawable.error_background);
                }
                else {
                    Toast.makeText(getActivity(), "Please enter your email and password.", Toast.LENGTH_LONG).show();
                    edtUsr.setBackgroundResource(R.drawable.error_background);
                    edtPwd.setBackgroundResource(R.drawable.error_background);
                    edtPwdCf.setBackgroundResource(R.drawable.edittext);
                }
            } else if (passwordConfirm.isEmpty()) {
                Toast.makeText(getActivity(), "Please enter your email and password confirmation.", Toast.LENGTH_LONG).show();
                edtUsr.setBackgroundResource(R.drawable.error_background);
                edtPwd.setBackgroundResource(R.drawable.edittext);
                edtPwdCf.setBackgroundResource(R.drawable.error_background);
            } else {
                Toast.makeText(getActivity(), "Please enter your email.", Toast.LENGTH_LONG).show();
                edtUsr.setBackgroundResource(R.drawable.error_background);
                edtPwd.setBackgroundResource(R.drawable.edittext);
                edtPwdCf.setBackgroundResource(R.drawable.edittext);
            }
            return;
        } else if (password.isEmpty()) {
            if (passwordConfirm.isEmpty()) {
                Toast.makeText(getActivity(), "Please enter your password and its confirmation.", Toast.LENGTH_LONG).show();
                edtUsr.setBackgroundResource(R.drawable.edittext);
                edtPwd.setBackgroundResource(R.drawable.error_background);
                edtPwdCf.setBackgroundResource(R.drawable.error_background);
            } else {
                Toast.makeText(getActivity(), "Please enter your password.", Toast.LENGTH_LONG).show();
                edtPwd.setBackgroundResource(R.drawable.error_background);
                edtUsr.setBackgroundResource(R.drawable.edittext);
                edtPwdCf.setBackgroundResource(R.drawable.edittext);
            }
            return;
        } else if (passwordConfirm.isEmpty()) {
            Toast.makeText(getActivity(), "Please enter your password confirmation.", Toast.LENGTH_LONG).show();
            edtPwdCf.setBackgroundResource(R.drawable.error_background);
            edtUsr.setBackgroundResource(R.drawable.edittext);
            edtPwd.setBackgroundResource(R.drawable.edittext);
            return;
        } else if (!password.equals(passwordConfirm)) {
            Toast.makeText(getActivity(), "Password confirmation does not match.", Toast.LENGTH_LONG).show();
            edtUsr.setBackgroundResource(R.drawable.edittext);
            edtPwd.setBackgroundResource(R.drawable.edittext);
            edtPwdCf.setBackgroundResource(R.drawable.error_background);
            return;
        } else {
            edtUsr.setBackgroundResource(R.drawable.edittext);
            edtPwd.setBackgroundResource(R.drawable.edittext);
            edtPwdCf.setBackgroundResource(R.drawable.edittext);
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        if (mAuth.getCurrentUser() != null) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                            View dialogView = getLayoutInflater().inflate(R.layout.dialog_verify, null);

                            // Set Cancelable false for when the user clicks on the outside the Dialog Box then it will remain show
                            builder.setCancelable(false);
                            builder.setView(dialogView);
                            AlertDialog dialog = builder.create();
                            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            dialog.setCanceledOnTouchOutside(false);
                            dialog.show();

                            dialogView.findViewById(R.id.buttonOK).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    mAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                User user = new User(email);
                                                // Send data to Firebase Realtime Database, using uid as unique identity of each user
                                                FirebaseDatabase.getInstance().getReference("users")
                                                        .child(mAuth.getInstance().getUid()).setValue(user)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                // Sign up success
                                                                dialog.dismiss();
                                                                NavHostFragment.findNavController(SignUpFragment.this)
                                                                        .navigate(ie.app.R.id.action_signUpFragment_to_signInFragment);
                                                                Toast.makeText(getActivity(), "Registered successfully!", Toast.LENGTH_LONG).show();
                                                            }
                                                        });
                                            } else {
                                                Toast.makeText(getActivity(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                                edtUsr.setBackgroundResource(R.drawable.error_background);
                                                edtPwd.setBackgroundResource(R.drawable.error_background);
                                                edtPwdCf.setBackgroundResource(R.drawable.error_background);
                                            }
                                        }
                                    });
                                }
                            });
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                        edtUsr.setBackgroundResource(R.drawable.error_background);
                        edtPwd.setBackgroundResource(R.drawable.error_background);
                        edtPwdCf.setBackgroundResource(R.drawable.error_background);
                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}