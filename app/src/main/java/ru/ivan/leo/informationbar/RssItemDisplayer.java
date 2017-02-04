package ru.ivan.leo.informationbar;

/**
 * Created by Leonid on 10.11.2016.
 */
import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.*;
import java.util.*;
import java.text.*;
import java.net.*;
import java.io.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;
import android.view.*;
public class RssItemDisplayer extends Activity {

    WebView webContent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.rss_item_displayer);
        RssItem selectedRssItem = RSSactivity.selectedRssItem;
        //инициализация элементов дисплея
        TextView titleTv = (TextView)findViewById(R.id.titleTextView);
        TextView contentTv = (TextView)findViewById(R.id.contentTextView);

        webContent = (WebView) findViewById(R.id.webContentData);
        webContent.setScrollContainer(true);
        webContent.getSettings().setJavaScriptEnabled(true);

        webContent.setWebViewClient(new MyWebViewClient());

        String title = "";
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd - hh:mm:ss");
        title = "\n" + selectedRssItem.getTitle() + "  ( "
                + sdf.format(selectedRssItem.getPubDate()) + " )\n\n";

        String content = "";
        content += //selectedRssItem.getDescription() + "\n"
                 selectedRssItem.getLink();
        titleTv.setText(title);
        contentTv.setText(content);
        webContent.loadUrl(selectedRssItem.getLink());
    }

    @Override
    public void onBackPressed(){
        if(webContent.canGoBack())
        {
            webContent.goBack();
        }else{
            super.onBackPressed();
        }
    }

    //используем свой веб-браузер для вывода странички заместо стандартного
    private class MyWebViewClient extends WebViewClient
    {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url)
        {
            view.loadUrl(url);
            return true;
        }
    }
}