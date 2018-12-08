package net.gsantner.markor.activity;

import android.graphics.Bitmap;
import android.support.test.filters.SmallTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;
import android.widget.ImageView;

import net.gsantner.markor.util.DownloadImageTask;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.InputStream;

import static android.support.test.InstrumentationRegistry.getContext;

/**
 * Created by Rado on 11/30/2018.
 */


@RunWith(AndroidJUnit4.class)
@SmallTest
public class DownloadImageTaskTest {
    private InputStream image = this.getClass().getClassLoader()
            .getResourceAsStream("dice_test.png");


    @Rule
    public ActivityTestRule<MainActivity> mActivityRule
            = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void doInBackground() throws Exception {
        System.out.println(image);
        String input = "https://upload.wikimedia.org/wikipedia/commons/4/47/PNG_transparency_demonstration_1.png";
        DownloadImageTask downloadImageTask = new DownloadImageTask(new ImageView(getContext()));
        Bitmap downloadedBitmap = downloadImageTask.execute(input).get();
        while(downloadImageTask.getStatus() != DownloadImageTask.Status.FINISHED)
        {
            System.out.println(downloadImageTask.getStatus());
            Thread.sleep(100);
        }

        Log.d("DOWNLOADIMAGETASK","INFO " + downloadedBitmap.getColorSpace());

        if (!downloadedBitmap.getColorSpace().toString().equals("sRGB IEC61966-2.1 (id=0, model=RGB)")){
            Assert.fail();
        }
    }
}