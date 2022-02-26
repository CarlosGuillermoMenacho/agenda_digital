package com.agendadigital.views.modules.tabchatcontact.components;

import android.view.ViewGroup;

import com.agendadigital.views.modules.contacts.ContactFragment;
import com.agendadigital.views.modules.expandableContacts.ExpandableContactsFragment;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class TabChatPagerAdapter extends FragmentPagerAdapter {

    Fragment fragment;

    public TabChatPagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {

        if (position == 0) {
            fragment = new ContactFragment();
        } else {
            fragment = new ExpandableContactsFragment();
        }
        return fragment;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        super.destroyItem(container, position, object);
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
