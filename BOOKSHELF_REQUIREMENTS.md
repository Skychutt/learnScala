# BookShelf 需求说明

命令行个人图书馆。你自己实现时，建议仍放在 `package showcase`，分成下面四个文件。原实现可以当对照，先按这份说明写，写完再对比。

建议写法（和课程后半对齐）：

- 数据用 `case class`，不要用 `null`，没有值用 `Option`
- 书库内部不改旧列表，增删借还都返回一份新的 `Library`
- 会失败的操作返回 `Either[String, Library]`：`Left` 是错误说明，`Right` 是新书库
- 当前正在用的那一份书库，用 `var` 接住每次返回的新值

运行方式（写完后应能这样跑）：

```text
sbt "runMain showcase.bookShelf"          交互菜单
sbt "runMain showcase.bookShelf preview"  自动演示，不读键盘
```

---

## 整体分工

```text
键盘 / 命令行参数
        │
        ▼
BookShelfApp          菜单、输入、打印、预览
        │
        ├── Library   增删借还、搜索、统计
        │       │
        │       └── Book   一本书，以及一行文本怎么转成书
        │
        └── Storage   把 Library 存成文本，再读回来
```

| 文件 | 类型 | 职责 |
|------|------|------|
| `model.scala` | `case class Book` + `object Book` | 一本书的数据；屏幕上一行怎么显示；文件里一行怎么读写 |
| `Library.scala` | `case class Library` + `object Library` | 藏书列表上的业务规则 |
| `Storage.scala` | `object Storage` | 读写 `data/bookshelf.txt` |
| `BookShelfApp.scala` | `object BookShelfApp` + `@main def bookShelf` | 菜单循环和自动演示 |

---

## 1. `Book`（`model.scala`）

一本书。`borrower` 为 `None` 表示在架，`Some(姓名)` 表示已借出。

### 字段

| 字段 | 类型 | 说明 |
|------|------|------|
| `id` | `Int` | 编号，由书库分配，不由用户输入 |
| `title` | `String` | 书名 |
| `author` | `String` | 作者 |
| `year` | `Int` | 出版年份 |
| `category` | `String` | 分类，例如「编程」 |
| `borrower` | `Option[String]` | 默认 `None`。这样 `Book(id, title, author, year, category)` 可以不传借阅人 |

### 实例方法

| 方法 | 返回 | 行为 |
|------|------|------|
| `available` | `Boolean` | 没有借阅人时为 `true` |
| `statusText` | `String` | 在架时返回 `在架`；已借出时返回 `借给 姓名` |
| `line` | `String` | 屏幕上的一行。编号、书名、作者、年份、分类、状态排成一行，书名和作者左对齐并留出固定宽度，方便上下对齐 |

### 伴生对象 `object Book`

文件格式固定为六个字段，用 `|` 分隔：

```text
id|title|author|year|category|borrower
```

在架的书最后一栏为空，例如：

```text
1|Programming in Scala|Odersky|2021|编程|
2|Scala for the Impatient|Horstmann|2022|编程|小明
```

| 方法 | 签名与行为 |
|------|------------|
| `toRecord` | `(book: Book): String`。按上面的格式拼成一行。`None` 写成空字符串 |
| `fromRecord` | `(raw: String): Option[Book]`。一行转回一本书。字段不是 6 个，或年份不是整数，返回 `None`。编号不是整数时，用 `0`。最后一栏为空则 `borrower` 为 `None`，否则为 `Some(那一栏的文字)` |

注意：按 `|` 切开时，最后一栏即使是空的也要保留，否则在架的书会少一个字段。

---

## 2. `Library`（`Library.scala`）

不可变藏书库。方法不修改自己，成功时用 `copy` 得到新的 `Library`。

### 字段

| 字段 | 类型 | 说明 |
|------|------|------|
| `books` | `List[Book]` | 默认 `Nil`。`Library()` 就是空书库 |

### 实例方法

