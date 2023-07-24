package com.burhanrashid52.photoediting.base

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsIgnoringVisibility
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.launch

/**
 * Created by Burhanuddin Rashid on 19/07/23.
 * @author  <https://github.com/burhanrashid52>
 */

@ExperimentalMaterial3Api
@Composable
fun BaseBottomSheetDialog(
    skipPartiallyExpanded: Boolean = false,
    sheetContent: @Composable (close: () -> Unit) -> Unit,
    openContent: @Composable (toggle: () -> Unit) -> Unit,
) {
    val openBottomSheet = rememberSaveable { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = skipPartiallyExpanded,
    )

    val onToggle = {
        openBottomSheet.value = !openBottomSheet.value
    }
    val onClose = {
        scope.launch { bottomSheetState.hide() }.invokeOnCompletion {
            if (!bottomSheetState.isVisible) {
                openBottomSheet.value = false
            }
        }
    }
    openContent(onToggle)

    if (openBottomSheet.value) {
        ModalBottomSheet(
            onDismissRequest = { openBottomSheet.value = false },
            sheetState = bottomSheetState,
            containerColor = Color.Transparent,
            windowInsets = WindowInsets.navigationBars,
        ) {
            sheetContent(onClose)
        }
    }
}
