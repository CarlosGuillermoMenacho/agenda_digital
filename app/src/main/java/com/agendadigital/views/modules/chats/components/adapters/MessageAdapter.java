package com.agendadigital.views.modules.chats.components.adapters;

import android.view.View;
import android.view.ViewGroup;

import com.agendadigital.core.modules.messages.domain.MessageEntity;
import com.agendadigital.views.modules.chats.components.views.MessageViewBuilder;
import com.agendadigital.views.modules.chats.components.views.MessageViewHolder;
import com.agendadigital.clases.Globals;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MessageAdapter extends RecyclerView.Adapter<MessageViewHolder> {

    private List<MessageEntity> messageEntities;

    public MessageAdapter() {
        messageEntities = new ArrayList<>();
    }

    public MessageAdapter(List<MessageEntity> messageEntities) {
        this.messageEntities = messageEntities;
    }

    public void setList(List<MessageEntity> notificationEntities) {
        this.messageEntities = notificationEntities;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        MessageEntity messageEntity = messageEntities.get(position);
        int viewType = 1;
        if(messageEntity.getDestinationId().equals(Globals.user.getCodigo()))
            viewType = 2;
        return viewType;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = MessageViewBuilder.build(parent, viewType);
        return new MessageViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        holder.set(messageEntities.get(position));
    }

    @Override
    public int getItemCount() {
        return messageEntities.size();
    }

    public void add(MessageEntity messageEntity) {
        if (messageEntity != null) {
            this.messageEntities.add(messageEntity);
            notifyDataSetChanged();
        }
    }

    public void set(List<MessageEntity> notificationEntities) {
        this.messageEntities = notificationEntities;
        notifyDataSetChanged();
    }
}