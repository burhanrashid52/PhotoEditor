# Change Logs

## 0.1.1
- Change : `app:src="@drawable/got_s"` to `app:photo_src="@drawable/got_s"` in `PhotoEditorView`

## 0.2.0
- New : Add filter using `mPhotoEditor.setFilterEffect(PhotoFilter.BRIGHTNESS);`
- New : Add Custom filter using `CustomEffect`

```
CustomEffect customEffect = new CustomEffect.Builder(EffectFactory.EFFECT_BRIGHTNESS)
                .setParameter("brightness", 0.5f)
                .build();
mPhotoEditor.setFilterEffect(customEffect);
```
- Change : `saveImage(String, OnSaveListener)` is deprecated use `saveAsFile(String, OnSaveListener)`
- New : You can save image as bitmap by using `saveAsBitmap(String, OnSaveListener)`
- Fixed : save image without transparency #35

## 0.2.1
- Fixed: Image not saving #40

## 0.3.1
- New: Disable clearAllViews onSave #54 #49 #42 and PR #71
- Fixed: ViewType in onRemoveViewListener() #50
- Fixed: Wrong ViewType on onStartViewChangeListener #74
- Fixed: clearHelperBox() #37
- Fixed: Brush color changes after saving image  #52
- Fixed: Glide null bitmap issue #59

## 0.3.3
- Fixed : Brush bug using PorterDuff.Mode.SRC_OVER #80 and PR #83

## 0.4.0
- New : Added compress quality and format in save settings
```
SaveSettings saveSettings = new SaveSettings.Builder()
      .setCompressFormat(compressFormat)
      .setCompressQuality(compressQuality)
      .build();
```
- New : Added Text style builder for add and edit text
```
new TextStyleBuilder()
      .withTextColor(123)
      .withTextSize(12f)
      .withGravity(3)
      .withTextFont(Typeface.DEFAULT)
      .withBackgroundColor(321)
      .withTextAppearance(144)
      .applyStyle(textView);
```
- New : Bumped support version to 28
- Removed : All Deprecated methods
- Test : Added test cases for `BrushDrawingView` with 100% code coverage

## 1.0.0
- New : Migrating the support libraries to AndroidX
- New : Bumped target sdk version to 29
- Test : Adding UI to test for the library

## 1.1.0
- Fixed : #263 and #57
- New : Allowing `PhotoEditor` to extend with protected constructor.

## 1.1.1
- Fixed : Replace deprecated `getDrawingCache` to capturing method #324

## 1.1.2
- Fixed : #219 One selection at time in
- Fixed: #345 Set pinch to scale to text only

## 1.1.4
- Fixed : #351 Internal Refactoring
- Removed: (Breaking Change) `PhotoEditor.getEmoji()` is no longer part of the library and it's now move to sample app.

### 1.5.0
- Deprecated : `setBrushSize()` , `setOpacity()` and `setBrushColor`. Use `ShapeBuilder`
- New : Drawing Shapes using `ShapeBuilder`. Support Line, Brush, Oval and Rectangle out of the box.Deprecated
- New : Allowing to add text shadow using `TextStyleBuilder.withTextShadow()`

### 1.5.1
- New : #379 Should disallow drawing on left or right of the image using `photoEditor.setClipSourceImage(true)`
- New/Break : #383 Get a callback when the image source is touched `onTouchSourceImage(MotionEvent event);`

### 2.0.0
- New : Migrated the app and library to Kotlin

### 3.0.0
- New : Arrow shape
- Change : (Breaking Change) `minSdkVersion` changed to `21`
- Change : (Breaking Change) Shape names are no longer UPPERCASE
- New : Suspending functions for saving images: `saveAsFile(String[, SaveSettings])` and `saveAsBitmap([SaveSettings])`
- Fixed : #374 `IndexOutOfBoundsException` when saving bitmap

### 3.0.1
- New : #518 Fix Builder methods' return type in Java
- Fixed : #522 Leak Graphics memory when changing filter

### 3.0.2
- Fixed : #460 Wrong return value of undo() method