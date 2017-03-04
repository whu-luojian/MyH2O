package com.whu.myh2o;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.DefaultMapViewOnTouchListener;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.LocationDisplay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol;
import com.whu.myh2o.spinner.ItemData;
import com.whu.myh2o.spinner.SpinnerAdapter;

import java.util.ArrayList;

public class MapActivity extends AppCompatActivity {

    private MapView mMapView;
    private TextView mTextView;
    private LocationDisplay mLocationDisplay;
    private Spinner mSpinner;
    private Point pt_get;
    private int requestCode = 2;
    String[] reqPermissions = new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission
            .ACCESS_COARSE_LOCATION};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);


        ActionBar actionBar=getSupportActionBar();
        //actionBar.setTitle("选取采样地点");
        actionBar.setCustomView(R.layout.titlebar_layout);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        mTextView=(TextView)findViewById(R.id.mytext);
        mTextView.setText("选取采样地点");
        actionBar.setDisplayHomeAsUpEnabled(true);

        mSpinner = (Spinner) findViewById(R.id.spinner);

        mMapView = (MapView) findViewById(R.id.mapView);
        ArcGISMap map = new ArcGISMap(Basemap.Type.STREETS, 40.056295, 116.195800, 6);
        mMapView.setMap(map);
        mLocationDisplay = mMapView.getLocationDisplay();

        // Listen to changes in the status of the location data source.
        mLocationDisplay.addDataSourceStatusChangedListener(new LocationDisplay.DataSourceStatusChangedListener() {
            @Override
            public void onStatusChanged(LocationDisplay.DataSourceStatusChangedEvent dataSourceStatusChangedEvent) {

                // If LocationDisplay started OK, then continue.
                if (dataSourceStatusChangedEvent.isStarted())
                    return;

                // No error is reported, then continue.
                if (dataSourceStatusChangedEvent.getError() == null)
                    return;

                // If an error is found, handle the failure to start.
                // Check permissions to see if failure may be due to lack of permissions.
                boolean permissionCheck1 = ContextCompat.checkSelfPermission(MapActivity.this, reqPermissions[0]) ==
                        PackageManager.PERMISSION_GRANTED;
                boolean permissionCheck2 = ContextCompat.checkSelfPermission(MapActivity.this, reqPermissions[1]) ==
                        PackageManager.PERMISSION_GRANTED;

                if (!(permissionCheck1 && permissionCheck2)) {
                    // If permissions are not already granted, request permission from the user.
                    ActivityCompat.requestPermissions(MapActivity.this, reqPermissions, requestCode);
                } else {
                    // Report other unknown failure types to the user - for example, location services may not
                    // be enabled on the device.
                    Toast.makeText(MapActivity.this, "尚未开启手机的定位服务，请在设置中开启！", Toast.LENGTH_LONG).show();

                    // Update UI to reflect that the location display did not actually start
                    mSpinner.setSelection(0, true);
                }
            }
        });


        // Populate the list for the Location display options for the com.whu.myh2o.spinner's Adapter
        ArrayList<ItemData> list = new ArrayList<>();
        list.add(new ItemData("开启自动定位", R.drawable.locationdisplayrecenter));
        list.add(new ItemData("停止自动定位", R.drawable.locationdisplaydisabled));
        list.add(new ItemData("手动定位", R.drawable.locationdisplayon));
        list.add(new ItemData("移除手动定位", R.drawable.locationdisplaydisabled));

        SpinnerAdapter adapter = new SpinnerAdapter(this, R.layout.spinner_layout, R.id.txt, list);
        mSpinner.setAdapter(adapter);
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        // Start Location Display
                        mLocationDisplay.setAutoPanMode(LocationDisplay.AutoPanMode.RECENTER);
                        if (!mLocationDisplay.isStarted()){
                            mLocationDisplay.startAsync();
                            pt_get = mLocationDisplay.getMapLocation();
                        }
                        break;
                    case 1:
                        // Stop Location Display
                        if (mLocationDisplay.isStarted())
                        {
                            pt_get = mLocationDisplay.getMapLocation();
                            mLocationDisplay.stop();
                        }
                        break;
                    case 2:
                        //Mannually get location
                        //stop locationdisplay
                        if (mLocationDisplay.isStarted()) {
                            pt_get = mLocationDisplay.getMapLocation();
                            mLocationDisplay.stop();
                        }
                        Toast.makeText(MapActivity.this,"请在地图上点击你所处的位置",Toast.LENGTH_SHORT);
                        //start map touch listener
                        mMapView.setOnTouchListener(new DefaultMapViewOnTouchListener(getApplicationContext(),mMapView){
                            @Override
                            public boolean onTouch(View v, MotionEvent event) {
                                if (!mMapView.getGraphicsOverlays().isEmpty())
                                    return false;
                                else {
                                    android.graphics.Point pt_screen = new android.graphics.Point((int)event.getX(),(int)event.getY());
                                    Point pt = mMapView.screenToLocation(pt_screen);
                                    pt_get = pt;
                                    SimpleMarkerSymbol ptMarker = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, new Color().BLUE, 20);
                                    final GraphicsOverlay pts = new GraphicsOverlay();
                                    mMapView.getGraphicsOverlays().add(pts);
                                    Graphic pt_graphic = new Graphic(pt, ptMarker);
                                    pts.getGraphics().add(pt_graphic);
                                    return true;
                                }
                            }
                        });
                    break;
                    case 3:
                        if(mMapView.getGraphicsOverlays()!=null){
                            mMapView.getGraphicsOverlays().clear();
                            pt_get = null;
                        }
                        if(mLocationDisplay!=null)
                            pt_get = mLocationDisplay.getMapLocation();
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.locate, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.action_locate:
                if(pt_get==null) {
                    Toast.makeText(MapActivity.this, "请添加手动定位点或开启自动定位！", Toast.LENGTH_LONG).show();
                    return false;
                }
                Intent returnIntent = new Intent();
                double[] coords = {pt_get.getX()*0.00001,pt_get.getY()*0.00001};
                returnIntent.putExtra("result",coords);
                setResult(Activity.RESULT_OK,returnIntent);
                finish();
                return true;
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    protected void onPause(){
        mMapView.pause();
        super.onPause();
    }

    @Override
    protected void onResume(){
        super.onResume();
        mMapView.resume();

    }
}
