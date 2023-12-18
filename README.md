# book-sore-spring
## Introduction
This application make more easier  and more safety purchase the books. It will be greate way for some one who selling books. By using this application it not nessesery to manetain any shops, which is save money and this application helps to controle the books assortment which is save time. With this application anyone can find the book what they are looking for and buy it, stay at home. 
## The technologies and tools used
* Java 17
* Maven
* Mockito
* Mapstruct
* MySql 8
* Liquibase
* Hibernate
* Spring Boot
* Spring Security
* Spring Data JPA
* Lombok
* Docker
* Swagger
## Endpoints
### Authentication Controller
* POST: /auth/register - allows to register new users. Email is unique. An example:
```json
{
  "email": "John.doe@example.com",
  "password": "12345678",
  "repeatPassword": "12345678",
  "firstName": "John",
  "lastName": "Doe",
  "shippingAddress": "123 Main St, City, Country"
}
```
* POST: /auth/login - Authenticates a user and returns JWT token. An example:
```json
{
  "email": "John.doe@example.com",
  "password": "12345678"
}
```
### Book controller
* GET: /books - Get a list of all available books. It allowed only for authenticated users.
* GET: /books/{id} - Get a book by id. It allowed only for authenticated users.
* GET: /books/search?titles=exapmle&authors=example - Search a book by title and author, or by one of this params. It allowed only for authenticated users.
* POST: /books - Add a new book. Isbn - is uniqure. It allowed only for admin. An example:
```json
{
    "title": "title",
    "author": "author",
    "isbn": "isbn",
    "price": "40",
    "description": "description",
    "coverImage": "coverImage",
    "categories":  [{ "id": 1 },
    {"id": 2} ]
}
```
* PUT: /books/{id} - Update book by id book. By using the same body as in case add a new book. It allowed only for admin.
* DELETE: /books/{id} - Delete book by id book. By using soft delete concept. It allowed only for admin.
### Category Controller
* GET: /categories - Get a list of all available categories. It allowed only for authenticated users.
* GET: /categories/{id} - Get a category by id. It allowed only for authenticated users.
* GET: /categories/{id}/books - Find all books by id category. It allowed only for authenticated users.
* POST: /categories - Create a new category. It allowed only for admin. An example: 
```json
{
  "name": "Fiction",
  "description": "Fiction books"
}
```
* PUT: /categories/{id} - Update a category by id. By using the same body as in case create a new category. It allowed only for admin.
* DELETE: /categories/{id} - Delete a category by id. By using soft delete concept. It allowed only for admin.
### Shopping cart Controller
* GET: /cart - Get all information of user shopping cart. It allowed only for authenticated users.
* POST: /cart - Add book to the shopping cart. It allowed only for authenticated users. An example:
```json
{
  "bookId": 2,
  "quantity": 3
}
```
* PUT: /cart/cart-items/{cartItemId} - Update quantity of a book in the shopping cart. It allowed only for authenticated users. An example:
```json
{
  "quantity": 5
}
```
* DELETE: /cart/cart-items/{cartItemId} - Remove a book from the shopping cart by cart item id. It allowed only for authenticated users.
### Order Controller
* GET: /orders - Retrieve user's order history. It allowed only for authenticated users.
* POST: /orders - Place an order by shipping address. It allowed only for authenticated users. An example:
```json
{
  "shippingAddress": "shippingAddress"
}
```
* GET: /orders/{orderId}/items - Retrieve all order items for a specific order. It allowed only for authenticated users.
* GET: /orders/{orderId}/items/{itemId} - Retrieve a specific order item within an order. It allowed only for authenticated users.
* PATCH: /orders/{orderId} - Update order status. There are three statuses: COMPLETED, PENDING, DELIVERED. It allowed only for admin. An example:
```json
{
  "status": "COMPLETED"
}
```
## How to use the application
1. Make sure you have installed next tools:
* JDK 17+
* Docker
2. Clone the repository from GitHub
3. Run the following commands:
```json
mvn clean package
docker-compose build
docker-compose up
```
Admin login:
```json
{
  "email": "admin@example.com",
  "password": "12345678"
}
```
Swagger is available for testing at http://localhost:11416/swagger-ui/index.html#/. But before you must create user by using http://localhost:11416 at the Postman.  Then use email and password of this user to enter at Swagger.
## Video presentation
https://www.youtube.com/watch?v=H3UxirHXDOw