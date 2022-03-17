package com.agendadigital.views.modules.groupContacts;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.agendadigital.R;
import com.agendadigital.core.modules.contacts.application.ContactFinder;
import com.agendadigital.core.modules.contacts.domain.ContactEntity;
import com.agendadigital.core.modules.contacts.infrastructure.ContactGroupRepository;
import com.agendadigital.core.modules.contacts.infrastructure.ContactRepository;
import com.agendadigital.databinding.FragmentGroupContactsBinding;
import com.agendadigital.views.modules.contacts.components.ContactAdapter;
import com.agendadigital.views.modules.groupContacts.components.GroupContactAdapter;
import com.agendadigital.views.shared.infrastructure.ViewHelpers;
import java.util.List;

public class GroupContactsFragment extends Fragment {

    private FragmentGroupContactsBinding binding;
    private Context context;
    private GroupContactAdapter groupContactAdapter;

    private ContactEntity.GroupEntity currentGroupEntity;
    int currentContactType;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            currentGroupEntity = (ContactEntity.GroupEntity) bundle.getSerializable("course");
            currentContactType = bundle.getInt("contactType");
            ActionBar actionBar = ViewHelpers.getActionBar(getActivity());
            if (actionBar != null) {
                actionBar.setTitle(currentGroupEntity.getCourseDescription());
            }
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentGroupContactsBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        context = view.getContext();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try {
            loadGroupContacts();
            initSearcher();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            loadGroupContacts();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadGroupContacts() throws Exception {
        ViewHelpers.initRecyclerView(context, binding.rvGroupContactList);
        groupContactAdapter = new GroupContactAdapter();
        ContactFinder contactFinder = new ContactFinder(new ContactGroupRepository(context), new ContactRepository(context));
        try {
            binding.pbGroupContacts.setVisibility(View.VISIBLE);
            List<ContactEntity> contactEntityList = contactFinder.findAllContactsByCourseAndType(currentGroupEntity.getCourseId(), currentContactType);
            groupContactAdapter.setContactEntityList(contactEntityList);
            binding.rvGroupContactList.setAdapter(groupContactAdapter);
            groupContactAdapter.setOnItemClickListener(new ContactAdapter.CustomClickListener() {
                @Override
                public void onClick(int position, View v) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("contact", groupContactAdapter.getItem(position));
                    Navigation.findNavController(requireView()).navigate(R.id.action_fragment_group_contacts_to_fragment_chat, bundle);
                }

                @Override
                public void onLongClick(int position, View v) { }
            });
            binding.pbGroupContacts.setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            binding.pbGroupContacts.setVisibility(View.GONE);
        }
    }

    private void initSearcher() {
        binding.etGroupContactSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                groupContactAdapter.getFilter().filter(binding.etGroupContactSearch.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

}