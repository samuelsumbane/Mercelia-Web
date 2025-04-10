package components

import androidx.compose.runtime.Composable
import org.jetbrains.compose.web.dom.*

@Composable
fun cardWG( // Card Widget ---------->>
    title: String,
    cardButtons: @Composable () -> Unit,
    content: @Composable () -> Unit
) {
    Div(attrs = { classes("card") }) {
        Div(attrs = { classes("card-header") }) {
            P { Text(title) }
        }

        Div(attrs = { classes("card-body") }) {
            content()
        }

        Div(attrs = { classes("card-footer") }) {
            cardButtons()
        }
    }
}