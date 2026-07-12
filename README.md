# NightFlow - Connect to the nightlife!

Developed for the Software Engineering and Web Design course, University of Rome Tor Vergata.

<p align="center">
  <img src="src/main/resources/images/logo.png" alt="NightFlow Logo" width="200">
</p>

## Description

NightFlow is a Java-based event management platform that connects organizers and attendees. It allows customers to easily book and manage event tickets, while organizers can efficiently create events and track attendees. The application supports both a graphical user interface (JavaFX) and a command-line interface.

* **Clients** can book tickets, modify existing reservations, view detailed information on active and past reservations, and receive email notifications.
* **Organizers** can create new events, edit their details, view information about registered customers, and receive email notifications.

## Technologies

* Java 17
* Maven
* MySQL
* JavaFX
* SendGrid API (notifiche email)
* Pagamento simulato

## Architecture

BCE (Boundary-Control-Entity) - MVC (Model-View-Controller) pattern with clear separation between:
* `controller/applicativo` — business logic
* `controller/cli` — CLI user interface
* `controller/gui` — GUI user interface
* `view/cli` — CLI boundary view
* `view/gui` — GUI boundary view
* `dao` — data access layer (DB, File, Memory)
* `model` — domain entities
* `bean` — data transfer objects
* `pattern` — GoF patterns (Singleton, Observer, State)

The system supports three persistence modes:
* **DATABASE** — MySQL (full-version)
* **FILE** — JSON (full-version)
* **MEMORY** — in-memory (demo-version)

## Getting started

At startup, the application asks to select the persistence mode:
* **Demo** → simulated in-memory data
* **Database** → MySQL persistence
* **File** → JSON file persistence

Then, the interface must be selected:
* **CLI** → text-based interface
* **GUI** → graphical interface

To use the database mode, create and configure the following file:

```text
src/main/resources/db.properties
```
with the following content:
```text
db.url=jdbc:mysql://localhost:3306/brainbankdb
db.user.login=bb_login
db.user.login.password=bb_login
db.user.student=bb_student
db.user.student.password=bb_student
db.user.tutor=bb_tutor
db.user.tutor.password=bb_tutor
db.user.admin=bb_admin
db.user.admin.password=bb_admin
sendgrid.api.key=YOUR_SENDGRID_API_KEY
```
⚠️ The `db.properties` file includes database credentials and a placeholder for the SendGrid API key.
Email notifications in action can be seen in the [project demo video](https://youtu.be/w-RNxzK7c6I?si=UnCEmEwtZw_1LKpJ).
## Demo credentials

| Role     | Email            | Password   |
|------------|----------------|------------|
| Client     | `client@demo`  | password   |
| Organizer  | `org@demo`     | password   |

Other test accounts available, see DemoDataStore for the full list.

## Database credentials (modalità MySQL)

| Role       | Email                           | Password    |
|------------|-------------------------------|-------------|
| Client     | `martinamaurizi30@gmail.com`  | password123 |
| Organizer  | `info@jolieclub.com`          | password123 |


Other test accounts available, see NightFlow_db.sql for the full list.
## Author
Maurizi Martina
