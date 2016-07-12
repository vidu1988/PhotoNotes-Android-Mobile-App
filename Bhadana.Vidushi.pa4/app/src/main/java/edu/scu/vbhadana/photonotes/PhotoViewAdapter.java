package edu.scu.vbhadana.photonotes;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vidushi on 5/18/16.
 */
public class PhotoViewAdapter extends RecyclerView.Adapter<PhotoViewHolder> implements DBAdapter.Observer{
    private final List<Photo> photoList = new ArrayList<>();
    private final DBAdapter dbAdapter;

    public PhotoViewAdapter(DBAdapter dbAdapter) {
        this.dbAdapter = dbAdapter;
        this.dbAdapter.setObserver(this);
        repopulateData();
    }

    public void repopulateData() {
        System.err.println("RepopulateData");
        final Cursor allRowsCursor = dbAdapter.getAllRows();

        photoList.clear();

        allRowsCursor.moveToFirst();
        while(!allRowsCursor.isAfterLast()) {
            String caption = allRowsCursor.getString(1);
            String filePath = allRowsCursor.getString(2);
            Photo photo = new Photo(allRowsCursor.getInt(0),
                    caption, filePath);
            photoList.add(photo);
            allRowsCursor.moveToNext();
        }

        notifyDataSetChanged();
    }

    @Override
    public PhotoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.namecard_layout, parent, false);
        return new PhotoViewHolder(itemView, parent.getContext());
    }

    @Override
    public void onBindViewHolder(PhotoViewHolder holder, int position) {
        Photo photo = photoList.get(position);
        holder.getImageCaption().setText(photo.getCaption());
        String filePath = Uri.parse(photo.getPhotoFilePath()).getPath();
        holder.getImageView().setImageBitmap(generateThumbnail(filePath));
        holder.setPhoto(photo);
    }

    @Override
    public int getItemCount() {
        return photoList.size();
    }

    private static Bitmap generateThumbnail(String path) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 8;
        return BitmapFactory.decodeFile(path, options);
    }

    // called by touch helper callback
    public void onItemDismissed(int position) {
        Photo photo = photoList.remove(position);
        dbAdapter.deleteRow(photo.getCaption(), photo.getPhotoFilePath());
        notifyItemRemoved(position);
    }

    // called by touch helper callback
    public boolean onItemMove(int fromPosition, int toPosition) {
        Photo temp = photoList.get(fromPosition);
        photoList.set(fromPosition, photoList.get(toPosition));
        photoList.set(toPosition, temp);
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }


    @Override
    public void onInserted() {
        repopulateData();
    }
}
