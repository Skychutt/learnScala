# Scala 期末编程题完整参考实现

依据 `previuos test/2025.Final.COMP3038J.Programming.Exam.students.pdf` 第 4–8 页，实现第 1 题、第 2 题所有部分及全部 7 项 Bonus。只使用 Scala/JDK 标准库，包名为 `programming_exam_2025`，与现有课程代码隔离。

## 文件与题目对应

- `Domain.scala`：Q1（40%）全部数据模型及构造验证；Q2(a)（10%）金额加减、汇率单例；Q2(b)（10%）订单状态、增删商品、数量调整、指定币种总价。
- `Store.scala`：Q2(c)（10%）商店单例，管理客户、商品、库存和订单。内部状态可修改，查询返回不可变快照。
- `DataLoader.scala`：Q2(d)（20%）四种 CSV 加载方法；每条错误直接用 println 输出原因，跳过错误记录并继续加载。
- `Main.scala`：Q2(e)（10%）初始化与数据加载；全部 7 个 Bonus 表达式，代码前保留对应英文题目注释。
- `SolutionChecks.scala`：单独运行的验证入口，无需额外测试框架。
- `sample-output.txt`：使用提供的四个 CSV 实际运行得到的输出。

## 运行

DataLoader 已改成适合逐步学习的版本：每个方法独立使用 while 循环、明确的字段变量和 try/catch/finally。建议按 loadStock → loadItems → loadCustomers → loadOrders 的顺序阅读。根据第二章和第三章讲义，加载结果使用 List，Map 使用 + (key -> value) 更新，异常使用 try/catch/finally。这里只修改局部变量，不修改领域对象，也不在加载器里写入 Store。Store 同样展开成普通循环，并使用 private var 保存不可变 Map。

在 IntelliJ 中打开现有 `learnScala` 项目，运行 `Main.scala` 中的 `programming_exam_2025.Main.main`。默认 working directory 设为 learnScala 项目根目录。若从本题目录运行，请把 "previuos test/2025" 作为第一个程序参数传入；主程序不再自动搜索目录。

如果本机有 sbt，在 `/Users/chu_tz/IdeaProjects/learnScala` 下执行：

```sh
sbt 'runMain programming_exam_2025.Main'
sbt 'runMain programming_exam_2025.SolutionChecks'
```

也可以把 CSV 所在目录作为第一个程序参数传入。路径含空格时，在 IntelliJ 的 Program arguments 中用双引号包住完整路径。

主程序每次开始都会清空 Store 后重新加载数据，因此重复运行不会重复导入。测试入口执行完也会清空 Store。

## 题目与数据的解释

1. **年龄分级名称**：PDF 使用 `All / Adult / Restricted`，实际 CSV 使用 `All / AdultOnly / AdultApproval`。本实现假设 `AdultOnly = Adult`、`AdultApproval = Restricted`，两套名称都可以读取。这是为兼容所给数据作出的映射，PDF 未明确说明别名。
2. **Adult 年龄边界**：严格按 PDF 的 “over the age of 18” 实现为周岁 `> 18`；Restricted 为 `>= 15`。如果老师实际希望 Adult 为 18 岁及以上，把 `AgeRating.allows` 中的 `age > 18` 改为 `age >= 18` 即可。年龄按运行当天计算，验证方法也允许传入固定日期。
3. **金额精度**：使用 `BigDecimal` 和试卷给定的 12 个方向的汇率，反向汇率直接使用原表，不能自行用倒数替代。运算保留全部精度，只在输出时四舍五入到两位小数。金额相加或相减必须币种相同，否则抛出 `IllegalArgumentException`；负数金额可表示差额，商品售价不能为负。
4. **验证方式**：地址和电话的 `create` 方法返回 `Either[String, ...]`，`Left` 表示验证失败，`Right` 包含合法对象。字段均不可变，构造函数及 `copy` 仍有 `require` 防止绕过校验。邮编和电话保留字符串，避免丢失前导零。
5. **CSV 行宽**：订单根据 `Num Items` 读取商品/数量对，接受可变行宽及末尾空列，拒绝超出声明数量的非空字段。同一商品出现多次时合并数量。当前使用 split(",", -1) 读取所给简单 CSV，不处理带引号的字段、字段内部逗号或跨行字段；需要时再添加解析器。
6. **引用完整性**：先加载客户和商品，再加载库存与订单；不存在的客户或商品、负库存、非正订单数量、无效日期/币种/状态均报告错误。CSV 没有物流编号字段，因此加载后的 `trackingId` 为 `None`，可通过 `withTrackingId` 设置。
7. **已有订单与库存**：导入 Processed、Collected、Delivered 历史订单时不再次扣库存。通过 Store 新处理订单时，库存足够则全部扣除并设为 Processed，不足则设为 AwaitingStock 且不扣任何库存。取消 Processed 订单会归还库存，Collected 和 Delivered 不能取消。
8. **不可变订单**：`addItem`、`removeItem`、`updateQuantity`、`updateStatus` 等返回新订单，需要保存返回值。商品修改仅允许 New/AwaitingStock；状态更新按流程约束。`Order.updateStatus` 本身不操作 Store，真实业务的处理与状态流转使用 Store 的方法。

