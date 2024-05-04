package ie.app.fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.text.method.SingleLineTransformationMethod;
import android.view.LayoutInflater;
import android.graphics.drawable.ColorDrawable;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import ie.app.R;
import ie.app.databinding.FragmentSignInBinding;
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

public class SignInFragment extends Fragment {
    private FragmentSignInBinding binding;
    private FirebaseAuth mAuth;
    private EditText edtUsername, edtPassword;
    private TextView tvswitchToSignUp, tvforgotPassword;

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentSignInBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        edtUsername = getActivity().findViewById(R.id.edtUsername);
        edtPassword = getActivity().findViewById(R.id.edtPassword);


        // Show/hide password
        edtPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edtPassword.getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance())) {
                    // If password is visible then hide it
                    edtPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    // Change icon
                    edtPassword.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.key, 0, R.drawable.visability, 0);
                } else {
                    edtPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    edtPassword.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.key, 0, R.drawable.visability_off, 0);
                }
                // Set cursor back to end of text
                edtPassword.setSelection(edtPassword.getText().length());
            }
        });

        binding.buttonSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                authenticateUser();
            }
        });

        tvswitchToSignUp = getActivity().findViewById(R.id.switchToSignUp);
        tvswitchToSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(SignInFragment.this)
                        .navigate(R.id.action_signInFragment_to_signUpFragment);
            }
        });

        tvforgotPassword = getActivity().findViewById(R.id.forgotPassword);
        tvforgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                View dialogView = getLayoutInflater().inflate(R.layout.dialog_forgot, null);
                EditText emailBox = dialogView.findViewById(R.id.emailBox);
//                String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

                builder.setView(dialogView);
                AlertDialog dialog = builder.create();

                dialogView.findViewById(R.id.buttonReset).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String userEmail = emailBox.getText().toString();

                        if (userEmail.isEmpty()) {
                            tvforgotPassword.setError("Email field cant be empty.");
                            Toast.makeText(getActivity(), "Please enter your registered email.", Toast.LENGTH_LONG).show();
                            return;
                        }

                        mAuth.sendPasswordResetEmail(userEmail)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(getActivity(), "Check your email.", Toast.LENGTH_LONG).show();
                                        dialog.dismiss();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getActivity(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                });
                    }
                });

                dialogView.findViewById(R.id.buttonCancel).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                if (dialog.getWindow() != null) {
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                }
                dialog.show();
            }
        });
    }

    private void authenticateUser() {
        String email = edtUsername.getText().toString();
        String password = edtPassword.getText().toString();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(getActivity(), "Please fill in all fields", Toast.LENGTH_LONG).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success
                            Bundle bundle = new Bundle();
                            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                            bundle.putString("uid", uid);
                            NavHostFragment.findNavController(SignInFragment.this)
                                    .navigate(R.id.action_signInFragment_to_homepageFragment, bundle);
                            Toast.makeText(getActivity(), "Login successfully!", Toast.LENGTH_LONG).show();
                        } else {
                            // If sign in fails, display a message to the user.
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