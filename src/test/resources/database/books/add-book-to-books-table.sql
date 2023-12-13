INSERT INTO categories (id, name, description) VALUES (1, 'Fiction', 'Fiction books');
INSERT INTO books (id, title, author, isbn, price, description, cover_image)
VALUES (1, 'Title1', 'Author1', 'Isbn1', 20, 'Description1', 'Cover Image1');
INSERT INTO books_categories(book_id, category_id) VALUES (1, 1);
