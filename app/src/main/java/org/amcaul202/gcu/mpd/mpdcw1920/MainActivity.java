//  Adrian McAulay
//  S1603916

package org.amcaul202.gcu.mpd.mpdcw1920;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.LinkedList;

public class MainActivity extends FragmentActivity implements View.OnClickListener, OnMapReadyCallback {
    private String result;

    private GoogleMap mMap;

    private Button RwButton;
    private Button PlanRwButton;
    private Button CurrentInButton;
    private TextView itemView;

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

        //Google Maps API
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        itemView = findViewById(R.id.ItemView);
        RwButton = findViewById(R.id.RwButton);
        PlanRwButton = findViewById(R.id.PlanRwButton);
        CurrentInButton = findViewById(R.id.CurrentInButton);
        RwButton.setOnClickListener(this);
        PlanRwButton.setOnClickListener(this);
        CurrentInButton.setOnClickListener(this);
    }

    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.clear();
        LatLng startPoint = new LatLng(56.09655575, -3.96606445);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(startPoint));
    }



    public void onClick(View aview) {
        //IF Statement for XML Links
        if (aview != null) {
            int id = aview.getId();
            mMap.clear();
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

            try {
                aurl = new URL(url);
                yc = aurl.openConnection();
                in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
                result = in.readLine();
                while ((inputLine = in.readLine()) != null) {
                    result = result + inputLine;

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
                            roadwork = new Roadwork();
                        }
                    } else if (eventType == XmlPullParser.END_TAG) {
                        if (xpp.getName().equalsIgnoreCase("item")) {
                            alist.add(roadwork);
                        } else if (xpp.getName().equalsIgnoreCase("title")) {
                            roadwork.setTitle(temp);
                        } else if (xpp.getName().equalsIgnoreCase("description")) {
                            roadwork.setDescription(temp);
                        } else if (xpp.getName().equalsIgnoreCase("point")) {
                            roadwork.setPosition(temp);
                        }
                    } else if (eventType == XmlPullParser.TEXT) {
                        temp = xpp.getText();
                        temp = temp.replaceAll("<br />", " \n ");
                    }
                    // Get the next event
                    eventType = xpp.next();
                } // End of while
            } catch (XmlPullParserException ae1) {
                Log.e("MyTag", "Parsing error" + ae1.toString());
            } catch (IOException ae1) {
                Log.e("MyTag", "IO error during parsing");
            }

            MainActivity.this.runOnUiThread(new Runnable() {
                public void run() {
                    String point = "";
                    String title = "";
                    String disc = "";
                    String item = "";

                    String[] pointSplit = new String[2];
                    String[] titleSplit = new String[2];
                    String[] discSplit = new String[2];

                    for(Roadwork roadwork:alist) {
                        roadwork.getTitle();
                        roadwork.getDescription();
                        roadwork.getPosition();

                        point += roadwork.getPosition() + " ";
                        title += roadwork.getTitle() + " / ";
                        disc += roadwork.getDescription() + " ¦ ";
                        item += "Road: " + roadwork.getTitle() + " \n " + roadwork.getDescription() + " \n \n ";

                        pointSplit = point.split(" ");
                        titleSplit = title.split(" / ");
                        discSplit = disc.split(" ¦ ");
                    }

                    int size = pointSplit.length;
                    int titleSize = titleSplit.length;
                    int discSize = discSplit.length;

                    Double[] longLat = new Double[size];
                    Double[] lat = new Double[size/2];
                    Double[] lon = new Double[size/2];
                    String[] titlePoint = new String[titleSize];
                    String[] discPoint = new String[discSize];

                    int lonCount = 0;
                    int latCount = 0;
                    int titleCount = 0;
                    int discCount = 0;

                    for(int i=0; i<size; i++) {
                        longLat[i] = Double.parseDouble(pointSplit[i]);
                    }

                    for(int i=0; i<titleSize; i++) {
                        titlePoint[titleCount] = titleSplit[i];
                        titleCount++;
                    }

                    for(int i=0; i<discSize; i++) {
                        discPoint[discCount] = discSplit[i];
                        discCount++;
                    }

                    System.out.println(Arrays.toString(discPoint));

                    for (int i = 0; i<size; i++) {
                        if (longLat[i] < 0) {
                            lon[lonCount] = longLat[i];
                            lonCount++;
                        } else if (longLat[i] >= 0) {
                            lat[latCount] = longLat[i];
                            latCount++;
                        }
                    }

                    int size1 = lat.length;

                    for (int i = 0; i < size1; i++) {
                        LatLng points = new LatLng(lat[i], lon[i]);
                        mMap.addMarker(new MarkerOptions().position(points).title(titlePoint[i]).snippet(discPoint[i]));
                    }

                    itemView.setText(item);
                }
            });
        }
    }

} // End of MainActivity