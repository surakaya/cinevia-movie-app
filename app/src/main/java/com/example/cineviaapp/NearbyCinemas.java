package com.example.cineviaapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
//import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class NearbyCinemas extends AppCompatActivity implements OnMapReadyCallback {

    private static final int LOCATION_REQUEST_CODE = 1;
    private ListView listView;
    private MapView mapView;
    private GoogleMap mMap;

    private final ArrayList<CinemaPlace> cinemaList = new ArrayList<>();
    private final ArrayList<String> cinemaNames = new ArrayList<>();
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby_cinemas);

        listView = findViewById(R.id.listNearbyPlaces);
        mapView = findViewById(R.id.mapNearby);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        adapter = new ArrayAdapter<>(this, R.layout.custom_list_item, R.id.customText, cinemaNames);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            CinemaPlace selectedPlace = cinemaList.get(position);

            if (mMap != null) {
                LatLng latLng = new LatLng(selectedPlace.getLat(), selectedPlace.getLng());
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));   // haritayı içeride hareket ettir
                // İstersen seçili sinemanın bilgi balonunu da aç:
                // mMap.addMarker(new MarkerOptions().position(latLng).title(selectedPlace.getName())).showInfoWindow();
            }

            // showMapPopup(selectedPlace);   <--  BU SATIRI SİL / YORUM YAP
        });

    }

    /**
     * Liste ve haritaya sabit sinema verilerini yükler.
     */
    private void loadStaticCinemas() {
        cinemaList.clear();
        cinemaNames.clear();


        cinemaList.add(new CinemaPlace("Cepa Sineması, Ankara", 39.9112, 32.7811));
        cinemaList.add(new CinemaPlace("Armada Sineması, Ankara", 39.9203, 32.7906));



        cinemaList.add(new CinemaPlace("Konak Pier Sineması, İzmir", 38.4192, 27.1273));
        cinemaList.add(new CinemaPlace("MaviBahçe Cinemaximum, İzmir", 38.4727, 27.0944));


        cinemaList.add(new CinemaPlace("Zorlu Cinemaximum, İstanbul", 41.0662, 29.0150));
        cinemaList.add(new CinemaPlace("Cevahir Cinemaximum, İstanbul", 41.0602, 28.9875));
        cinemaList.add(new CinemaPlace("Cinemaximum Korupark AVM, Bursa", 40.2134, 28.9649));
        cinemaList.add(new CinemaPlace("ÖzdilekPark Cinetime, Bursa", 40.2474, 29.0185));
        cinemaList.add(new CinemaPlace("Cinemaximum MarkAntalya AVM, Antalya", 36.8947, 30.7044));
        cinemaList.add(new CinemaPlace("Cinemaximum Terracity AVM, Antalya", 36.8705, 30.7396));
        cinemaList.add(new CinemaPlace("Cinemaximum Espark AVM, Eskişehir", 39.7731, 30.5240));
        cinemaList.add(new CinemaPlace("Cinemaximum Forum Gaziantep AVM, Gaziantep", 37.0666, 37.3839));
        cinemaList.add(new CinemaPlace("Cinemaximum Kentplaza AVM, Konya", 37.8720, 32.4782));
        cinemaList.add(new CinemaPlace("Cinemaximum M1 Adana AVM, Adana", 37.0043, 35.2972));
        cinemaList.add(new CinemaPlace("Cinemaximum Kayseri Park AVM, Kayseri", 38.7340, 35.4812));
        cinemaList.add(new CinemaPlace("Cinemaximum Piazza AVM, Samsun", 41.2767, 36.3302));


        for (CinemaPlace c : cinemaList) {
            cinemaNames.add(c.getName());
        }
        adapter.notifyDataSetChanged();

        if (mMap != null) {
            mMap.clear();
            for (CinemaPlace cinema : cinemaList) {
                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(cinema.getLat(), cinema.getLng()))
                        .title(cinema.getName()));
            }
            // Başlangıçta Ankara'ya odaklan
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(39.9203, 32.7906), 11));
        }
    }

    /**
     * Tıklandığında kullanıcıya Google Haritalar’a yönlendirme seçeneği sunar.
     */
   /*  private void showMapPopup(CinemaPlace place) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(place.getName());

        String mapUrl = "https://www.google.com/maps/search/?api=1&query="
                + place.getLat() + "," + place.getLng();

        builder.setMessage("Bu sinemanın konumunu Google Haritalar'da görmek ister misiniz?")
                .setPositiveButton("Haritayı Aç",
                        (dialog, which) -> startActivity(
                                new Intent(Intent.ACTION_VIEW, Uri.parse(mapUrl))))
                .setNegativeButton("Kapat", (dialog, which) -> dialog.dismiss());

        builder.create().show();
    }  */


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            try {
                mMap.setMyLocationEnabled(true);
            } catch (SecurityException e) {
                e.printStackTrace();
            }
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_REQUEST_CODE);
        }

        // Harita hazır olduğunda sabit sinemaları yükle
        loadStaticCinemas();
    }


    // MapView lifecycle
    @Override protected void onResume() { super.onResume(); mapView.onResume(); }
    @Override protected void onPause() { super.onPause(); mapView.onPause(); }
    @Override protected void onDestroy() { super.onDestroy(); mapView.onDestroy(); }
    @Override public void onLowMemory() { super.onLowMemory(); mapView.onLowMemory(); }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_REQUEST_CODE &&
                grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            try {
                if (mMap != null) {
                    mMap.setMyLocationEnabled(true);
                }
            } catch (SecurityException e) {
                e.printStackTrace();
            }
        }

    }
}
