# BookShelf 书架系统逐行导读

这份导读只讲 `src/main/scala/showcase/` 中的书架系统，以及负责跳转到它的 `src/main/scala/main.scala`。行号按当前源码标注；阅读时在编辑器里打开对应文件，左右对照。空行只用于分段，单独的 `}`、`)` 只用于结束前面开启的代码块或参数列表。

## 1. 先建立全局图景

```text
键盘输入 / 命令行参数
        ↓
main.scala → BookShelfApp.scala  菜单、预览、打印
                    ├──→ Library.scala  业务规则，使用 Book
                    └──→ Storage.scala  读写文件，调用 Book 的文本转换
                              ↓
                         model.scala    Book 数据与一行文本的转换
```

- `Book` 是一本书：编号、书名、作者、年份、分类、借阅人。
- `Library` 是一个 `List[Book]` 加上操作它的方法。`add`、`remove`、`borrow`、`giveBack` 都产生新的 `Library`，旧值不被修改。
- `BookShelfApp` 持有一个可重新赋值的 `var lib`，把每次成功操作得到的新 `Library` 放进去，再交给 `Storage.save` 保存。
- `Storage` 负责把对象转成文本，或者把文本转回对象。`data/bookshelf.txt` 是交互模式的实际数据，预览模式使用 `target/bookshelf-preview.txt`。

关键区别：**`Library` 对象内部不可变，不等于程序的当前状态永远不变**。当前状态通过 `var lib = 新的 Library` 更新。

## 2. 运行入口：`src/main/scala/main.scala`

| 行 | 解释 |
|---|---|
| 1 | `import lessons.Course`：把课程菜单对象引入当前文件，供后面调用。 |
| 2 | `import showcase.BookShelfApp`：引入书架程序对象。`showcase` 是包名。 |
| 4–13 | `/** ... */` 是文档注释，只说明命令，不参与运行。`sbt "run preview"` 把 `preview` 当程序参数；`runMain showcase.bookShelf` 则直接选择另一个入口。 |
| 14 | `@main` 是 Scala 3 注解：让下一行的顶层方法成为可启动的程序入口。 |
| 15 | `def main(args: String*): Unit = {`：定义入口；`String*` 表示零个或多个字符串参数，方法内部把它当序列；`Unit` 类似 Java 的 `void`；`{` 开始方法体。 |
| 16 | `args.headOption` 安全取得第一个参数：无参数得 `None`，有参数得 `Some(参数)`；`.map(_.toLowerCase)` 把存在的参数转小写；`match` 根据结果分支。下划线 `_` 是这个小函数的单个参数。 |
| 17 | `case Some("demo") | Some("app") | Some("bookshelf") =>`：三个模式任选其一；`=>` 后是执行内容。 |
| 18 | 调用 `BookShelfApp.runInteractive()`，进入交互模式。 |
| 19 | `case Some("preview") =>`：匹配第一个参数为 `preview`。 |
| 20 | 调用 `BookShelfApp.preview()`，进入自动演示。 |
| 21 | `Some("help" | "-h" | "--help")` 是嵌套在 `Some` 里的“或”模式，匹配三种帮助参数。 |
| 22 | 打印课程目录。 |
| 23 | `println()` 不传参数，打印空行。 |
| 24 | 打印预览命令；源码中的 `\"` 用于在字符串内显示双引号。 |
| 25 | 打印交互命令。 |
| 26 | `case _` 匹配其余情况，包括完全没有参数。这里的 `_` 表示“任何值”。 |
| 27 | 其余参数交给课程入口处理。 |
| 28 | 结束 `match` 块。 |
| 29 | 结束方法体。Scala 的分支和块会产生值，但本入口声明 `Unit`，只关心执行效果。 |

`build.sbt` 中 `scalaVersion := "3.3.8"` 指定 Scala 版本，`Compile / mainClass := Some("main")` 让普通 `sbt run` 默认进入这里。因此 `sbt "run preview"` 的调用链是 `main → BookShelfApp.preview`。

## 3. 数据模型：`src/main/scala/showcase/model.scala`

