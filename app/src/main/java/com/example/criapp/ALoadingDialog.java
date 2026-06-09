package com.example.criapp;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import com.example.criapp.base.BaseActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.facebook.shimmer.Shimmer;
import com.facebook.shimmer.ShimmerFrameLayout;

public class ALoadingDialog extends Dialog {

    private static final long SHOW_DELAY_MS = 200;
    private static final long PHASE2_DELAY_MS = 1300;

    private final Handler handler = new Handler(Looper.getMainLooper());
    private boolean pendingShow = false;

    private ShimmerFrameLayout shimmerBar;
    private View fullScreenLoader;
    private LottieAnimationView lottieView;
    private final Activity activity;

    public ALoadingDialog(@NonNull Context context) {
        super(context);
        activity = context instanceof Activity ? (Activity) context : null;
        Window window = getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
        setTitle(null);
        setCancelable(false);
        setOnCancelListener(null);
        View view = LayoutInflater.from(context).inflate(R.layout.loading_layout, null);
        setContentView(view);
        shimmerBar = view.findViewById(R.id.shimmer_bar);
        shimmerBar.setShimmer(new Shimmer.ColorHighlightBuilder()
                .setHighlightColor(ContextCompat.getColor(context, R.color.light_blue))
                .setBaseColor(ContextCompat.getColor(context, R.color.purple))
                .setDuration(800)
                .setDirection(Shimmer.Direction.LEFT_TO_RIGHT)
                .setAutoStart(false)
                .build());
        fullScreenLoader = view.findViewById(R.id.full_screen_loader);
        lottieView = view.findViewById(R.id.lottie_view);
    }

    @Override
    protected void onStart() {
        super.onStart();
        shimmerBar.startShimmer();
        Window window = getWindow();
        if (window != null) {
            window.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT
            );
            WindowManager.LayoutParams params = window.getAttributes();
            params.dimAmount = 0f;
            window.setAttributes(params);
        }
    }

    @Override
    public void show() {
        pendingShow = true;
        handler.postDelayed(() -> {
            if (pendingShow) {
                ALoadingDialog.super.show();
                handler.postDelayed(() -> {
                    if (pendingShow && isShowing()) {
                        switchToPhase2();
                    }
                }, PHASE2_DELAY_MS);
            }
        }, SHOW_DELAY_MS);
    }

    private void switchToPhase2() {
        shimmerBar.stopShimmer();
        fullScreenLoader.setAlpha(0f);
        fullScreenLoader.setVisibility(View.VISIBLE);
        lottieView.playAnimation();
        fullScreenLoader.animate()
                .alpha(1f)
                .setDuration(400)
                .withEndAction(() -> shimmerBar.setVisibility(View.GONE))
                .start();
        applyLoadingSystemBarsStyle();
    }

    private void applyLoadingSystemBarsStyle() {
        if (activity == null) return;
        Window activityWindow = activity.getWindow();
        boolean isDark = (activity.getResources().getConfiguration().uiMode &
                android.content.res.Configuration.UI_MODE_NIGHT_MASK) ==
                android.content.res.Configuration.UI_MODE_NIGHT_YES;
        if (isDark) {
            activityWindow.setStatusBarColor(ContextCompat.getColor(activity, R.color.status_bar_default));
            activityWindow.setNavigationBarColor(ContextCompat.getColor(activity, R.color.status_bar_default));
        } else {
            activityWindow.setStatusBarColor(Color.parseColor("#E6FFFFFF"));
            activityWindow.setNavigationBarColor(Color.parseColor("#E6FFFFFF"));
        }
        WindowInsetsControllerCompat insetsController =
                new WindowInsetsControllerCompat(activityWindow, activityWindow.getDecorView());
        insetsController.setAppearanceLightStatusBars(!isDark);
        insetsController.setAppearanceLightNavigationBars(!isDark);
    }

    private void restoreSystemBarsStyle() {
        if (activity == null) return;
        Window activityWindow = activity.getWindow();
        if (activity instanceof BaseActivity) {
            BaseActivity base = (BaseActivity) activity;
            activityWindow.setStatusBarColor(ContextCompat.getColor(activity, base.getStatusBarColorRes()));
            activityWindow.setNavigationBarColor(ContextCompat.getColor(activity, base.getNavigationBarColorRes()));
            WindowInsetsControllerCompat insetsController =
                    new WindowInsetsControllerCompat(activityWindow, activityWindow.getDecorView());
            insetsController.setAppearanceLightStatusBars(base.getLightStatusBars());
            insetsController.setAppearanceLightNavigationBars(base.getLightNavigationBars());
        }
    }

    @Override
    public void cancel() {
        pendingShow = false;
        handler.removeCallbacksAndMessages(null);
        if (isShowing()) {
            shimmerBar.stopShimmer();
            lottieView.cancelAnimation();
            restoreSystemBarsStyle();
            super.cancel();
        }
    }
}

