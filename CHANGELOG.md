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
