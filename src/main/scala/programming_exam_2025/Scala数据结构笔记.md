# Scala 常用数据结构笔记

这份笔记用于当前 CSV 和考试项目练习，先掌握 Array、List、Map、Set、Tuple，再了解 ArrayBuffer 和 Vector。

## 1. 先理解类型写法

Scala 用方括号表示集合中元素的类型：

```scala
Array[String]       // 字符串数组
List[Customer]      // 客户对象列表
Map[String, Item]   // 键是字符串，值是商品对象
List[(Item, Int)]   // 每个元素是“商品、数量”元组的列表
```

声明空集合也需要说明元素类型：

```scala
val names = List.empty[String]
val items = Map.empty[String, Item]
```

## 2. Array：数组

特点：有顺序、长度固定、元素可以修改、按下标访问快。下标从 0 开始。

```scala
val names: Array[String] = Array("Alice", "Bob", "Tom")

println(names(0))      // Alice
println(names.length) // 3
names(0) = "Mary"     // 修改原数组的元素

val more = names :+ "Jack" // 返回新数组，原数组长度不变

for (name <- names) {
  println(name)
}
```

项目用途：split 拆分 CSV 后返回 Array[String]，适合按列取字段。

```scala
val fields = "VG001,150".split(",", -1)
val itemId = fields(0).trim
val quantity = fields(1).trim.toInt
```

- `-1` 保留末尾空字段，不是保留空格。
- `trim` 去掉字符串两端的空白。
- 下标不存在会抛异常。

## 3. List：不可变列表

特点：有顺序、可以保存重复元素、不可变。底层是链表，在头部添加快，按下标访问需要从头遍历。

```scala
val numbers: List[Int] = List(10, 20, 30)

numbers(0)          // 10
numbers.head        // 10
numbers.tail        // List(20, 30)
numbers.size        // 3
numbers.isEmpty     // false
numbers.contains(20) // true
```

空列表不能直接调用 head 或 tail。

添加和拼接会创建新列表，原列表不变：

```scala
val atStart = 5 +: numbers       // List(5, 10, 20, 30)
val atEnd = numbers :+ 40        // List(10, 20, 30, 40)
val joined = numbers ++ List(40, 50)
```

逐步保存读取结果：

```scala
var results = List.empty[Int]
results = results :+ 10
results = results :+ 20
```

这里重新赋值的是 results，不是修改原来的 List。

项目用途：Loader 返回 List[Customer]、List[Item]、List[Order] 等。

本题数据少，使用 :+ 直白易懂；大量数据反复追加到 List 末尾效率较低。

## 4. Map：键值映射，也叫字典

特点：根据键查找值，键不能重复。默认 Map 不可变，不应依赖其插入顺序。

```scala
var stock: Map[String, Int] = Map(
  "VG001" -> 150,
  "VG004" -> 220
)
```

`"VG001" -> 150` 是键值对，也可以写成元组 `("VG001", 150)`。

查询：

```scala
stock("VG001")               // 150，键不存在会抛异常
stock.contains("VG001")      // true
stock.get("VG001")           // Some(150)
stock.get("UNKNOWN")         // None
stock.getOrElse("UNKNOWN", 0) // 0
stock.size                    // 2
```

添加、替换和删除：

```scala
stock = stock + ("VG007" -> 90)  // 添加
stock = stock + ("VG001" -> 100) // 相同键，替换值
stock = stock - "VG004"         // 删除键及其值
```

注意：设置 VG001 为 100 不是在旧数量上增加 100。

遍历：

```scala
for ((itemId, quantity) <- stock) {
  println(itemId + ": " + quantity)
}
```

项目用途：Store 用 Map[String, Customer] 等类型，按 ID 保存和查找对象。

汇率表还使用元组作为键：

```scala
val rates: Map[(Currency, Currency), BigDecimal] = Map(
  (Currency.EUR, Currency.USD) -> BigDecimal("1.16206")
)
```

两个币种合起来是一个键，顺序表示转换方向。

## 5. Set：不重复元素的集合

默认 Set 不可变，不应依赖其插入顺序。

```scala
val countries: Set[String] = Set("China", "Ireland", "China")

countries.size              // 2
countries.contains("China") // true

val more = countries + "France"
val less = countries - "Ireland"
```

用途：去重、记录是否出现过某个值。当前简化版 Loader 没有额外的去重集合。

## 6. Tuple：元组

特点：把固定数量的值组合起来，各个位置可以使用不同类型。

```scala
val entry: (String, Int) = ("VG001", 150)

entry._1 // VG001
entry._2 // 150
```

注意：元组的 ._1 从 1 开始；数组和列表的下标从 0 开始。

可以直接拆开命名：

```scala
val (itemId, quantity) = entry
println(itemId)
println(quantity)
```

项目用途：库存方法返回 List[(Item, Int)]，每个元素组合一个商品和库存数量。

Tuple 适合临时组合数据；客户、商品等业务概念优先用字段名明确的 case class。

## 7. ArrayBuffer：可变、可增长的数组缓冲区

使用前导入：

```scala
import scala.collection.mutable.ArrayBuffer

val numbers = ArrayBuffer[Int]()
numbers += 10
numbers += 20
numbers ++= List(30, 40)

numbers(0) = 99
numbers.remove(1) // 删除下标 1 的元素，即 20
numbers -= 30    // 删除一个值为 30 的元素

val result: List[Int] = numbers.toList
```

区别：remove(1) 按位置删除，-= 30 按值删除。

用途：需要可变集合、频繁追加数据时使用。考试业务对象本身仍应按题意保持不可变。

## 8. Vector：不可变的索引序列

```scala
val numbers = Vector(10, 20, 30)
val more = numbers :+ 40
println(numbers(1)) // 20
```

支持快速的按下标访问及末尾追加，底层是树状结构。

当前项目为了贴近讲义，Loader 使用 List，不必急着掌握 Vector 的实现。

## 9. val、var 与集合是否可变，是两回事

val：变量不能重新赋值；var：变量可以重新赋值。

集合是否能修改元素，由集合类型决定。

```scala
val a = Array(1, 2)
a(0) = 9 // 可以：数组可变，a 没有重新赋值

var b = List(1, 2)
b = b :+ 3 // 可以：把新 List 赋给 b

// b(0) = 9 // 不可以：List 本身不可变
```

不要把“val”理解成“对象内所有内容都不能修改”。

## 10. 常见集合转换

```scala
val fields = "A,B,C".split(",", -1) // Array[String]
val fieldList = fields.toList       // List[String]
val arrayAgain = fieldList.toArray  // Array[String]
val unique = List(1, 1, 2).toSet     // Set(1, 2)
val text = fieldList.mkString(", ") // A, B, C
```

在 Loader 中，表头转成 List 是为了和预期的 List 比较内容；普通数据字段按下标读取，保留 Array 即可。

## 11. 当前项目怎么选

- CSV 一行的字段：Array[String]。
- Loader 读取成功的多条记录：List。
- 按客户、商品、订单 ID 查询：Map。
- 商品和库存数量的一对数据：Tuple。
- 需要去重：Set。
- 需要频繁修改、追加：ArrayBuffer。

读 CSV 时，核心流程是：拆成 Array → 读取字段并创建对象 → 加入 List → Main 遍历 List → Store 保存到 Map。
