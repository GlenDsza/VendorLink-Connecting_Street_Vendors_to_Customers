package com.example.test333;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class MenuDialog extends DialogFragment {
    EditText ed1,ed2,ed3;
    MenuDialogListener listener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_add, null);
        ed1 = view.findViewById(R.id.dialog_product);
        ed2 = view.findViewById(R.id.dialog_price);
        ed3 = view.findViewById(R.id.dialog_description);

        builder.setView(view)
                .setTitle("Add Product")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    getDialog().dismiss();
                    }
                })
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String name = ed1.getText().toString();
                        String price = ed2.getText().toString();
                        String description = ed3.getText().toString();
                        listener.getData(name, price, description);
                    }
                });
        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (MenuDialogListener) getTargetFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "must implement MenuDialogListener");
        }
    }

    public interface MenuDialogListener{
        void getData(String name, String Price, String Description);
    }
}
