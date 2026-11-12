package inventory

import money.Money

case class Item(stockID: String, name: String, description: String, price: Money, rating: Rating)
