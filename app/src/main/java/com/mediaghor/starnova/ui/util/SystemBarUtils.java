package com.mediaghor.starnova.ui.util;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowInsetsController;
import androidx.annotation.ColorRes;
import androidx.core.content.ContextCompat;

public class SystemBarUtils {

    /**
     * Set StatusBar and NavigationBar with the same color
     */
    public static void setSystemBars(Activity activity, int backgroundColor, boolean lightIcons) {
        setSystemBars(activity, backgroundColor, backgroundColor, lightIcons);
    }

    /**
     * Set StatusBar and NavigationBar with separate colors
     *
     * @param activity The activity where you want to apply it
     * @param statusBarColor The color for the status bar background
     * @param navigationBarColor The color for the bottom navigation bar
     * @param lightIcons True = dark icons, False = light icons
     */
    public static void setSystemBars(Activity activity, int statusBarColor,
                                     int navigationBarColor, boolean lightIcons) {

        // Convert resource IDs to actual colors if needed
        int statusColor = resolveColor(activity, statusBarColor);
        int navColor = resolveColor(activity, navigationBarColor);

        Window window = activity.getWindow();
        window.setStatusBarColor(statusColor);
        window.setNavigationBarColor(navColor);

        setIconsAppearance(window, lightIcons);
    }

    /**
     * Set StatusBar and NavigationBar with resource IDs
     */
    public static void setSystemBarsWithResources(Activity activity, @ColorRes int statusBarColorRes,
                                                  @ColorRes int navigationBarColorRes, boolean lightIcons) {
        int statusColor = ContextCompat.getColor(activity, statusBarColorRes);
        int navColor = ContextCompat.getColor(activity, navigationBarColorRes);
        setSystemBars(activity, statusColor, navColor, lightIcons);
    }

    /**
     * Helper method to set icons appearance (dark/light)
     */
    private static void setIconsAppearance(Window window, boolean lightIcons) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // Android 11+
            WindowInsetsController controller = window.getInsetsController();
            if (controller != null) {
                int appearance = lightIcons
                        ? WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
                        | WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS
                        : 0;

                controller.setSystemBarsAppearance(
                        appearance,
                        WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
                                | WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS
                );
            }

        } else {
            // Android 8–10
            View decorView = window.getDecorView();
            int flags = decorView.getSystemUiVisibility();

            if (lightIcons) {
                flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    flags |= View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
                }
            } else {
                flags &= ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    flags &= ~View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
                }
            }

            decorView.setSystemUiVisibility(flags);
        }
    }

    /**
     * Resolve color - if input is a resource ID, convert to color value
     */
    private static int resolveColor(Activity activity, int colorInput) {
        // If it's a color resource ID (typically large positive number)
        try {
            return ContextCompat.getColor(activity, colorInput);
        } catch (Resources.NotFoundException e) {
            // If it's not a resource ID, use it as a direct color value
            return colorInput;
        }
    }
}