| 行 | 解释 |
|---|---|
| 1 | `package showcase`：把本文件的顶层定义放进 `showcase` 命名空间。 |
| 3–7 | 文档注释解释 `borrower` 的含义；注释本身不产生运行逻辑。 |
| 8 | `case class Book(`：定义数据类。编译器自动提供构造、按字段比较、可读的 `toString`、`copy`、模式匹配支持等。此处开始构造参数列表。 |
| 9 | `id: Int`：编号是整数。 |
| 10 | `title: String`：书名是字符串。 |
| 11 | `author: String`：作者是字符串。 |
| 12 | `year: Int`：年份是整数。 |
| 13 | `category: String`：分类是字符串。 |
| 14 | `borrower: Option[String] = None`：借阅人可能没有。默认 `None` 表示在架；`Some("小明")` 表示被小明借走。默认参数让 `Book(1, ..., "编程")` 不必传第六项。 |
| 15 | `)` 结束参数列表，`{` 开始类体。每个参数默认同时成为可读取的字段，如 `book.title`。 |
| 16 | `def available: Boolean = borrower.isEmpty`：无参数方法，返回布尔值；`None.isEmpty` 是 `true`，`Some(...)` 是 `false`。调用时写 `book.available`。 |
| 18 | `statusText` 返回状态文字；`borrower match` 对 `Option` 做模式匹配，`{` 开始分支块。 |
| 19 | 借阅人是 `None`，返回“在架”。 |
| 20 | `Some(who)` 解包并把内部姓名绑定到变量 `who`；`s"借给 $who"` 是字符串插值。 |
| 21 | 结束匹配块。两个分支都是 `String`，因此整个方法是 `String`。 |
| 23 | `def line: String =` 声明生成屏幕显示行的方法；表达式放在下一行。 |
| 24 | `f"..."` 是格式化插值：`$id%3d` 使整数占至少 3 位；`${title}%-24s` 使书名左对齐、占至少 24 位；作者和分类同理。`$statusText` 调用前面的方法。宽度是**最小**宽度，长书名不会截断，所以列可能错位。 |
| 25 | 结束 `Book` 类体。 |
| 27 | `object Book` 是 `Book` 的伴生对象：名称相同，放工具方法。调用写 `Book.toRecord(...)`。`case class` 的构造也可写 `Book(...)`。 |
| 28 | 文档注释定义文件格式：六个字段，以竖线分隔。 |
| 29 | `toRecord(book: Book): String` 接受一本书，返回文本；`{` 开始方法体。 |
| 30 | `getOrElse("")`：`Some(姓名)` 得到姓名；`None` 得到空字符串。文件中最后一个字段因此可以为空。 |
| 31 | 建立 6 项 `List`，用 `.mkString("|")` 拼接。`Int` 会转成文本。例如在架的 1 号书成为 `1|Programming in Scala|Odersky|2021|编程|`。 |
| 32 | 结束 `toRecord`。 |
| 34 | `fromRecord(raw: String): Option[Book]` 把一行文字尝试转为一本书；格式不对时返回 `None`。 |
| 35 | `split("\\|", -1)` 按竖线分成字段。竖线在正则中有特殊含义，故写成 `"\\|"`；`-1` 保留最后一个空字段。`parts` 是字符串数组。 |
| 36 | 检查字段数是否不是 6。先检查长度，后面使用 `parts(0)` 等位置才安全。 |
| 37 | 字段数不对，返回 `None`。 |
| 38 | `else` 开始处理字段数正确的情况。 |
| 39 | 解析第 1 项编号；如果编号不是数字，采用 0。 |
| 40 | 第 2 项是书名。 |
| 41 | 第 3 项是作者。 |
| 42 | 第 4 项尝试解析年份；类型为 `Option[Int]`。 |
| 43 | 第 5 项是分类。 |
| 44 | 声明借阅人类型为 `Option[String]`。 |
| 45 | 第 6 项为空时用 `None` 表示在架，否则用 `Some(姓名)`。 |
| 47 | 对年份解析结果做模式匹配。 |
| 48 | 年份有效时，创建 `Book`，再用 `Some` 包起来。 |
| 49 | 年份无效时返回 `None`。 |
| 50–53 | 结束年份匹配、`else`、方法和伴生对象。 |

这里同时用到了两种“空”：`None` 是程序里没有借阅人的类型安全表示；文本文件里对应的是最后一个字段为空。`Book.toRecord` 和 `Book.fromRecord` 是两个方向的转换。

## 4. 业务规则：`src/main/scala/showcase/Library.scala`

