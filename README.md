# In-Progress Summary
There will be two components to this project: the backend which will be made up of OOAD objects and principles and SQL 
and the frontend which includes the HTML files. The necessary data for frontend logic and arguments for objects will be
located in SQL databases. To simplify the logic, I've ordered the README in topdown format, where information is
formatted front to back.

## Springboot/HTML
Most of the important files to understand will be located in the com.commerce.ooad.E_Commerce folder. Since this
project is focused on the backend, we won't worry too much about frontend design or formatting, just logic.
### Controller
The controller will handle GET and POST requests for different sites and submitting data. It can be found in the
the **com.commerce.ooad.E_Commerce folder**.
### HTML Templates
The templates can be found in **resources.templates** or **resources.static**. The main HTML files we should worry
about are **login, dashboard, shop, cart, and checkout**. 
___ 

## SQL
The SQL database will handle the frontend logic that should be passed to the Java objects for calculation. Models can
be found in the **com.commerce.ooad.E_Commerce.model** folder.
### Models
#### UserSQL
The UserSQL model is for logging in and tracking names for site formatting purposes. It also creates an ID for other
models to reference. The attributes of the UserSQL model include:
* **ID -** Long unique identifier
* **Username -** String unique identifier
* **Password -** String password to log into site
* **First/Last Name -** String names for site formatting
* **Email -** String email for formatting purposes
* **State -** String of state for tax purposes (Limited to Colorado(CO) and California(CA))

#### CartSQL
The CartSQL model is for tracking each user's cart upon logging in and out. Attributes include:
* **User_ID -** Foreign key, references UserSQL model's ID
* **ID -** Long unique identifier
* **Cart_Item_IDs -** List<Long> of all CartItemSQL model IDs in cart

#### CartItemSQL
This model is used to handle the many-to-many relationship of carts and products. Attributes include:
* **ID -** Long unique identifier
* **Cart_ID -** Foreign key, references CartSQL model's ID
* **Product_ID -** Foreign key, references ProductSQL model's ID
* **Quantity -** Integer representing the quantity of the product in the cart

#### ProductSQL
ProductSQL model is used to track different products in the site. Attributes include:
* **ID -** Long unique identifier
* **Name -** String product name
* **Cost -** BigDecimal product cost

#### PaymentMethodSQL
PaymentMethodSQL is used to track users' payment information. Each payment strategy (see classes) will require
different attributes, so some columns may be null depending on the Payment_Type. **Each user should only have one of
each Payment_Type (Paypal and Visa)**. Attributes include:
* **User_ID** Foreign key, references UserSQL model's ID
* **ID -** Long unique identifier
* **Balance -** BigDecimal balance of payment method
* **Payment_Type -** String of payment method strategy type (currently Visa and Paypal, see classes)
* **Payment_Email -** String of email for payment **(PaypalStrategy)**
* **Payment_Password -** String of password for payment **(PaypalStrategy)**
* **Payment_Card_Number -** String of card number **(VisaStrategy + Default)**
* **Payment_Card_PIN -** String of card pin **(VisaStrategy + Default)**
* **Payment_Card_Name -** String of name on card **(VisaStrategy + Default)**

### Repositories
Each SQL model will have an associated repository for storing and accessing SQL data tables via CRUD (Create, Read, 
Update, and Delete) methods. Repositories can be found in the **com.commerce.ooad.E_Commerce.repository** folder.

### Schema/Data
The schema.sql file will initialize SQL tables and data.sql will handle initializing entries to these tables. Each
model will have its own table, with similar attribute types as the models (except in SQL). These can be found in the 
**resources** folder.
___

## Java Classes (OOAD)
### User
Upon logging in, a session will be created. Upon creation, a User object along with others will be initialized and 
attached to the session for backend calculations. The attributes of User objects are as follows:
* **username -** String username of current session's user
* **state -** String of state abbreviation (CO or CA)
* **cart -** A Cart object for the specific user
* **paymentMethods -** A List<PaymentMethod> of each of the user's payment methods

### Cart
A cart object will also be initialized upon logging in. It will contain:
* **items -** A List<CartItems> of cart items in cart

### CartItem
Each cart will have cart items to handle the many-to-many relationship between cart and product. It will contain:
* **product -** Product object
* **quantity -** Integer representing amount of product in cart

### Product
An object that represents a purchasable product. Its attributes include:
* **Name -** String product name
* **Cost -** BigDecimal product cost

### Checkout
Checkout will be structured as a template pattern that handles checkout based on the location of the user (for taxation
purposes). The attributes are as follows:
* **cart -** The cart (Cart object) that is to be checked out and subsequently removed from the cart
* **paymentMethod -** The payment method (PaymentMethod object) that will be charged for the purchase
* **state -** String of state abbreviation (CO or CA)

### Observer
The only observer will be NotificationObserver that notifies users via a notification on the site of events. A test
observer will most likely be necessary too. Currently, these events include:
* **All -** All events
* **SuccessfulTransaction -** Upon successful completion of transaction
* **FailedTransaction -** Upon failure of transaction (due to insufficient balance)

### PaymentMethod
This object will hold all necessary information for checking out. The attributes include:
* **Type -** String of type of PaymentStrategy (Visa or Paypal)
* **Balance -** BigDecimal value of balance on the instance
* **email -** String of email for payment **(PaypalStrategy)**
* **password -** String of password for payment **(PaypalStrategy)**
* **cardNumber -** String of card number **(VisaStrategy + Default)**
* **cardPIN -** String of card pin **(VisaStrategy + Default)**
* **cardName -** String of name on card **(VisaStrategy + Default)**

### PaymentStrategy
Alters the validate method for PaymentMethod. For example, PaypalStrategy will need to validate the existence of
"email" and "password" attribute, VisaStrategy would validate "cardNumber", "cardPIN", and "cardName" attributes, etc.