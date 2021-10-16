package com.example.suraj.myapplication;

import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;


import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import static java.lang.Math.cos;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener, RoutingListener {

    private GoogleMap mMap;
    private List<Polyline> polylines = null;


    private GPSTracker gpsTracker;
    private Location mLocation;
    double latitude, longitude;
    protected LatLng start = null;
    protected LatLng point1 = null;
    protected LatLng point2 = null;
    protected LatLng point3 = null;
    double kil=10;
    double shir;
    double dol;
    EditText editText;
    TextView textView;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        textView = findViewById(R.id.textView);
        editText = findViewById(R.id.editText);

        gpsTracker = new GPSTracker(getApplicationContext());
        mLocation = gpsTracker.getLocation();


        latitude = mLocation.getLatitude();
        longitude = mLocation.getLongitude();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }


    @Override
    public void onMapReady(GoogleMap googleMap) { //функция вызова карты, которая готова к использованию
        mMap = googleMap;
        kil=kil/4; //четверть введенного значения километров
        shir=kil/111.32; // перевод четверти введенного значения километров в градусы широты
        dol=360/(40075*cos(shir)); // перевод четверти введенного значения километров в градусы долготы

        //Добавление маркеров и перемещение центра
        LatLng loc = new LatLng(latitude, longitude); //задание loc с координатами текущей геолокации
        mMap.addMarker(new MarkerOptions().position(loc).title("My location")); // добавление маркера на карту. Маркер с названием "My location"
        mMap.moveCamera(CameraUpdateFactory.newLatLng(loc));// передвижение центра карты на координаты текущей геолокации
        start = new LatLng(mLocation.getLatitude(), mLocation.getLongitude());// присваивание значения текущего положения точке "start"
        point1 = new LatLng(mLocation.getLatitude(), mLocation.getLongitude() + dol);// обозначение следующей точки с координатами
        mMap.addMarker(new MarkerOptions().position(point1).title("Point 1"));// добавление следующего маркера на карту
        point2 = new LatLng(mLocation.getLatitude() + shir, mLocation.getLongitude() + dol);
        mMap.addMarker(new MarkerOptions().position(point2).title("Point 2"));
        point3 = new LatLng(mLocation.getLatitude() + shir, mLocation.getLongitude());
        mMap.addMarker(new MarkerOptions().position(point3).title("Point 3"));
        Findroutes(start, point1);//обозначение функции нахождения маршрута между точкой начала и следующей
        Findroutes1(point1, point2); //обозначение функции нахождения маршрута между второй точкой и третьей
        Findroutes2(point2, point3); //обозначение функции нахождения маршрута между третьей точкой и четвертой
        Findroutes3(point3, start); //обозначение функции нахождения маршрута между  четвертой точкой и начальной

    }

    public void Findroutes(LatLng Start, LatLng Point1) { //функция построения маршрута. В скобках указываются точки
        if (Start == null || Point1 == null) { // проверка точек на нулевые значения
            Toast.makeText(MapsActivity.this, "Unable to get location", Toast.LENGTH_LONG).show(); //вывод сообщения о невозможности
                                                                                                                // нвхождения точек
        } else {
            Routing routing = new Routing.Builder() //метод нахождения маршрута
                    .travelMode(AbstractRouting.TravelMode.WALKING)//указание вида построения маршрута
                    .withListener(this)//прослушивание объекта "this"
                    .alternativeRoutes(true)//нахождение альтернативной дороги
                    .waypoints(Start, Point1)// указание точек
                    .key("AIzaSyBcic8EQMJpYDgsJbSwQSt4RL8yyWUBlcs")//API ключ
                    .build();//нахождение
            routing.execute();
        }
    }

    public void Findroutes1(LatLng Point1, LatLng Point2) {
        if (Point1 == null || Point2 == null) {
            Toast.makeText(MapsActivity.this, "Unable to get location", Toast.LENGTH_LONG).show();
        } else {
            Routing routing = new Routing.Builder()
                    .travelMode(AbstractRouting.TravelMode.WALKING)
                    .withListener(this)
                    .alternativeRoutes(true)
                    .waypoints(Point1, Point2)
                    .key("AIzaSyBcic8EQMJpYDgsJbSwQSt4RL8yyWUBlcs")
                    .build();
            routing.execute();
        }
    }

    public void Findroutes2(LatLng Point2, LatLng Point3) {
        if (Point2 == null || Point3 == null) {
            Toast.makeText(MapsActivity.this, "Unable to get location", Toast.LENGTH_LONG).show();
        } else {
        Routing routing = new Routing.Builder()
                .travelMode(AbstractRouting.TravelMode.WALKING)
                .withListener(this)
                .alternativeRoutes(true)
                .waypoints(Point2, Point3)
                .key("AIzaSyBcic8EQMJpYDgsJbSwQSt4RL8yyWUBlcs")
                .build();
        routing.execute();
    }

}


    public void Findroutes3(LatLng Point3, LatLng Start) {
        if (Point3 == null || Start == null) {
            Toast.makeText(MapsActivity.this, "Unable to get location", Toast.LENGTH_LONG).show();
        } else {
            Routing routing = new Routing.Builder()
                    .travelMode(AbstractRouting.TravelMode.WALKING)
                    .withListener(this)
                    .alternativeRoutes(true)
                    .waypoints(Point3, Start)
                    .key("AIzaSyBcic8EQMJpYDgsJbSwQSt4RL8yyWUBlcs")
                    .build();
            routing.execute();
        }
    }


    //Routing call back functions.
    @Override
    public void onRoutingFailure(RouteException e) {
        View parentLayout = findViewById(android.R.id.content);
        Snackbar snackbar = Snackbar.make(parentLayout, e.toString(), Snackbar.LENGTH_LONG);
        snackbar.show();
// Findroutes(start,end);
    }

    @Override
    public void onRoutingStart() {
        Toast.makeText(MapsActivity.this, "Finding Route...", Toast.LENGTH_LONG).show();
    }

    //Если дорога найдена успешно..
    @Override
    public void onRoutingSuccess(ArrayList<Route> route, int shortestRouteIndex) {

        CameraUpdate center = CameraUpdateFactory.newLatLng(start);
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(16);
        if (polylines != null) {
            polylines.clear();
        }

    PolylineOptions polyOptions = new PolylineOptions();



        polylines = new ArrayList<>();
//add route(s) to the map using polyline
        for (int i = 0; i < route.size(); i++) {

            if (i == shortestRouteIndex) {
                polyOptions.color(getResources().getColor(R.color.colorPrimary));
                polyOptions.width(7);
                polyOptions.addAll(route.get(shortestRouteIndex).getPoints());
                Polyline polyline = mMap.addPolyline(polyOptions);
                polylines.add(polyline);

            }

        }

    }

    @Override
    public void onRoutingCancelled() {
        Findroutes(start,point1);
        Findroutes1(point1,point2);
        Findroutes2(point2,point3);
        Findroutes3(point3,start);

    }




    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Findroutes(start,point1);
        Findroutes1(point1,point2);
        Findroutes2(point2,point3);
        Findroutes3(point3,start);

    }



}
