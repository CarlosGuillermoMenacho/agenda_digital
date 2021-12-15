package com.agendadigital.views.modules.contacts;

import android.content.ContentValues;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.agendadigital.R;
import com.agendadigital.core.modules.contacts.domain.ContactEntity;
import com.agendadigital.core.modules.contacts.infrastructure.ContactRepository;
import com.agendadigital.core.services.contacts.ContactDto;
import com.agendadigital.core.shared.infrastructure.AsyncHttpRest;
import com.agendadigital.views.modules.contacts.components.ContactAdapter;
import com.agendadigital.clases.Globals;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.JsonHttpResponseHandler;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.UnsupportedEncodingException;
import java.util.List;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import cz.msebera.android.httpclient.Header;

public class ContactFragment extends Fragment {

    private final String TAG = "ContactFragment";
    private View viewFragment;
    private List<ContactEntity> contactEntityList;
    private ContactRepository contactRepository;
    private ProgressBar pbContacts;
    private ContactAdapter contactAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        viewFragment = inflater.inflate(R.layout.fragment_contacts, container, false);
        contactRepository = new ContactRepository(viewFragment.getContext());
        RecyclerView rvContactList = viewFragment.findViewById(R.id.rvContactList);
        rvContactList.setLayoutManager(new LinearLayoutManager(viewFragment.getContext()));
        rvContactList.addItemDecoration(new DividerItemDecoration(viewFragment.getContext(), DividerItemDecoration.HORIZONTAL));
        pbContacts = viewFragment.findViewById(R.id.pbContacts);
        contactAdapter = new ContactAdapter();
        rvContactList.setAdapter(contactAdapter);
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
                Toast.makeText(viewFragment.getContext(), "OnClick", Toast.LENGTH_SHORT).show();
                Bundle bundle = new Bundle();
                bundle.putSerializable("contact", contactEntityList.get(position));
                Navigation.findNavController(requireView()).navigate(R.id.action_fragment_contacts_to_fragment_chat, bundle);
            }

            @Override
            public void onLongClick(int position, View v) {
                Toast.makeText(viewFragment.getContext(), "OnLongClick", Toast.LENGTH_SHORT).show();
            }
        });
        return viewFragment;
    }

    private void getContactsFromDatabase() throws Exception {
        pbContacts.setVisibility(View.VISIBLE);
        contactEntityList =  contactRepository.findAll();
        pbContacts.setVisibility(View.INVISIBLE);
    }

    private void getContactsFromServer() throws UnsupportedEncodingException, JSONException {
        JSONObject params = new JSONObject();
        ContactDto.CreateContactRequest createContactRequest = new ContactDto.CreateContactRequest(Globals.user.getCodigo(), Globals.user.getTipo().getValue());
        params.put("userId", new Gson().toJson(createContactRequest));

        AsyncHttpRest.post(viewFragment.getContext(), "/contacts", params, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Log.d(TAG, "onSuccess: JSONObject" + response);
                try {
                    List<ContactDto.CreateContactResponse> contactEntityList = new Gson().fromJson(response.getString("contacts"), new TypeToken<List<ContactDto.CreateContactResponse>>() {
                            }.getType());
                    pbContacts.setMax(contactEntityList.size());
                    int index = 0;
//                    db.beginTransaction();
                    pbContacts.setVisibility(View.VISIBLE);
                    for (ContactDto.CreateContactResponse contact: contactEntityList) {
                        ContentValues values = new ContentValues();
                        long rowsInserted = contactRepository.insert(new ContactEntity(contact.getId(), contact.getName(), ContactEntity.ContactType.setValue(contact.getTypeContact())));
//                        values.put(FeedReaderContract.FeedContact._ID, contact.getId());
//                        values.put(FeedReaderContract.FeedContact.COL_NAME, contact.getName());
//                        values.put(FeedReaderContract.FeedContact.COL_TYPE_CONTACT, contact.getTypeContact());
//                        long rowsInserted = db.insert(FeedReaderContract.FeedContact.TABLE_NAME, null, values);
                        if(rowsInserted == -1) {
                            Toast.makeText(viewFragment.getContext(), contact.getName(), Toast.LENGTH_SHORT).show();
                            throw new Exception("Error guardando el contacto : ".concat(contact.getName()));
                        }else {
                            contactAdapter.add(new ContactEntity(contact.getId(), contact.getName(), ContactEntity.ContactType.setValue(contact.getTypeContact())));
                            pbContacts.setProgress(index++);
                        }
                    }
                    pbContacts.setVisibility(View.INVISIBLE);
                    Toast.makeText(viewFragment.getContext(), "Contactos sincronizados (" + contactEntityList.size() + ")", Toast.LENGTH_SHORT).show();
                    //                    db.endTransaction();

                } catch (JSONException e) {
                    Toast.makeText(viewFragment.getContext(), "Error al sincronizar los contactos.", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                } catch (Exception e) {
                    Toast.makeText(viewFragment.getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Log.d(TAG, "onFailure:" + throwable);
            }
        });
    }
}