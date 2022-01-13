package com.agendadigital.views.modules.restrictions.components.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;
import com.agendadigital.R;
import com.agendadigital.clases.User;
import com.agendadigital.core.modules.restrictions.RestrictionType;
import com.agendadigital.core.services.contacts.ContactDto;
import com.agendadigital.core.services.restrictions.RestrictionDto;
import java.util.ArrayList;
import java.util.List;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.RecyclerView;

public class GroupMemberAdapter extends RecyclerView.Adapter<GroupMemberAdapter.GroupMemberViewHolder> {

    private CustomCheckedChangedListener customCheckedChangedListener;
    private List<ContactDto.GroupMemberRestrictionResult> groupMemberList;

    public GroupMemberAdapter(List<ContactDto.GroupMemberRestrictionResult> groupMemberList) {
        this.groupMemberList = groupMemberList;
    }

    public GroupMemberAdapter() {
        this.groupMemberList = new ArrayList<>();
    }

    public void setGroupMemberList(List<ContactDto.GroupMemberRestrictionResult> groupMemberList) {
        this.groupMemberList = groupMemberList;
        notifyDataSetChanged();
    }

    public List<ContactDto.GroupMemberRestrictionResult> getGroupMemberList() {
        return groupMemberList;
    }

    public void addRestrictionToGroupMember(int index, RestrictionDto.CreateUserRestrictionResponse restrictionResponse) {
        boolean exists = false;
        for (RestrictionDto.CreateUserRestrictionResponse restriction: groupMemberList.get(index).getUserRestrictions()) {
            if (restriction.getId().equals(restrictionResponse.getId())) {
                exists = true;
                break;
            }
        }
        if (!exists) {
            this.groupMemberList.get(index).getUserRestrictions().add(restrictionResponse);
            notifyDataSetChanged();
        }
    }

    public void removeRestrictionToGroupMember(int indexGroupMember, int indexRestriction) {
        this.groupMemberList.get(indexGroupMember).getUserRestrictions().remove(indexRestriction);
        notifyDataSetChanged();
    }

    public void setCustomCheckedChangedListener(CustomCheckedChangedListener customCheckedChangedListener) {
        this.customCheckedChangedListener = customCheckedChangedListener;
    }

    @NonNull
    @Override
    public GroupMemberAdapter.GroupMemberViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = null;
        LayoutInflater inflater = (LayoutInflater)
                parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (inflater != null) {
            view = inflater.inflate(R.layout.item_contact_restrictions, parent, false);
        }
        return new GroupMemberAdapter.GroupMemberViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupMemberAdapter.GroupMemberViewHolder holder, int position) {
        try {
            holder.set(groupMemberList.get(position));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return groupMemberList.size();
    }

    public class GroupMemberViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvRestrictionContactName;
        private final TextView tvRestrictionTypeContact;
        private final SwitchCompat scRestrictionText;
        private final SwitchCompat scRestrictionAudio;
        private final SwitchCompat scRestrictionImage;
        private final SwitchCompat scRestrictionVideo;
        private final SwitchCompat scRestrictionDocument;

        public GroupMemberViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRestrictionContactName = itemView.findViewById(R.id.tvRestrictionContactName);
            tvRestrictionTypeContact = itemView.findViewById(R.id.tvRestrictionTypeContact);
            scRestrictionText = itemView.findViewById(R.id.scRestrictionText);
            scRestrictionAudio = itemView.findViewById(R.id.scRestrictionAudio);
            scRestrictionImage = itemView.findViewById(R.id.scRestrictionImage);
            scRestrictionVideo = itemView.findViewById(R.id.scRestrictionVideo);
            scRestrictionDocument = itemView.findViewById(R.id.scRestrictionDocument);

            scRestrictionText.setOnCheckedChangeListener((buttonView, isChecked) ->
                    customCheckedChangedListener.onCheckedChanged(getAdapterPosition(), buttonView, isChecked, RestrictionType.Mute));
            scRestrictionAudio.setOnCheckedChangeListener((buttonView, isChecked) ->
                    customCheckedChangedListener.onCheckedChanged(getAdapterPosition(), buttonView, isChecked, RestrictionType.SendAudio));
            scRestrictionImage.setOnCheckedChangeListener((buttonView, isChecked) ->
                    customCheckedChangedListener.onCheckedChanged(getAdapterPosition(), buttonView, isChecked, RestrictionType.SendImages));
            scRestrictionVideo.setOnCheckedChangeListener((buttonView, isChecked) ->
                    customCheckedChangedListener.onCheckedChanged(getAdapterPosition(), buttonView, isChecked, RestrictionType.SendVideos));
            scRestrictionDocument.setOnCheckedChangeListener((buttonView, isChecked) ->
                    customCheckedChangedListener.onCheckedChanged(getAdapterPosition(), buttonView, isChecked, RestrictionType.SendDocuments));
        }

        public void set(ContactDto.GroupMemberRestrictionResult groupMember) throws Exception {
            tvRestrictionContactName.setText(groupMember.getUserToken().getName());
            tvRestrictionTypeContact.setText(User.UserType.setValue(groupMember.getUserToken().getUserType()).toString());
            boolean existsMute = false;
            boolean existsSendImages = false;
            boolean existsSendVideos = false;
            boolean existsSendDocuments = false;
            boolean existsSendAudio = false;
            for(RestrictionDto.CreateUserRestrictionResponse restriction: groupMember.getUserRestrictions()) {
                switch (RestrictionType.setValue(restriction.getRestrictionType())) {
                    case Mute:
                        scRestrictionText.setChecked(false);
                        existsMute = true;
                    break;
                    case SendImages:
                        scRestrictionImage.setChecked(false);
                        existsSendImages = true;
                        break;
                    case SendVideos:
                        scRestrictionVideo.setChecked(false);
                        existsSendVideos = true;
                        break;
                    case SendDocuments:
                        scRestrictionDocument.setChecked(false);
                        existsSendDocuments = true;
                        break;
                    case SendAudio:
                        scRestrictionAudio.setChecked(false);
                        existsSendAudio = true;
                        break;
                }
            }
            if (!existsMute) {
                scRestrictionText.setChecked(true);
            }
            if (!existsSendImages) {
                scRestrictionImage.setChecked(true);
            }
            if (!existsSendVideos) {
                scRestrictionVideo.setChecked(true);
            }
            if (!existsSendDocuments) {
                scRestrictionDocument.setChecked(true);
            }
            if (!existsSendAudio) {
                scRestrictionAudio.setChecked(true);
            }
        }
    }

    public interface CustomCheckedChangedListener {
        void onCheckedChanged(int position, CompoundButton buttonView, boolean isChecked, RestrictionType restrictionType);
    }
}
