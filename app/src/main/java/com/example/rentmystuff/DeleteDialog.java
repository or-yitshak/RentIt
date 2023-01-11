package com.example.rentmystuff;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.rentmystuff.post.PostModel;
import com.example.rentmystuff.post.PostPageActivity;
import com.example.rentmystuff.profile.ProfileActivity;

public class DeleteDialog extends AppCompatDialogFragment {

    private PostModel post_model;
    private DeleteDialogListener listener;
    private String post_id;

    public DeleteDialog(PostModel post_model, String post_id) {
        this.post_model = post_model;
        this.post_id = post_id;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.delete_dialog, null);
        builder.setView(view)
                .setTitle("Delete?")
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        return;
                    }
                })
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                       post_model.deletePost(post_id);
                        Intent intent = new Intent(DeleteDialog.this.getActivity(), ProfileActivity.class);
                        startActivity(intent);
                    }
                });

        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (DeleteDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() +
                    "must implement ExampleDialogListener");
        }
    }

    public interface DeleteDialogListener {
        void applyTexts(String username, String password);
    }
}
