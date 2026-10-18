package programming_exam_2025.csv_practice

// CSV 中的币种文字会转换成这个枚举。
enum Currencies {
  case CNY, EUR, USD
}

// 一个 Product 对象对应 CSV 中的一行商品数据。
case class Product(
    id: String,
    name: String,
    currency: Currencies,
    units: Int,       // 价格整数部分
    centiunits: Int,  // 价格小数部分，例如 50 表示 0.50
    quantity: Int    // 库存数量
)
