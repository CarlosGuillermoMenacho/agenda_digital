package com.agendadigital.views.modules.tabchatcontact;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.agendadigital.databinding.FragmentTabChatContactBinding;
import com.agendadigital.views.modules.tabchatcontact.components.TabChatPagerAdapter;
import com.google.android.material.tabs.TabLayout;

public class TabChatContactFragment extends Fragment {

    private FragmentTabChatContactBinding binding;
    private TabChatPagerAdapter tabChatPagerAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentTabChatContactBinding.inflate(inflater, container, false);
        tabChatPagerAdapter = new TabChatPagerAdapter(getChildFragmentManager(), 1);
        initTabs();
        return binding.getRoot();
    }

    private void initTabs() {
        binding.viewPagerChatContact.setAdapter(tabChatPagerAdapter);
        binding.tabChatContact.setupWithViewPager(binding.viewPagerChatContact);
        binding.tabChatContact.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) { }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) { }
            @Override
            public void onTabReselected(TabLayout.Tab tab) { }
        });
    }

}