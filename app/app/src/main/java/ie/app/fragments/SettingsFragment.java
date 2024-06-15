package ie.app.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.text.method.SingleLineTransformationMethod;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import ie.app.R;
import ie.app.databinding.FragmentSettingsBinding;
import ie.app.models.User;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;

public class SettingsFragment extends BaseFragment {
    private FragmentSettingsBinding binding;
    private FirebaseAuth mAuth;
    private DatabaseReference nameRef;
    private EditText edtOldPwd, edtNewPwd, edtNewPwdCf, edtName;
    private TextView tvUsername;
    private String username;
    public boolean isSignInWithGoogle;
    private Button changePwdBtn, logOutBtn, languageBtn, engBtn, vietBtn;
    private ImageButton avatarBtn;
    public static final int PICK_IMAGE = 1;

    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        super.onCreateView();
        binding = FragmentSettingsBinding.inflate(inflater, container, false);

        changePwdBtn = binding.buttonChangePwd;
        languageBtn = binding.buttonLanguage;
        logOutBtn = binding.buttonLogOut;
        engBtn = binding.English;
        vietBtn = binding.Vietnamese;
        avatarBtn = binding.avatar;
        Bundle bundle = getArguments();
        String language_changes = null;
        if (bundle != null) {
             language_changes = bundle.getString("language");
        }
        if (language_changes == null) {
            engBtn.setVisibility(View.GONE);
            vietBtn.setVisibility(View.GONE);
        } else {
            engBtn.setVisibility(View.VISIBLE);
            vietBtn.setVisibility(View.VISIBLE);
            languageBtn.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.language_icon, 0, R.drawable.down_arrow, 0);
            if (language_changes.equals("en")) {
                engBtn.setBackgroundResource(R.drawable.language_select);
                vietBtn.setBackgroundResource(R.drawable.language_not_select);
            } else {
                vietBtn.setBackgroundResource(R.drawable.language_select);
                engBtn.setBackgroundResource(R.drawable.language_not_select);
            }
        }
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        avatarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageChooser();
            }
        });

        tvUsername = getActivity().findViewById(R.id.username);

        nameRef = FirebaseDatabase.getInstance().getReference("users")
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        // display username
        displayUsername();

        tvUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                View dialogView = getLayoutInflater().inflate(R.layout.dialog_changename, null);
                edtName = dialogView.findViewById(R.id.usernameBox);

                // Set Cancelable false for when the user clicks on the outside the Dialog Box then it will remain show
                builder.setCancelable(false);

                builder.setView(dialogView);
                AlertDialog dialog = builder.create();

                dialogView.findViewById(R.id.buttonOK).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (edtName.getText().toString().isEmpty()) {
                            Toast.makeText(getActivity(), "Please enter your username.", Toast.LENGTH_LONG).show();
                            edtName.setBackgroundResource(R.drawable.error_background);
                        }

                        if (isSignInWithGoogle) {
                            nameRef.child("name").setValue(edtName.getText().toString());
                            dialog.dismiss();
                            Toast.makeText(getActivity(), "Username changed successfully!", Toast.LENGTH_LONG).show();
                        } else {
                            Map<String, String> usernameData = new HashMap<String, String>();
                            usernameData.put("email", mAuth.getCurrentUser().getEmail());
                            usernameData.put("name", edtName.getText().toString());

                            nameRef.setValue(usernameData);
                            dialog.dismiss();
                            Toast.makeText(getActivity(), "Username changed successfully!", Toast.LENGTH_LONG).show();
                        }
                    }
                });
                dialogView.findViewById(R.id.buttonCancel).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                if (dialog.getWindow() != null) {
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                }
                dialog.show();
            }
        });

        changePwdBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                View dialogView = getLayoutInflater().inflate(R.layout.dialog_changepwd, null);
                edtOldPwd = dialogView.findViewById(R.id.oldPwdBox);
                edtNewPwd = dialogView.findViewById(R.id.newPwdBox);
                edtNewPwdCf = dialogView.findViewById(R.id.confirmNewPwdBox);

                // Set Cancelable false for when the user clicks on the outside the Dialog Box then it will remain show
                builder.setCancelable(false);

                builder.setView(dialogView);
                AlertDialog dialog = builder.create();

                // Show/hide old newPwd
                edtOldPwd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (edtOldPwd.getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance())) {
                            // If newPwd is visible then hide it
                            edtOldPwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            // Change icon
                            edtOldPwd.setCompoundDrawablesRelativeWithIntrinsicBounds(ie.app.R.drawable.key, 0, ie.app.R.drawable.visability, 0);
                        } else {
                            edtOldPwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                            edtOldPwd.setCompoundDrawablesRelativeWithIntrinsicBounds(ie.app.R.drawable.key, 0, ie.app.R.drawable.visability_off, 0);
                        }
                        // Set cursor back to end of text
                        edtOldPwd.setSelection(edtOldPwd.getText().length());
                    }
                });

                // Show/hide new newPwd
                edtNewPwd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (edtNewPwd.getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance())) {
                            // If newPwd is visible then hide it
                            edtNewPwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            // Change icon
                            edtNewPwd.setCompoundDrawablesRelativeWithIntrinsicBounds(ie.app.R.drawable.key, 0, ie.app.R.drawable.visability, 0);
                        } else {
                            edtNewPwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                            edtNewPwd.setCompoundDrawablesRelativeWithIntrinsicBounds(ie.app.R.drawable.key, 0, ie.app.R.drawable.visability_off, 0);
                        }
                        // Set cursor back to end of text
                        edtNewPwd.setSelection(edtNewPwd.getText().length());
                    }
                });

                // Show/hide new newPwd confirmation
                edtNewPwdCf.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (edtNewPwdCf.getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance())) {
                            // If newPwd is visible then hide it
                            edtNewPwdCf.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            // Change icon
                            edtNewPwdCf.setCompoundDrawablesRelativeWithIntrinsicBounds(ie.app.R.drawable.key, 0, ie.app.R.drawable.visability, 0);
                        } else {
                            edtNewPwdCf.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                            edtNewPwdCf.setCompoundDrawablesRelativeWithIntrinsicBounds(ie.app.R.drawable.key, 0, ie.app.R.drawable.visability_off, 0);
                        }
                        // Set cursor back to end of text
                        edtNewPwdCf.setSelection(edtNewPwdCf.getText().length());
                    }
                });
                
                dialogView.findViewById(R.id.buttonChange).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // get data from EditText into String variables
                        String oldPwd = edtOldPwd.getText().toString();
                        String newPwd = edtNewPwd.getText().toString();
                        String newPwdCf = edtNewPwdCf.getText().toString();

                        // bunch of error handlings
                        if (oldPwd.isEmpty()) {
                            if (newPwd.isEmpty()) {
                                if (newPwdCf.isEmpty()) {
                                    Toast.makeText(getActivity(), "Please fill in all fields.", Toast.LENGTH_LONG).show();
                                    edtOldPwd.setBackgroundResource(R.drawable.error_background);
                                    edtNewPwd.setBackgroundResource(R.drawable.error_background);
                                    edtNewPwdCf.setBackgroundResource(R.drawable.error_background);
                                }
                                else {
                                    Toast.makeText(getActivity(), "Please enter your current and new passwords.", Toast.LENGTH_LONG).show();
                                    edtOldPwd.setBackgroundResource(R.drawable.error_background);
                                    edtNewPwd.setBackgroundResource(R.drawable.error_background);
                                    edtNewPwdCf.setBackgroundResource(R.drawable.edittext);
                                }
                            } else if (newPwdCf.isEmpty()) {
                                Toast.makeText(getActivity(), "Please enter your current password and new password confirmation.", Toast.LENGTH_LONG).show();
                                edtOldPwd.setBackgroundResource(R.drawable.error_background);
                                edtNewPwd.setBackgroundResource(R.drawable.edittext);
                                edtNewPwdCf.setBackgroundResource(R.drawable.error_background);
                            } else {
                                Toast.makeText(getActivity(), "Please enter your current password.", Toast.LENGTH_LONG).show();
                                edtOldPwd.setBackgroundResource(R.drawable.error_background);
                                edtNewPwd.setBackgroundResource(R.drawable.edittext);
                                edtNewPwdCf.setBackgroundResource(R.drawable.edittext);
                            }
                            return;
                        } else if (newPwd.isEmpty()) {
                            if (newPwdCf.isEmpty()) {
                                Toast.makeText(getActivity(), "Please enter your new password and its confirmation.", Toast.LENGTH_LONG).show();
                                edtOldPwd.setBackgroundResource(R.drawable.edittext);
                                edtNewPwd.setBackgroundResource(R.drawable.error_background);
                                edtNewPwdCf.setBackgroundResource(R.drawable.error_background);
                            } else {
                                Toast.makeText(getActivity(), "Please enter your new password.", Toast.LENGTH_LONG).show();
                                edtNewPwd.setBackgroundResource(R.drawable.error_background);
                                edtOldPwd.setBackgroundResource(R.drawable.edittext);
                                edtNewPwdCf.setBackgroundResource(R.drawable.edittext);
                            }
                            return;
                        } else if (newPwdCf.isEmpty()) {
                            Toast.makeText(getActivity(), "Please enter your new password confirmation.", Toast.LENGTH_LONG).show();
                            edtNewPwdCf.setBackgroundResource(R.drawable.error_background);
                            edtOldPwd.setBackgroundResource(R.drawable.edittext);
                            edtNewPwd.setBackgroundResource(R.drawable.edittext);
                            return;
                        } else if (oldPwd.equals(newPwd)) {
                            Toast.makeText(getActivity(), "The new password is the same as your current password.", Toast.LENGTH_LONG).show();
                            edtOldPwd.setBackgroundResource(R.drawable.edittext);
                            edtNewPwd.setBackgroundResource(R.drawable.error_background);
                            edtNewPwdCf.setBackgroundResource(R.drawable.edittext);
                            return;
                        } else if (!newPwd.equals(newPwdCf)) {
                            Toast.makeText(getActivity(), "New password confirmation does not match.", Toast.LENGTH_LONG).show();
                            edtOldPwd.setBackgroundResource(R.drawable.edittext);
                            edtNewPwd.setBackgroundResource(R.drawable.edittext);
                            edtNewPwdCf.setBackgroundResource(R.drawable.error_background);
                            return;
                        } else {
                            edtOldPwd.setBackgroundResource(R.drawable.edittext);
                            edtNewPwd.setBackgroundResource(R.drawable.edittext);
                            edtNewPwdCf.setBackgroundResource(R.drawable.edittext);
                        }

                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        AuthCredential authCredential = EmailAuthProvider.getCredential(user.getEmail(), oldPwd);
                        user.reauthenticate(authCredential).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                user.updatePassword(newPwd).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            dialog.dismiss();
                                            Toast.makeText(getActivity(), "Password changed successfully!", Toast.LENGTH_LONG).show();
                                        } else {
                                            Toast.makeText(getActivity(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                                edtOldPwd.setBackgroundResource(R.drawable.error_background);
                                edtNewPwd.setBackgroundResource(R.drawable.error_background);
                                edtNewPwdCf.setBackgroundResource(R.drawable.error_background);
                            }
                        });
                    }
                });
                
                dialogView.findViewById(R.id.buttonCancel).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                if (dialog.getWindow() != null) {
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                }
                dialog.show();
            }
        });

        languageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Locale.getDefault().getLanguage().equals("en")) {
                    engBtn.setBackgroundResource(R.drawable.language_select);
                    vietBtn.setBackgroundResource(R.drawable.language_not_select);
                } else {
                    vietBtn.setBackgroundResource(R.drawable.language_select);
                    engBtn.setBackgroundResource(R.drawable.language_not_select);
                }

                if (engBtn.getVisibility() == View.GONE) {
                    engBtn.setVisibility(View.VISIBLE);
                    vietBtn.setVisibility(View.VISIBLE);
                    languageBtn.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.language_icon, 0, R.drawable.down_arrow, 0);
                } else if (engBtn.getVisibility() == View.VISIBLE) {
                    engBtn.setVisibility(View.GONE);
                    vietBtn.setVisibility(View.GONE);
                    languageBtn.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.language_icon, 0, R.drawable.right_arrow, 0);
                }
            }
        });

        engBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                engBtn.setBackgroundResource(R.drawable.language_select);
                vietBtn.setBackgroundResource(R.drawable.language_not_select);
                setLocal(getActivity(), "en");
                Bundle bundle = new Bundle();
                bundle.putString("uid", uid);
                bundle.putString("language", "en");
                Toast.makeText(getActivity(), "Language changed into English successfully!", Toast.LENGTH_LONG).show();
                NavHostFragment.findNavController(SettingsFragment.this)
                        .navigate(R.id.action_settingsFragment_to_SettingsFragment, bundle);
                displayUsername();
            }
        });

        vietBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vietBtn.setBackgroundResource(R.drawable.language_select);
                engBtn.setBackgroundResource(R.drawable.language_not_select);
                setLocal(getActivity(), "vi");
                Bundle bundle = new Bundle();
                bundle.putString("uid", uid);
                bundle.putString("language", "vi");
                Toast.makeText(getActivity(), "Đổi ngôn ngữ sang tiếng Việt\nthành công!", Toast.LENGTH_LONG).show();
                NavHostFragment.findNavController(SettingsFragment.this)
                        .navigate(R.id.action_settingsFragment_to_SettingsFragment, bundle);
                displayUsername();
            }
        });

        logOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                View dialogView = getLayoutInflater().inflate(R.layout.dialog_logout, null);

                // Set Cancelable false for when the user clicks on the outside the Dialog Box then it will remain show
                builder.setCancelable(false);

                builder.setView(dialogView);
                AlertDialog dialog = builder.create();
                dialogView.findViewById(R.id.buttonOK).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                        mAuth.signOut();
                        Toast.makeText(getActivity(), "Logged Out!", Toast.LENGTH_LONG).show();
                        NavHostFragment.findNavController(SettingsFragment.this)
                                .navigate(R.id.action_settingsFragment_to_WelcomeFragment);
                    }
                });
                dialogView.findViewById(R.id.buttonCancel).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                if (dialog.getWindow() != null) {
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                }
                dialog.show();
            }
        });

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Bundle bundle = new Bundle();
                bundle.putString("uid", uid);
                NavHostFragment.findNavController(SettingsFragment.this)
                        .navigate(R.id.action_settingsFragment_to_homepageFragment, bundle);
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback (callback);

        BottomNavigationView bottomNavigationView = binding.bottomNavigation;
        bottomNavigationView.setSelectedItemId(R.id.setting_button);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            if(item.getItemId()==R.id.home_button) {
                Bundle bundle = new Bundle();
                bundle.putString("uid", uid);
                NavHostFragment.findNavController(SettingsFragment.this)
                        .navigate(R.id.action_settingsFragment_to_homepageFragment, bundle);
                return true;
            }
            else if(item.getItemId()==R.id.garden_button) {
                Bundle bundle = new Bundle();
                bundle.putString("uid", uid);
                NavHostFragment.findNavController(SettingsFragment.this)
                        .navigate(R.id.action_settingsFragment_to_FieldlistFragment, bundle);
                return true;
            }
            else if(item.getItemId()==R.id.tips_button) {
                Bundle bundle = new Bundle();
                bundle.putString("uid", uid);
                NavHostFragment.findNavController(SettingsFragment.this)
                        .navigate(R.id.action_settingsFragment_to_tipsFragment, bundle);
                return true;
            }
            else if(item.getItemId()==R.id.setting_button) {
                return true;
            }
            return false;
        });
    }

    public void setLocal(Activity activity, String langCode) {
        Locale locale = new Locale(langCode);
        locale.setDefault(locale);
        Resources resources = activity.getResources();
        Configuration config = resources.getConfiguration();
        DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        config.locale = locale;
        resources.updateConfiguration(config, displayMetrics);
        // restart currently loaded activity
        activity.recreate();
    }

    public void imageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE) {
            if (data != null) {
                Uri selectedImageUri = data.getData();
                avatarBtn.setImageURI(selectedImageUri);
            }
        }
    }

    private void displayUsername() {
        nameRef.child("name").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // if user signs in with google, display name
                if (snapshot.exists()) {
                    isSignInWithGoogle = true;
                    username = snapshot.getValue(String.class);
                    tvUsername.setText(username);
                } else {
                    // if user signs in with email, display email
                    nameRef.child("email").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            username = snapshot.getValue(String.class);
                            tvUsername.setText(username);
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}