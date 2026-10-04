# BookShelf 项目 Scala 3 语法笔记

这份笔记只整理书架系统和它的入口**实际用到**的知识。先分清：`match`、`case`、`val` 是语言语法；`map`、`filter`、`split` 是库提供的方法。看到 `.`，通常是在访问字段或调用方法。

## 1. 一段 Scala 代码怎么读

```scala
def byId(id: Int): Option[Book] = books.find(_.id == id)
```

| 片段 | 意思 |
|---|---|
| `def` | 定义方法 |
| `byId` | 方法名 |
| `id: Int` | 参数 `id` 的类型是整数 |
| `: Option[Book]` | 返回类型：可能找到一本 `Book`，也可能没找到 |
| `=` | 右边给出方法的实现 |
| `books.find(...)` | 调用列表的方法 |

Scala 常把**最后一个表达式**作为方法结果，不必写 `return`。例如 `add` 方法最后一行是 `copy(...)`，这个新 `Library` 就是返回值。语句末尾通常不用写分号。

方括号写**类型参数**：`List[Book]` 是“书的列表”，`Option[Book]` 是“可能有一本书”，`Either[String, Library]` 是“错误文字或成功后的书库”。

### 常见符号

| 符号 | 项目里的例子 | 意思 |
|---|---|---|
| `:` | `id: Int` | 指定类型 |
| `=` | `val id = ...` | 定义或赋值；在方法定义中接方法体 |
| `==` / `!=` | `b.id == id` / `parts.length != 6` | 相等 / 不相等 |
| `=>` | `b => b.id`、`case None =>` | 左边是函数参数或匹配模式，右边是处理代码 |
| `_` | `_.id`、`case _` | 依上下文表示省略的参数或“任意值” |
| 竖线（pipe） | 多个菜单输入写在同一个 `case` 中 | 在模式中表示“或”；文件文本中则是分隔符 |
| `:+` | `books :+ book` | 在不可变列表尾部追加，得到新列表 |
| `*` | `String*`、`"=" * 56` | 前者是可变数量参数；后者是重复字符串 |

## 2. 变量、作用域和访问权限

```scala
val book = Book(...)      // 这个名字不能重新赋值
var lib = Storage.load()  // 后面可以 lib = 新书库
```

- `val`：名字只能绑定一次；`var`：名字能重新赋值。**这与全局或局部无关**。
- 方法内部定义的 `lib` 是局部变量；`Storage.defaultPath` 是单例对象的成员；`book.title` 属于某一本书。
- `val year: Int = 2024` 显式写类型；`val year = 2024` 由编译器推断成 `Int`。
- `private def replace(...)` 只能从所属的 `Library` 内部访问。
- `Unit` 表示方法不返回有用的结果，类似 Java `void`；例如 `save(...): Unit`。

## 3. 类、单例对象和创建数据

```scala
case class Book(id: Int, title: String, borrower: Option[String] = None)
object Book {
  def fromRecord(raw: String): Option[Book] = ...
}
```

- `case class Book` 描述**每一本具体的书**。它自动提供 `copy`、按字段比较、可读的打印形式和模式匹配能力。
- `Book(1, "Scala", "张三", 2024, "编程")` 不写 `new`，是 `case class` 自动提供的便捷构造调用。
- `object Book` 是同名的**伴生对象**，只有一份。`Book.fromRecord(text)` 不需要先有一本书就能调用。
- `object Storage` 也是单例，集中放文件读写方法；`Storage.defaultPath` 是它的属性。
- `book.copy(borrower = Some("小明"))` 创建**新书**，只修改指定字段；旧 `book` 不变。`borrower = ...` 在这里是**命名参数**。
- `borrower: Option[String] = None` 中的 `= None` 是**默认参数**；构造书时可以省略借阅人。
- `Library(books: List[Book] = Nil)` 让 `Library()` 创建空书库；`Nil` 是空列表。
- `object Library` 中的 `Library.sample` 创建五本样例书。`def sample` 每次调用都会构造一份书库。

## 4. 程序入口和参数

```scala
@main
def bookShelf(args: String*): Unit = {
  args.headOption.map(_.toLowerCase) match {
    case Some("preview") => BookShelfApp.preview()
    case _               => BookShelfApp.runInteractive()
  }
}
```

- `@main`：Scala 3 程序入口注解；方法可以叫 `bookShelf`，不必叫 `main`。
- `args: String*`：接收零个或多个字符串启动参数；IDE 没填 Program arguments 时，`args` 是空序列。
- `args.headOption`：安全地取第一个参数；没有则是 `None`。
- `.map(_.toLowerCase)`：如果有第一个参数，就转成小写。
- 首参为 `preview` 时运行预览；其余情况运行交互模式。

