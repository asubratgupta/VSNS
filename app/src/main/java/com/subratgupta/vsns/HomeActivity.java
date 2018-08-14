package com.subratgupta.vsns;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static DatabaseReference mDatabase;
    public static FirebaseData appData;
    private static final Integer INTERNET = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        db_init(findViewById(R.id.youtube_layout));

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        askForPermission(Manifest.permission.INTERNET, INTERNET);
        WebView youtubeView = (WebView) findViewById(R.id.youtube_view);
        youtubeView.getSettings().setJavaScriptEnabled(true);
        youtubeView.loadUrl("https://m.youtube.com/channel/UCOAvF3WBpbGOBbuSn66o5cA");
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
            Toast.makeText(getApplicationContext(),"Currently, this option is not available.",Toast.LENGTH_SHORT).show();
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

        /*if (id == R.id.nav_home) {
            // Handle the Home action
        } else */
        if (id == R.id.nav_youtube) {
            askForPermission(Manifest.permission.INTERNET, INTERNET);
            WebView youtubeView = (WebView) findViewById(R.id.youtube_view);
            youtubeView.getSettings().setJavaScriptEnabled(true);
            youtubeView.loadUrl("https://m.youtube.com/channel/UCOAvF3WBpbGOBbuSn66o5cA");
            db_init(findViewById(R.id.youtube_layout));
        } else if (id == R.id.nav_donate) {
            try {
                if (appData.getEditable()) {
                    findViewById(R.id.amount_section).setVisibility(View.VISIBLE);
                }
                findViewById(R.id.donate_layout).setVisibility(View.VISIBLE);
                ((TextView) findViewById(R.id.header_donate)).setText(appData.getHeader());
                ((TextView) findViewById(R.id.phone_donate)).setText(appData.getPhone());
                ((Button) findViewById(R.id.link_donate)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        try {
                            Uri uri;
                            if (appData.getEditable()) {
                                RadioGroup amount_group = (RadioGroup) findViewById(R.id.amount_radio);
                                int select_id = amount_group.getCheckedRadioButtonId();
                                RadioButton radioButton = (RadioButton) findViewById(select_id);
                                uri = Uri.parse("http://m.p-y.tm/requestPayment?recipient=" + appData.getPhone() + "&amount=" + radioButton.getText().toString() + "&comment=" + appData.getComment_paytm());
                            } else {
                                uri = Uri.parse(appData.getLink());
                            }
                            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                            startActivity(intent);
                        } catch (Exception e) {
                            db_init(findViewById(R.id.donate_layout));
                        }

                    }
                });
                Glide.with(getApplicationContext()).load(appData.getQr_link()).into((ImageView) findViewById(R.id.qr_code_donate));
            } catch (Exception e){
                Log.e("HomeActivity",e.getMessage()+"@donate");
                Toast.makeText(getApplicationContext(),"Interne Disconnected!",Toast.LENGTH_SHORT).show();
                Toast.makeText(getApplicationContext(),"Switch on Internet",Toast.LENGTH_SHORT).show();
            }

        } else if (id == R.id.nav_share) {
            try {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_SUBJECT, "VSNS");
                String sAux = "VSNS (A little help can make a great change.)\n\n";
                sAux = sAux + "https://play.google.com/store/apps/details?id=com.subratgupta.vsns";
                i.putExtra(Intent.EXTRA_TEXT, sAux);
                startActivity(Intent.createChooser(i, "choose one"));
            } catch (Exception e) {
                //e.toString();
            }
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
        }
    }

    private void db_init(final View view){
        mDatabase.child("VSNS").child("appData").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    appData = dataSnapshot.getValue(FirebaseData.class);
                    view.setVisibility(View.VISIBLE);
                } catch (Exception e) {
                    Log.e("HomeActivity", e.getMessage());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(),"No Internet Connection...",Toast.LENGTH_SHORT).show();
            }
        });

        try{
            appData.getEditable();
        }
        catch (Exception e){
            view.setVisibility(View.GONE);
            Toast.makeText(getApplicationContext(),"Oops! Internet Not Available",Toast.LENGTH_SHORT).show();
        }
    }
}
