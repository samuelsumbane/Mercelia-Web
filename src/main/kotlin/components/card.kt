package components

import androidx.compose.runtime.Composable
import org.jetbrains.compose.web.css.bottom
import org.jetbrains.compose.web.css.padding
import org.jetbrains.compose.web.css.paddingBottom
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.dom.*

@Composable
fun cardWG( // Card Widget ---------->>
    title: String,
    warningClass: String = "empty",
    showSeparator: Boolean = false,
    cardButtons: @Composable () -> Unit,
    content: @Composable () -> Unit
) {
    Div(attrs = { classes("card", warningClass) }) {
        Div(attrs = { classes("card-header") }) {
            P { Text(title) }
        }
        if (showSeparator) { Hr() }
        Div(attrs = { classes("card-body") }) { content() }

        Div(attrs = {
            classes("card-footer")
            style { paddingBottom(18.px) }
        }) {
            cardButtons()
        }
    }
}