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

### Installing
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
mPhotoEditorView.getSource().setImageResource(R.drawable.got);
```
