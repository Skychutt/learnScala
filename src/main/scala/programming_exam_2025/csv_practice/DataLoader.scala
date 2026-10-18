package programming_exam_2025.csv_practice

import scala.io.Source

object DataLoader {
  def loadProducts(path: String): List[Product] = {
    // 打开文件，得到一个可以逐行读取的迭代器。
    val source = Source.fromFile(path, "UTF-8")

    try {
      val lines = source.getLines()

      // 一开始没有商品，所以创建一个空 List。
      var products: List[Product] = List.empty[Product]

      // 跳过第一行：id,name,currency,units,centiunits,quantity。
      if (lines.hasNext) {
        lines.next()
      }

      while (lines.hasNext) {
        val line = lines.next()

        // 空行不处理。
        if (line.trim.nonEmpty) {
          // 本次练习没有带引号或包含逗号的字段，可以直接拆分。
          val fields = line.split(",", -1)

          val product = Product(
            id = fields(0).trim,
            name = fields(1).trim,
            currency = Currencies.valueOf(fields(2).trim),
            units = fields(3).trim.toInt,
            centiunits = fields(4).trim.toInt,
            quantity = fields(5).trim.toInt
          )

          // :+ 把商品放在 List 末尾，返回新的 List。
          // 因此需要把新的 List 重新赋给 products。
          products = products :+ product
        }
      }

      // Scala 把代码块最后的表达式作为返回值。
      products
    } finally {
      // 无论读取成功还是中途出错，都关闭文件。
      source.close()
    }
  }
}
