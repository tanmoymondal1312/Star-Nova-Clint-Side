package com.mediaghor.starnova.ui.Dialog;

import android.animation.ValueAnimator;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;

import com.airbnb.lottie.LottieAnimationView;

public class LoadingDialog {

    private final Dialog dialog;

    public LoadingDialog(Context context) {

        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        // Root layout
        FrameLayout rootLayout = new FrameLayout(context);
        rootLayout.setLayoutParams(new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));
        rootLayout.setBackgroundColor(Color.TRANSPARENT);

        // Lottie view
        LottieAnimationView lottieView = new LottieAnimationView(context);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                200,
                200
        );
        params.gravity = Gravity.CENTER;
        lottieView.setLayoutParams(params);

        // Lottie setup
        lottieView.setAnimation("loading.json"); // assets folder
        lottieView.setRepeatCount(ValueAnimator.INFINITE);
        lottieView.playAnimation();

        rootLayout.addView(lottieView);
        dialog.setContentView(rootLayout);

        // Transparent background
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(
                    new ColorDrawable(Color.TRANSPARENT)
            );
        }
    }

    // Show loader safely
    public void start() {
        if (dialog != null && !dialog.isShowing()) {
            dialog.show();
        }
    }

    // Dismiss loader safely
    public void dismiss() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    // 🔥 NEW: Check if loader is visible
    public boolean isShowing() {
        return dialog != null && dialog.isShowing();
    }
}
