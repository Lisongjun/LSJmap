package com.lsj.lsjmap;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
//import android.util.Log;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
/*

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.Poi;
*/
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;

import java.util.List;

import static android.telephony.SmsManager.RESULT_ERROR_GENERIC_FAILURE;
import static android.telephony.SmsManager.RESULT_ERROR_NO_SERVICE;
import static android.telephony.SmsManager.RESULT_ERROR_NULL_PDU;
import static android.telephony.SmsManager.RESULT_ERROR_RADIO_OFF;

public class MainActivity extends AppCompatActivity {

    private Button btnRefresh = null;
    private Button btnMark = null;

    private StaticStorage publicStorage;//静态全局

    private MapView mMapView = null;
    private Button buttonFriend = null;

    private BaiduMap mBaiduMap;

    //定位模块
    private LocationManager locationManager;
    private String provider;
    private Location tempLoc;

    //发送短信模块
    private String SMS_SEND_ACTION = "SMS_SEND";
    private String SMS_DELIVERED_ACTION = "SMS_DELIVERED";
    private SmsStatusReceiver mSmsStatusReceiver;
    private SmsDeliveryStatusReceiver mSmsDeliveryStatusReceiver;
    private IntentFilter receiveFilter;
    private MessageReceiver messageReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //在使用SDK各组件之前初始化context信息，传入ApplicationContext,注意该方法要再setContentView方法之前实现
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_main);

        /*mLocationClient.start();*/
        mMapView = (MapView) findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();
        initMarkMyself();

        btnMark = (Button) findViewById(R.id.btnMark);
        btnMark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //标记friend
                for (int i = 0; i < publicStorage.friendList.size(); i++) {
                    Contacts temp = publicStorage.friendList.get(i);
                    if (temp.getFlag()) {
                        LatLng point = new LatLng(temp.getLatitude(), temp.getLonggitude());
//构建Marker图标
                        BitmapDescriptor bitmap = BitmapDescriptorFactory
                                .fromResource(R.drawable.pin_map_friend);
//构建MarkerOption，用于在地图上添加Marker
                        OverlayOptions option = new MarkerOptions()
                                .position(point)
                                .icon(bitmap);
//在地图上添加Marker，并显示
                        mBaiduMap.addOverlay(option);

                    }
                }

                LatLng cenpt = new LatLng(22.256479, 113.540707);
                MapStatus mMapStatus = new MapStatus.Builder()
                        .target(cenpt)
                        .zoom(18)
                        .build();

                MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
                //改变地图状态
                mBaiduMap.setMapStatus(mMapStatusUpdate);
            }
        });

        //mLocationClient.start();
        //点击刷新重新发送短信对列表联系人进行发短信定位
        btnRefresh = (Button) findViewById(R.id.btnRefresh);
        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PendingIntent sentIntent = PendingIntent.getBroadcast(MainActivity.this, 1, new Intent(SMS_SEND_ACTION), 0);
                //这个意图包装了对短信接受状态回调的处理逻辑
                PendingIntent deliveryIntent = PendingIntent.getBroadcast(MainActivity.this, 2, new Intent(SMS_DELIVERED_ACTION), 0);
                SmsManager manager = SmsManager.getDefault();
                for (int i = 0; i < publicStorage.friendList.size(); i++) {
                    Contacts temp = publicStorage.friendList.get(i);
                    if (temp.getFlag()) {
                        manager.sendTextMessage(temp.getPhoneNum(), null, "Where", sentIntent, deliveryIntent);
                    }
                }
            }
        });
        //短信监听广播注册
        receiveFilter = new IntentFilter();
        receiveFilter.addAction("android.provider.Telephony.SMS_RECEIVED");
        messageReceiver = new MessageReceiver();
        registerReceiver(messageReceiver, receiveFilter);

        buttonFriend = (Button) findViewById(R.id.btnFriend);
        buttonFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentFriend = new Intent();
                intentFriend.setClass(MainActivity.this, FriendList.class);
                startActivity(intentFriend);
            }
        });
    }

    private void initMarkMyself() {
        LatLng point = new LatLng(22.256479, 113.540707);
//构建Marker图标
        BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.this_is_myself);
//构建MarkerOption，用于在地图上添加Marker
        OverlayOptions option = new MarkerOptions().position(point).icon(bitmap);
