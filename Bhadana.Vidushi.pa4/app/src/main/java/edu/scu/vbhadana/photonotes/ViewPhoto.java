package edu.scu.vbhadana.photonotes;

import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

public class ViewPhoto extends AppCompatActivity implements MediaPlayer.OnCompletionListener{
    public static final String ITEMCLICKED = "Itemclicked";
    private DBAdapter db;
    private int rowid;
    private ImageView imageDisplay;
    private TextView captionDisplay;
    private TextView locationDisplay;
    private Button btnMapActivity;
    private Button btnPlay;
    private Button btnStop;
    Double longitude;
    Double latitude;
    String audioPath;

    // Handling audio
    private static final String LOG_TAG = "AudioRecordTest";
    private MediaPlayer mPlayer = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_photo);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            rowid = extras.getInt(ITEMCLICKED);
            dbOperations();
        }

        btnMapActivity = (Button) findViewById(R.id.btnLaunchMap);

        //Viewing map activity
        btnMapActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                viewLocationMap();

            }
        });

        //To stop playing audio
        btnStop = (Button) findViewById(R.id.btnStopRecording);
        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mPlayer.stop();
                Toast.makeText(getApplicationContext(),"Audio Stopped",Toast.LENGTH_SHORT).show();

            }
        });

        btnPlay = (Button) findViewById(R.id.btnPlayRecording);
        //Play Audio
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                listenAudio();
            }
        });


    }

    private void listenAudio() {
       mPlayer = new MediaPlayer();
        mPlayer.setOnCompletionListener(ViewPhoto.this);
        Log.v("no path",audioPath);
        if(audioPath == null) {
            Toast.makeText(getApplicationContext(),"No audio recorded", Toast.LENGTH_SHORT).show();
        }
        else {
            try {
                mPlayer.setDataSource(audioPath);
                mPlayer.prepare();
                mPlayer.start();
            } catch (IOException e) {
                Log.e(LOG_TAG, "prepare() failed");
            }
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        Toast.makeText(getApplicationContext(), "Playback is completed", Toast.LENGTH_SHORT).show();
    }

    private void viewLocationMap() {

        Intent intent = new Intent(ViewPhoto.this, MapsActivity.class);
        Bundle b = new Bundle();
        b.putDouble("Longitude", longitude);
        b.putDouble("Latitude", latitude);
        intent.putExtras(b);
        startActivity(intent);

    }

    private void dbOperations() {

        captionDisplay = (TextView) findViewById(R.id.imageTextView);
        locationDisplay = (TextView)findViewById(R.id.txtViewAddress);
        imageDisplay = (ImageView) findViewById(R.id.imageView);
        db = DBAdapter.getInstance(this);
        db.open();
        Cursor c = db.getRow(rowid);
        String Uname = c.getString(1);
        longitude = c.getDouble(3);
        latitude = c.getDouble(4);
        String address = c.getString(5);
        audioPath = c.getString(6);
        captionDisplay.setText(Uname);
        locationDisplay.setText(address);
        imageDisplay.setImageURI(Uri.parse(c.getString(2)));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu.
        getMenuInflater().inflate(R.menu.menu_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.uninstall) {
            Uri packageURI = Uri.parse("package:edu.scu.vbhadana.photonotes");
            Intent uninstallIntent = new Intent(Intent.ACTION_DELETE, packageURI);
            startActivity(uninstallIntent);
        } else if (id == R.id.goToPhotoActivity) {
            Intent myIntent = new Intent(ViewPhoto.this, AddPhoto.class);
            startActivity(myIntent);
        }
        return super.onOptionsItemSelected(item);
    }
}


