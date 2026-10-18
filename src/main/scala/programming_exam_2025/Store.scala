package programming_exam_2025

import java.time.LocalDate

// 第二题 c：Store 是系统运行期间保存数据的单例，不负责读 CSV。
// 使用讲义第三章的 private、var、Map、contains、键查找和 + 添加键值对。
// Map 本身不可变；var 允许我们把更新后的新 Map 重新保存。
object Store {
  private var customers: Map[String, Customer] = Map.empty[String, Customer]
  private var items: Map[String, Item] = Map.empty[String, Item]
  private var stock: Map[String, Int] = Map.empty[String, Int]
  private var orders: Map[String, Order] = Map.empty[String, Order]

  // 清空数据，便于重复运行主程序。
  def clear(): Unit = {
    customers = Map.empty[String, Customer]
    items = Map.empty[String, Item]
    stock = Map.empty[String, Int]
    orders = Map.empty[String, Order]
  }

  def addCustomer(customer: Customer): Unit = {
    require(!customers.contains(customer.id), s"Duplicate customer ${customer.id}")
    customers = customers + (customer.id -> customer)
  }

  def addItem(item: Item): Unit = {
    require(!items.contains(item.id), s"Duplicate item ${item.id}")
    items = items + (item.id -> item)
    stock = stock + (item.id -> 0)
  }

  // 导入已有订单，只保存记录，不重新扣除库存。
  def addOrder(order: Order): Unit = {
    require(!orders.contains(order.id), s"Duplicate order ${order.id}")
    val customerId = order.customer.id
    require(customers.contains(customerId), "Unknown customer")
    require(customers(customerId) == order.customer, "Customer details do not match")
    for ((item, quantity) <- order.items) {
      require(items.contains(item.id), s"Unknown item ${item.id}")
      require(items(item.id) == item, "Item details do not match")
    }
    orders = orders + (order.id -> order)
  }

  // 设置最新库存，不是把 quantity 累加到原库存。
  def updateStock(itemId: String, quantity: Int): Unit = {
    require(items.contains(itemId), s"Unknown item $itemId")
    require(quantity >= 0, "Stock must be non-negative")
    stock = stock + (itemId -> quantity)
  }

  // 查询返回不可变 Map，外部不能通过返回值修改 Store 内部状态。
  def getCustomers: Map[String, Customer] = {
    customers
  }

  def getItems: Map[String, Item] = {
    items
  }

  def getOrders: Map[String, Order] = {
    orders
  }

  // 内部是“商品 ID → 数量”，这里转换成题目需要的“商品对象 → 数量”。
  def getStock: Map[Item, Int] = {
    var result = Map.empty[Item, Int]
    for ((itemId, item) <- items) {
      val quantity = stock(itemId)
      result = result + (item -> quantity)
    }
    result
  }

  // 以下是完整业务流程的补充；初学时先掌握上面的添加、更新、查询。
  def processOrder(orderId: String, on: LocalDate = LocalDate.now()): Order = {
    require(orders.contains(orderId), s"Unknown order $orderId")
    val order = orders(orderId)
    require(order.status == OrderStatus.New || order.status == OrderStatus.AwaitingStock,
      "Only new or awaiting-stock orders can be processed")
    require(order.items.nonEmpty, "Cannot process an empty order")

    var available = true
    for ((item, quantity) <- order.items) {
      require(item.suitableFor(order.customer, on), "Age restriction violated")
      if (stock(item.id) < quantity) {
        available = false
      }
    }

    val nextStatus = if (available) OrderStatus.Processed else OrderStatus.AwaitingStock
    val updatedOrder = order.updateStatus(nextStatus)
    if (available) {
      // 全部检查通过后才扣库存，避免缺货时只扣掉部分商品。
      var newStock = stock
      for ((item, quantity) <- order.items) {
        val remaining = newStock(item.id) - quantity
        newStock = newStock + (item.id -> remaining)
      }
      stock = newStock
    }
    orders = orders + (orderId -> updatedOrder)
    updatedOrder
  }

  def updateOrderStatus(orderId: String, status: OrderStatus): Order = {
    if (status == OrderStatus.Processed) {
      processOrder(orderId)
    } else {
      require(orders.contains(orderId), s"Unknown order $orderId")
      val order = orders(orderId)
      val updatedOrder = order.updateStatus(status)
      if (status == OrderStatus.Cancelled && order.status == OrderStatus.Processed) {
        var newStock = stock
        for ((item, quantity) <- order.items) {
          val returnedQuantity = Math.addExact(newStock(item.id), quantity)
          newStock = newStock + (item.id -> returnedQuantity)
        }
        stock = newStock
      }
      orders = orders + (orderId -> updatedOrder)
      updatedOrder
    }
  }
}
