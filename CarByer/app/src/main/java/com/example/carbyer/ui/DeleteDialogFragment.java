package com.example.carbyer.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.example.carbyer.R;

public class DeleteDialogFragment extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        String label = getArguments().getString("label", "item");

        return new AlertDialog.Builder(requireContext())
                .setTitle(R.string.delete)
                .setMessage(getString(R.string.delete_confirmation, label))
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.delete, (dialog, which) -> {

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