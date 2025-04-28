package components

import androidx.compose.runtime.*
import org.jetbrains.compose.web.attributes.ButtonType
import org.jetbrains.compose.web.dom.*

@Composable
fun Dialog(
    title: String,
    message: String,
    onDismiss: () -> Unit
) {
    Div(attrs = {
        style {
            property("position", "fixed")
            property("top", "0")
            property("left", "0")
            property("width", "100vw")
            property("height", "100vh")
            property("background-color", "rgba(0,0,0,0.5)")
            property("display", "flex")
            property("justify-content", "center")
            property("align-items", "center")
        }
    }) {
        Div(attrs = {
            style {
                property("background-color", "white")
                property("min-width", "200px")
                property("min-height", "150px")
                property("padding", "20px")
                property("border-radius", "10px")
                property("box-shadow", "0 4px 8px rgba(0,0,0,0.2)")
                property("text-align", "center")
            }
        }) {
            Text(title)
            Br()
            Text(message)
            Br()

            button("l", "OK", btnType = ButtonType.Button) {
                onDismiss()
            }
        }
    }
}
