package com.agendadigital.views.modules.chats.components.adapters;

import android.util.Log;
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
        notifyItemRangeChanged(0, this.messageEntities.size());
        //notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        MessageEntity messageEntity = messageEntities.get(position);
        int viewType = 2;
        if(messageEntity.getDeviceFromId().equals(Globals.user.getCodigo()))
            viewType = 1;
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
        try {
            holder.set(messageEntities.get(position));
        } catch (Exception e) {
            Log.d("MessageAdapter", "onBindViewHolder: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return messageEntities.size();
    }

    public void add(MessageEntity messageEntity) {
        if (messageEntity != null) {
            this.messageEntities.add(messageEntity);
            notifyItemInserted(this.messageEntities.size());
            //notifyDataSetChanged();
        }
    }

    public void updateDestinationState(MessageEntity messageSend) {
        for (int i = 0; i < this.messageEntities.size(); i++) {
            MessageEntity message = this.messageEntities.get(i);
            if (message.getId().equals(messageSend.getId())) {
                message.setDestinationState(messageSend.getDestinationState());
                notifyItemChanged(i);
                //notifyDataSetChanged();
                break;
            }
        }
    }
    public void delete(MessageEntity messageSend) {
        for (int i = 0; i < this.messageEntities.size(); i++) {
            if (messageEntities.get(i).getId().equals(messageSend.getId())) {
                this.messageEntities.remove(i);
                notifyItemRemoved(i);
                //notifyDataSetChanged();
                break;
            }
        }
    }

    public List<MessageEntity> getMessageEntities() {
        return messageEntities;
    }

}
