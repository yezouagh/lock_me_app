package com.yez_inc.lock_me_app;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.support.design.widget.NavigationView;
import android.view.View;
import android.widget.TextView;

import com.yez_inc.lock_me_app.cropImage.CropImage;
import com.yez_inc.lock_me_app.lockmeapp.About;
import com.yez_inc.lock_me_app.lockmeapp.Setting;
import com.yez_inc.lock_me_app.lockmeapp.AddWidgets;
import com.yez_inc.lock_me_app.lockmeapp.locks.LockListener;
import com.yez_inc.lock_me_app.lockmeapp.utils.LockManager;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    public static final String TAG_Setting = "Setting";
    static final String TAG_addWidgets = "addWidgets";
    static final String TAG_About = "About";
    public static final String TEMP_PHOTO_FILE_NAME = "temp_photo.jpg";
    public static final int REQUEST_CODE_GALLERY = 0x1;
    public static final int REQUEST_CODE_TAKE_PICTURE = 0x2;
    public static final int REQUEST_CODE_CROP_IMAGE = 0x3;
    Setting setting;
    AddWidgets addWidgets;
    About about;
    private File mFileTemp;
    FragmentManager fragmentManager;
    LockManager lockManager;
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        lockManager = new LockManager(this.getApplicationContext());
        fragmentManager = getSupportFragmentManager();
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        final NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close){
            @Override public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                TextView text = (TextView) navigationView.getHeaderView(0).findViewById(R.id.display_name);
                String txt = lockManager.getDisplay_Name();
                if (!txt.equals("")) {text.setText(txt);}
                text = (TextView) navigationView.getHeaderView(0).findViewById(R.id.email);
                text.setText(lockManager.getEmail());
            }
        };
        drawer.addDrawerListener(toggle);
        //noinspection deprecation
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_manage);openSetting();
    }
    @Override public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
    @Override public boolean onNavigationItemSelected(@SuppressWarnings("NullableProblems") MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_manage) {
            openSetting();
        } else if (id == R.id.nav_share) {
            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            String shareBody = "Get Locker The Best Lock screen for android now!!!\n\nGet it here : https://play.google.com/store/apps/details?id=com.pilockerstable";
            sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
            startActivity(Intent.createChooser(sharingIntent, "Share using"));
        } else if (id == R.id.nav_send) {
            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            String shareBody = "Get Locker The Best Lock screen for android now!!!\n\nGet it here : https://play.google.com/store/apps/details?id=com.pilockerstable";
            sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
            startActivity(Intent.createChooser(sharingIntent, "Send using"));
        } else if (id == R.id.about) {openAbout();}
        else if (id == R.id.nav_more_apps) {
            Uri uri = Uri.parse("https://play.google.com/store/apps/developer?id=Pi-Developers");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        }else if (id == R.id.add_widgets) {openAddWidgets();}
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    private void startCropImage() {
        Intent intent = new Intent(this, CropImage.class);
        intent.putExtra(CropImage.IMAGE_PATH, mFileTemp.getPath());
        intent.putExtra(CropImage.SCALE, true);
        intent.putExtra(CropImage.ASPECT_X, 0);
        intent.putExtra(CropImage.ASPECT_Y, 0);
        startActivityForResult(intent, REQUEST_CODE_CROP_IMAGE);
    }
    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_GALLERY:
                    try {
                        InputStream inputStream = getContentResolver().openInputStream(data.getData());
                        FileOutputStream fileOutputStream = new FileOutputStream(mFileTemp);
                        copyStream(inputStream, fileOutputStream);
                        fileOutputStream.close();
                        if (inputStream != null) {
                            inputStream.close();
                        }
                        startCropImage();
                    } catch (Exception e) {
                        Log.e(TAG_Setting, "Error while creating temp file", e);}
                    break;
                case REQUEST_CODE_TAKE_PICTURE:
                    startCropImage();break;
                case REQUEST_CODE_CROP_IMAGE:
                    String path = data.getStringExtra(CropImage.IMAGE_PATH);
                    if (path != null) {
                        lockManager.setIsBackground_Color(false);
                        lockManager.setLockBackground(mFileTemp.getPath());
                    }
                    break;
            }
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
    public static void copyStream(InputStream input, OutputStream output) throws IOException {
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = input.read(buffer)) != -1) {
            output.write(buffer, 0, bytesRead);
        }
    }
    private void openGallery() {
        mFileTemp = new File(getFilesDir(), TEMP_PHOTO_FILE_NAME);
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, REQUEST_CODE_GALLERY);
    }
    LockListener gallery_listener=new LockListener() {
        @Override public void onLockInteraction(String action) {
            if (action.equals("openGallery"))
                openGallery();
        }
    };
    void openSetting(){
        setting = (Setting)fragmentManager.findFragmentByTag(TAG_Setting);
        if (setting==null){
            setting = new Setting();
            setting.setGallery_listener(gallery_listener);
        }
        replaceFragment(setting,TAG_Setting);
    }
    void openAddWidgets(){
        addWidgets = (AddWidgets)fragmentManager.findFragmentByTag(TAG_addWidgets);
        if (addWidgets==null){
            addWidgets = new AddWidgets();
        }
        replaceFragment(addWidgets,TAG_addWidgets);
    }
    void openAbout(){
        about = (About)fragmentManager.findFragmentByTag(TAG_About);
        if (about==null){
            about = new About();
        }
        replaceFragment(about,TAG_About);
    }
    void replaceFragment(Fragment fragment, String TAG){
        fragmentManager.beginTransaction().replace(R.id.content_main,fragment,TAG).commit();
    }
}
