package com.agendadigital.views.modules.contacts;

import android.content.ContentValues;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.agendadigital.R;
import com.agendadigital.clases.Constants;
import com.agendadigital.clases.ConstantsGlobals;
import com.agendadigital.clases.MySingleton;
import com.agendadigital.clases.User;
import com.agendadigital.core.modules.contacts.domain.ContactEntity;
import com.agendadigital.core.modules.contacts.infrastructure.ContactRepository;
import com.agendadigital.core.modules.messages.domain.MessageEntity;
import com.agendadigital.core.services.contacts.ContactDto;
import com.agendadigital.core.shared.infrastructure.AsyncHttpRest;
import com.agendadigital.views.modules.chats.ChatFragment;
import com.agendadigital.views.modules.chats.components.observers.MessageObservable;
import com.agendadigital.views.modules.contacts.components.ContactAdapter;
import com.agendadigital.clases.Globals;
import com.agendadigital.views.modules.contacts.components.observers.ContactObservable;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.JsonHttpResponseHandler;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import cz.msebera.android.httpclient.Header;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static android.widget.LinearLayout.HORIZONTAL;

public class ContactFragment extends Fragment {

    private final String TAG = "ContactFragment";
    private View viewFragment;
    private List<ContactEntity> contactEntityList;
    private ContactRepository contactRepository;
    private ProgressBar pbContacts;
    private ContactAdapter contactAdapter;
    private final ContactObservable contactObservable = new ContactObservable();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        viewFragment = inflater.inflate(R.layout.fragment_contacts, container, false);
        contactRepository = new ContactRepository(viewFragment.getContext());
        RecyclerView rvContactList = viewFragment.findViewById(R.id.rvContactList);
        DividerItemDecoration itemDecoration = new DividerItemDecoration(viewFragment.getContext(), DividerItemDecoration.VERTICAL);
        itemDecoration.setDrawable(getResources().getDrawable(R.drawable.divider));
        rvContactList.setLayoutManager(new LinearLayoutManager(viewFragment.getContext(), LinearLayoutManager.VERTICAL, false));
        rvContactList.addItemDecoration(itemDecoration);
        pbContacts = viewFragment.findViewById(R.id.pbContacts);
        contactAdapter = new ContactAdapter();
        rvContactList.setAdapter(contactAdapter);
        contactObservable
                .getNotificationObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ContactEntity>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.d(TAG, "onSubscribe: ");
                    }

                    @Override
                    public void onNext(ContactEntity contactEntity) {
                        contactAdapter.updateContactMessage(contactEntity);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "onError: " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "onComplete: ");
                    }
                });
        try {
            getContactsFromDatabase();
            if (contactEntityList.size() == 0) {
                getContactsFromServer();
            }
            contactAdapter.setContactEntityList(contactEntityList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        contactAdapter.setOnItemClickListener(new ContactAdapter.CustomClickListener() {
            @Override
            public void onClick(int position, View v) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("contact", contactEntityList.get(position));
                Navigation.findNavController(requireView()).navigate(R.id.action_fragment_contacts_to_fragment_chat, bundle);
            }

            @Override
            public void onLongClick(int position, View v) {

                if ((Globals.user.getTipo() == User.UserType.Director && contactEntityList.get(position).getContactType() == ContactEntity.ContactType.TeacherAndDirectorGroup)
                        || (Globals.user.getTipo() == User.UserType.Teacher
                            && (contactEntityList.get(position).getContactType() == ContactEntity.ContactType.Course
                                || contactEntityList.get(position).getContactType() == ContactEntity.ContactType.CourseWithTutors))) {
                    PopupMenu popupMenu = new PopupMenu(viewFragment.getContext(), v);
                    popupMenu.setOnMenuItemClickListener(item -> {
                        if(item.getItemId() == R.id.groupRestrictionsConfig) {
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("contact", contactEntityList.get(position));
                            Navigation.findNavController(requireView()).navigate(R.id.action_fragment_contacts_to_fragment_restrictions, bundle);
                        }
                        return ContactFragment.super.onOptionsItemSelected(item);
                    });
                    popupMenu.inflate(R.menu.popup_group_restrictions_config);
                    popupMenu.show();
                }

            }
        });
        return viewFragment;
    }

    private void getContactsFromDatabase() throws Exception {
        pbContacts.setVisibility(View.VISIBLE);
        contactEntityList = contactRepository.findAll();
        pbContacts.setVisibility(View.INVISIBLE);
    }

    private void getContactsFromServer() throws JSONException {

        JSONObject jsonObject = new JSONObject();
        ContactDto.CreateContactRequest contactRequest = new ContactDto.CreateContactRequest(Globals.user.getCodigo(), Globals.user.getTipo().getValue());
        jsonObject.put("user", new JSONObject(contactRequest.toJSON()));
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,ConstantsGlobals.urlChatServer + "/contacts", jsonObject, response -> {
            Log.d(TAG, "onResponse: " + response);
            try {
                List<ContactDto.CreateContactResponse> contactEntityList = new Gson().fromJson(response.getString("contacts"), new TypeToken<List<ContactDto.CreateContactResponse>>() {
                }.getType());
                pbContacts.setMax(contactEntityList.size());
                int index = 0;
                pbContacts.setVisibility(View.VISIBLE);
                for (ContactDto.CreateContactResponse contact: contactEntityList) {
                    ContactEntity newContact = new ContactEntity(contact.getId(), contact.getName(), ContactEntity.ContactType.setValue(contact.getContactType()), 0, "", null);
                    long rowsInserted = contactRepository.insert(newContact);
                    if(rowsInserted == -1) {
                        Toast.makeText(viewFragment.getContext(), contact.getName(), Toast.LENGTH_SHORT).show();
                    }else {
                        contactAdapter.add(newContact);
                        pbContacts.setProgress(index++);
                    }
                }
                pbContacts.setVisibility(View.INVISIBLE);
                Toast.makeText(viewFragment.getContext(), "Contactos sincronizados (" + contactEntityList.size() + ")", Toast.LENGTH_SHORT).show();

            } catch (Exception e) {
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }

        }, error -> Log.d(TAG, "onErrorResponse: " + error.getMessage()));

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(Constants.MY_DEFAULT_TIMEOUT, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getInstance(getContext()).addToRequest(jsonObjectRequest);
    }
}