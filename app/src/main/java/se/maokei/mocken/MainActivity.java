/**
 * @author Rickard Johansson - maokei
 * */
package se.maokei.mocken;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.support.v7.app.ActionBarActivity;
import android.os.*;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MainActivity extends ActionBarActivity {
    private SupportMapFragment map;
    private GoogleMap gmap;
    private LatLngBounds sweden;
    private MarkerOptions userMarker;
    private Bitmap logo;
    private BitmapDescriptor icon;
    private Handler handler;

    //Buttons
    private Button mapButton;
    private CheckBox trackButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //icon, fragment and map
        this.logo = BitmapFactory.decodeResource(getResources(), R.drawable.st1_logo);
        this.logo = Bitmap.createScaledBitmap(logo, logo.getWidth() / 4, logo.getHeight() / 4, false);
        this.icon = BitmapDescriptorFactory.fromBitmap(logo);
        this.sweden = new LatLngBounds(new LatLng(55.001099, 11.10694), new LatLng(69.063141, 24.16707));
        map = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        gmap = map.getMap();
        gmap.getUiSettings().setMyLocationButtonEnabled(true);

        mapButton = (Button) findViewById(R.id.mapButton);
        trackButton = (CheckBox) findViewById(R.id.trackBtn);

        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(gmap.getMapType() == GoogleMap.MAP_TYPE_NORMAL) {
                    gmap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                    mapButton.setBackground(getResources().getDrawable(R.drawable.standard_map));
                }
                else{
                    gmap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    mapButton.setBackground(getResources().getDrawable(R.drawable.satellite_map));
                }
            }
        });

        //text file read test
        try {
            InputStream is = getResources().getAssets().open("st1s.csv");
            //String textfile  = convertStreamToString(is);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line = reader.readLine();

            Integer numLines = 0;
            while(line != null) {
                //Scanner scanner = new Scanner(is);
                Log.d("RMJ", "Read line: " + line);

                String[] tokens = line.split(",");
                tokens[0] = tokens[0].replaceAll("\\s+", "");
                tokens[1] = tokens[1].replaceAll("\\s+", "");
                tokens[2] = tokens[2].replaceAll("\"", "").trim();
                tokens[3] = tokens[3].replaceAll("\"", "").trim();
                /*Log.d("RMJ", "tokens length:: " + tokens.length);
                Log.d("RMJ","0: " + tokens[0]);
                Log.d("RMJ","1: " + tokens[1]);
                Log.d("RMJ", "2: " + tokens[2]);
                Log.d("RMJ", "3: " + tokens[3]);*/
                addMarker(Double.parseDouble(tokens[0]),Double.parseDouble(tokens[1]),tokens[2], tokens[3]);

                numLines++;
                line = reader.readLine();
            }
            reader.close();
            is.close();
            //Log.d("RMJ","Number of lines:: " + numLines);
        } catch (IOException e) {
            Log.d("RMJ", "Problem: " + e.getMessage());
            e.printStackTrace();
        }

        //animate camera
        handler = new Handler();
        final Runnable r = new Runnable() {
            public void run() {
                gmap.animateCamera(CameraUpdateFactory.newLatLngBounds(sweden, 0));

            }
        };
        handler.postDelayed(r, 1000);

        //default button image normal
        mapButton.setBackground(getResources().getDrawable(R.drawable.satellite_map));
        gmap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {
                if(location != null) {
                    if(trackButton.isChecked()) {
                        //Toast.makeText(getApplicationContext(), "change", Toast.LENGTH_SHORT).show();
                        gmap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude())));
                    }
                }
            }
        });

        gmap.setMyLocationEnabled(true);
        gmap.getUiSettings().setCompassEnabled(true);
    }


    /**
     * @name populateMap
     * @brief Add gasoline station markers
     * */
    private void populateMap() {

    }

    /**
     * @name addMarker
     * @brief add a marker on the map
     * */
    private void addMarker(double lat, double lng, String title, String desc) {
        //Log.d("mark", "Adding lat: " + lat + "Lng: " + lng);
        gmap.addMarker(new MarkerOptions().title(title)
                .icon(icon)
                .title(title)
                .snippet(desc)
                .position(new LatLng(lng, lat)));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
