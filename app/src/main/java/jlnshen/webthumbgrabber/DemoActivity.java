package jlnshen.webthumbgrabber;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import jlnshen.webthumb.WebThumbnailService;
import jlnshen.webthumbgrabber.R;

public class DemoActivity extends Activity {

    ImageView mImageView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);
        mImageView = (ImageView) findViewById(R.id.imageView);

        Intent pageThumbIntent = new Intent(this, WebThumbnailService.class);
        PendingIntent pi = createPendingResult(0, new Intent(), PendingIntent.FLAG_UPDATE_CURRENT);
        pageThumbIntent.putExtra(WebThumbnailService.EXTRA_CALLBACK, pi);
        pageThumbIntent.putExtra(WebThumbnailService.EXTRA_WIDTH, 800);
        pageThumbIntent.putExtra(WebThumbnailService.EXTRA_HEIGHT, 500);
        pageThumbIntent.putExtra(WebThumbnailService.EXTRA_URL, "http://www.yahoo.com");
        pageThumbIntent.putExtra(WebThumbnailService.EXTRA_MOBILE_SITE, true);
        pageThumbIntent.setAction(WebThumbnailService.ACTION_CAPTURE_WEB_THUMBNAIL);

        startService(pageThumbIntent);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.demo, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 0 && resultCode == RESULT_OK) {
            Log.d("DEMO", "get thumbnail done");
            Bitmap bitmap = data.getParcelableExtra(WebThumbnailService.EXTRA_RESULT_BITMAP);
            mImageView.setImageBitmap(bitmap);
        } else {
            Log.d("DEMO", "error to get thumbnail");
        }
    }
}
