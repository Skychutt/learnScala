package programming_exam_2025

object Main {
  def main(args: Array[String]): Unit = {
    // ============================================================
    // 第二题 e：Main Program（必做，10%）
    // 要求：获取 Store 和 DataLoader，读取四个 CSV，并把数据加入 Store。
    // ============================================================
    // 第二题 e：先获取两个单例，再按依赖顺序读取并保存数据。
    val store = Store
    val loader = DataLoader
    // 和讲义一样，直接使用字符串路径。默认从 learnScala 项目根目录运行。
    // 也可以把四个 CSV 所在目录作为第一个程序参数传入。
    val directory = if (args.length > 0) {
      args(0)
    } else {
      "src/main/scala/programming_exam_2025/previuos test/2025"
    }
    store.clear()

    val customers = loader.loadCustomers(directory + "/customers.csv")
    for (customer <- customers) {
      store.addCustomer(customer)
    }

    val items = loader.loadItems(directory + "/items.csv")
    for (item <- items) {
      store.addItem(item)
    }

    val stock = loader.loadStock(directory + "/stock.csv", store.getItems)
    for ((item, quantity) <- stock) {
      store.updateStock(item.id, quantity)
    }

    val orders = loader.loadOrders(directory + "/orders.csv", store.getCustomers, store.getItems)
    for (order <- orders) {
      store.addOrder(order)
    }

    // ==================== 第二题 e 必做部分结束 ====================

    // 辅助输出：检查加载数量，不是第二题 e 明确要求的内容。
    println(s"Loaded: ${store.getCustomers.size} customers, ${store.getItems.size} items, " +
      s"${store.getStock.size} stock entries, ${store.getOrders.size} orders")

    // ============================================================
    // Bonus：附加题（可选，合计最多加 15%）
    // 以下代码用于完成七项附加任务，不属于第二题 e 的必做部分。
    // ============================================================

    // 附加题共用的准备代码：取得编号列表和库存，方便后续查询。
    // sorted 是标准库的排序方法，这里只对字符串 ID 排序，便于每次按相同顺序输出。
    val itemIds = store.getItems.keys.toList.sorted
    val customerIds = store.getCustomers.keys.toList.sorted
    val orderIds = store.getOrders.keys.toList.sorted
    val stockByItem = store.getStock

    // Bonus 1：找出有库存、适合 15 岁以下客户的商品。
    // Write an expression to create a list containing all of the items that have at least one item
    // in stock and are suitable for a customer under the age of 15.
    var under15Items = List.empty[Item]
    var under15Ids = List.empty[String]
    for (itemId <- itemIds) {
      val item = store.getItems(itemId)
      if (stockByItem(item) > 0 && item.rating == AgeRating.All) {
        under15Items = under15Items :+ item
        under15Ids = under15Ids :+ item.id
      }
    }
    println(s"In-stock items suitable for under-15s (${under15Items.size}): " + under15Ids.mkString(", "))

    // Bonus 2：打印所有订单的编号和人民币总价。
    // Write an expression to print the id and price (in CNY) of all of the orders currently in the system.
    for (orderId <- orderIds) {
      val order = store.getOrders(orderId)
      val price = order.totalPrice(Currency.CNY)
      println(s"${order.id}: $price")
    }

    // Bonus 3：找出住在爱尔兰或中国的客户。
    // Write an expression to create a list containing all of the customers that live in either Ireland or China.
    var irelandOrChina = List.empty[Customer]
    var selectedCustomerIds = List.empty[String]
    for (customerId <- customerIds) {
      val customer = store.getCustomers(customerId)
      val country = customer.address.country
      if (country == "Ireland" || country == "China") {
        irelandOrChina = irelandOrChina :+ customer
        selectedCustomerIds = selectedCustomerIds :+ customer.id
      }
    }
    println(s"Customers in Ireland or China (${irelandOrChina.size}): " + selectedCustomerIds.mkString(", "))

    // Bonus 4：打印等待补货订单的编号、客户姓名和国家。
    // Write an expression to print the order id, name of the customer, and country of the
    // customer for all orders that have the status AwaitingStock.
    println("AwaitingStock orders (ID, customer name, country):")
    for (orderId <- orderIds) {
      val order = store.getOrders(orderId)
      if (order.status == OrderStatus.AwaitingStock) {
        println(s"${order.id}, ${order.customer.name}, ${order.customer.address.country}")
      }
    }

    // Bonus 5：打印库存最多的五件商品的名称、价格和数量。
    // Write an expression to print the name and price and quantity of the 5 items in the store
    // that have the highest amount of stock.
    println("Top 5 items by stock (name, price, quantity):")
    var remaining = stockByItem
    var printed = 0
    // 每轮找出剩余商品中库存最多的一件，打印后移除，最多重复五次。
    while (printed < 5 && remaining.nonEmpty) {
      var highest = -1
      var bestId = ""
      for (itemId <- itemIds) {
        val item = store.getItems(itemId)
        if (remaining.contains(item) && remaining(item) > highest) {
          highest = remaining(item)
          bestId = itemId
        }
      }
      val bestItem = store.getItems(bestId)
      println(s"${bestItem.name}, ${bestItem.price}, $highest")
      remaining = remaining - bestItem
      printed += 1
    }

    // Bonus 6：计算全部库存的美元总价值。
    // Write an expression to print the total value of all of the items currently in stock in USD.
    var inventoryValue = Money(BigDecimal(0), Currency.USD)
    for ((item, quantity) <- stockByItem) {
      val value = (item.price * quantity).in(Currency.USD)
      inventoryValue = inventoryValue + value
    }
    println(s"Total inventory value: $inventoryValue")

    // Bonus 7：计算所有等待补货订单的英镑总金额。
    // Write an expression to print the total value in GBP for all orders that have the status AwaitingStock.
    var awaitingStockValue = Money(BigDecimal(0), Currency.GBP)
    for (orderId <- orderIds) {
      val order = store.getOrders(orderId)
      if (order.status == OrderStatus.AwaitingStock) {
        awaitingStockValue = awaitingStockValue + order.totalPrice(Currency.GBP)
      }
    }
    println(s"AwaitingStock total: $awaitingStockValue")
    // ==================== Bonus 附加题部分结束 ====================
  }
}
