✓ Requirements !!!!!!!!!!
Before running the project, ensure the following are installed:
Java 17+
MySQL 8+

⚙️ 2. Application Configuration (IMPORTANT)
Before starting the app, open:
src/main/resources/application.properties
and update these fields:
🔧 Database Configuration

Replace with your own MySQL credentials:

spring.datasource.url=jdbc:mysql://localhost:3306/rating_system?useSSL=false&serverTimezone=UTC
spring.datasource.username=YOUR_MYSQL_USERNAME
spring.datasource.password=YOUR_MYSQL_PASSWORD

🔐 JWT Configuration
jwt.secret=YOUR_SECRET_KEY
jwt.expiration-ms=3600000

Generate your own long secret key.
(At least 64+ characters recommended.)

✉ Email (Gmail SMTP)

To enable email confirmation + password reset:

spring.mail.host=smtp.gmail.com
spring.mail.port=587

spring.mail.username=YOUR_GMAIL_ADDRESS
spring.mail.password=YOUR_GMAIL_APP_PASSWORD

spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true

⚠ Do not use your real Gmail password — Gmail requires an App Password.

🌐 Server Port
server.port=8080

🗂 3. Database Setup

Create a new database in MySQL:

CREATE DATABASE rating_system;


💡 Hibernate schema is automatically created on startup.

🎮 4. Seeder (Default Games)

When you run the application for the first time:

Default games (e.g., Valorant, NBA2K, FIFA, CS) will be auto-inserted.

This helps you immediately test endpoints like:

/object

/users?gameTitle=valorant

etc.

You don’t need to add games manually unless you want custom ones.


▶ 6. Running the Project
1) Install dependencies
mvn clean install

2) Run the application
mvn spring-boot:run

The server will start at:

http://localhost:8080

🧪 7. Postman Collection (Ready to Import)

You can import this file directly in Postman and test all endpoints:

📁 Rating System.postman_collection.json
(File included in the repository)

Reference: 

Rating System.postman_collection

How to use:

Open Postman

Click Import

Upload the .json file

All folders (Auth, Users, Admin, Games, Comments, GameObjects) will appear instantly
