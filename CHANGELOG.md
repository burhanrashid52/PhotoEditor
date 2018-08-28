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
