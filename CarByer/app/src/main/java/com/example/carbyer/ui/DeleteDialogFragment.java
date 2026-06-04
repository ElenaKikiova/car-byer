package com.example.carbyer.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

public class DeleteDialogFragment extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        String label = getArguments().getString("label", "item");

        return new AlertDialog.Builder(requireContext())
                .setTitle("Delete")
                .setMessage("Are you sure you want to delete this " + label + "?")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Delete", (dialog, which) -> {

                    Bundle result = new Bundle();
                    result.putBoolean("confirmed", true);

                    getParentFragmentManager().setFragmentResult(
                            "delete_result",
                            result
                    );
                })
                .create();
    }

    public static DeleteDialogFragment newInstance(String label, int id) {

        Bundle args = new Bundle();
        args.putString("label", label);
        args.putInt("id", id);

        DeleteDialogFragment fragment = new DeleteDialogFragment();
        fragment.setArguments(args);

        return fragment;
    }
}