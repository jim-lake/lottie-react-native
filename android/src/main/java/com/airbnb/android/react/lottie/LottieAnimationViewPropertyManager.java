package com.airbnb.android.react.lottie;

import android.util.JsonReader;
import android.widget.ImageView;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieDrawable;
import com.airbnb.lottie.RenderMode;

import java.io.StringReader;
import java.lang.ref.WeakReference;

/**
 * Class responsible for applying the properties to the LottieView.
 * The way react-native works makes it impossible to predict in which order properties will be set,
 * also some of the properties of the LottieView needs to be set simultaneously.
 *
 * To solve this, instance of this class accumulates all changes to the view and applies them at
 * the end of react transaction, so it could control how changes are applied.
 */
public class LottieAnimationViewPropertyManager {

  private final WeakReference<LottieAnimationView> viewWeakReference;

  private String animationJson;
  private Float progress;
  private Boolean loop;
  private Float speed;

  /**
   * Should be set to true if one of the animationName related parameters has changed as a result
   * of last reconciliation. We need to update the animation in this case.
   */
  private boolean animationNameDirty;

  private String animationName;
  //private LottieAnimationView.CacheStrategy cacheStrategy;
  private Boolean useHardwareAcceleration;
  private ImageView.ScaleType scaleType;
  private String imageAssetsFolder;
  private Boolean enableMergePaths;

  public LottieAnimationViewPropertyManager(LottieAnimationView view) {
    this.viewWeakReference = new WeakReference<>(view);
  }

  public void setAnimationName(String animationName) {
    this.animationName = animationName;
    this.animationNameDirty = true;
  }

  public void setAnimationJson(String json) {
    this.animationJson = json;
  }
  /*
  public void setCacheStrategy(LottieAnimationView.CacheStrategy strategy) {
    this.cacheStrategy = strategy;
    this.animationNameDirty = true;
  }
  */

  public void setProgress(Float progress) {
    this.progress = progress;
  }

  public void setSpeed(float speed) {
    this.speed = speed;
  }

  public void setLoop(boolean loop) {
    this.loop = loop;
  }

  public void setUseHardwareAcceleration(boolean useHardwareAcceleration) {
    this.useHardwareAcceleration = useHardwareAcceleration;
  }

  public void setScaleType(ImageView.ScaleType scaleType) {
    this.scaleType = scaleType;
  }

  public void setImageAssetsFolder(String imageAssetsFolder) {
    this.imageAssetsFolder = imageAssetsFolder;
  }

  public void setEnableMergePaths(boolean enableMergePaths) {
    this.enableMergePaths = enableMergePaths;
  }

  /**
   * Updates the view with changed fields.
   * Majority of the properties here are independent so they are has to be reset to null
   * as soon as view is updated with the value.
   *
   * The only exception from this rule is the group of the properties for the animation.
   * For now this is animationName and cacheStrategy. These two properties are should be set
   * simultaneously if the dirty flag is set.
   */
  public void commitChanges() {
    LottieAnimationView view = viewWeakReference.get();
    if (view == null) {
      return;
    }

    if (animationJson != null) {
      view.setAnimationFromJson(animationJson);
      animationJson = null;
    }

    if (animationNameDirty) {
      view.setAnimation(animationName);
      animationNameDirty = false;
    }

    if (progress != null) {
      view.setProgress(progress);
      progress = null;
    }

    if (loop != null) {
      view.setRepeatCount(loop ? LottieDrawable.INFINITE : 0);
      loop = null;
    }

    if (speed != null) {
      view.setSpeed(speed);
      speed = null;
    }

    if (useHardwareAcceleration != null) {
      view.setRenderMode(useHardwareAcceleration ? RenderMode.HARDWARE : RenderMode.SOFTWARE);
      useHardwareAcceleration = null;
    }

    if (scaleType != null) {
      view.setScaleType(scaleType);
      scaleType = null;
    }

    if (imageAssetsFolder != null) {
      view.setImageAssetsFolder(imageAssetsFolder);
      imageAssetsFolder = null;
    }

    if (enableMergePaths != null) {
        view.enableMergePathsForKitKatAndAbove(enableMergePaths);
        enableMergePaths = null;
    }
  }
}
