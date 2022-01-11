package com.agendadigital.views.modules.chats.components.views;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageButton;
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
    protected ImageButton ibMessagePlayVideo;

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
        ibMessagePlayVideo = this.findViewById(R.id.ibMessagePlayVideo);
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

                    ivImage.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showImageView(imageBitmap);
                        }
                    });
                    break;
                case Video:
                    ivImage.setVisibility(GONE);
                    flVideoMessageContainer.setVisibility(VISIBLE);
                    Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(file.getPath(), MediaStore.Images.Thumbnails.MINI_KIND);
                    BitmapDrawable bitmapDrawable = new BitmapDrawable(bitmap);
                    vvVideoMessage.setBackground(bitmapDrawable);
                    vvVideoMessage.setOnClickListener(v -> showVideoView(file.getPath()));

                    ibMessagePlayVideo.setOnClickListener(v -> showVideoView(file.getPath()));
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

    private void showImageView(Bitmap bitmap) {
        AlertDialog.Builder builder = new AlertDialog.Builder(viewGroup.getContext());
        LayoutInflater inflater = (LayoutInflater)viewGroup.getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogLayout = inflater.inflate(R.layout.dialog_full_size_imageview, null);
        builder.setView(dialogLayout);
        builder.setNegativeButton("Cerrar", (dialog, which) -> {
            dialog.cancel();
        });
        final AlertDialog dialog = builder.create();

        //dialog.setView(dialogLayout);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setOnShowListener(d -> {
            ImageView image = dialog.findViewById(R.id.ivDialogImage);
//            image.setImageResource(R.drawable.backgroud_splash);
            image.setImageBitmap(bitmap);
            float imageWidthInPX = (float)image.getWidth();
//
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(Math.round(imageWidthInPX),
                   Math.round(imageWidthInPX * (float)bitmap.getHeight() / (float)bitmap.getWidth()));
            image.setLayoutParams(layoutParams);
        });
        dialog.show();


    }

    private void showVideoView(String path) {
        AlertDialog.Builder builder = new AlertDialog.Builder(viewGroup.getContext());
        LayoutInflater inflater = (LayoutInflater)viewGroup.getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogLayout = inflater.inflate(R.layout.dialog_full_size_videoview, null);
        builder.setView(dialogLayout);
        builder.setNegativeButton("Cerrar", (dialog, which) -> {
            dialog.cancel();
        });
        final AlertDialog dialog = builder.create();

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setOnShowListener(d -> {
            VideoView videoView = dialog.findViewById(R.id.vvDialogVideo);
            videoView.setVideoPath(path);
            videoView.start();
        });
        dialog.show();
    }
}
