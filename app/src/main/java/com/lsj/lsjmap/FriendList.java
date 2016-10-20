package com.lsj.lsjmap;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.platform.comapi.map.A;

import java.util.ArrayList;
import java.util.List;

import static android.telephony.SmsManager.RESULT_ERROR_GENERIC_FAILURE;
import static android.telephony.SmsManager.RESULT_ERROR_NO_SERVICE;
import static android.telephony.SmsManager.RESULT_ERROR_NULL_PDU;
import static android.telephony.SmsManager.RESULT_ERROR_RADIO_OFF;

public class FriendList extends AppCompatActivity {

    private StaticStorage publicStorage;
    private static boolean initFlagOfFriend = false; //标识第一次初始化

    private Button btnReturn = null;
    private Button btnGetLocation = null;
    private Button btnEdit = null;
    private Button btnAdd = null;

    private List<Contacts> friendList = new ArrayList<Contacts>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_list);
        //initFriend();
        if(!initFlagOfFriend) {
            initFriend();
            publicStorage.friendList = friendList;
            initFlagOfFriend = true;
        }
        FriendAdapter adapter = new FriendAdapter(FriendList.this,R.layout.friend_item,publicStorage.friendList);
        ListView listview = (ListView) findViewById(R.id.listViewOfFriend);
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                //此处编辑联系人信息
                //final Contacts ob = friendList.get(position);

                LayoutInflater factory = LayoutInflater.from(FriendList.this);
                final View textEntryView = factory.inflate(R.layout.logindialog,null);
                AlertDialog dlg = new AlertDialog.Builder(FriendList.this)
                        .setTitle("编辑联系人")
                        .setView(textEntryView)
                        .setPositiveButton("sure", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String inputUserName = "";
                                String inputPhoneNum = "";

                                EditText userName = (EditText) textEntryView.findViewById(R.id.edit_username);
                                if(userName!= null) {
                                    inputUserName = userName.getText().toString();
                                }
                                EditText phoneNum = (EditText) textEntryView.findViewById(R.id.edit_password);
                                if (phoneNum!= null) {
                                    inputPhoneNum = phoneNum.getText().toString();
                                }

                                publicStorage.friendList.get(position).setFlag(true);
                                publicStorage.friendList.get(position).setName(inputUserName);
                                publicStorage.friendList.get(position).setPhoneNum(inputPhoneNum);
                                //friendList.get(position) = ob;
                            }
                        })
                        .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .create();
                dlg.show();
            }
        });

        btnReturn = (Button) findViewById(R.id.btnReturn);
        btnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initFriend(){
        Contacts Allen = new Contacts("My wife","10086",22.2565180000,113.5433300000,true);
        friendList.add(Allen);
        Contacts Amy = new Contacts("My son","10086",22.2565350000,113.5422160000,true);
        friendList.add(Amy);
        for(int i=0;i<20;++i){
            Contacts test = new Contacts("Friend Name","10010",22,113,false);
            friendList.add(test);
        }
    }
}
