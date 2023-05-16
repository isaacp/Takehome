## Usage-Based Billing Project

- [Design Document](docs/design.md)

## Run Project

> 1. sbt compile

> 2. sbt run
 
The usage database is prepulated with user data for 3 accounts over five minute period from the start of the application.
To see the changing report data as time goes by, type the following comand into the prompt:

```&> report <customer_number>```
 
customer_number = 1, 2, or 3

To add a price adjustment for a customer:

> ```&> adjust <customer_number> <amount>```

To Exit:
> &> q

Or

> &> quit 