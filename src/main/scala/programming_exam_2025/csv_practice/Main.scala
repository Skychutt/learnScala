package programming_exam_2025.csv_practice

object Main {
  def main(args: Array[String]): Unit = {
    // 默认从 learnScala 项目根目录运行；也可以通过参数传入文件路径。
    val path = if (args.length > 0) {
      args(0)
    } else {
      "src/main/scala/programming_exam_2025/csv_practice/products.csv"
    }

    // CSV 数据最终存放在 List[Product] 里面。
    val products: List[Product] = DataLoader.loadProducts(path)

    println("共读取 " + products.size + " 件商品")
    for (product <- products) {
      // 用十进制数合并价格：12 + 50 / 100 = 12.50。
      val price = BigDecimal(product.units) + BigDecimal(product.centiunits) / 100
      // 显示时保留两位小数，例如 3.00，而不是 3。
      val displayedPrice = price.setScale(2)
      println(s"${product.id}, ${product.name}, $displayedPrice ${product.currency}, 库存：${product.quantity}")
    }
  }
}
