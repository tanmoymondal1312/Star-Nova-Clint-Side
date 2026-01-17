package com.mediaghor.starnova.ui.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.AppCompatButton;

import com.mediaghor.starnova.R;

public class CustomDialog {

    private Dialog dialog;
    private Context context;

    private AppCompatImageView iconView;
    private AppCompatImageView mainImageView;
    private AppCompatTextView titleText;
    private AppCompatTextView descriptionText;
    private AppCompatButton okButton;

    public CustomDialog(@NonNull Context context) {
        this.context = context;
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_fallback); // your XML layout
        dialog.setCancelable(true);

        // Make background transparent
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            // Make dialog full width
            WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
            params.width = WindowManager.LayoutParams.MATCH_PARENT;
            params.height = WindowManager.LayoutParams.WRAP_CONTENT;
            params.gravity = Gravity.CENTER;
            dialog.getWindow().setAttributes(params);
        }

        // Initialize views
        iconView = dialog.findViewById(R.id.icon_type);
        mainImageView = dialog.findViewById(R.id.appCompatImageView);
        titleText = dialog.findViewById(R.id.titleTextDialog);
        descriptionText = dialog.findViewById(R.id.descriptionTextDialog);
        okButton = dialog.findViewById(R.id.appCompatButtonOk);

        // Close dialog on OK click
        okButton.setOnClickListener(v -> dialog.dismiss());
    }

    // Set icon image
    public void setIcon(@DrawableRes int resId) {
        iconView.setImageResource(resId);
    }

    // Set main image
    public void setMainImage(@DrawableRes int resId) {
        mainImageView.setImageResource(resId);
    }

    // Set title text
    public void setTitle(String title) {
        titleText.setText(title);
    }

    // Set description text
    public void setDescription(String description) {
        descriptionText.setText(description);
    }

    // Set icon tint dynamically
    public void setIconTint(int color) {
        iconView.setColorFilter(color);
    }

    // Show the dialog
    public void show() {
        dialog.show();
    }

    // Dismiss the dialog
    public void dismiss() {
        dialog.dismiss();
    }
}
