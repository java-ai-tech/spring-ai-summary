# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Overview

Spring AI Summary is a modular collection of Spring AI sample projects demonstrating various AI capabilities including chat models, RAG (Retrieval-Augmented Generation), vector storage, tool calling, memory management, and observability features.

## Project Structure

This is a multi-module Maven project with the following key modules:

- `spring-ai-chat/` - Chat implementations for different AI models (OpenAI, Qwen, DeepSeek, Doubao, Ollama)
- `spring-ai-rag/` - RAG implementation with document processing and vector storage
- `spring-ai-vector/` - Vector storage implementations (Milvus, Redis, MariaDB)
- `spring-ai-tool-calling/` - Function calling and tool integration examples
- `spring-ai-chat-memory/` - Chat memory management (JDBC, local storage)
- `spring-ai-agent/` - Agent workflows and orchestration
- `spring-ai-mcp/` - Model Context Protocol (MCP) client and server implementations
- `spring-ai-evaluation/` - AI response evaluation framework
- `spring-ai-observability/` - Monitoring and tracing with metrics

## Build Commands

```bash
# Build entire project
mvn clean compile

# Build without tests
mvn clean compile -DskipTests

# Package all modules
mvn clean package

# Install to local repository
mvn clean install
```

## Test Commands

```bash
# Run all tests
mvn test

# Run tests for specific module
mvn test -pl spring-ai-chat/spring-ai-chat-deepseek

# Run specific test class
mvn test -Dtest=ChainWorkflowTest

# Run tests with profiles
mvn test -Dspring.profiles.active=test
```

## Running Applications

Each module contains a Spring Boot application. To run a specific module:

```bash
# Run from module directory
cd spring-ai-chat/spring-ai-chat-deepseek
mvn spring-boot:run

# Or run with Maven from root
mvn spring-boot:run -pl spring-ai-chat/spring-ai-chat-deepseek
```

## Configuration

### API Keys
Set environment variables for AI service API keys:
- `SPRING_AI_OPENAI_API_KEY` - OpenAI API key
- `SPRING_AI_DEEPSEEK_API_KEY` - DeepSeek API key  
- `SPRING_AI_QWEN_API_KEY` - Qwen API key

### Application Properties
Each module has its own `application.properties` or `application.yml` file in `src/main/resources/`. Key configurations include:
- AI model endpoints and API keys
- Server ports (typically 8080-8090 range)
- Database connections for vector storage
- Actuator endpoints for monitoring

## Architecture Notes

### Chat Modules
- Each chat module demonstrates integration with a specific AI model
- Common pattern: Controller → Service → ChatClient
- Support for streaming responses and chat memory
- Metric collection via Micrometer

### Vector Storage
- Abstract `VectorStoreStorage` interface with different implementations
- Support for similarity search and document retrieval
- Integration with Spring AI's vector store abstraction

### Tool Calling
- Function definitions in `tools/function/` packages
- Support for both automatic and user-controlled execution
- Integration with weather services and file operations

### Agent Workflows
- Workflow orchestration with parallel and sequential execution
- Router-based workflow selection
- Support for complex multi-step agent interactions

### MCP Integration
- Server-side MCP implementations for external tool integration
- Client-side MCP consumption for agent workflows
- Support for both SSE and stdio communication

## Development Environment

- **Java Version**: 21+
- **Spring Boot**: 3.3.6
- **Spring AI**: 1.0.0
- **Maven**: 3.6+
- **Docker**: Required for vector databases (Milvus, Redis, MariaDB)

## Common Development Tasks

### Adding New Chat Model
1. Create new module under `spring-ai-chat/`
2. Add ChatClient configuration
3. Implement controller with chat endpoints
4. Add application properties for model configuration

### Adding Vector Storage
1. Create new module under `spring-ai-vector/`
2. Implement `VectorStoreStorage` interface
3. Add database-specific configuration
4. Include integration tests

### Tool Function Development
1. Add function definitions in `tools/function/`
2. Register functions in configuration class
3. Test with tool calling examples

## Testing Strategy

- Integration tests for each module
- Test configurations in `src/test/resources/`
- Mock external dependencies where appropriate
- Separate test profiles for different environments