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

    private TextView tvTitle;
    private TextView tvBody;
    private TextView tvReceivedAt;
    private ImageView ivImage;
    private boolean isImageFitToScreen;
    private MessageView messageView;

    public MessageViewHolder(@NonNull View itemView) {
        super(itemView);
//        this.messageView = (MessageView) itemView;
        tvTitle = itemView.findViewById(R.id.sender_text_view);
        tvTitle.setVisibility(View.GONE);
        tvBody = itemView.findViewById(R.id.message_text_view);
        tvReceivedAt = itemView.findViewById(R.id.timestamp_text_view);
        ivImage = itemView.findViewById(R.id.ivImageMessage);
        initImageView();
    }

    private void initImageView() {
        ivImage.setOnClickListener(v -> {
            if(isImageFitToScreen) {
                isImageFitToScreen=false;
                ivImage.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                ivImage.setAdjustViewBounds(true);
            }else{
                isImageFitToScreen=true;
                ivImage.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                ivImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
            }
        });
    }

    public void set(MessageEntity messageEntity, int viewType) throws IOException {
//        this.messageView.setSender(String.valueOf(messageEntity.getDeviceFromId()));
//        this.messageView.setMessage(messageEntity.getData());
//        this.messageView.setTimestamp(new SimpleDateFormat("HH:mm").format(messageEntity.getReceivedAt()));
//        this.tvTitle.setText(String.valueOf(messageEntity.getDeviceFromId()));
        this.tvBody.setText(messageEntity.getData());
        if(messageEntity.getDestinationState() == MessageEntity.DestinationState.Create) {
            this.tvBody.setTextColor(Color.RED);
        }
        else {
            if (viewType == 1) {
                this.tvBody.setTextColor(Color.BLACK);
            } else if (viewType == 2) {
                this.tvBody.setTextColor(Color.WHITE);
            }
        }
        this.tvReceivedAt.setText(new SimpleDateFormat("HH:mm")
                                    .format(messageEntity.getDeviceFromId().equals(Globals.user.getCodigo())?
                                            messageEntity.getCreatedAt():
                                            messageEntity.getReceivedAt()));
        if (messageEntity.getMessageType() != MessageEntity.MessageType.Text && messageEntity.getMultimediaEntity() != null) {
            ivImage.setVisibility(View.VISIBLE);
            File file = new File(messageEntity.getMultimediaEntity().getLocalUri());

            if (messageEntity.getMessageType() == MessageEntity.MessageType.Image) {
                if ((file.exists() && !file.isDirectory()) || viewType == 1) {
                    Uri uri;
                    if (viewType == 1) {
                        uri = Uri.parse(messageEntity.getMultimediaEntity().getLocalUri());
                    } else {
                        uri = Uri.fromFile(file);
                    }
                    ivImage.setImageBitmap(MediaStore.Images.Media.getBitmap(itemView.getContext().getContentResolver(), uri));
                }else {
                    ivImage.setImageResource(R.drawable.not_found);
                }
            }else if (messageEntity.getMessageType() == MessageEntity.MessageType.Document) {
                if(file.getPath().endsWith(".txt")) {
                    ivImage.setImageResource(R.drawable.txt_32icon);
                } else if (file.getPath().endsWith(".pdf")) {
                    ivImage.setImageResource(R.drawable.pdf_32icon);
                } else if (file.getPath().endsWith(".doc") || (file.getPath().endsWith(".docx"))) {
                    ivImage.setImageResource(R.drawable.word_32icon);
                }
                ivImage.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                tvBody.setText(file.getName());
            }
        }else {
            ivImage.setVisibility(View.GONE);
        }
    }
}