## 关键结果

完整加载：50 位客户、50 件商品、50 条库存、15 个订单。

- 有库存且适合 15 岁以下的商品：30 件。
- 居住在 Ireland 或 China 的客户：26 位。
- ORD001 总价：760.21 CNY（未舍入值 760.20545）。
- 库存总值：228058.50 USD（未舍入值 228058.5018425）。
- 前五库存量：VG014 为 250、ACC235 为 240、ACC222 为 230、VG004 为 220、ACC229 为 215。
- 原始 CSV 没有 AwaitingStock 订单，因此该列表为空、GBP 总价为 0.00。主程序保留原始状态，不为演示而修改数据。

已使用现有本地缓存中的 Scala 3.3.8 编译器直接编译全部答案文件，并运行主程序和验证入口；73 项检查全部通过。没有修改项目的 build.sbt，也没有安装新依赖。

验证包括实际 CSV 加载、独立金额核算、全部汇率方向、年龄生日边界、金额币种限制、订单不可变更新、缺货不部分扣库存、取消归还库存，以及四类 CSV 错误记录后继续读取。


## 全套答案的学习写法

Domain、Store、DataLoader 和 Main 均采用明确的代码块、字段名和分步变量。集合以 List 和 Map 为主，使用普通 for/while、Map 的 + 和 -、以及 try/catch/finally；不再用 find、filter、collect、foldLeft 等组合来实现业务流程。领域对象仍保持不可变，局部 var 仅用于累计结果。Main 的七项 Bonus 也展开成逐步操作，便于学习。

sorted（编号排序）、mkString（输出列表）、日期、枚举、Either、BigDecimal 属于仍需掌握的库接口或模型功能；不能理解为讲义中的每个接口都已逐一讲到。SolutionChecks 是验证代码，不是考试需要提交的业务功能；保留其独立的检查逻辑用于验证整套答案。

建议学习顺序：Domain 的字段和验证 → Money → Order → Store → DataLoader.loadStock → 其他加载方法 → Main → Bonus。小练习 csv_practice 继续保留原有基础写法。

Loader 不额外去重：逐行返回合法记录。客户、商品和订单的重复编号仍由 Store 添加方法拒绝；库存文件中同一编号出现多次时，Main 按文件顺序设置，后面的值覆盖前面的值。

文件读取统一使用讲义中的字符串路径，例如 Source.fromFile(path, "UTF-8")；不使用 Path、Paths、resolve、toFile 或自动目录搜索，也不再处理 BOM 前缀。SolutionChecks 是单独的开发验证工具，其中临时文件管理使用 JDK 接口，不属于需学习或提交的考试业务实现。
