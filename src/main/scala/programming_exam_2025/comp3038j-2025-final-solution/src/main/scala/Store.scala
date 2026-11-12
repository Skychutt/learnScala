import scala.collection.mutable

import customer.Customer
import inventory.{Item, Order}

object Store:
    private val customers: mutable.Map[String, Customer] = mutable.Map()
    private val orders: mutable.Map[String, Order] = mutable.Map()
    private val items: mutable.Map[String, Item] = mutable.Map()
    private val stock: mutable.Map[Item, Int] = mutable.Map()

    def addCustomer(c: Customer): Unit = customers.put(c.customerId, c)

    def addItem(item: Item): Unit = items.put(item.stockID, item)

    def addOrder(order: Order): Unit = orders.put(order.orderId, order)

    def updateStock(item: Item, quantity: Int): Unit = stock.put(item, quantity)

    def getCustomers: Map[String, Customer] = customers.toMap

    def getItems: Map[String, Item] = items.toMap

    def getOrders: List[Order] = orders.values.toList

    def getStock: Map[Item, Int] = stock.toMap
