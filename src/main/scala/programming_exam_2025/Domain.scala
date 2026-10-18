package programming_exam_2025

import java.time.LocalDate
import java.time.Period

// 阅读顺序：Currency → Address → PhoneNumber → Customer → Money → AgeRating → Item → Order。
// case class 保存数据；object 提供创建或转换方法；enum 表示固定的几种选项。
// 标注“第二题”的方法可以先跳过，第一题先看字段和 require 验证。
// 使用普通循环和 Map + 键值对；方法中的局部 var 不会修改原来的领域对象。

// ==================== 1. 货币种类 ====================
enum Currency {
  case EUR, GBP, USD, CNY
}

// ==================== 2. 地址 ====================
case class Address private (
    street: String,    // 街道
    area: String,      // 区域：题目不要求非空
    city: String,      // 城市
    region: String,    // 地区：题目不要求非空
    country: String,   // 国家
    postcode: String   // 邮编用字符串，保留字母、空格和前导零
) {
  // trim 去掉两端空白；nonEmpty 表示不是空字符串。
  // 类内验证保证通过 copy 等方式创建的对象也必须合法。
  require(street.trim.nonEmpty, "Street must not be empty")
  require(city.trim.nonEmpty, "City must not be empty")
  require(country.trim.nonEmpty, "Country must not be empty")
  require(postcode.trim.nonEmpty, "Postcode must not be empty")
}

object Address {
  // Left 是错误信息；Right 是创建成功的地址。
  def create(
      street: String,
      area: String,
      city: String,
      region: String,
      country: String,
      postcode: String
  ): Either[String, Address] = {
    if (street.trim.isEmpty) {
      Left("Street must not be empty")
    } else if (city.trim.isEmpty) {
      Left("City must not be empty")
    } else if (country.trim.isEmpty) {
      Left("Country must not be empty")
    } else if (postcode.trim.isEmpty) {
      Left("Postcode must not be empty")
    } else {
      Right(new Address(
        street.trim, area.trim, city.trim,
        region.trim, country.trim, postcode.trim
      ))
    }
  }
}

// ==================== 3. 电话号码 ====================
case class PhoneNumber private (
    countryCode: String,  // 例如 "0086"
    areaCode: String,     // 例如 "138"
    number: String        // 例如 "00138000"
) {
  // [0-9]+ 表示一个或多个数字；[0-9]{6,} 表示至少六个数字。
  // 国家代码纯数字是额外检查；题目明确要求的是区号和号码检查。
  require(countryCode.matches("[0-9]+"), "Country code must contain only digits")
  require(areaCode.matches("[0-9]+"), "Area code must be non-empty and contain only digits")
  require(number.matches("[0-9]{6,}"), "Phone number must contain at least six digits")

  override def toString: String = {
    s"$countryCode $areaCode $number"
  }
}

object PhoneNumber {
  def create(
      countryCode: String,
      areaCode: String,
      number: String
  ): Either[String, PhoneNumber] = {
    if (!countryCode.matches("[0-9]+")) {
      Left("Country code must contain only digits")
    } else if (!areaCode.matches("[0-9]+")) {
      Left("Area code must be non-empty and contain only digits")
    } else if (!number.matches("[0-9]{6,}")) {
      Left("Phone number must contain at least six digits")
    } else {
      Right(new PhoneNumber(countryCode, areaCode, number))
    }
  }

  // CSV 加载辅助方法：把 "0086 138 00138000" 拆成三个字段。
  def parse(value: String): Either[String, PhoneNumber] = {
    val parts = value.trim.split("\\s+") // 按一个或多个空白字符分割
    if (parts.length != 3) {
      Left("Phone must have country code, area code and number separated by spaces")
    } else {
      create(parts(0), parts(1), parts(2))
    }
  }
}

// ==================== 4. 客户 ====================
case class Customer(
    id: String,
    name: String,
    email: String,
    dob: LocalDate,       // 出生日期，dob = date of birth
    phone: PhoneNumber,  // 一个客户包含一个电话号码对象
    address: Address,    // 一个客户包含一个地址对象
    currency: Currency  // 默认使用的货币
) {
  require(id.matches("[A-Za-z0-9]+"), "Customer ID must be a non-empty alphanumeric string")
  require(name.trim.nonEmpty, "Customer name must be non-empty")
  // ID 格式在这里检查；是否与其他客户重复，由 Store 检查。

  // 年龄辅助方法：不传日期时，默认按今天计算周岁。
  def age(on: LocalDate = LocalDate.now()): Int = {
    Period.between(dob, on).getYears
  }
}