项目还有 `main.scala` 中的默认入口，它把 `preview`、`demo` 等参数转交给书架或课程。`import showcase.BookShelfApp` 使这个对象可直接用名字访问；`package showcase` 把多个源码定义放在同一个包中。`import java.nio.file.{Files, Path}` 一次导入两个名字。

## 5. 分支：`if`、`while`、`match/case`

### `if` 是表达式

```scala
if (q.isEmpty) books else books.filter(...)
```

条件为真得到左边的值，否则得到右边的值。代码块的最后一个表达式也能作为整个块的结果。

### `while` 用于重复执行

```scala
var running = true
while (running) {
  printMenu()
  // 读取并处理一次菜单选择
}
```

退出时把 `running = false`，下一轮检查条件便结束。

### `match` 根据“形状”选择分支

```scala
lib.byId(1) match {
  case None       => println("没找到")
  case Some(book) => println(book.title)
}
```

项目还用到这些模式：

| 写法 | 意思 |
|---|---|
| `case Some(book) =>` | 有值时把里面的值取出，命名为 `book` |
| `case Left(err) =>` | 操作失败，取出错误文字 |
| `case Right(next) =>` | 操作成功，取出新书库 |
| `case Some(book) if book.available =>` | 匹配到书后，再检查一个条件；`if` 是**守卫** |
| 退出菜单的三个字符串模式 | 三个值匹配任意一个 |
| `case (cat, xs) =>` | 把二元组拆成分类名和该分类的书列表 |
| `case _ =>` | 匹配其他所有情况，不保存具体值 |
| `case other =>` | 匹配其他所有情况，并把值保存为 `other` |

例如退出选项写成 `case "q" | "Q" | "0" =>`。`case` 从上到下尝试；先匹配成功的分支先执行。

## 6. `Option[A]`：值可能不存在

```scala
val found: Option[Book] = lib.byId(1)
```

- `Some(book)`：有值；`None`：无值。
- `Some("")` **不是** `None`；它装着一个空字符串。
- `isEmpty` 检查是否为空；`isDefined` 检查是否有值。
- `getOrElse(默认值)`：有值就取值，否则使用默认值。
- `map(函数)`：只有 `Some` 时才处理里面的值；`None` 保持 `None`。
- `.get` 能强行取值，但对 `None` 会抛异常；通常用 `match` 或 `getOrElse`。

例子：

```scala
"12".toIntOption                 // Some(12)
"abc".toIntOption                // None
"abc".toIntOption.getOrElse(0)   // 0
```

`Book.fromRecord` 的返回类型是 `Option[Book]`：格式和年份有效时 `Some(Book(...))`，否则 `None`。

## 7. `Either[E, A]`：失败原因或成功结果

```scala
lib.borrow(1, "小明") match {
  case Left(err)   => println(err)
  case Right(next) => lib = next
}
```

项目中 `Either[String, Library]` 的左侧 `String` 是错误文字，右侧 `Library` 是成功后的新书库。`Option` 只说明“有/无”；`Either` 还能说明**为什么失败**。预览中的 `giveBack(1).getOrElse(lib)` 表示：成功时用新书库，失败时保留旧书库。

## 8. `List` 和集合方法

`List[Book]` 是书列表；本项目使用的是不可变 `List`。方法返回新列表，不直接改旧列表。

| 方法 | 直白理解 | 项目例子 |
|---|---|---|
| `List(...)` | 创建列表 | `List(b.title, b.author, b.category)` |
| `Nil` | 空列表 | `books: List[Book] = Nil` |
| `size` / `isEmpty` | 项数 / 是否为空 | `books.size` |
| `last` | 最后一项 | `lib.books.last.id` |
| `map` | 每项变成另一项 | `books.map(_.id)` 得编号列表 |
| `filter` | 保留满足条件的项 | `books.filter(_.available)` |
| `filterNot` | 排除满足条件的项 | `books.filterNot(_.available)` |
| `find` | 找第一项，返回 `Option` | `books.find(_.id == id)` |
| `exists` | 是否至少有一项满足条件 | 搜索书名、作者、分类 |
| `flatMap` | 转换并展开，可丢掉失败项 | `lines.flatMap(Book.fromRecord)` |
| `maxOption` | 安全取最大值 | 编号列表取最大编号 |
| `groupBy` | 按键分组，得到 `Map` | `books.groupBy(_.category)` |
| `sortBy` | 按指定值排序 | `books.sortBy(_.id)` |
| `foreach` | 逐项执行动作，通常用来打印 | `books.foreach(b => println(b.line))` |
| `mkString` | 用指定分隔符连接各项 | 把书字段用竖线拼成文件记录 |
| `toList` | 转成列表 | 数组或分组后的 `Map` 转列表 |
| `books :+ book` | 在末尾追加书，得到新列表 | `add` 方法 |

