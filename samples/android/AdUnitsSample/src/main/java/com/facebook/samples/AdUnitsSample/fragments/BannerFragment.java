/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 * All rights reserved.
 *
 * This source code is licensed under the license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.facebook.samples.AdUnitsSample.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdListener;
import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;
import com.facebook.samples.AdUnitsSample.R;

public class BannerFragment extends Fragment implements AdListener {

  private static final String TAG = BannerFragment.class.getSimpleName();

  private RelativeLayout bannerAdContainer;
  private Button refreshBannerButton;
  private TextView bannerStatusLabel;
  private @Nullable AdView bannerAdView;

  @Override
  public View onCreateView(
      LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_banner, container, false);

    bannerStatusLabel = (TextView) view.findViewById(R.id.bannerStatusLabel);
    bannerAdContainer = (RelativeLayout) view.findViewById(R.id.bannerAdContainer);
    refreshBannerButton = (Button) view.findViewById(R.id.refreshBannerButton);
    refreshBannerButton.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            loadAdView();
          }
        });
    loadAdView();
    return view;
  }

  @Override
  public void onDestroyView() {
    bannerAdContainer.removeView(bannerAdView);
    super.onDestroyView();
  }

  @Override
  public void onDestroy() {
    if (bannerAdView != null) {
      bannerAdView.destroy();
      bannerAdView = null;
    }
    super.onDestroy();
  }

  private void loadAdView() {
    if (bannerAdView != null) {
      bannerAdView.destroy();
      bannerAdView = null;
    }

    // Update progress message
    setLabel(getString(R.string.loading_status));

    // Create a banner's ad view with a unique placement ID (generate your own on the Facebook
    // app settings). Use different ID for each ad placement in your app.
    boolean isTablet = getResources().getBoolean(R.bool.is_tablet);
    bannerAdView =
        new AdView(
            this.getActivity(),
            "YOUR_PLACEMENT_ID",
            isTablet ? AdSize.BANNER_HEIGHT_90 : AdSize.BANNER_HEIGHT_50);

    // Reposition the ad and add it to the view hierarchy.
    bannerAdContainer.addView(bannerAdView);

    // Initiate a request to load an ad.
    bannerAdView.loadAd(bannerAdView.buildLoadAdConfig().withAdListener(this).build());
  }

  @Override
  public void onError(Ad ad, AdError error) {
    if (ad == bannerAdView) {
      setLabel("Ad failed to load: " + error.getErrorMessage());
    }
  }

  @Override
  public void onAdLoaded(Ad ad) {
    if (ad == bannerAdView) {
      setLabel("");
    }
  }

  @Override
  public void onAdClicked(Ad ad) {
    Toast.makeText(this.getActivity(), "Ad Clicked", Toast.LENGTH_SHORT).show();
  }

  @Override
  public void onLoggingImpression(Ad ad) {
    Log.d(TAG, "onLoggingImpression");
  }

  private void setLabel(String status) {
    bannerStatusLabel.setText(status);
  }
}
