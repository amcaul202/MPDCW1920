package org.amcaul202.gcu.mpd.mpdcw1920;

import android.app.ListActivity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {//implements View.OnClickListener{
    private TextView rawDataDisplay;
    private String result;

    private Button RwButton;
    private Button PlanRwButton;
    private Button CurrentInButton;

    //Traffic Scotland URLs (Split for IF Statement in onClick)
    private String urlSource1 = "https://trafficscotland.org/rss/feeds/roadworks.aspx";
    private String urlSource2 = "https://trafficscotland.org/rss/feeds/plannedroadworks.aspx";
    private String urlSource3 = "https://trafficscotland.org/rss/feeds/currentincidents.aspx";

    // Traffic Scotland URLs
    //private String urlSource = "https://trafficscotland.org/rss/feeds/roadworks.aspx";
    //private String urlSource = "https://trafficscotland.org/rss/feeds/plannedroadworks.aspx";
    //private String urlSource = "https://trafficscotland.org/rss/feeds/currentincidents.aspx";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rawDataDisplay = (TextView) findViewById(R.id.rawDataDisplay);
        RwButton = (Button) findViewById(R.id.RwButton);
        PlanRwButton = (Button) findViewById(R.id.PlanRwButton);
        CurrentInButton = (Button) findViewById(R.id.CurrentInButton);
        RwButton.setOnClickListener(this);
        PlanRwButton.setOnClickListener(this);
        CurrentInButton.setOnClickListener(this);
    }

    public void onClick(View aview) {
        //IF Statement for XML Links
        if (aview != null) {
            int id = aview.getId();

            if (id == R.id.RwButton) {
                String urlSource = urlSource1;
                startProgress(urlSource);
            } else if (id == R.id.PlanRwButton) {
                String urlSource = urlSource2;
                startProgress(urlSource);
            } else if (id == R.id.CurrentInButton) {
                String urlSource = urlSource3;
                startProgress(urlSource);
            }
        }
    }

    public void startProgress(String urlSource) {
        new Thread(new Task(urlSource)).start();
    }

    private class Task implements Runnable {
        public String url;
        private LinkedList<Roadwork> alist;

        public Task(String aurl) {
            url = aurl;
        }

        @Override
        public void run() {

            URL aurl = null;
            URLConnection yc;
            BufferedReader in = null;
            String inputLine = "";

            Log.e("MyTag", "in run"); //Tag for Testing (REMOVE)

            try {
                Log.e("MyTag", "in try"); //Tag for Testing (REMOVE)
                aurl = new URL(url);
                yc = aurl.openConnection();
                in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
                result = in.readLine();
                while ((inputLine = in.readLine()) != null) {
                    result = result + inputLine;
                    Log.e("MyTag", inputLine);

                }
                in.close();
            } catch (IOException ae) {
                Log.e("MyTag", "ioexception");
            }

            Roadwork roadwork = new Roadwork();
            String temp = null;
            alist = null;
            try {
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(true);
                XmlPullParser xpp = factory.newPullParser();
                xpp.setInput(new StringReader(result));
                int eventType = xpp.getEventType();
                while (eventType != XmlPullParser.END_DOCUMENT) {
                    // Found a start tag
                    if (eventType == XmlPullParser.START_TAG) {
                        // Check which Tag we have
                        if (xpp.getName().equalsIgnoreCase("channel")) {
                            alist = new LinkedList<Roadwork>();
                        } else if (xpp.getName().equalsIgnoreCase("item")) {
                            Log.e("MyTag", "Item Start Tag found"); //Tag for Testing (REMOVE)
                            roadwork = new Roadwork();
                        }
                    } else if (eventType == XmlPullParser.END_TAG) {
                        if (xpp.getName().equalsIgnoreCase("item")) {
                            Log.e("MyTag", "item is " + roadwork.toString()); //Tag for Testing (REMOVE)
                            alist.add(roadwork);
                        } else if (xpp.getName().equalsIgnoreCase("title")) {
                            Log.e("MyTag", "Title is " + temp); //Tag for Testing (REMOVE)
                            roadwork.setTitle(temp);
                        } else if (xpp.getName().equalsIgnoreCase("description")) {
                            roadwork.setDescription(temp);
                        }
                    } else if (eventType == XmlPullParser.TEXT) {
                        temp = xpp.getText();
                        System.out.println("Text " + temp);
                        Log.e("MyTag", "Text is " + temp); //Tag for Testing (REMOVE)
                    }
                    // Get the next event
                    eventType = xpp.next();
                } // End of while
            } catch (XmlPullParserException ae1) {
                Log.e("MyTag", "Parsing error" + ae1.toString());
            } catch (IOException ae1) {
                Log.e("MyTag", "IO error during parsing");
            }

            Log.e("MyTag", "End document"); //Tag for Testing (REMOVE)

            System.out.println("Size: " + alist.size()); //Tag for Testing (REMOVE)
            MainActivity.this.runOnUiThread(new Runnable() {
                public void run() {
                    Log.d("UI thread", "I am the UI thread"); //Tag for Testing (REMOVE)

                    String data = "";

                    for(Roadwork roadwork:alist) {
                        roadwork.getTitle();
                        roadwork.getDescription();
                        data += roadwork.getTitle() + " / " + roadwork.getDescription() + " \n  \n __";
                    }
                    rawDataDisplay.setText(data);
                }
            });
        }
    }

    /*List headlines;
    //List links;
    //List descs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //new Task().execute();

        RwButton = (Button) findViewById(R.id.RwButton);
        PlanRwButton = (Button) findViewById(R.id.PlanRwButton);
        CurrentInButton = (Button) findViewById(R.id.CurrentInButton);
        RwButton.setOnClickListener(this);
        PlanRwButton.setOnClickListener(this);
        CurrentInButton.setOnClickListener(this);
    }

    public void onClick(View aview) {
        //IF Statement for XML Links
        if (aview != null) {
            int id = aview.getId();

            if (id == R.id.RwButton) {
                String urlSource = urlSource1;
                startProgress(urlSource);
            } else if (id == R.id.PlanRwButton) {
                String urlSource = urlSource2;
                startProgress(urlSource);
            } else if (id == R.id.CurrentInButton) {
                String urlSource = urlSource3;
                startProgress(urlSource);
            }
        }
    }

    public void startProgress(String urlSource) {
        new Task(urlSource).execute();
    }

    class Task extends AsyncTask<Object, Void, ArrayAdapter> {
        public String url;

        public Task(String aurl) {
            url = aurl;
        }

        @Override
        protected  ArrayAdapter doInBackground(Object[] params) {
            headlines = new ArrayList();
            //links = new ArrayList();
            //descs = new ArrayList();
            try {
                URL aurl = null;
                URLConnection yc;
                BufferedReader in = null;
                String inputLine = "";

                Log.e("MyTag", "in run"); //Tag for Testing (REMOVE)

                try {
                    Log.e("MyTag", "in try"); //Tag for Testing (REMOVE)
                    aurl = new URL(url);
                    yc = aurl.openConnection();
                    in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
                    result = in.readLine();
                    while ((inputLine = in.readLine()) != null) {
                        result = result + inputLine;
                        Log.e("MyTag", inputLine);

                    }
                    in.close();
                } catch (IOException ae) {
                    Log.e("MyTag", "ioexception");
                }
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(false);
                XmlPullParser xpp = factory.newPullParser();

                xpp.setInput(getInputStream(aurl), "UTF_8");
                boolean insideItem = false;

                int eventType = xpp.getEventType();
                while (eventType != XmlPullParser.END_DOCUMENT) {
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equalsIgnoreCase("item")){
                            insideItem = true;
                        } else if (xpp.getName().equalsIgnoreCase("title")) {
                            if (insideItem)
                                headlines.add(xpp.nextText());
                        } else if (xpp.getName().equalsIgnoreCase("link")) {
                            if (insideItem)
                                headlines.add(xpp.nextText());
                        } else if (xpp.getName().equalsIgnoreCase("description")) {
                            if (insideItem)
                                headlines.add(xpp.nextText());
                        }
                    } else if (eventType == XmlPullParser.END_TAG && xpp.getName().equalsIgnoreCase("item")) {
                        insideItem = false;
                    }
                    eventType = xpp.next();
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } return null;
        }

        protected void onPostExecute(ArrayAdapter adapter) {
            adapter = new ArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_1, headlines);
            //adapter2 = new ArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_2, links);
            setListAdapter(adapter);
        }

        public InputStream getInputStream(URL url){
            try {
                return url.openConnection().getInputStream();
            } catch (IOException e){
                return null;
            }
        }
    }*/

} // End of MainActivity