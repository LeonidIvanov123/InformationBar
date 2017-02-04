package ru.ivan.leo.informationbar;

/**
 * Created by Leonid on 10.11.2016.
 */
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.*;
import java.util.*;
import java.text.*;
import java.net.*;
import java.io.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import android.view.*;
import android.view.View;
import android.widget.*;
import android.content.*;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

public class RSSactivity extends Activity {

    public static RssItem selectedRssItem = null;
    String feedUrl = "https://lenta.ru/rss/last24";
    String weaderUrl = "http://informer.gismeteo.ru/rss/22820.xml";
    boolean weadtrue = false;
    String weaderInf = "";

    ListView rssListView = null;
    TextView ListWeader;
    ArrayList<RssItem> rssItems = new ArrayList<RssItem>();
    ArrayAdapter<RssItem> aa = null;
    private GoogleApiClient client;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //вывод погоды
        ListWeader = (TextView) findViewById(R.id.textViewWeader);

        new Thread(new Runnable() {
            @Override
            public void run() {
                refrashWeader();
                while(weadtrue == false){}
            }
        }).start();
        if(weadtrue == true) {
            ListWeader.setText(weaderInf);
            weadtrue = false;
        }

        Button fetchWeader = (Button) findViewById(R.id.refrWeader);
        //обновление новостной ленты
        Button fetchRss = (Button) findViewById(R.id.fetchRss);
        //обработка нажатия кнопки "обновить погоду"
        fetchWeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refrashWeader();
                while(weadtrue == false){}
                ListWeader.setText(weaderInf);
                weadtrue = false;
            }
        });

        //обработка нажатия кнопки "обновить ленту новостей"
        fetchRss.setOnClickListener(new View.OnClickListener() {

            //@Override
            public void onClick(View v) {
                aa.notifyDataSetChanged();
                refressRssList();
            }
        });

        rssListView = (ListView) findViewById(R.id.rssListView);
        //обработка нажатия на элемент списка новостей на Главном экране
        rssListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            //@Override
            public void onItemClick(AdapterView<?> av, View view, int index,
                                    long arg3) {
                selectedRssItem = rssItems.get(index);
                Intent intent = new Intent("ru.ivanov.leo.displayRssItem");
                startActivity(intent);
            }
        });

        rssListView.setScrollContainer(true);
        aa = new ArrayAdapter<RssItem>(this, R.layout.list_item, rssItems);
        rssListView.setAdapter(aa);
        refressRssList();
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private void refressRssList() {

        ArrayList<RssItem> newItems = RssItem.getRssItems(feedUrl);
        rssItems.clear();
        rssItems.addAll(newItems);
        aa.notifyDataSetChanged();
    }

    //процедура вызывается для получения погоды
    private void refrashWeader() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                URL url = null;
                try {
                    url = new URL(weaderUrl);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                InputStream is = null;
                DocumentBuilder db = null;
                Document document = null;
                HttpURLConnection conn = null;
                Element element = null;
                NodeList nodeList = null;


                try {
                    conn = (HttpURLConnection) url.openConnection();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Log.d("weadr", "conn compl");
                try {
                    if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        is = conn.getInputStream();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Log.d("weadr", "is compl");
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


                DocumentBuilderFactory dbf = DocumentBuilderFactory
                        .newInstance();
                try {
                    db = dbf.newDocumentBuilder();
                } catch (ParserConfigurationException e) {
                    e.printStackTrace();
                }
                Log.d("weadr", "db compl");
                try {
                    document = db.parse(is);
                } catch (SAXException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                element = document.getDocumentElement();
                nodeList = element.getElementsByTagName("item");

                Log.d("weadr"," in IF()..");

                if (nodeList.getLength() > 0) {
                    for (int i = 0; i < nodeList.getLength(); i++) {
                        Element entry = (Element) nodeList.item(i);
                        Element _titleE = (Element) entry.getElementsByTagName(
                                "title").item(0);
                        Element _descriptionE = (Element) entry
                                .getElementsByTagName("description").item(0);
                        Element _pubDateE = (Element) entry
                                .getElementsByTagName("pubDate").item(0);
                        weaderInf = _descriptionE.getFirstChild().getNodeValue();
                    }
                }

                Log.d("weadr", "parse is");
                //Log.d("weadr", element.toString());
                //weaderInf = element.getElementsByTagName(".section.higher").toString();
                weadtrue = true;
            }
        }).start();




        //weaderInf = "pishem pogodu";
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "RSSactivity Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://ru.ivan.leo.informationbar/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "RSSactivity Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://ru.ivan.leo.informationbar/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }
}