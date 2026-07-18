# Operion API Guide

## Overview

Operion is a workforce operations platform API built with Spring Boot. It provides comprehensive employee management, attendance tracking, leave management, project management, and task management capabilities.

## Base URL

```
http://localhost:8080
```

## Standard Response Format

All API endpoints return a consistent response structure wrapped in `ApiResponseBody`:

```json
{
  "success": true,
  "message": "Operation completed successfully",
  "data": { },
  "timestamp": "2024-01-15T10:30:00"
}
```

- **success**: Boolean indicating if the operation was successful
- **message**: Human-readable description of the operation result
- **data**: The actual response payload (varies by endpoint)
- **timestamp**: ISO-8601 timestamp of the response

For error responses:
```json
{
  "success": false,
  "message": "Error description",
  "data": null,
  "timestamp": "2024-01-15T10:30:00"
}
```

## Authentication

The API uses JWT (JSON Web Token) for authentication. Include the JWT token in the `Authorization` header as a Bearer token:

```
Authorization: Bearer <your-jwt-token>
```

## User Roles

The API supports the following roles:

- **ADMIN** - Full system access
- **HR** - Human Resources management
- **MANAGER** - Department and project management
- **EMPLOYEE** - Basic employee access

---

## API Endpoints

### Authentication

#### Login

Authenticate user and receive JWT token.

**Endpoint:** `POST /api/auth/login`

**Request Body:**
```json
{
  "email": "string (required, valid email)",
  "password": "string (required)"
}
```

**Response (200):**
```json
{
  "success": true,
  "message": "Login successful",
  "data": {
    "token": "string (JWT token)",
    "id": "number (employee ID)",
    "email": "string (employee email)",
    "role": "string (user role: ADMIN|HR|MANAGER|EMPLOYEE)"
  },
  "timestamp": "2024-01-15T10:30:00"
}
```

**Roles Required:** None (public endpoint)

---

### Employee Management

#### Create Employee

Create a new employee account.

**Endpoint:** `POST /api/employees`

**Request Body:**
```json
{
  "firstName": "string (required)",
  "lastName": "string (required)",
  "email": "string (required, valid email)",
  "password": "string (required)",
  "role": "ADMIN|HR|MANAGER|EMPLOYEE (required)",
  "departmentId": "number (optional)",
  "phone": "string (optional)",
  "address": "string (optional)",
  "position": "string (optional)",
  "joiningDate": "date (YYYY-MM-DD, optional)"
}
```

**Response (201):**
```json
{
  "success": true,
  "message": "Employee created successfully",
  "data": {
    "id": "number",
    "firstName": "string",
    "lastName": "string",
    "email": "string",
    "role": "ADMIN|HR|MANAGER|EMPLOYEE",
    "departmentId": "number",
    "departmentName": "string",
    "phone": "string",
    "address": "string",
    "position": "string",
    "joiningDate": "date (YYYY-MM-DD)",
    "status": "ACTIVE|INACTIVE",
    "createdAt": "datetime (ISO-8601)",
    "updatedAt": "datetime (ISO-8601)"
  },
  "timestamp": "2024-01-15T10:30:00"
}
```

**Roles Required:** ADMIN, HR

---

#### Get All Employees

Retrieve all employees with pagination.

**Endpoint:** `GET /api/employees`

**Query Parameters:**
- `page` - Page number (default: 0)
- `size` - Page size (default: 10)
- `sort` - Sorting field and direction (e.g., `firstName,asc`)

**Response (200):**
```json
{
  "success": true,
  "message": "Operation completed successfully",
  "data": {
    "content": [
      {
        "id": "number",
        "firstName": "string",
        "lastName": "string",
        "email": "string",
        "role": "ADMIN|HR|MANAGER|EMPLOYEE",
        "departmentId": "number",
        "departmentName": "string",
        "phone": "string",
        "address": "string",
        "position": "string",
        "joiningDate": "date (YYYY-MM-DD)",
        "status": "ACTIVE|INACTIVE",
        "createdAt": "datetime (ISO-8601)",
        "updatedAt": "datetime (ISO-8601)"
      }
    ],
    "pageable": {},
    "totalElements": "number",
    "totalPages": "number"
  },
  "timestamp": "2024-01-15T10:30:00"
}
```

**Roles Required:** ADMIN, HR, MANAGER, EMPLOYEE

---

#### Get Employee by ID

Retrieve a specific employee by ID.

**Endpoint:** `GET /api/employees/{id}`

**Path Parameters:**
- `id` - Employee ID

**Response (200):**
```json
{
  "success": true,
  "message": "Operation completed successfully",
  "data": {
    "id": "number",
    "firstName": "string",
    "lastName": "string",
    "email": "string",
    "role": "ADMIN|HR|MANAGER|EMPLOYEE",
    "departmentId": "number",
    "departmentName": "string",
    "phone": "string",
    "address": "string",
    "position": "string",
    "joiningDate": "date (YYYY-MM-DD)",
    "status": "ACTIVE|INACTIVE",
    "createdAt": "datetime (ISO-8601)",
    "updatedAt": "datetime (ISO-8601)"
  },
  "timestamp": "2024-01-15T10:30:00"
}
```

