package com.learning.phisingdetection;

import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.learning.phisingdetection.databinding.FragmentFirstBinding;
import com.learning.phisingdetection.service.InvalidURLException;
import com.learning.phisingdetection.service.URLInfo;
import com.learning.phisingdetection.validation.Validations;

public class FirstFragment extends Fragment {

    private FragmentFirstBinding binding;
    private Handler handler;
    private Handler mainHandler;
    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentFirstBinding.inflate(inflater, container, false);
        return binding.getRoot();


    }


    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        HandlerThread thread = new HandlerThread("MYHandler");
        thread.start();
        Handler handler = new Handler(thread.getLooper());
        mainHandler = new Handler(view.getContext().getMainLooper());

        binding.buttonFirst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.output.setText("");
                String url = binding.url.getText().toString().toLowerCase();

                if (handler == null) {
                    binding.output.setText("Failed to check");
                    return;
                }

                handler.post(() -> {
                        if (Validations.validURL(url) == false) {
                            mainHandler.post(() -> {
                                binding.output.setText("URL not exists");
                            });

                            return;
                        }

                        try {
                            URLInfo urlInfo = new URLInfo(url);
                            String status =  urlInfo.getStatus();
                            mainHandler.post(() -> {
                            binding.output.setText(status);
                            });
                        } catch (Exception ex) {
                            mainHandler.post(() -> {
                                binding.output.setText("Invalid URL");
                            });
                        }

                });

            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}