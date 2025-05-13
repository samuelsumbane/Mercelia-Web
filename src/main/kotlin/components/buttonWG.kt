package components

import androidx.compose.runtime.*
import kotlinx.html.H3
import org.jetbrains.compose.web.attributes.ButtonType
import org.jetbrains.compose.web.attributes.type
import org.jetbrains.compose.web.dom.Button
import org.jetbrains.compose.web.dom.H4
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Text

// Button Widget -------->>
@Composable
fun button(
    btnClass: String,
    btnText: String,
    btnType: ButtonType = ButtonType.Button,
    hoverText: String = "",
    btnChildElement: String = "Text",
    onClick: () -> Unit = {}
) {
    Button(
        attrs = {
            type(btnType)
            classes(btnClass, "tooltip")
            onClick { onClick() }
        }
    ) {
        if (btnChildElement == "Text") {
            Text(btnText)
        } else {
            H4 { Text(btnText) }
        }
        if (hoverText.isNotBlank()) {
            Span(attrs = { classes("tooltiptext") }) {
                Text(hoverText)
            }
        }
    }
}