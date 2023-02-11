package com.example.test333;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MenuFragment extends Fragment implements MenuDialog.MenuDialogListener, MenuDialogDel.MenuDialogDelListener {

    Button fmenu_b1, fmenu_b2, fmenu_b3,fmenu_b4;
    public String fmenu_name, fmenu_price, fmenu_desc, fmenu_product;
    TableLayout fmenu_tl;
    ArrayList Row_id = new ArrayList(50), Name_id = new ArrayList(50), Price_id = new ArrayList(50), Desc_id = new ArrayList(50);
    Integer row_id = 1, name_id = 101, price_id = 201, desc_id = 301;

    sqliteDB sdb;
    String username;
    FirebaseFirestore userdb;
    DatabaseReference database;
    String item_name,item_price,item_desc;
    View fmenu;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fmenu_view =  inflater.inflate(R.layout.fragment_menu, container, false);
        fmenu = fmenu_view;
        fmenu_b1 = fmenu_view.findViewById(R.id.fmenu_b1);
        fmenu_b2 = fmenu_view.findViewById(R.id.fmenu_b2);
        fmenu_b3 = fmenu_view.findViewById(R.id.fmenu_b3);
        fmenu_b4 = fmenu_view.findViewById(R.id.fmenu_b4);
        fmenu_tl = fmenu_view.findViewById(R.id.fmenu_tl);

        sdb = new sqliteDB(getContext());
        username=sdb.getDoc();
        userdb= FirebaseFirestore.getInstance();
        database = FirebaseDatabase.getInstance().getReference("HAWKERS").child(username).child("items");

        getDataFromFirebase();
        if (Row_id.isEmpty()) {
            fmenu_b2.setEnabled(false);
            fmenu_b3.setEnabled(false);
        }
        fmenu_b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MenuDialog fmenu_dialog = new MenuDialog();
                fmenu_dialog.setTargetFragment(MenuFragment.this, 96);
                fmenu_dialog.show(getActivity().getSupportFragmentManager(), "Add Row");
            }
        });

        fmenu_b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fmenu_b1.setEnabled(false);
                fmenu_b4.setEnabled(false);
                editMenu(fmenu_view);
                fmenu_b2.setVisibility(View.GONE);
                fmenu_b3.setVisibility(View.VISIBLE);
            }
        });

        fmenu_b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fmenu_b1.setEnabled(true);
                fmenu_b4.setEnabled(true);
                updateMenu();
                saveMenu(fmenu_view);
                fmenu_b3.setVisibility(View.GONE);
                fmenu_b2.setVisibility(View.VISIBLE);
            }
        });

        fmenu_b4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MenuDialogDel fmenu_dialog1 = new MenuDialogDel();
                fmenu_dialog1.setTargetFragment(MenuFragment.this, 117);
                fmenu_dialog1.show(getActivity().getSupportFragmentManager(), "Delete Row");
            }
        });

        return fmenu_view;
    }

    private void getDataFromFirebase() {
        ProgressDialog dialog = new ProgressDialog(getContext());
        dialog.setMessage("Fetching Details");
        dialog.show();
        userdb.collection("HAWKERS").document(username).collection("items").addSnapshotListener(new EventListener<QuerySnapshot>() {
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

    @Override
    public void getData(String vname, String vprice, String vdesc) {
        fmenu_name = vname;
        fmenu_price = vprice;
        fmenu_desc = vdesc;
        Toast.makeText(getActivity().getApplicationContext(), "Product Name = "+fmenu_name+"\n Product Price = "+fmenu_price+"\n Product Description = "+fmenu_desc, Toast.LENGTH_SHORT).show();
        insertIntoDatabase(fmenu_name,fmenu_price,fmenu_desc);
    }

    private void insertIntoDatabase(String f_name,String f_price,String f_desc) {
        ProgressDialog pd = new ProgressDialog(getContext());
        pd.setMessage("Adding item");
        pd.show();
        Map<String,String> items=new HashMap<>();
        items.put("price",f_price);
        items.put("description",f_desc);
        DocumentReference docRef = userdb.collection("HAWKERS").document(username).collection("items").document(f_name);
        docRef.set(items).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(getContext(), "item Added", Toast.LENGTH_SHORT).show();
                pd.dismiss();
            }
        });docRef.set(items).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "Operation failed", Toast.LENGTH_SHORT).show();
                pd.dismiss();
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
        fmenu_b2.setEnabled(true);
        fmenu_b3.setEnabled(true);
    }

    public void updateMenu(){
        String it_name,it_price,it_desc;
        TextView tv;
        for (int i = 0; i < Row_id.size(); i++){
            int id_name = (int) Name_id.get(i);
            tv  = (TextView) fmenu.findViewById(id_name);
            it_name = tv.getText().toString();
            int id_price = (int) Price_id.get(i);
            tv  = (TextView) fmenu.findViewById(id_price);
            it_price = tv.getText().toString();
            int id_desc = (int) Desc_id.get(i);
            tv  = (TextView) fmenu.findViewById(id_desc);
            it_desc = tv.getText().toString();
            insertIntoDatabase(it_name,it_price,it_desc);
        }
    }

    public void editMenu(View v){
        for (int i = 0; i < Name_id.size(); i++){
            int id = (int) Name_id.get(i);
            v.findViewById(id).setEnabled(true);
        }
        for (int i = 0; i < Price_id.size(); i++){
            int id = (int) Price_id.get(i);
            v.findViewById(id).setEnabled(true);
        }
        for (int i = 0; i < Desc_id.size(); i++){
            int id = (int) Desc_id.get(i);
            v.findViewById(id).setEnabled(true);
        }
    }

    public void saveMenu(View v){
        for (int i = 0; i < Name_id.size(); i++){
            int id = (int) Name_id.get(i);
            v.findViewById(id).setEnabled(false);
        }
        for (int i = 0; i < Price_id.size(); i++){
            int id = (int) Price_id.get(i);
            v.findViewById(id).setEnabled(false);
        }
        for (int i = 0; i < Desc_id.size(); i++){
            int id = (int) Desc_id.get(i);
            v.findViewById(id).setEnabled(false);
        }
    }

    @Override
    public void getProduct(String name) {
        fmenu_product = name;
        Toast.makeText(getActivity().getApplicationContext(), fmenu_product+" removed!", Toast.LENGTH_SHORT).show();
    }
}