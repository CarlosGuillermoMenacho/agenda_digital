package com.agendadigital.views.modules.chats.components.views;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;
import com.agendadigital.BuildConfig;
import com.agendadigital.R;
import com.agendadigital.core.modules.messages.domain.MessageEntity;
import java.io.File;
import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.FileProvider;

public abstract class MessageView extends RelativeLayout {

    protected TextView tvMessageContactName;
    protected CardView bubble;
    protected TextView tvMessageBody;
    protected TextView tvMessageReceivedAt;
    protected ImageView ivImage;
    protected FrameLayout flVideoMessageContainer;
    protected VideoView vvVideoMessage;
    protected ViewGroup viewGroup;
    protected ImageButton ibMessagePlayVideo;
    protected FrameLayout flMessageLoadingContainer;

    protected RelativeLayout rlMessageAudioContainer;
    protected Button btAudioPlay;
    protected Button btAudioPause;
    protected SeekBar sbAudioBar;
    protected MediaPlayer mediaPlayer;
    private final Handler mHandler = new Handler();

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
        flMessageLoadingContainer = this.findViewById(R.id.flMessageLoadingContainer);
        vvVideoMessage = this.findViewById(R.id.vvVideoMessage);
        ibMessagePlayVideo = this.findViewById(R.id.ibMessagePlayVideo);
        bubble = this.findViewById(R.id.bubble);

