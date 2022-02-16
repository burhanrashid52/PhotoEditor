package ja.burhanrashid52.photoeditor;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by Burhanuddin Rashid on 15/05/21.
 *
 * @author <https://github.com/burhanrashid52>
 */
@RunWith(RobolectricTestRunner.class)
public class GraphicManagerTest {

    protected Context mContext = RuntimeEnvironment.systemContext;

    @Test
    public void testGraphicMangerAddViews() {
        View view = new View(mContext);
        view.setId(1);

        ViewGroup viewGroup = new ViewGroup(mContext) {
            @Override
            protected void onLayout(boolean changed, int l, int t, int r, int b) {

            }
        };
        GraphicManager graphicManager = new GraphicManager(viewGroup, new PhotoEditorViewState());
        Graphic graphic = new Graphic(view, graphicManager) {

            @Override
            ViewType getViewType() {
                return ViewType.TEXT;
            }

            @Override
            int getLayoutId() {
                return 1;
            }

            @Override
            void setupView(View rootView) {

            }
        };

        graphicManager.addView(graphic);
        assertEquals(viewGroup.getChildCount(), 1);
        assertNotNull(viewGroup.findViewById(1));
    }
}
