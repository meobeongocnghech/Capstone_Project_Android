package com.viettrekker.mountaintrekkingadviser.util.transformation;

import org.jetbrains.annotations.NotNull;

import android.view.View;

import java.util.ArrayList;
import java.util.List;

import androidx.viewpager.widget.ViewPager;

public class ParallaxPageTransformer implements ViewPager.PageTransformer {

    private List<ParallaxTransformInformation> mViewsToParallax
            = new ArrayList<ParallaxTransformInformation>();

    public ParallaxPageTransformer() {
    }

    public ParallaxPageTransformer(@NotNull List<ParallaxTransformInformation> viewsToParallax) {
        mViewsToParallax = viewsToParallax;
    }

    public ParallaxPageTransformer addViewToParallax(
            @NotNull ParallaxTransformInformation viewInfo) {
        if (mViewsToParallax != null) {
            mViewsToParallax.add(viewInfo);
        }
        return this;
    }

    public void transformPage(View view, float position) {

        int pageWidth = view.getWidth();

        if (position < -1) {
            // This page is way off-screen to the left.
            view.setAlpha(1);

        } else if (position <= 1 && mViewsToParallax != null) { // [-1,1]
            for (ParallaxTransformInformation parallaxTransformInformation : mViewsToParallax) {
                applyParallaxEffect(view, position, pageWidth, parallaxTransformInformation,
                        position > 0);
            }
        } else {
            // This page is way off-screen to the right.
            view.setAlpha(1);
        }
    }

    private void applyParallaxEffect(View view, float position, int pageWidth,
                                     ParallaxTransformInformation information, boolean isEnter) {
        if (information.isValid() && view.findViewById(information.resource) != null) {
            if (isEnter && !information.isEnterDefault()) {
                view.findViewById(information.resource)
                        .setTranslationX(-position * (pageWidth / information.parallaxEnterEffect));
            } else if (!isEnter && !information.isExitDefault()) {
                view.findViewById(information.resource)
                        .setTranslationX(-position * (pageWidth / information.parallaxExitEffect));
            }
        }
    }
}
