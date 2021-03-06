package com.quickhandslogistics.controls;

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
import com.quickhandslogistics.contracts.DashBoardContract;
import com.quickhandslogistics.utils.AppUtils;
import com.quickhandslogistics.utils.CustomDialogWarningListener;
import com.quickhandslogistics.utils.CustomProgressBar;
import com.quickhandslogistics.views.BaseActivity;
import com.quickhandslogistics.views.attendance.TimeClockAttendanceFragment;
import com.quickhandslogistics.views.customerSheet.CustomerSheetFragment;
import com.quickhandslogistics.views.workSheet.WorkSheetFragment;

import java.util.ArrayList;

import kotlin.Pair;

public class NavDrawer {

    private final BaseActivity activity;
    private final FragmentTransaction fragmentTransaction;
    private final DashBoardContract.View.OnFragmentInteractionListener onFragmentInteractionListener;

    private final Toolbar toolbar;
    private final DrawerLayout drawerLayout;
    private final ViewGroup navDrawerView;

    private ArrayList<NavDrawerItem> items;
    private NavDrawerItem selectedItem;

    public NavDrawer(BaseActivity activity, Toolbar toolbar, FragmentTransaction fragmentTransaction, DashBoardContract.View.OnFragmentInteractionListener onFragmentInteractionListener) {
        this.activity = activity;
        this.toolbar = toolbar;
        this.fragmentTransaction = fragmentTransaction;
        this.onFragmentInteractionListener = onFragmentInteractionListener;

        items = new ArrayList<>();

        drawerLayout = activity.findViewById(R.id.drawerLayout);
        navDrawerView = activity.findViewById(R.id.navView);

        if (drawerLayout == null || navDrawerView == null) {
            throw new RuntimeException("To use this class, you must have views with the ids of drawer_layout and nav_drawer");
        }

        this.toolbar.setNavigationIcon(R.drawable.ic_sidemenu);
        this.toolbar.setNavigationOnClickListener(v -> {
            AppUtils.hideSoftKeyboard(activity);
            setOpen(!isOpen());
        });
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

    private void invalidateOptionMenu(String title) {
        onFragmentInteractionListener.onNewFragmentReplaced(title);
    }

    private void showLogoutDialog() {
        onFragmentInteractionListener.onLogoutOptionSelected();
    }

    public void create() {
        LayoutInflater inflater = activity.getLayoutInflater();

        for (NavDrawerItem item : items) {
            item.inflate(inflater, navDrawerView);
        }
    }

    public void updateWorkSheetList() {
        Fragment fragment = activity.getSupportFragmentManager().findFragmentByTag(WorkSheetFragment.class.getSimpleName());
        if (fragment != null && fragment.isVisible()) {
            ((WorkSheetFragment) fragment).fetchWorkSheetList();
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

            view = inflater.inflate(R.layout.item_nav_drawer, container, false);
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

    public static class AppNavDrawerItem extends BasicNavDrawerItem {
        private final Fragment targetFragment;
        private String text;
        boolean showOnLaunch;

        /**
         * Pair contains two items. First is 'Tab Title Text' and Second is 'isShowOnLaunch'
         */
        public AppNavDrawerItem(Fragment targetFragment, int iconDrawable, int containerId, Pair<String, Boolean> pair) {
            super(pair.getFirst(), iconDrawable, containerId);

            this.targetFragment = targetFragment;
            this.text = pair.getFirst();
            this.showOnLaunch = pair.getSecond();
        }

        @Override
        public void inflate(LayoutInflater inflater, ViewGroup navDrawer) {
            super.inflate(inflater, navDrawer);

            if (showOnLaunch) {
//                toolbar.setTitle(text);
                this.navDrawer.setSelectedItem(this);
                showFragment(this.navDrawer.activity, false);
                this.navDrawer.invalidateOptionMenu(text);
            }
        }

        @Override
        public void onClick(View v) {
            navDrawer.setOpen(false);

            final BaseActivity activity = navDrawer.activity;
            super.onClick(v);

            if (text.equals(activity.getString(R.string.logout))) {
                navDrawer.showLogoutDialog();
            } else {
                Fragment currentFragment = activity.getSupportFragmentManager().findFragmentById(R.id.frameLayoutMain);
                if (currentFragment != null && checkCurrentFragment(currentFragment) && targetedFragment(targetFragment)) {
                    if (currentFragment instanceof TimeClockAttendanceFragment) {
                        if (((TimeClockAttendanceFragment) currentFragment).onDataChanges()) {
                            showLeavePageAlert(activity, text);
                        } else {
                            setFragment(activity, text);
                        }
                    }
                    if (currentFragment instanceof CustomerSheetFragment) {
                        if (((CustomerSheetFragment) currentFragment).onDataChanges()) {
                            showLeavePageAlert(activity, text);
                        } else {
                            setFragment(activity, text);
                        }
                    }
                } else {
                    setFragment(activity, text);
                }
            }
        }

        private boolean targetedFragment(Fragment targetFragment) {
            return !targetFragment.getClass().getSimpleName().equals(TimeClockAttendanceFragment.class.getSimpleName())|| targetFragment.getClass().getSimpleName().equals(CustomerSheetFragment.class.getSimpleName());
        }

        private boolean checkCurrentFragment(Fragment currentFragment) {
            return currentFragment.getClass().getSimpleName().equals(TimeClockAttendanceFragment.class.getSimpleName()) || currentFragment.getClass().getSimpleName().equals(CustomerSheetFragment.class.getSimpleName());
        }

        private void showLeavePageAlert(BaseActivity activity, String text) {
            CustomProgressBar.Companion.getInstance().showLeaveDialog(activity.getString(R.string.discard_leave_alert_message), activity, new CustomDialogWarningListener() {
                @Override
                public void onConfirmClick() {
                    setFragment(activity, text);
                }

                @Override
                public void onCancelClick() {
                }
            });
        }

        public void setFragment(BaseActivity activity, String text) {
//            toolbar.setTitle(text);
            navDrawer.setSelectedItem(this);
            showFragment(activity, true);
            navDrawer.invalidateOptionMenu(text);
        }

        private void showFragment(BaseActivity activity, Boolean isClearArguments) {
            if (isClearArguments) {
                targetFragment.setArguments(null);
            }
            Fragment currentFragment = activity.getSupportFragmentManager().findFragmentById(R.id.frameLayoutMain);
            if (currentFragment != null && currentFragment.getClass().getSimpleName().equals(targetFragment.getClass().getSimpleName())) {
                return;
            }
            FragmentTransaction fragmentTransaction = activity.getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.frameLayoutMain, targetFragment, targetFragment.getClass().getSimpleName());
            fragmentTransaction.commit();
        }
    }
}
