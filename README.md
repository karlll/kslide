# kslide

`kslide` is a tool for managing and manipulating PowerPoint-style slideshows.

## Project Structure

This project follows a multi-module Gradle structure:

```
kslide/
│
├── core/                     # Core library for slideshow management
├── shell/                    # Spring Shell CLI frontend
├── mcp/                      # (Planned) MCP server for AI integration
```
## Getting Started

### Prerequisites
- Java 17+
- Kotlin 1.9.25
- Gradle 7.6+

### Build and Run

1. Clone the repository:
   ```bash
   git clone https://github.com/karlll/kslide.git
   cd kslide
   ```

2. Build the project:
   ```bash
   ./gradlew build
   ```

3. Run the CLI (Spring Shell):
   ```bash
   ./gradlew :shell:bootRun
   ```

## Dependencies

- **Apache POI**: For PowerPoint file manipulation.
- **Spring Shell**: For the CLI interface.
- **Spring Boot**: For the planned MCP server.

## License

This project is licensed under the MIT License. 
