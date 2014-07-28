/*
 * Copyright [2014] Julian Shen
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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

        if (requestCode == 0 && resultCode == RESULT_OK) {
            Log.d("DEMO", "get thumbnail done");
            Bitmap bitmap = data.getParcelableExtra(WebThumbnailService.EXTRA_RESULT_BITMAP);
            mImageView.setImageBitmap(bitmap);
        } else {
            Log.d("DEMO", "error to get thumbnail");
        }
    }
}
