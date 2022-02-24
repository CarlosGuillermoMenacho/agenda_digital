package com.agendadigital.views.modules.contacts.components;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import com.agendadigital.R;
import com.agendadigital.core.modules.contacts.domain.ContactEntity;
import com.agendadigital.core.modules.messages.domain.MessageEntity;
import com.agendadigital.core.modules.messages.infrastructure.MessageRepository;
import com.nex3z.notificationbadge.NotificationBadge;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> implements Filterable {

    private CustomClickListener clickListener;
    private List<ContactEntity> fullContactEntityList;
    private List<ContactEntity> contactEntityList;

    private Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<ContactEntity> filtered = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filtered.addAll(fullContactEntityList);
            }else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (ContactEntity contactEntity: fullContactEntityList) {
                    if (contactEntity.getName().toLowerCase().contains(filterPattern) || contactEntity.getContactType().toString().toLowerCase().contains(filterPattern)) {
                        filtered.add(contactEntity);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filtered;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            contactEntityList.clear();
            contactEntityList.addAll((List) results.values);
            Collections.sort(contactEntityList, ContactEntity.ContactLastReceivedMessages);
            notifyDataSetChanged();
        }
    };

    public ContactAdapter(List<ContactEntity> contactEntityList) {
        this.contactEntityList = contactEntityList;
        this.fullContactEntityList = new ArrayList<>(contactEntityList);
    }

    public ContactAdapter() {
        this.contactEntityList = new ArrayList<>();
    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = null;
        LayoutInflater inflater = (LayoutInflater)
                parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (inflater != null) {
            view = inflater.inflate(R.layout.item_contact_chat, parent, false);
        }
        return new ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        try {
            holder.set(contactEntityList.get(position));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return contactEntityList.size();
    }

    public void add(ContactEntity entity) {
        this.contactEntityList.add(entity);
        notifyDataSetChanged();
    }

    public ContactEntity getItem(int position) {
        return this.contactEntityList.get(position);
    }

    public void setContactEntityList(List<ContactEntity> contactEntityList) {
        this.contactEntityList = contactEntityList;
        this.fullContactEntityList = new ArrayList<>(contactEntityList);
        Collections.sort(this.contactEntityList, ContactEntity.ContactLastReceivedMessages);
//        contactAdapter.notifyDataSetChanged();
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(CustomClickListener onItemClickListener) {
        this.clickListener = onItemClickListener;
    }

    public void updateContactMessage(ContactEntity contactEntity) {
        for (ContactEntity contact: this.contactEntityList) {
            if(contact.getId().equals(contactEntity.getId()) && contact.getContactType()==contactEntity.getContactType()){
                contact.setUnreadMessages(contactEntity.getUnreadMessages());
                contact.setLastMessageData(contactEntity.getLastMessageData());
                contact.setLastMessageReceived(contactEntity.getLastMessageReceived());
                Collections.sort(this.contactEntityList, ContactEntity.ContactLastReceivedMessages);
                notifyDataSetChanged();
                break;
            }
        }
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    public class ContactViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{

        private final MessageRepository messageRepository;
        private final TextView tvName;
        private final TextView tvTypeContact;
        private final TextView tvLastMessage;
        private final TextView tvContactUnreadMessages;
        private final TextView tvLastReceivedMessage;
        private final CardView cvContactUnreadMessages;
//        private final NotificationBadge nbUnreadMessages;
        private final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");

        public ContactViewHolder(@NonNull View itemView) {
            super(itemView);
            messageRepository = new MessageRepository(itemView.getContext());
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            tvName = itemView.findViewById(R.id.tvContactName);
            tvTypeContact = itemView.findViewById(R.id.tvContactType);
            tvLastMessage = itemView.findViewById(R.id.tvContactLastMessage);
            tvLastReceivedMessage = itemView.findViewById(R.id.tvContactLastReceivedMessage);
            tvContactUnreadMessages = itemView.findViewById(R.id.tvContactUnreadMessages);
            cvContactUnreadMessages = itemView.findViewById(R.id.cvContactUnreadMessages);
        }

        public void set(ContactEntity contactEntity) throws Exception {
            tvName.setText(contactEntity.getName());
            tvTypeContact.setText(contactEntity.getContactType().toString());
            tvLastMessage.setText(contactEntity.getLastMessageData());
            tvLastReceivedMessage.setText(contactEntity.getLastMessageReceived()!= null? dateFormat.format(contactEntity.getLastMessageReceived()): "");
            if (contactEntity.getUnreadMessages() > 0) {
                cvContactUnreadMessages.setVisibility(View.VISIBLE);
                tvContactUnreadMessages.setVisibility(View.VISIBLE);
                tvContactUnreadMessages.setText(String.valueOf(contactEntity.getUnreadMessages()));
            }else {
                cvContactUnreadMessages.setVisibility(View.GONE);
                tvContactUnreadMessages.setVisibility(View.GONE);
            }
        }

        @Override
        public void onClick(View v) {
            clickListener.onClick(getAdapterPosition(), v);
        }

        @Override
        public boolean onLongClick(View v) {
            clickListener.onLongClick(getAdapterPosition(), v);
            return false;
        }
    }

    public interface CustomClickListener {
        void onClick(int position, View v);
        void onLongClick(int position, View v);
    }
}
