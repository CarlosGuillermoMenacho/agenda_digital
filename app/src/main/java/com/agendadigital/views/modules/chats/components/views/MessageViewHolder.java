package com.agendadigital.views.modules.chats.components.views;

import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.view.View;
import android.widget.TextView;

import com.agendadigital.R;
import com.agendadigital.core.modules.messages.domain.MessageEntity;
import com.agendadigital.clases.Globals;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

public class MessageViewHolder extends RecyclerView.ViewHolder {

    private TextView tvTitle;
    private TextView tvBody;
    private TextView tvReceivedAt;
    private MessageView messageView;

    public MessageViewHolder(@NonNull View itemView) {
        super(itemView);
        //this.messageView = (MessageView) itemView;
        tvTitle = itemView.findViewById(R.id.sender_text_view);
        tvTitle.setVisibility(View.GONE);
        tvBody = itemView.findViewById(R.id.message_text_view);
        tvReceivedAt = itemView.findViewById(R.id.timestamp_text_view);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void set(MessageEntity messageEntity) {
//        this.messageView.setSender(String.valueOf(messageEntity.getDeviceFromId()));
//        this.messageView.setMessage(messageEntity.getData());
//        this.messageView.setTimestamp(new SimpleDateFormat("HH:mm").format(messageEntity.getReceivedAt()));
//        this.tvTitle.setText(String.valueOf(messageEntity.getDeviceFromId()));
        this.tvBody.setText(messageEntity.getData());
        this.tvReceivedAt.setText(new SimpleDateFormat("HH:mm")
                                    .format(messageEntity.getDeviceFromId().equals(Globals.user.getCodigo())?
                                            messageEntity.getCreatedAt():
                                            messageEntity.getReceivedAt()));
    }
}
