package com.dirtyunicorns.duupdater;

import android.Manifest;
import android.content.ComponentName;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.PermissionChecker;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.dirtyunicorns.duupdater.fragments.FragmentGappsBeans;
import com.dirtyunicorns.duupdater.fragments.FragmentHyperunicorns;
import com.dirtyunicorns.duupdater.fragments.FragmentGappsTR;
import com.dirtyunicorns.duupdater.fragments.FragmentSettings;
import com.dirtyunicorns.duupdater.utils.Utils;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private final static int REQUEST_READ_STORAGE_PERMISSION = 1;
    private DrawerLayout mDrawerLayout;
    private Fragment frag;
    private Toolbar toolbar;
    private Snackbar snackbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        InitInterface();
        InitPermissions();
        InitHyperunicorns();

        com.dirtyunicorns.duupdater.utils.Preferences.themeMe(this);
    }

    public void InitInterface() {
        View view = findViewById(R.id.content_frame);

        assert toolbar != null;
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {

            @Override
            public void onDrawerClosed(View v) {
                super.onDrawerClosed(v);
            }

            @Override
            public void onDrawerOpened(View v) {
                super.onDrawerOpened(v);
            }
        };
        actionBarDrawerToggle.syncState();

        snackbar = Snackbar.make(view, "", Snackbar.LENGTH_INDEFINITE);
        if (!Utils.isOnline(this)) {
            showSnackBar(R.string.no_internet_snackbar);
        } else {
            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

            assert navigationView != null;
            navigationView.setNavigationItemSelectedListener(this);
        }
    }

    private void InitHyperunicorns() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        assert navigationView != null;
        navigationView.setCheckedItem(R.id.hyperunicorns);
        frag = new FragmentHyperunicorns();
        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        collapsingToolbarLayout.setCollapsedTitleTextColor(getResources().getColor(R.color.collapsing_toolbar_layout_collapsed_title_color));
        collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(R.color.collapsing_toolbar_layout_expanded_title_color));
        collapsingToolbarLayout.setTitle(getString(R.string.hyperunicorns));
        UpdateFragment();
    }

    private void UpdateFragment() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content_frame, frag);
        ft.commit();
    }

    public void InitPermissions() {
        if (Build.VERSION.SDK_INT >= 23 && PermissionChecker
                .checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) !=
                PermissionChecker.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_READ_STORAGE_PERMISSION);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        collapsingToolbarLayout.setCollapsedTitleTextColor(getResources().getColor(R.color.collapsing_toolbar_layout_collapsed_title_color));
        collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(R.color.collapsing_toolbar_layout_expanded_title_color));
        int id = item.getItemId();
        switch (id) {
            case R.id.hyperunicorns:
                frag = new FragmentHyperunicorns();
                collapsingToolbarLayout.setTitle(getString(R.string.hyperunicorns));
                break;
            case R.id.gappsbeans:
                frag = new FragmentGappsBeans();
                collapsingToolbarLayout.setTitle(getString(R.string.beans_gapps));
                break;
            case R.id.gappstr:
                frag = new FragmentGappsTR();
                collapsingToolbarLayout.setTitle(getString(R.string.tr_gapps));
                break;
            case R.id.settings:
                frag = new FragmentSettings();
                collapsingToolbarLayout.setTitle(getString(R.string.settings_alt));
        }
        mDrawerLayout.closeDrawers();
        hideSnackBar();
        UpdateFragment();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        menu.findItem(R.id.hide_app_icon).setChecked(!isLauncherIconEnabled());
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.hide_app_icon:
                boolean checked = item.isChecked();
                item.setChecked(!checked);
                setLauncherIconEnabled(checked);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void showSnackBar(int resId) {
        hideSnackBar();
        snackbar.setText(resId).show();
    }

    public void hideSnackBar() {
        if (snackbar.isShown()) snackbar.dismiss();
    }

    public boolean isLauncherIconEnabled() {
        PackageManager pm = getPackageManager();
        return (pm.getComponentEnabledSetting(new ComponentName(this, com.dirtyunicorns.duupdater.LauncherActivity.class)) != PackageManager.COMPONENT_ENABLED_STATE_DISABLED);
    }

    public void setLauncherIconEnabled(boolean enabled) {
        int newState;
        PackageManager pm = getPackageManager();
        if (enabled) {
            newState = PackageManager.COMPONENT_ENABLED_STATE_ENABLED;
        } else {
            newState = PackageManager.COMPONENT_ENABLED_STATE_DISABLED;
        }
        pm.setComponentEnabledSetting(new ComponentName(this, com.dirtyunicorns.duupdater.LauncherActivity.class), newState, PackageManager.DONT_KILL_APP);
    }

    public void InitBeansGapps() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        assert navigationView != null;
        navigationView.setCheckedItem(R.id.gappsbeans);
        frag = new FragmentGappsBeans();
        UpdateFragment();
    }

    public void InitTrGapps() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        assert navigationView != null;
        navigationView.setCheckedItem(R.id.gappstr);
        frag = new FragmentGappsTR();
        UpdateFragment();
    }
}