**Roles Required:** ADMIN, HR, MANAGER, EMPLOYEE

---

#### Update Employee

Update employee information (partial update).

**Endpoint:** `PATCH /api/employees/{id}`

**Path Parameters:**
- `id` - Employee ID

**Request Body:**
```json
{
  "firstName": "string (optional)",
  "lastName": "string (optional)",
  "email": "string (optional)",
  "password": "string (optional)",
  "role": "ADMIN|HR|MANAGER|EMPLOYEE (optional)",
  "phone": "string (optional)",
  "address": "string (optional)",
  "position": "string (optional)",
  "joiningDate": "date (YYYY-MM-DD, optional)"
}
```

**Response (200):**
```json
{
  "success": true,
  "message": "Employee updated successfully",
  "data": {
    "id": "number",
    "firstName": "string",
    "lastName": "string",
    "email": "string",
    "role": "ADMIN|HR|MANAGER|EMPLOYEE",
    "departmentId": "number",
    "departmentName": "string",
    "phone": "string",
    "address": "string",
    "position": "string",
    "joiningDate": "date (YYYY-MM-DD)",
    "status": "ACTIVE|INACTIVE",
    "createdAt": "datetime (ISO-8601)",
    "updatedAt": "datetime (ISO-8601)"
  },
  "timestamp": "2024-01-15T10:30:00"
}
```

**Roles Required:** ADMIN, HR

---

#### Delete Employee

Delete an employee by ID.

**Endpoint:** `DELETE /api/employees/{id}`

**Path Parameters:**
- `id` - Employee ID

**Response (200):**
```json
{
  "success": true,
  "message": "Employee deleted successfully",
  "data": null,
  "timestamp": "2024-01-15T10:30:00"
}
```

**Roles Required:** ADMIN, HR

---

#### Assign Department to Employee

Assign a department to an employee.

**Endpoint:** `PATCH /api/employees/{employeeId}/department/{departmentId}`

**Path Parameters:**
- `employeeId` - Employee ID
- `departmentId` - Department ID

**Response (200):**
```json
{
  "success": true,
  "message": "Operation completed successfully",
  "data": {
    "id": "number",
    "firstName": "string",
    "lastName": "string",
    "email": "string",
    "role": "ADMIN|HR|MANAGER|EMPLOYEE",
    "departmentId": "number",
    "departmentName": "string",
    "phone": "string",
    "address": "string",
    "position": "string",
    "joiningDate": "date (YYYY-MM-DD)",
    "status": "ACTIVE|INACTIVE",
    "createdAt": "datetime (ISO-8601)",
    "updatedAt": "datetime (ISO-8601)"
  },
  "timestamp": "2024-01-15T10:30:00"
}
```

**Roles Required:** ADMIN, HR

---

### Department Management

#### Create Department

Create a new department.

**Endpoint:** `POST /api/departments`

**Request Body:**
```json
{
  "name": "string (required)",
  "code": "string (required)",
  "description": "string (optional)"
}
```

**Response (201):**
```json
{
  "success": true,
  "message": "Department created successfully",
  "data": {
    "id": "number",
    "name": "string",
    "code": "string",
    "description": "string",
    "employeeCount": "number",
    "createdAt": "datetime (ISO-8601)",
    "updatedAt": "datetime (ISO-8601)"
  },
  "timestamp": "2024-01-15T10:30:00"
}
```

**Roles Required:** ADMIN, HR

---

#### Get All Departments

Retrieve all departments with pagination.

**Endpoint:** `GET /api/departments`

**Query Parameters:**
- `page` - Page number (default: 0)
- `size` - Page size (default: 10)
- `sort` - Sorting field and direction (e.g., `name,asc`)

**Response (200):**
```json
{
  "success": true,
  "message": "Operation completed successfully",
  "data": {
    "content": [
      {
        "id": "number",
        "name": "string",
        "code": "string",
        "description": "string",
        "employeeCount": "number",
        "createdAt": "datetime (ISO-8601)",
        "updatedAt": "datetime (ISO-8601)"
      }
    ],
    "pageable": {},
    "totalElements": "number",
    "totalPages": "number"
  },
  "timestamp": "2024-01-15T10:30:00"
}
```

**Roles Required:** ADMIN, HR, MANAGER, EMPLOYEE

---

#### Get Department by ID

Retrieve a specific department by ID.

**Endpoint:** `GET /api/departments/{id}`

**Path Parameters:**
- `id` - Department ID

**Response (200):**
```json
{
  "success": true,
  "message": "Operation completed successfully",
  "data": {
    "id": "number",
    "name": "string",
    "code": "string",
    "description": "string",
    "employeeCount": "number",
    "createdAt": "datetime (ISO-8601)",
    "updatedAt": "datetime (ISO-8601)"
  },
  "timestamp": "2024-01-15T10:30:00"
}
```

**Roles Required:** ADMIN, HR, MANAGER, EMPLOYEE

---

#### Update Department

Update department information.

**Endpoint:** `PUT /api/departments/{id}`

**Path Parameters:**
- `id` - Department ID

**Request Body:**
```json
{
  "name": "string (optional)",
  "code": "string (optional)",
  "description": "string (optional)"
}
```

