package inventory

enum OrderStatus:
    case New, Processed, AwaitingStock, Collected, Delivered, Cancelled
