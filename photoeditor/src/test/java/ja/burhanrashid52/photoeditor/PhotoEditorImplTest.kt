package ja.burhanrashid52.photoeditor

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class PhotoEditorImplTest {

    private val context = ApplicationProvider.getApplicationContext<Context>()

    @Test
    fun addTextUsesDefaultStyleAndCenteredPosition() {
        val photoEditorView = PhotoEditorView(context)
        val photoEditor = PhotoEditor.Builder(context, photoEditorView).build()

        photoEditor.addText(text = "Default text")

        val textView = textView(photoEditorView)
        val params = textRoot(textView).layoutParams as RelativeLayout.LayoutParams
        assertEquals("Default text", textView.text)
        assertEquals(RelativeLayout.TRUE, params.getRule(RelativeLayout.CENTER_IN_PARENT))
    }

    @Test
    fun addTextUsesProvidedStyleBuilderUnchanged() {
        val photoEditorView = PhotoEditorView(context)
        val photoEditor = PhotoEditor.Builder(context, photoEditorView).build()
        val styleBuilder = TextStyleBuilder().apply {
            withTextColor(Color.MAGENTA)
            withTextFont(Typeface.SERIF)
        }

        photoEditor.addText(
            text = "Styled text",
            styleBuilder = styleBuilder,
            textTypeface = Typeface.DEFAULT_BOLD,
            colorCodeTextView = Color.BLUE
        )

        val textView = textView(photoEditorView)
        assertEquals(Color.MAGENTA, textView.currentTextColor)
        assertEquals(Typeface.SERIF, textView.typeface)
    }

    @Test
    fun addTextBuildsStyleAndUsesExplicitPosition() {
        val photoEditorView = PhotoEditorView(context)
        val photoEditor = PhotoEditor.Builder(context, photoEditorView).build()

        photoEditor.addText(
            text = "Positioned text",
            position = Position(x = 42, y = 84),
            textTypeface = Typeface.DEFAULT_BOLD,
            colorCodeTextView = Color.BLUE
        )

        val textView = textView(photoEditorView)
        val params = textRoot(textView).layoutParams as RelativeLayout.LayoutParams
        assertEquals(Color.BLUE, textView.currentTextColor)
        assertEquals(Typeface.DEFAULT_BOLD, textView.typeface)
        assertEquals(42, params.leftMargin)
        assertEquals(84, params.topMargin)
        assertFalse(params.getRule(RelativeLayout.CENTER_IN_PARENT) == RelativeLayout.TRUE)
    }

    private fun textView(photoEditorView: PhotoEditorView): TextView {
        return photoEditorView.findViewById(R.id.tvPhotoEditorText)
    }

    private fun textRoot(textView: TextView): View {
        return (textView.parent as View).parent as View
    }
}
