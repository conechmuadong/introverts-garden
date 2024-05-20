package ie.app.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import ie.app.R;
import ie.app.databinding.FragmentTipsBinding;

public class TipsFragment extends BaseFragment {

    private FragmentTipsBinding binding;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        super.onCreateView();
        binding = FragmentTipsBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle bundle = new Bundle();
        bundle.putString("uid", uid);

        BottomNavigationView bottomNavigationView = binding.bottomNavigation;
        bottomNavigationView.setSelectedItemId(R.id.tips_button);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            if(item.getItemId()==R.id.home_button) {
                NavHostFragment.findNavController( TipsFragment.this)
                        .navigate(R.id.action_tipsFragment_to_homepageFragment, bundle);
                return true;
            }
            else if(item.getItemId()==R.id.garden_button) {

                NavHostFragment.findNavController( TipsFragment.this)
                        .navigate(R.id.action_tipsFragment_to_fieldlistFragment, bundle);
                return true;
            }
            else if(item.getItemId()==R.id.tips_button) {
                return true;
            }
            else if(item.getItemId()==R.id.setting_button) {
//                Bundle bundle = new Bundle();
//                bundle.putString("uid", uid);
                NavHostFragment.findNavController( TipsFragment.this)
                        .navigate(R.id.action_tipsFragment_to_settingsFragment);
                return true;
            }
            return false;
        });

        binding.tip1.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        bundle.putString("Tip chosen", "Tip 1");

                        NavHostFragment.findNavController(TipsFragment.this)
                                .navigate(R.id.action_tipsFragment_to_videoShowFragment, bundle);
                    }
                }
        );
        binding.tip2.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        bundle.putString("Tip chosen", "Tip 2");

                        NavHostFragment.findNavController(TipsFragment.this)
                                .navigate(R.id.action_tipsFragment_to_videoShowFragment, bundle);
                    }
                }
        );
        binding.tip3.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        bundle.putString("Tip chosen", "Tip 3");

                        NavHostFragment.findNavController(TipsFragment.this)
                                .navigate(R.id.action_tipsFragment_to_videoShowFragment, bundle);
                    }
                }
        );
        binding.tip4.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        bundle.putString("Tip chosen", "Tip 4");

                        NavHostFragment.findNavController(TipsFragment.this)
                                .navigate(R.id.action_tipsFragment_to_videoShowFragment, bundle);
                    }
                }
        );
        binding.tip5.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        bundle.putString("Tip chosen", "Tip 5");

                        NavHostFragment.findNavController(TipsFragment.this)
                                .navigate(R.id.action_tipsFragment_to_videoShowFragment, bundle);
                    }
                }
        );

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}