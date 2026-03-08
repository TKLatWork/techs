# api Module of CookApi

    API to submit/check/cancel orders

# Java Spec

    model classes should under package me.webServer.server.api.model
    Interface should under package me.webServer.server.api

    All response in JSON, in Response type, with code/message/data fields.

# Terms
    
    Order: a list of cooking order. with orderId/clientName/list of cookItem
    CookItem: a cook in menu, with name/quantity
    

# Cases

## Case submit

    Client send a new Order, with orderId/clientName/items.
    Items is a list of cookItem, which has name and quantity.
    API will return a order, with status "pending" and price as calculated.

### Errors
    - 400: Bad request, if the request body is not valid.
    - 409: Conflict, if the orderId already exists.

## Case check

    Client send a orderId, API will return the order with the given orderId.

### Errors
    - 404: Not found, if the orderId does not exist.

## Case cancel

    Client send a orderId, API will try to cancel all pending cookItem.
    When order is cancelled, the status will be "cancelled" and price will be sum of all finished cookItem.

### Errors
    - 404: Not found, if the orderId does not exist.
    - 409: Conflict, if the order is already cancelled or finished.