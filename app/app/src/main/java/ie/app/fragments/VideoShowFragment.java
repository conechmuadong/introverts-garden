package ie.app.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

import androidx.navigation.fragment.NavHostFragment;
import ie.app.R;
import ie.app.databinding.FragmentShowVideoBinding;
import org.jetbrains.annotations.NotNull;

public class VideoShowFragment extends BaseFragment {
    private List<String> videoEmbeddedList = List.of("<iframe width=\"100%\" height=\"100%\" src=\"https://www.youtube.com/embed/JPR5YT1FXL0?si=jioZ0NsaJ73aTL0N\" title=\"YouTube video player\" frameborder=\"0\" allow=\"accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share\" referrerpolicy=\"strict-origin-when-cross-origin\" allowfullscreen></iframe>",
            "<iframe width=\"100%\" height=\"100%\" src=\"https://www.youtube.com/embed/wW5JCuhxNQg?si=b34SDUuJXg_753ow\" title=\"YouTube video player\" frameborder=\"0\" allow=\"accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share\" referrerpolicy=\"strict-origin-when-cross-origin\" allowfullscreen></iframe>",
            "<iframe width=\"100%\" height=\"100%\" src=\"https://www.youtube.com/embed/brbPNLVF9b4?si=F6HsEg9zJWa7R3qu\" title=\"YouTube video player\" frameborder=\"0\" allow=\"accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share\" referrerpolicy=\"strict-origin-when-cross-origin\" allowfullscreen></iframe>",
            "<iframe width=\"100%\" height=\"100%\" src=\"https://www.youtube.com/embed/opmsG0r69L4?si=_tH0pPGrJOABZM_P\" title=\"YouTube video player\" frameborder=\"0\" allow=\"accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share\" referrerpolicy=\"strict-origin-when-cross-origin\" allowfullscreen></iframe>",
            "<iframe width=\"100%\" height=\"100%\" src=\"https://www.youtube.com/embed/JWMrwq-irK8?si=Kf_rjQfSL4UZoWVm\" title=\"YouTube video player\" frameborder=\"0\" allow=\"accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share\" referrerpolicy=\"strict-origin-when-cross-origin\" allowfullscreen></iframe>");
    private FragmentShowVideoBinding binding;
    private int tip;
    @SuppressLint("SetJavaScriptEnabled")
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView();
        binding = FragmentShowVideoBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        WebView videoPlayer = binding.videoPlayer;
        Bundle bundle = getArguments();
        if (bundle != null){
            String tipNo = bundle.getString("Tip chosen");
            switch (tipNo){
                case "Tip 1":
                    videoPlayer.loadData(videoEmbeddedList.get(0),"text/html", "utf-8");
                    videoPlayer.getSettings().setJavaScriptEnabled(true);
                    videoPlayer.setWebChromeClient(new WebChromeClient());
                    binding.videoTitle.setText(R.string.tip1_title);
                    binding.authorAva.setImageResource(R.drawable.profile1);
                    binding.authorName.setText(R.string.tip1_author);
                    tip = 0;
                    break;
                case "Tip 2":
                    videoPlayer.loadData(videoEmbeddedList.get(1),"text/html", "utf-8");
                    videoPlayer.getSettings().setJavaScriptEnabled(true);
                    videoPlayer.setWebChromeClient(new WebChromeClient());
                    binding.videoTitle.setText(R.string.tip2_title);
                    binding.authorAva.setImageResource(R.drawable.profile2);
                    binding.authorName.setText(R.string.tip2_author);
                    tip = 1;
                    break;
                case "Tip 3":
                    videoPlayer.loadData(videoEmbeddedList.get(2),"text/html", "utf-8");
                    videoPlayer.getSettings().setJavaScriptEnabled(true);
                    videoPlayer.setWebChromeClient(new WebChromeClient());
                    binding.videoTitle.setText(R.string.tip3_title);
                    binding.authorAva.setImageResource(R.drawable.profile5);
                    binding.authorName.setText(R.string.tip3_author);
                    tip = 2;
                    break;
                case "Tip 4":
                    videoPlayer.loadData(videoEmbeddedList.get(3),"text/html", "utf-8");
                    videoPlayer.getSettings().setJavaScriptEnabled(true);
                    videoPlayer.setWebChromeClient(new WebChromeClient());
                    binding.videoTitle.setText(R.string.tip4_title);
                    binding.authorAva.setImageResource(R.drawable.profile3);
                    binding.authorName.setText(R.string.tip4_author);
                    tip = 3;
                    break;
                case "Tip 5":
                    videoPlayer.loadData(videoEmbeddedList.get(4),"text/html", "utf-8");
                    videoPlayer.getSettings().setJavaScriptEnabled(true);
                    videoPlayer.setWebChromeClient(new WebChromeClient());
                    binding.videoTitle.setText(R.string.tip5_title);
                    binding.authorAva.setImageResource(R.drawable.profile4);
                    binding.authorName.setText(R.string.tip5_author);
                    tip = 4;
                    break;
            }
        }
        return binding.getRoot();
    }
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("uid", uid);
                NavHostFragment.findNavController(VideoShowFragment.this)
                        .navigate(R.id.action_videoShowFragment_to_tipsFragment, bundle);
            }
        });
        binding.toYoutube.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW, null);
                intent.setData(Uri.parse(videoEmbeddedList.get(tip).substring(videoEmbeddedList.get(tip).indexOf("src=\"")+5,videoEmbeddedList.get(tip).indexOf("?"))));
                startActivity(intent);
            }
        });
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Bundle bundle = new Bundle();
                bundle.putString("uid", uid);
                NavHostFragment.findNavController(VideoShowFragment.this)
                        .navigate(R.id.action_videoShowFragment_to_tipsFragment, bundle);
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(callback);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
