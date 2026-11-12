package inventory

import customer.Customer
import money.*

case class Order(
    orderId: String,
    customer: Customer,
    status: OrderStatus,
    items: List[(Item, Int)],
    tracking: Option[String] = None
):

    def updateStatus(newStatus: OrderStatus): Order = copy(status = newStatus)

    def addToOrder(item: Item, quantity: Int): Order =
        if items.exists(_._1 == item) then
            copy(items = items.map((i, q) => if i == item then (i, q + quantity) else (i, q)))
        else copy(items = items :+ (item, quantity))

    def removeFromOrder(item: Item): Order = copy(items = items.filter(_._1 != item))

    def updateQuantity(item: Item, quantity: Int): Order =
        if quantity == 0 then removeFromOrder(item)
        else if items.exists(_._1 == item) then
            copy(items = items.map((i, q) => if i == item then (i, quantity) else (i, q)))
        else copy(items = items :+ (item, quantity))

    def calculatePrice(currency: Currency): Money =
        items.map((i, q) => CurrencyConverter.convert(i.price * q, currency)).foldLeft(Money(currency, 0, 0))(_ + _)
