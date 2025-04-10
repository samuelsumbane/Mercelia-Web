package components

import androidx.compose.runtime.*
import org.jetbrains.compose.web.attributes.ButtonType
import org.jetbrains.compose.web.attributes.type
import org.jetbrains.compose.web.dom.Button
import org.jetbrains.compose.web.dom.Text

// Button Widget
@Composable
fun button(
    btnClass: String,
    btnText: String,
    btnType: ButtonType = ButtonType.Button,
    onClick: () -> Unit = {}
) {
    Button(
        attrs = {
            type(btnType)
            classes(btnClass)
            onClick {
                onClick()
            }
        }
    ) {
        Text(btnText)
    }
}