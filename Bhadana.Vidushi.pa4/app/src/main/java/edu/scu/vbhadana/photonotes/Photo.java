package edu.scu.vbhadana.photonotes;

/**
 * Created by vidushi on 5/15/16.
 */
public class Photo {

    private final String caption;
    private final int id;
    private final String photoFilePath;

    public Photo(int id, String caption, String photoFilePath) {
        this.id = id;
        this.caption = caption;
        this.photoFilePath = photoFilePath;
    }

    public int getId() { return id; }

    public String getPhotoFilePath() {
        return photoFilePath;
    }

    public String getCaption() {
        return caption;
    }

}
