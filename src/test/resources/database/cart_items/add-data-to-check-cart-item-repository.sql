insert into categories (id, name, description) values (1, 'Fiction1', 'Fiction books1');
insert into books (id, title, author, isbn, price, description, cover_image) values (1, 'Title1', 'Author1', 'Isbn1', 120, 'Description1', 'Cover Image1');
insert into books_categories (book_id, category_id) values (1, 1);
insert into users_roles (user_id, role_id) values (2, 2);
insert into cart_items (id, shopping_cart_id, book_id, quantity) values (1, 1, 1, 1);
insert into shopping_carts (id, user_id) values (1, 2);
