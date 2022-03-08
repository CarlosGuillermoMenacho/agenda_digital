package com.agendadigital.views.modules.chats.components.views;

import android.graphics.Color;
import android.view.ViewGroup;
import com.agendadigital.R;
import com.agendadigital.core.modules.messages.domain.MessageEntity;
import com.agendadigital.core.shared.infrastructure.utils.DateFormatter;
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
        if (message.getMessageType() == MessageEntity.MessageType.Text) {
            if (message.getDestinationState() == MessageEntity.DestinationState.Create) {
                this.tvMessageBody.setTextColor(Color.RED);
            } else {
                this.tvMessageBody.setTextColor(Color.BLACK);
            }
        }
    }
}