| 行 | 解释 |
|---|---|
| 1 | 与 `Book` 同属 `showcase` 包，所以直接写 `Book`，不用再导入。 |
| 3–6 | 注释声明设计意图：更新时得到新书库。 |
| 7 | `case class Library(books: List[Book] = Nil)`：书库由书的列表组成，默认 `Nil` 是空列表。`Library()` 可建空书库。 |
| 9 | `nextId`：先 `.map(_.id)` 得编号列表，再 `.maxOption` 安全取最大值，空列表时得 `None`；`.getOrElse(0) + 1` 得新编号。编号删除后若最大值减少，编号可能再次使用。 |
| 11 | `byId` 使用 `.find(_.id == id)` 找第一本匹配书；找到是 `Some(Book)`，未找到是 `None`。内层 `_.id` 指遍历到的书，右侧 `id` 指方法参数。 |
| 13 | `add` 接收四个字段，返回 `Library`；`{` 开始方法体。 |
| 14 | 建书：自动编号，文字字段 `.trim` 去首尾空白。这里未检查年份、作者或分类的合法性。 |
| 15 | `books :+ book` 在列表末尾追加一本书；`copy(books = ...)` 是 `case class` 自动提供的复制方法，只替换 `books` 字段，生成**新** `Library`。块最后一个表达式就是返回值，无需 `return`。 |
| 16 | 结束 `add`。 |
| 18 | `remove` 的返回类型是 `Either[String, Library]`：`Left(错误说明)` 或 `Right(新书库)`。 |
| 19 | 查不到编号就返回 `Left`；`if` 本身是表达式，能直接作为方法返回值。 |
| 20 | 查得到就用 `.filterNot(_.id == id)` 保留所有编号不同的书，再 `copy` 得新书库，并包在 `Right` 中。这个方法没有禁止删除已借出的书。 |
| 21 | 结束 `remove`。 |
| 23 | `borrow` 输入编号和姓名，也用 `Either` 表示成功/失败。 |
| 24 | 查书并立即对 `Option[Book]` 匹配。 |
| 25 | `None`：编号不存在。 |
| 26 | `Some(book) if ...` 是带守卫的模式；找到书且 `borrower.isDefined` 时进入分支。 |
| 27 | 返回“已经借给谁”的 `Left`。此处 `.get` 因第 26 行的守卫才安全，平时应尽量用模式匹配处理 `Option`。 |
| 28 | 书存在且前一分支没匹配，再判断借阅人姓名去空白后是否为空。分支有顺序，所以已借出的书即使这次姓名为空，显示的还是“已经借出”。 |
| 29 | 姓名为空则返回 `Left` 错误。 |
| 30 | `Some(book)` 捕获其余找到书的情况。 |
| 31 | `book.copy(borrower = Some(who.trim))` 得新 `Book`；`replace(...)` 把它放入新书库；`Right(...)` 表示成功。 |
| 32–33 | 结束匹配和方法。 |
| 35 | `giveBack` 输入编号，返回错误或新书库。 |
| 36 | 查书并匹配。 |
| 37 | 查不到，返回错误。 |
| 38 | 查到书且 `available` 为真，即本来就在架上。 |
| 39 | 对重复归还返回错误。 |
| 40 | 最后一个 `Some(book)` 捕获其余已借出的情况。 |
| 41 | 复制书并把 `borrower` 设回 `None`，再替换进新书库并返回 `Right`。 |
| 42–43 | 结束匹配和方法。 |
| 45 | `find` 用关键词搜索，返回书列表。注意它与按编号查找的 `byId` 不同。 |
| 46 | `.trim.toLowerCase` 去空格并转小写，实现大小写不敏感搜索。 |
| 47 | 空关键词返回全部书。 |
| 48 | `.filter { b =>` 开始遍历每本书，并把当前书命名为 `b`；花括号内的布尔值决定是否保留。 |
| 49 | 把书名、作者、分类放进列表，`.exists(...)` 表示至少一个字段的小写文本包含关键词 `q`。借阅人和年份不参与搜索。 |
| 50 | 结束传给 `filter` 的函数体。 |
| 51 | 结束 `find`。 |
| 53 | `.filter(_.available)` 只保留在架书。 |
| 54 | `.filterNot(_.available)` 只保留已借书。 |
| 56 | `stats` 返回一段统计文本。 |
| 57 | `books.size` 是总本数。 |
| 58 | 已借本数通过前面的方法计算。 |
| 59 | `.groupBy(_.category)` 得 `Map[分类, List[Book]]`；`.toList` 转成 `(分类, 书列表)` 元组列表；`.sortBy(_._1)` 按元组第 1 项即分类名排序。分类排序遵循字符串顺序。 |
| 60 | 对每个元组做模式匹配 `case (cat, xs)`，生成“分类 n 本”；`.mkString("，")` 用中文逗号连接。空书库时得到空串。 |
| 61 | `s"..."` 把统计数与分类文字插入结果。`total - out` 就是在架数。 |
| 62 | 结束 `stats`。 |
| 64 | `private def replace`：只允许 `Library` 内部调用的辅助方法；输入更新后的书，返回新书库。 |
| 65 | `.map` 遍历所有旧书；编号相同则放新书，否则保留旧书；`copy` 包成新 `Library`。`.map` 保留列表长度和原有顺序。 |
| 66 | 结束类体。 |
| 68 | `object Library` 是 `Library` 的伴生对象，提供样例数据。 |
| 69 | `sample` 方法返回带五本书的库；`Library(List(...))` 是构造器调用。 |
| 70 | 构造 1 号《Programming in Scala》，分类“编程”；借阅人省略，默认 `None`。 |
| 71 | 构造 2 号《Scala for the Impatient》，分类“编程”。 |
| 72 | 构造 3 号《How to Design Programs》，分类“函数式”。 |
| 73 | 构造 4 号《Functional Programming in Scala》，分类“函数式”。 |
| 74 | 构造 5 号《The Pragmatic Programmer》，分类“工程”。 |
| 75–76 | 结束列表、方法和对象。 |

