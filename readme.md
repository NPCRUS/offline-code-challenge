#### Letter
First of all, I want to say that I had huge fun doing rest api using scala.
I'm looking forward for our further conversation. Runnable file is: `src/main/scala/WebServer`.
API documentation realized manually and saved in `openapi.yaml`, standard is OpenAPI 3.0 you can open this file
using any swagger online editors, [for example here](https://editor.swagger.io/)

Since I was implementing database in-memory and haven't used any ready solutions, it's missing couple of key features
which is really important for live system: **Lock** mechanism and **Transaction** mechanism, let me know if
features are required for successful implementation of this code challenge.

For easier manual testing I implemented some seeds:
* User - test@gmail.com
* Product1 - coca-cola, id: 1, price: 2, quantity: 100
* Product2 - pepsi-cola, id: 2, price: 1, quantity: 200

#### Features:
1. API logic
2. In-memory database(stores)
3. Tests
4. API documentation

#### Possible improvements:
1. ~~API documentation(manual swagger or using lib)~~
2. ~~Better seeding mechanism~~
3. Router
    1. Organize routes into services (akka-http abstraction)
    2. Controllers to organize logic
4. Stores
    1. ~~refreshable state~~
    2. ~~primary id sequence abstract logic~~
    3. lock on write mechanism
    4. transactions
5. Models:
    1. ~~Entity abstraction~~
6. Tests:
    1. ~~Refresh stores state between tests~~
    2. Tests for stores