package com.agendadigital.views.modules.chats.components.views;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;
import com.agendadigital.R;
import com.agendadigital.core.modules.messages.domain.MessageEntity;
import java.io.File;
import java.io.IOException;
import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;

public abstract class MessageView extends RelativeLayout {

    protected MessageEntity messageEntity;
    protected TextView tvMessageContactName;
    protected CardView bubble;
    protected TextView tvMessageBody;
    protected TextView tvMessageReceivedAt;
    protected ImageView ivImage;
    protected FrameLayout flVideoMessageContainer;
    protected VideoView vvVideoMessage;
    protected ViewGroup viewGroup;

    public MessageView(@NonNull ViewGroup viewGroup) {
        super(viewGroup.getContext());
        this.viewGroup = viewGroup;
    }

    public void initComponents(){
        tvMessageContactName = this.findViewById(R.id.sender_text_view);
        tvMessageBody = this.findViewById(R.id.message_text_view);
        tvMessageReceivedAt = this.findViewById(R.id.timestamp_text_view);
        ivImage = this.findViewById(R.id.ivImageMessage);
        flVideoMessageContainer = this.findViewById(R.id.flMessageVideoContainer);
        vvVideoMessage = this.findViewById(R.id.vvVideoMessage);
        bubble = this.findViewById(R.id.bubble);
    }

    public void setMessage(MessageEntity message) throws IOException {
        messageEntity = message;
        tvMessageContactName.setVisibility(GONE);
        tvMessageBody.setText(messageEntity.getData());

        if(messageEntity.getMessageType() != MessageEntity.MessageType.Text) {
            File file = new File(messageEntity.getMultimediaEntity().getLocalUri());
            switch (messageEntity.getMessageType()) {
                case Image:
                    flVideoMessageContainer.setVisibility(GONE);
                    ivImage.setVisibility(VISIBLE);
                    Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(viewGroup.getContext().getContentResolver(), Uri.fromFile(file));
                    ivImage.setImageBitmap(Bitmap.createScaledBitmap(imageBitmap, ivImage.getMaxWidth(), ivImage.getMaxHeight(), true));
                    break;
                case Video:
                    ivImage.setVisibility(GONE);
                    flVideoMessageContainer.setVisibility(VISIBLE);
                    Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(file.getPath(), MediaStore.Images.Thumbnails.MINI_KIND);
                    BitmapDrawable bitmapDrawable = new BitmapDrawable(bitmap);
                    vvVideoMessage.setBackground(bitmapDrawable);
                    break;
                case Document:
                    flVideoMessageContainer.setVisibility(GONE);
                    ivImage.setVisibility(VISIBLE);
                    if (file.getPath().endsWith(".txt")) {
                        ivImage.setImageResource(R.drawable.txt_32icon);
                    } else if (file.getPath().endsWith(".pdf")) {
                        ivImage.setImageResource(R.drawable.pdf_32icon);
                    } else if (file.getPath().endsWith(".doc") || (file.getPath().endsWith(".docx"))) {
                        ivImage.setImageResource(R.drawable.word_32icon);
                    }
                    ivImage.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                    tvMessageBody.setText(file.getName());
                    break;
            }
        } else {
            ivImage.setVisibility(GONE);
            flVideoMessageContainer.setVisibility(GONE);
        }
    }

    public void setBackground(@ColorInt int background) {
        if (bubble == null) {
            this.bubble = findViewById(R.id.bubble);
        }
        bubble.setCardBackgroundColor(background);
    }

    public void setElevation(float elevation) {
        if (bubble == null) {
            this.bubble = findViewById(R.id.bubble);
        }
        bubble.setCardElevation(elevation);
    }

}
