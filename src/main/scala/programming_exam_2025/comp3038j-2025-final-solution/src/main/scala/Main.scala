import data.DataManager

import inventory.Rating
import money.{Currency, CurrencyConverter, Money}
import inventory.OrderStatus

@main def main(): Unit =
    val store = Store
    val dm = DataManager

    dm.loadCustomers("customers.csv").foreach(store.addCustomer)
    dm.loadItems("items.csv").values.foreach(store.addItem)
    dm.loadStock("stock.csv", store.getItems).foreach((i, q) => store.updateStock(i, q))
    dm.loadOrders("orders.csv", store.getItems, store.getCustomers).foreach(store.addOrder)

    println(s"Loaded ${store.getCustomers.size} customers, ${store.getItems.size} items, " +
        s"${store.getStock.size} stock entries and ${store.getOrders.size} orders")

    // Write an expression to find all the items that have at least one item in stock and are suitable
    // for a customer under the age of 15
    val allAvailableItems = store.getStock.filter((k, v) => v > 0).keys.filter(_.rating == Rating.All).toList

    // Write an expression to print the id and price of the orders currently in the system
    println("Order ids and the prices of the orders in CNY")
    store.getOrders.foreach(o => println(o.orderId + " " + o.calculatePrice(Currency.CNY)))
    println()

    // Write an expression to create a list containing all of the customers that live in either Ireland or China
    val ICCustomers =
        store.getCustomers.values.filter(c => c.address.country == "Ireland" || c.address.country == "China").toList

    // Write an expression to print the order id, name of the customer, and country of the customer for all
    // orders that have the status AwaitingStock
    println("Orders that are waiting for stock")
    store.getOrders.filter(_.status == OrderStatus.AwaitingStock).foreach(o =>
        println(o.orderId + " " + o.customer.name + " " + o.customer.address.country)
    )
    println()

    // Write an expression to print the 5 name and price and quantity of items in the store that have the
    // highest amount of stock
    println("Highest 5 items in stock:")
    store.getStock.toList.sortBy(-_._2).take(5).foreach((i, c) =>
        println(f"${i.name}%-40s - ${CurrencyConverter.convert(i.price, Currency.EUR)}  -  $c")
    )
    println()

    // Write an expression that will print out the total value of all of the items currently in stock in USD
    val totalValue =
        store.getStock.toList.map((i, q) => CurrencyConverter.convert(i.price, Currency.USD) * q).foldLeft(Money(
            Currency.USD,
            0,
            0
        ))(_ + _)
    println(s"Total value of items in stock is ${totalValue}")
    println()

    // Write an expression to print the total value in GBP for all orders that have the status AwaitingStock
    val valueWaiting = store.getOrders.filter(_.status == OrderStatus.AwaitingStock).map(o =>
        o.calculatePrice(Currency.GBP)
    ).fold(Money(Currency.GBP, 0, 0))(_ + _)
    println(s"Total value of orders waiting for stock is $valueWaiting")
