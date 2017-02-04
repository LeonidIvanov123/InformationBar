package ru.ivan.leo.informationbar;

/**
 * Created by Leonid on 02.11.2016.
 */

import android.util.Log;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class RssItem {

    private static String MyLog = "MyLogs";

    private String title;
    private String description;
    private Date pubDate;
    private String link;

    static InputStream is;
    static boolean flagstatus ;
    static URL url;
    private static HttpURLConnection conn;
    static Document document;
    static Element element;
    static DocumentBuilder db;
    static NodeList nodeList;
    static boolean flagnext;

    public RssItem(String title, String description, Date pubDate, String link) {
        this.title = title;
        this.description = description;
        this.pubDate = pubDate;
        this.link = link;
    }

    public String getTitle()
    {
        return this.title;
    }

    public String getLink()
    {
        return this.link;
    }

    public String getDescription()
    {
        return this.description;
    }

    public Date getPubDate()
    {
        return this.pubDate;
    }

    @Override
    public String toString() {

        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd - hh:mm:ss");

        String result = getTitle() + "  ( " + sdf.format(this.getPubDate()) + " )";
        return result;
    }

    public static ArrayList<RssItem> getRssItems(String feedUrl) {

        ArrayList<RssItem> rssItems = new ArrayList<RssItem>();

        RssItem rssItemT = new RssItem("Источник: Lenta ru", "Best news.",
                new Date(), feedUrl);//первый элемент списка, укажу источник новостей

        rssItems.add(rssItemT);

        try {
            //Log.d(MyLog,"запрос УРЛ");
            URL url = new URL(feedUrl);
            is = null;
            flagstatus = false;

            //Log.d(MyLog,"открываем соединение!"); //   Log.d(MyLog,"");
            conn = (HttpURLConnection) url.openConnection();

            //Log.d(MyLog,"привязываем поток");

             new Thread(new Runnable() {
                    @Override
                 public void run() {
                        try {
                            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                                is = conn.getInputStream();
                                flagstatus = true;
                            }
                        }catch (IOException tre){}
                  }
                }).start();

            //Log.d(MyLog,"получаем данные.");

            while(!flagstatus){
             //Log.d(MyLog,"ждем ответ");
                Thread.sleep(500);
            }

            if(flagstatus == true){
                //Log.d(MyLog,"flagstatus = true");
                //xml parsing
                DocumentBuilderFactory dbf = DocumentBuilderFactory
                        .newInstance();
                db = dbf.newDocumentBuilder();
                //Log.d(MyLog,"db создан");
                //(Document Builder) parse xml data

                flagnext = false;
                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        try {
                            document = db.parse(is);
                        } catch (SAXException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        element = document.getDocumentElement();
                        flagnext = true;
                    }
                }).start();


                while(!flagnext){
                //Log.d(MyLog,"ждем element");
                    Thread.sleep(100);
                }
                flagnext = false;
              //Log.d(MyLog,"nodeList впереди");
                //rss to NodeList
                nodeList = element.getElementsByTagName("item");
               // Log.d(MyLog,"nodeList готово");
                if (nodeList.getLength() > 0) {
                    for (int i = 0; i < nodeList.getLength(); i++) {
                        String s = "записей = " + nodeList.getLength();
                        Log.d(MyLog, s);
                        //take each entry (corresponds to <item></item> tags in
                        //xml data
                        //Log.d(MyLog,"entry");
                        Element entry = (Element) nodeList.item(i);
                        //Log.d(MyLog,"end entry");
                        Element _titleE = (Element) entry.getElementsByTagName(
                                "title").item(0);
                        Element _descriptionE = (Element) entry
                                .getElementsByTagName("description").item(0);
                        Element _pubDateE = (Element) entry
                                .getElementsByTagName("pubDate").item(0);
                        Element _linkE = (Element) entry.getElementsByTagName(
                                "link").item(0);
                        //Log.d(MyLog,"end all elements");
                        String _title = _titleE.getFirstChild().getNodeValue();
                        String _description = _descriptionE.getFirstChild().getNodeValue();
                        //Log.d("descriptionNews", _descriptionE.toString());
                        Date _pubDate = new Date(_pubDateE.getFirstChild().getNodeValue());
                        String _link = _linkE.getFirstChild().getNodeValue();

                        //create RssItemObject and add it to the ArrayList
                        RssItem rssItem = new RssItem(_title, _description,
                                _pubDate, _link);

                        rssItems.add(rssItem);
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.d(MyLog,"возвращаем rssItems");
        return rssItems;
    }

}