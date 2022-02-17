package ja.burhanrashid52.photoeditor

import android.content.Context
import android.view.View
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import android.view.ViewGroup
import androidx.test.core.app.ApplicationProvider
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import org.junit.Assert
import org.junit.Test

/**
 * Created by Burhanuddin Rashid on 15/05/21.
 *
 * @author <https:></https:>//github.com/burhanrashid52>
 */
@RunWith(RobolectricTestRunner::class)
class GraphicManagerTest {

    private var mContext = ApplicationProvider.getApplicationContext<Context>()

//    @Test
//    fun testGraphicMangerAddViews() {
//        val id = R.layout.view_photo_editor_text
//        val childId = R.id.frmBorder
//        val viewGroup: ViewGroup = object : ViewGroup(mContext) {
//            override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {}
//        }
//        val graphicManager = GraphicManager(viewGroup, PhotoEditorViewState())
//        val graphic: Graphic = object : Graphic(
//            context = mContext,
//            layoutId = id,
//            viewType = ViewType.TEXT,
//            graphicManager = graphicManager
//        ) {
//
//        }
//        graphicManager.addView(graphic)
//        assertEquals(viewGroup.childCount.toLong(), 1)
//        assertNotNull(viewGroup.findViewById(childId))
//    }
}