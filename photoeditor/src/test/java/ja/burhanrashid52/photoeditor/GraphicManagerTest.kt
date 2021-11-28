package ja.burhanrashid52.photoeditor

import android.content.Context
import android.view.View
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import android.view.ViewGroup
import org.junit.Assert
import org.junit.Test

/**
 * Created by Burhanuddin Rashid on 15/05/21.
 *
 * @author <https:></https:>//github.com/burhanrashid52>
 */
@RunWith(RobolectricTestRunner::class)
class GraphicManagerTest {
    private var mContext: Context = RuntimeEnvironment.systemContext
    @Test
    fun testGraphicMangerAddViews() {
        val view = View(mContext)
        view.id = 1
        val viewGroup: ViewGroup = object : ViewGroup(mContext) {
            override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {}
        }
        val graphicManager = GraphicManager(viewGroup, PhotoEditorViewState())
        val graphic: Graphic = object : Graphic(view, graphicManager) {
            public override fun getViewType(): ViewType {
                return ViewType.TEXT
            }

            public override fun getLayoutId(): Int {
                return 1
            }

            public override fun setupView(rootView: View) {}
        }
        graphicManager.addView(graphic)
        Assert.assertEquals(viewGroup.childCount.toLong(), 1)
        Assert.assertNotNull(viewGroup.findViewById(1))
    }
}