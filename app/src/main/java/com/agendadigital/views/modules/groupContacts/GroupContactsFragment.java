package com.agendadigital.views.modules.groupContacts;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import com.agendadigital.MainActivity;
import com.agendadigital.R;
import com.agendadigital.core.modules.contacts.application.ContactFinder;
import com.agendadigital.core.modules.contacts.domain.ContactEntity;
import com.agendadigital.core.modules.contacts.infrastructure.ContactCourseRepository;
import com.agendadigital.core.modules.contacts.infrastructure.ContactRepository;
import com.agendadigital.views.modules.contacts.components.ContactAdapter;
import com.agendadigital.views.modules.groupContacts.components.GroupContactAdapter;
import java.util.List;

public class GroupContactsFragment extends Fragment {

    private View view;
    private RecyclerView rvGroupContactList;
    private GroupContactAdapter groupContactAdapter;
    private ProgressBar pbGroupContacts;
    private EditText etGroupContactSearch;

    private ContactFinder contactFinder;

    private ContactEntity.CourseEntity currentCourseEntity;
    int currentContactType;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        Bundle bundle = getArguments();
        if (bundle != null) {
            currentCourseEntity = (ContactEntity.CourseEntity) bundle.getSerializable("course");
            currentContactType = bundle.getInt("contactType");
            ((MainActivity) getActivity()).getSupportActionBar().setTitle(currentCourseEntity.getCourseDescription());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_group_contacts, container, false);
        rvGroupContactList = view.findViewById(R.id.rvGroupContactList);
        pbGroupContacts = view.findViewById(R.id.pbGroupContacts);
        loadGroupContacts();
        initSearcher();
        return view;
    }

    private void loadGroupContacts() {
        DividerItemDecoration itemDecoration = new DividerItemDecoration(view.getContext(), DividerItemDecoration.VERTICAL);
        itemDecoration.setDrawable(getResources().getDrawable(R.drawable.divider));
        rvGroupContactList.setLayoutManager(new LinearLayoutManager(view.getContext(), LinearLayoutManager.VERTICAL, false));
        rvGroupContactList.addItemDecoration(itemDecoration);
        groupContactAdapter = new GroupContactAdapter();

        contactFinder = new ContactFinder(new ContactCourseRepository(view.getContext()), new ContactRepository(view.getContext()));
        try {
            pbGroupContacts.setVisibility(View.VISIBLE);
            List<ContactEntity> contactEntityList = contactFinder.findAllContactsByCourseAndType(currentCourseEntity.getCourseId(), currentContactType);
            groupContactAdapter.setContactEntityList(contactEntityList);
            rvGroupContactList.setAdapter(groupContactAdapter);
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
            pbGroupContacts.setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pbGroupContacts.setVisibility(View.GONE);
        }
    }

    private void initSearcher() {
        etGroupContactSearch = view.findViewById(R.id.etGroupContactSearch);
        etGroupContactSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                groupContactAdapter.getFilter().filter(etGroupContactSearch.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem dark = menu.findItem(R.id.action_darkTheme);
        MenuItem light = menu.findItem(R.id.action_lightTheme);
        if ( dark != null) {
            dark.setVisible(false);
        }
        if ( light != null) {
            light.setVisible(false);
        }
    }
}