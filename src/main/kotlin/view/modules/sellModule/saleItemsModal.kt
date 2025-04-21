package view.modules.sellModule

import androidx.compose.runtime.*
import components.button
import org.jetbrains.compose.web.dom.*
import repository.OrderItemsItem
import repository.moneyFormat
import repository.twoDigits

@Composable
fun saleItemsModal(
    orderId: String,
    ordersItems: List<OrderItemsItem>,
    itemsModalState: String,
    onCloseModal: () -> Unit,
) {
    val ordersItemsData by mutableStateOf(ordersItems)

    Div(attrs = { classes("scrolled", "medium-modal", itemsModalState) }) {

        Div(attrs = { classes("medium-modal-header") }) {
            H3(attrs = { classes("medium-modal-title") }) { Text("Itens de pedidos") }
        }

        Div(attrs = { classes("medium-modal-body") }) {

            P { Text("ID do pedido: $orderId") }
            Br()
            Table(attrs = {
                classes("display", "myTable")
            }) {
                Thead {
                    Tr {
                        Th { Text("Nome_producto") }
                        Th { Text("Quantidade") }
                        Th { Text("Sub Total") }
                        Th { Text("Lucro") }
                    }
                }
                Tbody {
                    ordersItemsData.forEach {
                        Tr {
                            Td { Text(it.productName.toString()) }
                            Td { Text(it.quantity.toString()) }
                            Td { Text(moneyFormat(it.subTotal)) }
                            Td { Text(moneyFormat(it.profit)) }
                        }
                    }
                }
            }
        }

        Div(attrs = { id("closeMediumModal") }) {
            button("closeButton", "Fechar") {
                onCloseModal()
            }
        }
    }
}