**Response (200):**
```json
{
  "success": true,
  "message": "Department updated successfully",
  "data": {
    "id": "number",
    "name": "string",
    "code": "string",
    "description": "string",
    "employeeCount": "number",
    "createdAt": "datetime (ISO-8601)",
    "updatedAt": "datetime (ISO-8601)"
  },
  "timestamp": "2024-01-15T10:30:00"
}
```

**Roles Required:** ADMIN, HR

---

#### Delete Department

Delete a department by ID.

**Endpoint:** `DELETE /api/departments/{id}`

**Path Parameters:**
- `id` - Department ID

**Response (200):**
```json
{
  "success": true,
  "message": "Department deleted successfully",
  "data": null,
  "timestamp": "2024-01-15T10:30:00"
}
```

**Roles Required:** ADMIN

---

#### Get Department Employees

Retrieve all employees in a specific department.

**Endpoint:** `GET /api/departments/{id}/employees`

**Path Parameters:**
- `id` - Department ID

**Query Parameters:**
- `page` - Page number (default: 0)
- `size` - Page size (default: 10)
- `sort` - Sorting field and direction

**Response (200):**
```json
{
  "success": true,
  "message": "Operation completed successfully",
  "data": {
    "content": [
      {
        "id": "number",
        "firstName": "string",
        "lastName": "string",
        "email": "string",
        "role": "ADMIN|HR|MANAGER|EMPLOYEE",
        "departmentId": "number",
        "departmentName": "string",
        "phone": "string",
        "address": "string",
        "position": "string",
        "joiningDate": "date (YYYY-MM-DD)",
        "status": "ACTIVE|INACTIVE",
        "createdAt": "datetime (ISO-8601)",
        "updatedAt": "datetime (ISO-8601)"
      }
    ],
    "pageable": {},
    "totalElements": "number",
    "totalPages": "number"
  },
  "timestamp": "2024-01-15T10:30:00"
}
```

**Roles Required:** ADMIN, HR, MANAGER

---

### Attendance Management

#### Clock In

Record clock-in time for the authenticated employee.

**Endpoint:** `POST /api/attendance/clock-in`

**Response (200):**
```json
{
  "success": true,
  "message": "Clock-in recorded successfully",
  "data": {
    "id": "number",
    "employeeId": "number",
    "employeeName": "string",
    "date": "date (YYYY-MM-DD)",
    "clockIn": "datetime (ISO-8601)",
    "clockOut": "datetime (ISO-8601)",
    "status": "PRESENT|ABSENT|LATE|HALF_DAY",
    "createdAt": "datetime (ISO-8601)",
    "updatedAt": "datetime (ISO-8601)"
  },
  "timestamp": "2024-01-15T10:30:00"
}
```

**Roles Required:** All authenticated users

---

#### Clock Out

Record clock-out time for the authenticated employee.

**Endpoint:** `PATCH /api/attendance/clock-out`

**Response (200):**
```json
{
  "success": true,
  "message": "Clock-out recorded successfully",
  "data": {
    "id": "number",
    "employeeId": "number",
    "employeeName": "string",
    "date": "date (YYYY-MM-DD)",
    "clockIn": "datetime (ISO-8601)",
    "clockOut": "datetime (ISO-8601)",
    "status": "PRESENT|ABSENT|LATE|HALF_DAY",
    "createdAt": "datetime (ISO-8601)",
    "updatedAt": "datetime (ISO-8601)"
  },
  "timestamp": "2024-01-15T10:30:00"
}
```

**Roles Required:** All authenticated users

---

#### Get My Attendance

Retrieve attendance records for the authenticated employee.

**Endpoint:** `GET /api/attendance/me`

**Query Parameters:**
- `page` - Page number (default: 0)
- `size` - Page size (default: 10)
- `sort` - Sorting field and direction

**Response (200):**
```json
{
  "success": true,
  "message": "Operation completed successfully",
  "data": {
    "content": [
      {
        "id": "number",
        "employeeId": "number",
        "employeeName": "string",
        "date": "date (YYYY-MM-DD)",
        "clockIn": "datetime (ISO-8601)",
        "clockOut": "datetime (ISO-8601)",
        "status": "PRESENT|ABSENT|LATE|HALF_DAY",
        "createdAt": "datetime (ISO-8601)",
        "updatedAt": "datetime (ISO-8601)"
      }
    ],
    "pageable": {},
    "totalElements": "number",
    "totalPages": "number"
  },
  "timestamp": "2024-01-15T10:30:00"
}
```

**Roles Required:** All authenticated users

---

#### Get All Attendance

Retrieve all attendance records (admin/manager view).

**Endpoint:** `GET /api/attendance`

**Query Parameters:**
- `page` - Page number (default: 0)
- `size` - Page size (default: 10)
- `sort` - Sorting field and direction

