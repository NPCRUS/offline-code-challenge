##### Possible improvements:
1. API documentation(manual swagger or using lib)
2. Better seeding mechanism
3. Router
    1. Organize routes into services (akka-http abstraction)
    2. Controllers to organize logic
4. Stores
    1. refreshable state
    2. primary id sequence abstract logic
    3. lock on write mechanism
5. Models:
    1. Entity abstraction
6. Tests:
    1. Refresh stores state between tests
    2. Tests for stores