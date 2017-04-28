| Line Number   | Fault Description | Fix Description |
| ------------- | ----------------- | --------------- |
| 151           | Throwing an exception if the current quote is not null | Change to check if the current quote is null |
| 187           | Subtracting the previous close value from the change | Don't subtract the previous close value |
| 220           | Not checking if there have been at least two quotes | Throw correct exception if previous or current quote is null |
| 220           | Not subtracting the previous quote's last trade from the current quote's | Subtract the previous quote's last trade from the current quote's |
