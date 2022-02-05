package com.builditcreative.todo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Timer;

public class HomeActivity extends AppCompatActivity {

    String UID;
    double i;
    String Key;
    private HashMap<String, Object> FDB_data = new HashMap<>();
    private ArrayList<HashMap<String, Object>> LM = new ArrayList<>();

    private ListView listView;
    private TextView seleced_text;
    private EditText text;
    private ImageButton share;
    private ImageButton edit;
    private ImageButton delete;
    private ImageButton done;
    private FloatingActionButton add_button;
    private FloatingActionButton save_button;
    private ConstraintLayout edit_layout;

    private final FirebaseDatabase _firebase = FirebaseDatabase.getInstance();
    private DatabaseReference FBDB = _firebase.getReference(""+UID+"");
    private ChildEventListener _FBDB_child_listener;
    private Calendar C;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        FirebaseApp.initializeApp(this);

        listView = findViewById(R.id.listview);
        seleced_text = findViewById(R.id.edit_title);
        share = findViewById(R.id.share_button);
        edit = findViewById(R.id.edit_button);
        delete = findViewById(R.id.delete_button);
        done = findViewById(R.id.done_button);
        add_button = findViewById(R.id.add_new);
        save_button = findViewById(R.id.save_buttton);
        edit_layout = findViewById(R.id.edit_layout);
        text = findViewById(R.id.text);

        String UID = getIntent().getStringExtra("UID");
        FBDB = _firebase.getReference(UID);

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edit_layout.setVisibility(View.VISIBLE);
                save_button.setVisibility(View.VISIBLE);
                add_button.setVisibility(View.GONE);
                text.setText(LM.get((int) i).get("Text").toString());
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FBDB.child(Key).removeValue();
            }
        });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, LM.get((int) i).get("Text").toString());
                sendIntent.setType("text/plain");

                Intent shareIntent = Intent.createChooser(sendIntent, null);
                startActivity(shareIntent);
            }
        });

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (LM.get((int) i).get("isDone").equals("true")){
                    FDB_data = new HashMap<>();
                    FDB_data.put("isDone",false);
                    FBDB.child(Key).updateChildren(FDB_data);
                    done.setImageResource(R.drawable.ic_baseline_done_24);
                }else if (LM.get((int) i).get("isDone").equals("false")){
                    FDB_data = new HashMap<>();
                    FDB_data.put("isDone",true);
                    FBDB.child(Key).updateChildren(FDB_data);
                    done.setImageResource(R.drawable.ic_baseline_close_24);
                }
            }
        });

        add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edit_layout.setVisibility(View.VISIBLE);
                save_button.setVisibility(View.VISIBLE);
                add_button.setVisibility(View.GONE);
                Key = FBDB.push().getKey();
            }
        });

        save_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edit_layout.setVisibility(View.GONE);
                save_button.setVisibility(View.GONE);
                add_button.setVisibility(View.VISIBLE);
                if (text.getText().toString().isEmpty()){

                }else {
                    FDB_data = new HashMap<>();
                    FDB_data.put("Text",text.getText().toString());
                    C = Calendar.getInstance();
                    FDB_data.put("Time", new SimpleDateFormat("hh:mm,dd.MM.yyyy").format(C.getTime()));
                    FDB_data.put("Key", Key);
                    FDB_data.put("isDone",LM.get((int) i).get("isDone"));
                    FBDB.child(FDB_data.get("Key").toString()).updateChildren(FDB_data);
                }
            }
        });

        text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (text.getText().toString().isEmpty()){
                    save_button.setImageResource(R.drawable.ic_baseline_arrow_back_ios_new_24);
                }else {
                    save_button.setImageResource(R.drawable.ic_baseline_save_24);
                }
            }
        });

        _FBDB_child_listener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                GenericTypeIndicator<HashMap<String, Object>> _ind = new GenericTypeIndicator<HashMap<String, Object>>() {};
                final String _childKey = snapshot.getKey();
                final HashMap<String, Object> _childValue = snapshot.getValue(_ind);
                FBDB.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot _dataSnapshot) {
                        LM = new ArrayList<>();
                        try {
                            GenericTypeIndicator<HashMap<String, Object>> _ind = new GenericTypeIndicator<HashMap<String, Object>>() {};
                            for (DataSnapshot _data : _dataSnapshot.getChildren()) {
                                HashMap<String, Object> _map = _data.getValue(_ind);
                                LM.add(_map);
                            }
                        }
                        catch (Exception _e) {
                            _e.printStackTrace();
                        }

                        listView.setAdapter(new ListViewAdapter(LM));
                        if (0 == LM.size()) {
                            listView.setVisibility(View.GONE);
                        }
                        else {
                            listView.setVisibility(View.VISIBLE);
                        }i = 1.5;
                        ((BaseAdapter)listView.getAdapter()).notifyDataSetChanged();

                    }
                    @Override
                    public void onCancelled(DatabaseError _databaseError) {
                    }
                });
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                GenericTypeIndicator<HashMap<String, Object>> _ind = new GenericTypeIndicator<HashMap<String, Object>>() {};
                final String _childKey = snapshot.getKey();
                final HashMap<String, Object> _childValue = snapshot.getValue(_ind);
                FBDB.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot _dataSnapshot) {
                        LM = new ArrayList<>();
                        try {
                            GenericTypeIndicator<HashMap<String, Object>> _ind = new GenericTypeIndicator<HashMap<String, Object>>() {};
                            for (DataSnapshot _data : _dataSnapshot.getChildren()) {
                                HashMap<String, Object> _map = _data.getValue(_ind);
                                LM.add(_map);
                            }
                        }
                        catch (Exception _e) {
                            _e.printStackTrace();
                        }

                        listView.setAdapter(new ListViewAdapter(LM));
                        if (0 == LM.size()) {
                            listView.setVisibility(View.GONE);
                        }
                        else {
                            listView.setVisibility(View.VISIBLE);
                        }
                        i = 1.5;
                        ((BaseAdapter)listView.getAdapter()).notifyDataSetChanged();

                    }
                    @Override
                    public void onCancelled(DatabaseError _databaseError) {
                    }
                });
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                GenericTypeIndicator<HashMap<String, Object>> _ind = new GenericTypeIndicator<HashMap<String, Object>>() {};
                final String _childKey = snapshot.getKey();
                final HashMap<String, Object> _childValue = snapshot.getValue(_ind);
                FBDB.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot _dataSnapshot) {
                        LM = new ArrayList<>();
                        try {
                            GenericTypeIndicator<HashMap<String, Object>> _ind = new GenericTypeIndicator<HashMap<String, Object>>() {};
                            for (DataSnapshot _data : _dataSnapshot.getChildren()) {
                                HashMap<String, Object> _map = _data.getValue(_ind);
                                LM.add(_map);
                            }
                        }
                        catch (Exception _e) {
                            _e.printStackTrace();
                        }

                        listView.setAdapter(new ListViewAdapter(LM));
                        if (0 == LM.size()) {
                            listView.setVisibility(View.GONE);
                        }
                        else {
                            listView.setVisibility(View.VISIBLE);
                        }
                        i = 1.5;
                        ((BaseAdapter)listView.getAdapter()).notifyDataSetChanged();

                    }
                    @Override
                    public void onCancelled(DatabaseError _databaseError) {
                    }
                });
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        FBDB.addChildEventListener(_FBDB_child_listener);


