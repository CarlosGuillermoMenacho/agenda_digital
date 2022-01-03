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

public class ItemReceiveView extends MessageView {
    public ItemReceiveView(@NonNull ViewGroup viewGroup) {
        super(viewGroup);
        inflate(viewGroup.getContext(), R.layout.chat_item_receive, this);
    }

    @Override
    public void setMessage(MessageEntity message) throws IOException {
        super.setMessage(message);
        this.tvMessageBody.setTextColor(Color.WHITE);
        this.tvMessageReceivedAt.setText(DateFormatter.formatToTime(message.getReceivedAt()));
    }
}
