package com.agendadigital.core.shared.infrastructure;

import com.google.firebase.messaging.FirebaseMessaging;

public class Firebase {

    private static Firebase instance;
    private String token;

    private Firebase(){
        requestToken();
    }

    public static Firebase getInstance(){
        if (instance == null){
            instance = new Firebase();
        }
        return instance;
    }

    public String getToken(){
        return token;
    }
    private void requestToken() {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        return;
                    }
                    token = task.getResult();
                });
    }
}
