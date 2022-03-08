package com.agendadigital.views.modules.chats.components.views;

import android.view.View;
import com.agendadigital.core.modules.messages.domain.MessageEntity;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MessageViewHolder extends RecyclerView.ViewHolder {

    private final MessageView messageView;

    public MessageViewHolder(@NonNull View itemView) {
        super(itemView);
        this.messageView = (MessageView) itemView;
    }

    public void set(MessageEntity messageEntity) throws Exception {
        this.messageView.setMessage(messageEntity);
    }
}
