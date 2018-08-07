package com.subratgupta.vsns;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final Integer INTERNET = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        findViewById(R.id.youtube_layout).setVisibility(View.GONE);
        findViewById(R.id.donate_layout).setVisibility(View.GONE);
        findViewById(R.id.amount_section).setVisibility(View.GONE);

        if (id == R.id.nav_home) {
            // Handle the Home action
        } else if (id == R.id.nav_youtube) {
            findViewById(R.id.youtube_layout).setVisibility(View.VISIBLE);
            askForPermission(Manifest.permission.INTERNET,INTERNET);
            WebView youtubeView = (WebView) findViewById(R.id.youtube_view);
            youtubeView.getSettings().setJavaScriptEnabled(true);
            youtubeView.loadUrl("https://m.youtube.com/channel/UCOAvF3WBpbGOBbuSn66o5cA");
        } else if (id == R.id.nav_donate) {
            if (MainActivity.appData.getEditable()){
                findViewById(R.id.amount_section).setVisibility(View.VISIBLE);
            }
                findViewById(R.id.donate_layout).setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.header_donate)).setText(MainActivity.appData.getHeader());
//            ((Button) findViewById(R.id.link_donate)).setText(MainActivity.appData.getLink());
            ((TextView) findViewById(R.id.phone_donate)).setText(MainActivity.appData.getPhone());
            ((Button) findViewById(R.id.link_donate)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Uri uri;
                    if (MainActivity.appData.getEditable()){
                        RadioGroup amount_group = (RadioGroup) findViewById(R.id.amount_radio);
                        int select_id = amount_group.getCheckedRadioButtonId();
                        RadioButton radioButton = (RadioButton) findViewById(select_id);
                        uri = Uri.parse("http://m.p-y.tm/requestPayment?recipient="+MainActivity.appData.getPhone()+"&amount="+radioButton.getText().toString()+"&comment="+MainActivity.appData.getComment_paytm());
                    }
                    else {
                        uri = Uri.parse(MainActivity.appData.getLink());
                    }
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                }
            });
            Glide.with(getApplicationContext()).load(MainActivity.appData.getQr_link()).into((ImageView) findViewById(R.id.qr_code_donate));
        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void askForPermission(String permission, Integer requestCode) {
        if (ContextCompat.checkSelfPermission(HomeActivity.this, permission) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(HomeActivity.this, permission)) {

                //This is called if user has denied the permission before
                //In this case I am just asking the permission again
                ActivityCompat.requestPermissions(HomeActivity.this, new String[]{permission}, requestCode);

            } else {

                ActivityCompat.requestPermissions(HomeActivity.this, new String[]{permission}, requestCode);
            }
        } else {
            Toast.makeText(this, "" + permission + " is already granted.", Toast.LENGTH_SHORT).show();
        }
    }
}
