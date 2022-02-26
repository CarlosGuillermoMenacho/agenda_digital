package com.agendadigital.views.modules.groupContacts.components;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import com.agendadigital.R;
import com.agendadigital.core.modules.contacts.domain.ContactEntity;
import com.agendadigital.views.modules.contacts.components.ContactAdapter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class GroupContactAdapter extends RecyclerView.Adapter<GroupContactAdapter.GroupContactViewHolder> implements Filterable {

    private ContactAdapter.CustomClickListener clickListener;
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

    public GroupContactAdapter(List<ContactEntity> contactEntityList) {
        this.contactEntityList = contactEntityList;
        this.fullContactEntityList = new ArrayList<>(contactEntityList);
    }

    public GroupContactAdapter() {
        this.contactEntityList = new ArrayList<>();
    }

    @NonNull
    @Override
    public GroupContactAdapter.GroupContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = null;
        LayoutInflater inflater = (LayoutInflater)
                parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (inflater != null) {
            view = inflater.inflate(R.layout.item_group_contact, parent, false);
        }
        return new GroupContactAdapter.GroupContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupContactAdapter.GroupContactViewHolder holder, int position) {
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
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(ContactAdapter.CustomClickListener onItemClickListener) {
        this.clickListener = onItemClickListener;
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    public class GroupContactViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{

        private final TextView tvName;
        private final TextView tvTypeContact;

        public GroupContactViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            tvName = itemView.findViewById(R.id.tvGroupContactName);
            tvTypeContact = itemView.findViewById(R.id.tvGroupContactType);
        }

        public void set(ContactEntity contactEntity) throws Exception {
            tvName.setText(contactEntity.getName());
            tvTypeContact.setText(contactEntity.getContactType().toString());
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
