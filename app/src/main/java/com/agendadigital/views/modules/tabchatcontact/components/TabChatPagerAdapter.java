package com.agendadigital.views.modules.tabchatcontact.components;

import com.agendadigital.views.modules.contacts.ContactFragment;
import com.agendadigital.views.modules.expandableContacts.ExpandableContactsFragment;

import java.util.ArrayList;
import java.util.List;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class TabChatPagerAdapter extends FragmentPagerAdapter {

    List<Fragment> fragmentList;

    public TabChatPagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
        fragmentList = new ArrayList<>();
        fragmentList.add(new ContactFragment());
        fragmentList.add(new ExpandableContactsFragment());
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (position == 0)
            return "Chat";
        return "Contactos";
    }
}
