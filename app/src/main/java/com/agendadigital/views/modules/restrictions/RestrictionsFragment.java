package com.agendadigital.views.modules.restrictions;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.agendadigital.MainActivity;
import com.agendadigital.R;
import com.agendadigital.clases.Constants;
import com.agendadigital.clases.ConstantsGlobals;
import com.agendadigital.clases.Globals;
import com.agendadigital.clases.MySingleton;
import com.agendadigital.core.modules.contacts.domain.ContactEntity;
import com.agendadigital.core.modules.restrictions.RestrictionType;
import com.agendadigital.core.services.contacts.ContactDto;
import com.agendadigital.core.services.restrictions.RestrictionDto;
import com.agendadigital.views.modules.restrictions.components.adapters.GroupMemberAdapter;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class RestrictionsFragment extends Fragment {

    private String TAG = "RestrictionsFragment";
    private View view;
    private ContactEntity currentContact;
    private List<ContactDto.GroupMemberRestrictionResult> groupMemberResponseList;
    private GroupMemberAdapter groupMemberAdapter;

    private ProgressBar pbRestrictionContacts;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_restrictions, container, false);

        Bundle bundle = getArguments();
        if (bundle != null) {
            currentContact = (ContactEntity) bundle.getSerializable("contact");
            ((MainActivity) getActivity()).getSupportActionBar().setTitle(currentContact.toString());
        }
        init();
        initSwitchEvents();
        getGroupMembersFromServer();
        return view;
    }

    private void init(){
        pbRestrictionContacts = view.findViewById(R.id.pbRestrictionContacts);

        groupMemberAdapter = new GroupMemberAdapter();
        RecyclerView rvRestrictionsContactList = view.findViewById(R.id.rvRestrictionsContactList);
        DividerItemDecoration itemDecoration = new DividerItemDecoration(view.getContext(), DividerItemDecoration.VERTICAL);
        itemDecoration.setDrawable(getResources().getDrawable(R.drawable.divider));
        rvRestrictionsContactList.setLayoutManager(new LinearLayoutManager(view.getContext(), LinearLayoutManager.VERTICAL, false));
        rvRestrictionsContactList.addItemDecoration(itemDecoration);

        rvRestrictionsContactList.setAdapter(groupMemberAdapter);
    }

    private void initSwitchEvents() {
        groupMemberAdapter.setCustomCheckedChangedListener(new GroupMemberAdapter.CustomCheckedChangedListener() {
            @Override
            public void onCheckedChanged(int position, CompoundButton buttonView, boolean isChecked, RestrictionType restrictionType) {
                if (isChecked) {
                    sendDeleteRestrictionRequest(restrictionType, position);
                }else {
                    sendCreateRestrictionRequest(restrictionType, position);
                }
            }
        });

    }

    private void getGroupMembersFromServer() {

        JSONObject jsonObject = new JSONObject();
        ContactDto.GetGroupMembersRequest contactRequest = new ContactDto.GetGroupMembersRequest(Globals.user.getCodigo(), Globals.user.getTipo().getValue(), currentContact.getId(), currentContact.getContactType().getValue());
        try {
            jsonObject.put("group", new JSONObject(contactRequest.toJSON()));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, ConstantsGlobals.urlChatServer + "/contacts/group-members", jsonObject, response -> {
            Log.d(TAG, "onResponse: " + response);
            try {
                groupMemberResponseList = new Gson().fromJson(response.getString("contacts"), new TypeToken<List<ContactDto.GroupMemberRestrictionResult>>() {
                }.getType());
                pbRestrictionContacts.setMax(groupMemberResponseList.size());

                pbRestrictionContacts.setVisibility(View.VISIBLE);

                if (groupMemberResponseList.size() > 0) {
                    groupMemberAdapter.setGroupMemberList(groupMemberResponseList);
                }

                pbRestrictionContacts.setVisibility(View.INVISIBLE);
                Toast.makeText(view.getContext(), "Contactos sincronizados (" + groupMemberResponseList.size() + ")", Toast.LENGTH_SHORT).show();

            } catch (Exception e) {
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }

        }, error -> Log.d(TAG, "onErrorResponse: " + error.getMessage()));

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(Constants.MY_DEFAULT_TIMEOUT, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getInstance(getContext()).addToRequest(jsonObjectRequest);
    }

    private void sendCreateRestrictionRequest(RestrictionType restrictionType, int position) {
        JSONObject jsonObject = new JSONObject();
        ContactDto.GroupMemberRestrictionResult groupMember = groupMemberAdapter.getGroupMemberList().get(position);

        RestrictionDto.CreateUserRestrictionRequest restrictionRequest =
                new RestrictionDto.CreateUserRestrictionRequest(
                        groupMember.getUserToken().getUserId()
                        , restrictionType.getValue()
                        , currentContact.getId()
                        , currentContact.getContactType().getValue()
                        , 1);
        try {
            jsonObject.put("restriction", new JSONObject(restrictionRequest.toJSON()));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, ConstantsGlobals.urlChatServer + "/user-restriction", jsonObject, response -> {
            Log.d(TAG, "onResponse: " + response);
            try {
                RestrictionDto.CreateUserRestrictionResponse restrictionResponse = new Gson().fromJson(response.getString("UserRestriction"), new TypeToken<RestrictionDto.CreateUserRestrictionResponse>() {
                }.getType());
                groupMemberAdapter.addRestrictionToGroupMember(position, new RestrictionDto.CreateUserRestrictionResponse(restrictionResponse.getId(), restrictionType.getValue(), restrictionResponse.getCreatedAt()));

            } catch (Exception e) {
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }

        }, error -> Log.d(TAG, "onErrorResponse: " + error.getMessage()));

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(Constants.MY_DEFAULT_TIMEOUT, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getInstance(getContext()).addToRequest(jsonObjectRequest);
    }
    private void sendDeleteRestrictionRequest(RestrictionType restrictionType, int position) {

        JSONObject jsonObject = new JSONObject();
        ContactDto.GroupMemberRestrictionResult groupMember = groupMemberAdapter.getGroupMemberList().get(position);

        String restrictionId = "";
        int index = 0;
        int indexRestrictionToRemove = -1;
        for (RestrictionDto.CreateUserRestrictionResponse restrictionResult: groupMember.getUserRestrictions()) {
            if (restrictionResult.getRestrictionType() == restrictionType.getValue()) {
                restrictionId = restrictionResult.getId();
                indexRestrictionToRemove = index;
                break;
            }
            index++;
        }
        RestrictionDto.DeleteUserRestrictionRequest restrictionRequest =
                new RestrictionDto.DeleteUserRestrictionRequest(
                        restrictionId,1);
        try {
            jsonObject.put("restriction", new JSONObject(restrictionRequest.toJSON()));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        int finalIndexRestrictionToRemove = indexRestrictionToRemove;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, ConstantsGlobals.urlChatServer + "/user-restriction/delete", jsonObject, response -> {
            Log.d(TAG, "onResponse: " + response);
            try {
                RestrictionDto.DeleteUserRestrictionResponse restrictionResponse = new Gson().fromJson(response.getString("UserRestriction"), new TypeToken<RestrictionDto.DeleteUserRestrictionResponse>() {
                }.getType());
                groupMemberAdapter.removeRestrictionToGroupMember(position, finalIndexRestrictionToRemove);
            } catch (Exception e) {
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }

        }, error -> Log.d(TAG, "onErrorResponse: " + error.getMessage()));

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(Constants.MY_DEFAULT_TIMEOUT, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getInstance(getContext()).addToRequest(jsonObjectRequest);
    }
}