借书的变化可写成：

```scala
val before = Library.sample
val result = before.borrow(1, "小明") // Right(新 Library)
val after = result.toOption.get   // 只为演示：这里已知会成功
before.byId(1).get.available      // true：旧书库没变
after.byId(1).get.available       // false：新书库中已借出
```

实际程序用 `match` 处理 `Right`/`Left`，避免随意 `.get`。

## 5. 持久化：`src/main/scala/showcase/Storage.scala`

| 行 | 解释 |
|---|---|
| 1 | 声明 `showcase` 包。 |
| 3 | `import java.nio.file.{Files, Path}` 一次导入两个 Java 类；Scala 可以直接使用 Java 标准库。 |
| 5 | 文档注释描述“一行一本”。 |
| 6 | `object Storage` 是单例工具对象，不需要 `new Storage`。 |
| 7 | `val defaultPath: Path = Path.of("data", "bookshelf.txt")`：默认相对路径。它相对于**运行时工作目录**，通常是项目根目录。`val` 引用不可重新赋值。 |
| 9 | `save(library, path = defaultPath): Unit`：第二个参数有默认值，通常只需传书库；`Unit` 表示不返回有意义的结果，主要效果是写文件。 |
| 10 | `path.getParent` 得父目录。只给纯文件名时可能是 Java 的 `null`。 |
| 11 | 父目录存在时创建目录；`Files.createDirectories` 已有目录时也可正常调用。这里直接与 Java `null` 比较。 |
| 12 | 对每本书调用 `Book.toRecord`，再用换行符连接成文件正文。`map(Book.toRecord)` 是把方法作为函数传入。空列表得到空字符串。 |
| 13 | `Files.writeString` 写入整个文本；非空时补最后一个换行，空时写空串。默认会创建文件或覆盖原文件，并使用 UTF-8。`if (...) ... else ...` 直接放在字符串加法中，是一个表达式。 |
| 14 | 结束保存方法。这里没有捕获 IO 异常，读写失败会向调用方抛出。 |
| 16 | `load` 读取文件；没有显式路径时用默认路径，结果类型是 `Library`。 |
| 17 | 文件不存在就返回五本样例书。 |
| 18 | 文件存在，进入 `else` 块。 |
| 19 | `Files.readString(path)` 一次读完整个文件；默认 UTF-8。 |
| 20 | 用 `\n` 切行；Windows 文本可能是 `\r\n`，后面的 `.trim` 会清掉末尾 `\r`。 |
| 21 | 把数组转成 `List[String]`。 |
| 22 | 去除每行首尾空白。副作用是书名等字段若本来刻意带首尾空白，重新读取时会丢失。 |
| 23 | 去掉空行。 |
| 24 | `.flatMap(Book.fromRecord)`：每行返回 `Option[Book]`，`Some` 展开为一本书，`None` 被跳过。因此格式错误的行会悄悄消失。 |
| 25 | 若解析后的书列表为空，仍返回五本样例书；否则用读到的书建库。**这意味着用户删光全部书并保存空文件，重启后会重新出现五本样例书。** |
| 26–28 | 结束 `else`、`load` 和 `Storage` 对象。 |

当前 `data/bookshelf.txt` 有五行。例如第一行末尾的 `|` 表示第六个字段，即借阅人，是空的。文件没有标题行。

## 6. 交互程序：`src/main/scala/showcase/BookShelfApp.scala`

### 6.1 对象、菜单与主循环（1–41 行）

