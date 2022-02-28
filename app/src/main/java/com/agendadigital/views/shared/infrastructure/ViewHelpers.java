package com.agendadigital.views.shared.infrastructure;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import com.agendadigital.MainActivity;
import com.agendadigital.R;
import androidx.appcompat.app.ActionBar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ViewHelpers {

    public static ActionBar getActionBar(Activity activity) {
        ActionBar actionBar = null;
        MainActivity mainActivity = ((MainActivity) activity);
        if (mainActivity != null) {
            actionBar = mainActivity.getSupportActionBar();
        }
        return actionBar;
    }

    public static void initRecyclerView(Context context, RecyclerView recyclerView) throws Exception {
        DividerItemDecoration itemDecoration = new DividerItemDecoration(context, DividerItemDecoration.VERTICAL);
        Drawable drawable = ContextCompat.getDrawable(context, R.drawable.divider);
        if (drawable == null) {
            throw new Exception("Divider not found");
        }
        itemDecoration.setDrawable(drawable);
        recyclerView.addItemDecoration(itemDecoration);
        recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
    }

}
