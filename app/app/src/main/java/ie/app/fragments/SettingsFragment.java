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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import ie.app.R;
import ie.app.databinding.FragmentSettingsBinding;
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

public class SettingsFragment extends Fragment {
    private FragmentSettingsBinding binding;
    private FirebaseAuth mAuth;
    private Button changePwdBtn, logOutBtn, languageBtn, engBtn, vietBtn;

    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentSettingsBinding.inflate(inflater, container, false);

        changePwdBtn = binding.buttonChangePwd;
        languageBtn = binding.buttonLanguage;
        logOutBtn = binding.buttonLogOut;
        engBtn = binding.English;
        vietBtn = binding.Vietnamese;

        engBtn.setVisibility(View.GONE);
        vietBtn.setVisibility(View.GONE);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        languageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
    }
}