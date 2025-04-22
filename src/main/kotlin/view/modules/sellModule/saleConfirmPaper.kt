package view.modules.sellModule

import androidx.compose.runtime.Composable
import org.jetbrains.compose.web.css.DisplayStyle
import org.jetbrains.compose.web.css.FlexDirection
import org.jetbrains.compose.web.css.LineStyle
import org.jetbrains.compose.web.css.backgroundColor
import org.jetbrains.compose.web.css.border
import org.jetbrains.compose.web.css.borderRadius
import org.jetbrains.compose.web.css.display
import org.jetbrains.compose.web.css.flexDirection
import org.jetbrains.compose.web.css.fontWeight
import org.jetbrains.compose.web.css.height
import org.jetbrains.compose.web.css.paddingBottom
import org.jetbrains.compose.web.css.paddingLeft
import org.jetbrains.compose.web.css.percent
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.css.rgb
import org.jetbrains.compose.web.css.textAlign
import org.jetbrains.compose.web.css.width
import org.jetbrains.compose.web.dom.Br
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.H3
import org.jetbrains.compose.web.dom.H4
import org.jetbrains.compose.web.dom.H5
import org.jetbrains.compose.web.dom.P
import org.jetbrains.compose.web.dom.Table
import org.jetbrains.compose.web.dom.Tbody
import org.jetbrains.compose.web.dom.Text
import org.jetbrains.compose.web.dom.Th
import org.jetbrains.compose.web.dom.Thead
import org.jetbrains.compose.web.dom.Tr
import repository.SaleConfirmationItem
import repository.moneyFormat


@Composable
fun SaleConfirmPaper(
    saleData: List<SaleConfirmationItem>,
    user: String,
) {
    Div(attrs = { id("parentMainDiv") }) {
        Div(attrs = { id("titulo") }) {
            H3 { Text("Mercelia") }
            Br()
        }

        Div(attrs = {
            id("location")
            style {
                height(15.percent)
            }
        }) {
            P(attrs = { style { textAlign("center") } }) { Text("Chicumbane Bairro 4") }
            P(attrs = { style { textAlign("center") } }) { Text("Limpopo") }
        }
        Br()

        Div(attrs = {
            id("contact")
            style {
                property("text-align", "right")
                width(48.percent)
                height(15.percent)
                property("margin-left", "auto")
                border { style = LineStyle.Solid; color = rgb(0, 162, 255); width = 1.px }
//                borderRadius(topLeft = 22.px)
            }
        }) {
            P { Text("Moçambique \u00A0") }
            P { Text("Nuit: 103908922 \u00A0") }
            P { Text("Celular: 836777777 \u00A0") }
        }
        Br()

        Div(attrs = {
            id("sellTypeInfo")
            style { height(5.percent) }
        }) {
            P(attrs = { style { paddingLeft(10.px) } }) { Text("Venda a dinheiro") }
        }
        Br()

        Div(attrs = {
            id("userNameDate")
            style { height(15.percent) }
        }) {
            H4(attrs = {
                style {
                    textAlign("right")
                    fontWeight("bold")
                }
            }) {
                Text("Original \u00A0")
            }
            Br()
            P(attrs = { style { paddingLeft(10.px) } }) { Text("NIF:") }

            Div(attrs = {
                style {
                    display(DisplayStyle.Flex)
                    flexDirection(FlexDirection.Column)
                }
            }) {
                Div(attrs = { style { display(DisplayStyle.Flex) } }) {
                    P(attrs = { style { paddingLeft(10.px) } }) { Text("Funcionário: \u00A0") }
                    P(attrs = { style { fontWeight("bold") } }) { Text(user) }
                }

                Div(attrs = { style { display(DisplayStyle.Flex) } }) {
                    P(attrs = { style { paddingLeft(10.px) } }) { Text("V.O") }
                    P(attrs = { style { paddingLeft(60.px) } }) { Text("<?php echo \$lS[\"sellValue\"]; ?>") }
                }

                Div(attrs = { style { display(DisplayStyle.Flex) } }) {
                    P(attrs = { style { paddingLeft(10.px) } }) { Text("2025/04/22") }
                    P(attrs = { style { paddingLeft(60.px) } }) { Text("17:10") }
                }
            }
        }
        Br()

        Div(attrs = { id("pTabela") }) {
            Table(attrs = {
                style {
                    property("margin", "auto")
                    backgroundColor(rgb(221, 230, 230))
                    width(95.percent)
                    border { width = 1.px; style = LineStyle.Solid; color = rgb(0, 162, 255) }
                    property("clip-path", "polygon(calc(100% - 10px) 0, 100% 10px, 100% 100%, 10px 100%, 0% calc(100% - 10px), 0 0)")
                }
            }) {
                Thead {
                    Tr {
                        Th(attrs = { style { width(20.percent)  } }) { Text("Taxa. Iva") }
                        Th(attrs = { style { width(20.percent) } }) { Text("Quant.") }
                        Th(attrs = { style { width(20.percent) } }) { Text("Descrição") }
                        Th(attrs = { style { width(20.percent) } }) { Text("Total") }
                    }
                }
                Tbody(attrs = { id("sellInfoRecPT") }) {

                }
            }
        }
        Br()

        Div(attrs = { id("sTabela") }) {
            Table(attrs = {
                style {
                    property("margin", "auto")
                    backgroundColor(rgb(221, 230, 230))
                    width(95.percent)
                    border { width = 1.px; style = LineStyle.Solid; color = rgb(0, 162, 255) }
                    property("clip-path", "polygon(calc(100% - 10px) 0, 100% 10px, 100% 100%, 10px 100%, 0% calc(100% - 10px), 0 0)")
                }
            }) {
                Thead {
                    Tr {
                        Th(attrs = { style { width(20.percent)  } }) { Text("Taxa") }
                        Th(attrs = { style { width(20.percent)  } }) { Text("Base") }
                        Th(attrs = { style { width(20.percent)  } }) { Text("IVA") }
                        Th(attrs = { style { width(20.percent)  } }) { Text("Total") }
                    }
                }
                Tbody(attrs = { id("oInfoST") }) {
                    saleData.forEach { item ->
                        Tr {
                            Th(attrs = { style { width(20.percent)  } }) { Text(item.taxa.toString()) }
                            Th(attrs = { style { width(20.percent)  } }) { Text(moneyFormat(item.base)) }
                            Th(attrs = { style { width(20.percent)  } }) { Text(item.iva) }
                            Th(attrs = { style { width(20.percent)  } }) { Text(moneyFormat(item.Total)) }
                        }
                    }
                }
            }
        }

        Br()
        Br()

        Div(attrs = { id("fechamento") }) {
            H5(attrs = { style { textAlign("center") } }) { Text("Obrigado pela Preferência") }
            H5(attrs = {
                style {
                    textAlign("center")
                    paddingBottom(15.px)
                }
            }) {
                Text("Volte Sempre!")
            }
        }
    }

}