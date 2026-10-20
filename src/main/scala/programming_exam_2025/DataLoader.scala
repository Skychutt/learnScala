package programming_exam_2025

import java.time.LocalDate
import scala.io.Source

// 第二题 d：四种文件分别读取，正确记录返回，错误记录打印后跳过。
// 使用讲义第二章的 Source、while、try/catch/finally，第三章的 List、元组和 Map。
// List 的 :+ 便于按文件顺序追加；本题数据很少，大文件可改用讲义中的 ArrayBuffer。
object DataLoader {
  def loadStock(path: String, items: Map[String, Item]): List[(Item, Int)] = {
    // 用列表收集这一份文件中正确读取的记录。
    var results = List.empty[(Item, Int)]
    // null 表示文件还没打开；打开成功后 source 才保存数据源。
    var source: Source = null
    try {
      source = Source.fromFile(path, "UTF-8")
      val lines = source.getLines()
      val header = lines.next().split(",", -1).toList
      require(header == List("Item ID", "Quantity"), "Unexpected CSV header")
      while (lines.hasNext) {
        val line = lines.next()
        if (line.trim.nonEmpty) {
          // 每一行单独 try/catch：一行错误不影响后面的行。
          try {
            // -1 保留末尾空字段；取出字段时再用 trim 去掉两端空白。
            val fields = line.split(",", -1)
            require(fields.size == 2, "Stock record must have 2 fields")
            val itemId = fields(0).trim
            val quantity = fields(1).trim.toInt
            // 找不到编号时 Map 会抛异常，交给本行的 catch 处理。
            val item = items(itemId)
            results = results :+ (item, quantity)
          } catch {
            case error: Exception => println("Read error: " + error.getMessage)
          }
        }
      }
    } catch {
      // 外层处理文件不存在、读取失败或表头错误。
      case error: Exception => println("Read error: " + error.getMessage)
    } finally {
      // 只有打开成功才需要关闭，避免打开失败时调用 null.close()。
      if (source != null) {
        source.close()
      }
    }
    results
  }

  def loadItems(path: String): List[Item] = {
    // 用列表收集这一份文件中正确读取的记录。
    var results = List.empty[Item]
    // null 表示文件还没打开；打开成功后 source 才保存数据源。
    var source: Source = null
    try {
      source = Source.fromFile(path, "UTF-8")
      val lines = source.getLines()
      val header = lines.next().split(",", -1).toList
      require(header == List("Item ID", "Name", "Description", "Rating", "Currency", "Units", "Centiunits"), "Unexpected CSV header")
      while (lines.hasNext) {
        val line = lines.next()
        if (line.trim.nonEmpty) {
          // 每一行单独 try/catch：一行错误不影响后面的行。
          try {
            // -1 保留末尾空字段；取出字段时再用 trim 去掉两端空白。
            val fields = line.split(",", -1)
            require(fields.size == 7, "Item record must have 7 fields")
            val itemId = fields(0).trim
            val name = fields(1).trim
            val description = fields(2).trim
            val rating = AgeRating.parse(fields(3).trim)
            val currency = Currency.valueOf(fields(4).trim)
            val units = fields(5).trim.toInt
            val centiunits = fields(6).trim.toInt
            val price = Money.fromParts(units, centiunits, currency)
            val item = Item(itemId, name, description, price, rating)
            results = results :+ item
          } catch {
            case error: Exception => println("Read error: " + error.getMessage)
          }
        }
      }
    } catch {
      // 外层处理文件不存在、读取失败或表头错误。
      case error: Exception => println("Read error: " + error.getMessage)
    } finally {
      // 只有打开成功才需要关闭，避免打开失败时调用 null.close()。
      if (source != null) {
        source.close()
      }
    }
    results
  }

