package repository

import kotlinx.serialization.Serializable


@Serializable
data class CategoryItem(
    val id: Int?,
    val name: String
)

@Serializable
data class ProductItem(
    val id: Int?,
    val name: String,
    val cost: Double,
    val price: Double,
    val stock: Int,
    val minStock: Int?,
    val categoryId: Int,
    val categoryName: String?,
    val barcode: String,
)

@Serializable
data class ChangeProductPriceDraft(
    val productId: Int,
    val newPrice: Double
)

@Serializable
data class IncreaseProductStockDraft(
    val productId: Int,
    val cost: Double,
    val price: Double,
    val newStock: Int,
    val reason: String,
    val userId: Int,
)

@Serializable
data class ClientItem(
    val id: Int?,
    val name: String,
    val telephone: String
)

//@Serializable
//data class OrderItemDraft(
//    val id: String,
//    val clientId: Int?,
//    val total: Double,
//    val status: String
//)

@Serializable
data class OrderItem(
    val id: String?,
    val clientId: Int?,
    val clientName: String?,
    val total: Double,
    val orderDateTime: String?,
    val status: String,
    val userId: Int,
    val userName: String,
    val branchName: String,
)

@Serializable
data class OrderItemsItem(
    val id: String?,
    val orderId: String?,
    val productId: Int,
    val productName: String?,
    val quantity: Int,
    val subTotal: Double,
    val profit: Double,
)

@Serializable
data class UserItem(
    val id: Int,
    val name: String,
    val email: String,
    val passwordHash: String,
    val role: String,
    val status: String,
    val lastLogin: String,
)

@Serializable
data class UserItemDraft(
    val name: String,
    val email: String,
    val role: String,
)

//@Serializable
//data class OrderItemDraft(
//    val clientId: Int?,
//    val total: Double,
//    val status: String,
//    val userId: Int,
//)

@Serializable
data class OrderItemDraft(
    val clientId: Int?,
    val total: Double,
    val status: String,
    val reason: String,
    val userId: Int,
    val branchId: Int,
)

@Serializable
data class OrderItemsItemDraft(
    val productId: Int,
    val quantity: Int,
    val costPrice: Double,
    val sellPrice: Double,
    val subTotal: Double,
    val profit: Double,
)


@Serializable
data class SaleItem(
    val order: OrderItemDraft,
    val o_items: List<OrderItemsItemDraft>
)


@Serializable
data class SupplierItem(
    val id: Int?,
    val name: String,
    val contact: String,
    val address: String
)

@Serializable
data class StockItem(
    val productName: String,
    val type: String,
    val quantity: Int,
    val beforeQty: Int,
    val afterQty: Int,
    val cost: Double,
    val price: Double,
    val reason: String,
    val datetime: String,
    val userId: Int,
    val userName: String,
    val branchName: String
)

@Serializable
data class ProductNameAndCategory(
    val productId: Int,
    val productName: String,
    val categoryId: Int,
    val barcode: String,
)

@Serializable
data class LoginRequest(val email: String, val password: String)


@Serializable
data class SellTableItem(
    val id: Int,
    val name: String,
    val quantity: Int,
    val productCost: Double?,
    val productPrice: Double,
    val subTotal: Double,
    val availableProQuantity: Int?
)


@Serializable
data class SaleReportItem(
    val productName: String,
    val quantity: Int,
    val subTotal: Double,
    val profit: Double,
    val status: String,
    val userId: Int,
    val userName: String,
    val datetime: String?,
)

//
@Serializable
data class ActivityItemGrupped(
    val id: Int?,
    val action: String,
    val productName: String,
    var quantity: Int,
    var totalPaid: Double,
    var afiliateName: String,
    val dateTime: String
)

//@Serializable
//data class SalesControlDraft(
//    val sProductid: Int,
//    val sProductName: String,
//    val sQuantity: Int,
//    val sProductPurchace: Double,
//    val sProductPrice: Double,
//    val sProfit: Double,
//    val sAmountPaid: Double,
//    val sPaymentStatus: String,
//    val sDiscont: Double,
//    val sUpLinkCommission: Double,
////    val sRemainingAmount: Double,
//    val sAfiliateId: Int,
//)
//
//@Serializable
//data class SalesControlItem(
//    val sId: Int,
//    val sDraft: SalesControlDraft,
//    val sDateTime: String
//)
//
//
//
@Serializable
data class SettingDraft(
    val id: Int?,
    val restantProductsLowerLimit: Int,
)

@Serializable
data class SysConfigDraft(
    val id: Int,
    val key: String,
    val value: String
)


@Serializable
data class SysConfigItem(
    val key: String,
    val value: String,
    val lastUpdate: String,
    val id: Int,
)

@Serializable
data class BranchItem(
    val id: Int,
    val name: String,
    val address: String,
)

data class SaleConfirmationItem(
    val taxa: Int,
    val base: Double,
    val iva: String,
    val Total: Double,
)

val emptyConfigItem =
    SysConfigItem("", "", "", 0)

//@Serializable
//data class PasswordDraft(
//    val uId: Int,
//    val currentPassword: String,
//    val newPassword: String
//)

//
//@Serializable
//data class UserSession(
//    val data: UserItem
//)



@Serializable
data class LoggedUserDC(
    val isLogged: Boolean,
    val userId: Int,
    val userName: String,
    val userRole: String,
)

data class UserDataAndSys(
    val userId: Int,
    val userName: String,
    val userRole: String,
    val sysPackage: String,
)



@Serializable
data class ChangeStatusDC(
    val status: Int,
    val userId: Int,
)

@Serializable
data class ChangeRoleDC(
    val role: String,
    val userId: Int,
)

@Serializable
data class PasswordDraft(
    val userId: Int,
    val hashedPassword: String,
    val newPassword: String
)

@Serializable
data class VerifyPasswordDC(
    val actualPassword: String,
    val hashedPassword: String,
)

val emptyLoggedUser = LoggedUserDC(
    false, 0, "", ""
)

val emptyUserItem = UserItem (
    0, "", "", "", "", "", ""
)


// Finance

@Serializable
data class PayableDraft(
    val fornecedor: String,
    val description: String,
    val value: Double,
    val expiration_date: String,
    val payment_method: String,
)

@Serializable
data class PayableItem(
    val id: String,
    val fornecedor: String,
    val description: String,
    val value: Double,
    val expiration_date: String,
    val payment_date: String,
    val payment_method: String,
    val status: String,
)

@Serializable
data class ReceivableDraft(
    val client: String,
    val description: String,
    val value: Double,
    val expiration_date: String,
    val received_method: String,
)

@Serializable
data class ReceivableItem(
    val id: String,
    val client: String,
    val description: String,
    val value: Double,
    val expiration_data: String,
    val received_data: String,
    val received_method: String,
    val status: String,
)

@Serializable
data class IdAndStatus(
    val id: Int,
    val status: Int,
)