| 行 | 解释 |
|---|---|
| 1 | 位于 `showcase` 包。 |
| 3 | `import scala.io.StdIn.readLine` 只导入 `readLine`，后面可直接调用。 |
| 5–11 | 文档注释列出自身入口和运行命令。 |
| 12 | `object BookShelfApp`：整个程序的单例控制器。 |
| 14 | `runInteractive(): Unit` 进入键盘交互模式。空括号说明没有参数；返回 `Unit`。 |
| 15 | 打印横幅；方法定义在 141–146 行。 |
| 16 | `var lib = Storage.load()`：加载文件或样例。`var` 可重新赋值，后续操作把新 `Library` 放入这个变量。 |
| 17 | `s"...${...}..."` 字符串插值，显示书本数和默认路径。表达式较长时用 `${...}`。 |
| 18 | 显示操作提示。 |
| 20 | `var running = true` 是菜单循环的开关。 |
| 21 | `while (running)` 每轮先检查条件，为真就执行花括号内的动作。`while` 用于反复进行有副作用的键盘交互。 |
| 22 | 每轮重新打印菜单。 |
| 23 | `readLine(">> ")` 打提示并读取整行；`.trim` 清首尾空格；`match` 按输入选项分支。若标准输入结束而 `readLine` 返回 `null`，这里的 `.trim` 会抛异常。 |
| 24 | 选 1：直接打印全部书。`printBooks` 定义在 156 行。 |
| 25 | `case "2" =>` 匹配查看借阅状态的菜单项。一个 `case` 后可以连续写多个表达式。 |
| 26 | 打印 `lib.availableOnly`，即在架书。 |
| 27 | 打印 `lib.borrowedOnly`，即已借书。 |
| 28 | `case "3" =>` 匹配搜索菜单项。 |
| 29 | `readLine` 读取关键词，绑定到局部常量 `q`。 |
| 30 | 调用 `lib.find(q)` 搜索并打印结果；`$q` 插入标题。 |
| 31 | `case "4" =>` 匹配添加菜单项。 |
| 32 | 读取书名，绑定到 `title`。 |
| 33 | 读取作者，绑定到 `author`。 |
| 34 | 读取年份并尝试转整数；`toIntOption.getOrElse(2026)` 在输入不是整数时静默采用 **2026**，这是硬编码值，不会自动更新。 |
| 35 | 读取分类，绑定到 `category`。 |
| 36 | 只检查书名去空白后是否为空；空则打印错误。Scala 允许省略 `if` 分支外围花括号，只要后面是一条表达式。 |
| 37 | `else {` 开始书名有效时执行的代码块。 |
| 38 | `lib.add(...)` 产生新库，再赋给可变变量 `lib`；旧库对象仍不变。 |
| 39 | 立即把新库保存到默认文件。 |
| 40 | 从新列表最后一本取得编号并打印；因为 `add` 已追加一本，所以 `.last` 有值。 |
| 41 | 结束 `else` 块。 |

### 6.2 借出、归还、删除、退出（42–92 行）

| 行 | 解释 |
|---|---|
| 42 | `case "5" =>` 匹配借书菜单项。 |
| 43 | 读取编号并用 `toIntOption` 转成 `Option[Int]`，所以能区分无效输入。 |
| 44 | 读取借阅人姓名。 |
| 45 | 对编号做第一次 `match`。 |
| 46 | `None` 说明不是合法整数，打印提示。 |
| 47 | `Some(n)` 解包整数编号。 |
| 48 | 调用 `lib.borrow(n, who)`；结果是 `Either`，再做第二次匹配。 |
| 49 | `Left(err)`：业务规则拒绝借阅，只显示错误，不改变或保存书库。 |
| 50 | `Right(next)` 解包成功的新书库。 |
| 51 | 把成功结果赋给当前 `lib`。 |
| 52 | 立刻保存新库。 |
| 53 | 显示借出成功。 |
| 54–55 | 结束两个嵌套的 `match`。 |
| 56 | `case "6" =>` 匹配归还菜单项。 |
| 57 | 读取归还编号并尝试转整数，直接对 `Option[Int]` 做 `match`。 |
| 58 | 非整数时打印错误。 |
| 59 | `Some(n)` 解包数字编号。 |
| 60 | 调用 `giveBack(n)` 并匹配成功或错误。 |
| 61 | 归还失败时仅显示错误。 |
| 62 | `Right(next)` 表示归还成功。 |
| 63 | 更新当前 `lib`。 |
| 64 | 保存新库。 |
| 65 | 打印归还成功。 |
| 66–67 | 结束内外两次匹配。 |
| 68 | `case "7" =>` 匹配删除菜单项。 |
| 69 | 读取删除编号并转为 `Option[Int]`，随后匹配。 |
| 70 | 非整数则显示错误。 |
| 71 | `Some(n)` 解包编号。 |
| 72 | 调用 `lib.remove(n)`，再匹配 `Either` 结果。 |
| 73 | `Left`：不存在这本书。 |
| 74 | `Right(next)` 表示删除成功。 |
| 75 | 用新库替换当前状态。 |
| 76 | 保存新库。 |
| 77 | 打印删除成功。 |
| 78–79 | 结束内外两次匹配。 |
| 80 | 选 8：打印 `lib.stats`；它是无参数方法，可省略括号。 |
| 81 | `case "9" =>` 匹配主动保存菜单项。此前成功的增删借还已经自动保存过。 |
| 82 | 保存当前库。 |
| 83 | 显示默认数据文件的绝对路径。 |
| 84 | `"q" | "Q" | "0"` 是三个可退出的模式。 |
| 85 | 退出前再次保存。 |
| 86 | 打印告别文字。 |
| 87 | 将循环开关设为 `false`，下次检查条件时循环结束。 |
| 88 | `case other` 捕获任意未匹配输入，同时绑定到变量 `other`。它类似 `case _`，但保留原值。 |
| 89 | 把这个未知输入显示给用户。 |
| 90–92 | 依次结束选项匹配、`while` 循环、交互方法。 |