**Response (200):**
```json
{
  "success": true,
  "message": "Operation completed successfully",
  "data": {
    "content": [
      {
        "id": "number",
        "employeeId": "number",
        "employeeName": "string",
        "date": "date (YYYY-MM-DD)",
        "clockIn": "datetime (ISO-8601)",
        "clockOut": "datetime (ISO-8601)",
        "status": "PRESENT|ABSENT|LATE|HALF_DAY",
        "createdAt": "datetime (ISO-8601)",
        "updatedAt": "datetime (ISO-8601)"
      }
    ],
    "pageable": {},
    "totalElements": "number",
    "totalPages": "number"
  },
  "timestamp": "2024-01-15T10:30:00"
}
```

**Roles Required:** ADMIN, HR, MANAGER

---

#### Get Employee Attendance

Retrieve attendance records for a specific employee.

**Endpoint:** `GET /api/attendance/employee/{employeeId}`

**Path Parameters:**
- `employeeId` - Employee ID

**Query Parameters:**
- `page` - Page number (default: 0)
- `size` - Page size (default: 10)
- `sort` - Sorting field and direction

**Response (200):**
```json
{
  "success": true,
  "message": "Operation completed successfully",
  "data": {
    "content": [
      {
        "id": "number",
        "employeeId": "number",
        "employeeName": "string",
        "date": "date (YYYY-MM-DD)",
        "clockIn": "datetime (ISO-8601)",
        "clockOut": "datetime (ISO-8601)",
        "status": "PRESENT|ABSENT|LATE|HALF_DAY",
        "createdAt": "datetime (ISO-8601)",
        "updatedAt": "datetime (ISO-8601)"
      }
    ],
    "pageable": {},
    "totalElements": "number",
    "totalPages": "number"
  },
  "timestamp": "2024-01-15T10:30:00"
}
```

**Roles Required:** ADMIN, HR, MANAGER

---

#### Update Attendance

Update attendance record (admin/HR only).

**Endpoint:** `PUT /api/attendance/{attendanceId}`

**Path Parameters:**
- `attendanceId` - Attendance record ID

**Request Body:**
```json
{
  "clockIn": "datetime (ISO-8601, optional)",
  "clockOut": "datetime (ISO-8601, optional)"
}
```

**Response (200):**
```json
{
  "success": true,
  "message": "Attendance updated successfully",
  "data": {
    "id": "number",
    "employeeId": "number",
    "employeeName": "string",
    "date": "date (YYYY-MM-DD)",
    "clockIn": "datetime (ISO-8601)",
    "clockOut": "datetime (ISO-8601)",
    "status": "PRESENT|ABSENT|LATE|HALF_DAY",
    "createdAt": "datetime (ISO-8601)",
    "updatedAt": "datetime (ISO-8601)"
  },
  "timestamp": "2024-01-15T10:30:00"
}
```

**Roles Required:** ADMIN, HR

---

### Leave Management

#### Create Leave Request

Submit a new leave request.

**Endpoint:** `POST /api/leaves`

**Request Body:**
```json
{
  "leaveType": "ANNUAL|SICK|CASUAL|MATERNITY|UNPAID (required)",
  "startDate": "date (YYYY-MM-DD, required)",
  "endDate": "date (YYYY-MM-DD, required)",
  "reason": "string (required)"
}
```

**Response (201):**
```json
{
  "id": "number",
  "employeeId": "number",
  "employeeName": "string",
  "leaveType": "ANNUAL|SICK|CASUAL|MATERNITY|UNPAID",
  "startDate": "date (YYYY-MM-DD)",
  "endDate": "date (YYYY-MM-DD)",
  "reason": "string",
  "status": "PENDING|APPROVED|REJECTED",
  "approvedById": "number",
  "approvedByName": "string",
  "createdDate": "date (YYYY-MM-DD)",
  "createdAt": "datetime (ISO-8601)",
  "updatedAt": "datetime (ISO-8601)"
}
```

**Roles Required:** All authenticated users

---

#### Get My Leave Requests

Retrieve leave requests for the authenticated employee.

**Endpoint:** `GET /api/leaves/me`

**Query Parameters:**
- `page` - Page number (default: 0)
- `size` - Page size (default: 10)
- `sort` - Sorting field and direction

**Response (200):**
```json
{
  "content": [
    {
      "id": "number",
      "employeeId": "number",
      "employeeName": "string",
      "leaveType": "ANNUAL|SICK|CASUAL|MATERNITY|UNPAID",
      "startDate": "date (YYYY-MM-DD)",
      "endDate": "date (YYYY-MM-DD)",
      "reason": "string",
      "status": "PENDING|APPROVED|REJECTED",
      "approvedById": "number",
      "approvedByName": "string",
      "createdDate": "date (YYYY-MM-DD)",
      "createdAt": "datetime (ISO-8601)",
      "updatedAt": "datetime (ISO-8601)"
    }
  ],
  "pageable": {},
  "totalElements": "number",
  "totalPages": "number"
}
```

**Roles Required:** All authenticated users

---

#### Get Leave Request by ID

Retrieve a specific leave request by ID.

**Endpoint:** `GET /api/leaves/{leaveRequestId}`

**Path Parameters:**
- `leaveRequestId` - Leave request ID

