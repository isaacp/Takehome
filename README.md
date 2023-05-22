## Updates:
 - Tidied up a little.
 - Added H2 Database as backing store for usageEvents, Metrics, Customer Accounts, and Adjustments.
 - Added akka fire-and-forget message queue to decouple metric conversion from usage store writing.

## Usage-Based Billing Project

- [Design Document](docs/design.md)

## Run Project

> 1. sbt compile  
> 2. sbt run
 
The usage database is pre-populated with user data for 4 accounts over a five-minute period from the start of the application.
To see the changing report data as time goes by, type the following command into the prompt:

```&> report <customer_number>```
 
customer_number = 1, 2, 3 or 4

The report will print as follows:

```
|------------------------------------------------------------
| Canopy, Inc            Period Ending: May 20 2023 21:05
| Customer: 1 (platinum)
|
|   Compute(23104 cu):
|   $184.83
|
|   Storage(23161 su):
|   $370.58
|
|   Bandwidth(21152 bu):
|   $444.19
|
|  Adjustment: ($103.00)
|
|  Total Amount: $896.60
-------------------------------------------------------------

```

To add a price adjustment for a customer:

> ```&> adjust <customer_number> <amount>```

To Exit:
> &> q

Or

> &> quit 