// ==================== 5. 金额 ====================
case class Money(
    amount: BigDecimal,  // 金额，例如 BigDecimal("12.34")，避免浮点数误差
    currency: Currency  // 金额必须和货币种类一起保存
) {
  // 以下运算属于第二题；第一题只需理解上面的两个字段。
  def +(other: Money): Money = {
    require(currency == other.currency, "Cannot add money in different currencies")
    Money(amount + other.amount, currency)
  }

  def -(other: Money): Money = {
    require(currency == other.currency, "Cannot subtract money in different currencies")
    Money(amount - other.amount, currency)
  }

  def *(quantity: Int): Money = {
    Money(amount * quantity, currency)
  }

  def in(target: Currency): Money = {
    ExchangeRates.convert(this, target)
  }

  // 计算时保留精度，显示时才四舍五入到两位小数。
  override def toString: String = {
    val displayedAmount = amount.setScale(2, BigDecimal.RoundingMode.HALF_UP)
    s"$displayedAmount $currency"
  }
}

object Money {
  // CSV 中 12,34 表示 12 + 34/100，也就是 12.34。
  def fromParts(units: Int, centiunits: Int, currency: Currency): Money = {
    require(units >= 0, "Price units must be non-negative")
    require(centiunits >= 0 && centiunits < 100, "Centiunits must be between 0 and 99")
    val amount = BigDecimal(units) + BigDecimal(centiunits) / 100
    Money(amount, currency)
  }
}

// 第二题：汇率表中的每个方向都直接使用试卷给定值。
object ExchangeRates {
  import Currency.*  //下面就不需要写成Currency.EUR ...

  // Map 中的键是 (原币种, 目标币种)，值是汇率。
  private val rates = Map(
    (EUR, USD) -> BigDecimal("1.16206"),
    (USD, EUR) -> BigDecimal("0.86035"),
    (EUR, GBP) -> BigDecimal("0.88181"),
    (GBP, EUR) -> BigDecimal("1.13298"),
    (EUR, CNY) -> BigDecimal("8.24901"),
    (CNY, EUR) -> BigDecimal("0.12118"),
    (USD, GBP) -> BigDecimal("0.75878"),
    (GBP, USD) -> BigDecimal("1.31669"),
    (USD, CNY) -> BigDecimal("7.09860"),
    (CNY, USD) -> BigDecimal("0.14085"),
    (CNY, GBP) -> BigDecimal("0.10688"),
    (GBP, CNY) -> BigDecimal("9.34663")
  )

  def convert(money: Money, target: Currency): Money = {
    if (money.currency == target) {
      money
    } else {
      // get 返回 Option：Some 表示找到，None 表示找不到。
      rates.get((money.currency, target)) match {
        case Some(rate) => Money(money.amount * rate, target)
        case None => throw new IllegalArgumentException(
          s"No rate for ${money.currency} -> $target"
        )
      }
    }
  }
}

// ==================== 6. 年龄分级 ====================
enum AgeRating {
  case All, Adult, Restricted

  // 判断某个生日的客户是否达到购买年龄。
  def allows(dob: LocalDate, on: LocalDate = LocalDate.now()): Boolean = {
    if (dob.isAfter(on)) {
      false // 出生日期不能在未来
    } else {
      val age = Period.between(dob, on).getYears
      this match {
        case All => true
        case Adult => age > 18 // 按题目 over the age of 18 的字面含义
        case Restricted => age >= 15
      }
    }
  }
}

object AgeRating {
  // CSV 使用的名称与 PDF 不同，这里兼容两种写法。
  def parse(value: String): AgeRating = {
    value match {
      case "AdultOnly" => AgeRating.Adult
      case "AdultApproval" => AgeRating.Restricted
      case other => AgeRating.valueOf(other)
    }
  }
}

// ==================== 7. 商品 ====================
case class Item(
    id: String,
    name: String,
    description: String,
    price: Money,       // 价格包含金额和币种
    rating: AgeRating   // 商品的年龄限制
) {
  require(id.matches("[A-Za-z0-9]+"), "Item ID must be a non-empty alphanumeric string")
  require(name.trim.nonEmpty, "Item name must be non-empty")
  require(price.amount >= 0, "Item price must be non-negative")
  // 库存数量由 Store 单独管理，不放在 Item 里。

  def suitableFor(customer: Customer, on: LocalDate = LocalDate.now()): Boolean = {
    rating.allows(customer.dob, on)
  }
}

