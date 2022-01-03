package com.agendadigital.views.modules.chats.components.views;

import android.graphics.Color;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.agendadigital.R;
import com.agendadigital.core.modules.messages.domain.MessageEntity;
import com.agendadigital.clases.Globals;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MessageViewHolder extends RecyclerView.ViewHolder {

    private boolean isImageFitToScreen;
    private final MessageView messageView;

    public MessageViewHolder(@NonNull View itemView) {
        super(itemView);
        this.messageView = (MessageView) itemView;
    }

//    private void initImageView() {
//        ivImage.setOnClickListener(v -> {
//            if(isImageFitToScreen) {
//                isImageFitToScreen=false;
//                ivImage.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
//                ivImage.setAdjustViewBounds(true);
//            }else{
//                isImageFitToScreen=true;
//                ivImage.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
//                ivImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
//            }
//        });
//    }

    public void set(MessageEntity messageEntity) throws IOException {
        this.messageView.setMessage(messageEntity);
    }
}
