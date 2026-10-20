package programming_exam_2025

import java.io.{ByteArrayOutputStream, PrintStream}
import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Paths}
import java.time.LocalDate
import scala.util.Try

// Run separately from Main: no testing dependency required.
object SolutionChecks:
  def main(args: Array[String]): Unit =
    var checks = 0
    def check(condition: Boolean, message: String): Unit =
      assert(condition, message)
      checks += 1
    def rejects(action: => Any): Boolean = Try(action).isFailure

    val directory = if args.nonEmpty then Paths.get(args(0)) else
      val local = Paths.get("previuos test/2025")
      if Files.isDirectory(local) then local
      else Paths.get("src/main/scala/programming_exam_2025/previuos test/2025")
    Store.clear()
    DataLoader.loadCustomers(directory.resolve("customers.csv").toString).foreach(Store.addCustomer)
    DataLoader.loadItems(directory.resolve("items.csv").toString).foreach(Store.addItem)
    DataLoader.loadStock(directory.resolve("stock.csv").toString, Store.getItems)
      .foreach { case (item, quantity) => Store.updateStock(item.id, quantity) }
    DataLoader.loadOrders(directory.resolve("orders.csv").toString, Store.getCustomers, Store.getItems)
      .foreach(Store.addOrder)
    check(Store.getCustomers.size == 50, "All customers loaded")
    check(Store.getItems.size == 50 && Store.getStock.size == 50, "All items/stock loaded")
    check(Store.getOrders.size == 15, "All orders loaded, including variable-width records")
    check(Store.getItems("BG102").rating == AgeRating.Restricted, "CSV AdultApproval alias")
    check(Store.getItems("VG009").rating == AgeRating.Adult, "CSV AdultOnly alias")
    check(Store.getCustomers("CUS001").phone.number == "00138000", "Phone leading zeroes preserved")
    check(Store.getOrders("ORD001").totalPrice(Currency.CNY).amount == BigDecimal("760.20545"),
      "Mixed-currency order uses full-precision rates")
    val inventory = Store.getStock.foldLeft(Money(BigDecimal(0), Currency.USD)) {
      case (total, (item, quantity)) => total + (item.price * quantity).in(Currency.USD)
    }
    check(inventory.amount == BigDecimal("228058.5018425"), "Inventory value matches independent calculation")
    check(Store.getStock.count { case (item, n) => n > 0 && item.rating == AgeRating.All } == 30,
      "Under-15 bonus expression")
    check(Store.getCustomers.values.count(c => Set("Ireland", "China")(c.address.country)) == 26,
      "Country bonus expression")

    check(Address.create(" ", "", "city", "", "country", "postcode").isLeft, "Empty address rejected")
    check(Address.create("street", "", "city", "", "country", "postcode").isRight, "Optional address fields")
    check(PhoneNumber.create("0086", "", "123456").isLeft, "Empty area code rejected")
    check(PhoneNumber.create("0086", "13A", "123456").isLeft, "Non-digit area rejected")
    check(PhoneNumber.create("0086", "138", "12345").isLeft, "Short phone rejected")
    check(PhoneNumber.create("0086", "138", "12345A").isLeft, "Non-digit phone rejected")
    val on = LocalDate.of(2026, 10, 5)
    check(!AgeRating.Restricted.allows(on.minusYears(15).plusDays(1), on), "Day before 15th birthday")
    check(AgeRating.Restricted.allows(on.minusYears(15), on), "15th birthday")
    check(!AgeRating.Adult.allows(on.minusYears(18), on), "Adult means age > 18")
    check(AgeRating.Adult.allows(on.minusYears(19), on), "19th birthday")
    check(!AgeRating.All.allows(on.plusDays(1), on), "Future birthday rejected")

    val eur = Money.fromParts(12, 34, Currency.EUR)
    check(eur.amount == BigDecimal("12.34"), "Units and centiunits")
    check((eur + Money(BigDecimal("0.66"), Currency.EUR)).amount == 13, "Money addition")
    check((eur - Money(BigDecimal("0.34"), Currency.EUR)).amount == 12, "Money subtraction")
    check(rejects(eur + Money(BigDecimal(1), Currency.USD)), "Cross-currency addition rejected")
    check(rejects(eur - Money(BigDecimal(1), Currency.USD)), "Cross-currency subtraction rejected")
    check(rejects(Money.fromParts(1, 100, Currency.EUR)), "Invalid centiunits rejected")
    val expected = Vector("1", "0.88181", "1.16206", "8.24901",
      "1.13298", "1", "1.31669", "9.34663",
      "0.86035", "0.75878", "1", "7.09860",
      "0.12118", "0.10688", "0.14085", "1")
    val currencies = Vector(Currency.EUR, Currency.GBP, Currency.USD, Currency.CNY)
    val actual = for from <- currencies; to <- currencies yield Money(BigDecimal(1), from).in(to).amount
    actual.zip(expected).foreach { case (value, rate) => check(value == BigDecimal(rate), "Exact exchange rate") }

    val original = Store.getOrders("ORD001")
    val item = Store.getItems("VG004")
    val added = original.addItem(item.copy(name = "Same ID"), 2)
    check(added.items.size == original.items.size && added.items(item) == 3, "Merge by item ID")
    check(original.items(item) == 1, "Original order remains unchanged")
    check(original.updateQuantity(item, 0).items.size == 2, "Zero quantity removes item")
    check(original.updateQuantity(item, 4).items(item) == 4, "Quantity update")
    check(original.removeItem(item).items.size == 2, "Item removal")
    check(rejects(original.addItem(item, 0)), "Zero addition rejected")
    check(rejects(original.updateQuantity(item, -1)), "Negative quantity rejected")
    check(rejects(original.updateQuantity("MISSING", 1)), "Unknown order line rejected")
    check(original.withTrackingId("TRACK123").trackingId.contains("TRACK123"), "Optional tracking ID")
    check(rejects(Store.getOrders("ORD009").updateStatus(OrderStatus.Cancelled)), "Delivered cannot cancel")
    check(rejects(Store.getOrders("ORD011").updateStatus(OrderStatus.Cancelled)), "Collected cannot cancel")
    check(rejects(Store.updateStock(item.id, -1)), "Negative stock rejected")
    check(rejects(Store.addCustomer(original.customer)), "Duplicate customer rejected")

    val snapshot = Store.getStock
    Store.updateStock(item.id, 0)
    check(Store.processOrder(original.id, on).status == OrderStatus.AwaitingStock, "Insufficient stock waits")
    check(Store.getStock.filterNot(_._1.id == item.id) == snapshot.filterNot(_._1.id == item.id),
      "Insufficient stock does not partially deduct")
    Store.updateStock(item.id, snapshot(item))
    check(Store.processOrder(original.id, on).status == OrderStatus.Processed, "Restock allows processing")
    check(Store.getStock(item) == snapshot(item) - 1, "Stock deducted once")
    check(snapshot(item) != Store.getStock(item), "Returned snapshot stays immutable")
    check(rejects(Store.processOrder(original.id, on)), "Cannot process twice")
    Store.updateOrderStatus(original.id, OrderStatus.Cancelled)
    check(Store.getStock == snapshot, "Cancellation returns processed stock")
    Store.updateOrderStatus(original.id, OrderStatus.Cancelled)
    check(Store.getStock == snapshot, "Repeated cancellation does not return stock twice")

    check("A,B,".split(",", -1).toList == List("A", "B", ""), "Trailing empty field preserved")
    val temp = Files.createTempDirectory("scala-exam-csv-checks-")
    val errors = new ByteArrayOutputStream
    val errorStream = new PrintStream(errors, true, "UTF-8")
    def fixture(name: String, contents: String) =
      Files.writeString(temp.resolve(name), contents, StandardCharsets.UTF_8)
    try
      Console.withOut(errorStream) {
        val stockFile = fixture("stock.csv", "Item ID,Quantity\nVG004,2\nUNKNOWN,3\nVG004,4\nVG007,-1\nVG014,abc\nVG020,5\n")
        check(DataLoader.loadStock(stockFile.toString, Store.getItems).map(_._2) == Vector(2, 4, -1, 5),
          "Bad stock rows skipped; later good rows retained")
        val itemsFile = fixture("items.csv", "Item ID,Name,Description,Rating,Currency,Units,Centiunits\n" +
          "I1,Notebook,Description,All,USD,1,50\nI2,Bad,Description,Invalid,USD,1,0\n" +
          "I3,Bad,Description,All,USD,1,100\nI4,Good,Description,Adult,EUR,2,0\n")
        check(DataLoader.loadItems(itemsFile.toString).map(_.id) == Vector("I1", "I4"), "Item error recovery")
        val customerLines = Files.readAllLines(directory.resolve("customers.csv"))
        val customerFile = fixture("customers.csv", customerLines.get(0) + "\n" +
          customerLines.get(1).replace("1985-04-12", "invalid-date") + "\n" +
          customerLines.get(1).replace("0086 138 00138000", "0086 BAD 123") + "\n" + customerLines.get(2))
        check(DataLoader.loadCustomers(customerFile.toString).map(_.id) == Vector("CUS002"), "Customer error recovery")
        val orderHeader = Files.readAllLines(directory.resolve("orders.csv")).get(0)
        val orderFile = fixture("orders.csv", orderHeader + "\n" +
          "O1,CUS004,New,2,VG004,1,VG004,2\nO2,CUS004,New,2,VG004,1\n" +
          "O3,UNKNOWN,New,1,VG004,1\nO4,CUS004,New,1,UNKNOWN,1\n" +
          "O5,CUS004,New,1,VG004,0\nO6,CUS004,New,1,VG004,1,EXTRA,1\n" +
          "O7,CUS004,New,1,VG004,1,,,,\n")
        val parsed = DataLoader.loadOrders(orderFile.toString, Store.getCustomers, Store.getItems)
        check(parsed.map(_.id) == Vector("O1", "O7") && parsed.head.items(item) == 2, "Order error recovery and direct Map storage")
        check(DataLoader.loadItems(temp.resolve("missing.csv").toString).isEmpty, "Missing file handled")
        check(DataLoader.loadItems(fixture("empty.csv", "").toString).isEmpty, "Empty file handled")
        check(DataLoader.loadItems(fixture("header.csv", "wrong,header\n").toString).isEmpty, "Wrong header handled")
      }
      check(errors.toString("UTF-8").contains("Read error: "), "Errors printed to screen")
    finally
      errorStream.close()
      val files = Files.list(temp)
      try files.forEach(path => { Files.delete(path); () }) finally files.close()
      Files.delete(temp)
    Store.clear()
    println(s"All $checks checks passed.")
