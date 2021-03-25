package edu.coen390.androidapp;

import android.webkit.URLUtil;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import edu.coen390.androidapp.View.LiveFeedActivity;

import static org.junit.Assert.assertTrue;

@RunWith(JUnit4.class)
public class WebViewUnitTest {
    @Test
    public void webView_validUrl_Test() {
        assertTrue(URLUtil.isValidUrl(LiveFeedActivity.WEB_FORM_URL));
    }
}