| 方法 | 签名 | 行为 |
|------|------|------|
| `nextId` | `Int` | 现有编号的最大值加 1。一本都没有时，下一号是 `1`。删掉最大编号后，这个号可以再用 |
| `byId` | `(id: Int): Option[Book]` | 按编号找书。找不到是 `None` |
| `add` | `(title, author, year, category): Library` | 四个参数类型依次是 `String, String, Int, String`。用 `nextId` 建一本在架的新书，文字先 `trim`，追加到列表末尾。这里不检查书名是否为空（空书名由菜单拦） |
| `remove` | `(id: Int): Either[String, Library]` | 没有这本书：`Left("没有编号 n 的书")`。有：去掉它，返回新书库。已借出的书也允许删 |
| `borrow` | `(id: Int, who: String): Either[String, Library]` | 见下面的顺序 |
| `giveBack` | `(id: Int): Either[String, Library]` | 见下面的顺序 |
| `find` | `(keyword: String): List[Book]` | 在书名、作者、分类里做不区分大小写的包含搜索。关键词 `trim` 后为空，返回全部。不搜年份和借阅人 |
| `availableOnly` | `List[Book]` | 只要在架的 |
| `borrowedOnly` | `List[Book]` | 只要已借出的 |
| `stats` | `String` | 见下面的格式 |
| `replace` | `private (updated: Book): Library` | 用同一编号的新书换掉旧书，其余书保持原顺序。`borrow` 和 `giveBack` 内部用它 |

`borrow` 按这个顺序判断：

1. 没有这个编号 → `Left("没有编号 n 的书")`
2. 已经借出 → `Left("《书名》已经借给 姓名")`（即使这次姓名也是空的，也先报已借出）
3. 姓名 `trim` 后为空 → `Left("借阅人姓名不能为空")`
4. 否则把借阅人设成 `Some(trim 后的姓名)`，返回新书库

`giveBack`：

1. 没有这个编号 → `Left("没有编号 n 的书")`
2. 本来就在架 → `Left("《书名》本来就在架上")`
3. 否则把借阅人设回 `None`，返回新书库

`stats` 的文字格式：

```text
共 5 本，在架 4 本，借出 1 本。分类：函数式 2 本，工程 1 本，编程 2 本
```

分类按分类名的字符串顺序排列，分类之间用中文逗号 `，`。空书库时分类那一段是空的：`共 0 本，在架 0 本，借出 0 本。分类：`。

### 伴生对象 `object Library`

| 方法 | 行为 |
|------|------|
| `sample` | 返回下面这 5 本，全部在架。文件不存在或文件里一本合法的书都没有时，用这份样例 |

| id | title | author | year | category |
|----|-------|--------|------|----------|
| 1 | Programming in Scala | Odersky | 2021 | 编程 |
| 2 | Scala for the Impatient | Horstmann | 2022 | 编程 |
| 3 | How to Design Programs | Felleisen | 2018 | 函数式 |
| 4 | Functional Programming in Scala | Chiusano | 2023 | 函数式 |
| 5 | The Pragmatic Programmer | Hunt | 2019 | 工程 |

---

## 3. `Storage`（`Storage.scala`）

单例，不保存书库本身，只负责文件。用 `java.nio.file.Files` 和 `Path`，不引入第三方库。

### 字段

| 字段 | 类型 | 说明 |
|------|------|------|
| `defaultPath` | `Path` | `data/bookshelf.txt`，相对项目根目录 |

### 方法

| 方法 | 签名 | 行为 |
|------|------|------|
| `save` | `(library: Library, path: Path = defaultPath): Unit` | 父目录不存在就创建。每本书用 `Book.toRecord` 写成一行，行之间用换行。有书时末尾再加一个换行；一本都没有时写成空文件 |
| `load` | `(path: Path = defaultPath): Library` | 文件不存在：返回 `Library.sample`。存在：按行 `trim`，丢掉空行，用 `Book.fromRecord` 解析，解析失败的行丢掉。解析完一本都没有：也返回 `Library.sample`。否则 `Library(解析出的列表)` |

交互模式读写默认路径。预览模式另存到 `target/bookshelf-preview.txt`，不要覆盖用户的 `data/bookshelf.txt`。

---

## 4. `BookShelfApp`（`BookShelfApp.scala`）

菜单和演示。书库状态是一个 `var lib`：操作成功后把返回的新 `Library` 赋回去，并立刻 `Storage.save`。

### 方法

