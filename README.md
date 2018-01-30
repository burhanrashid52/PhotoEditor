# PhotoEditor

A Photo Editor library with simple, easy support for image editing using paints,text,emoji and Sticker like in Instagram stories.

## Features

- [**Drawing**](#drawing) on image with option to change its Brush's Color,Size,Opacity and Erasing.
- Adding/Editing [**Text**](#text) with option to change its Color with Custom Fonts.
- Adding [**Emoji**](#emoji) with Custom Emoji Fonts.
- Adding [**Images/Stickers**](#adding-imagesstickers)
- Pinch to Scale and Rotate views.
- [**Undo and Redo**](#undo-and-redo) for Brush and Views.
- [**Deleting**](#deleting) Views
- [**Saving**](#saving) Photo after editing.


## Benefits
- Hassle free coding
- Increase efficiency
- Easy image editing

## Getting Started
To start with this , you need to just simply add the dependencies in gradle file of app module like this
```
implementation 'ja.burhanrashid52:photoeditor:0.0.5'
```
or your can also import the :photoeditor module from sample for customization

### Prerequisites

Minimum SDK version is supported till API 14

## Setting up the View
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

## Building a PhotoEditor
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
| `setPinchTextScalable()`  | set false to disable pinch to zoom on text insertion.By deafult its true
| `setDefaultTextTypeface()`  | set default text font to be added on image  |
| `setDefaultEmojiTypeface()`  | set default font specifc to add emojis |

That's it we are done with setting up our library



## Drawing
We can customize our brush and paint with diffrent set of property.To start drawing on image we need to enable the drawing mode

![](https://i.imgur.com/INi5LIy.gif)

| Type  | Method |
| ------------- | ------------- |
| Enable/Disable  | `mPhotoEditor.setBrushDrawingMode(true);` |
| Bursh Size (px)  | `mPhotoEditor.setBrushSize(brushSize)` |
| Color Opacity (In %)  |   `mPhotoEditor.setOpacity(opacity)`  |
| Brush Color | `mPhotoEditor.setBrushColor(colorCode)`  |
| Brush Eraser  | `mPhotoEditor.brushEraser()` |

**Note**: Whenever you set any property for brush for drawing it will automatically enables the drawing mode




## Text
You can add the text with input text and colorCode like this

`mPhotoEditor.addText(inputText, colorCode);` 

It will take default fonts provided in the builder,If you want diffrent fonts for diffrent text you can set typeface with each text like this 

`mPhotoEditor.addText(mTypeface,inputText, colorCode);`

In order to edit the text you need the view which you will reacive in you PhotoEditor callback.This callback will trigger when you **Long Press** the added text

 ```
 mPhotoEditor.setOnPhotoEditorListener(new OnPhotoEditorListener() {
            @Override
            public void onEditTextChangeListener(View rootView, String text, int colorCode) {
                
            }
        });
  ```
Now you can edit the text with a view like this

`mPhotoEditor.editText(rootView, inputText, colorCode);`




## Emoji
You can add the Emoji by `PhotoEditor.getEmojis(getActivity());` which will return a list of emojis unicodes

`mPhotoEditor.addEmoji(emojiUnicode);`

It will take default fonts provided in the builder,If you want diffrent Emoji fonts for diffrent emoji you can set typeface with each Emoji like this 

`mPhotoEditor.addEmoji(mEmojiTypeface,emojiUnicode);`




## Adding Images/Stickers
 You need to provide a Bitmap to add you Images  `mPhotoEditor.addImage(bitmap);`
 
 
 

## Undo and Redo
 ```
   mPhotoEditor.undo();
   mPhotoEditor.redo();
 ```
 


## Deleting
  For deleting a Text/Emoji/Image you can click on the view to toggle the view highlighter box which will have a close icon so by on clicking on the icon you can delete the view
  
  
  

## Saving
   
   You need provide a file with callback method when edited image is saved
   
   ```
    mPhotoEditor.saveImage(filePath, new PhotoEditor.OnSaveListener() {
                    @Override
                    public void onSuccess(@NonNull String imagePath) {
                       Log.e("PhotoEditor","Image Saved Successfully");
                    }

                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Log.e("PhotoEditor","Failed to save Image");
                    }
                });
                
    ```
    
 
