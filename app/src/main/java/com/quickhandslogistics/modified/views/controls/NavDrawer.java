package com.quickhandslogistics.modified.views.controls;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.quickhandslogistics.R;
import com.quickhandslogistics.modified.views.BaseActivity;
import com.quickhandslogistics.view.fragments.LogoutDialog;

import java.util.ArrayList;

public class NavDrawer {

    private final BaseActivity activity;
    private final FragmentTransaction fragmentTransaction;

    private final Toolbar toolbar;
    private final DrawerLayout drawerLayout;
    private final ViewGroup navDrawerView;

    private ArrayList<NavDrawerItem> items;
    private NavDrawerItem selectedItem;

    public NavDrawer(BaseActivity activity, Toolbar toolbar, FragmentTransaction fragmentTransaction) {
        this.activity = activity;
        this.toolbar = toolbar;
        this.fragmentTransaction = fragmentTransaction;

        items = new ArrayList<>();

        drawerLayout = activity.findViewById(R.id.drawerLayout);
        navDrawerView = activity.findViewById(R.id.navView);

        if (drawerLayout == null || navDrawerView == null) {
            throw new RuntimeException("To use this class, you must have views with the ids of drawer_layout and nav_drawer");
        }

        this.toolbar.setNavigationIcon(R.drawable.ic_sidemenu);
        this.toolbar.setNavigationOnClickListener(v -> setOpen(!isOpen()));
    }

    public void addItem(NavDrawerItem item) {
        items.add(item);
        item.navDrawer = this;
        item.toolbar = toolbar;
        item.fragmentTransaction = fragmentTransaction;
    }

    private boolean isOpen() {
        return drawerLayout.isDrawerOpen(GravityCompat.START);
    }

    public void setOpen(boolean isOpen) {
        if (isOpen) {
            drawerLayout.openDrawer(GravityCompat.START);
        } else {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    private void setSelectedItem(NavDrawerItem item) {
        if (selectedItem != null) {
            selectedItem.setSelected(false);
        }

        selectedItem = item;
        selectedItem.setSelected(true);
    }

    public void create() {
        LayoutInflater inflater = activity.getLayoutInflater();

        for (NavDrawerItem item : items) {
            item.inflate(inflater, navDrawerView);
        }
    }

    public static abstract class NavDrawerItem {
        Toolbar toolbar;
        NavDrawer navDrawer;
        FragmentTransaction fragmentTransaction;

        public abstract void inflate(LayoutInflater inflater, ViewGroup container);

        public abstract void setSelected(boolean isSelected);
    }

    public static class BasicNavDrawerItem extends NavDrawerItem implements View.OnClickListener {
        private int containerId;
        private int iconDrawable;
        private String text;

        private TextView textView;
        private View view;

        BasicNavDrawerItem(String text, int iconDrawable, int containerId) {
            this.text = text;
            this.iconDrawable = iconDrawable;
            this.containerId = containerId;
        }

        @Override
        public void inflate(LayoutInflater inflater, ViewGroup navDrawerView) {
            ViewGroup container = navDrawerView.findViewById(containerId);

            if (container == null) {
                throw new RuntimeException("Nav drawer item" + text + " could not be attached to ViewGroup. View not found.");
            }

            view = inflater.inflate(R.layout.list_item_nav_drawer, container, false);
            container.addView(view);
            view.setOnClickListener(this);

            ImageView icon = view.findViewById(R.id.list_item_nav_drawer_icon);
            textView = view.findViewById(R.id.list_item_nav_drawer_text);

            icon.setImageResource(iconDrawable);
            textView.setText(text);
        }

        @Override
        public void setSelected(boolean isSelected) {
            if (isSelected) {
                view.setBackgroundColor(ContextCompat.getColor(navDrawer.activity, R.color.navDrawerSelectedItemBackground));
            } else {
                view.setBackground(null);
            }
        }

        public void setText(String text) {
            this.text = text;

            if (view != null) {
                textView.setText(text);
            }
        }

        @Override
        public void onClick(View v) {
        }
    }

    public static class ActivityNavDrawerItem extends BasicNavDrawerItem {
        private final Fragment targetFragment;
        private String text;
        boolean showOnLaunch;

        public ActivityNavDrawerItem(Fragment targetFragment, String text, int iconDrawable, int containerId, boolean showOnLaunch) {
            super(text, iconDrawable, containerId);

            this.targetFragment = targetFragment;
            this.text = text;
            this.showOnLaunch = showOnLaunch;
        }

        @Override
        public void inflate(LayoutInflater inflater, ViewGroup navDrawer) {
            super.inflate(inflater, navDrawer);

            if (showOnLaunch) {
                toolbar.setTitle(text);
                this.navDrawer.setSelectedItem(this);
                showFragment(this.navDrawer.activity);
            }
        }

        @Override
        public void onClick(View v) {
            navDrawer.setOpen(false);

            final BaseActivity activity = navDrawer.activity;
            super.onClick(v);

            if (targetFragment instanceof LogoutDialog) {
                LogoutDialog dialog = new LogoutDialog();
                dialog.show(activity.getSupportFragmentManager(), "logoutPrompt");
            } else {
                toolbar.setTitle(text);
                navDrawer.setSelectedItem(this);
                showFragment(activity);
            }
        }

        private void showFragment(BaseActivity activity) {
            FragmentTransaction fragmentTransaction = activity.getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.frameLayoutMain, targetFragment);
            fragmentTransaction.commit();
        }
    }
}
