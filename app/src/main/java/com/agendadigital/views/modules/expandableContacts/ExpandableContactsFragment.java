package com.agendadigital.views.modules.expandableContacts;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ConcatAdapter;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import com.agendadigital.R;
import com.agendadigital.core.modules.contacts.application.ContactTypeCoursesFinder;
import com.agendadigital.core.modules.contacts.domain.ContactTypeEntity;
import com.agendadigital.core.modules.contacts.infrastructure.ContactCourseRepository;
import com.agendadigital.core.modules.contacts.infrastructure.ContactTypeRepository;
import com.agendadigital.views.modules.expandableContacts.components.ExpandableContactAdapter;
import java.util.ArrayList;
import java.util.List;

public class ExpandableContactsFragment extends Fragment {

    private final String TAG = "ExpandableFragment";
    private ContactTypeCoursesFinder contactTypeCoursesFinder;
    private List<ExpandableContactAdapter> expandableContactAdapterList;
    private RecyclerView rvContactExpandable;
    private ProgressBar pbExpandableContacts;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: OK" );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: OK" );
        return inflater.inflate(R.layout.fragment_expandable_contacts, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        expandableContactAdapterList = new ArrayList<>();
        initList();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated: OK" );
        expandableContactAdapterList = new ArrayList<>();
        contactTypeCoursesFinder = new ContactTypeCoursesFinder(new ContactTypeRepository(view.getContext()), new ContactCourseRepository(view.getContext()));
        DividerItemDecoration itemDecoration = new DividerItemDecoration(view.getContext(), DividerItemDecoration.VERTICAL);
        itemDecoration.setDrawable(getResources().getDrawable(R.drawable.divider));
        rvContactExpandable = view.findViewById(R.id.rvContactExpandable);
        rvContactExpandable.setLayoutManager(new LinearLayoutManager(view.getContext(), LinearLayoutManager.VERTICAL, false));
        rvContactExpandable.addItemDecoration(itemDecoration);
        pbExpandableContacts = view.findViewById(R.id.pbExpandableContacts);
        initList();
    }

    private void initList() {
        try {
            pbExpandableContacts.setVisibility(View.VISIBLE);
            List<ContactTypeEntity.ContactTypeCourses> contactTypeEntityList = contactTypeCoursesFinder.findAll();
            for (ContactTypeEntity.ContactTypeCourses contactTypeCourses: contactTypeEntityList) {
                ExpandableContactAdapter expandableContactAdapter = new ExpandableContactAdapter(contactTypeCourses);
                expandableContactAdapterList.add(expandableContactAdapter);
            }
            ConcatAdapter.Config concatAdapterConfig = new ConcatAdapter.Config.Builder()
                    .setIsolateViewTypes(false)
                    .build();
            ConcatAdapter concatAdapter = new ConcatAdapter(concatAdapterConfig, expandableContactAdapterList);
            rvContactExpandable.setAdapter(concatAdapter);
            pbExpandableContacts.setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pbExpandableContacts.setVisibility(View.GONE);
        }
    }

}