// ==================== 8. 订单状态 ====================
enum OrderStatus {
  case New, Processed, AwaitingStock, Collected, Delivered, Cancelled
}

// ==================== 9. 订单 ====================
case class Order(
    id: String,
    customer: Customer,
    status: OrderStatus = OrderStatus.New,  // 默认是新订单
    trackingId: Option[String] = None,     // None 表示暂时没有物流编号
    items: Map[Item, Int] = Map.empty       // 商品 → 购买数量；默认是空 Map
) {
  require(id.matches("[A-Za-z0-9]+"), "Order ID must be a non-empty alphanumeric string")

  // 对订单的每个商品检查数量，并检查商品 ID 没有重复。
  for ((item, quantity) <- items) {
    require(quantity > 0, "Order quantities must be positive")
  }
  // 用普通循环记录已经出现的 ID，避免两个不同商品对象使用相同编号。
  private def checkItemIds(): Unit = {
    var seenIds = List.empty[String]
    for ((item, quantity) <- items) {
      require(!seenIds.contains(item.id), "Duplicate item IDs in order")
      seenIds = seenIds :+ item.id
    }
  }
  checkItemIds()

  trackingId match {
    case Some(value) => require(value.trim.nonEmpty, "Tracking ID must be non-empty when supplied")
    case None => () // 没有物流编号是合法情况，无需处理
  }

  // 以下全部属于第二题：第一题先读到这里即可。
  // copy 会创建新订单；原订单不会被修改。
  def updateStatus(next: OrderStatus): Order = {
    import OrderStatus.*
    val allowed = if (status == next) {
      true
    } else {
      status match {
        case New | AwaitingStock =>
          next == Processed || next == AwaitingStock || next == Cancelled
        case Processed => next == Collected || next == Cancelled
        case Collected => next == Delivered
        case Delivered | Cancelled => false
      }
    }
    require(allowed, s"Invalid order status transition: $status -> $next")
    copy(status = next)
  }

  private def editable(): Unit = {
    require(status == OrderStatus.New || status == OrderStatus.AwaitingStock,
      "Items can only be edited before the order is processed")
  }

  def removeItem(itemId: String): Order = {
    editable()
    var remainingItems = Map.empty[Item, Int]
    for ((item, quantity) <- items) {
      if (item.id != itemId) {
        remainingItems = remainingItems + (item -> quantity)
      }
    }
    copy(items = remainingItems)
  }

  def removeItem(item: Item): Order = {
    removeItem(item.id)
  }

  def addItem(item: Item, quantity: Int): Order = {
    editable()
    require(quantity > 0, "Added quantity must be positive")
    // 默认是新商品；如果找到相同 ID，就使用订单中已有的商品对象。
    var selectedItem = item
    var oldQuantity = 0
    for ((existing, existingQuantity) <- items) {
      if (existing.id == item.id) {
        selectedItem = existing
        oldQuantity = existingQuantity
      }
    }
    val newQuantity = Math.addExact(oldQuantity, quantity)
    val newItems = items + (selectedItem -> newQuantity)
    copy(items = newItems)
  }

  def updateQuantity(itemId: String, quantity: Int): Order = {
    editable()
    require(quantity >= 0, "Quantity must be non-negative")
    var found = false
    var newItems = Map.empty[Item, Int]
    for ((item, oldQuantity) <- items) {
      if (item.id == itemId) {
        found = true
        if (quantity > 0) {
          newItems = newItems + (item -> quantity)
        }
        // 新数量是 0 时不加入新 Map，相当于删除。
      } else {
        newItems = newItems + (item -> oldQuantity)
      }
    }
    require(found, s"Item $itemId is not in order $id")
    copy(items = newItems)
  }

  def updateQuantity(item: Item, quantity: Int): Order = {
    updateQuantity(item.id, quantity)
  }

  def withTrackingId(value: String): Order = {
    copy(trackingId = Some(value))
  }

  def totalPrice(currency: Currency): Money = {
    // total 只是方法内部的临时变量，不修改订单。
    var total = Money(BigDecimal(0), currency)
    for ((item, quantity) <- items) {
      val linePrice = item.price * quantity
      val convertedPrice = linePrice.in(currency)
      total = total + convertedPrice
    }
    total
  }
}