**Response (200):**
```json
{
  "id": "number",
  "employeeId": "number",
  "employeeName": "string",
  "leaveType": "ANNUAL|SICK|CASUAL|MATERNITY|UNPAID",
  "startDate": "date (YYYY-MM-DD)",
  "endDate": "date (YYYY-MM-DD)",
  "reason": "string",
  "status": "PENDING|APPROVED|REJECTED",
  "approvedById": "number",
  "approvedByName": "string",
  "createdDate": "date (YYYY-MM-DD)",
  "createdAt": "datetime (ISO-8601)",
  "updatedAt": "datetime (ISO-8601)"
}
```

**Roles Required:** ADMIN, HR, MANAGER

---

#### Get All Leave Requests

Retrieve all leave requests (admin/manager view).

**Endpoint:** `GET /api/leaves`

**Query Parameters:**
- `page` - Page number (default: 0)
- `size` - Page size (default: 10)
- `sort` - Sorting field and direction

**Response (200):**
```json
{
  "content": [
    {
      "id": "number",
      "employeeId": "number",
      "employeeName": "string",
      "leaveType": "ANNUAL|SICK|CASUAL|MATERNITY|UNPAID",
      "startDate": "date (YYYY-MM-DD)",
      "endDate": "date (YYYY-MM-DD)",
      "reason": "string",
      "status": "PENDING|APPROVED|REJECTED",
      "approvedById": "number",
      "approvedByName": "string",
      "createdDate": "date (YYYY-MM-DD)",
      "createdAt": "datetime (ISO-8601)",
      "updatedAt": "datetime (ISO-8601)"
    }
  ],
  "pageable": {},
  "totalElements": "number",
  "totalPages": "number"
}
```

**Roles Required:** ADMIN, HR, MANAGER

---

#### Get Leave Requests by Status

Retrieve leave requests filtered by status.

**Endpoint:** `GET /api/leaves/status/{status}`

**Path Parameters:**
- `status` - Leave status (PENDING|APPROVED|REJECTED)

**Query Parameters:**
- `page` - Page number (default: 0)
- `size` - Page size (default: 10)
- `sort` - Sorting field and direction

**Response (200):**
```json
{
  "content": [
    {
      "id": "number",
      "employeeId": "number",
      "employeeName": "string",
      "leaveType": "ANNUAL|SICK|CASUAL|MATERNITY|UNPAID",
      "startDate": "date (YYYY-MM-DD)",
      "endDate": "date (YYYY-MM-DD)",
      "reason": "string",
      "status": "PENDING|APPROVED|REJECTED",
      "approvedById": "number",
      "approvedByName": "string",
      "createdDate": "date (YYYY-MM-DD)",
      "createdAt": "datetime (ISO-8601)",
      "updatedAt": "datetime (ISO-8601)"
    }
  ],
  "pageable": {},
  "totalElements": "number",
  "totalPages": "number"
}
```

**Roles Required:** ADMIN, HR, MANAGER

---

#### Update Leave Request

Update an existing leave request.

**Endpoint:** `PUT /api/leaves/{leaveRequestId}`

**Path Parameters:**
- `leaveRequestId` - Leave request ID

**Request Body:**
```json
{
  "leaveType": "ANNUAL|SICK|CASUAL|MATERNITY|UNPAID (optional)",
  "startDate": "date (YYYY-MM-DD, optional)",
  "endDate": "date (YYYY-MM-DD, optional)",
  "reason": "string (optional)"
}
```

**Response (200):**
```json
{
  "id": "number",
  "employeeId": "number",
  "employeeName": "string",
  "leaveType": "ANNUAL|SICK|CASUAL|MATERNITY|UNPAID",
  "startDate": "date (YYYY-MM-DD)",
  "endDate": "date (YYYY-MM-DD)",
  "reason": "string",
  "status": "PENDING|APPROVED|REJECTED",
  "approvedById": "number",
  "approvedByName": "string",
  "createdDate": "date (YYYY-MM-DD)",
  "createdAt": "datetime (ISO-8601)",
  "updatedAt": "datetime (ISO-8601)"
}
```

**Roles Required:** All authenticated users (own requests only)

---

#### Approve/Reject Leave Request

Approve or reject a leave request.

**Endpoint:** `PATCH /api/leaves/{leaveRequestId}/approval`

**Path Parameters:**
- `leaveRequestId` - Leave request ID

**Request Body:**
```json
{
  "status": "APPROVED|REJECTED (required)"
}
```

**Response (200):**
```json
{
  "id": "number",
  "employeeId": "number",
  "employeeName": "string",
  "leaveType": "ANNUAL|SICK|CASUAL|MATERNITY|UNPAID",
  "startDate": "date (YYYY-MM-DD)",
  "endDate": "date (YYYY-MM-DD)",
  "reason": "string",
  "status": "PENDING|APPROVED|REJECTED",
  "approvedById": "number",
  "approvedByName": "string",
  "createdDate": "date (YYYY-MM-DD)",
  "createdAt": "datetime (ISO-8601)",
  "updatedAt": "datetime (ISO-8601)"
}
```

**Roles Required:** ADMIN, HR, MANAGER

---

#### Delete Leave Request

Delete a leave request.

**Endpoint:** `DELETE /api/leaves/{leaveRequestId}`

**Path Parameters:**
- `leaveRequestId` - Leave request ID

**Response (204):** No content

**Roles Required:** All authenticated users (own requests only)

---

### Project Management

