package edu.coen390.androidapp;

import android.content.Intent;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.regex.Pattern;

import edu.coen390.androidapp.View.LiveFeedActivity;

import static androidx.test.espresso.web.assertion.WebViewAssertions.webMatches;
import static androidx.test.espresso.web.model.Atoms.getCurrentUrl;
import static androidx.test.espresso.web.sugar.Web.onWebView;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class WebViewActivityTest {
    private String validHostPort = "^\\s*(.*?):(\\d+)\\s*$";

    @Rule
    public ActivityTestRule<LiveFeedActivity> mActivityRule = new ActivityTestRule<LiveFeedActivity>(
            LiveFeedActivity.class, false, false) {
        @Override
        protected void afterActivityLaunched() {
            onWebView().forceJavascriptEnabled();
        }
    };

    @Test
    public void webView_validUrl_Test() {
        // TODO: replace the host/port with LiveFeedActivity.WEB_FORM_URL
        String host_port = "http://192.000.00.0:5000".split("//")[1];
        Pattern p = Pattern.compile(validHostPort);
        boolean match = p.matcher(host_port).matches();
        assertTrue(match);
    }

    @Test
    public void webView_invalidUrl_Test() {
        String host_port = "http://192.000".split("//")[1];
        Pattern p = Pattern.compile(validHostPort);
        boolean match = p.matcher(host_port).matches();
        assertFalse(match);
    }

    @Test
    public void webView_verifyWebServerUrl_Test() {
        mActivityRule.launchActivity(withWebFormIntent());

        //TODO: Change the string to the URL of the web server
        onWebView()
            .check(webMatches(getCurrentUrl(), containsString("google")));
    }

    // TODO: uncomment this when testing with the actual server. Add ID to <img> element.
//    @Test
//    public void webView_verifyImageReceivedFromWebServer_Test() {
//        mActivityRule.launchActivity(withWebFormIntent());
//
//        onWebView()
//            .withElement(findElement(Locator.ID, "facial_recognition"));
//    }

    private static Intent withWebFormIntent() {
        Intent basicFormIntent = new Intent();
        basicFormIntent.putExtra(LiveFeedActivity.KEY_URL_TO_LOAD, LiveFeedActivity.WEB_FORM_URL);
        return basicFormIntent;
    }
}
