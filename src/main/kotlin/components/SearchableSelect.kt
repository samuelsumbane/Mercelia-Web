//package components
//
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.MutableState
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.setValue
//import org.jetbrains.compose.web.attributes.InputType
//import org.jetbrains.compose.web.attributes.placeholder
//import org.jetbrains.compose.web.css.Color
//import org.jetbrains.compose.web.css.LineStyle
//import org.jetbrains.compose.web.css.Position
//import org.jetbrains.compose.web.css.backgroundColor
//import org.jetbrains.compose.web.css.border
//import org.jetbrains.compose.web.css.cursor
//import org.jetbrains.compose.web.css.left
//import org.jetbrains.compose.web.css.marginBottom
//import org.jetbrains.compose.web.css.padding
//import org.jetbrains.compose.web.css.percent
//import org.jetbrains.compose.web.css.position
//import org.jetbrains.compose.web.css.px
//import org.jetbrains.compose.web.css.selectors.CSSSelector.PseudoClass.hover
//import org.jetbrains.compose.web.css.top
//import org.jetbrains.compose.web.css.width
//import org.jetbrains.compose.web.dom.Button
//import org.jetbrains.compose.web.dom.Div
//import org.jetbrains.compose.web.dom.Input
//import org.jetbrains.compose.web.dom.Li
//import org.jetbrains.compose.web.dom.Text
//import org.jetbrains.compose.web.dom.Ul
//import view.modules.sellModule.proItem
//
//@Composable
//fun SearchableSelect(
////    options: MutableList<Map<String, String>>,
//    options: MutableList<proItem>,
//    onOptionSelected: (String) -> Unit
//) {
//    var expanded by remember { mutableStateOf(false) }
//    var query by remember { mutableStateOf("") }
//
//    val filteredOptions = options.filter { it.proName.contains(query, ignoreCase = true) }
//
//    Div(attrs = {
//        style {
//            position(Position.Relative)
//            width(200.px)
//        }
//    }) {
//        Button(attrs = {
//            onClick { expanded = !expanded }
//            classes("formTextInput")
//            style {
//                width(100.percent)
//            }
//        }) {
//            Text("Selecionar opção")
//        }
//
//        if (expanded) {
//            Div(attrs = {
//                classes("expandedSelect")
//            }) {
//                Input(type = InputType.Text, attrs = {
//                    classes("formTextInput")
//                    placeholder("Pesquisar...")
//                    value(query)
//                    onInput { event -> query = event.value }
////                    style {
////                        width(100.percent)
////                        marginBottom(8.px)
////                    }
//                })
//
//
//                Ul(attrs = { classes("scroled") }) {
//                    filteredOptions.forEach { option ->
//                        Li(attrs = {
//                            onClick {
//                                onOptionSelected(option.proId)
//                                expanded = false
//                                query = ""
//                            }
//                            style {
//                                cursor("pointer")
//                                padding(4.px)
////                                hover {
////                                    backgroundColor(Color.lightgray)
////                                }
//                            }
//                        }) {
//                            Text(option.proName)
//                        }
//                    }
//
//                    if (filteredOptions.isEmpty()) {
//                        Li {
//                            Text("Nenhum resultado")
//                        }
//                    }
//                }
//            }
//        }
//    }
//}
