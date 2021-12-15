package com.agendadigital.views.modules.chats.components.views;

import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.agendadigital.R;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;

public abstract class MessageView extends FrameLayout {

    protected TextView tvSender;
    protected CardView bubble;
    protected TextView tvMessage;
    protected TextView tvReceivedAt;
    protected ViewGroup viewGroup;

    public MessageView(@NonNull ViewGroup viewGroup) {
        super(viewGroup.getContext());
        this.viewGroup = viewGroup;
        initComponents();
    }

    public MessageView(ViewGroup viewGroup, AttributeSet attrs) {
        super(viewGroup.getContext(), attrs);
        this.viewGroup = viewGroup;
        initComponents();
    }

    private void initComponents(){
        tvSender = findViewById(R.id.sender_text_view);
        tvMessage = findViewById(R.id.message_text_view);
        tvReceivedAt = findViewById(R.id.timestamp_text_view);
        bubble = findViewById(R.id.bubble);
    }

    public abstract View initializeView();

    public void setSender(String sender) {
        if (tvSender == null) {
            this.tvSender = findViewById(R.id.sender_text_view);
        }

        tvSender.setVisibility(VISIBLE);
        tvSender.setText(sender);
    }

    public void setMessage(String message) {
        if (tvMessage == null) {
            tvMessage = findViewById(R.id.message_text_view);
        }
        tvMessage.setText(message);
    }

    public void setTimestamp(String timestamp) {
        if (tvReceivedAt == null) {
            tvReceivedAt = findViewById(R.id.timestamp_text_view);
        }
        tvReceivedAt.setText(timestamp);

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
