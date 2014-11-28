package com.benjycap.drawshare;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

/**
 * Created by Ben on 15/11/2014.
 */
public class SaveFileDialogFragment extends DialogFragment {


    // TODO open keyboard when dialog opens
    public static final String TAG = "SaveFileDialogFragment";

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_fragment_save_file, null);

        // Put DrawActivity instance's working file name in the dialog's edit box
        String workingFileName = ((DrawActivity)getActivity()).getWorkingFileName();
        if (workingFileName != null) {
            EditText editText = (EditText) dialogView.findViewById(R.id.save_dialog_edit_text);
            editText.setText(workingFileName);
            // Select all text for easy rename
            editText.setSelection(0, editText.length());
        }

        builder.setView(dialogView)
                .setPositiveButton(R.string.save_serializable_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Get activity's draw fragment
                        DrawFragment drawFragment = (DrawFragment)getActivity().getSupportFragmentManager().findFragmentById(R.id.fragment_draw);
                        // Get draw fragment's painted path that is going to be saved
                        PaintedPathList.SerializableInstance saveInstance = drawFragment.getDrawViewSerializable();
                        // Get file name from EditText
                        EditText editText = (EditText)getDialog().findViewById(R.id.save_dialog_edit_text);
                        String fileName = editText.getText().toString();
                        // Save File
                        FileHelper.Save(getActivity(), saveInstance, fileName);
                        // Update DrawActivity with details of current working file name
                        ((DrawActivity) getActivity()).setWorkingFileName(fileName);
                    }
                })
                .setNegativeButton(R.string.cancel_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        getDialog().cancel();
                    }
                });

        return builder.create();
    }
}
