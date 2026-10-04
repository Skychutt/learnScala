# Scala 基础语法课程

面向初学者的 **Scala 3** 可运行教程，内容对齐 UCD *Multi-Paradigm Programming* 讲义（Chapter 1–2 的 IO / 字符串 / 包），并补全后面会用到的集合、面向对象、模式匹配等基础。

当前工程：`Scala 3.3.8` + `sbt`。

## 怎么运行

在 IntelliJ 里运行 `src/main/scala/main.scala`，或在项目根目录：

```bash
sbt run                 # 按顺序运行全部 22 课
sbt "run 5"             # 只看第 5 课
sbt "run 21 22"         # 连续看包和 IO
sbt "run preview"       # 小项目自动演示（不需要键盘）
sbt "run demo"          # 小项目交互式图书馆（要键盘）
sbt "run help"          # 打印目录
```

学习方式：先打开对应 `.scala` 把注释读完 → `sbt "run N"` 看输出 → 做课末练习。

## 课程目录

| 课 | 文件 | 内容 |
|---:|------|------|
| 01 | `lessons/Lesson01_Hello.scala` | 入口、注释、打印、表达式 |
| 02 | `lessons/Lesson02_Variables.scala` | `val` / `var` / `lazy val` |
| 03 | `lessons/Lesson03_Types.scala` | 基本类型与类型层级 |
| 04 | `lessons/Lesson04_Operators.scala` | 运算符、短路、运算符即方法 |
| 05 | `lessons/Lesson05_Strings.scala` | 字符串、s/f/raw 插值（对应讲义 Ch.2 前半） |
| 06 | `lessons/Lesson06_IfElse.scala` | `if` 表达式、没有 else 是 Unit |
| 07 | `lessons/Lesson07_Loops.scala` | `while` / `for` / `yield` |
| 08 | `lessons/Lesson08_Methods.scala` | 方法 `def` |
| 09 | `lessons/Lesson09_Functions.scala` | 函数值、lambda、高阶函数 |
| 10 | `lessons/Lesson10_Collections.scala` | 元组、List、Map 等 |
| 11 | `lessons/Lesson11_CollectionOps.scala` | `map` / `filter` / `fold` |
| 12 | `lessons/Lesson12_Classes.scala` | 类与构造器 |
| 13 | `lessons/Lesson13_Objects.scala` | 单例、伴生对象、`apply` |
| 14 | `lessons/Lesson14_Inheritance.scala` | 继承、抽象类、`sealed` |
| 15 | `lessons/Lesson15_Traits.scala` | 特质 |
| 16 | `lessons/Lesson16_CaseClassEnum.scala` | case class 与 enum |
| 17 | `lessons/Lesson17_PatternMatching.scala` | 模式匹配 |
| 18 | `lessons/Lesson18_OptionAndError.scala` | Option / Either / Try |
| 19 | `lessons/Lesson19_GenericsAndFor.scala` | 泛型与 for 推导式 |
| 20 | `lessons/Lesson20_Scala3.scala` | 扩展方法、given/using |
| 21 | `lessons/Lesson21_Packages.scala` | 包与 import（对应讲义 Ch.1 末） |
| 22 | `lessons/Lesson22_IO.scala` | 控制台输入、文件读写（对应讲义 Ch.2 后半） |

每课结构固定：**为什么学 → 分节演示 → 易错点 / 对照 Java → 小结 → 练习**。

## 小项目：BookShelf 命令行图书馆

源码在 `src/main/scala/showcase/`。

| 文件 | 作用 | 用到的语法 |
|------|------|------------|
| `model.scala` | `Book` 数据 | case class、Option、字符串插值 |
| `Library.scala` | 增删借还、搜索、统计 | 不可变 List、Either、高阶函数 |
| `Storage.scala` | 读写 `data/bookshelf.txt` | `java.nio.file.Files` |
| `BookShelfApp.scala` | 菜单循环 | `StdIn.readLine`、`match` |

功能：列出 / 搜索 / 添加 / 借出 / 归还 / 删除 / 统计 / 自动存盘。

```bash
sbt "run preview"    # 先看一遍自动演示
sbt "run demo"       # 自己操作，数据存在 data/bookshelf.txt
```

## 学习顺序

- **第 1–9 课 + 21**：对应老师 Chapter 1（再多讲了一点集合前的准备）
- **第 5 课 + 22**：对应老师 Chapter 2（字符串、输入输出、文件）
- **第 10–20 课**：课程后面会用到的基础，提前铺开
- **showcase**：把上面串成一个能用的小程序

## 贯穿始终的习惯

1. 能用 `val` 就不用 `var`
2. 不要返回 `null`，用 `Option`
3. 数据用 `case class` / `enum`，能力用 `trait`
4. 能写 `map` / `filter` 就少写 `while`
5. 分支一复杂就上 `match`
6. 读文件用完要 `close`（或用 `Using` / `Files.readString`）
