# Operion Workforce Operations Platform

A comprehensive workforce management API built with Spring Boot that provides employee management, attendance tracking, leave management, project management, and task management capabilities.

## Features

- **Employee Management**: Complete CRUD operations for employee records with department assignment
- **Department Management**: Organize employees into departments
- **Attendance Tracking**: Monitor employee attendance with clock-in/clock-out functionality
- **Leave Management**: Handle leave requests with approval workflows
- **Project Management**: Create and manage projects with status tracking
- **Task Management**: Assign and track tasks within projects
- **Team Management**: Assign employees to projects with specific roles
- **Authentication & Authorization**: JWT-based authentication with role-based access control
- **API Documentation**: Interactive Swagger/OpenAPI documentation

## Technology Stack

- **Java 17**
- **Spring Boot 4.1.0**
- **Spring Data JPA** - Database ORM
- **Spring Security** - Authentication and authorization
- **MySQL** - Database
- **Flyway** - Database migration management
- **JWT (jjwt)** - Token-based authentication
- **Lombok** - Reduce boilerplate code
- **SpringDoc OpenAPI** - API documentation
- **Maven** - Build tool

## Database Schema

The application uses the following main entities:

- **employee**: Employee information with roles and department assignments
- **department**: Organizational departments
- **attendance**: Daily attendance records with clock-in/clock-out times
- **leave_request**: Leave requests with approval status
- **project**: Project information and status tracking
- **project_member**: Employee-project assignments with roles
- **task**: Project tasks with assignment and status tracking

## User Roles

The system supports four user roles with different permissions:

- **ADMIN**: Full system access
- **HR**: Employee management, department management, leave approvals
- **MANAGER**: Project management, task assignment
- **EMPLOYEE**: View-only access to most resources, can create leave requests

## Prerequisites

- Java 17 or higher
- Maven 3.6+
- MySQL 8.0+
- Git

## Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/thathsaraDinu/Operion-Workforce-Operations-Platform.git
   cd Operion-Workforce-Operations-Platform
   ```

2. **Configure MySQL Database**
   - Create a MySQL database named `operion`
   - Update database credentials in `src/main/resources/application.properties`:
     ```properties
     spring.datasource.url=jdbc:mysql://localhost:3306/operion
     spring.datasource.username=your_username
     spring.datasource.password=your_password
     ```

3. **Build the project**
   ```bash
   ./mvnw clean install
   ```
   Or on Windows:
   ```bash
   mvnw.cmd clean install
   ```

4. **Run the application**
   ```bash
   ./mvnw spring-boot:run
   ```
   Or on Windows:
   ```bash
   mvnw.cmd spring-boot:run
   ```

The application will start on `http://localhost:8080`

## API Documentation

Once the application is running, access the interactive API documentation at:

- **Swagger UI**: `http://localhost:8080/swagger-ui.html`
- **OpenAPI JSON**: `http://localhost:8080/v3/api-docs`

## Standard Response Format

All API endpoints return a consistent response structure:

```json
{
  "success": true,
  "message": "Operation completed successfully",
  "data": { ... },
  "timestamp": "2024-01-15T10:30:00"
}
```

- **success**: Boolean indicating if the operation was successful
- **message**: Human-readable description of the operation result
- **data**: The actual response payload (varies by endpoint)
- **timestamp**: ISO-8601 timestamp of the response

## API Endpoints

### Authentication
- `POST /api/auth/login` - Authenticate user and receive JWT token

### Employees
- `GET /api/employees` - Get all employees (paginated)
- `GET /api/employees/{id}` - Get employee by ID
- `POST /api/employees` - Create new employee (ADMIN, HR)
- `PATCH /api/employees/{id}` - Update employee (ADMIN, HR)
- `DELETE /api/employees/{id}` - Delete employee (ADMIN, HR)
- `PATCH /api/employees/{employeeId}/department/{departmentId}` - Assign department (ADMIN, HR)

### Departments
- `GET /api/departments` - Get all departments
- `GET /api/departments/{id}` - Get department by ID
- `POST /api/departments` - Create new department (ADMIN, HR)
- `PATCH /api/departments/{id}` - Update department (ADMIN, HR)
- `DELETE /api/departments/{id}` - Delete department (ADMIN only)
- `GET /api/departments/{id}/employees` - Get department employees (ADMIN, HR, MANAGER)

### Attendance
- `POST /api/attendance/clock-in` - Clock in (all authenticated users)
- `PATCH /api/attendance/clock-out` - Clock out (all authenticated users)
- `GET /api/attendance/me` - Get my attendance (all authenticated users)
- `GET /api/attendance` - Get all attendance records (ADMIN, HR, MANAGER)
- `GET /api/attendance/employee/{employeeId}` - Get employee attendance (ADMIN, HR, MANAGER)
- `PUT /api/attendance/{attendanceId}` - Update attendance record (ADMIN, HR)

