/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 * All rights reserved.
 *
 * This source code is licensed under the license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.facebook.samples.AdUnitsSample.fragments;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.NativeAdListener;
import com.facebook.ads.NativeAdViewAttributes;
import com.facebook.ads.NativeBannerAd;
import com.facebook.ads.NativeBannerAdView;
import com.facebook.samples.AdUnitsSample.R;

public class NativeBannerAdTemplateFragment extends Fragment implements NativeAdListener {

  private static final String TAG = NativeBannerAdTemplateFragment.class.getSimpleName();

  private static final int COLOR_LIGHT_GRAY = 0xff90949c;
  private static final int COLOR_DARK_GRAY = 0xff4e5665;
  private static final int COLOR_CTA_BLUE_BG = 0xff4080ff;

  private @Nullable NativeBannerAd mNativeBannerAd;
  private NativeBannerAdView.Type mViewType = NativeBannerAdView.Type.HEIGHT_100;

  private int mAdBackgroundColor, mTitleColor, mLinkColor, mContentColor, mCtaBgColor;

  private TextView mStatusText;
  private ViewGroup mNativeAdContainer;
  private Spinner mBackgroundColorSpinner;
  private Spinner mAdViewTypeSpinner;
  private Button mShowCodeButton;

  @Override
  public View onCreateView(
      LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

    View view = inflater.inflate(R.layout.fragment_native_banner_ad_template, container, false);

    mStatusText = view.findViewById(R.id.status);
    mNativeAdContainer = view.findViewById(R.id.templateContainer);
    mShowCodeButton = view.findViewById(R.id.showCodeButton);
    mBackgroundColorSpinner = view.findViewById(R.id.backgroundColorSpinner);
    mAdViewTypeSpinner = view.findViewById(R.id.adViewTypeSpinner);

    ArrayAdapter<CharSequence> backgroundColorSpinnerAdapter =
        ArrayAdapter.createFromResource(
            getActivity(), R.array.background_color_array, android.R.layout.simple_spinner_item);
    backgroundColorSpinnerAdapter.setDropDownViewResource(
        android.R.layout.simple_spinner_dropdown_item);
    mBackgroundColorSpinner.setAdapter(backgroundColorSpinnerAdapter);

    ArrayAdapter<CharSequence> adViewTypeSpinnerAdapter =
        ArrayAdapter.createFromResource(
            getActivity(), R.array.ad_bannerview_type_array, android.R.layout.simple_spinner_item);
    adViewTypeSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    mAdViewTypeSpinner.setAdapter(adViewTypeSpinnerAdapter);

    setSpinnerListeners();
    setButtonListeners();

    createAndLoadNativeAd();

    return view;
  }

  protected void createAndLoadNativeAd() {
    // Create a native banner ad request with a unique placement ID
    // (generate your own on the Facebook app settings).
    // Use different ID for each ad placement in your app.
    mNativeBannerAd = new NativeBannerAd(getContext(), "YOUR_PLACEMENT_ID");

    // Initiate a request to load an ad.
    mNativeBannerAd.loadAd(mNativeBannerAd.buildLoadAdConfig().withAdListener(this).build());

    mStatusText.setText(R.string.ad_loading);
  }

  private void reloadAdContainer() {
    Activity activity = getActivity();
    if (activity != null
        && mNativeBannerAd != null
        && mNativeBannerAd.isAdLoaded()
        && !mNativeBannerAd.isAdInvalidated()) {
      mNativeAdContainer.removeAllViews();

      // Create a NativeAdViewAttributes object and set the attributes
      NativeAdViewAttributes attributes =
          new NativeAdViewAttributes(activity)
              .setBackgroundColor(mAdBackgroundColor)
              .setTitleTextColor(mTitleColor)
              .setDescriptionTextColor(mContentColor)
              .setButtonBorderColor(mCtaBgColor)
              .setButtonTextColor(mLinkColor)
              .setButtonColor(mCtaBgColor);

      // Use NativeAdView.render to generate the ad View
      View adView = NativeBannerAdView.render(activity, mNativeBannerAd, mViewType, attributes);

      // Add adView to the container showing Ads
      mNativeAdContainer.addView(adView, 0);
      mNativeAdContainer.setBackgroundColor(Color.TRANSPARENT);

      mShowCodeButton.setText(R.string.show_code);
    }
  }

