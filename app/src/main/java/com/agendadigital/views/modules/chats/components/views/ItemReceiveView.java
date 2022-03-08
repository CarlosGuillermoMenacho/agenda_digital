package com.agendadigital.views.modules.chats.components.views;

import android.graphics.Color;
import android.view.ViewGroup;
import com.agendadigital.R;
import com.agendadigital.core.modules.contacts.infrastructure.ContactRepository;
import com.agendadigital.core.modules.messages.domain.MessageEntity;
import com.agendadigital.core.shared.infrastructure.utils.DateFormatter;
import androidx.annotation.NonNull;

public class ItemReceiveView extends MessageView {

    public ItemReceiveView(@NonNull ViewGroup viewGroup) {
        super(viewGroup);
        inflate(viewGroup.getContext(), R.layout.chat_item_receive, this);
    }

    @Override
    public void setMessage(MessageEntity message) throws Exception {
        super.setMessage(message);
        this.tvMessageBody.setTextColor(Color.WHITE);
        if (!message.getGroupId().isEmpty()) {
            this.tvMessageContactName.setVisibility(VISIBLE);
            this.tvMessageContactName.setText(new ContactRepository(viewGroup.getContext()).findByIdAndType(message.getDeviceFromId(), message.getDeviceFromType().getValue()).getName());
        }
        this.tvMessageReceivedAt.setText(DateFormatter.formatToTime(message.getReceivedAt()));
    }
}
