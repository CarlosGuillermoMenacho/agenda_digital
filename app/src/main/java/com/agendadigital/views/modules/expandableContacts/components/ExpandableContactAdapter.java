package com.agendadigital.views.modules.expandableContacts.components;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.agendadigital.R;
import com.agendadigital.core.modules.contacts.domain.ContactEntity;
import com.agendadigital.core.modules.contacts.domain.ContactTypeEntity;
import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

public class ExpandableContactAdapter extends RecyclerView.Adapter<ExpandableContactAdapter.ExpandableContactViewHolder> {

    private final int HEADER = 0;
    private final int CHILD = 1;

    private final ContactTypeEntity contactTypeEntity;
    private boolean isExpanded = false;

    public ExpandableContactAdapter(ContactTypeEntity contactTypeEntity) {
        this.contactTypeEntity = contactTypeEntity;
    }

    @NonNull
    @Override
    public ExpandableContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        ExpandableContactViewHolder expandableContactViewHolder;
        LayoutInflater inflater = (LayoutInflater)
                parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (viewType == HEADER) {
            view = inflater.inflate(R.layout.item_header_contact, parent, false);
            expandableContactViewHolder = new ExpandableContactViewHolder.ExpandableHeaderContactViewHolder(view);
        } else {
            view = inflater.inflate(R.layout.item_child_contact, parent, false);
            expandableContactViewHolder = new ExpandableContactViewHolder.ExpandableChildContactViewHolder(view);
        }
        return expandableContactViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ExpandableContactViewHolder holder, int position) {
        if (holder instanceof ExpandableContactViewHolder.ExpandableHeaderContactViewHolder) {
            ((ExpandableContactViewHolder.ExpandableHeaderContactViewHolder)holder).onBind(contactTypeEntity.description, onHeaderClicked);
        } else {
            ((ExpandableContactViewHolder.ExpandableChildContactViewHolder)holder).onBind(contactTypeEntity.contactEntityList.get(position - 1));
        }
    }

    @Override
    public int getItemCount() {
        return isExpanded ? contactTypeEntity.contactEntityList.size() + 1: 1;
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? HEADER : CHILD;
    }

    private final View.OnClickListener onHeaderClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            isExpanded = !isExpanded;

            if (isExpanded) {
                notifyItemRangeInserted(1, contactTypeEntity.contactEntityList.size());
                notifyItemChanged(0);
            } else {
                notifyItemRangeRemoved(1, contactTypeEntity.getContactEntityList().size());
                notifyItemChanged(0);
            }
        }
    };

    public static class ExpandableContactViewHolder extends RecyclerView.ViewHolder {

        public ExpandableContactViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        public static class ExpandableHeaderContactViewHolder extends ExpandableContactViewHolder {

            private final TextView tvContactExpandableHeaderDescription;

            public ExpandableHeaderContactViewHolder(@NonNull View itemView) {
                super(itemView);
                tvContactExpandableHeaderDescription = itemView.findViewById(R.id.tvContactExpandableHeaderDescription);
            }

            public void onBind(String description, View.OnClickListener clickListener) {
                this.tvContactExpandableHeaderDescription.setText(description);
                itemView.setOnClickListener(clickListener);
            }
        }

        public static class ExpandableChildContactViewHolder extends ExpandableContactViewHolder {

            private final TextView tvExpandableContactChildName;
            private final TextView tvExpandableContactChildContactType;

            public ExpandableChildContactViewHolder(@NonNull View itemView) {
                super(itemView);
                tvExpandableContactChildName = itemView.findViewById(R.id.tvExpandableContactChildName);
                tvExpandableContactChildContactType = itemView.findViewById(R.id.tvExpandableContactChildContactType);
            }

            public void onBind(ContactEntity contactEntity) {
                tvExpandableContactChildContactType.setText(contactEntity.getContactType().toString());
                tvExpandableContactChildName.setText(contactEntity.getName());
                itemView.setOnClickListener(v -> {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("contact", contactEntity);
                    Navigation.findNavController(itemView).navigate(R.id.action_fragment_tabchat_contact_to_fragment_chat, bundle);
                });
            }
        }
    }

}
