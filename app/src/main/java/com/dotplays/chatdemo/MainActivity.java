package com.dotplays.chatdemo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.dotplays.chatdemo.adapter.ChatAdapter;
import com.dotplays.chatdemo.model.Chat;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.rockerhieu.emojicon.EmojiconEditText;
import com.vanniktech.emoji.EmojiEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private TextView tvLa;
    private TextView tvLo;

    Double x = 1.0;
    Double y =2.0;
    private Socket mSocket;

    {
        try {
            mSocket = IO.socket("https://socket-io-chat.now.sh");
        } catch (URISyntaxException e) {
        }
    }
    private Button btnShare;
    private RecyclerView lvList;

    private List<Chat> chatList;

    private LinearLayoutManager linearLayoutManager;

    private ChatAdapter chatAdapter;

    private EmojiconEditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvLa = (TextView) findViewById(R.id.tvLa);
        tvLo = (TextView) findViewById(R.id.tvLo);
        btnShare = (Button) findViewById(R.id.btnShare);

//        x= Double.valueOf(tvLa.getText().toString());
//        y= Double.valueOf(tvLo.getText().toString());
//        LatLng sydney = new LatLng(x, y);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(MainActivity.this);
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // reuqest for permission
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    AppConstants.LOCATION_REQUEST);
            locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(20 * 1000);
            locationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    if (locationResult == null) {
                        return;
                    }
                    for (Location location : locationResult.getLocations()) {
                        if (location != null) {
                            x = location.getLatitude();
                            y = location.getLongitude();
                            tvLa.setText(x.toString());
                            tvLo.setText(y.toString());
                            String message = tvLa.getText().toString() + tvLo.getText().toString();
                            Toast.makeText(MainActivity.this, message,Toast.LENGTH_LONG).show();
                            Chat chat = new Chat();
                            chat.name = "Nguyên";
                            chat.message = message;
                            chatList.add(chat);
                            chatAdapter.notifyDataSetChanged();

                            mSocket.emit("new message", message);
                            editText.setText("");

                            lvList.smoothScrollToPosition(chatList.size() - 1);

                            LatLng sydney = new LatLng(x, y);
                            mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in My Position"));
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
                        }
                    }
                }
            };

        } else {
            locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(20 * 1000);
            locationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    if (locationResult == null) {
                        return;
                    }
                    for (Location location : locationResult.getLocations()) {
                        if (location != null) {
                            x = location.getLatitude();
                            y = location.getLongitude();
                            tvLa.setText(x.toString());
                            tvLo.setText(y.toString());
                            String message = tvLa.getText().toString() + tvLo.getText().toString();
                            Toast.makeText(MainActivity.this, message,Toast.LENGTH_LONG).show();
                            Chat chat = new Chat();
                            chat.name = "Nguyên";
                            chat.message = message;
                            chatList.add(chat);
                            chatAdapter.notifyDataSetChanged();

                            mSocket.emit("new message", message);
                            editText.setText("");

                            lvList.smoothScrollToPosition(chatList.size() - 1);
                            LatLng sydney = new LatLng(x, y);
                            mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Me"));
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
                        }
                    }
                }
            };}


        lvList = findViewById(R.id.lvList);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        chatList = new ArrayList<>();
        linearLayoutManager = new LinearLayoutManager(this);
        chatAdapter = new ChatAdapter(chatList, this);
        lvList.setAdapter(chatAdapter);
        lvList.setLayoutManager(linearLayoutManager);


        editText = findViewById(R.id.editText);
        Button btnSend = (Button) findViewById(R.id.button);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = editText.getText().toString();

                Chat chat = new Chat();
                chat.name = "Nguyên";
                chat.message = message;
                chatList.add(chat);
                chatAdapter.notifyDataSetChanged();

                mSocket.emit("new message", message);
                editText.setText("");

                lvList.smoothScrollToPosition(chatList.size() - 1);
            }
        });

        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {

                Log.e("EVENT", keyEvent.getAction() + "");
                if (keyEvent.getAction() == 0) {
                    String message = editText.getText().toString();

                    Chat chat = new Chat();
                    chat.name = "Nguyên";
                    chat.message = message;
                    chatList.add(chat);
                    chatAdapter.notifyDataSetChanged();

                    mSocket.emit("new message", message);
                    editText.setText("");

                    lvList.smoothScrollToPosition(chatList.size() - 1);
                }

                return false;
            }
        });

        mSocket.emit("add user", "Anh");

        mSocket.on("new message", onNewMessage);
        mSocket.on("login", onLogin);
        mSocket.connect();

        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                                    String message = tvLa.getText().toString()+"\n" + tvLo.getText().toString();
                                    Chat chat = new Chat();
                                    chat.name = "Nguyên";
                                    chat.message = message;
                                    chatList.add(chat);
                                    chatAdapter.notifyDataSetChanged();

                                    mSocket.emit("new message", message);
                                    editText.setText("");

                                    lvList.smoothScrollToPosition(chatList.size() - 1);
                                    LatLng sydney = new LatLng(x, y);
                                    mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Me"));
                                    mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));


        }
        });
    }

    private Emitter.Listener onLogin = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];

                    int numUsers;
                    try {
                        numUsers = data.getInt("numUsers");
                        Toast.makeText(MainActivity.this, "Có " + numUsers + " trong phòng chat",
                                Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        return;
                    }

                }
            });

        }
    };
    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String username;
                    String message;
                    try {
                        username = data.getString("username");
                        message = data.getString("message");

                        Chat chat = new Chat();
                        chat.name = username;
                        chat.message = message;
                        chatList.add(chat);
                        chatAdapter.notifyDataSetChanged();
                        lvList.smoothScrollToPosition(chatList.size() - 1);

                    } catch (JSONException e) {
                        return;
                    }

                    // add the message to view
                    //addMessage(username, message);
                }
            });
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSocket.disconnect();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            // Logic to handle location object
                            x = location.getLatitude();
                            y=location.getLongitude();
                            tvLa.setText(x.toString());
                            tvLo.setText(y.toString());
                            Toast.makeText(MainActivity.this, x.toString() + " " + y.toString(),Toast.LENGTH_SHORT).show();
                            LatLng sydney = new LatLng(x, y);
                            mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
                        }
                    }
                });
        // Add a marker in Sydney and move the camera

    }
}
