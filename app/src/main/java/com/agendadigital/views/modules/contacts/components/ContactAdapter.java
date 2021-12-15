package com.agendadigital.views.modules.contacts.components;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.agendadigital.R;
import com.agendadigital.core.modules.contacts.domain.ContactEntity;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ContactAdapter  extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> {

    private int selectedPosition = RecyclerView.NO_POSITION;
    private CustomClickListener clickListener;
    private List<ContactEntity> contactEntityList;

    public ContactAdapter(List<ContactEntity> contactEntityList) {
        this.contactEntityList = contactEntityList;
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
            view = inflater.inflate(R.layout.item_contact, parent, false);
        }
        return new ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        holder.set(contactEntityList.get(position));
//        holder.itemView.setBackgroundColor(selectedPosition == position ? Color.GREEN : Color.TRANSPARENT);
//        holder.itemView.setSelected(selectedPosition == position);
    }

    @Override
    public int getItemCount() {
        return contactEntityList.size();
    }

    public void add(ContactEntity entity) {
        this.contactEntityList.add(entity);
        notifyDataSetChanged();
    }

    public void setContactEntityList(List<ContactEntity> contactEntityList) {
        this.contactEntityList = contactEntityList;
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(CustomClickListener onItemClickListener) {
        this.clickListener = onItemClickListener;
    }

    public class ContactViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{

        //private CustomClickListener clickListener;
        private TextView tvName;
        private TextView tvTypeContact;

        public ContactViewHolder(@NonNull View itemView) {
            super(itemView);
            //this.clickListener = clickListener;
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            tvName = itemView.findViewById(R.id.tvName);
            tvTypeContact = itemView.findViewById(R.id.tvTypeContact);
        }

        public void set(ContactEntity contactEntity){
            tvName.setText(contactEntity.getId().concat(":").concat(contactEntity.getName()));
            tvTypeContact.setText(contactEntity.getTypeContact().toString());
        }

        @Override
        public void onClick(View v) {
            clickListener.onClick(getAdapterPosition(), v);
//            notifyItemChanged(selectedPosition);
//            selectedPosition = getLayoutPosition();
//            notifyItemChanged(selectedPosition);
        }

        @Override
        public boolean onLongClick(View v) {
            clickListener.onLongClick(getAdapterPosition(), v);
//            notifyItemChanged(selectedPosition);
//            selectedPosition = getLayoutPosition();
//            notifyItemChanged(selectedPosition);
            return false;
        }
    }

    public interface CustomClickListener {
        void onClick(int position, View v);
        void onLongClick(int position, View v);
    }
}
