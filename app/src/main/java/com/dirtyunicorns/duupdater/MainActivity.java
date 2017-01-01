package com.dirtyunicorns.duupdater;

import android.Manifest;
import android.content.ComponentName;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.PermissionChecker;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.dirtyunicorns.duupdater.adapters.CardAdapter;
import com.dirtyunicorns.duupdater.utils.File;
import com.dirtyunicorns.duupdater.utils.NetUtils;
import com.dirtyunicorns.duupdater.utils.ServerUtils;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private final static int REQUEST_READ_STORAGE_PERMISSION = 1;
    private final static String DIR_OFFICIAL = "Official";
    private final static String DIR_WEEKLIES = "Weeklies";
    private final static String DIR_RC = "Rc";
    private final static String DIR_GAPPS_DYNAMIC = "gapps/Dynamic";
    private final static String DIR_GAPPS_TBO = "gapps/TBO";
    private DrawerLayout mDrawerLayout;
    private Fragment frag;
    private Toolbar toolbar;
    private Snackbar snackbar;
    private RecyclerView rv;
    private CardAdapter adapter;
    private GetFiles getFiles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        InitInterface();
        InitPermissions();
        InitOfficial();
    }

    public void InitInterface() {
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

        rv = (RecyclerView) findViewById(R.id.rv);
        rv.setHasFixedSize(true);
        Animation anim = AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left);
        rv.setAnimation(anim);
        rv.animate();
        adapter = new CardAdapter(this);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);
        snackbar = Snackbar.make(rv, "", Snackbar.LENGTH_INDEFINITE);

        if (!NetUtils.isOnline(this)) {
            showSnackBar(R.string.no_internet_snackbar);
        } else {
            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

            assert navigationView != null;
            navigationView.setNavigationItemSelectedListener(this);
        }
    }

    private void InitOfficial() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        assert navigationView != null;
        navigationView.setCheckedItem(R.id.official);
        UpdateData(DIR_OFFICIAL, true);
    }

    private void UpdateData(String dir, boolean device) {
        if (NetUtils.isOnline(this)) {
            if (getFiles != null && getFiles.getStatus() == AsyncTask.Status.RUNNING) {
                getFiles.cancel(true);
            }
            getFiles = new GetFiles(dir, device);
            getFiles.execute();
        } else {
            adapter.removeItem();
            showSnackBar(R.string.no_internet_snackbar);
        }
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

        mDrawerLayout.closeDrawers();
        hideSnackBar();
        switch (item.getItemId()) {
            case R.id.official:
                UpdateData(DIR_OFFICIAL, true);
                break;
            case R.id.weeklies:
                UpdateData(DIR_WEEKLIES, true);
                break;
            case R.id.rc:
                UpdateData(DIR_RC, true);
                break;
            case R.id.gappsdynamic:
                UpdateData(DIR_GAPPS_DYNAMIC, false);
                break;
            case R.id.gappstbo:
                UpdateData(DIR_GAPPS_TBO, false);
                break;
        }
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

    public void InitWeeklies() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        assert navigationView != null;
        navigationView.setCheckedItem(R.id.weeklies);
        UpdateData(DIR_WEEKLIES, true);
    }

    public void InitRc() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        assert navigationView != null;
        navigationView.setCheckedItem(R.id.rc);
        UpdateData(DIR_RC, true);
    }

    public void InitDynamicGapps() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        assert navigationView != null;
        navigationView.setCheckedItem(R.id.gappsdynamic);
        UpdateData(DIR_GAPPS_DYNAMIC, false);
    }

    public void InitTboGapps() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        assert navigationView != null;
        navigationView.setCheckedItem(R.id.gappstbo);
        UpdateData(DIR_GAPPS_TBO, false);
    }

    class GetFiles extends AsyncTask<String, String, ArrayList<File>> {

        private String dir;
        private Boolean device;

        GetFiles(String dir, boolean device) {
            this.dir = dir;
            this.device = device;
            adapter.removeItem();
        }

        @Override
        protected ArrayList<File> doInBackground(String... params) {
            return ServerUtils.getFiles(dir, device);
        }

        protected void onPostExecute(ArrayList<File> result) {
            if (!result.isEmpty()) {
                adapter.addItem(result);
            } else {
                showSnackBar(R.string.no_files_to_show);
            }

        }
    }
}
