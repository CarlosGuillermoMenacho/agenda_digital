package com.agendadigital.views.modules.chats.components.views;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.agendadigital.R;

import androidx.annotation.NonNull;

public class ItemSentView extends MessageView {

    public ItemSentView(@NonNull ViewGroup viewGroup) {
        super(viewGroup);
    }

    public ItemSentView(ViewGroup viewGroup, AttributeSet attrs) {
        super(viewGroup, attrs);
    }

    @Override
    public View initializeView() {
        View view = null;
        LayoutInflater inflater = (LayoutInflater)
                viewGroup.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (inflater != null) {
            view = inflater.inflate(R.layout.chat_item_sent, viewGroup, false);
        }
        return view;
    }
}
