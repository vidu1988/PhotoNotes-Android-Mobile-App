package edu.scu.vbhadana.photonotes;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by vidushi on 5/15/16.
 */


public class AddPhoto extends AppCompatActivity implements SensorEventListener, MediaPlayer.OnCompletionListener {

    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    public static final int MEDIA_TYPE_IMAGE = 1;
   // public static final int MEDIA_TYPE_AUDIO = 2;
    private Uri fileUri = null;
    //private Uri fileAudioUri = null;
    private static final String IMAGE_DIRECTORY_NAME = "Notes";
    private static final String AUDIO_DIRECTORY_NAME = "NotesAudio";
    private ImageView imageView;
    private ImageButton takePhotos;
    private ImageButton saveButton;
    private String captionString = null;
    private TextView textPreview;
    DBAdapter db = DBAdapter.getInstance(this);
    TouchDrawView view;
    EditText editTextLongitude;
    EditText editTextLatitude;
    EditText editTextAddress;

    double longitude;
    double latitude;
    String locationAddress;

    private SensorManager mSensorManager;
    private float mAccel; // acceleration apart from gravity
    private float mAccelCurrent; // current acceleration including gravity
    private float mAccelLast; // last acceleration including gravity
    private int count;
    ImageButton btnLocation;
    AppLocationService appLocationService;

    // Handling audio
    private static final String LOG_TAG = "AudioRecordTest";
    private static String mFileName = null;
    private MediaRecorder mRecorder = null;
    private MediaPlayer mPlayer = null;
    boolean flag = false;

