package com.agendadigital.views.modules.chats.components.views;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.agendadigital.R;
import com.agendadigital.core.modules.messages.domain.MessageEntity;
import com.agendadigital.core.shared.infrastructure.utils.DateFormatter;

import java.io.IOException;

import androidx.annotation.NonNull;

public class ItemSentView extends MessageView {

    public ItemSentView(@NonNull ViewGroup viewGroup) {
        super(viewGroup);
        inflate(viewGroup.getContext(), R.layout.chat_item_sent, this);
    }

    @Override
    public void setMessage(MessageEntity message) throws Exception {
        super.setMessage(message);
        this.tvMessageReceivedAt.setText(DateFormatter.formatToTime(message.getCreatedAt()));
        if(messageEntity.getDestinationState() == MessageEntity.DestinationState.Create) {
            this.tvMessageBody.setTextColor(Color.RED);
        } else {
            this.tvMessageBody.setTextColor(Color.BLACK);
        }
    }
}