//        AlertDialog.Builder alert = new AlertDialog.Builder(this);
//        final EditText edittext = new EditText(this);
//        alert.setTitle("Enter Text");
//        alert.setView(edittext);
//        alert.setPositiveButton("Save", new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int whichButton) {
//
//            }
//        });
//        alert.setNegativeButton("Cancal", new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int whichButton) {
//            }
//        });
//
//        add_button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                alert.show();
//            }
//        });


    }
    public class ListViewAdapter extends BaseAdapter {

        ArrayList<HashMap<String, Object>> _data;

        public ListViewAdapter(ArrayList<HashMap<String, Object>> _arr) {
            _data = _arr;
        }

        @Override
        public int getCount() {
            return _data.size();
        }

        @Override
        public HashMap<String, Object> getItem(int _index) {
            return _data.get(_index);
        }

        @Override
        public long getItemId(int _index) {
            return _index;
        }

        @Override
        public View getView(final int _position, View _v, ViewGroup _container) {
            LayoutInflater _inflater = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View _view = _v;
            if (_view == null) {
                _view = _inflater.inflate(R.layout.list_item, null);
            }
            CheckBox checkbox = _view.findViewById(R.id.checkbox);
            final ConstraintLayout tool_bar = findViewById(R.id.tool_bar);
            if (_data.get(_position).get("isDone").equals(true)){
                checkbox.setPaintFlags(checkbox.getPaintFlags()| Paint.STRIKE_THRU_TEXT_FLAG);
            }
            checkbox.setText(_data.get((int)_position).get("Text").toString());

            if (i!=1.5){
                tool_bar.setEnabled(true);
                edit.setEnabled(true);
                delete.setEnabled(true);
                share.setEnabled(true);
                done.setEnabled(true);
                if (i==_position){
                    checkbox.setChecked(true);
                } else{
                    checkbox.setChecked(false);
                }
            }else {
                tool_bar.setEnabled(false);
                edit.setEnabled(false);
                delete.setEnabled(false);
                share.setEnabled(false);
                done.setEnabled(false);
            }

            checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean Checked) {
                    if (Checked){
                        if (_data.get((int)_position).get("isDone").equals(true)){
                            done.setImageResource(R.drawable.ic_baseline_close_24);
                        }else {
                            done.setImageResource(R.drawable.ic_baseline_done_24);
                        }
                        Key = _data.get((int)_position).get("Key").toString();
                        i = _position;
                    }else {
                        if (i==_position){
                            i = 1.5;
                        }
                    }
                    ((BaseAdapter)listView.getAdapter()).notifyDataSetChanged();
                }
            });
            return _view;
        }
    }
}