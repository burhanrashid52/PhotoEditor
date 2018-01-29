# PhotoEditor

A Photo Editor library with simple, easy support for image editing using paints,text,emoji and Sticker like in Instagram stories.

## Getting Started
To start with this , you need to just simply add the gradle link your app module like this
```
dependencies {

    implementation 'ja.burhanrashid52:photoeditor:0.0.5'
    
}
```
or your can also import the :photoeditor module from sample for customization

### Prerequisites

Minimum SDK version is supported till API 14

## Installing

### Setting up the View
First you need to add `PhotoEditorView` in your xml layout

```
 <ja.burhanrashid52.photoeditor.PhotoEditorView
        android:id="@+id/photoEditorView"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        app:src="@drawable/got" />
  
```
Your can define your drawable or color resource directly using `app:src`

Your can set the image programatically by getting source from `PhotoEditorView` which will return a `ImageView` so that you can load image from resources,file or (Picasso/Glide)
```
PhotoEditorView mPhotoEditorView = findViewById(R.id.photoEditorView);

mPhotoEditorView.getSource().setImageResource(R.drawable.got);
```

### Building a PhotoEditor
To use the image editing feature you need to build a PhotoEditor which requires a Context and PhotoEditorView which we have setup in our xml layout


```
//Use custom font using latest support library
Typeface mTextRobotoTf = ResourcesCompat.getFont(this, R.font.roboto_medium);

//loading font from assest
Typeface mEmojiTypeFace = Typeface.createFromAsset(getAssets(), "emojione-android.ttf");

mPhotoEditor = new PhotoEditor.Builder(this, mPhotoEditorView)
         .setPinchTextScalable(true)
         .setDefaultTextTypeface(mTextRobotoTf)
         .setDefaultEmojiTypeface(mEmojiTypeFace)
         .build();
 ```
You can customize the properties in the PhotoEditor as per your requirement

| Property  | Usage |
| ------------- | ------------- |
| setPinchTextScalable()  | set false to disable pinch to zoom on text insertion.By deafult its true
| setDefaultTextTypeface()  | set default text font to be added on image  |
| setDefaultEmojiTypeface()  | set default font specifc to add emojis |

That's it we are done with setting up our library




## Features

### Drawing
We can customize our brush and paint with diffrent set of propert.To start drawing on image we need to enable the drawing mode

| Type  | Method |
| ------------- | ------------- |
| Enable/Disable  | mPhotoEditor.setBrushDrawingMode(true); |
| Bursh Size (px)  | mPhotoEditor.setBrushSize(brushSize) |
| Color Opacity (In %)  |   mPhotoEditor.setOpacity(opacity)  |
| Brush Color | mPhotoEditor.setBrushColor(colorCode)  |



