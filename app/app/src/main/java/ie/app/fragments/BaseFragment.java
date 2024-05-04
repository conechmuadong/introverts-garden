package ie.app.fragments;

import android.os.Bundle;
import androidx.fragment.app.Fragment;

import ie.app.models.Field;
import ie.app.models.User;

public class BaseFragment extends Fragment {
    public static User user = new User();
    public static Field field = new Field();
    public static String uid;

    public void onCreateView() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            uid = bundle.getString("uid");
        }
    }
}