#### Create Project

Create a new project.

**Endpoint:** `POST /api/projects`

**Request Body:**
```json
{
  "name": "string (required)",
  "startDate": "date (YYYY-MM-DD, required)",
  "status": "PLANNING|ACTIVE|COMPLETED|CANCELLED (required)",
  "description": "string (optional)",
  "endDate": "date (YYYY-MM-DD, optional)"
}
```

**Response (201):**
```json
{
  "id": "number",
  "name": "string",
  "description": "string",
  "startDate": "date (YYYY-MM-DD)",
  "endDate": "date (YYYY-MM-DD)",
  "status": "PLANNING|ACTIVE|COMPLETED|CANCELLED",
  "createdAt": "datetime (ISO-8601)",
  "updatedAt": "datetime (ISO-8601)"
}
```

**Roles Required:** ADMIN, HR, MANAGER

---

#### Get Project by ID

Retrieve a specific project by ID.

**Endpoint:** `GET /api/projects/{id}`

**Path Parameters:**
- `id` - Project ID

**Response (200):**
```json
{
  "id": "number",
  "name": "string",
  "description": "string",
  "startDate": "date (YYYY-MM-DD)",
  "endDate": "date (YYYY-MM-DD)",
  "status": "PLANNING|ACTIVE|COMPLETED|CANCELLED",
  "createdAt": "datetime (ISO-8601)",
  "updatedAt": "datetime (ISO-8601)"
}
```

**Roles Required:** ADMIN, HR, MANAGER, EMPLOYEE

---

#### Get All Projects

Retrieve all projects with pagination.

**Endpoint:** `GET /api/projects`

**Query Parameters:**
- `page` - Page number (default: 0)
- `size` - Page size (default: 10)
- `sort` - Sorting field and direction

**Response (200):**
```json
{
  "content": [
    {
      "id": "number",
      "name": "string",
      "description": "string",
      "startDate": "date (YYYY-MM-DD)",
      "endDate": "date (YYYY-MM-DD)",
      "status": "PLANNING|ACTIVE|COMPLETED|CANCELLED",
      "createdAt": "datetime (ISO-8601)",
      "updatedAt": "datetime (ISO-8601)"
    }
  ],
  "pageable": {},
  "totalElements": "number",
  "totalPages": "number"
}
```

**Roles Required:** ADMIN, HR, MANAGER, EMPLOYEE

---

#### Update Project

Update project information.

**Endpoint:** `PUT /api/projects/{id}`

**Path Parameters:**
- `id` - Project ID

**Request Body:**
```json
{
  "name": "string (optional)",
  "description": "string (optional)",
  "startDate": "date (YYYY-MM-DD, optional)",
  "endDate": "date (YYYY-MM-DD, optional)",
  "status": "PLANNING|ACTIVE|COMPLETED|CANCELLED (optional)"
}
```

**Response (200):**
```json
{
  "id": "number",
  "name": "string",
  "description": "string",
  "startDate": "date (YYYY-MM-DD)",
  "endDate": "date (YYYY-MM-DD)",
  "status": "PLANNING|ACTIVE|COMPLETED|CANCELLED",
  "createdAt": "datetime (ISO-8601)",
  "updatedAt": "datetime (ISO-8601)"
}
```

**Roles Required:** ADMIN, HR, MANAGER

---

#### Delete Project

Delete a project by ID.

**Endpoint:** `DELETE /api/projects/{id}`

**Path Parameters:**
- `id` - Project ID

**Response (204):** No content

**Roles Required:** ADMIN, HR

---

### Project Member Management

#### Add Member to Project

Add an employee to a project.

**Endpoint:** `POST /api/projects/{projectId}/members`

**Path Parameters:**
- `projectId` - Project ID

**Request Body:**
```json
{
  "employeeId": "number (required)",
  "projectRole": "PROJECT_MANAGER|BACKEND_DEVELOPER|FRONTEND_DEVELOPER|QA_ENGINEER (required)"
}
```

**Response (201):**
```json
{
  "id": "number",
  "employeeId": "number",
  "employeeName": "string",
  "projectId": "number",
  "projectRole": "PROJECT_MANAGER|BACKEND_DEVELOPER|FRONTEND_DEVELOPER|QA_ENGINEER",
  "assignedDate": "date (YYYY-MM-DD)",
  "createdAt": "datetime (ISO-8601)",
  "updatedAt": "datetime (ISO-8601)"
}
```

**Roles Required:** ADMIN, HR, MANAGER

---

#### Get Project Members

Retrieve all members of a specific project.

**Endpoint:** `GET /api/projects/{projectId}/members`

**Path Parameters:**
- `projectId` - Project ID

**Query Parameters:**
- `page` - Page number (default: 0)
- `size` - Page size (default: 10)
- `sort` - Sorting field and direction

**Response (200):**
```json
{
  "content": [
    {
      "id": "number",
      "employeeId": "number",
      "employeeName": "string",
      "projectId": "number",
      "projectRole": "PROJECT_MANAGER|BACKEND_DEVELOPER|FRONTEND_DEVELOPER|QA_ENGINEER",
      "assignedDate": "date (YYYY-MM-DD)",
      "createdAt": "datetime (ISO-8601)",
      "updatedAt": "datetime (ISO-8601)"
    }
  ],
  "pageable": {},
  "totalElements": "number",
  "totalPages": "number"
}
```

