# SE_Project_Appointment_Scheduling_System

## Project Overview
This project is a simple Appointment Scheduling System developed in Java using Maven.  
It was built as part of a software engineering assignment to apply object-oriented programming, layered architecture, testing, and design patterns.

The system allows users to book and manage appointments, while administrators can monitor and manage all reservations.  
The project also includes appointment rules, notifications, and unit testing.

---

## Main Features

### User Features
- Register a new account
- Log in to the system
- View available appointment slots
- Book an appointment
- View personal appointments
- Modify a future appointment
- Cancel a future appointment

### Administrator Features
- Log in as administrator
- View all appointments
- Modify any appointment
- Cancel any appointment
- Send appointment reminders

---

## Business Rules
The system applies several validation and booking rules, such as:

- Appointment must be scheduled in the future
- Appointment must be within working hours
- Appointment must be on working days only
- Appointment duration must not exceed the allowed limit
- Number of participants must not exceed the allowed limit
- Overlapping appointments for the same user are not allowed

The system also supports different appointment types and applies different rules depending on:
- Purpose
- Category
- Mode

Examples:
- Urgent appointments have stricter duration rules
- Follow-up appointments are limited by category
- Assessment appointments must be in-person

---

## Project Structure
The project follows a layered design:

### 1. Presentation Layer
This layer contains the user interaction part of the system, including menus and dialogs.

### 2. Service Layer
This layer contains the main business logic such as:
- booking
- modifying
- cancelling
- reminders
- authentication
- validation

### 3. Domain Layer
This layer contains the main entities and enums used in the system, such as:
- `Appointment`
- `User`
- `Administrator`
- `TimeSlot`
- `AppointmentStatus`
- `AppointmentPurpose`
- `AppointmentCategory`
- `AppointmentMode`

### 4. Repository Layer
This layer is responsible for storing data in memory during runtime.

---

## Design Patterns Used

### Strategy Pattern
The Strategy Pattern is used for appointment rule validation.  
Different rules are separated into different classes, which makes the system easier to extend without changing the core booking logic.

Examples:
- `UrgentAppointmentRule`
- `FollowUpAppointmentRule`
- `AssessmentAppointmentRule`
- `IndividualAppointmentRule`
- `GroupAppointmentRule`
- `VirtualAppointmentRule`
- `InPersonAppointmentRule`

Factories are used to select the correct rule depending on the appointment type.

### Observer Pattern
The Observer Pattern is used for notifications.  
When an appointment is booked, modified, or cancelled, the system notifies observers, and the notification service generates the appropriate message.

Examples:
- `AppointmentObserver`
- `NotificationObserver`

---

## Notifications
The project includes a notification system for:
- booking confirmation
- appointment modification
- cancellation
- reminders for upcoming appointments

A mock notification service is used in the project to simulate sending messages, which is helpful for testing and tracking sent notifications.

---

## Configuration
The project uses a `config.properties` file to store email configuration values such as:

- sender email
- app password

This is better than writing these values directly inside the code.

Example:
```properties
mail.sender=your_email@gmail.com
mail.password=your_app_password

---

## Testing
The project includes unit tests using:

- JUnit 5
- Mockito
- JaCoCo

The tests cover:

- appointment booking
- validation rules
- edge cases
- notification behavior
- repository behavior

---

## Technologies Used
- Java
- Maven
- JUnit 5
- Mockito
- JaCoCo
- Swing / JOptionPane
- Object-Oriented Programming

---

Notes
The project currently uses in-memory repositories, so data is not saved permanently after closing the program.
The system is designed mainly to demonstrate software engineering concepts such as layering, testing, and design patterns.
The notification service is mocked for simplicity and testing purposes.

Conclusion
This project helped us practice how to build a small but complete software system using Java.
It includes authentication, booking management, validation rules, notifications, design patterns, and testing.
It also shows how software can be organized into layers to make it easier to understand and maintain.