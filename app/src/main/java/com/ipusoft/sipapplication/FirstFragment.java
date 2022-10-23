package com.ipusoft.sipapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.ipusoft.sip.manager.SipPhoneManager;
import com.ipusoft.sipapplication.databinding.FragmentFirstBinding;

public class FirstFragment extends Fragment {

    private FragmentFirstBinding binding;

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


        //  String extend = GsonUtils.toJson(new AuthInfo("123", "345", "789"));
        //  String encode = EncodeUtils.base64Encode2String(extend.getBytes());
        //15175182683 qr
        binding.buttonFirst.setOnClickListener(view1 -> SipPhoneManager.callPhoneBySip("18317893005"));
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}