**Roles Required:** ADMIN, HR, MANAGER, EMPLOYEE

---

#### Remove Member from Project

Remove a member from a project.

**Endpoint:** `DELETE /api/projects/{projectId}/members/{memberId}`

**Path Parameters:**
- `projectId` - Project ID
- `memberId` - Project member ID

**Response (204):** No content

**Roles Required:** ADMIN, HR, MANAGER

---

### Task Management

#### Create Task

Create a new task.

**Endpoint:** `POST /api/tasks`

**Request Body:**
```json
{
  "title": "string (required)",
  "priority": "LOW|MEDIUM|HIGH (required)",
  "description": "string (optional)",
  "status": "TODO|IN_PROGRESS|DONE (required)",
  "projectId": "number (required)",
  "assignedEmployeeId": "number (required)"
}
```

**Response (201):**
```json
{
  "id": "number",
  "title": "string",
  "description": "string",
  "priority": "LOW|MEDIUM|HIGH",
  "status": "TODO|IN_PROGRESS|DONE",
  "createdDate": "date (YYYY-MM-DD)",
  "projectId": "number",
  "projectName": "string",
  "assignedEmployeeId": "number",
  "assignedEmployeeName": "string",
  "createdAt": "datetime (ISO-8601)",
  "updatedAt": "datetime (ISO-8601)"
}
```

**Roles Required:** ADMIN, HR, MANAGER

---

#### Get Task by ID

Retrieve a specific task by ID.

**Endpoint:** `GET /api/tasks/{id}`

**Path Parameters:**
- `id` - Task ID

**Response (200):**
```json
{
  "id": "number",
  "title": "string",
  "description": "string",
  "priority": "LOW|MEDIUM|HIGH",
  "status": "TODO|IN_PROGRESS|DONE",
  "createdDate": "date (YYYY-MM-DD)",
  "projectId": "number",
  "projectName": "string",
  "assignedEmployeeId": "number",
  "assignedEmployeeName": "string",
  "createdAt": "datetime (ISO-8601)",
  "updatedAt": "datetime (ISO-8601)"
}
```

**Roles Required:** ADMIN, HR, MANAGER, EMPLOYEE

---

#### Get All Tasks

Retrieve all tasks with pagination.

**Endpoint:** `GET /api/tasks`

**Query Parameters:**
- `page` - Page number (default: 0)
- `size` - Page size (default: 10)
- `sort` - Sorting field and direction

**Response (200):**
```json
{
  "content": [
    {
      "id": "number",
      "title": "string",
      "description": "string",
      "priority": "LOW|MEDIUM|HIGH",
      "status": "TODO|IN_PROGRESS|DONE",
      "createdDate": "date (YYYY-MM-DD)",
      "projectId": "number",
      "projectName": "string",
      "assignedEmployeeId": "number",
      "assignedEmployeeName": "string",
      "createdAt": "datetime (ISO-8601)",
      "updatedAt": "datetime (ISO-8601)"
    }
  ],
  "pageable": {},
  "totalElements": "number",
  "totalPages": "number"
}
```

**Roles Required:** ADMIN, HR, MANAGER

---

#### Update Task

Update task information.

**Endpoint:** `PUT /api/tasks/{id}`

**Path Parameters:**
- `id` - Task ID

**Request Body:**
```json
{
  "title": "string (optional)",
  "priority": "LOW|MEDIUM|HIGH (optional)",
  "description": "string (optional)",
  "status": "TODO|IN_PROGRESS|DONE (optional)"
}
```

**Response (200):**
```json
{
  "id": "number",
  "title": "string",
  "description": "string",
  "priority": "LOW|MEDIUM|HIGH",
  "status": "TODO|IN_PROGRESS|DONE",
  "createdDate": "date (YYYY-MM-DD)",
  "projectId": "number",
  "projectName": "string",
  "assignedEmployeeId": "number",
  "assignedEmployeeName": "string",
  "createdAt": "datetime (ISO-8601)",
  "updatedAt": "datetime (ISO-8601)"
}
```

**Roles Required:** ADMIN, HR, MANAGER

---

#### Delete Task

Delete a task by ID.

**Endpoint:** `DELETE /api/tasks/{id}`

**Path Parameters:**
- `id` - Task ID

**Response (204):** No content

**Roles Required:** ADMIN, HR, MANAGER

---

#### Get Tasks by Project

Retrieve all tasks for a specific project.

**Endpoint:** `GET /api/tasks/project/{projectId}`

**Path Parameters:**
- `projectId` - Project ID

**Query Parameters:**
- `page` - Page number (default: 0)
- `size` - Page size (default: 10)
- `sort` - Sorting field and direction

**Response (200):**
```json
{
  "content": [
    {
      "id": "number",
      "title": "string",
      "description": "string",
      "priority": "LOW|MEDIUM|HIGH",
      "status": "TODO|IN_PROGRESS|DONE",
      "createdDate": "date (YYYY-MM-DD)",
      "projectId": "number",
      "projectName": "string",
      "assignedEmployeeId": "number",
      "assignedEmployeeName": "string",
      "createdAt": "datetime (ISO-8601)",
      "updatedAt": "datetime (ISO-8601)"
    }
  ],
  "pageable": {},
  "totalElements": "number",
  "totalPages": "number"
}
```

