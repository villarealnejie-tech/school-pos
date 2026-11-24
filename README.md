# School POS (Java Swing + MySQL)

A point-of-sale system for a school project: login, cashier screen with cart and receipt,
product/category management, and sales history. Data is stored in MySQL.

## What you need
1. **JDK 23 or newer** (set by `maven.compiler.release` in `pom.xml`)
2. **MySQL Server + MySQL Workbench**
3. **NetBeans or Maven.** The MySQL Connector/J driver is a Maven dependency in `pom.xml`,
   so it downloads automatically.

## Step 1 - Create the database in MySQL Workbench
1. Open MySQL Workbench and connect to your local server.
2. **File > Open SQL Script...** and choose `database/schema.sql`.
3. Click the lightning-bolt button (Execute). You should see `school_pos database created successfully`.

> Warning: the script starts with `DROP DATABASE IF EXISTS school_pos`, so running it again wipes all data.

## Step 2 - Set your MySQL password
Open `src/main/java/pos/model/dao/DB.java` and set `PASSWORD` to your MySQL password
(change `USER` too if you don't use `root`).

## Step 3 - Run
NetBeans: open the `SchoolPOS` project and press **Run Project** (F6). The main class is `pos.Main`.

Command line, from the project folder: `mvn compile exec:java`

## Logins
| Username | Password   | Role    |
|----------|------------|---------|
| admin    | admin123   | ADMIN (POS + Products + History) |
| cashier  | cashier123 | CASHIER (POS + History) |

## How it works
- **Point of Sale tab:** search or double-click a product (or type/scan a barcode and press Enter),
  adjust quantities with + / -, type the cash given, press CHECKOUT. A receipt is shown.
- **Products tab (admin):** add, edit, delete products; add categories.
- **Sales History tab:** every sale, its items, and today's total.
- Checkout is one database transaction: the sale, its items and the stock deduction all succeed
  or all roll back. Stock can never go negative.
- Passwords are stored as SHA-256 hashes, and all SQL uses prepared statements.

## Project layout (MVC)
```
SchoolPOS/
  database/schema.sql      <- run this in MySQL Workbench
  pom.xml                  <- Maven build (Java version, MySQL driver, main class)
  src/main/java/pos/
    Main.java              <- starts the app (opens the login screen)
    model/                 <- MODEL: data and business rules
      User, Product, Category, CartItem, Sale, SaleItem
      Cart                 <- cart rules (can't add more than the stock)
      ValidationException  <- a broken business rule, shown to the user
      dao/                 <- database access (all SQL lives here)
        DB, UserDao, ProductDao, SaleDao
    view/                  <- VIEW: Swing screens, no SQL and no business rules
      LoginFrame, MainFrame, SalesPanel, ProductsPanel, HistoryPanel, Ui
    controller/            <- CONTROLLER: reacts to user actions, uses the model, updates the view
      LoginController, MainController, SalesController, ProductController, HistoryController
    util/Money.java        <- peso formatting/parsing
```

### How the layers talk
1. The user clicks a button in a **view** (e.g. CHECKOUT in `SalesPanel`).
2. The view passes the click and its input to its **controller** (`controller.checkout(cashText)`).
3. The controller validates the input and calls the **model** (`Cart`, `SaleDao.checkout(...)`).
4. The controller tells the view what to show (`view.showReceipt(...)` or `view.showError(...)`).

Views never import a DAO, and the model never imports a view or controller.
Each controller is created with its view and calls `view.setController(this)`;
`MainController` builds the tabs for the logged-in user's role.

### Editing the screens
The views are NetBeans GUI Builder forms (each has a `.form` file next to its `.java` file in `view/`).
Open one and switch to the **Design** tab to edit it visually. The gray code inside `initComponents()`
is generated from the `.form` file, so edit it with the Designer, not by hand. Event handlers
should only pass the input on to the controller. Things the Designer can't set up (text-change
and table selection listeners) are in each view's constructor, after `initComponents()`.

## Troubleshooting
- **"No suitable driver" / ClassNotFoundException:** Maven has not downloaded the driver yet.
  In NetBeans, right-click the project and choose **Build with Dependencies**, with internet access.
- **"Access denied for user":** wrong password in `DB.java`.
- **"Unknown database school_pos":** run `database/schema.sql` in Workbench first.
- **Communications link failure:** MySQL Server isn't running (start it from Services / Workbench).
