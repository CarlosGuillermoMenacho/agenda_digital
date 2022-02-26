package com.agendadigital.views.modules.tabchatcontact;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ConcatAdapter;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.agendadigital.R;
import com.agendadigital.views.modules.tabchatcontact.components.TabChatPagerAdapter;
import com.google.android.material.tabs.TabLayout;

public class TabChatContactFragment extends Fragment {

    private View view;
    private TabChatPagerAdapter tabChatPagerAdapter;
    private TabLayout tabChatContact;
    private ViewPager viewPagerChatContact;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_tab_chat_contact, container, false);
        tabChatPagerAdapter = new TabChatPagerAdapter(getChildFragmentManager(), 1);
        initViews();
        return view;
    }

    private void initViews() {
        tabChatContact = view.findViewById(R.id.tabChatContact);
        viewPagerChatContact = view.findViewById(R.id.viewPagerChatContact);
        viewPagerChatContact.setAdapter(tabChatPagerAdapter);
        tabChatContact.setupWithViewPager(viewPagerChatContact);
        tabChatContact.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) { }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) { }
            @Override
            public void onTabReselected(TabLayout.Tab tab) { }
        });
    }
}