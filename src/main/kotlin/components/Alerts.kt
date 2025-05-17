package components

import androidx.compose.runtime.Composable
import kotlinx.browser.window

@JsName("showAlert")
external fun showAlert(icon: String, title: String, text: String)

@JsName("showOkayAlert")
external fun showOkayAlert(icon: String, title: String, text: String, onOkay: () -> Unit)

fun alert(icon: String, title: String, text: String) {
    window.setTimeout({
        showAlert(icon, title, text)
    }, 50)
}

fun onOkayAlert(icon: String, title: String, text: String, onOkay: () -> Unit) {
    window.setTimeout({
        showOkayAlert(icon, title, text, onOkay)
    }, 50)
}


@JsName("showAlertDelete")
external fun showAlertDelete(title: String, text: String, onDelete: () -> Unit)

fun alertDelete(title: String, text: String, onDelete: () -> Unit) {
    window.setTimeout({
        showAlertDelete(title, text, onDelete)
    }, 50)
}

@JsName("showAlertTimer")
external fun showAlertTimer(title: String)

fun alertTimer(title: String) {
    window.setTimeout({
        showAlertTimer(title)
    }, 10)
}


fun recordsNotFound() {
    alert(
        "info",
        "Registros não encontrados",
        "Nenhum registro encontrado com datas selecionadas"
    )
}


fun todayRecordsNotFound() {
    alert(
    "info",
    "Registros não encontrados",
    "Nenhum registro feito hoje foi encontrado."
    )
}