  def loadCustomers(path: String): List[Customer] = {
    // 用列表收集这一份文件中正确读取的记录。
    var results = List.empty[Customer]
    // null 表示文件还没打开；打开成功后 source 才保存数据源。
    var source: Source = null
    try {
      source = Source.fromFile(path, "UTF-8")
      val lines = source.getLines()
      val header = lines.next().split(",", -1).toList
      require(header == List("id", "name", "dob", "email", "phone", "currency", "street", "area", "city", "region", "country", "postcode"), "Unexpected CSV header")
      while (lines.hasNext) {
        val line = lines.next()
        if (line.trim.nonEmpty) {
          // 每一行单独 try/catch：一行错误不影响后面的行。
          try {
            // -1 保留末尾空字段；取出字段时再用 trim 去掉两端空白。
            val fields = line.split(",", -1)
            require(fields.size == 12, "Customer record must have 12 fields")
            val customerId = fields(0).trim
            val name = fields(1).trim
            val dob = LocalDate.parse(fields(2).trim)
            val email = fields(3).trim
            val currency = Currency.valueOf(fields(5).trim)
            
            // create 返回 Either：成功时取出对象，失败时抛给下面的 catch。
            val address = Address.create(fields(6).trim, fields(7).trim, fields(8).trim,
              fields(9).trim, fields(10).trim, fields(11).trim) match {
              case Right(value) => value
              case Left(message) => throw new IllegalArgumentException(message)
            }
            val phone = PhoneNumber.parse(fields(4).trim) match {
              case Right(value) => value
              case Left(message) => throw new IllegalArgumentException(message)
            }
            val customer = Customer(customerId, name, email, dob, phone, address, currency)
            results = results :+ customer
          } catch {
            case error: Exception => println("Read error: " + error.getMessage)
          }
        }
      }
    } catch {
      // 外层处理文件不存在、读取失败或表头错误。
      case error: Exception => println("Read error: " + error.getMessage)
    } finally {
      // 只有打开成功才需要关闭，避免打开失败时调用 null.close()。
      if (source != null) {
        source.close()
      }
    }
    results
  }

  def loadOrders(path: String, customers: Map[String, Customer], items: Map[String, Item]): List[Order] = {
    // 用列表收集这一份文件中正确读取的记录。
    var results = List.empty[Order]
    // null 表示文件还没打开；打开成功后 source 才保存数据源。
    var source: Source = null
    try {
      source = Source.fromFile(path, "UTF-8")
      val lines = source.getLines()
      val header = lines.next().split(",", -1).toList
      require(header == List("Order ID", "Customer ID", "Status", "Num Items", "Item Id", "Quantity", "Item Id", "Quantity", "Item Id", "Quantity", "Item Id", "Quantity", "Item Id", "Quantity"), "Unexpected CSV header")
      while (lines.hasNext) {
        val line = lines.next()
        if (line.trim.nonEmpty) {
          // 每一行单独 try/catch：一行错误不影响后面的行。
          try {
            // -1 保留末尾空字段；取出字段时再用 trim 去掉两端空白。
            val fields = line.split(",", -1)
            val orderId = fields(0).trim
            val customerId = fields(1).trim
            val status = OrderStatus.valueOf(fields(2).trim)
            val count = fields(3).trim.toInt
            require(count >= 0, "Number of order lines must be non-negative")
            // 前四列是订单资料，后面每件商品占两列：商品 ID、数量。
            val expected = 4 + 2 * count
            require(fields.size >= expected, "Missing item/quantity pairs")
            // 允许末尾空列，但不允许声明数量之外还有商品数据。
            var extraIndex = expected
            while (extraIndex < fields.size) {
              require(fields(extraIndex).trim.isEmpty, "Unexpected data after declared item pairs")
              extraIndex += 1
            }
            // 不再重复检查 contains，找不到客户时直接由 catch 处理。
            val customer = customers(customerId)
            var orderItems = Map.empty[Item, Int]
            var index = 0
            while (index < count) {
              val itemId = fields(4 + 2 * index).trim
              val quantity = fields(5 + 2 * index).trim.toInt
              // 找不到编号时 Map 会抛异常，交给本行的 catch 处理。
              val item = items(itemId)
              // 直接保存本行读到的商品和数量，假设每组商品编号不重复。
              orderItems = orderItems + (item -> quantity)
              index += 1
            }
            val order = Order(orderId, customer, status, None, orderItems)
            results = results :+ order
          } catch {
            case error: Exception => println("Read error: " + error.getMessage)
          }
        }
      }
    } catch {
      // 外层处理文件不存在、读取失败或表头错误。
      case error: Exception => println("Read error: " + error.getMessage)
    } finally {
      // 只有打开成功才需要关闭，避免打开失败时调用 null.close()。
      if (source != null) {
        source.close()
      }
    }
    results
  }

}
