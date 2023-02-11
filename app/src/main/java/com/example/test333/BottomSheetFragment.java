package com.example.test333;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class BottomSheetFragment extends BottomSheetDialogFragment {
RatingBar bs_rb;
TextView bs_tv3,bs_tv1,bs_tv2;
CheckBox bs_cb1;
ImageButton bs_hide;
// tab layout
TabLayout bs_tablayout;
ViewPager2 bs_viewpager;
BSAdapter bs_adapter;
private String titles[] = new String[]{"ITEMS", "GALLERY", "REVIEWS"};

ConstraintLayout bs_cl;
BottomSheetDialog dialog;
BottomSheetBehavior bottomSheetBehavior;
String hawker_name;
StorageReference mStorage;
CircleImageView cv_profile;

    BottomSheetFragment(String h_name){this.hawker_name = h_name;}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_bottom_sheet, container, false);
        bs_rb = v.findViewById(R.id.bs_ratingBar);
        bs_tv3 = v.findViewById(R.id.bs_tv3);
        bs_tv1 = v.findViewById(R.id.bs_tv1);
        bs_tv2 = v.findViewById(R.id.bs_tv2);
        float rating = bs_rb.getRating();
        bs_tv3.setText((Float.toString(rating)));
        bs_cb1 = v.findViewById(R.id.bs_cb1);
        mStorage = FirebaseStorage.getInstance().getReference();
        cv_profile = v.findViewById(R.id.bs_profile_image);

        bs_tv2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i1 = new Intent(Intent.ACTION_DIAL);
                i1.setData(Uri.parse("tel:"+bs_tv2.getText().toString()));
                startActivity(i1);
            }
        });

        fillinfo();

        bs_cb1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    Toast.makeText(getContext(), "Vendor added to favourite list", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(getContext(), "Vendor removed from favourite list", Toast.LENGTH_SHORT).show();
            }
        });

        bs_hide = v.findViewById(R.id.bs_hide);
        bs_hide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        //for tabLayout
        bs_tablayout = v.findViewById(R.id.bs_tablayout);
        bs_viewpager = v.findViewById(R.id.bs_viewpager);

        bs_adapter = new BSAdapter(this,hawker_name);
        bs_viewpager.setAdapter(bs_adapter);

        new TabLayoutMediator(bs_tablayout, bs_viewpager,
                ((tab, position) -> {tab.setText(titles[position]);setIco(tab, position);})).attach();

        View root = bs_tablayout.getChildAt(0);
        if (root instanceof LinearLayout) {
            ((LinearLayout) root).setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
            GradientDrawable drawable = new GradientDrawable();
            drawable.setColor(getResources().getColor(R.color.com_facebook_likeview_text_color));
            drawable.setSize(5, 1);
            ((LinearLayout) root).setDividerPadding(10);
            ((LinearLayout) root).setDividerDrawable(drawable);
        }
        //end for tab layout

        return v;
    }

    private void fillinfo() {
        FirebaseFirestore.getInstance()
                .collection("HAWKERS")
                .document(hawker_name)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        bs_tv1.setText(documentSnapshot.getString("Shopname"));
                        bs_tv2.setText(documentSnapshot.getString("Mobile"));
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
        StorageReference filepath = mStorage.child("USERS").child("HAWKERS").child(hawker_name).child("PROFILE");
        filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                if(uri!=null) {
                    Picasso.get().load(uri).into(cv_profile);
                }
            }
        });
        filepath.getDownloadUrl().addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bs_cl = view.findViewById(R.id.bs_cl);
    }
//used to set peek height
    @NonNull @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                BottomSheetDialog bottom_dialog = (BottomSheetDialog) dialog;

                FrameLayout bottomSheet = (FrameLayout) bottom_dialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
                assert bottomSheet != null;
                //BottomSheetBehavior.from(bottomSheet).setState(BottomSheetBehavior.STATE_EXPANDED);
                DisplayMetrics displayMetrics = requireActivity().getResources().getDisplayMetrics();
                int height = displayMetrics.heightPixels;
                int maxHeight = (int) (height*0.20);
//                BottomSheetBehavior.from(bottomSheet).setPeekHeight(maxHeight);
                BottomSheetBehavior.from(bottomSheet).setPeekHeight(400);
            }
        });
        return dialog;
    }

    // user-defined function to set icon in tab items
    public void setIco(TabLayout.Tab tab, Integer position){
        switch (position){
            case 0:
                tab.setIcon(R.drawable.vhome_menu);
                break;
            case 1:
                tab.setIcon(R.drawable.vhome_image);
                break;
            case 2:
                tab.setIcon(R.drawable.vhome_reviews);
                break;
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override public int getTheme() {
       return R.style.CustomShapeAppearanceBottomSheetDialog;
    }
    private int getWindowHeight() {
        // Calculate window height for fullscreen use
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) getContext()).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.heightPixels;
    }
}