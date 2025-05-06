package components

import androidx.compose.runtime.Composable
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.H3
import org.jetbrains.compose.web.dom.Text



@Composable
fun minModal(
    modalState: String,
    title: String,
    content: @Composable () -> Unit
) {
    Div(attrs = { classes("min-modal", modalState) }) {
        Div(attrs = { classes("min-modal-header") }) {
            H3(attrs = { classes("min-modal-title") }) {
                Text(title)
            }
        }

        Div(attrs = { classes("min-modal-body") }) {
            content()
        }

        Div(attrs = { classes("min-modal-footer")})
    }
}