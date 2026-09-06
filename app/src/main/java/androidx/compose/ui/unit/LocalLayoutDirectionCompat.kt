package androidx.compose.ui.unit

/**
 * Compatibility bridge for the RTL composition local used by RedBox.
 * The actual CompositionLocal lives in androidx.compose.ui.platform.
 */
val LocalLayoutDirection: androidx.compose.runtime.ProvidableCompositionLocal<LayoutDirection>
    get() = androidx.compose.ui.platform.LocalLayoutDirection
