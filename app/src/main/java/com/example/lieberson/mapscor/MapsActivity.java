package com.example.lieberson.mapscor;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback
                                                            , GoogleApiClient.OnConnectionFailedListener
                                                            , GoogleApiClient.ConnectionCallbacks {

    private GoogleMap mMap;
    private LocationManager locationManager;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        ActivityCompat.requestPermissions(this,
                                                 new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                                 99);

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);


   /*
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
*/
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 99: {
                // Se a solicitação for cancelada, as matrizes de resultado estarão vazias.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permissão foi concedida. Faça  tarefa relacionada à localização que você precisa fazer.
                    locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

                    FusedLocationProviderClient mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);


                    if (ActivityCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION) !=
                            PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                            this, Manifest.permission.ACCESS_COARSE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED) {

                        return;
                    }

                    mFusedLocationClient.getLastLocation()
                            .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                                @Override
                                public void onSuccess(Location location) {
                                    // Tem a última localização conhecida. Em algumas situações raras, isso pode ser nulo
                                    if (location != null) {
                                        // Lógica para manipular o objeto de localização
                                        setMyLocation(location);
                                    }
                                }
                            });

                } else {

                    // permissão negada! Desative a funcionalidade que depende dessa permissão.
                }
                return;
            }

        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    public void setMyLocation( Location location ){
        if(location != null) {
            // Recupera latitude e longitude da
            // ultima localização do usuário
            LatLng ultimaLocalizacao = new LatLng(location.getLatitude(), location.getLongitude());
            // Configuração da câmera
            final CameraPosition position = new CameraPosition.Builder()
                    .target(ultimaLocalizacao)     //  Localização
                    .bearing(45)        //  Rotação da câmera
                    .tilt(90)            //   ngulo em graus
                    .zoom(17)           //  Zoom
                    .build();
            CameraUpdate update = CameraUpdateFactory.newCameraPosition(position);
            mMap.animateCamera(update);

            // Criando um objeto do tipo MarkerOptions
            final MarkerOptions markerOptions = new MarkerOptions();
            // Configurando as propriedades do marker
            markerOptions.position( ultimaLocalizacao )    // Localização
                    .title("Minha Localização")       // Título
                    .snippet("Latitude: , Longitude:"); // Descrição
            mMap.addMarker( markerOptions );
        }


    }



    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }
    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i("LOG", "Conectado ao Google Play Services!");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i("LOG", "Conexão Interrompida");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i("LOG", "Erro ao conectar: " + connectionResult);
    }


}
