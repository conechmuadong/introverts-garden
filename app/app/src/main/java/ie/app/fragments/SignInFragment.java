package ie.app.fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
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
import ie.app.models.User;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.Nullable;
public class SignInFragment extends Fragment {
    private FragmentSignInBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private EditText edtUsername, edtPassword;
    private TextView tvswitchToSignUp, tvforgotPassword;
    private GoogleSignInClient googleSignInClient;
    int RC_SIGN_IN = 20;

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
                            emailBox.setBackgroundResource(R.drawable.error_background);
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
                                        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
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

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("336272297460-itg9oasu1micej9f04d3r33our9mmkq1.apps.googleusercontent.com")
                .requestEmail().build();
        googleSignInClient = GoogleSignIn.getClient(this.getContext(), gso);

        // every time the user logs in, the app will automatically select the previous email so we need to log out
        googleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    FirebaseAuth.getInstance().signOut();
                }
            }
        });

        binding.buttonGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                googleSignIn();
            }
        });
    }

    private void googleSignIn() {
        Intent intent = googleSignInClient.getSignInIntent();
        startActivityForResult(intent, RC_SIGN_IN);
    }

    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuth(account.getIdToken());
            }
            catch (Exception e) {
                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

    private void firebaseAuth(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    User user = new User(mAuth.getCurrentUser().getEmail(), mAuth.getCurrentUser().getDisplayName());

                    FirebaseDatabase.getInstance().getReference("users")
                            .child(mAuth.getCurrentUser().getUid()).setValue(user)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    NavHostFragment.findNavController(SignInFragment.this)
                                            .navigate(R.id.action_signInFragment_to_homepageFragment);
                                    Toast.makeText(getActivity(), "Login successfully!", Toast.LENGTH_LONG).show();
                                }
                            });

                } else {
                    Toast.makeText(getActivity(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void authenticateUser() {
        String email = edtUsername.getText().toString();
        String password = edtPassword.getText().toString();

        if (email.isEmpty()) {
            if (password.isEmpty()) {
                Toast.makeText(getActivity(), "Please fill in all fields.", Toast.LENGTH_LONG).show();
                edtUsername.setBackgroundResource(R.drawable.error_background);
                edtPassword.setBackgroundResource(R.drawable.error_background);
                return;
            } else {
                Toast.makeText(getActivity(), "Please enter your email.", Toast.LENGTH_LONG).show();
                edtUsername.setBackgroundResource(R.drawable.error_background);
                edtPassword.setBackgroundResource(R.drawable.edittext);
                return;
            }
        } else if (password.isEmpty()) {
            Toast.makeText(getActivity(), "Please enter your password.", Toast.LENGTH_LONG).show();
            edtPassword.setBackgroundResource(R.drawable.error_background);
            edtUsername.setBackgroundResource(R.drawable.edittext);
            return;
        } else {
            edtPassword.setBackgroundResource(R.drawable.edittext);
            edtUsername.setBackgroundResource(R.drawable.edittext);
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        if (mAuth.getCurrentUser() != null && mAuth.getCurrentUser().isEmailVerified()) {
                            // Sign in success
                            Bundle bundle = new Bundle();
                            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                            bundle.putString("uid", uid);
                            NavHostFragment.findNavController(SignInFragment.this)
                                    .navigate(R.id.action_signInFragment_to_homepageFragment, bundle);
                            Toast.makeText(getActivity(), "Login successfully!", Toast.LENGTH_LONG).show();
                        } else {
                            edtPassword.setBackgroundResource(R.drawable.edittext);
                            edtUsername.setBackgroundResource(R.drawable.error_background);

                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                            View dialogView = getLayoutInflater().inflate(R.layout.dialog_reverify, null);
                            TextView tvresendVerificationLink = dialogView.findViewById(R.id.resendVerificationLink);

                            builder.setView(dialogView);
                            AlertDialog dialog = builder.create();

                            dialogView.findViewById(R.id.buttonOK).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.dismiss();
                                }
                            });

                            tvresendVerificationLink.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    mAuth.getCurrentUser().sendEmailVerification();
                                    dialog.dismiss();
                                    Toast.makeText(getActivity(), "Verification link resent!", Toast.LENGTH_LONG).show();
                                }
                            });
                            if (dialog.getWindow() != null) {
                                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                            }
                            dialog.show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                        edtPassword.setBackgroundResource(R.drawable.error_background);
                        edtUsername.setBackgroundResource(R.drawable.error_background);
                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}