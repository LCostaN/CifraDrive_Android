package cifradrive.fatec.br.models;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import org.jetbrains.annotations.NotNull;

import cifradrive.fatec.br.R;

public class LoginDialog extends DialogFragment {
    
    public interface LoginDialogListener {
        void onDialogPositiveClick(DialogFragment dialog);
        void onDialogNegativeClick(DialogFragment dialog);
    }
    
    LoginDialogListener mListener;

    @Override
    public void show(FragmentManager manager, String tag) {
        try {
            FragmentTransaction ft = manager.beginTransaction();
            ft.add(this, tag);
            ft.commit();
        } catch (IllegalStateException e) {
            Log.d(tag+"_ERROR", "Exception", e);
        }
    }

    @NotNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.loginAskMessage).setTitle(R.string.loginDialogTitle)
                .setPositiveButton(R.string.goToLogin, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mListener.onDialogPositiveClick(LoginDialog.this);
                    }
                })
                .setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mListener.onDialogNegativeClick(LoginDialog.this);
                    }
                });
        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // Verify that the host context implements the callback interface
        try {
            mListener = (LoginDialogListener) context;
        } catch (ClassCastException e) {
            // The context doesn't implement the interface, throw exception
            throw new ClassCastException(context.toString()
                    + " must implement LoginDialogListener");
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        dialog.dismiss();
        requireActivity().finish();
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        dialog.cancel();
        requireActivity().finish();
    }
}

