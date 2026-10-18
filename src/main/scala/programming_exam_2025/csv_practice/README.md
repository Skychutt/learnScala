# 商品 CSV 小练习

目标：从一个 CSV 文件出发，自己建立模型、读取数据、验证数据，然后完成查询和统计。

## 当前基础答案

已提供 Model.scala、DataLoader.scala 和 Main.scala。使用 while 循环逐行读取，把数据转换成 Product，再存进 List[Product]。运行本练习目录里的 Main.main，默认 working directory 为 learnScala 项目根目录；也可以在程序参数中传入 CSV 路径。

这版先针对提供的合法数据练习，不包含模型参数验证、字段数量检查或逐行错误恢复；读到错误数据会抛异常。finally 保证文件关闭。下面的验证、查询统计和错误处理步骤可以随后继续练习。

## 数据含义

`products.csv` 每行表示一种商品，第一行是表头。

- `id`：商品编号，非空且只含字母和数字。
- `name`：商品名称，不能为空。
- `currency`：币种，本次所有商品都是 CNY。
- `units`：价格的整数部分，必须大于或等于 0。
- `centiunits`：价格的小数部分，必须在 0 到 99 之间。
- `quantity`：库存，必须大于或等于 0；0 表示缺货，是合法数据。

例如 `P001,Notebook,CNY,12,50,20` 表示：编号 P001 的笔记本，单价 12.50 CNY，库存 20 本。

商品名称没有逗号或引号，字段也没有换行，因此这次可以使用简单的 `line.split(",", -1)`。第二个参数 -1 用于保留末尾空字段。

## 按顺序完成

1. 建模：先决定 Currency、Money、Product 分别保存哪些字段以及字段类型。在这个小练习中，库存可以直接作为 Product 的字段。
2. 创建对象：手动创建一件商品，打印名称、价格和库存，检查构造验证是否生效。
3. 读取文件：使用 Source.fromFile 和 getLines，先只打印每一行，读完关闭文件。
4. 解析数据：跳过表头，拆分每行，检查字段数量，把数字字段转换成 Int，把币种转换成枚举。
5. 创建并收集模型：把每行转换成 Product，返回 List[Product] 或 Vector[Product]。在关闭文件前完成读取和收集。
6. 查询统计：打印所有商品；找出有库存的商品；找出库存最多的商品；计算全部库存的总价值。
7. 练习错误处理：先用正常数据跑通，再临时把一个库存改成 abc 或负数。要求打印错误、跳过错误行，继续读取后面的合法商品；最后恢复数据。

建议新建练习代码时使用包名 `programming_exam_2025.csv_practice`，避免与原考试答案中的同名类型混淆。

## 文件路径

如果运行目录是 learnScala 项目根目录，相对路径是：

```text
src/main/scala/programming_exam_2025/csv_practice/products.csv
```

如果运行目录是 programming_exam_2025，则相对路径是：

```text
csv_practice/products.csv
```