**Roles Required:** ADMIN, HR, MANAGER, EMPLOYEE

---

#### Get Tasks by Employee

Retrieve all tasks assigned to a specific employee.

**Endpoint:** `GET /api/tasks/employee/{employeeId}`

**Path Parameters:**
- `employeeId` - Employee ID

**Query Parameters:**
- `page` - Page number (default: 0)
- `size` - Page size (default: 10)
- `sort` - Sorting field and direction

**Response (200):**
```json
{
  "content": [
    {
      "id": "number",
      "title": "string",
      "description": "string",
      "priority": "LOW|MEDIUM|HIGH",
      "status": "TODO|IN_PROGRESS|DONE",
      "createdDate": "date (YYYY-MM-DD)",
      "projectId": "number",
      "projectName": "string",
      "assignedEmployeeId": "number",
      "assignedEmployeeName": "string",
      "createdAt": "datetime (ISO-8601)",
      "updatedAt": "datetime (ISO-8601)"
    }
  ],
  "pageable": {},
  "totalElements": "number",
  "totalPages": "number"
}
```

**Roles Required:** ADMIN, HR, MANAGER

---

#### Get Tasks by Status

Retrieve tasks filtered by status.

**Endpoint:** `GET /api/tasks/status/{status}`

**Path Parameters:**
- `status` - Task status (TODO|IN_PROGRESS|DONE)

**Query Parameters:**
- `page` - Page number (default: 0)
- `size` - Page size (default: 10)
- `sort` - Sorting field and direction

**Response (200):**
```json
{
  "content": [
    {
      "id": "number",
      "title": "string",
      "description": "string",
      "priority": "LOW|MEDIUM|HIGH",
      "status": "TODO|IN_PROGRESS|DONE",
      "createdDate": "date (YYYY-MM-DD)",
      "projectId": "number",
      "projectName": "string",
      "assignedEmployeeId": "number",
      "assignedEmployeeName": "string",
      "createdAt": "datetime (ISO-8601)",
      "updatedAt": "datetime (ISO-8601)"
    }
  ],
  "pageable": {},
  "totalElements": "number",
  "totalPages": "number"
}
```

**Roles Required:** ADMIN, HR, MANAGER

---

## Enums Reference

### Role
- `ADMIN` - Full system access
- `HR` - Human Resources management
- `MANAGER` - Department and project management
- `EMPLOYEE` - Basic employee access

### EmployeeStatus
- `ACTIVE` - Employee is active
- `INACTIVE` - Employee is inactive

### AttendanceStatus
- `PRESENT` - Employee was present
- `ABSENT` - Employee was absent
- `LATE` - Employee was late
- `HALF_DAY` - Employee worked half day

### LeaveStatus
- `PENDING` - Leave request is pending approval
- `APPROVED` - Leave request has been approved
- `REJECTED` - Leave request has been rejected

### LeaveType
- `ANNUAL` - Annual leave
- `SICK` - Sick leave
- `CASUAL` - Casual leave
- `MATERNITY` - Maternity leave
- `UNPAID` - Unpaid leave

### ProjectStatus
- `PLANNING` - Project is in planning phase
- `ACTIVE` - Project is currently active
- `COMPLETED` - Project has been completed
- `CANCELLED` - Project has been cancelled

### ProjectRole
- `PROJECT_MANAGER` - Project manager role
- `BACKEND_DEVELOPER` - Backend developer role
- `FRONTEND_DEVELOPER` - Frontend developer role
- `QA_ENGINEER` - QA Engineer role

### TaskStatus
- `TODO` - Task is to be done
- `IN_PROGRESS` - Task is in progress
- `DONE` - Task is completed

### TaskPriority
- `LOW` - Low priority task
- `MEDIUM` - Medium priority task
- `HIGH` - High priority task

---

## Error Response Format

All error responses follow this format:

```json
{
  "timestamp": "datetime (ISO-8601)",
  "status": "number (HTTP status code)",
  "error": "string (error type)",
  "message": "string (detailed error message)"
}
```

Common HTTP status codes:
- `400` - Bad Request (validation errors)
- `401` - Unauthorized (authentication required)
- `403` - Forbidden (insufficient permissions)
- `404` - Not Found (resource not found)
- `500` - Internal Server Error

---

## Pagination

All list endpoints support pagination using Spring Data's pagination parameters:

- `page` - Page number (0-indexed, default: 0)
- `size` - Number of items per page (default: 10)
- `sort` - Sorting criteria in format: `property,direction` (e.g., `firstName,asc`)

Example:
```
GET /api/employees?page=0&size=20&sort=firstName,asc
```

---

## Date/Time Format

All dates and times follow ISO-8601 format:
- Dates: `YYYY-MM-DD` (e.g., `2024-01-15`)
- DateTimes: `YYYY-MM-DDTHH:mm:ss` (e.g., `2024-01-15T10:30:00`)