交互模式的一次成功借书，真实调用链是：

```text
输入「5」→ 输入编号「1」→ Some(1)
→ Library.borrow(1, 姓名) → Right(新的 Library)
→ lib = next → Storage.save(lib)
→ 每本书由 Book.toRecord 转成一行 → 写入 data/bookshelf.txt
```

失败时停在 `Left(err)` 分支，不修改 `lib`，也不写文件。

### 6.3 自动预览（94–139 行）

| 行 | 解释 |
|---|---|
| 94 | 注释说明预览目的。当前预览实际演示添加、搜索、借出、借出失败、归还、统计和读写文件；**没有执行删除**，注释中“增删借还”的“删”与代码不一致。 |
| 95 | `preview(): Unit` 定义无键盘演示方法。 |
| 96 | 打印横幅。 |
| 97 | 打印预览提示。 |
| 98 | `println()` 输出空行。 |
| 100 | 用五本样例书作为预览初始状态，不读取交互模式的数据文件。 |
| 101 | 打印第 1 步标题。 |
| 102 | 调用 `printBooks` 输出五本样例。 |
| 104 | 添加第六本书，把新 `Library` 赋给 `lib`。由于原编号最大是 5，新编号是 6。 |
| 105 | 打印第 2 步标题。 |
| 106 | 打印新书的格式化显示行；`"   " + ...` 是字符串连接。 |
| 108 | 打印第 3 步标题。 |
| 109 | 搜索 `Scala` 并打印结果；它匹配书名，结果是 1、2、4、6 号四本。 |
| 111 | 打印第 4 步标题。 |
| 112 | 尝试把 1 号书借给小明，并对 `Either` 结果做模式匹配。 |
| 113 | `Right(next)` 解包成功时的新书库。 |
| 114 | 把它放入当前 `lib`。 |
| 115 | 按编号找书；`.map(_.line)` 把 `Option[Book]` 变成 `Option[String]`；`getOrElse("")` 在找不到时显示空串，然后打印。 |
| 116 | `Left(err)` 时打印错误；样例中 1 号存在且可借，所以不会走这个分支。 |
| 117 | 结束第一次借书的匹配。 |
| 119 | 打印第 5 步标题，说明要再次借同一本。 |
| 120 | 再次调用 `borrow`，验证“已借出”规则。 |
| 121 | 预期是 `Left(err)`，打印错误说明。 |
| 122 | 若意外返回 `Right`，打印“出错了”；`Right(_)` 表示不关心新库的具体值。 |
| 123 | 结束第二次借书的匹配。 |
| 125 | 打印第 6 步标题。 |
| 126 | `giveBack(1).getOrElse(lib)`：`Right` 时拿新库，`Left` 时保留旧库；这里没有显示归还错误。 |
| 127 | 打印 1 号书的归还后状态。 |
| 129 | 打印第 7 步标题。 |
| 130 | 显示统计。归还后 6 本都在架，借出 0 本。 |
| 132 | 建立预览专用的 `target/bookshelf-preview.txt` 路径；使用 Java 类的全名，所以本文件不需要另写 `import`。 |
| 133 | 把当前库写入该路径。 |
| 134 | 再读回来，用于验证基本的往返转换。 |
| 135 | 打印读回的本数；样例运行读回 6 本。 |
| 136 | 打印预览文件的绝对路径。 |
| 137 | 打印空行。 |
| 138 | 提示用户怎样进入交互模式；字符串里的 `\"` 用于显示引号。 |
| 139 | 结束预览方法。 |

### 6.4 输出辅助方法与独立入口（141–170 行）