        rlMessageAudioContainer = this.findViewById(R.id.rlMessageAudioContainer);
        btAudioPlay = this.findViewById(R.id.btAudioPlay);
        btAudioPause = this.findViewById(R.id.btAudioPause);
        sbAudioBar = this.findViewById(R.id.sbAudioBar);
    }

    public void setMessage(MessageEntity message) throws Exception {
        tvMessageContactName.setVisibility(GONE);
        tvMessageBody.setText(message.getData());

        if(message.getMessageType() != MessageEntity.MessageType.Text) {
            if (message.getDestinationState() == MessageEntity.DestinationState.Create) {
                flMessageLoadingContainer.setVisibility(VISIBLE);
                ivImage.setVisibility(GONE);
                flVideoMessageContainer.setVisibility(GONE);
                rlMessageAudioContainer.setVisibility(GONE);
            } else {
                Log.d("MessageView", "setMessage: " + message.getDestinationState().toString() + ";" + message.getMultimediaEntity().getLocalUri());
                flMessageLoadingContainer.setVisibility(GONE);
                File file = new File(message.getMultimediaEntity().getLocalUri());
                switch (message.getMessageType()) {
                    case Image:
                        rlMessageAudioContainer.setVisibility(GONE);
                        flVideoMessageContainer.setVisibility(GONE);
                        ivImage.setVisibility(VISIBLE);
                        int rotate = 0;
                        Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(viewGroup.getContext().getContentResolver(), Uri.fromFile(file));
                        ExifInterface exif = new ExifInterface(
                                file.getAbsolutePath());
                        int orientation = exif.getAttributeInt(
                                ExifInterface.TAG_ORIENTATION,
                                ExifInterface.ORIENTATION_NORMAL);

                        switch (orientation) {
                            case ExifInterface.ORIENTATION_ROTATE_270:
                                rotate = 270;
                                break;
                            case ExifInterface.ORIENTATION_ROTATE_180:
                                rotate = 180;
                                break;
                            case ExifInterface.ORIENTATION_ROTATE_90:
                                rotate = 90;
                                break;
                        }
                        Matrix matrix = new Matrix();
                        matrix.postRotate(rotate);
                        Bitmap scaledBitmap = Bitmap.createScaledBitmap(imageBitmap, ivImage.getMaxWidth(), ivImage.getMaxHeight(), true);
                        Bitmap rotatedBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, ivImage.getMaxWidth(), ivImage.getMaxHeight(), matrix, true);
                        ivImage.setImageBitmap(rotatedBitmap);

                        ivImage.setOnClickListener(v -> showImageView(rotatedBitmap));
                        break;
                    case Video:
                        rlMessageAudioContainer.setVisibility(GONE);
                        ivImage.setVisibility(GONE);
                        flVideoMessageContainer.setVisibility(VISIBLE);
                        Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(file.getPath(), MediaStore.Images.Thumbnails.MINI_KIND);
                        BitmapDrawable bitmapDrawable = new BitmapDrawable(bitmap);
                        vvVideoMessage.setBackground(bitmapDrawable);
                        vvVideoMessage.setOnClickListener(v -> showVideoView(file.getPath()));
                        ibMessagePlayVideo.setOnClickListener(v -> showVideoView(file.getPath()));
                        break;
                    case Document:
                        rlMessageAudioContainer.setVisibility(GONE);
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
                        ivImage.setOnClickListener(v -> {
                            Intent target = new Intent(Intent.ACTION_VIEW);
                            target.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
                            String extension = android.webkit.MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(file).toString());
                            String mimetype = android.webkit.MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
                            Uri uri = FileProvider.getUriForFile(viewGroup.getContext(), BuildConfig.APPLICATION_ID + ".provider", file);
                            if (extension.equalsIgnoreCase("") || mimetype == null) {
                                target.setDataAndType(uri, "text/*");
                            } else {
                                target.setDataAndType(uri, mimetype);
                            }
                            Intent intent = Intent.createChooser(target, "Open File");
                            viewGroup.getContext().startActivity(intent);
                        });
                        break;
                    case Audio:
                        tvMessageBody.setVisibility(GONE);
                        ivImage.setVisibility(GONE);
                        flVideoMessageContainer.setVisibility(GONE);
                        rlMessageAudioContainer.setVisibility(VISIBLE);
                        mediaPlayer = MediaPlayer.create(viewGroup.getContext(), Uri.fromFile(file));
                        sbAudioBar.setMax(mediaPlayer.getDuration());

                        btAudioPlay.setOnClickListener(v -> {
                            btAudioPlay.setVisibility(GONE);
                            btAudioPause.setVisibility(VISIBLE);
                            mediaPlayer.start();
                            this.post(new Runnable() {
                                @Override
                                public void run() {
                                    int currentPosition = 0;
                                    int duration = mediaPlayer.getDuration();
                                    if (mediaPlayer != null && currentPosition < duration) {
                                        currentPosition = mediaPlayer.getCurrentPosition();
                                        sbAudioBar.setProgress(currentPosition);
                                    }
                                    if (currentPosition >= duration) {
                                        sbAudioBar.setProgress(0);
                                        btAudioPause.setVisibility(GONE);
                                        btAudioPlay.setVisibility(VISIBLE);
                                    }
                                    mHandler.postDelayed(this, 300);
                                }
                            });
                        });
                        btAudioPause.setOnClickListener(v -> {
                            btAudioPause.setVisibility(GONE);
                            btAudioPlay.setVisibility(VISIBLE);
                            mediaPlayer.pause();
                        });

                        sbAudioBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                            @Override
                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                if (fromUser) {
                                    mediaPlayer.seekTo(progress);
                                }
                            }

                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) {

                            }

                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) {
                            }
                        });

                        break;
                }
            }
        } else {
            ivImage.setVisibility(GONE);
            flMessageLoadingContainer.setVisibility(GONE);
            flVideoMessageContainer.setVisibility(GONE);
            rlMessageAudioContainer.setVisibility(GONE);
            tvMessageBody.setVisibility(VISIBLE);
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
        builder.setNegativeButton("Cerrar", (dialog, which) -> dialog.cancel());
        final AlertDialog dialog = builder.create();

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setOnShowListener(d -> {
            ImageView image = dialog.findViewById(R.id.ivDialogImage);
            image.setImageBitmap(bitmap);
            float imageWidthInPX = (float)image.getWidth();
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
        builder.setNegativeButton("Cerrar", (dialog, which) -> dialog.cancel());
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
