package com.example.test333;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class BSMenuFragment extends Fragment {
    String hawker_name;
    public String fmenu_name, fmenu_price, fmenu_desc;
    TableLayout fmenu_tl;
    ArrayList Row_id = new ArrayList(50), Name_id = new ArrayList(50), Price_id = new ArrayList(50), Desc_id = new ArrayList(50);
    Integer row_id = 1, name_id = 101, price_id = 201, desc_id = 301;

    sqliteDB sdb;
    String username;
    FirebaseFirestore userdb;
    DatabaseReference database;
    String item_name,item_price,item_desc;
    View fmenu;
    BSMenuFragment(String h_name){this.hawker_name = h_name;}
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View bsmenu_rootView = inflater.inflate(R.layout.fragment_bs_menu, container, false);
        fmenu = bsmenu_rootView;
        fmenu_tl = bsmenu_rootView.findViewById(R.id.bsmenu_tl);
        Toast.makeText(getContext(), hawker_name, Toast.LENGTH_SHORT).show();
        userdb= FirebaseFirestore.getInstance();
        database = FirebaseDatabase.getInstance().getReference("HAWKERS").child(hawker_name).child("items");
        getDataFromFirebase();
        return bsmenu_rootView;
    }
    private void getDataFromFirebase() {
        ProgressDialog dialog = new ProgressDialog(getContext());
        dialog.setMessage("Fetching Details");
        userdb.collection("HAWKERS").document(hawker_name).collection("items").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                clearRows();
                for (DocumentSnapshot doc : value) {
                    dialog.show();
                    if (error != null) {
                        Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        item_name = doc.getId().toString();
                        item_price = doc.get("price").toString();
                        item_desc = doc.getString("description");
                        addRow(item_name,item_price,item_desc);
                    }
                    dialog.dismiss();
                }

            }
        });
    }
    public void addRow(String item_name,String item_price,String item_desc){

        TableRow tr_row = new TableRow(getActivity());
        tr_row.setId(row_id);
        Row_id.add(row_id);
        row_id++;
        tr_row.setLayoutParams(new TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT));

        EditText label_name = new EditText(getActivity());
        label_name.setId(name_id);
        Name_id.add(name_id);
        name_id++;
        label_name.setText(item_name);
        label_name.setTextColor(Color.BLACK);
        label_name.setGravity(Gravity.CENTER);
        label_name.setEnabled(false);
        label_name.setPadding(5, 20, 5, 20);
        tr_row.addView(label_name);

        EditText label_price = new EditText(getActivity());
        label_price.setId(price_id);
        Price_id.add(price_id);
        price_id++;
        label_price.setText(item_price);
        label_price.setTextColor(Color.BLACK);
        label_price.setGravity(Gravity.CENTER);
        label_price.setEnabled(false);
        label_price.setPadding(5, 20, 5, 20);
        tr_row.addView(label_price);

        EditText label_desc = new EditText(getActivity());
        label_desc.setId(desc_id);
        Desc_id.add(desc_id);
        desc_id++;
        label_desc.setText(item_desc);
        label_desc.setTextColor(Color.BLACK);
        label_desc.setGravity(Gravity.CENTER);
        label_desc.setEnabled(false);
        label_desc.setPadding(5, 20, 5, 20);
        tr_row.addView(label_desc);

        fmenu_tl.addView(tr_row, new TableLayout.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.MATCH_PARENT));

    }
    private void clearRows() {
        for (int i = 0; i < Row_id.size(); i++){
            int id_r = (int) Row_id.get(i);
            TableRow row = (TableRow) fmenu.findViewById(id_r);
            fmenu_tl.removeView(row);
            row_id = 1;
            name_id = 101;
            price_id = 201;
            desc_id = 301;
        }
        Row_id.clear();
    }
}