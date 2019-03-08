#All Possible USE cases

Invalid action results as a **WAIT** action.

**Dishwasher**

| Carried Item | => | Carried Item |
|:---:|:---:|:---:|
| none | => | DISH |
| full DISH | => | empty DISH |
| food | => | DISH + food |

**Food Crates**

| Carried Item | Type of Crate | => | Carried Item |
|:---:|:---:|:---:|:--:|:---:|
| none | any |  => | corresponding food |
| CHOPPED_DOUGH | BLUEBERRY | => | RAW_TART |
| DISH | BLUEBERRY or ICE_CREAM | => | DISH + (BLUEBERRY or ICE_CREAM) |
| DISH | STRAWBERRY or DOUGH | => | Invalid action |
| any food but CHOPPED_DOUGH | any | => | Invalid action |


Food in food crates is unlimited.

**Chopping Board**

| Carried Item | => | Carried Item |
|:---:|:---:|:---:|
| STRAWBERRIES | => | CHOPPED_STRAWBERRIES |
| DOUGH | => | CHOPPED_DOUGH |
| any other item | => | Invalid action |

**Oven**

| Carried Item | State of Oven | => | State of Oven | Carried Item |
|:---:|:---:|:---:|:--:|:---:|
| DOUGH | empty |  => | DOUGH cooking | empty |
| RAW_TART | empty | => | RAW_TART cooking | empty |
| none | CROISSANT or TART | => | empty | CROISSANT or TART |
| DISH | CROISSANT or TART | => | empty | DISH + (CROISSANT or TART) |
| any other item | empty | => | Invalid action | Invalid action |
| RAW_TART or DOUGH | cooking (DOUGH or TART) | => | Invalid action | Invalid action |


**Tables**

| Carried Item | State of Table | => | State of Table | Carried Item |
|:---:|:---:|:---:|:--:|:---:|
| any item | empty |  => | item | none |
| none | any item | => | empty | item |
| DISH | any finished dessert | => | empty | DISH + finished dessert |
| DISH | any non-finished dessert | => | Invalid Action | Invalid action |
| DISH | DISH | => | Invalid Action | Invalid Action |
| any finished dessert | DISH | => | DISH + finished dessert | none |
| any non-finished dessert | DISH | => | Invalid action | Invalid action |
| CHOPPED_DOUGH | BLUEBERRY | => | none | RAW_TART |
| BLUEBERRY | CHOPPED_DOUGH | => | none | RAW_TART |
| any other food | any other food | => | Invalid action | Invalid action |