  private void setSpinnerListeners() {
    mBackgroundColorSpinner.setOnItemSelectedListener(
        new android.widget.AdapterView.OnItemSelectedListener() {
          @Override
          public void onItemSelected(AdapterView<?> arg0, View view, int position, long id) {
            int item = mBackgroundColorSpinner.getSelectedItemPosition();
            switch (item) {
              case 0:
                mAdBackgroundColor = Color.WHITE;
                mTitleColor = COLOR_DARK_GRAY;
                mLinkColor = Color.WHITE;
                mContentColor = COLOR_LIGHT_GRAY;
                mCtaBgColor = COLOR_CTA_BLUE_BG;
                break;
              case 1:
                mAdBackgroundColor = Color.BLACK;
                mTitleColor = Color.WHITE;
                mContentColor = Color.LTGRAY;
                mLinkColor = Color.BLACK;
                mCtaBgColor = Color.WHITE;
                break;
            }
            reloadAdContainer();
          }

          @Override
          public void onNothingSelected(AdapterView<?> parent) {}
        });

    mAdViewTypeSpinner.setOnItemSelectedListener(
        new android.widget.AdapterView.OnItemSelectedListener() {
          public void onItemSelected(AdapterView<?> arg0, View view, int position, long id) {
            int item = mAdViewTypeSpinner.getSelectedItemPosition();
            if (item == 0) {
              mViewType = NativeBannerAdView.Type.HEIGHT_50;
            } else if (item == 1) {
              mViewType = NativeBannerAdView.Type.HEIGHT_100;
            } else if (item == 2) {
              mViewType = NativeBannerAdView.Type.HEIGHT_120;
            }
            reloadAdContainer();
          }

          @Override
          public void onNothingSelected(AdapterView<?> parent) {}
        });
  }

  private void setButtonListeners() {
    mShowCodeButton.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            if (mShowCodeButton.getText() == getResources().getString(R.string.show_ad)) {
              reloadAdContainer();
            } else {
              showCodeInAdContainer();
            }
          }
        });
  }

  private void showCodeInAdContainer() {
    String[] lines = getResources().getStringArray(R.array.code_snippet_banner_template);
    StringBuilder codeSnippet = new StringBuilder();
    for (String line : lines) {
      codeSnippet.append(line).append("\r\n");
    }
    mNativeAdContainer.removeAllViews();
    TextView code = new TextView(getActivity());
    code.setText(codeSnippet);
    code.setBackgroundColor(Color.WHITE);
    code.setTextColor(Color.BLACK);
    mNativeAdContainer.addView(code, 0);

    mShowCodeButton.setText(R.string.show_ad);
  }

  @Override
  public void onAdLoaded(Ad ad) {
    if (mNativeBannerAd == null || mNativeBannerAd != ad) {
      // Race condition, load() called again before last ad was displayed
      return;
    }

    mStatusText.setText(R.string.ad_loaded);
    reloadAdContainer();
  }

  @Override
  public void onError(Ad ad, AdError error) {
    String msg = getResources().getString(R.string.ad_load_failed, error.getErrorMessage());
    mStatusText.setText(msg);
  }

  @Override
  public void onAdClicked(Ad ad) {
    mStatusText.setText(R.string.ad_clicked);
  }

  @Override
  public void onLoggingImpression(Ad ad) {
    Log.d(TAG, "onLoggingImpression");
  }

  @Override
  public void onMediaDownloaded(Ad ad) {
    Log.d(TAG, "onMediaDownloaded");
  }

  @Override
  public void onDestroy() {
    mNativeBannerAd = null;
    super.onDestroy();
  }
}
