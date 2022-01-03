package com.agendadigital.views.modules.chats.components.views;

import android.view.View;
import android.view.ViewGroup;

public class MessageViewBuilder {

    public static View build(ViewGroup parent, int messageType) {
        MessageView view= new ItemSentView(parent);
        if (messageType == 2) {
            view = new ItemReceiveView(parent);
        }
        view.initComponents();
        return view;
    }
}
