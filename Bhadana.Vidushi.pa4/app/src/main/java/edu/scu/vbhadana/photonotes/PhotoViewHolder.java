package edu.scu.vbhadana.photonotes;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by vidushi on 5/18/16.
 */
public class PhotoViewHolder extends RecyclerView.ViewHolder {
    private final ImageView imageView;
    private final Context context;
    public TextView getImageCaption() {
        return imageCaption;
    }

    public ImageView getImageView() {
        return imageView;
    }

    private final TextView imageCaption;

    public PhotoViewHolder(View itemView,
                           final Context context) {
        super(itemView);
        this.imageView = (ImageView) itemView.findViewById(R.id.image_icon);
        this.imageCaption = (TextView) itemView.findViewById(R.id.image_caption);
        this.context = context;
    }

    public void setPhoto(final Photo photo) {
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(context, ViewPhoto.class);
                intent.putExtra(ViewPhoto.ITEMCLICKED, photo.getId());
                context.startActivity(intent);
            }
        });

    }



}
