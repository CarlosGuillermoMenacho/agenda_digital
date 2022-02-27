package com.agendadigital.views.modules.contacts;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.agendadigital.R;
import com.agendadigital.clases.Constants;
import com.agendadigital.clases.ConstantsGlobals;
import com.agendadigital.clases.MySingleton;
import com.agendadigital.clases.User;
import com.agendadigital.core.modules.contacts.application.ContactUpdater;
import com.agendadigital.core.modules.contacts.domain.ContactEntity;
import com.agendadigital.core.modules.contacts.domain.ContactTypeEntity;
import com.agendadigital.core.modules.contacts.infrastructure.ContactCourseRepository;
import com.agendadigital.core.modules.contacts.infrastructure.ContactRepository;
import com.agendadigital.core.modules.contacts.infrastructure.ContactTypeRepository;
import com.agendadigital.core.services.contacts.ContactDto;
import com.agendadigital.views.modules.contacts.components.ContactAdapter;
import com.agendadigital.clases.Globals;
import com.agendadigital.views.modules.contacts.components.observers.ContactObservable;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.List;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class ContactFragment extends Fragment {

    private final String TAG = "ContactFragment";
    private View viewFragment;
    private List<ContactEntity> contactEntityList;
    private ContactRepository contactRepository;
    private ContactTypeRepository contactTypeRepository;
    private ContactCourseRepository contactCourseRepository;
    private RecyclerView rvContactList;
    private ProgressBar pbContacts;
    private EditText etContactSearch;
    private ContactAdapter contactAdapter;
    private Disposable contactDisposable;
    private final ContactObservable contactObservable = new ContactObservable();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        viewFragment = inflater.inflate(R.layout.fragment_contacts, container, false);
        contactRepository = new ContactRepository(viewFragment.getContext());
        contactTypeRepository = new ContactTypeRepository(viewFragment.getContext());
        contactCourseRepository = new ContactCourseRepository(viewFragment.getContext());
        rvContactList = viewFragment.findViewById(R.id.rvContactList);
        etContactSearch = viewFragment.findViewById(R.id.etContactSearch);
        initObservable();
        return viewFragment;
    }

    private void initObservable() {
        DividerItemDecoration itemDecoration = new DividerItemDecoration(viewFragment.getContext(), DividerItemDecoration.VERTICAL);
        itemDecoration.setDrawable(getResources().getDrawable(R.drawable.divider));
        rvContactList.setLayoutManager(new LinearLayoutManager(viewFragment.getContext(), LinearLayoutManager.VERTICAL, false));
        rvContactList.addItemDecoration(itemDecoration);
        pbContacts = viewFragment.findViewById(R.id.pbContacts);
        pbContacts.setVisibility(View.GONE);
        contactAdapter = new ContactAdapter();
        rvContactList.setAdapter(contactAdapter);
        contactDisposable =
                contactObservable
                        .getNotificationObservable()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(contactEntity -> {
                            contactAdapter.updateContactMessage(contactEntity);
                        },error -> {
                            Log.d(TAG, "onError: " + error.getMessage());
                        });
        try {
            contactEntityList = contactRepository.findAll();
            if (contactEntityList.size() == 0) {
                getContactsFromServer();
            }
            contactAdapter.setContactEntityList(contactEntityList);
            etContactSearch.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    contactAdapter.getFilter().filter(etContactSearch.getText().toString());
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        contactAdapter.setOnItemClickListener(new ContactAdapter.CustomClickListener() {
            @Override
            public void onClick(int position, View v) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("contact", contactAdapter.getItem(position));
                Navigation.findNavController(requireView()).navigate(R.id.action_fragment_tabchat_contact_to_fragment_chat, bundle);
            }

            @Override
            public void onLongClick(int position, View v) {
                if ((Globals.user.getTipo() == User.UserType.Director && contactAdapter.getItem(position).getContactType() == ContactEntity.ContactType.TeacherAndDirectorGroup)
                        || (Globals.user.getTipo() == User.UserType.Teacher
                        && (contactAdapter.getItem(position).getContactType() == ContactEntity.ContactType.Course
                        || contactAdapter.getItem(position).getContactType() == ContactEntity.ContactType.CourseWithTutors))) {
                    PopupMenu popupMenu = new PopupMenu(viewFragment.getContext(), v);
                    popupMenu.setOnMenuItemClickListener(item -> {
                        if(item.getItemId() == R.id.groupRestrictionsConfig) {
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("contact", contactAdapter.getItem(position));
                            Navigation.findNavController(requireView()).navigate(R.id.action_fragment_tabchat_contact_to_fragment_restrictions, bundle);
                        }
                        return ContactFragment.super.onOptionsItemSelected(item);
                    });
                    popupMenu.inflate(R.menu.popup_group_restrictions_config);
                    popupMenu.show();
                }

            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        contactDisposable.dispose();
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

    private void getContactsFromServer() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        ContactDto.CreateContactRequest contactRequest = new ContactDto.CreateContactRequest(Globals.user.getCodigo(), Globals.user.getTipo().getValue(), Globals.colegio.getCodigo());
        jsonObject.put("user", new JSONObject(contactRequest.toJSON()));
        pbContacts.setVisibility(View.VISIBLE);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,ConstantsGlobals.urlChatServer + "/contacts", jsonObject, response -> {
            Log.d(TAG, "onResponse: " + response);
            try {
                List<ContactDto.CreateContactResponse> contactEntityList = new Gson().fromJson(response.getString("contacts"), new TypeToken<List<ContactDto.CreateContactResponse>>() {
                }.getType());
                pbContacts.setMax(contactEntityList.size());
                int index = 0;
                for (ContactDto.CreateContactResponse contact: contactEntityList) {
                    ContactEntity newContact = new ContactEntity(contact.getId(), contact.getName(), ContactEntity.ContactType.setValue(contact.getContactType()), 0, "", null);
                    if (contactTypeRepository.findById(newContact.getContactType().getValue()) == null) {
                        contactTypeRepository.insert(new ContactTypeEntity(newContact.getContactType().getValue(), newContact.getContactType().getForLabel()));
                    }
                    for(ContactDto.CourseResponse course: contact.getCourses()) {
                        contactCourseRepository.insert(new ContactEntity.ContactCourseEntity(-1, new ContactEntity.CourseEntity(course.getId(), course.getName()), contact.getId(), contact.getContactType()));
                    }
                    long rowsInserted = contactRepository.insert(newContact);
                    if(rowsInserted == -1) {
                        Toast.makeText(viewFragment.getContext(), contact.getName(), Toast.LENGTH_SHORT).show();
                    }else {
                        contactAdapter.add(newContact);
                        pbContacts.setProgress(index++);
                    }
                }
                pbContacts.setVisibility(View.GONE);
                Toast.makeText(viewFragment.getContext(), "Contactos sincronizados (" + contactEntityList.size() + ")", Toast.LENGTH_SHORT).show();

            } catch (Exception e) {
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            } finally {
                pbContacts.setVisibility(View.GONE);
            }
        }, error -> Log.d(TAG, "onErrorResponse: " + error.getMessage()));
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(Constants.MY_DEFAULT_TIMEOUT, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getInstance(getContext()).addToRequest(jsonObjectRequest);
    }

    private void updateContactsFromServer() throws JSONException {
        ContactUpdater.Deleter contactDeleter = new ContactUpdater.Deleter(contactRepository, contactCourseRepository);
        ContactUpdater.Inserter contactInserter = new ContactUpdater.Inserter(contactRepository, contactCourseRepository, contactTypeRepository);
        JSONObject jsonObject = new JSONObject();
        ContactDto.CreateContactRequest contactRequest = new ContactDto.CreateContactRequest(Globals.user.getCodigo(), Globals.user.getTipo().getValue(), Globals.colegio.getCodigo());
        jsonObject.put("user", new JSONObject(contactRequest.toJSON()));
        pbContacts.setVisibility(View.VISIBLE);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,ConstantsGlobals.urlChatServer + "/contacts", jsonObject, response -> {
            Log.d(TAG, "onResponse: " + response);
            try {
                List<ContactDto.CreateContactResponse> contactResponseList = new Gson().fromJson(response.getString("contacts"), new TypeToken<List<ContactDto.CreateContactResponse>>() {
                }.getType());
                pbContacts.setMax(contactResponseList.size());
                List<ContactEntity> actualContactList = contactRepository.findAll();
                contactDeleter.deleteContactAndCoursesNotFound(actualContactList, contactResponseList);
                contactInserter.insertNewContactsAndCourses(actualContactList, contactResponseList);
                contactEntityList = contactRepository.findAll();
                contactAdapter.setContactEntityList(contactEntityList);
                contactAdapter.notifyDataSetChanged();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                pbContacts.setVisibility(View.GONE);
            }
        }, error -> Log.d(TAG, "onErrorResponse: " + error.getMessage()));

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(Constants.MY_DEFAULT_TIMEOUT, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getInstance(getContext()).addToRequest(jsonObjectRequest);
    }

}