# PhotoEditor

[![CircleCI](https://circleci.com/gh/burhanrashid52/PhotoEditor.svg?style=svg)](https://circleci.com/gh/burhanrashid52/PhotoEditor)
[![Downloads](https://img.shields.io/badge/Download-0.4.0-blue.svg)](https://bintray.com/burhanrashid52/maven/photoeditor) ![API](https://img.shields.io/badge/API-14%2B-brightgreen.svg) [![JavaDoc](https://img.shields.io/badge/JavaDoc-PhotoEditor-blue.svg)](https://burhanrashid52.github.io/PhotoEditor/) [![Uplabs](https://img.shields.io/badge/Uplabs-PhotoEditor-orange.svg)](https://www.uplabs.com/posts/photoeditor)
[![AndroidArsenal](https://img.shields.io/badge/Android%20Arsenal-PhotoEditor-blue.svg)](https://android-arsenal.com/details/1/6736)
[![AndroidDevDigest](https://img.shields.io/badge/AndroidDev%20Digest-%23185-brightgreen.svg)](https://www.androiddevdigest.com/digest-185)
[![AwesomeAndroid](https://img.shields.io/badge/Awesome%20Android-%2397-red.svg)](https://android.libhunt.com/newsletter/97)
[![AndroidWeekly](https://img.shields.io/badge/Android%20Weekly-%23312-blue.svg)](http://androidweekly.net/issues/issue-312)
[![Mindorks](https://img.shields.io/badge/Mindorks%20Newsletter-%234-ff69b4.svg)](https://mindorks.com/newsletter/edition/4)

A Photo Editor library with simple, easy support for image editing using Paints, Text, Filters, Emoji and Sticker like stories.

## Features

- [**Drawing**](#drawing) on image with option to change its Brush's Color, Size, Opacity and Erasing.
- Apply [**Filter Effect**](#filter-effect) on image using MediaEffect
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
To start with this, we need to simply add the dependencies in the gradle file of our app module like this
```java
implementation 'ja.burhanrashid52:photoeditor:0.4.0'
```
or we can also import the :photoeditor module from sample for further customization


## Setting up the View
First we need to add `PhotoEditorView` in our xml layout

```xml
 <ja.burhanrashid52.photoeditor.PhotoEditorView
        android:id="@+id/photoEditorView"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        app:photo_src="@drawable/got_s" />
  
```
We can define our drawable or color resource directly using `app:photo_src`

We can set the image programmatically by getting source from `PhotoEditorView` which will return a `ImageView` so that we can load image from resources,file or (Picasso/Glide)
```java
PhotoEditorView mPhotoEditorView = findViewById(R.id.photoEditorView);

mPhotoEditorView.getSource().setImageResource(R.drawable.got);
```

## Building a PhotoEditor
To use the image editing feature we need to build a PhotoEditor which requires a Context and PhotoEditorView which we have to setup in our xml layout


```java
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
We can customize the properties in the PhotoEditor as per our requirement

| Property  | Usage |
| ------------- | ------------- |
| `setPinchTextScalable()`  | set false to disable pinch to zoom on text insertion.By default its true
| `setDefaultTextTypeface()`  | set default text font to be added on image  |
| `setDefaultEmojiTypeface()`  | set default font specifc to add emojis |

That's it we are done with setting up our library



## Drawing
We can customize our brush and paint with different set of property. To start drawing on image we need to enable the drawing mode

![](https://i.imgur.com/INi5LIy.gif)

| Type  | Method |
| ------------- | ------------- |
| Enable/Disable  | `mPhotoEditor.setBrushDrawingMode(true);` |
| Bursh Size (px)  | `mPhotoEditor.setBrushSize(brushSize)` |
| Color Opacity (In %)  |   `mPhotoEditor.setOpacity(opacity)`  |
| Brush Color | `mPhotoEditor.setBrushColor(colorCode)`  |
| Brush Eraser  | `mPhotoEditor.brushEraser()` |

**Note**: Whenever we set any property of a brush for drawing it will automatically enable the drawing mode



## Filter Effect
We can apply inbuild filter to the source images using 

 `mPhotoEditor.setFilterEffect(PhotoFilter.BRIGHTNESS);`

![](https://i.imgur.com/xXTGcVC.gif)

We can also apply custom effect using `Custom.Builder`

For more details check [Custom Filters](https://github.com/burhanrashid52/PhotoEditor/wiki/Filter-Effect)



## Text

![](https://i.imgur.com/491BmE8.gif)

We can add the text with inputText and colorCode like this

`mPhotoEditor.addText(inputText, colorCode);` 

It will take default fonts provided in the builder. If we want different fonts for different text we can set typeface with each text like this

`mPhotoEditor.addText(mTypeface,inputText, colorCode);`

In order to edit the text we need the view, which we will receive in our PhotoEditor callback. This callback will trigger when we **Long Press** the added text

 ```java
 mPhotoEditor.setOnPhotoEditorListener(new OnPhotoEditorListener() {
            @Override
            public void onEditTextChangeListener(View rootView, String text, int colorCode) {
                
            }
        });
  ```
Now we can edit the text with a view like this

`mPhotoEditor.editText(rootView, inputText, colorCode);`

If you want more customization on text. Please refer the wiki page for more details.


## Emoji

![](https://i.imgur.com/RP8kqz6.gif)

We can add the Emoji by `PhotoEditor.getEmojis(getActivity());` which will return a list of emojis unicode.

`mPhotoEditor.addEmoji(emojiUnicode);`

It will take default fonts provided in the builder. If we want different Emoji fonts for different emoji we can set typeface with each Emoji like this

`mPhotoEditor.addEmoji(mEmojiTypeface,emojiUnicode);`




## Adding Images/Stickers
 We need to provide a Bitmap to add our Images  `mPhotoEditor.addImage(bitmap);`
 
 
 

## Undo and Redo

![](https://i.imgur.com/1Y9WcCB.gif)

 ```java
   mPhotoEditor.undo();
   mPhotoEditor.redo();
 ```
 


## Deleting
  For deleting a Text/Emoji/Image we can click on the view to toggle the view highlighter box which will have a close icon. So, by clicking on the icon we can delete the view.
  
  
  

## Saving
   
   We need to provide a file with callback method when edited image is saved
   
   ```java
    mPhotoEditor.saveAsFile(filePath, new PhotoEditor.OnSaveListener() {
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
For more detail check [Saving](https://github.com/burhanrashid52/PhotoEditor/wiki/Saving)
    
## How to contribute?
* Check out contribution guidelines 👉[CONTRIBUTING.md](https://github.com/burhanrashid52/PhotoEditor/blob/master/CONTRIBUTING.md)


## What's next?
- Croping Image with Custom Aspect ratio and more customization text/emoji/stickers


## Questions?🤔
Hit me on twitter [![Twitter](https://img.shields.io/badge/Twitter-%40burhanrashid52-blue.svg)](https://twitter.com/burhanrashid52)
[![Medium](https://img.shields.io/badge/Medium-%40burhanrashid52-brightgreen.svg)](https://medium.com/@burhanrashid52)
[![Facebook](https://img.shields.io/badge/Facebook-Burhanuddin%20Rashid-blue.svg)](https://www.facebook.com/Bursid)



## Credits
This project is inspired from [PhotoEditorSDK](https://github.com/eventtus/photo-editor-android)

## Buy a cup of coffee
If you found this project helpful or you learned something from the source code and want to thank me, consider buying me a cup of ☕️
[PayPal](https://www.paypal.me/burhanrashid52)

## MIT License

Copyright (c) 2018 Burhanuddin Rashid

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
 