重点区别：

```scala
books.map(_.id)           // 所有书 → 所有编号
books.filter(_.available) // 所有书 → 满足条件的多本书
books.find(_.id == 1)     // 所有书 → Some(一本书) 或 None
```

`foreach` 主要为了打印等副作用；`map` 主要为了生成新集合。`flatMap(Book.fromRecord)` 会把 `Some(book)` 展开成一本书，把 `None` 当作零本书跳过。

### 匿名函数和下划线

```scala
books.map(book => book.id)
books.map(_.id)             // 上面写法的缩写
```

`book => book.id` 表示“输入一本书，输出它的编号”。`_.id` 中的 `_` 代表当前元素。`books.map(Book.toRecord)` 则把现有方法作为参数传给 `map`。

### 元组

`groupBy` 得到按分类分组的 `Map`，`.toList` 后每项是 `(分类名, 书列表)` 这样的二元组。`_._1` 表示取二元组的第 1 项；`case (cat, xs)` 则给两项分别取名字。

## 9. 字符串与文本解析

| 写法 | 意思 |
|---|---|
| `title.trim` | 去掉首尾空白 |
| `text.toLowerCase` | 转小写，用于不区分大小写的搜索 |
| `text.contains(q)` | 是否包含子串 `q` |
| `text.isEmpty` / `text.nonEmpty` | 空 / 非空 |
| `text.toIntOption` | 尝试转整数，得到 `Option[Int]` |
| `s"借给 $who"` | 把变量插入字符串 |
| `f"$id%3d"` | 插入整数，至少占 3 个字符宽度 |
| `"=" * 56` | 重复字符串 56 次 |
| `"\n"` | 换行字符 |
| `"\""` | 字符串内的双引号 |

文件一行用 `|` 分隔。解析时：

```scala
val parts = raw.split("\\|", -1)
```

- `split` 的参数是正则表达式。`|` 在正则中有特殊含义，所以源码写 `"\\|"` 来表示“按普通竖线切”。
- `-1` 保留末尾的空字段；未借出的记录以 `|` 结尾，第 6 项必须保留为 `""`。
- `parts` 是数组；下标从 0 开始，所以 `parts(0)` 是编号，`parts(5)` 是借阅人。
- 代码先检查 `parts.length != 6`；不是 6 项就返回 `None`，其余情况才访问这些位置，避免越界。

## 10. 输入输出与路径

| 调用 | 做什么 |
|---|---|
| `readLine("提示")` | 显示提示并读取键盘一行文字 |
| `print(...)` / `println(...)` | 打印；`println` 结尾换行 |
| `println()` | 打印一个空行 |
| `Path.of("data", "bookshelf.txt")` | 建立路径对象 |
| `path.getParent` | 取父目录 |
| `path.toAbsolutePath` | 显示绝对路径 |
| `Files.exists(path)` | 文件是否存在 |
| `Files.createDirectories(parent)` | 创建父目录 |
| `Files.writeString(path, body)` | 写入文本，默认会覆盖原文件 |
| `Files.readString(path)` | 读取整个文件为字符串 |

`Path` 和 `Files` 来自 **Java 标准库**，Scala 可以直接调用。`data/bookshelf.txt` 是相对路径，实际位置取决于程序运行时的工作目录。

`def save(library: Library, path: Path = defaultPath)` 中的第二个参数有默认值：`Storage.save(lib)` 用默认路径，`Storage.save(lib, previewFile)` 用指定路径。`path.getParent` 可能返回 Java 的 `null`，所以代码先用 `parent != null` 检查。

## 11. 一次借书，把语法串起来

```text
readLine 读到 "1"
→ "1".toIntOption 得 Some(1)
→ case Some(n) 取出编号 1
→ lib.borrow(1, "小明")
→ byId 得 Some(book)
→ book.copy(borrower = Some("小明")) 得新书
→ Library 内部的 copy(...) 得新书库
→ Right(next) 表示借书成功
→ lib = next 更新当前状态
→ Storage.save(lib) 写入文件
```

## 12. 最容易混淆的四组

| 写法 | 实际含义 |
|---|---|
| `None` | `Option` 中没有值 |
| `Nil` | 空的 `List` |
| `""` | 存在的空字符串；`Some("")` 仍然有值 |
| `Left(err)` | `Either` 的失败；`Right(value)` 是成功 |

还有两条项目行为要记住：`Storage.load` 在文件不存在或读不到有效书时会返回 `Library.sample`；`Book.fromRecord` 遇到非法编号会使用 `0`，遇到非法年份会返回 `None`。这些是**项目自己的规则**，不是 Scala 语法自动规定的。
