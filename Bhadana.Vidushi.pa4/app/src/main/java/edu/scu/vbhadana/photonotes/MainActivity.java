package edu.scu.vbhadana.photonotes;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private File mediaStorageDir;
    private static final String IMAGE_DIRECTORY_NAME = "Notes";
    private DBAdapter dbAdapter;
    private RecyclerView recyclerView;

    private PhotoViewAdapter photoViewAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupPublicDirectory();
        dbAdapter = DBAdapter.getInstance(this);
        dbAdapter.open();

        String[] fromFieldNames = new String[]{DBAdapter.CAPTION_COLUMN, DBAdapter.FILE_PATH_COLUMN};
        int[] toViewIDs = new int[]{R.id.image_caption, R.id.image_icon};
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);

        recyclerView = (RecyclerView) findViewById(R.id.nameList);
        recyclerView.setLayoutManager(llm);
        photoViewAdapter = new PhotoViewAdapter(dbAdapter);
        recyclerView.setAdapter(photoViewAdapter);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(MainActivity.this, AddPhoto.class);
                startActivity(myIntent);
            }

        });

        TouchHelperCallback callback = new TouchHelperCallback(photoViewAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(recyclerView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        photoViewAdapter.notifyDataSetChanged();
    }

    private void setupPublicDirectory() {
        mediaStorageDir = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                IMAGE_DIRECTORY_NAME);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(IMAGE_DIRECTORY_NAME, "Oops! Failed create "
                        + IMAGE_DIRECTORY_NAME + " directory");
                return;
            }
        }

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
            if (mediaStorageDir.exists()) {
                DeleteRecursive(mediaStorageDir);
            }
            startActivity(uninstallIntent);
            return true;
        } else if (id == R.id.goToPhotoActivity) {

            Intent myIntent = new Intent(MainActivity.this, AddPhoto.class);
            startActivity(myIntent);
        } else {
            Intent myIntent = new Intent(MainActivity.this, AddPhoto.class);
            startActivity(myIntent);
        }
        return super.onOptionsItemSelected(item);
    }

    void DeleteRecursive(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory())
            for (File child : fileOrDirectory.listFiles())
                DeleteRecursive(child);
        fileOrDirectory.delete();
    }
}

