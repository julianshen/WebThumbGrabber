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

package jlnshen.webthumb;

import android.app.Activity;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.util.concurrent.Semaphore;


public class WebThumbnailService extends IntentService {

    public static final String ACTION_CAPTURE_WEB_THUMBNAIL = "web_thumbnail";
    public static final String EXTRA_CALLBACK = "callback";
    public static final String EXTRA_WIDTH = "width";
    public static final String EXTRA_HEIGHT = "height";
    public static final String EXTRA_URL = "url";
    public static final String EXTRA_MOBILE_SITE = "mobile_site";
    public static final String EXTRA_RESULT_BITMAP = "result_bitmap";

    private static final int DEFAULT_WIDTH = 400;
    private static final int DEFAULT_HEIGHT = 400;

    WebView mWebView = null;
    Handler mMainHandler = null;

    private final Semaphore mSemaphore = new Semaphore(0);

    public WebThumbnailService() {
        super("WebThumbIntentService");

    }

    @Override
    public void onCreate() {
        super.onCreate();
        mWebView = new WebView(this);
        mMainHandler = new Handler();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_CAPTURE_WEB_THUMBNAIL.equals(action)) {
                final PendingIntent callback = intent.getParcelableExtra(EXTRA_CALLBACK);
                final String url = intent.getStringExtra(EXTRA_URL);
                final int width = intent.getIntExtra(EXTRA_WIDTH, DEFAULT_WIDTH);
                final int height = intent.getIntExtra(EXTRA_HEIGHT, DEFAULT_HEIGHT);
                final boolean mobile = intent.getBooleanExtra(EXTRA_MOBILE_SITE, false);

                Log.d("WebThumbnailService", "generate page");

                mWebView.measure(View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY), View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY));
                mWebView.layout(0, 0, width, height);

                mMainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mWebView.setWebViewClient(new WebViewClient() {
                            boolean mError = false;

                            @Override
                            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                                super.onPageStarted(view, url, favicon);
                                mError = false;
                            }

                            @Override
                            public void onPageFinished(WebView view, String url) {
                                super.onPageFinished(view, url);
                                Log.d("WebThumbnailService", "page finished");

                                try {
                                    if (mError) {
                                        callback.send(Activity.RESULT_CANCELED);
                                    } else {
                                        Bitmap mBitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
                                        Canvas canvas = new Canvas(mBitmap);
                                        view.draw(canvas);
                                        Intent data = new Intent();
                                        data.putExtra(EXTRA_RESULT_BITMAP, mBitmap);
                                        callback.send(WebThumbnailService.this, Activity.RESULT_OK, data);
                                    }
                                } catch (PendingIntent.CanceledException e) {
                                    e.printStackTrace();
                                } finally {
                                    mSemaphore.release();
                                }
                            }

                            @Override
                            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                                super.onReceivedError(view, errorCode, description, failingUrl);
                                Log.e("WebThumbnailService", "Error " + errorCode + ":" + description);
                                mError = true;
                            }
                        });
                        if (!mobile) {
                            mWebView.getSettings().setUserAgentString("Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.9.0.4) Gecko/20100101 Firefox/4.0");
                        } else {
                            mWebView.getSettings().setUserAgentString(null);
                        }
                        mWebView.getSettings().setJavaScriptEnabled(true);
                        Log.d("WebThumbnailService", "load " + url);
                        mWebView.loadUrl(url);
                    }
                });

                try {
                    mSemaphore.acquire();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("WebThumbnailService", "onDestroy");
        mWebView = null;
    }
}