### Leave Requests
- `POST /api/leaves` - Create leave request (all authenticated users)
- `GET /api/leaves/me` - Get my leave requests (all authenticated users)
- `GET /api/leaves/{id}` - Get leave request by ID (ADMIN, HR, MANAGER)
- `GET /api/leaves` - Get all leave requests (ADMIN, HR, MANAGER)
- `GET /api/leaves/status/{status}` - Get leave requests by status (ADMIN, HR, MANAGER)
- `PUT /api/leaves/{id}` - Update leave request (own pending requests only)
- `PATCH /api/leaves/{id}/approval` - Approve/Reject leave request (ADMIN, HR, MANAGER)
- `DELETE /api/leaves/{id}` - Delete leave request (own pending requests only)

### Projects
- `GET /api/projects` - Get all projects (paginated)
- `GET /api/projects/{id}` - Get project by ID
- `POST /api/projects` - Create new project (ADMIN, MANAGER)
- `PUT /api/projects/{id}` - Update project (ADMIN, MANAGER)
- `DELETE /api/projects/{id}` - Delete project (ADMIN, HR)

### Project Members
- `POST /api/projects/{projectId}/members` - Add member to project (ADMIN, HR, MANAGER)
- `GET /api/projects/{projectId}/members` - Get project members (all authenticated users)
- `DELETE /api/projects/{projectId}/members/{memberId}` - Remove member from project (ADMIN, HR, MANAGER)

### Tasks
- `GET /api/tasks` - Get all tasks (ADMIN, MANAGER)
- `GET /api/tasks/{id}` - Get task by ID (all authenticated users)
- `POST /api/tasks` - Create new task (ADMIN, MANAGER)
- `PUT /api/tasks/{id}` - Update task (ADMIN, MANAGER)
- `DELETE /api/tasks/{id}` - Delete task (ADMIN, MANAGER)
- `GET /api/tasks/project/{projectId}` - Get tasks by project (all authenticated users)
- `GET /api/tasks/employee/{employeeId}` - Get tasks by employee (ADMIN, MANAGER)
- `GET /api/tasks/status/{status}` - Get tasks by status (ADMIN, MANAGER)

## Authentication

All endpoints (except `/api/auth/login`) require JWT authentication in the request header:

```
Authorization: Bearer <your-jwt-token>
```

To obtain a JWT token, use the login endpoint:
```bash
POST /api/auth/login
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "password"
}
```

## Database Migrations

The application uses Flyway for database migrations. Migration scripts are located in:
- `src/main/resources/db/migration/`

Initial schema is automatically created when the application starts.

## Configuration

Key configuration properties in `src/main/resources/application.properties`:

- **Server Port**: Default 8080
- **Database**: MySQL connection settings
- **JWT Secret**: Secret key for token generation
- **JPA Settings**: Hibernate configuration and SQL logging

## Project Structure

```
src/main/java/com/dinoryn/operion/
├── config/           # Configuration classes (OpenAPI, Web)
├── controller/       # REST API controllers
├── dto/             # Data Transfer Objects
├── entity/          # JPA entities
├── exception/       # Custom exception handlers
├── mapper/          # Entity-DTO mappers
├── repository/      # JPA repositories
├── security/        # Security configuration (JWT, filters)
├── service/         # Business logic layer
└── OperionApiApplication.java  # Main application class
```

## Development

### Testing

The project includes comprehensive unit tests for critical business logic using JUnit 5 and Mockito. Test coverage focuses on:

**Test Suites:**
- **EmployeeServiceTest**: Employee creation, validation, updates, and department assignment
- **LeaveServiceTest**: Leave request workflow, approval process, and authorization checks  
- **AttendanceServiceTest**: Clock-in/clock-out validation and attendance record management
- **AuthServiceTest**: Authentication flow and JWT token generation

**Key Test Coverage:**
- Business rule validation (email uniqueness, date ranges, time logic)
- Authorization and access control (role-based permissions, ownership verification)
- State management (pending-only updates, attendance state transitions)
- Error handling (proper exception throwing for invalid operations)
- Data integrity (foreign key validation, entity relationships)

**Running Tests:**
```bash
./mvnw test
```

**Running Specific Test Class:**
```bash
./mvnw test -Dtest=EmployeeServiceTest
```

### Code Formatting
The project uses Lombok to reduce boilerplate code. Ensure you have the Lombok plugin installed in your IDE.

### Adding New Features
1. Create entity in `entity/` package
2. Create repository in `repository/` package
3. Create DTOs in `dto/` package
4. Create mapper in `mapper/` package
5. Create service interface and implementation in `service/` package
6. Create controller in `controller/` package
7. Add Flyway migration script if database changes are needed

## Security Considerations

- Change the default JWT secret in production
- Use environment variables for sensitive configuration
- Enable HTTPS in production
- Regularly update dependencies
- Implement rate limiting for API endpoints

## License

This project is licensed under the MIT License.

## Support

For support and questions, contact the Operion Team at support@operion.com
