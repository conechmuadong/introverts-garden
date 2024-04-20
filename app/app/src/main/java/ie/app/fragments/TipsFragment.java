package ie.app.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import ie.app.R;
import ie.app.databinding.FragmentTipsBinding;

public class TipsFragment extends Fragment {

    private FragmentTipsBinding binding;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentTipsBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle bundle = new Bundle();
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