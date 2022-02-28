package com.agendadigital.views.modules.restrictions;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.agendadigital.R;
import com.agendadigital.clases.Constants;
import com.agendadigital.clases.ConstantsGlobals;
import com.agendadigital.clases.Globals;
import com.agendadigital.clases.MySingleton;
import com.agendadigital.core.modules.contacts.domain.ContactEntity;
import com.agendadigital.core.modules.restrictions.RestrictionType;
import com.agendadigital.core.services.contacts.ContactDto;
import com.agendadigital.core.services.restrictions.RestrictionDto;
import com.agendadigital.databinding.FragmentRestrictionsBinding;
import com.agendadigital.views.modules.restrictions.components.adapters.GroupMemberAdapter;
import com.agendadigital.views.shared.infrastructure.ViewHelpers;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.List;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;

public class RestrictionsFragment extends Fragment {

    private FragmentRestrictionsBinding binding;
    private Context context;
    private ContactEntity currentContact;
    private List<ContactDto.GroupMemberRestrictionResult> groupMemberResponseList;
    private GroupMemberAdapter groupMemberAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            currentContact = (ContactEntity) bundle.getSerializable("contact");
            ActionBar actionBar = ViewHelpers.getActionBar(getActivity());
            if (actionBar != null) {
                actionBar.setTitle(currentContact.toString());
            }
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentRestrictionsBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        context = view.getContext();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try {
            initRecyclerView();
            initSwitchEvents();
            getGroupMembersFromServer();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initRecyclerView() throws Exception {
        ViewHelpers.initRecyclerView(context, binding.rvRestrictionsContactList);
        groupMemberAdapter = new GroupMemberAdapter();
        binding.rvRestrictionsContactList.setAdapter(groupMemberAdapter);
    }

    private void initSwitchEvents() {
        groupMemberAdapter.setCustomCheckedChangedListener((position, buttonView, isChecked, restrictionType) -> {
            if (isChecked) {
                sendDeleteRestrictionRequest(restrictionType, position);
            }else {
                sendCreateRestrictionRequest(restrictionType, position);
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
            try {
                groupMemberResponseList = new Gson().fromJson(response.getString("contacts"), new TypeToken<List<ContactDto.GroupMemberRestrictionResult>>() {
                }.getType());
                binding.pbRestrictionContacts.setMax(groupMemberResponseList.size());
                binding.pbRestrictionContacts.setVisibility(View.VISIBLE);

                if (groupMemberResponseList.size() > 0) {
                    groupMemberAdapter.setGroupMemberList(groupMemberResponseList);
                }
                binding.pbRestrictionContacts.setVisibility(View.INVISIBLE);
                Toast.makeText(context, String.format(context.getString(R.string.sync_contacts_amount), groupMemberResponseList.size()), Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }, error -> Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show());
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
            try {
                RestrictionDto.CreateUserRestrictionResponse restrictionResponse = new Gson().fromJson(response.getString("UserRestriction"), new TypeToken<RestrictionDto.CreateUserRestrictionResponse>() {
                }.getType());
                groupMemberAdapter.addRestrictionToGroupMember(position, new RestrictionDto.CreateUserRestrictionResponse(restrictionResponse.getId(), restrictionType.getValue(), restrictionResponse.getCreatedAt()));
            } catch (Exception e) {
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }, error -> Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show());

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
                new RestrictionDto.DeleteUserRestrictionRequest(restrictionId,1);
        try {
            jsonObject.put("restriction", new JSONObject(restrictionRequest.toJSON()));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        int finalIndexRestrictionToRemove = indexRestrictionToRemove;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, ConstantsGlobals.urlChatServer + "/user-restriction/delete", jsonObject, response -> {
            try {
                groupMemberAdapter.removeRestrictionToGroupMember(position, finalIndexRestrictionToRemove);
            } catch (Exception e) {
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }

        }, error -> Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show());

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(Constants.MY_DEFAULT_TIMEOUT, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getInstance(getContext()).addToRequest(jsonObjectRequest);
    }

}