| 行 | 解释 |
|---|---|
| 141 | `private def banner(): Unit`：只在这个对象内部使用的横幅方法。 |
| 142 | `"=" * 56` 是 Scala 字符串重复操作，得到 56 个等号。 |
| 143 | 打印程序标题。 |
| 144 | 打印程序说明。 |
| 145 | 再打印 56 个等号作为分隔线。 |
| 146 | 结束横幅方法。 |
| 148 | `private def printMenu()`：内部菜单打印方法。 |
| 149 | 先打印空行。 |
| 150 | 打印菜单中的选项 1、2、3。 |
| 151 | 打印菜单中的选项 4、5、6。 |
| 152 | 打印菜单中的选项 7、8、9。 |
| 153 | 打印退出选项。这些数字与上面的 `match` 分支一一对应。 |
| 154 | 结束菜单方法。 |
| 156 | `printBooks(title: String, books: List[Book])`：传入标题和书列表；`private` 限制为本对象内使用。 |
| 157 | 打印标题和本数；`${books.size}` 是插值表达式。 |
| 158 | 空列表时打印“没有书”。 |
| 159 | 非空则按编号 `.sortBy(_.id)` 排序，`.foreach(b => println(...))` 逐本打印。`foreach` 用于副作用，返回 `Unit`；`map` 通常用于生成新列表。 |
| 160–161 | 结束 `printBooks` 和 `BookShelfApp` 对象。 |
| 163 | 文档注释说明另一个入口。 |
| 164 | `@main` 让下方顶层 `bookShelf` 方法可被直接启动。 |
| 165 | `bookShelf(args: String*): Unit` 接收可变数量参数。 |
| 166 | 只取第一个参数并转小写，然后匹配。无参数时是 `None`。 |
| 167 | 参数为 `preview`，运行自动预览。 |
| 168 | 其他情况，包括无参数，运行交互模式。 |
| 169–170 | 结束 `match` 和方法。 |

项目有**两个**入口：默认的 `main` 先做课程/书架分流；`showcase.bookShelf` 可直接启动书架。`sbt "run preview"` 和 `sbt "runMain showcase.bookShelf preview"` 最终都进入 `BookShelfApp.preview()`。

## 7. 把相关 Scala 语法串起来

| 语法 | 此项目的例子 | 怎样理解 |
|---|---|---|
| `val` | `val book = ...` | 名字只绑定一次；对象内部也建议尽量用不可变数据。 |
| `var` | `var lib = Storage.load()` | 名字可重新赋值；这里代表正在运行的当前状态。 |
| 类型标注 | `id: Int`、`: Library` | 冒号后是类型；方法参数必须写类型，局部变量通常可推断。 |
| `def` | `def stats: String = ...` | 定义方法；无参数且无副作用的查询常写成不带 `()`。 |
| 块返回值 | `val x = ...; copy(...)` | 花括号里最后一个表达式是整个块的值。 |
| `case class` | `Book`、`Library` | 适合表示数据；自动给 `copy`、值比较、构造与解构等。 |
| `object` | `Storage`、`Book` | 单例；可存放程序入口或工具方法。与同名 `case class` 组成伴生关系。 |
| 默认参数 | `borrower = None`、`path = defaultPath` | 调用方可省略该参数。 |
| 命名参数 | `copy(books = ...)` | 明确只要覆盖哪个参数，其他字段沿用旧值。 |
| `Option[A]` | `Option[Book]` | 值可能不存在；`Some(x)` 有值，`None` 无值。通常通过 `match`、`map`、`getOrElse` 处理。 |
| `Either[E,A]` | `Either[String, Library]` | 业务操作可能失败；`Left` 错误，`Right` 成功。这里避免用异常处理普通业务失败。 |
| `match` | `lib.borrow(...) match { ... }` | 按形状分支，可以解包 `Some`、`Right`、元组或列表。 |
| 守卫 | `case Some(book) if book.available =>` | 模式匹配成功后再检查额外布尔条件。 |
| `case _` | 文件格式不合时 | 通配任意值但不保存它；`case other` 则会保存值供后面使用。 |
| `=>` | `b => b.id`、`case None =>` | 在 lambda 中分隔参数和函数体，在 `case` 中分隔模式和分支体。 |
| `_` 占位 | `books.map(_.id)` | 这里等同 `books.map(b => b.id)`；不同位置的 `_` 含义要结合上下文看。 |
| 不可变 `List` | `Nil`、`books :+ book` | 列表更新返回新列表；`:+` 用于尾部追加。 |
| `map` | `books.map(_.id)` | 一项变一项，得到新容器；`Option.map` 也遵守这个思路。 |
| `flatMap` | `.flatMap(Book.fromRecord)` | 一项可变成零项或一项，适合丢掉解析失败的行。 |
| `filter` / `filterNot` | `books.filter(_.available)` | 根据布尔条件保留 / 排除元素。 |
| `find` / `exists` | `books.find(...)` / `fields.exists(...)` | 前者取第一项并包成 `Option`，后者只判断有没有符合者。 |
| `groupBy` / `sortBy` | 按分类统计 | 先分组，再按指定键排序。 |
| `s` / `f` 插值 | `s"借给 $who"` / `f"$id%3d"` | `s` 做表达式插入；`f` 还能设置格式宽度和类型。 |
| `@main` | `def bookShelf(...)` | Scala 3 的可执行入口注解。 |

