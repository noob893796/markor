package net.gsantner.markor.util;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;
import java.io.InputStream;

public class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
    Bitmap imageResult;
    ImageView mImageView;
    public DownloadImageTask(ImageView imageView) {
        this.mImageView = imageView;
    }
    public Bitmap doInBackground(String... urls) {
        String urldisplay = urls[0];
        Bitmap mIcon11 = null;
        try {
            InputStream in = new java.net.URL(urldisplay).openStream();
            mIcon11 = BitmapFactory.decodeStream(in);

            imageResult = mIcon11;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mIcon11;
    }
    protected void onPostExecute(Bitmap result) {
        System.out.println(result);
        this.imageResult = result;
        System.out.println(imageResult);
    }
}