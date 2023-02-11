package com.example.test333;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RatingBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class BSReviewDialog extends DialogFragment {
    EditText ed1;
    RatingBar bsrev_rb1;
    BSReviewDialogListener listener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_bsreview, null);
        ed1 = view.findViewById(R.id.bsrev_dialog_ed1);
        bsrev_rb1 = view.findViewById(R.id.bsrev_rb1);

        builder.setView(view)
                .setTitle("Add Ratings & Review")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getDialog().dismiss();
                    }
                })
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String review = ed1.getText().toString();
                        float ratings = bsrev_rb1.getRating();
//                        String sratings = Float.toString(ratings);
                        listener.getData(review, ratings);
                    }
                });
        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (BSReviewDialogListener) getTargetFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "must implement BSReviewDialogListener");
        }
    }

    public interface BSReviewDialogListener{
        void getData(String review, Float ratings);
    }
}
