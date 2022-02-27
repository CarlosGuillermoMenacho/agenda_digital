package com.agendadigital.views.modules.groupContacts;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import com.agendadigital.MainActivity;
import com.agendadigital.R;
import com.agendadigital.clases.Constants;
import com.agendadigital.clases.ConstantsGlobals;
import com.agendadigital.clases.Globals;
import com.agendadigital.clases.MySingleton;
import com.agendadigital.core.modules.contacts.application.ContactFinder;
import com.agendadigital.core.modules.contacts.domain.ContactEntity;
import com.agendadigital.core.modules.contacts.infrastructure.ContactCourseRepository;
import com.agendadigital.core.modules.contacts.infrastructure.ContactRepository;
import com.agendadigital.core.services.contacts.ContactDto;
import com.agendadigital.databinding.FragmentGroupContactsBinding;
import com.agendadigital.views.modules.contacts.components.ContactAdapter;
import com.agendadigital.views.modules.groupContacts.components.GroupContactAdapter;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.List;

public class GroupContactsFragment extends Fragment {

    private final String TAG = "GroupContactsFragment";
    private FragmentGroupContactsBinding binding;
    private GroupContactAdapter groupContactAdapter;

    private ContactEntity.CourseEntity currentCourseEntity;
    int currentContactType;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            currentCourseEntity = (ContactEntity.CourseEntity) bundle.getSerializable("course");
            currentContactType = bundle.getInt("contactType");
            ((MainActivity) getActivity()).getSupportActionBar().setTitle(currentCourseEntity.getCourseDescription());
        }
    }

    @Override
    public void onResume() {
        loadGroupContacts();
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentGroupContactsBinding.inflate(inflater, container, false);
        loadGroupContacts();
        initSearcher();
        return binding.getRoot();
    }

    private void loadGroupContacts() {
        DividerItemDecoration itemDecoration = new DividerItemDecoration(binding.getRoot().getContext(), DividerItemDecoration.VERTICAL);
        itemDecoration.setDrawable(getResources().getDrawable(R.drawable.divider));
        binding.rvGroupContactList.setLayoutManager(new LinearLayoutManager(binding.getRoot().getContext(), LinearLayoutManager.VERTICAL, false));
        binding.rvGroupContactList.addItemDecoration(itemDecoration);
        groupContactAdapter = new GroupContactAdapter();

        ContactFinder contactFinder = new ContactFinder(new ContactCourseRepository(binding.getRoot().getContext()), new ContactRepository(binding.getRoot().getContext()));
        try {
            binding.pbGroupContacts.setVisibility(View.VISIBLE);
            List<ContactEntity> contactEntityList = contactFinder.findAllContactsByCourseAndType(currentCourseEntity.getCourseId(), currentContactType);
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