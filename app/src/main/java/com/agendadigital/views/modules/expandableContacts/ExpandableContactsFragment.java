package com.agendadigital.views.modules.expandableContacts;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ConcatAdapter;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.agendadigital.R;
import com.agendadigital.clases.Constants;
import com.agendadigital.clases.ConstantsGlobals;
import com.agendadigital.clases.Globals;
import com.agendadigital.clases.MySingleton;
import com.agendadigital.core.modules.contacts.application.ContactTypeCoursesFinder;
import com.agendadigital.core.modules.contacts.application.ContactUpdater;
import com.agendadigital.core.modules.contacts.domain.ContactEntity;
import com.agendadigital.core.modules.contacts.domain.ContactTypeEntity;
import com.agendadigital.core.modules.contacts.infrastructure.ContactCourseRepository;
import com.agendadigital.core.modules.contacts.infrastructure.ContactRepository;
import com.agendadigital.core.modules.contacts.infrastructure.ContactTypeRepository;
import com.agendadigital.core.services.contacts.ContactDto;
import com.agendadigital.databinding.FragmentExpandableContactsBinding;
import com.agendadigital.views.modules.expandableContacts.components.ExpandableContactAdapter;
import com.agendadigital.views.shared.infrastructure.ViewHelpers;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class ExpandableContactsFragment extends Fragment {

    private FragmentExpandableContactsBinding binding;
    private Context context;
    private ContactTypeCoursesFinder contactTypeCoursesFinder;
    private List<ExpandableContactAdapter> expandableContactAdapterList;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        binding = FragmentExpandableContactsBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        context = view.getContext();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        contactTypeCoursesFinder = new ContactTypeCoursesFinder(new ContactTypeRepository(context), new ContactCourseRepository(context));
        try {
            ViewHelpers.initRecyclerView(context, binding.rvContactExpandable);
            loadRecyclerView();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        expandableContactAdapterList = new ArrayList<>();
        loadRecyclerView();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        MenuItem updateContacts = menu.findItem(R.id.action_updateContacts);
        if (updateContacts != null)
            updateContacts.setVisible(true);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_updateContacts) {
            try {
                updateContactsFromServer();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadRecyclerView() {
        try {
            expandableContactAdapterList = new ArrayList<>();
            binding.pbExpandableContacts.setVisibility(View.VISIBLE);
            List<ContactTypeEntity.ContactTypeCourses> contactTypeEntityList = contactTypeCoursesFinder.findAll();
            for (ContactTypeEntity.ContactTypeCourses contactTypeCourses: contactTypeEntityList) {
                ExpandableContactAdapter expandableContactAdapter = new ExpandableContactAdapter(contactTypeCourses);
                expandableContactAdapterList.add(expandableContactAdapter);
            }
            ConcatAdapter.Config concatAdapterConfig = new ConcatAdapter.Config.Builder()
                    .setIsolateViewTypes(false)
                    .build();
            ConcatAdapter concatAdapter = new ConcatAdapter(concatAdapterConfig, expandableContactAdapterList);
            binding.rvContactExpandable.setAdapter(concatAdapter);
            binding.pbExpandableContacts.setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            binding.pbExpandableContacts.setVisibility(View.GONE);
        }
    }

    private void updateContactsFromServer() throws JSONException {
        ContactRepository contactRepository = new ContactRepository(getContext());
        ContactCourseRepository contactCourseRepository = new ContactCourseRepository(getContext());
        ContactTypeRepository contactTypeRepository = new ContactTypeRepository(getContext());
        ContactUpdater.Deleter contactDeleter = new ContactUpdater.Deleter(contactRepository, contactCourseRepository);
        ContactUpdater.Inserter contactInserter = new ContactUpdater.Inserter(contactRepository, contactCourseRepository, contactTypeRepository);

        JSONObject jsonObject = new JSONObject();
        ContactDto.CreateContactRequest contactRequest = new ContactDto.CreateContactRequest(Globals.user.getCodigo(), Globals.user.getTipo().getValue(), Globals.colegio.getCodigo());
        jsonObject.put("user", new JSONObject(contactRequest.toJSON()));
        binding.pbExpandableContacts.setVisibility(View.VISIBLE);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,ConstantsGlobals.urlChatServer + "/contacts", jsonObject, response -> {
            try {
                List<ContactDto.CreateContactResponse> contactResponseList = new Gson().fromJson(response.getString("contacts"), new TypeToken<List<ContactDto.CreateContactResponse>>() {
                }.getType());
                binding.pbExpandableContacts.setMax(contactResponseList.size());
                List<ContactEntity> actualContactList = contactRepository.findAll();
                contactDeleter.deleteContactAndCoursesNotFound(actualContactList, contactResponseList);
                contactInserter.insertNewContactsAndCourses(actualContactList, contactResponseList);
                expandableContactAdapterList = new ArrayList<>();
                loadRecyclerView();
                binding.pbExpandableContacts.setVisibility(View.GONE);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                binding.pbExpandableContacts.setVisibility(View.GONE);
            }
        }, error -> Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show());

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(Constants.MY_DEFAULT_TIMEOUT, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getInstance(getContext()).addToRequest(jsonObjectRequest);
    }

}