| 方法 | 行为 |
|------|------|
| `runInteractive(): Unit` | 交互菜单，见下一节 |
| `preview(): Unit` | 自动演示，见再下一节 |
| `banner(): Unit` | `private`。打印标题框：一行等号，一行程序名「BookShelf 命令行个人图书馆」，一行简介，再一行等号 |
| `printMenu(): Unit` | `private`。打印 1–9 和退出说明 |
| `printBooks(title: String, books: List[Book]): Unit` | `private`。先打 `── 标题（本数）──`。空列表打 `（没有书）`。否则按 `id` 排序，每本打 `Book.line` |

同一文件里再写程序入口：

```scala
@main
def bookShelf(args: String*): Unit
```

第一个参数忽略大小写后是 `preview` 就跑 `preview()`，否则跑 `runInteractive()`。

### 交互菜单

启动时：

1. 打 banner
2. `lib = Storage.load()`
3. 打印已载入本数，以及数据文件路径
4. 提示输入数字，`q` 退出
5. `while` 循环，每次先 `printMenu`，再读一行

| 输入 | 行为 |
|------|------|
| `1` | 列出全部，标题「全部藏书」 |
| `2` | 先列「在架」，再列「已借出」 |
| `3` | 读关键词，标题为 `搜索「关键词」`，调用 `find` |
| `4` | 依次读书名、作者、年份、分类。书名 `trim` 后为空：打印 `书名不能为空。`，不改书库。年份不是整数：当成 `2026`。成功后保存，并打印 `已添加，编号 n。`（`n` 是刚加进去那本的编号） |
| `5` | 读编号和借阅人。编号不是整数：`编号必须是数字。`。然后 `borrow`：`Left` 就打印错误原文；`Right` 就换上新书库、保存、打印 `借出成功。` |
| `6` | 读编号。不是整数：`编号必须是数字。`。`giveBack` 成功则保存并打印 `已归还。` |
| `7` | 读编号。不是整数：`编号必须是数字。`。`remove` 成功则保存并打印 `已删除。` |
| `8` | 打印 `lib.stats` |
| `9` | 立刻保存，并打印绝对路径 |
| `0` / `q` / `Q` | 保存，打印 `已保存，再见。`，结束循环 |
| 其他 | 打印 `不认识的选项：` 加上用户输入的原文 |

读输入用 `scala.io.StdIn.readLine`。

### 预览模式 `preview`

不读键盘，按这个顺序做，方便你对照输出：

1. banner，并说明这是预览、不读键盘
2. 从 `Library.sample` 开始（不要读 `data/bookshelf.txt`），打印全部
3. `add("Scala 3 小册", "Anonymous", 2024, "编程")`，打印新书那一行
4. `find("Scala")`，打印结果
5. `borrow(1, "小明")`，成功则打印 1 号书的新行
6. 再 `borrow(1, "小红")`，应当失败，打印那条错误
7. `giveBack(1)`，打印归还后的 1 号书
8. 打印 `stats`
9. 保存到 `target/bookshelf-preview.txt`，再 `load` 回来，打印读回后的本数和文件绝对路径

---

## 建议书写顺序

1. `Book` 的字段、`available`、`statusText`、`line`
2. `Book.toRecord` / `Book.fromRecord`，先在工作表里拿一两行字符串试解析
3. `Library.sample`、`nextId`、`byId`、`add`
4. `remove`、`borrow`、`giveBack`（先把 `Either` 的四种失败写对）
5. `find`、`availableOnly`、`borrowedOnly`、`stats`
6. `Storage.save` / `Storage.load`
7. `preview()`，不碰键盘就能看出业务对不对
8. 最后写 `runInteractive()` 和 `@main`

## 写完后自己对一下

- 空书库的下一编号是 1；已有 1 和 5 时，下一编号是 6
- 同一本书不能借两次；在架的书不能归还
- 借阅人姓名只有空格时，借出失败
- 搜索 `scala` 能命中书名里的 `Scala`，命中作者或分类也同样算
- 文件最后一栏为空，读回来是在架；坏行被跳过，不会让整个文件加载失败
- 文件不存在时，交互模式看到的是那 5 本样例
- 预览存盘不会改掉 `data/bookshelf.txt`
