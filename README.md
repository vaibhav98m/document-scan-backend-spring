# OTP Login API

A Spring Boot application providing OTP-based authentication via email, document management, and text-to-speech services.

## 🚀 Features

- **OTP Authentication**: Secure email-based OTP login system
- **User Management**: User onboarding and profile management
- **Document Processing**: Upload, analyze, and query documents with AI-powered insights
- **Text-to-Speech**: Azure Cognitive Services integration for audio generation
- **Document Intelligence**: Automatic summarization and key insights extraction

## 📋 Table of Contents

- [Getting Started](#getting-started)
- [API Endpoints](#api-endpoints)
- [Authentication Flow](#authentication-flow)
- [Document Management](#document-management)
- [Text-to-Speech](#text-to-speech)
- [Request/Response Examples](#requestresponse-examples)
- [Error Handling](#error-handling)
- [Contributing](#contributing)

## 🛠️ Getting Started

### Prerequisites

- Java 11 or higher
- Spring Boot 2.x
- Azure Cognitive Services account (for TTS)
- Email service configuration

### Installation

1. Clone the repository
2. Configure application properties
3. Run the application:
   ```bash
   ./mvnw spring-boot:run
   ```

The API will be available at `http://localhost:8081`

## 📚 API Endpoints

### Authentication

| Method | Endpoint                | Description         |
| ------ | ----------------------- | ------------------- |
| POST   | `/api/auth/request-otp` | Send OTP to email   |
| POST   | `/api/auth/verify-otp`  | Verify received OTP |

### User Management

| Method | Endpoint             | Description         |
| ------ | -------------------- | ------------------- |
| POST   | `/api/users/onboard` | Register a new user |

### Document Management

| Method | Endpoint                                    | Description            |
| ------ | ------------------------------------------- | ---------------------- |
| POST   | `/api/documents/upload`                     | Upload a document      |
| POST   | `/api/documents/query`                      | Query document content |
| GET    | `/api/documents/{documentId}/summary`       | Get document summary   |
| GET    | `/api/documents/{documentId}/insights`      | Get document insights  |
| GET    | `/api/documents/{documentId}/query/history` | Get query history      |

### Text-to-Speech

| Method | Endpoint            | Description              |
| ------ | ------------------- | ------------------------ |
| POST   | `/api/tts/generate` | Generate audio from text |
| GET    | `/api/tts/health`   | Health check             |

## 🔐 Authentication Flow

1. **Request OTP**: Send email to `/api/auth/request-otp`
2. **Verify OTP**: Submit OTP code to `/api/auth/verify-otp`
3. **Access Protected Resources**: Use authentication token for subsequent requests

### Request OTP Example

```json
POST /api/auth/request-otp
Content-Type: application/json

{
  "email": "user@example.com"
}
```

### Verify OTP Example

```json
POST /api/auth/verify-otp
Content-Type: application/json

{
  "email": "user@example.com",
  "otp": "123456"
}
```

## 📄 Document Management

### Upload Document

```bash
POST /api/documents/upload?userId=123
Content-Type: multipart/form-data

document: [binary file]
```

### Query Document

```json
POST /api/documents/query
Content-Type: application/json

{
  "question": "What is the main topic of this document?",
  "userId": 123,
  "documentId": 456
}
```

### Document Summary Response

```json
{
  "status": "success",
  "message": "Summary generated successfully",
  "data": {
    "id": "summary-123",
    "documentId": "doc-456",
    "summaryData": "This document discusses...",
    "keyPoints": ["Key point 1", "Key point 2"],
    "wordCount": 1500,
    "readingTime": 6,
    "generatedAt": "2024-01-15T10:30:00Z"
  }
}
```

## 🗣️ Text-to-Speech

### Generate Audio

```json
POST /api/tts/generate
Content-Type: application/json

{
  "text": "Hello, this is a test message",
  "language": "en-US"
}
```

**Response**: Audio file (WAV format)

## 👤 User Onboarding

### Register New User

```json
POST /api/users/onboard
Content-Type: application/json

{
  "email": "user@example.com",
  "firstname": "John",
  "lastname": "Doe",
  "phone": "+1234567890"
}
```

### User Response

```json
{
  "status": "success",
  "message": "User registered successfully",
  "data": {
    "id": 123,
    "email": "user@example.com",
    "name": "John Doe",
    "mobileNumber": "+1234567890",
    "createdAt": "2024-01-15T10:30:00Z"
  }
}
```

## 📊 Document Insights

The API provides detailed insights for uploaded documents:

- **Content Analysis**: Automated content categorization
- **Key Insights**: Important information extraction
- **Relevance Scoring**: Confidence metrics for insights
- **Contextual Information**: Related content and tags

### Insight Structure

```json
{
  "id": "insight-123",
  "type": "key_finding",
  "priority": "high",
  "content": "Main finding from the document",
  "relevance": 0.95,
  "context": "Found in section 2.3",
  "category": "financial",
  "tags": ["revenue", "growth", "analysis"]
}
```

## ⚠️ Error Handling

All endpoints return standardized error responses:

```json
{
  "status": "error",
  "message": "Descriptive error message",
  "error": "ERROR_CODE",
  "data": null
}
```

## 🔧 Configuration

### Required Environment Variables

```properties
# Email Configuration
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your-email@gmail.com
spring.mail.password=your-app-password

# Azure Cognitive Services
azure.cognitive.speech.key=your-speech-key
azure.cognitive.speech.region=your-region

# Database Configuration
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.username=sa
spring.datasource.password=
```

## 📝 API Response Format

All API responses follow this structure:

```json
{
  "status": "success|error",
  "message": "Human readable message",
  "error": "Error code (if applicable)",
  "data": "Response data object"
}
```

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## 📄 License

This project is licensed under the MIT License.

## 🆘 Support

For questions or support, please contact the development team or create an issue in the repository.

---

**Version**: 1.0  
**Base URL**: `http://localhost:8081`  
**API Documentation**: Available via OpenAPI 3.0.1 specification