    int progress = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_photo);
        imageView = (ImageView) findViewById(R.id.imageView);
        textPreview = (TextView) findViewById(R.id.textPreview);
        editTextLongitude = (EditText)findViewById(R.id.txtLongitude);
        editTextLatitude = (EditText) findViewById(R.id.txtLatitude);
        editTextAddress = (EditText) findViewById(R.id.txtAddress);
        saveButton = (ImageButton) (findViewById(R.id.saveImage));
        final ProgressBar pb = (ProgressBar) findViewById(R.id.progressBar);


        final ImageButton recordButton = (ImageButton)findViewById(R.id.btnRecord);
        recordButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                pb.setVisibility(View.VISIBLE);

                Toast.makeText(getApplicationContext(),"Recording started",Toast.LENGTH_SHORT).show();
                    startRecording();
                flag = true;

            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                for (; ; ) {
                    if (flag) {
                        progress += 5;
                        if (progress >= 100) progress -= 100;
                        ProgressBar pb = (ProgressBar) findViewById(R.id.progressBar);
                        pb.setProgress(progress);
                        Log.i("jsun", "Set progress to " + progress);
                    }
                    try {
                        Thread.sleep(200);
                    } catch (Exception e) {
                    }
                }
            }
        }).start();

        final ImageButton stopButton = (ImageButton)findViewById(R.id.btnStop);
        stopButton.setOnClickListener(new View.OnClickListener() {
            // boolean mStartRecording = true;

            @Override
            public void onClick(View v) {

                stopRecording();
                Toast.makeText(getApplicationContext(),"Recording Stopped",Toast.LENGTH_SHORT).show();
                flag = false;
            }

        });

        // Saving Image
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    saveImageFromView();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });

        takePhotos = (ImageButton) (findViewById(R.id.clickPhoto));

        //Clicking photo from camera
        takePhotos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dispatchTakePictureIntent();

            }
        });


        btnLocation = (ImageButton) (findViewById(R.id.saveLocation));
        //Finding location of picture
        btnLocation.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                appLocationService = new AppLocationService(AddPhoto.this);
                //tvAddress.setEnabled(false);
                Location location = appLocationService.getLocation(LocationManager.GPS_PROVIDER);

                if (location != null) {
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();

                    LocationAddress locationAddress = new LocationAddress();
                    locationAddress.getAddressFromLocation(latitude, longitude,
                    getApplicationContext(), new GeocoderHandler());
                    editTextLongitude.setText(Double.toString(longitude));
                    editTextLatitude.setText(Double.toString(latitude));
                    //editTextAddress.setText();
                } else {
                    showSettingsAlert();
                }

            }
        });


        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        count = 0;
        mAccel = 0.00f;
        mAccelCurrent = SensorManager.GRAVITY_EARTH;
        mAccelLast = SensorManager.GRAVITY_EARTH;


        mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        //mFileName += "/audiorecordtest.3gp";

        String timeStamp = new SimpleDateFormat("yyyy_MMdd_HHmmss", Locale.getDefault()).format(new Date());
       mFileName += "/"+ timeStamp + ".3gp";





        final ImageButton playButton = (ImageButton)findViewById(R.id.btnPlay);
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPlayer = new MediaPlayer();
                mPlayer.setOnCompletionListener(AddPhoto.this);
                try {
                    mPlayer.setDataSource(mFileName);
                    mPlayer.prepare();
                    mPlayer.start();
                } catch (IOException e) {
                    Log.e(LOG_TAG, "prepare() failed");
                }
            }
        });
    }


    private void startRecording() {
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(mFileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }

        mRecorder.start();
    }

    private void stopRecording() {
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;

    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        Toast.makeText(getApplicationContext(), "playback is completed", Toast.LENGTH_SHORT).show();
    }

    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                AddPhoto.this);
        alertDialog.setTitle("SETTINGS");
        alertDialog.setMessage("Enable Location Provider! Go to settings menu?");
        alertDialog.setPositiveButton("Settings",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(
                                Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        AddPhoto.this.startActivity(intent);
                    }
                });
        alertDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        alertDialog.show();
    }

    private class GeocoderHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
           // String locationAddress;
            switch (message.what) {
                case 1:
                    Bundle bundle = message.getData();
                    locationAddress = bundle.getString("address");
                    break;
                default:
                    locationAddress = null;
            }
            editTextAddress.setText(locationAddress);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this,
                mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        mSensorManager.unregisterListener(this);
        super.onPause();
    }

    public void onSensorChanged(SensorEvent se) {
        float x = se.values[0];
        float y = se.values[1];
        float z = se.values[2];
        mAccelLast = mAccelCurrent;
        mAccelCurrent = (float) Math.sqrt((double) (x*x + y*y + z*z));
        float delta = mAccelCurrent - mAccelLast;
        mAccel = mAccel * 0.9f + delta * 0.1f; // perform low-cut filter

        displayAcceleration();
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    private void displayAcceleration() {

        float accel = Math.abs( mAccel);
        if (accel > 1.0f) {
            view.clear();
        }

    }

    private void saveImageFromView() throws IOException {
        if (fileUri == null) {
            Toast.makeText(this, "Set Caption and/or Click Image", Toast.LENGTH_SHORT).show();
            return;
        }
        Point size = getBitmapSize(fileUri);
        int imageWidth = size.x, imageHeight = size.y;

        Bitmap bitmap = BitmapFactory.decodeFile(fileUri.getPath()).copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(bitmap);
        float circleRadius = imageWidth * 10.0f / view.getWidth();

        for (Point point : view.getPoints()) {
            int x = (int) Math.floor((point.x * 1.0 * imageWidth) / view.getWidth());
            int y = (int) Math.floor((point.y * 1.0 * imageHeight) / view.getHeight());

            canvas.drawCircle(x, y, circleRadius, view.getPaint());
        }
        File outputFile = new File(fileUri.getPath());
        OutputStream outputStream = null;
        try {
            outputStream = new BufferedOutputStream(new FileOutputStream(outputFile));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        } finally {
            if(outputStream != null) {
                outputStream.close();
            }
        }
        database_operations(fileUri);

    }

    private Point getBitmapSize(Uri fileUri) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(fileUri.getPath(), options);
        return new Point(options.outWidth, options.outHeight);
    }

    private void database_operations(Uri fileUri) {

        EditText editText = (EditText) findViewById(R.id.editText);
        captionString = String.valueOf(editText.getText());
        db.open();
        if (captionString.isEmpty() || fileUri == null) {
            Toast.makeText(this, "Set Caption and/or Click Image", Toast.LENGTH_SHORT).show();
        } else {
            Log.v("vidu",mFileName);
            Log.v("path",fileUri.toString());
            long id = db.insertRow(captionString, fileUri.toString(),longitude,latitude,locationAddress,
                    Uri.fromFile(new File(mFileName)).toString());

            finish();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.uninstall) {
            Intent uninstallIntent = new Intent(Intent.ACTION_DELETE);
            uninstallIntent.setData(Uri.parse("package:edu.scu.vbhadana.photonotes"));
            startActivity(uninstallIntent);
            return true;
        } else if (id == R.id.goToPhotoActivity) {
            Intent myIntent = new Intent(AddPhoto.this, AddPhoto.class);
            startActivity(myIntent);
        } else {
            Intent myIntent = new Intent(AddPhoto.this, AddPhoto.class);
            startActivity(myIntent);
        }
        return super.onOptionsItemSelected(item);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // if the result is capturing Image
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {

                // Display image in image view (Successful Capture)
                previewCapturedImage();

            } else if (resultCode == RESULT_CANCELED) {
                // user cancelled Image capture
                Toast.makeText(getApplicationContext(),
                        "User cancelled image capture", Toast.LENGTH_SHORT)
                        .show();
            } else {
                // failed to capture image
                Toast.makeText(getApplicationContext(),
                        "Sorry! Failed to capture image", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    private void previewCapturedImage() {
        try {

            view = (TouchDrawView) findViewById(R.id.myview);
            if (view == null) {
                Log.e("Vidushi", "we have a problem");
            }
            //imageView.setVisibility(View.VISIBLE);
            view.setVisibility(View.VISIBLE);

            // Reducing size of image file to handle OutOfMemory Exception for large images
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 8;
            final Bitmap bitmap = BitmapFactory.decodeFile(fileUri.getPath(), options);

            // To create bitmap of size which has same width and height as of custom view.
            Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, view.getWidth(), view.getHeight(), true);
            view.setImageBitmap(scaledBitmap);
            textPreview.setVisibility(View.INVISIBLE);


        } catch (NullPointerException e) {
            e.printStackTrace();
        }

    }

    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // Saving file url in bundle as it will be null on screen orientation changes
        outState.putParcelable("file_uri", fileUri);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // get the file url
        fileUri = savedInstanceState.getParcelable("file_uri");
    }


    private void dispatchTakePictureIntent() {

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        // start the image capture Intent
        startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
    }

    public Uri getOutputMediaFileUri(int type) {

        return Uri.fromFile(getOutputMediaFile(type));
    }

    private File getOutputMediaFile(int type) {

        // External sdcard location //sdcard/Pictures/Hello Camera
        File mediaStorageDir = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                IMAGE_DIRECTORY_NAME);


        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(IMAGE_DIRECTORY_NAME, "Oops! Failed create "
                        + IMAGE_DIRECTORY_NAME + " directory");
                return null;
            }
        }

        String timeStamp = new SimpleDateFormat("yyyy_MMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "IMG_" + timeStamp + ".jpg");
        } else {
            return null;
        }

        return mediaFile;
    }




}


