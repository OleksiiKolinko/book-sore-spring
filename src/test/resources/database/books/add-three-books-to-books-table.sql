insert into categories (id, name, description) values (1, 'Fiction1', 'Fiction books1');
insert into categories (id, name, description) values (2, 'Fiction2', 'Fiction books2');
insert into books (id, title, author, isbn, price, description, cover_image) values (1, 'Title1', 'Author1', 'Isbn1', 120, 'Description1', 'Cover Image1');
insert into books (id, title, author, isbn, price, description, cover_image) values (2, 'Title2', 'Author2', 'Isbn2', 220, 'Description2', 'Cover Image2');
insert into books (id, title, author, isbn, price, description, cover_image) values (3, 'Title3', 'Author3', 'Isbn3', 320, 'Description3', 'Cover Image3');
insert into books_categories(book_id, category_id) values (1, 1);
insert into books_categories(book_id, category_id) values (2, 1);
insert into books_categories(book_id, category_id) values (2, 2);
insert into books_categories(book_id, category_id) values (3, 2);