//在地图上添加Marker，并显示
        //mMapView.addOverlay(option);
        mBaiduMap.addOverlay(option);
        LatLng cenpt = new LatLng(22.256479, 113.540707);
        //定义地图状态
        MapStatus mMapStatus = new MapStatus.Builder()
                .target(cenpt)
                .zoom(18)
                .build();
        MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
        //改变地图状态
        mBaiduMap.setMapStatus(mMapStatusUpdate);
    }
    public class SmsStatusReceiver extends BroadcastReceiver {

        private static final String TAG = "SmsStatusReceiver";

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "SmsStatusReceiver onReceive.");
            switch (getResultCode()) {
                case Activity.RESULT_OK:
                    Log.d(TAG, "Activity.RESULT_OK");
                    break;
                case RESULT_ERROR_GENERIC_FAILURE:
                    Log.d(TAG, "RESULT_ERROR_GENERIC_FAILURE");
                    break;
                case RESULT_ERROR_NO_SERVICE:
                    Log.d(TAG, "RESULT_ERROR_NO_SERVICE");
                    break;
                case RESULT_ERROR_NULL_PDU:
                    Log.d(TAG, "RESULT_ERROR_NULL_PDU");
                    break;
                case RESULT_ERROR_RADIO_OFF:
                    Log.d(TAG, "RESULT_ERROR_RADIO_OFF");
                    break;
            }
        }
    }

    public class SmsDeliveryStatusReceiver extends BroadcastReceiver {

        private static final String TAG = "SmsDeliveryStatusReceiver";

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "SmsDeliveryStatusReceiver onReceive.");
            switch (getResultCode()) {
                case Activity.RESULT_OK:
                    Toast.makeText(context, "Send Succeeded", Toast.LENGTH_SHORT).show();
                    Log.i(TAG, "RESULT_OK");
                    break;
                case Activity.RESULT_CANCELED:
                    Toast.makeText(context, "Send Failed", Toast.LENGTH_SHORT).show();
                    Log.i(TAG, "RESULT_CANCELED");
                    break;
            }
        }
    }

    class MessageReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String sms = "";
            String phoneNum = "";
            Bundle bundle = intent.getExtras();
            Object[] pdus = (Object[]) intent.getExtras().get("pdus");
            for (Object pdu : pdus) {
                SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdu);
                String address = smsMessage.getDisplayOriginatingAddress();
                String fullMessage = smsMessage.getMessageBody();
                phoneNum = address;
                sms += fullMessage;
            }
            if (sms.equals("Where")) {
                //启动定位，定位当前位置
                List<String> providerList = locationManager.getProviders(true);
                if (providerList.contains(LocationManager.GPS_PROVIDER)) {
                    provider = LocationManager.GPS_PROVIDER;
                } else if (providerList.contains(LocationManager.NETWORK_PROVIDER)) {
                    provider = LocationManager.NETWORK_PROVIDER;
                } else {
                    Toast.makeText(MainActivity.this, "No location provider to use", Toast.LENGTH_SHORT).show();
                    //return;
                    Intent i = new Intent();
                    i.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(i);
                }
                Location location = locationManager.getLastKnownLocation(provider);
                if (location != null) {
                    showLocation(location);
                    //System.out.println("get!!!!!");
                }
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                locationManager.requestLocationUpdates(provider, 1000, 1, locationListener);

                PendingIntent sentIntent = PendingIntent.getBroadcast(MainActivity.this, 1, new Intent(SMS_SEND_ACTION), 0);
                //这个意图包装了对短信接受状态回调的处理逻辑
                PendingIntent deliveryIntent = PendingIntent.getBroadcast(MainActivity.this, 2, new Intent(SMS_DELIVERED_ACTION), 0);
                SmsManager manager = SmsManager.getDefault();
                String curPosition =location.getLatitude() + "-" + location.getLongitude();
                manager.sendTextMessage(phoneNum, null, curPosition, sentIntent, deliveryIntent);
            }else{
                //读入经纬度，更新储存
                //分离经纬度
                String tempLatitude = "";
                String tempLongitude = "";
                boolean mark = false;
                for(int i = 0;i<sms.length();i++){
                    if(sms.charAt(i) == '-'){
                        mark = true;
                    }else {
                        if (!mark) {
                            tempLatitude += sms.charAt(i);
                        } else {
                            tempLongitude += sms.charAt(i);
                        }
                    }
                }
                //分别刷新
                for(int i= 0;i<publicStorage.friendList.size();i++){
                    Contacts temp = publicStorage.friendList.get(i);
                    if(temp.getFlag()){
                        //manager.sendTextMessage(temp.getPhoneNum(), null, "Where", sentIntent, deliveryIntent);
                        if(temp.getPhoneNum().equals(phoneNum)){
                            temp.setLatitude(Double.valueOf(tempLatitude.toString()));
                            temp.setLonggitude(Double.valueOf(tempLongitude.toString()));
                        }
                    }
                }
            }
        }
    }

    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            showLocation(location);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }

    };

    private  void  showLocation(Location location){
        String currentPosition ="latitude is "+location.getLatitude() + "\n" + "longgitude is " + location.getLongitude();
        //positionTextView.setText(currentPosition);
        tempLoc = location;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();

        //短信发送模块注销短信接收模块
        unregisterReceiver(messageReceiver);

        //注销位置信息监听(此处有bug)
        if(locationListener!=null) {
            //locationManager.removeUpdates(locationListener);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();

        //短信发送模块注册广播
        mSmsStatusReceiver = new SmsStatusReceiver();
        registerReceiver(mSmsStatusReceiver,new IntentFilter(SMS_SEND_ACTION));
        mSmsDeliveryStatusReceiver = new SmsDeliveryStatusReceiver();
        registerReceiver(mSmsDeliveryStatusReceiver,new IntentFilter(SMS_DELIVERED_ACTION));
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();

        //短信发送模块注销监听广播
        unregisterReceiver(mSmsStatusReceiver);
        unregisterReceiver(mSmsDeliveryStatusReceiver);
    }
}
