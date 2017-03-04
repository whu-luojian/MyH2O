package com.whu.myh2o;


import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.esri.arcgisruntime.ArcGISRuntimeEnvironment;
import com.esri.arcgisruntime.datasource.arcgis.ArcGISFeature;
import com.esri.arcgisruntime.datasource.arcgis.ServiceFeatureTable;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.MapView;


/**
 * A simple {@link Fragment} subclass.
 */
public class MapFragment extends Fragment {
    MapView mapView;
    FeatureLayer mFeatureLayer;
    public static ServiceFeatureTable serviceFeatureTable;
    private boolean mFeatureSelected = false;
    private ArcGISFeature mIdentifiedFeature;
    public MapFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ArcGISRuntimeEnvironment.setClientId("N7JPBdBkT209Rw80");
        View v = inflater.inflate(R.layout.fragment_map, container, false);

        return v;
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){

        mapView = (MapView)view.findViewById(R.id.map);
        ArcGISMap map = new ArcGISMap(Basemap.createStreets());
        map.setInitialViewpoint(new Viewpoint(new Point(112.343, 28.585, SpatialReferences.getWgs84()), 1E7));

        //final ServiceFeatureTable
        serviceFeatureTable = new ServiceFeatureTable(getResources().getString(R.string.featurelyrUrl));

        mFeatureLayer = new FeatureLayer(serviceFeatureTable);
        mFeatureLayer.setSelectionColor(Color.CYAN);
        mFeatureLayer.setSelectionWidth(3);
        mFeatureLayer.setPopupEnabled(true);
        map.getOperationalLayers().add(mFeatureLayer);

        mapView.setMap(map);
    }


}
