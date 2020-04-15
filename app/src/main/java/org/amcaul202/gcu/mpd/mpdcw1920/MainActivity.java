package org.amcaul202.gcu.mpd.mpdcw1920;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.LinkedList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener
{
    private TextView rawDataDisplay;
    private String result;
    private Button startButton;
    // Traffic Scotland URLs
    //private String urlSource = "https://trafficscotland.org/rss/feeds/roadworks.aspx";
    //private String urlSource = "https://trafficscotland.org/rss/feeds/plannedroadworks.aspx";
    private String urlSource = "https://trafficscotland.org/rss/feeds/currentincidents.aspx";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rawDataDisplay = (TextView)findViewById(R.id.rawDataDisplay);
        startButton = (Button)findViewById(R.id.startButton);
        startButton.setOnClickListener(this);

    }

    public void onClick(View aview)
    {
        startProgress();

    }

    public void startProgress()
    {
        // Run network access on a separate thread;
        new Thread(new Task(urlSource)).start();
    } //

    // Need separate thread to access the internet resource over network
    // Other neater solutions should be adopted in later iterations.
    private class Task implements Runnable
    {
        private String url;
        private LinkedList<Roadwork> alist;

        public Task(String aurl)
        {
            url = aurl;
        }

        @Override
        public void run()
        {

            URL aurl;
            URLConnection yc;
            BufferedReader in = null;
            String inputLine = "";


            Log.e("MyTag","in run");

            try
            {
                Log.e("MyTag","in try");
                aurl = new URL(url);
                yc = aurl.openConnection();
                in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
                //
                // Throw away the first 2 header lines before parsing
                //
                //
                //
                while ((inputLine = in.readLine()) != null)
                {
                    result = result + inputLine;
                    Log.e("MyTag",inputLine);

                }
                in.close();
            }
            catch (IOException ae)
            {
                Log.e("MyTag", "ioexception");
            }

            //
            // Now that you have the xml data you can parse it
            //

            // Now update the TextView to display raw XML data
            // Probably not the best way to update TextView
            // but we are just getting started !

            Roadwork roadwork = new Roadwork();
            String temp = null;
            alist = null;

            try
            {
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(true);
                XmlPullParser xpp = factory.newPullParser();
                xpp.setInput( new StringReader(result) );
                int eventType = xpp.getEventType();
                while (eventType != XmlPullParser.END_DOCUMENT)
                {
                    // Found a start tag
                    if(eventType == XmlPullParser.START_TAG)
                    {
                        // Check which Tag we have
                        if (xpp.getName().equalsIgnoreCase("channel"))
                        {
                            alist = new LinkedList<Roadwork>();
                        }
                        else if (xpp.getName().equalsIgnoreCase("item"))
                        {
                            Log.e("MyTag","Item Start Tag Found" );
                            roadwork = new Roadwork();
                        }
                        else if (eventType == XmlPullParser.END_TAG)
                        {
                            if (xpp.getName().equalsIgnoreCase("item"))
                            {
                                Log.e("MyTag", "Item is: " + roadwork.toString());
                                alist.add(roadwork);
                            }
                            else if (xpp.getName().equalsIgnoreCase("title"))
                            {
                                Log.e("MyTag", "Title is: " + temp);
                                roadwork.setTitle(temp);
                            }
                            else if (xpp.getName().equalsIgnoreCase("description"))
                            {
                                Log.e("MyTag", "Description is: " + temp);
                                roadwork.setDescription(temp);
                            }
                        }
                        else if (eventType == XmlPullParser.TEXT)
                        {
                            temp = xpp.getText();
                            System.out.println("Text: " + temp);
                            Log.e("MyTag", "Text is: " + temp);
                        }
                    }
                    // Get the next event
                    eventType = xpp.next();

                } // End of while
            }
            catch (XmlPullParserException ae1)
            {
                Log.e("MyTag","Parsing error" + ae1.toString());
            }
            catch (IOException ae1)
            {
                Log.e("MyTag","IO error during parsing");
            }

            Log.e("MyTag","End document");

            System.out.println("Size: " + alist.size());

            MainActivity.this.runOnUiThread(new Runnable()
            {
                public void run() {
                    Log.d("UI thread", "I am the UI thread");
                    rawDataDisplay.setText(result);
                }
            });
        }

    }



    private void parseData()
    {}

} // End of MainActivity