注意：表中 `books :+ book` 的符号是 `:+`（冒号在前、加号在后）。它对不可变 `List` 通常需要走到尾部，书很多时反复尾插不是高效写法；这个小项目数据量少，易读性更重要。

## 8. 一次完整运行的状态变化

预览模式（已用本机的 IntelliJ 自带 sbt launcher 成功编译并运行）按以下顺序发生：

1. `Library.sample` 建立编号 1–5 的五本在架书。
2. `add` 用 `nextId` 得 6，返回六本书的新库，旧库仍是五本。
3. `find("Scala")` 取得编号 1、2、4、6 四本。
4. `borrow(1, "小明")` 返回 `Right(新库)`；1 号书的 `borrower` 从 `None` 变成 `Some("小明")`。
5. 再借 1 号，返回 `Left("已经借给……")`，当前库仍保持原状态。
6. `giveBack(1)` 把借阅人重新设为 `None`；统计为总数 6、在架 6、借出 0。
7. 保存至 `target/bookshelf-preview.txt`，再读取，得到 6 本。预览不改 `data/bookshelf.txt`。

交互模式则从 `Storage.load()` 开始。当前文件是五本样例；每次添加、借出、归还、删除成功后都会立即保存到 `data/bookshelf.txt`。

## 9. 读懂之后要知道的边界

这些是当前实现的真实行为，不影响作为学习项目使用：

1. **删光后重启会恢复五本样例。**原因在 `Storage.load` 第 25 行：读到空列表就返回 `Library.sample`。
2. **坏数据可能被静默改变或忽略。**`Book.fromRecord` 会把坏编号变成 0；坏年份或字段数不对的行会被 `flatMap` 忽略。
3. **文本格式不能安全保存竖线或换行。**书名若含 `|`，下次读入就会被切成多字段；字段含换行也会变成多条记录。
4. **输入和 IO 异常没有集中处理。**例如用户在提示符处发送 EOF，或文件无写权限，程序可能抛异常结束。
5. **年份输入不合法会采用固定的 2026。**这不是当前年份计算，也不会提示用户输入出错。
6. **重复编号、删除已借出的书均未限制。**正常交互不会自动制造重复编号，但手工编辑文件可以；`remove` 也不检查借阅状态。
7. **样例预览没有测试删除。**尽管注释说“增删借还”，实际代码只演示增、借、还等步骤。

## 10. 建议亲手做的五个小练习

1. 在 IDE 中给 `Library.borrow` 第 24 行和 `Storage.save` 第 12 行设断点，借出 1 号书，观察 `None → Some(姓名)`、`Left/Right`、旧 `lib` 与 `next` 的区别。
2. 不改源码，分别在交互界面输入 `5`、编号 `999`，以及给 `1` 号书输入空姓名，预测每次会出现哪个 `Left`。
3. 手算 `Library.sample.add("X", "Y", 2024, "编程").stats` 的输出，再运行预览对照。
4. 查看保存文件最后一个 `|`；借一本书后再看第六个字段如何变化，归还后如何恢复。
5. 尝试设计“真正允许空书库”的规则：只在**文件不存在**时加载样例；文件存在但为空时返回 `Library(Nil)`。先说明预期，再考虑改代码。

### 如何运行

项目要求 Scala 3.3.8 与 sbt。若命令行能找到 `sbt`：

```powershell
sbt "run preview"                      # 自动预览，写 target/bookshelf-preview.txt
sbt "run demo"                         # 交互模式，读写 data/bookshelf.txt
sbt "runMain showcase.bookShelf preview" # 直接走书架入口
```

当前机器的 PowerShell 没有全局 `sbt` 命令，但 IntelliJ 自带的 sbt launcher 已成功执行预览。也可直接在 IntelliJ 打开 `BookShelfApp.scala`，运行第 164 行的 `@main def bookShelf`。
